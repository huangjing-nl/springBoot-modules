package com.huawei.spring.core.implement.SQLScan.custom;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.SQLScan.custom.BTLimitEntity;
import com.huawei.spring.catalogue.SQLScan.custom.Expression;
import com.huawei.spring.catalogue.SQLScan.custom.WhereClause;
import com.huawei.spring.catalogue.parse.*;
import com.huawei.spring.core.implement.SQLScan.util.SqlScanUtil;
import com.huawei.spring.exceptions.SQLFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomFunctions {
  private static Logger log = LoggerFactory.getLogger(SqlScanUtil.class);

  /**
   * 大表必须要有天分区作为限制的定制方法
   */
  public static List<ScanResult> mustBePartitioned(Map<String, List<QueryBean>> queryBeanMap, SQLBlock block, ScanRule rule) {
//    String ruleName = "大表天分区校验规则";
    String ruleDes = "大表查询必须要有天分区条件作为限制条件";
//    String level = "ERROR";
    List<QueryBean> queryBeans = queryBeanMap.get("big"); // 大表的查询语句集合
    boolean mustBigTable = true;
    return getResultByCheck(queryBeans, block, rule.getName(), ruleDes, rule.getLevel().getLevel(), mustBigTable);
  }

  /**
   * 天表_dm、周表_wm、月表_mm,必须要有对应的pt_d,pt_w,pt_m
   */
  public static List<ScanResult> tableWithPt(CustomParameter parameter) {
    List<ScanResult> results = new ArrayList<>();
    ScanRule rule = parameter.getRule();
//    String ruleName = "表分区校验规则";
//    String ruleDes = "天表_dm、周表_wm、月表_mm,必须要有对应的pt_d,pt_w,pt_m";
//    String level = "ERROR";
    boolean notMustBigTable = false;
    List<SQLBlock> blocks = parameter.getBlocks();
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        try {
          List<QueryBean> queryBeans = getQueryBeans(block);
          queryBeans = queryBeans.stream().filter(s -> !"${bl-ock}".equals(s.getFromTableName())).collect(Collectors.toList());
          results.addAll(getResultByCheck(queryBeans, block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel(), notMustBigTable));
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  private static List<ScanResult> getResultByCheck(List<QueryBean> queryBeans, SQLBlock block, String ruleName, String ruleDes, String level, boolean mustBigTable) {
    List<ScanResult> results = new ArrayList<>();
    Map<QueryBean, List<BTLimitEntity>> limitMap = new HashMap<>();
    for (QueryBean q : queryBeans) {
      if (null != q.getWhereConditionsStr() && !"".equals(q.getWhereConditionsStr())) {  //todo union all 时解析有些异常, where条件后的语句没拿到
        List<BTLimitEntity> limits = new ArrayList<>();
        if (null != q.getFromTableAliasName() && !"".equals(q.getFromTableAliasName())) {
          BTLimitEntity limit = new BTLimitEntity(q.getFromTableAliasName(), q.getFromTableName(), false, mustBigTable);
          if (null != limit.checkField && !"".equals(limit.checkField)) {
            limits.add(limit);
          }
        } else {
          BTLimitEntity limit = new BTLimitEntity(q.getFromTableName(), q.getFromTableName(), false, mustBigTable);
          limit.emptyName = "NULL";
          if (null != limit.checkField && !"".equals(limit.checkField)) {
            limits.add(limit);
          }
        }
        ArrayList<String> joinTableNames = q.getJoinTableName();
        if (null != joinTableNames && joinTableNames.size() > 0) {
          for (String s : joinTableNames) {
            String trim = s.trim();
            String[] split = trim.split(" ");
            String name = split[split.length - 1];
            String tName = split[0];
            BTLimitEntity limit = new BTLimitEntity(name, tName, false, mustBigTable);
            if (null != limit.checkField && !"".equals(limit.checkField)) {
              limits.add(limit);
            }
          }
        }
        limitMap.put(q, limits);
      } else {
        results.add(CustomUtils.getResult(block, ruleName, ruleDes + "(Table-->" + q.getFromTableName() + ")", level));  //这里如果是join可能是一组表名，暂时只展示一个
      }
    }
    if (limitMap.size() > 0) {
      for (Map.Entry<QueryBean, List<BTLimitEntity>> entry : limitMap.entrySet()) {
        QueryBean key = entry.getKey();
        List<BTLimitEntity> limits = entry.getValue();
        WhereClause whereClause = new WhereClause();
        Expression expression = whereClause.getExpression();
        if (null == expression) whereClause.setExpression(new Expression());
        expression = whereClause.getExpression();
        String s = key.getWhereConditionsStr();
        whereClause.setOrigin(s);
        s = s.trim().toLowerCase().replaceAll("[\\s]+", " ");
        whereClause.setFormat(s);
        expression.setConditions(s);
        // System.out.println(s);
        expression = BigTableUtil.recursiveStruct(expression);  // 解析出整个where条件的结构Tree
        BigTableUtil.recursiveLevel(expression);  // 解析出所有层条件的层级Tree
        BigTableUtil.recursiveMap(expression);   // 解析出or条件的MapTree
        BigTableUtil.checkPt_dForeachBT(expression, limits);
      }
      for (Map.Entry<QueryBean, List<BTLimitEntity>> entry : limitMap.entrySet()) {
        List<BTLimitEntity> values = entry.getValue();
        for (BTLimitEntity limit : values) {
          if (!limit.check) {
            results.add(CustomUtils.getResult(block, ruleName, ruleDes + "(Table-->" + limit.name + ")", level));
          }
        }
      }
    }
    return results;
  }

  /**
   * 大表和小表的天分区和小时分区的定制方法
   */
  public static List<ScanResult> bigTableCustom(CustomParameter customParameter) {
//    String ruleName = "大表校验规则";
//    String level = "ERROR";
    ScanRule rule = customParameter.getRule();
    List<QueryBean> bigTableBeans = new ArrayList<>();
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        List<String> strList = null;
        //todo 可能需要替换解析器的地方
        try {
          List<QueryBean> queryBeans = getQueryBeans(block);
          if (null == queryBeans || queryBeans.size() == 0) {
            return results;
          } else {
            bigTableBeans = queryBeans;
          }
          Map<String, List<QueryBean>> queryBeanMap = BigTableUtil.filterQueryBeans(queryBeans, customParameter.getBigTables());
          // 判断大表有没有pt_d的方法
          List<ScanResult> limitResults = mustBePartitioned(queryBeanMap, block, rule);
          if (null != limitResults && limitResults.size() > 0) results.addAll(limitResults);
          // 求区间的结果方法
          // strlist = BigTableUtil.getResults(queryBeanMap, block, bigTables, bDays, sDays, daysOfHour);
          strList = BigTableUtil.getResults(queryBeanMap, customParameter.getLongDays(), customParameter.getShortDays(), customParameter.getHourDays());
          if (null != strList && !strList.isEmpty()) {
            for (String s : strList) {
              results.add(CustomUtils.getResult(block, rule.getName(), s, rule.getLevel().getLevel()));
            }
          }
          List<ScanResult> tableNumbers = checkBigTableNumbers(block, bigTableBeans, customParameter.getBigTables(), rule);
          if (tableNumbers.size() > 0) results.addAll(tableNumbers);
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  private static List<QueryBean> getQueryBeans(SQLBlock block) throws SQLFormatException {
    List<QueryBean> queryBeans = new ArrayList<>();
    if (block.getBlockType().toLowerCase().equals("create")) {
      CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
      queryBeans = BigTableUtil.backQueryBeans(createBean, queryBeans, false);
    } else if (block.getBlockType().toLowerCase().equals("insert")) {
      InsertBean insertBean = new ParseInsert().parseInsertSql(block.getBlockContent());
      queryBeans = BigTableUtil.backQueryBeans(insertBean, queryBeans, false);
    } else if (block.getBlockType().toLowerCase().equals("select")) {
      QueryBean queryBean = new ParseQuery().itratorQuery(block.getBlockContent());
      queryBeans = BigTableUtil.backQueryBeans(queryBean, queryBeans, false);
      // Map<String, List<?>> map = BigTableUtil.getResults(queryBeans, block, bigTables, bDays, sDays, daysOfHour);
      // results = (List<ScanResult>) map.get("scanResult");
      // strlist = (List<String>) map.get("strResult");
    }
    return queryBeans;
  }

  /**
   * 创建临时表必须删除的定制方法 //参数blocks是相对一个sql脚本的script中的所有块
   */
  public static List<ScanResult> createAndDropTable(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SqlScript> scripts = customParameter.getScripts();
    ScanRule rule = customParameter.getRule();
//    String ruleName = "创建临时表对应删除规则";
//    String ruleDes = "一个sql脚本中如果创建一个临时表，那么在这条创建语句后必须要有删表语句";
//    String level = "ERROR";
    String createRegex = ".*create( |\\n)+table.";
    if (null != scripts && scripts.size() > 0) {
      for (SqlScript script : scripts) {
        List<SQLBlock> blocks = script.getBlocks();
        if (null != blocks && blocks.size() > 0) {
          for (int i = 0; i < blocks.size(); i++) {
            String tableName = "";
            int index = 0;
            Matcher createMatcher = Pattern.compile(createRegex).matcher(blocks.get(i).getBlockContent().toLowerCase());
            if (createMatcher.find()) {
              try {
                CreateBean createBean = new ParseCreate().parseCreateSql(blocks.get(i).getBlockContent().toLowerCase());
                tableName = createBean.getTabName();
                String formatName = CustomUtils.formatSpecialCharacter(tableName);
                if (null != formatName && !"".equals(formatName)) {
                  index = i;
                  if (index == blocks.size() - 1) {
                    results.add(CustomUtils.getResult(blocks.get(i), rule.getName(), rule.getDescription() + "(" + tableName + ")", rule.getLevel().getLevel()));
                    break;
                  } else {
                    String dropRegex = "drop +table +.*" + formatName + "(|( |\\n)+)";
                    boolean dropMatcherIsFind = false;
                    for (int j = index + 1; j < blocks.size(); j++) {
                      String s = blocks.get(j).getBlockContent().toLowerCase();
                      s = s.trim().replaceAll("[\\s]+", " ");
                      s = CustomUtils.formatSpecialCharacter(s);
                      Matcher dropMatcher = Pattern.compile(dropRegex).matcher(s);
                      if (dropMatcher.find()) {
                        dropMatcherIsFind = true;
                        break;
                      }
                    }
                    if (!dropMatcherIsFind) {
                      results.add(CustomUtils.getResult(blocks.get(i), rule.getName(), rule.getDescription() + "(" + tableName + ")", rule.getLevel().getLevel()));
                    }
                  }
                }
              } catch (SQLFormatException e) {
                results.add(CustomUtils.getResult(blocks.get(i), "语法解析规则", e.getMessage(), "ERROR"));
              }
            }
          }
        }
      }
    }
    return results;
  }

  /**
   * 创建外表表名需要注释, location里面的表名需要和建表的表名一致
   */
  public static List<ScanResult> checkTableAndComment(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    ScanRule rule = customParameter.getRule();
    // String ruleComment = "外表表名规范规则";
    // String commentDes = "创建外部表时，需要有表名的注释";
    // String ruleLocation = "location表名一致规则";
    String locationDes = "创建外部表时，location里面的表名需要和建表的表名一致";
    // String level = "WARN";
    String tableRegex = ".*create[\\s]+external[\\s]+table[\\s\\S]*";
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        if (Pattern.compile(tableRegex).matcher(block.getBlockContent().toLowerCase()).find()) {
          try {
            CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
            String tabName = createBean.getTabName();
            String tabComment = createBean.getTabComment();
            String tabLocation = createBean.getTabLocation();
            if (tabComment != null && tabComment.length() > 0) {
              String[] split = tabComment.trim().split(" ");
              if (split.length < 2/* && tabComment.trim().length() == 7*/) {
                results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
              }
            } else {
              results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
            }
            if (tabLocation != null && tabLocation.length() > 0) {
              String[] split = tabName.split("\\.");
              tabName = split[split.length - 1];
              String[] locations = tabLocation.split(" ");
              String location = locations[locations.length - 1];
              location = location.replaceAll("'", "").replaceAll("\"", "");
              String[] paths = location.split("/");
              String path = paths[paths.length - 1];
              if (!path.equals(tabName)) {
                results.add(CustomUtils.getResult(block, rule.getName(), locationDes, rule.getLevel().getLevel()));
              }
            }
          } catch (SQLFormatException e) {
            results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
          }
        }
      }
    }
    return results;
  }

  /**
   * 多个大表关联影响性能（大于2张）
   */
  public static List<ScanResult> checkBigTableNumbers(SQLBlock block, List<QueryBean> bigTableBeans, List<BigTable> bigTables, ScanRule rule) {
    List<ScanResult> results = new ArrayList<>();
    Map<String, BigTable> tableMap = bigTables.stream().collect(Collectors.toMap(BigTable::getName, Function.identity()));
    // String ruleBigTables = "大表关联数量";
    String tablesDes = "多个大表关联影响性能（大于2张）";
    String level = "WARN";
    int count = 0;
    for (QueryBean b : bigTableBeans) {
      String name = b.getFromTableName();
      if (name.contains(".")) {
        String[] split = name.split("\\.");
        name = split[split.length - 1];
      }
      BigTable bigTable = tableMap.get(name);
      if (null != bigTable) {
        count = count + 1;
      }
    }
    if (count > 2) {
      results.add(CustomUtils.getResult(block, rule.getName(), tablesDes, level));
    }
    return results;
  }

  /**
   * 一次查询不要多次访问同一张表
   */
  public static List<ScanResult> sameTableTimes(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    ScanRule rule = customParameter.getRule();
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        Map<String, Integer> tNameCount = new HashMap<>();
        Set<String> tNames = new HashSet<>();
        List<QueryBean> queryBeans = new ArrayList<>();
        //todo 可能需要替换解析器的地方
        try {
          if (block.getBlockType().equalsIgnoreCase("create")) {
            CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(createBean, queryBeans, false);
          } else if (block.getBlockType().equalsIgnoreCase("insert")) {
            InsertBean insertBean = new ParseInsert().parseInsertSql(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(insertBean, queryBeans, false);
          } else if (block.getBlockType().equalsIgnoreCase("select")) {
            QueryBean queryBean = new ParseQuery().itratorQuery(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(queryBean, queryBeans, false);
          }
          if (queryBeans.size() > 0) {
            for (QueryBean bean : queryBeans) {
              String tableName = bean.getFromTableName();
              if (!"${bl-ock}".equals(tableName)) {
                if (null == tNameCount.get(tableName)) {
                  tNameCount.put(tableName, 0);
                } else {
                  Integer i = tNameCount.get(tableName);
                  i = i + 1;
                  tNameCount.put(tableName, i);
                  if (i > 2) {
                    tNames.add(tableName);
                    break;
                  }
                }
              }
            }
          }
          if (tNames.size() > 0) {
            results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
          }
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  /**
   * 检查字段注释
   */
  public static List<ScanResult> checkColumnComment(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    ScanRule rule = customParameter.getRule();
//    String ruleName = "字段注释规则";
//    String ruleDes = "创建外表时，表的字段要加上注释";
//    String level = "WARN";
    String regex = "create( |\\n)+external( |\\n)+table";
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        try {
          if (Pattern.compile(regex).matcher(block.getBlockContent().toLowerCase()).find()) {
            CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
            ArrayList<Column> colList = createBean.getTabColList();
            for (Column column : colList) {
              if (column.getColComment() == null || "".equals(column.getColComment())) {
                results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
                break;
              }
            }
          }
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  /**
   * 检查字段词根
   */
  public static List<ScanResult> checkColumnRoot(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    Map<String, String> rootCode = CustomParameter.rootCode;
    ScanRule rule = customParameter.getRule();
    // String regex = "create[\\s]+external[\\s]+table"; //创建外表时才会有字段，临时表最后要删除，所以这边暂时不考虑（block.getBlockType().toLowerCase().equals("create"))
    String regex = "create[\\s]+external[\\s]+table";
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        try {
          if (Pattern.compile(regex).matcher(block.getBlockContent().toLowerCase()).find()) {
            StringBuilder builder = new StringBuilder();
            //todo 可能需要替换解析器的地方
            CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
            ArrayList<Column> colList = createBean.getTabColList();
            for (Column col : colList) {
              String colName = col.getColName();
              if (null != colName && !"".equals(colName)) {
                List<String> codes = CustomUtils.columnCodes(colName);
                boolean legalRoot = false;
                for (String code : codes) {
                  if (null != rootCode.get(code)) {
                    legalRoot = true;
                    break;
                  }
                }
                if (!legalRoot) {
                  builder.append(colName).append(", ");
                }
              }
            }
            String str = builder.toString().trim();
            if (str.length() > 0) {
              if (str.endsWith(",")) {
                str = "(" + str.substring(0, str.length() - 1) + ")";
              }
              results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription() + str, rule.getLevel().getLevel()));
            }
          }
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  /**
   * 在注释部分的output输出的表名需要和脚本中出现的所有的表名是一致的，不多不少（推荐业务提的需求）
   */
  public static List<ScanResult> checkOutputTnames(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    ScanRule rule = customParameter.getRule();
    Set<String> tNames = new HashSet<>();
    String regex = "create[ \\n]+external[ \\n]+table";
    List<SqlScript> scripts = customParameter.getScripts();
    if (null != scripts && scripts.size() > 0) {
      for (SqlScript script : scripts) {
        Map<String, String> nameMap = script.getOutputComment().gettNameMap();
        List<SQLBlock> blocks = script.getBlocks();
        if (null != blocks && blocks.size() > 0) {
          for (SQLBlock block : blocks) {
            try {
              if (Pattern.compile(regex).matcher(block.getBlockContent().toLowerCase()).find()) {
                //todo 可能需要替换解析器的地方
                CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
                String tabName = createBean.getTabName();
                if (tabName.contains(".")) {
                  String[] split = tabName.split("\\.");
                  tabName = split[split.length - 1];
                  tNames.add(tabName);
                }
              }
            } catch (SQLFormatException e) {
              results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
            }
          }
        }
        if (tNames.size() > 0 && (null == nameMap || nameMap.size() == 0)) {
          SQLBlock b = customParameter.getScripts().get(0).getBlocks().get(0);
          SQLBlock block = new SQLBlock(b.getBaseType(), b.getBusiness(), b.getScriptId(), b.getScriptName(), b.getTaskId());
          results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
        }
        if (tNames.size() > 0 && (null != nameMap && nameMap.size() > 0)) {
          for (String key : tNames) {
            nameMap.remove(key);
          }
          if (nameMap.size() > 0) {
            SQLBlock b = customParameter.getScripts().get(0).getBlocks().get(0);
            SQLBlock block = new SQLBlock(b.getBaseType(), b.getBusiness(), b.getScriptId(), b.getScriptName(), b.getTaskId());
            results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
          }
        }
      }
    }
    return results;
  }

  /**
   * 支持版本级全量脚本的临时表重复命名检查，不能有重名临时表
   */
  public static List<ScanResult> checkRepeatTname(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    ScanRule rule = customParameter.getRule();
    Map<String, Integer> tNameCount = new HashMap<>();
    Set<String> tNames = new HashSet<>();
    String regex = "create[ \\n]+table";
    List<SqlScript> scripts = customParameter.getScripts();
    if (null != scripts && scripts.size() > 0) {
      for (SqlScript script : scripts) {
        List<SQLBlock> blocks = script.getBlocks();
        if (null != blocks && blocks.size() > 0) {
          for (SQLBlock block : blocks) {
            try {
              if (Pattern.compile(regex).matcher(block.getBlockContent().toLowerCase()).find()) {
                //todo 可能需要替换解析器的地方
                CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
                String tabName = createBean.getTabName();
                if (null == tNameCount.get(tabName)) {
                  tNameCount.put(tabName, 0);
                } else {
                  Integer i = tNameCount.get(tabName);
                  i = i + 1;
                  tNameCount.put(tabName, i);
                  tNames.add(tabName);
                }
              }
            } catch (SQLFormatException e) {
              results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
            }
          }
        }
      }
    }
    if (tNames.size() > 0) {
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      int n = 0;
      for (String s : tNames) {
        n++;
        if (tNames.size() > n) {
          builder.append(s).append(", ");
        } else {
          builder.append(s);
        }
      }
      builder.append(")");
      SQLBlock b = customParameter.getScripts().get(0).getBlocks().get(0);
      SQLBlock block = new SQLBlock(b.getBaseType(), b.getBusiness(), b.getScriptId(), b.getScriptName(), b.getTaskId());
      results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription() + builder.toString(), rule.getLevel().getLevel()));
    }
    return results;
  }

  /**
   * 消灭子查询内的 GROUP BY
   */
  public static List<ScanResult> checkGroupByOfSubQuery(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    List<SQLBlock> blocks = customParameter.getBlocks();
    ScanRule rule = customParameter.getRule();
    if (null != blocks && blocks.size() > 0) {
      for (SQLBlock block : blocks) {
        try {
          List<QueryBean> queryBeans = new ArrayList<>();
          //todo 可能需要替换解析器的地方
          if (block.getBlockType().equalsIgnoreCase("create")) {
            CreateBean createBean = new ParseCreate().parseCreateSql(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(createBean, queryBeans, true);
          } else if (block.getBlockType().equalsIgnoreCase("insert")) {
            InsertBean insertBean = new ParseInsert().parseInsertSql(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(insertBean, queryBeans, true);
          } else if (block.getBlockType().equalsIgnoreCase("select")) {
            QueryBean queryBean = new ParseQuery().itratorQuery(block.getBlockContent());
            queryBeans = BigTableUtil.backQueryBeans(queryBean, queryBeans, true);
          }
          if (queryBeans.size() > 0) {
            for (QueryBean bean : queryBeans) {
              if (null != bean.getGroupBy() && !"".equals(bean.getGroupBy())) {
                results.add(CustomUtils.getResult(block, rule.getName(), rule.getDescription(), rule.getLevel().getLevel()));
                break;
              }
            }
          }
        } catch (SQLFormatException e) {
          results.add(CustomUtils.getResult(block, "语法解析规则", e.getMessage(), "ERROR"));
        }
      }
    }
    return results;
  }

  /**
   * Hive对 union all 的优化，消灭子查询中的JOIN
   */
  public static List<ScanResult> removeGroupByOfSubQueryForUnion(CustomParameter customParameter) {
    List<ScanResult> results = new ArrayList<>();
    return results;
  }

}
