package com.huawei.spring.core.implement.SQLScan.util;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.util.IdGenerator;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlScanUtil {
  // public static List<ScanResult> scanResults = new ArrayList<>();
  private static Logger log = LoggerFactory.getLogger(SqlScanUtil.class);

  public static List<ScanResult> sqlScanner(CustomParameter customParameter) {
    List<ScanResult> scanResults = new ArrayList<>();
    List<SQLBlock> allBlocks = customParameter.getBlocks();
    ArrayList<SQLBlock> parseFailedBlocks = new ArrayList<>();
    for (SQLBlock b : allBlocks) {
      ScanResult parse = SqlScanUtil.parse(b);
      if (null != parse) {
        scanResults.add(parse);
        parseFailedBlocks.add(b);
      }
    }
    allBlocks.removeAll(parseFailedBlocks);
    for (Map.Entry entry : customParameter.getTypeMap().entrySet()) {
      String key = (String) entry.getKey();
      OperType value = (OperType) entry.getValue();
      Set<ScanRule> rules = value.getScanRules();
      //TODO 这里有个多余循环的情况，因为按规则循环，就必须把所有规则走完，但是不一定每个规则都能找到对应sql块（原先是根据sql块来找规则，所以无此问题，但无法解决所有规则可控的需求）
      for (ScanRule rule : rules) {  // todo 这里的规则类型即操作类型和block即sql里面的操作类型要一致， 如何更有效的将二者进行绑定，可以在这里优化
        if (null == rule.getMethod() || "".equals(rule.getMethod())) { //非定制化
          for (SQLBlock block : allBlocks) {
            if (block.getBlockType().equalsIgnoreCase(key)) {
              scanResults = blockScan(block, rule, scanResults);
            }
          }
        } else { //定制化
          try {
            List<SQLBlock> collect = allBlocks.stream().filter(s -> s.getBlockType().equalsIgnoreCase(key)).collect(Collectors.toList());
            if (null != collect && collect.size() > 0) {
              CustomMethod customMethod = CustomMethod.valueOf(rule.getMethod());
              customParameter.setBlocks(collect);
              customParameter.setRule(rule);
              scanResults = customScan(customParameter, customMethod, scanResults);
            }
          } catch (IllegalArgumentException e) {
            log.warn("This function is not found the corresponding function, please double-check the rules configuration or contact engineers!");
          }
        }
        if (allBlocks.isEmpty()) break;
      }
    }
    return scanResults;
  }


  private static List<ScanResult> blockScan(SQLBlock block, ScanRule rule, List<ScanResult> scanResults) {
    if (rule.getRuleSwitch() != 0) {
      String blockContent = block.getBlockContent();
      Pattern typePattern = Pattern.compile(rule.getTypeRegexp());
      Pattern conditionPattern = null;
      Pattern groupPattern = null;
      if (null != rule.getConditionRegexp() && !"".equals(rule.getConditionRegexp())) {
        conditionPattern = Pattern.compile(rule.getConditionRegexp());
      }
      if (null != rule.getRuleGroup() && !"".equals(rule.getRuleGroup())) {
        groupPattern = Pattern.compile(rule.getRuleGroup());
      }
      if (null == groupPattern) {
        if (typePattern.matcher(blockContent.toLowerCase()).find()) {
          // conditionRegex is null
          if (null == conditionPattern) {
            ScanResult scanResult = getScanResult(block, rule, "Failed", "ACTIVE");
            scanResults.add(scanResult);
          } else {
            // conditionRegex is not null continue to check group
            if (conditionPattern.matcher(blockContent.toLowerCase()).find()) {
//                ScanResult scanResult = getScanResult(block, rule, "Success", "ACTIVE");
//                scanResults.add(scanResult);
            } else {
              ScanResult scanResult = getScanResult(block, rule, "Failed", "ACTIVE");
              scanResults.add(scanResult);
            }
          }
        }
      } else {
        if (null == conditionPattern) {
          Matcher matcher = groupPattern.matcher(blockContent.toLowerCase());
          while (matcher.find()) {
            String group = matcher.group();
            if (typePattern.matcher(group).find()) {
              ScanResult scanResult = getScanResult(block, rule, "Failed", "ACTIVE");
              scanResults.add(scanResult);
              break;
            }
          }
        } else {
          if (typePattern.matcher(blockContent.toLowerCase()).find()) {
            Matcher matcher = groupPattern.matcher(blockContent.toLowerCase());
            while (matcher.find()) {
              String group = matcher.group();
              if (!conditionPattern.matcher(group).find()) {
                ScanResult scanResult = getScanResult(block, rule, "Failed", "ACTIVE");
                scanResults.add(scanResult);
                break;
              }
            }
          }
        }
      }
    }
    return scanResults;
  }

  //todo 利用java反射机制调用定制化函数
  private static List<ScanResult> customScan(CustomParameter parameter, CustomMethod method, List<ScanResult> scanResults) {
    try {
      Class<?> customClass = Class.forName("com.huawei.spring.core.implement.SQLScan.custom.CustomFunctions");
      String methodName = method.name();
      Method customMethod = customClass.getMethod(methodName, CustomParameter.class);
      List<ScanResult> results = (List<ScanResult>) customMethod.invoke(null, parameter);
      if (null != results && results.size() > 0) scanResults.addAll(results);
//      switch (method.ordinal()) {
//        case 0:
//
//          break;
//        case 1:
//
//          break;
//        case 2:
//
//          break;
//        case 3:
//
//          break;
//        case 4:
//
//          break;
//      }
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return scanResults;
  }

  public static List<SQLBlock> parseBuilder(List<String> builders, Set<OperType> opers, SqlScript script, String business) {
    List<SQLBlock> sqlBlocks = new ArrayList<SQLBlock>();
    for (String builder : builders) {
      boolean isEnd = false;
      boolean checkOperType = false;
      String type = null;
      StringBuilder sqlBuilder = new StringBuilder();
      String[] split = builder.split("\n");
      int l = split.length;
      int k = 0;
      if (l % 2 == 0) {
        k = l / 2;
      }
      int sn = Integer.parseInt(split[0]);
      int en = Integer.parseInt(split[2 * (k - 1)]);
      for (int i = 0; i < k; i++) {
        // todo parse remove '#' and 'beeline'
        if (!isHashtag(split[2 * i + 1].trim().toLowerCase())) {
          if (!checkOperType) {
            type = checkOperType(split[2 * i + 1].trim().toLowerCase(), opers);
            if (null != type && !"".equals(type)) checkOperType = true;
            if (checkOperType) sn = Integer.parseInt(split[2*i]);
          }
          if (isBeeline(split[2 * i + 1].trim().toLowerCase())) {
            String s = cutBeeline(split[2 * i + 1].trim().toLowerCase());
            if (null != s) {
              if (checkOperType) {
                sqlBuilder.append(s).append(" ").append("\n");
              }
            }
          } else if (isBeelineEnd(split[2 * i + 1].trim().toLowerCase())) {
            isEnd = isBeelineEnd(split[2 * i + 1].trim().toLowerCase());
            String s = cutBeelineEnd(split[2 * i + 1].trim().toLowerCase());
            if (null != s && checkOperType) {
              sqlBuilder.append(s).append(" ").append("\n");
            }
          } else {
            if (checkOperType) sqlBuilder.append(split[2 * i + 1]).append(" ").append("\n");
          }
        }
        if (isEnd) {
          break;
        }
      }
      String sql = sqlBuilder.toString();
      // ScanResult parse = null;
      if (!"".equals(sql.trim()) && !isSet(sql.trim())) {
        // todo Parsing syntax before the block
//        SQLBlock b_parse = new SQLBlock("syntax_block", sn, en, business, script.getId(), script.getName(), script.getTaskId());
//        b_parse.setBlockContent(sql);
//        b_parse.setVersion(script.getVersion());
//        parse = parse(sql, b_parse);
        SQLBlock block = removeComment(sql, type, sn, en, script, business);
        if (null != block) {
          sqlBlocks.add(block);
        }
      }
//      if (null != parse) {
//        scanResults.add(parse);
//      } else {
//        SQLBlock block = removeComment(sql, opers, sn, en, script, business);
//        if (null != block) {
//          sqlBlocks.add(block);
//        }
//      }
      if (isEnd) {
        break;
      }
    }
    return sqlBlocks;
  }

  public static String checkOperType(String s, Set<OperType> opers) {
    String type = null;
    for (OperType o : opers) {
      if (s.trim().toLowerCase().trim().startsWith(o.getName().toLowerCase())) {
        type = o.getName();
        break;
      }
    }
    return type;
  }

  public static SQLBlock removeComment(String sql, String type, int sn, int en, SqlScript script, String business) {
    //StringBuilder builder = new StringBuilder();
    //String[] sqls = sql.split("\n");
//    int index = 0;
//    boolean isOper = false;
//    String operType = null;
    SQLBlock block = new SQLBlock(script.getBaseType(), business, script.getId(), script.getName(), script.getTaskId());
    block.setId(IdGenerator.createUUID());
    block.setName(type);
    block.setVersion(script.getVersion());
    block.setBlockType(type);
    block.setStartLineNum(sn);
    block.setEndLineNum(en);
    block.setBlockContent(sql);
//    out: for (int i = 0; i < sqls.length; i++) {
//      String s = sqls[i].trim().toLowerCase();
//      for (OperType oper : opers) {
//        if (s.startsWith(oper.getName().toLowerCase())) {
//          index = i;
//          operType = oper.getName();
//          isOper = true;
//          break out;
//        }
//      }
//    }
//    if (isOper) {
//      for (int i = index; i < sqls.length; i++) {
//        builder.append(sqls[i]).append("\n");
//      }
//      block = new SQLBlock(script.getBaseType(), business, script.getId(), script.getName(), script.getTaskId());
//      block.setId(IdGenerator.createUUID());
//      block.setName(operType);
//      block.setVersion(script.getVersion());
//      block.setBlockType(operType);
//      block.setStartLineNum(sn);
//      block.setEndLineNum(en);
//      block.setBlockContent(builder.toString());
//    }
    return block;
  }

  public static boolean isHashtag(String builder) {
    boolean b = false;
    if (builder.startsWith("#")) {
      b = true;
    }
    return b;
  }

  //TODO 由于解析包的问题临时将set的语法解析进行屏蔽
  public static boolean isSet(String builder) {
    boolean b = false;
    if (builder.startsWith("set ")) {
      b = true;
    }
    return b;
  }

  public static boolean isBeeline(String builder) {
    boolean b = false;
    if (builder.startsWith("beeline")) {
      b = true;
    }
    return b;
  }

  public static String cutBeeline(String builder) {
    String s = null;
    String ss = builder.replaceAll(" ", "").replaceAll("\t", "");
    if (ss.length() > 10) {
      s = ss.substring(10, ss.length());
    }
    return s;
  }

  public static boolean isBeelineEnd(String builder) {
    boolean b = false;
    if (builder.endsWith("$end")) {
      return true;
    }
    if (builder.endsWith("\"")) {
      String s = builder.replaceAll(" ", "").replaceAll("\t", "");
      int count = getCount("\"", s);
      if (!s.startsWith("beeline") && count % 2 == 1) {
        b = true;
      }
    }
    return b;
  }

  public static String cutBeelineEnd(String builder) {
    String s = null;
    if (builder.startsWith(" ")) {
      builder = " " + builder.trim();
    } else {
      builder = builder.trim();
    }
    if (builder.endsWith("\"")) {
      s = builder.substring(0, builder.lastIndexOf("\""));
    } else if (builder.endsWith("\"$end")) {
      s = builder.substring(0, builder.indexOf("\"$end"));
    }
    if (null != s && s.endsWith(";")) {
      s = s.substring(0, s.lastIndexOf(";"));
    }
    return s;
  }

  public static int getCount(String reg, String src) {
    int count = 0;
    Pattern pattern = Pattern.compile(reg);
    Matcher matcher = pattern.matcher(src);
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  public static ScanResult parse(SQLBlock block) {
    ScanResult scanResult = null;
    ParseDriver parse = new ParseDriver();
    try {
      String blockContent = formatSpecialCharacter(block.getBlockContent());
      ASTNode node = parse.parse(blockContent);
      //TODO 在block对象中添加一个sql解析驱动对象的属性
      log.info("ASTNode--> " + node.toStringTree());
    } catch (ParseException e) {
      ScanRule scanRule = new ScanRule();
      scanRule.setName("语法校验规则");
      scanRule.setLevel(new RuleLevel(null, "ERROR"));
      scanRule.setDescription(e.getMessage());
      scanResult = getScanResult(block, scanRule, "Failed", "ACTIVE");
    }
    return scanResult;
  }

  public static CustomParameter configParam(List<SQLBlock> blocks, List<SqlScript> scripts, List<BigTable> bigTables, int longDays, int shortDays, int hourDays, Map<String, OperType> typeMap) {
    return new CustomParameter(blocks, scripts, bigTables, longDays, shortDays, hourDays, typeMap);
  }

  public static SQLBlock getBlock(SQLBlock block, String operType, StringBuilder blockLine, int startLineNum, int endLineNum, SqlScript sqlScript, String business) {
    block.setId(IdGenerator.createUUID());
    block.setName(operType);
    block.setBlockContent(blockLine.toString());
    block.setStartLineNum(startLineNum);
    block.setEndLineNum(endLineNum);
    block.setBaseType(sqlScript.getBaseType());
    block.setBlockType(operType);
    block.setScriptId(sqlScript.getId());
    block.setTaskId(sqlScript.getTaskId());
    block.setBusiness(business);
    return block;
  }

  public static ScanResult getScanResult(SQLBlock block, ScanRule rule, String head, String active) {
    ScanResult scanResult = new ScanResult();
    scanResult.setId(IdGenerator.createUUID());
    scanResult.setCreated(new Date());
    scanResult.setStatus(active);
    scanResult.setHead(head);
    scanResult.setLevel(rule.getLevel().getLevel());
    scanResult.setMessage(rule.getDescription());
    scanResult.setRuleId(rule.getId());
    scanResult.setRuleName(rule.getName());
    scanResult.setName(rule.getName() + "_scanResult");
    if (null != block) {
      scanResult.setVersion(block.getVersion());
      scanResult.setStartLineNum(block.getStartLineNum());
      scanResult.setEndLineNum(block.getEndLineNum());
      scanResult.setSqlString(block.getBlockContent());
      scanResult.setScriptName(block.getScriptName());
      scanResult.setOperType(block.getBlockType());
      scanResult.setBlockId(block.getId());
      scanResult.setScriptId(block.getScriptId());
      scanResult.setTaskId(block.getTaskId());
      scanResult.setBusiness(block.getBusiness());
    }
    //scanResult.setOperType("TODO");
    return scanResult;
  }

  public static String formatSpecialCharacter(String s) {
    return s.replaceAll("[$]", "01");
  }
}
