package com.huawei.spring.core.implement.SQLScan.custom;

import com.huawei.spring.catalogue.SQLScan.*;
import com.huawei.spring.catalogue.SQLScan.custom.BTLimitEntity;
import com.huawei.spring.catalogue.SQLScan.custom.CompleteRange;
import com.huawei.spring.catalogue.SQLScan.custom.Expression;
import com.huawei.spring.catalogue.SQLScan.custom.Range;
import com.huawei.spring.catalogue.parse.*;
import com.huawei.spring.catalogue.util.IdGenerator;
import com.huawei.spring.core.implement.SQLScan.util.DateUtil;
import com.huawei.spring.core.implement.SQLScan.util.SqlScanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BigTableUtil {
  private static Logger log = LoggerFactory.getLogger(SqlScanUtil.class);

  private static DateParameter dateParameter = new DateParameter();

  public static List<QueryBean> backQueryBeans(Object bean, List<QueryBean> queryBeans, boolean subQuery) {
    QueryBean queryBean = null;
    if (bean instanceof CreateBean) {
      queryBean = ((CreateBean) bean).getQueryBean();
      if (null != queryBean) {
        if (!subQuery) queryBeans.add(queryBean);
        queryBeans = getQueryBeans(queryBean, queryBeans);
      }
    } else if (bean instanceof InsertBean) {
      queryBean = ((InsertBean) bean).getQueryBean();
      if (null != queryBean) {
        if (!subQuery) queryBeans.add(queryBean);
        queryBeans = getQueryBeans(queryBean, queryBeans);
      }
    } else if (bean instanceof QueryBean) {
      if (!subQuery) queryBeans.add((QueryBean) bean);
      queryBeans = getQueryBeans((QueryBean) bean, queryBeans);
    }
    return queryBeans;
  }

//  public static Map<String, List<?>> getResults(List<QueryBean> queryBeans, SQLBlock block, List<BigTable> bigTables, int bDays, int sDays, int daysOfHour) {
//    Map<String, List<?>> map = new HashMap<>();
//    Map<String, List<QueryBean>> queryBeanMap = filterQueryBeans(queryBeans, bigTables);
//    queryBeans = queryBeanMap.get("big");
//    //List<QueryBean> small = queryBeanMap.get("small");
//    List<BtSWNodeGroup> groupList = groupNodes(queryBeans);
//    // List<ScanResult> results = mustBePartitioned(queryBeans, block);
//    List<ScanResult> results = mustBePartitioned(groupList, block);
//    List<String> bigTableStrs = getResultsByGroups(groupList, bDays, daysOfHour);
////    List<String> smallTableStrs = getResultsByGroups(groupNodes(small), sDays);
////    if (null != smallTableStrs && !smallTableStrs.isEmpty()) {
////      bigTableStrs.addAll(smallTableStrs);
////    }
//    map.put("scanResult", results);
//    map.put("strResult", bigTableStrs);
//    return map;
//  }

  public static List<String> getResults(Map<String, List<QueryBean>> queryBeanMap, int bDays, int sDays, int daysOfHour) {
    List<QueryBean> queryBeans = queryBeanMap.get("big");
    //List<QueryBean> small = queryBeanMap.get("small");
    List<BtSWNodeGroup> groupList = groupNodes(queryBeans);
    // todo 单独出来 List<ScanResult> results = mustBePartitioned(groupList, block);
    return getResultsByGroups(groupList, bDays, daysOfHour);
//    List<String> smallTableStrs = getResultsByGroups(groupNodes(small), sDays);
//    if (null != smallTableStrs && !smallTableStrs.isEmpty()) {
//      bigTableStrs.addAll(smallTableStrs);
//    }
  }

  public static List<String> getResultsByGroups(List<BtSWNodeGroup> groupList, int days, int daysOfHour) {
    List<String> results = new ArrayList<>();
    if (null != groupList && !groupList.isEmpty()) {
      for (BtSWNodeGroup group : groupList) {
        List<String> resultsByGroup = getResultsByGroup(group, days, daysOfHour);
        if (null != resultsByGroup && !resultsByGroup.isEmpty()) {
          results.addAll(resultsByGroup);
        }
      }
    }
    return results;
  }

  public static List<String> getResultsByGroup(BtSWNodeGroup group, int days, int daysOfHour) {
    List<String> results = new ArrayList<>();
    // List<BtSWCNode> nodes = group.getBtSWCNodes();
    List<String> strings = BigTableUtil.parsePartitionNew(group, days, daysOfHour);
    if (strings.size() > 0) {
      results.addAll(strings);
    }
    return results;
  }

  public static List<BtSWNodeGroup> groupNodes(List<QueryBean> queryBeans) {
    List<BtSWNodeGroup> groupNodes = new ArrayList<>();
    if (queryBeans != null && !queryBeans.isEmpty()) {
      for (QueryBean bean : queryBeans) {
        List<BtSWNodeGroup> groups = getGroupNodes(bean);
        if (!groups.isEmpty()) {
          groupNodes.addAll(groups);
        }
      }
    }
    return groupNodes;
  }

  public static List<BtSWNodeGroup> getGroupNodes(QueryBean bean) {
    String uuid = IdGenerator.createUUID();
    String tNameWithoutAlias = "null_" + uuid; //没有别名的自定义一个表名
    List<BtSWNodeGroup> groups = new ArrayList<>();
    List<BtSWCNode> nodes = getNodes(bean);
    Map<String, BtSWCNode> nodeMap = new HashMap<>();
    Map<String, BtSWNodeGroup> groupMap = new HashMap<>();
    if (null != nodes && !nodes.isEmpty()) {
      for (BtSWCNode node : nodes) {
        String nodeStr = node.getOrigin();
        String cd = "";  // 获取等号前面的条件
        String tNameAlias = ""; // 表的别名， 有或者没有
        if (nodeStr.contains("pt_d") || nodeStr.contains("pt_h")) { // 包含pt_d的条件节点， 同时不能忽略非pt_d条件的节点
          node = getStrPt_dOrPt_h(node);
          // String pt_d = node.getPartition();
          cd = node.getPartition();
          // pt_d = pt_d.substring(0, pt_d.length()-4);
        } else {  //处理非pt_d条件的节点
          cd = nodeStr.trim().split(" ")[0].trim(); // 获取等号前面的条件
        }
        if (cd.contains(".")) {
          tNameAlias = cd.split("\\.")[0];
        } else {
          tNameAlias = tNameWithoutAlias;
        }
        if (null == nodeMap.get(tNameAlias)) {
          nodeMap.put(tNameAlias, node);
          List<BtSWCNode> swcNodes = new ArrayList<>();
          swcNodes.add(node);
          BtSWNodeGroup group = new BtSWNodeGroup();
          if (!group.isPt_d()) group.setPt_d(node.isPt_d());
          if (!group.isPt_h()) group.setPt_h(node.isPt_h());
          group.setBtSWCNodes(swcNodes);
          groupMap.put(tNameAlias, group);
        } else {
          BtSWNodeGroup group = groupMap.get(tNameAlias);
          if (!group.isPt_d()) group.setPt_d(node.isPt_d());
          if (!group.isPt_h()) group.setPt_h(node.isPt_h());
          group.getBtSWCNodes().add(node);
        }
      }
    }
    groupMap = getOrWhereConditionGroup(groupMap);
    Collection<BtSWNodeGroup> values = groupMap.values();
    return new ArrayList<>(values);
  }

  public static Map<String, BtSWNodeGroup> getOrWhereConditionGroup(Map<String, BtSWNodeGroup> groupMap) {
    for (Map.Entry<String, BtSWNodeGroup> entry : groupMap.entrySet()) {
      BtSWNodeGroup value = entry.getValue();
      List<BtSWCNode> nodes = value.getBtSWCNodes();
      Map<Integer, List<BtSWCNode>> orGroupMapNodes = new HashMap<>();
      for (BtSWCNode node : nodes) {
        if (null == orGroupMapNodes.get(node.getGroupNum())) {
          List<BtSWCNode> swcNodes = new ArrayList<>();
          swcNodes.add(node);
          orGroupMapNodes.put(node.getGroupNum(), swcNodes);
        } else {
          orGroupMapNodes.get(node.getGroupNum()).add(node);
        }
      }
      value.setOrGroupMapNodes(orGroupMapNodes);
    }
    return groupMap;
  }

  public static BtSWCNode getStrPt_dOrPt_h(BtSWCNode node) {
    String s = node.getOrigin();
    String pt_dOrh = "";
    if (s.contains("pt_d")) {
      node.setPt_d(true);
      pt_dOrh = s.substring(0, s.indexOf("pt_d") + 4);
    } else {
      node.setPt_h(true);
      pt_dOrh = s.substring(0, s.indexOf("pt_h") + 4);
    }
    String[] split = pt_dOrh.split(" ");
    node.setPartition(split[split.length-1]);
    return node;
  }

  public static List<BtSWCNode> getNodes(QueryBean bean) {
    int count = 0;
    List<BtSWCNode> nodes = new ArrayList<BtSWCNode>();
    String whereConditionsStr = bean.getWhereConditionsStr();
    if (null == whereConditionsStr || "".equals(whereConditionsStr)) {
      return nodes;
    }
    String str = whereConditionsStr.trim().toLowerCase().replaceAll("[\\s]+", " ");
    str = ParseHiveSQL.formatWhereStr(str);
    String[] split = str.trim().split(" ");
    StringBuilder builder = new StringBuilder();
    boolean isBetween = false;
    boolean isAndBelongBetween = false;
    for (int i = 0; i < split.length; i++) {
      String node = split[i];
      if (node.equals("between")) {
        isBetween = true;
      }
      if (node.equals("and") && isBetween) {
        isAndBelongBetween = true;
        isBetween = false;
      } else if ((node.equals("and") || node.equals("or")) && !isBetween) {
        isAndBelongBetween = false;
      }
      if ((node.equals("and") || node.equals("or")) && !isAndBelongBetween) {
        if (!"and ".equals(builder.toString()) && !"or ".equals(builder.toString())) {
          if (node.equals("or")) {
            String cnode = builder.toString();
            nodes.add(getNode(cnode, count));
            count++;
            builder = new StringBuilder();
            builder.append(node).append(" ");
          } else {
            String cnode = builder.toString();
            nodes.add(getNode(cnode, count));
            builder = new StringBuilder();
            builder.append(node).append(" ");
          }
        }
      } else {
        builder.append(node).append(" ");
      }
    }
    if (!"".equals(builder.toString()) && !"and ".equals(builder.toString()) && !"or ".equals(builder.toString())) {
      String cnode = builder.toString();
      nodes.add(getNode(cnode, count));
    }
    return nodes;
  }

  public static BtSWCNode getNode(String s, int count) {
    BtSWCNode node = new BtSWCNode();
    node.setOrigin(s);
    node.setGroupNum(count);
    String[] cns = s.split(" ");
    String sub = s.substring(s.indexOf(" ") + 1);
    if (cns[0].equals("and")) {
      node.setAnd(true);
      node.setNode(sub);
      node = configNode(node);
    } else if (cns[0].equals("or")) {
      node.setAnd(false);
      node.setNode(sub);
      node = configNode(node);
    } else {
      node.setAnd(true);
      node.setNode(sub);
      node = configNode(node);
    }
    return node;
  }

  public static BtSWCNode configNode(BtSWCNode node) {
    String s = node.getOrigin();
    if (s.contains("pt_d") && s.contains("between") && s.contains("and")) {
      node = isBetweenNode(node);
    } else if (s.contains("pt_d") && s.contains("=") && !s.contains("<") && !s.contains(">")) {
      node = isEqualsNode(node);
    } else if (s.contains("pt_d") && s.contains("<") && !s.contains(">") && !s.contains("=")) {
      node = isLessThan(node);
    } else if (s.contains("pt_d") && s.contains("<=")) {
      node = isLessThan(node);
    } else if (s.contains("pt_d") && s.contains(">") && !s.contains("<") && !s.contains("=")) {
      node = isMoreThan(node);
    } else if (s.contains("pt_d") && s.contains(">=")) {
      node = isMoreThan(node);
    } else if (s.contains("pt_d") && s.contains("<>")) {
    } else if (s.contains("pt_d") && (s.contains(" in ") || s.contains(" in("))) {
      node = isIn(node);
    }
    //TODO substr中的<,<=,=,>,>=先不做, in和not in 需在考虑
    return node;
  }

  private static String dateToStr(String value) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String classPath = "com.huawei.spring.core.implement.SQLScan.util.DateUtil";
    String date = null;
    if (null != value && !"".equals(value)) {
      Class<?> classDateUtil = Class.forName(classPath);
      Method method = classDateUtil.getMethod(value, null);
      date = (String) method.invoke(null, null);
    }
    return date;
  }

  public static BtSWCNode isBetweenNode(BtSWCNode node) {
    List<BtDateValue> values = new ArrayList<>();
    String s = node.getNode();
    String tss = null;
    String tse = null;
    int tssc = 0;
    int tsec = 0;
    String between = s.substring(s.indexOf("between") + 7, s.length()).trim();
    String[] ands = between.split("and");
    String tssSub = null;
    String tseSub = null;
    boolean isQuotesTssSub = false;
    boolean isQuotesTseSub = false;
    if (ands.length < 2) {
      node.setLegal(false);
    } else {
      tss = ands[0].trim();
      tse = ands[1].trim().split(" ")[0].trim();
      if ((tss.startsWith("'") && tss.endsWith("'")) || (tss.startsWith("\"") && tss.endsWith("\""))) {
        isQuotesTssSub = true;
      }
      if ((tse.startsWith("'") && tse.endsWith("'")) || (tse.startsWith("\"") && tse.endsWith("\""))) {
        isQuotesTseSub = true;
      }
      tssSub = tss.replaceAll("'", "").replaceAll("\"", "").trim();
      tseSub = tse.replaceAll("'", "").replaceAll("\"", "").trim();
    }
    if (null != tssSub) {
      //TODO $date
      String key1 = dateParameter.getDateMap().get(tssSub);
      String key2 = dateParameter.getDateMap().get(tseSub);
      try {
        if (key1 != null && !"".equals(key1)) tssSub = dateToStr(key1);
        if (key2 != null && !"".equals(key2)) tseSub = dateToStr(key2);
      } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        e.printStackTrace();
      }
      if (tssSub.length() != 8 || tseSub.length() != 8) {
        node.setLegal(false);
      }
      BtDateValue v1 = new BtDateValue(tssSub, isQuotesTssSub, 4);
      BtDateValue v2 = new BtDateValue(tseSub, isQuotesTseSub, 2);
      values.add(v1);
      values.add(v2);
      node.setValues(values);
    }
    return node;
  }

  public static BtSWCNode isEqualsNode(BtSWCNode node) {
    List<BtDateValue> values = new ArrayList<>();
    String s = node.getNode();
    String[] split = s.split("=");
    String t = split[1].trim().split(" ")[0].trim();
    boolean isQuotes = false;
    if ((t.startsWith("'") && t.endsWith("'")) || (t.startsWith("\"") && t.endsWith("\""))) {
      isQuotes = true;
    }
    String tSub = t.replaceAll("'", "").replaceAll("\"", "").trim();
    //TODO $date
    String key = dateParameter.getDateMap().get(tSub);
    try {
      if (key != null && !"".equals(key)) tSub = dateToStr(key);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    if (tSub.length() != 8) {
      node.setLegal(false);
    } else {
      BtDateValue v1 = new BtDateValue(tSub, isQuotes, 0);
      values.add(v1);
    }
    node.setValues(values);
    return node;
  }

  public static BtSWCNode isLessThan(BtSWCNode node) {
    List<BtDateValue> values = new ArrayList<>();
    String s = node.getNode();
    String[] split = null;
    boolean b = s.contains("<=");
    if (b) {
      split = s.split("<=");
    } else {
      split = s.split("<");
    }
    String t = split[1].trim().split(" ")[0].trim();
    boolean isQuotes = false;
    if ((t.startsWith("'") && t.endsWith("'")) || (t.startsWith("\"") && t.endsWith("\""))) {
      isQuotes = true;
    }
    String tSub = t.replaceAll("'", "").replaceAll("\"", "").trim();
    //TODO $date
    String key = dateParameter.getDateMap().get(tSub);
    try {
      if (key != null && !"".equals(key)) tSub = dateToStr(key);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    if (tSub.length() != 8) {
      node.setLegal(false);
    } else {
      BtDateValue v1 = null;
      if (b) {
        v1 = new BtDateValue(tSub, isQuotes, 2);
      } else {
        v1 = new BtDateValue(tSub, isQuotes, 1);
      }
      values.add(v1);
      node.setLegal(false);
      node.setValues(values);
    }
    return node;
  }

  public static BtSWCNode isMoreThan(BtSWCNode node) {
    List<BtDateValue> values = new ArrayList<>();
    String s = node.getNode();
    String[] split = null;
    boolean b = s.contains(">=");
    if (b) {
      split = s.split(">=");
    } else {
      split = s.split(">");
    }
    String t = split[1].trim().split(" ")[0].trim();
    boolean isQuotes = false;
    if ((t.startsWith("'") && t.endsWith("'")) || (t.startsWith("\"") && t.endsWith("\""))) {
      isQuotes = true;
    }
    String tSub = t.replaceAll("'", "").replaceAll("\"", "").trim();
    //TODO $date
    String key = dateParameter.getDateMap().get(tSub);
    try {
      if (key != null && !"".equals(key)) tSub = dateToStr(key);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    if (tSub.length() != 8) {
      node.setLegal(false);
    } else {
      BtDateValue v1 = null;
      if (b) {
        v1 = new BtDateValue(tSub, isQuotes, 4);
      } else {
        v1 = new BtDateValue(tSub, isQuotes, 3);
      }
      values.add(v1);
      node.setValues(values);
    }
    return node;
  }

  public static BtSWCNode isIn(BtSWCNode node) {
    List<BtDateValue> values = new ArrayList<>();
    String s = node.getNode();
    String ss = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
    String[] splits = ss.trim().split(",");
    for (String split : splits) {
      boolean isQuotes = false;
      if ((split.trim().startsWith("'") && split.trim().endsWith("'")) || (split.trim().startsWith("\"") && split.trim().endsWith("\""))) {
        isQuotes = true;
      }
      String trim = split.trim().replaceAll("'", "");
      //TODO $date
      String key = dateParameter.getDateMap().get(trim);
      try {
        if (key != null && !"".equals(key)) trim = dateToStr(key);
      } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        e.printStackTrace();
      }
      if (trim.length() == 8) {
        BtDateValue v1 = new BtDateValue(trim, isQuotes, 0);
        values.add(v1);
      }
    }
    node.setValues(values);
    return node;
  }

  public static List<String> getStrlist(List<QueryBean> queryBeans, int days) {
    List<String> list = new ArrayList<>();
    for (QueryBean bean : queryBeans) {
      if (!"${bl-ock}".equals(bean.getFromTableName())) {
        List<String> strings = BigTableUtil.parseCondition(bean, days);
        if (!strings.isEmpty()) {
          list.addAll(strings);
        }
      }
    }
    return list;
  }

  public static Map<String, List<QueryBean>> filterQueryBeans(List<QueryBean> queryBeans, List<BigTable> bigTables) {
    // Map<String, ScanRule> configedMap = queryBeans.stream().collect(Collectors.toMap(ScanRule::getId, Function.identity()));
    List<QueryBean> bigTableBeans = new ArrayList<>();
    List<QueryBean> otherBeans = new ArrayList<>();
    Map<String, List<QueryBean>> map = new HashMap<>();
    Map<String, BigTable> tableMap = bigTables.stream().collect(Collectors.toMap(BigTable::getName, Function.identity()));
    for (QueryBean bean : queryBeans) {
      String tableName = bean.getFromTableName();
      if (tableName.contains(".")) {
        String[] split = tableName.split("\\.");
        tableName = split[split.length - 1];
      }
      BigTable bigTable = tableMap.get(tableName);
      if (null != bigTable) {
        if (bigTable.getIsPt_d() == 1)
          bigTableBeans.add(bean);
      } else if (!"${bl-ock}".equals(tableName)) {
        otherBeans.add(bean);
      }
    }
    map.put("big", bigTableBeans);
    map.put("small", otherBeans);
    return map;
  }

//  public static List<ScanResult> mustBePartitioned(List<QueryBean> beans, SQLBlock block) {
//    List<ScanResult> results = new ArrayList<>();
//    for (QueryBean b : beans) {
//      // todo 多表关联时，需考虑一个查询语句有多个大表的分区
//      if (!b.getWhereConditionsStr().contains("pt_d")) {
//        ScanRule scanRule = new ScanRule();
//        scanRule.setName("大表天分区校验规则");
//        scanRule.setLevel(new RuleLevel(null, "ERROR"));
//        scanRule.setDescription("Large table must have a talent zone query conditions" + "(" + b.getFromTableAliasName() + ")");
//        results.add(SqlScanUtil.getScanResult(block, scanRule, "Failed", "ACTIVE"));
//      }
//    }
//    return results;
//  }

//  public static List<ScanResult> mustBePartitioned(List<BtSWNodeGroup> groupList, SQLBlock block) {
//    String ruleName = "大表天分区校验规则";
//    String ruleDes = "大表查询必须要有天分区条件作为限制条件";
//    String level = "ERROR";
//    List<ScanResult> results = new ArrayList<>();
//    for (BtSWNodeGroup group : groupList) {
//      // todo 多表关联时，需考虑一个查询语句有多个大表的分区
//      // if (!b.getWhereConditionsStr().contains("pt_d")) {}
//      Map<Integer, List<BtSWCNode>> orGroupMapNodes = group.getOrGroupMapNodes();
//      for (Map.Entry<Integer, List<BtSWCNode>> entry : orGroupMapNodes.entrySet()) {
//        List<BtSWCNode> nodesByOr = entry.getValue();
//        int count = 0;
//        for (BtSWCNode node : nodesByOr) {
//          if (node.getOrigin().contains("pt_d")) {
//            break;
//          }
//          count += 1;
//        }
//        if (count == nodesByOr.size() && count > 0) {
//          ScanRule scanRule = new ScanRule();
//          scanRule.setName(ruleName);
//          scanRule.setLevel(new RuleLevel(null, "ERROR"));
//          // scanRule.setDescription(ruleDes + "(" + b.getFromTableAliasName() + ")");
//          scanRule.setDescription(ruleDes);
//          results.add(SqlScanUtil.getScanResult(block, scanRule, "Failed", "ACTIVE"));
//        }
//      }
//    }
//    return results;
//  }

  private static List<QueryBean> getQueryBeans(QueryBean bean, List<QueryBean> queryBeans) {
    ArrayList<QueryBean> beans = bean.getQueryBeans();
    if (null != beans && !beans.isEmpty()) {
      queryBeans.addAll(beans);
      for (QueryBean b : beans) {
        if (null != b) {
          getQueryBeans(b, queryBeans);
        }
      }
    }
    return queryBeans;
  }

//  public static List<String> parsePartitionNew(BtSWNodeGroup group, int days, int daysOfHour) {
//    List<String> strings = new ArrayList<>();
//    List<BtSWCNode> nodes = group.getBtSWCNodes();
//    int period = 0;
//    int condition = 0;
//    long totalPeriod = 0;
//    //区间
//    HashSet<String> eqSet = new HashSet<>();
//    //条件+区间(可以有多个)
//    List<Map<String, String>> intervalList = new ArrayList<>();
//    //intervalList的第一个值
//    if (null != nodes && !nodes.isEmpty()) {
//      Map<String, String> map = null;
//      for (int i = 0; i < nodes.size(); i++) {
//        BtSWCNode bswcs = nodes.get(i); // todo BtSWCNode.isLegal need deal
//        if (bswcs.isPt_d() || (null != bswcs.getValues() && bswcs.getValues().size() > 0) || bswcs.getPartition().contains("pt_d")) {
//          //todo
//          for (BtDateValue value : bswcs.getValues()) {
//            if (!value.isQuotes()) strings.add("pt_d的时间值为字符串类型， (" + value.getDateValue() + ")需要加上引号");
//          }
//          if (null == map || map.size() > 0) {
//            map = getIntervalBySWCNode(bswcs);
//            if (null != map && map.size() > 0) {
//              intervalList.add(map);
//            }
//          } else if (intervalList.size() >0){
//            intervalList = intervalMerge(intervalList, bswcs);
//          }
//        }
//      }
//    }
//    // 去除重复区间
//    for (int i = 0; i <intervalList.size() ; i++) {
//      HashMap<String,String> mapCom1= (HashMap<String, String>) intervalList.get(i);
//      for (int j = i+1; j <intervalList.size() ; j++) {
//        HashMap<String,String> mapCom2= (HashMap<String, String>) intervalList.get(j);
//        if (mapCom1.equals(mapCom2)){
//          intervalList.remove(i);
//          i--;
//          break;
//        }
//      }
//    }
//    //计算区间的周期
//    for (int i = 0; i < intervalList.size(); i++) {
//      HashMap map = (HashMap) intervalList.get(i);
//      int isCL = 1;
//      int isCR = 1;
//      String lValue = null;
//      String rValue = null;
//      if (map.get("l1") != null) {
//        lValue = (String) map.get("l1");
//      } else {
//        lValue = (String) map.get("l2");
//        isCL = 2;
//      }
//      if (map.get("r1") != null) {
//        rValue = (String) map.get("r1");
//      } else {
//        rValue = (String) map.get("r2");
//        isCR = 2;
//      }
//      long stamp = 0;
//      try {
//        stamp = dayStamp(lValue, rValue, isCL, isCR);
//      } catch (ParseException e) {
//        e.printStackTrace();
//        // return "Talent zone time period is greater than the maximum limit: 3 days";
//        strings.add("天分区的时间大于默认天数：" + days + " 天");
//      }
//      totalPeriod = totalPeriod + stamp;
//    }
//    if (group.isPt_d()) {
//      if (totalPeriod > days) {
//        //分区周期大于3天+“where条件”
//        // return "Talent zone time period is greater than the maximum limit: 3 days";
//        strings.add("天分区的时间大于默认天数：" + days + " 天");
//      } else {
//        if (group.isPt_h()) {
//          if (totalPeriod > daysOfHour) {
//            strings.add("小时分区的时间大于默认天数：" + daysOfHour + " 天");
//          }
//        }
//      }
//    }
//    return strings;
//  }

  public static List<String> parsePartitionNew(BtSWNodeGroup group, int days, int daysOfHour) {
    List<String> strings = new ArrayList<>();
    Map<Integer, List<BtSWCNode>> mapNodes = group.getOrGroupMapNodes();
    List<CompleteRange> completeRanges = new ArrayList<>();
    long totalPeriod = 0;
    if (null != mapNodes && mapNodes.size() > 0) {
      for (Map.Entry<Integer, List<BtSWCNode>> entry : mapNodes.entrySet()) {
        List<Range> leftSection = new ArrayList<>(); //左区间集合
        List<Range> rightSection = new ArrayList<>(); //右区间集合
        List<BtSWCNode> nodeList = entry.getValue();
        for (BtSWCNode node : nodeList) {
          // todo BtSWCNode.isLegal need deal
          if (node.isPt_d() || (null != node.getValues() && node.getValues().size() > 0) || (null != node.getPartition() && node.getPartition().contains("pt_d"))) {
            //todo
            for (BtDateValue value : node.getValues()) {
              if (!value.isQuotes()) strings.add("pt_d的时间值为字符串类型， (" + value.getDateValue() + ")需要加上引号");
            }
            getIntervalBySWCNode(leftSection, rightSection, node);
          }
        }
        if (leftSection.size() > 0) {
          //计算and条件的区间, 左边取最大, 右边取最小,进行排序-升序
          minTpMax(leftSection);
          minTpMax(rightSection);
          CompleteRange completeRange = new CompleteRange(leftSection.get(leftSection.size() - 1), rightSection.get(0));
          long andDays = 0;
          try {
            andDays = calculateAndDays(completeRange);
          } catch (ParseException e) {
            e.printStackTrace();
            strings.add("天分区的时间大于默认天数：" + days + " 天");
          }
          if (andDays > 0) completeRanges.add(completeRange);
        }
      }
    }
    if (completeRanges.size() > 0) {
      completeRanges.sort(new Comparator<CompleteRange>() {
        @Override
        public int compare(CompleteRange o1, CompleteRange o2) {
          return o1.getLeftRange().getValue().compareTo(o2.getLeftRange().getValue());
        }
      });
      try {
        totalPeriod = calculateOrDays(completeRanges);
      } catch (ParseException e) {
        e.printStackTrace();
        strings.add("天分区的时间大于默认天数：" + days + " 天");
      }
    }
    if (group.isPt_d()) {
      if (totalPeriod > days) {
        //分区周期大于3天+“where条件”
        // return "Talent zone time period is greater than the maximum limit: 3 days";
        strings.add("天分区的时间大于默认天数：" + days + " 天");
      } else {
        if (group.isPt_h()) {
          if (totalPeriod > daysOfHour) {
            strings.add("小时分区的时间大于默认天数：" + daysOfHour + " 天");
          }
        }
      }
    }
    return strings;
  }

  public static void minTpMax(List<Range> param) {
    param.sort(new Comparator<Range>() {
      @Override
      public int compare(Range o1, Range o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
  }

  public static long calculateAndDays(CompleteRange completeRange) throws ParseException {
    Range leftRange = completeRange.getLeftRange();
    Range rightRange = completeRange.getRightRange();
    int isCL = 1;
    int isCR = 1;
    if (leftRange.getDirection().equals("l2")) isCL = 2;
    if (rightRange.getDirection().equals("r2")) isCR = 2;
    return dayStamp(leftRange.getValue(), rightRange.getValue(), isCL, isCR);
  }

  public static long calculateOrDays(List<CompleteRange> completeRanges) throws ParseException {
    long days = 0;
    List<CompleteRange> newCompleteRanges = new ArrayList<>();
    newCompleteRanges.add(completeRanges.get(0)); //前提至少有一个区间
    for (int i = 1; i < completeRanges.size(); i++) {
      newCompleteRanges = getNewCompleteRanges(newCompleteRanges, completeRanges.get(i));
    }
    if (newCompleteRanges.size() > 0) {
      for (CompleteRange cr : newCompleteRanges) {
        days = days + calculateAndDays(cr);
      }
    }
    return days;
  }

  public static List<CompleteRange> getNewCompleteRanges(List<CompleteRange> oldCompleteRanges, CompleteRange completeRange) {
    List<CompleteRange> newCompleteRanges = new ArrayList<>();
    int leftCursor = 0; //左游标
    int rightCursor = 0; //右游标
    for (int i = 0; i < oldCompleteRanges.size(); i++) {
      CompleteRange oldCR = oldCompleteRanges.get(i);
      if (isLeftMoreRight(oldCR, completeRange)) {
        newCompleteRanges.add(oldCR);
      } else {
        leftCursor = i + 1; //加1只是与0做区别
        break;
      }
    }
    if (leftCursor > 0) {
      leftCursor = leftCursor - 1;
      CompleteRange startRange = oldCompleteRanges.get(leftCursor);
      Range leftRange = null;
      Range rightRange = null;
      Range oldLeftRange = startRange.getLeftRange();
      Range newLeftRange = completeRange.getLeftRange();
      if (oldLeftRange.getValue().compareTo(newLeftRange.getValue()) < 0) leftRange = oldLeftRange;
      if (newLeftRange.getValue().compareTo(oldLeftRange.getValue()) < 0) leftRange = newLeftRange;
      if (newLeftRange.getValue().compareTo(oldLeftRange.getValue()) == 0) {
        if (oldLeftRange.getDirection().equals("l2")) {
          leftRange = oldLeftRange;
        } else {
          leftRange = newLeftRange;
        }
      }
      if (isRightInFirstRange(startRange, completeRange)) {
        rightRange = startRange.getRightRange();
        CompleteRange newCR = new CompleteRange(leftRange, rightRange);
        newCompleteRanges.add(newCR);
        List<CompleteRange> partCRs = oldCompleteRanges.subList(leftCursor + 1, oldCompleteRanges.size());
        newCompleteRanges.addAll(partCRs);
      } else {
        for (int i = leftCursor + 1; i < oldCompleteRanges.size(); i++) {
          if (isRightLessRight(completeRange, oldCompleteRanges.get(i))) {
            rightCursor = i + 1;
            break;
          }
        }
        if (rightCursor == 0) {
          rightRange = completeRange.getRightRange();
          CompleteRange newCR = new CompleteRange(leftRange, rightRange);
          newCompleteRanges.add(newCR);
        } else {
          rightCursor = rightCursor - 1;
          CompleteRange oldCR = oldCompleteRanges.get(rightCursor);
          Range newRightRange = completeRange.getRightRange();
          Range oldLeftRange2 = oldCR.getLeftRange();
          if (newRightRange.getValue().compareTo(oldLeftRange2.getValue()) > 0) rightRange = oldCR.getRightRange();
          if (newRightRange.getValue().compareTo(oldLeftRange2.getValue()) < 0) {
            rightRange = newRightRange;
            newCompleteRanges.add(oldCR);
          }
          if (newRightRange.getValue().compareTo(oldLeftRange2.getValue()) == 0) {
            if (newRightRange.getDirection().equals("r1") && oldLeftRange2.getDirection().equals("l1")) {
              rightRange = newRightRange;
            } else {
              rightRange = oldCR.getRightRange();
              newCompleteRanges.add(oldCR);
            }
          }
          CompleteRange newCR = new CompleteRange(leftRange, rightRange);
          newCompleteRanges.add(newCR);
          List<CompleteRange> partCRs = oldCompleteRanges.subList(rightCursor + 1, oldCompleteRanges.size());
          newCompleteRanges.addAll(partCRs);
        }
      }
    } else {
      newCompleteRanges.add(completeRange);
    }
    return newCompleteRanges;
  }

  public static boolean isLeftMoreRight(CompleteRange oldCR, CompleteRange newCR) {
    boolean b = false;
    Range oldRightRange = oldCR.getRightRange();
    Range leftRange = newCR.getLeftRange();
    int compare = leftRange.getValue().compareTo(oldRightRange.getValue());
    if (compare > 0) b = true;
    if (compare == 0) {
      if (oldRightRange.getDirection().equals("r1") && leftRange.getDirection().equals("l1")) {
        b = true;
      }
    }
    return b;
  }

  public static boolean isRightInFirstRange(CompleteRange oldCR, CompleteRange newCR) {
    boolean b = false;
    Range oldRightRange = oldCR.getRightRange();
    Range rightRange = newCR.getRightRange();
    int compare = rightRange.getValue().compareTo(oldRightRange.getValue());
    if (compare < 0) b = true;
    if (compare == 0) {
      if (oldRightRange.getDirection().equals("r2")) {
        b = true;
      }
    }
    return b;
  }

  public static boolean isRightLessRight(CompleteRange newCR, CompleteRange oldCR) {
    boolean b = false;
    Range newRightRange = newCR.getRightRange();
    Range oldRightRange = oldCR.getRightRange();
    if (newRightRange.getValue().compareTo(oldRightRange.getValue()) < 0) b = true;
    if (newRightRange.getValue().compareTo(oldRightRange.getValue()) == 0) {
      if (oldRightRange.getDirection().equals("r2")) {
        b = true;
      }
    }
    return b;
  }

//
//  /**
//   * zwx450505  add
//   * 合并区间或者新增区间
//   *
//   * @param intervalList
//   * @param bswcs
//   * @return
//   */
//
//  //todo and,or 是有优先级的 and > or
//  public static List<Map<String, String>> intervalMerge(List<Map<String, String>> intervalList, BtSWCNode bswcs) {
//    List<BtDateValue> values = bswcs.getValues();
//    if (values != null && values.size() > 0) {
//      intervalList = getMergeByCondition(intervalList, bswcs, bswcs.isAnd());
////    } else if (values == null) {
////      //in 条件为or
////      List<BtDateValue> bdvList = bswcs.getValues();
////      for (int j = 0; j < bdvList.size(); j++) {
////        String value = bdvList.get(j).getDateValue();
////        HashMap<String, String> map11 = new HashMap<>();
////        map11.put("l2", value);
////        map11.put("r2", value);
////        intervalList = getMergeByCondition(intervalList, map11, false);
////
////      }
//    }
//    return intervalList;
//  }

//  /**
//   * zwx450505 add
//   *
//   * @param intervalList
//   * @param bswcs
//   * @param isAnd
//   * @return
//   */
//  public static ArrayList addItervalOrAddCon(ArrayList intervalList, BtSWCNode bswcs, boolean isAnd) {
//    Map map1 = getIntervalBySWCNode(bswcs);
//    HashSet<String> set = (HashSet<String>) intervalList.get(0);
//    if (map1 != null) {
//      //新增区间
//      intervalList.add(map1);
//    } else {
//      //新增条件
//      ArrayList<BtDateValue> bdList = (ArrayList<BtDateValue>) bswcs.getValues();
//      for (int i = 0; i < bdList.size(); i++) {
//        set.add(bdList.get(i).getDateValue());
//      }
//      intervalList.remove(0);
//      intervalList.add(0, set);
//
//    }
//    return intervalList;
//  }

//  /**
//   * zwx450505 add
//   * 获得合并后的区间
//   *
//   * @param list
//   * @param bswc
//   * @return
//   */
//  public static List<Map<String, String>> getMergeByCondition(List<Map<String, String>> list, BtSWCNode bswc, boolean isAnd) {
//    Map<String, String> map1 = getIntervalBySWCNode(bswc);
//    if (null != map1 && map1.size() > 0) {
//      //合并区间 todo and,or 是有优先级的 and > or
//      for (int i = 0; i < list.size(); i++) {
//        list = getMergeByInteval(list, isAnd, map1, list.get(i), i);
//      }
//    } /*else {
//      //添加条件
//      HashSet<String> set = (HashSet<String>) list.get(0);
//      List<BtDateValue> values = bswc.getValues();
//      for (int j = 0; j < values.size(); j++) {
//        set.add(values.get(j).getDateValue());
//      }
//      list.remove(0);
//      list.add(0, set);
//
//    }*/
//    return list;
//  }

//  public static List<Map<String, String>> getMergeByCondition(List<Map<String, String>> list, HashMap<String, String> map1, boolean isAnd) {
//    //合并区间
//    for (int i = 1; i < list.size(); i++) {
//      list = getMergeByInteval(list, isAnd, map1, list.get(i), i);
//    }
//    return list;
//  }


//  public static List<Map<String, String>> getMergeByInteval(List<Map<String, String>> list, boolean isAnd, Map<String, String> inmap1, Map<String, String> inmap2, int index) {
//    HashMap<String, String> outMap = new HashMap<>();
//    String ll1 = inmap1.containsKey("l1") ? inmap1.get("l1").trim() : inmap1.get("l2").trim();
//    int intll1 = Integer.parseInt(ll1);
//    String rr1 = inmap1.containsKey("r1") ? inmap1.get("r1").trim() : inmap1.get("r2").trim();
//    int intrr1 = Integer.parseInt(rr1);
//    String ll2 = inmap2.containsKey("l1") ? inmap2.get("l1").trim() : inmap2.get("l2").trim();
//    int intll2 = Integer.parseInt(ll2);
//    String rr2 = inmap2.containsKey("r1") ? inmap2.get("r1").trim() : inmap2.get("r2").trim();
//    int intrr2 = Integer.parseInt(rr2);
//    //and |or判断
//    if (isAnd) {
//      //6种情况
//      //1交2
//      if (intll2 >= intll1 && intll2 < intrr1 && intrr1 <= intrr2 && intrr1 >= intll2) {
//        //左边界取ll2
//        if (inmap2.containsKey("l1")) {
//          outMap.put("l1", ll2);
//        } else {
//          outMap.put("l2", ll2);
//        }
//        //右边界取rr1
//        if (inmap1.containsKey("r1")) {
//          outMap.put("r1", rr1);
//        } else {
//          outMap.put("r2", rr1);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      }
//      // 2交1
//      else if (intll1 >= intll2 && intll1 <= intrr2 && intrr2 >= intll1 && intrr2 <= intrr1) {
//        //左取 ll1
//        if (inmap1.containsKey("ll")) {
//          outMap.put("l1", ll1);
//        } else {
//          outMap.put("l2", ll1);
//        }
//        //右取 rr2
//        if (inmap1.containsKey("r1")) {
//          outMap.put("r1", rr2);
//        } else {
//          outMap.put("r2", rr2);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      }
//
//      //1含2
//      else if (intll1 < intll2 && intrr1 > intrr2) {
//        //左取2
//        if (inmap2.containsKey("l1")) {
//          outMap.put("l1", ll2);
//        } else {
//          outMap.put("l2", ll2);
//        }
//        //右取2
//        if (inmap2.containsKey("r1")) {
//          outMap.put("r1", rr2);
//        } else {
//          outMap.put("r2", rr2);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      }
//      //2含1
//      else if (intll2 < intll1 && intrr2 > intrr1) {
//        //左取1
//        if (inmap1.containsKey("l1")) {
//          outMap.put("l1", ll1);
//        } else {
//          outMap.put("l2", ll1);
//        }
//        //右取1
//        if (inmap1.containsKey("r1")) {
//          outMap.put("r1", rr1);
//        } else {
//          outMap.put("r2", rr1);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      } else {
//        // 1外2  这种情况移除该条件
//        //2外1  这种情况移除该条件
//        list.remove(index);
//        HashMap<String,String> hashMap=new HashMap<>();
//        hashMap.put("l1","20170101");
//        hashMap.put("r1","20170101");
//        list.add(index,hashMap);
//      }
//
//
//    } else {
//      //6种情况
//      //1交2
//      if (intll2 >= intll1 && intll2 < intrr2 && intrr1 <= intrr2 && intrr1 >= intll2) {
//        //左边界取ll1
//        if (inmap1.containsKey("l1")) {
//          outMap.put("l1", ll1);
//        } else {
//          outMap.put("l2", ll1);
//        }
//        //右边界取rr2
//        if (inmap2.containsKey("r1")) {
//          outMap.put("r1", rr2);
//        } else {
//          outMap.put("r2", rr2);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//
//      }
//      // 2交1
//      else if (intll1 >= intll2 && intll1 <= intrr2 && intrr2 >= intll1 && intrr2 <= intrr1) {
//        //左取 ll2
//        if (inmap2.containsKey("ll")) {
//          outMap.put("l1", ll2);
//        } else {
//          outMap.put("l2", ll2);
//        }
//        //右取 rr1
//        if (inmap1.containsKey("r1")) {
//          outMap.put("r1", rr1);
//        } else {
//          outMap.put("r2", rr1);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      }
//
//      //1含2
//      else if (intll1 < intll2 && intrr1 > intrr2) {
//        //左取1
//        if (inmap1.containsKey("l1")) {
//          outMap.put("l1", ll1);
//        } else {
//          outMap.put("l2", ll1);
//        }
//        //右取1
//        if (inmap1.containsKey("r1")) {
//          outMap.put("r1", rr1);
//        } else {
//          outMap.put("r2", rr1);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//      }
//      //2含1
//      else if (intll2 < intll1 && intrr2 > intrr1) {
//        //左取2
//        if (inmap2.containsKey("l1")) {
//          outMap.put("l1", ll2);
//        } else {
//          outMap.put("l2", ll2);
//        }
//        //右取2
//        if (inmap2.containsKey("r1")) {
//          outMap.put("r1", rr2);
//        } else {
//          outMap.put("r2", rr2);
//        }
//        list.remove(index);
//        list.add(index, outMap);
//
//      } else {
//        // 1外2  新增
//        //新增
//        //2外1 新增
//        list.add(inmap1);
//      }
//
//    }
//    return list;
//  }

//  /**
//   * 根据ArrayList<BtDateValue>获得区间
//   * zwx450505  add
//   *
//   * @param bswc
//   */
//  public static Map<String, String> getIntervalBySWCNode(BtSWCNode bswc) {
//    Map<String, String> map = new HashMap<>();
//    //不包含左边界
//    String lOpen = "";
//    //包含左边界
//    String lClose = "";
//    //不包含右边界
//    String rOpen = "";
//    //包含右边界
//    String rClose = "";
//    String node = bswc.getOrigin().replaceAll("[\n ]+", " ");
//    List<BtDateValue> values = bswc.getValues();
//    if (node.contains("between ") && node.contains("and ")) {
//      if (values.size() != 2) {
//        log.warn("Node resolution is wrong!");
//      } else {
//        lClose = values.get(0).getDateValue();
//        rClose = values.get(1).getDateValue();
//        map.put("l2", lClose);
//        map.put("r2", rClose);
//      }
//    } else if (node.contains(" in(") || node.contains(" in (")) {
//      for (int i = 0; i < values.size(); i++) {
//        BtDateValue btDateValue = values.get(i);
//        String value = btDateValue.getDateValue();
//        map.put("l2", value);
//        map.put("r2", value);
//      }
//    } else {
//      if (values.size() > 1) {
//        log.warn("Node resolution is wrong!");
//      } else if (values.size() == 1) {
//        BtDateValue bd = values.get(0);
//        int Symbol = bd.getIntervalSymbol();
//        //0:'=';  1:'<';  2:'<=';  3:'>';  4:'>=';
//        switch (Symbol) {
//          case 0:
//            lClose = bd.getDateValue();
//            rClose = bd.getDateValue();
//            map.put("l2", lClose);
//            map.put("r2", rClose);
//            break;
//          case 1:
//            rOpen = bd.getDateValue();
//            lClose = "20000101";
//            map.put("r1", rOpen);
//            map.put("l2", lClose);
//            break;
//          case 2:
//            rClose = bd.getDateValue();
//            lOpen = "20000101";
//            map.put("r2", rClose);
//            map.put("l1", lOpen);
//            break;
//          case 3:
//            lOpen = bd.getDateValue();
//            rClose = getYesterdayDate();
//            map.put("l1", lOpen);
//            map.put("r2", rClose);
//            break;
//          case 4:
//            lClose = bd.getDateValue();
//            rClose = getYesterdayDate();
//            map.put("l2", lClose);
//            map.put("r2", rClose);
//            break;
//          default:
//
//        }
//      }
//    }
//    return map;
//  }

  public static void getIntervalBySWCNode(List<Range> leftSection, List<Range> rightSection, BtSWCNode bswc) {
    //不包含左边界
    String lOpen = "";
    //包含左边界
    String lClose = "";
    //不包含右边界
    String rOpen = "";
    //包含右边界
    String rClose = "";
    String node = bswc.getOrigin().replaceAll("[\\s]+", " ");
    List<BtDateValue> values = bswc.getValues();
    if (node.contains("between ") && node.contains("and ")) {
      if (values.size() != 2) {
        log.warn("Node resolution is wrong!");
      } else {
        lClose = values.get(0).getDateValue();
        rClose = values.get(1).getDateValue();
        leftSection.add(new Range("l2", lClose));
        rightSection.add(new Range("r2", rClose));
      }
    } else if (node.contains(" in(") || node.contains(" in (")) {
      for (int i = 0; i < values.size(); i++) {
        BtDateValue btDateValue = values.get(i);
        String value = btDateValue.getDateValue();
        leftSection.add(new Range("l2", value));
        rightSection.add(new Range("r2", value));
      }
    } else {
      if (values.size() > 1) {
        log.warn("Node resolution is wrong!");
      } else if (values.size() == 1) {
        BtDateValue bd = values.get(0);
        int Symbol = bd.getIntervalSymbol();
        Map<String, String> map = new HashMap<>();
        //0:'=';  1:'<';  2:'<=';  3:'>';  4:'>=';
        switch (Symbol) {
          case 0:
            lClose = bd.getDateValue();
            rClose = bd.getDateValue();
            leftSection.add(new Range("l2", lClose));
            rightSection.add(new Range("r2", rClose));
            break;
          case 1:
            rOpen = bd.getDateValue();
            lClose = "20000101";
            leftSection.add(new Range("l2", lClose));
            rightSection.add(new Range("r1", rOpen));
            break;
          case 2:
            rClose = bd.getDateValue();
            lOpen = "20000101";
            leftSection.add(new Range("l1", lOpen));
            rightSection.add(new Range("r2", rClose));
            break;
          case 3:
            lOpen = bd.getDateValue();
            rClose = getYesterdayDate();
            leftSection.add(new Range("l1", lOpen));
            rightSection.add(new Range("r2", rClose));
            break;
          case 4:
            lClose = bd.getDateValue();
            rClose = getYesterdayDate();
            leftSection.add(new Range("l2", lClose));
            rightSection.add(new Range("r2", rClose));
            break;
          default:
        }
      }
    }
  }

  /**
   * 获得昨天的日期20171225
   *
   * @return
   */
  public static String getYesterdayDate() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    return new SimpleDateFormat("yyyyMMdd ").format(cal.getTime());
  }


  public static List<String> parseCondition(QueryBean bean, int days) {
    List<String> list = new ArrayList<>();
    ArrayList<String> whereConditions = bean.getWhereConditions();
    if (null != whereConditions && !whereConditions.isEmpty()) {
      for (String c : whereConditions) {
        if (c.contains("pt_d")) {
          String pp = parsePartition(c, days);
          if (null != pp && !"".equals(pp)) {
            list.add(pp);
          }
          //System.out.println(c);
        }
      }
    }
    return list;
  }

  private static String parsePartition(String s, int days) {
    // List<String> list = new ArrayList<>();
    String result = "";
    boolean b = true;
    Map<String, Pattern> map = getPatternOfPt_d();
    for (Map.Entry<String, Pattern> entry : map.entrySet()) {
      if (entry.getValue().matcher(s).find()) {
        if (entry.getKey().equals("between")) {
          b = isLegalBetween(s, days);
          // result = "between and---> " + b;
        } else if (entry.getKey().equals("=")) {
          b = isLegalEquals(s);
          //result = "=---> " + b;
        } else if (entry.getKey().equals(">|>=")) {
          b = isLegalGe(s, days);
          //result = ">|>=---> " + b;
        } else if (entry.getKey().equals("substr")) {
          b = isLegalSs(s);
          //result = "substr---> " + b;
        }
        if (!b) {
          result = "Talent zone time period is greater than the maximum limit: " + days + "(" + s + ")";
        }
        break;
      }
    }
    return result;
  }

  private static Map<String, Pattern> getPatternOfPt_d() {
    HashMap<String, Pattern> map = new HashMap<>();
    String ba = ".*pt_d *between.*and.*";
    String e = ".*pt_d *=.*";
    String ge = ".*pt_d *>=|>.*";
    String ss = ".*substr\\( *pt_d.*\\) *=";
    Pattern pba = Pattern.compile(ba);
    Pattern pe = Pattern.compile(e);
    Pattern pge = Pattern.compile(ge);
    Pattern pss = Pattern.compile(ss);
    map.put("between", pba);
    map.put("=", pe);
    map.put(">|>=", pge);
    map.put("substr", pss);
    return map;
  }

  private static boolean isLegalBetween(String s, int ds) {
    String tss = null;
    String tse = null;
    int tssc = 0;
    int tsec = 0;
    String between = s.substring(s.indexOf("between") + 7, s.length()).trim();
    String[] ands = between.split("and");
    String tssSub = null;
    String tseSub = null;
    if (ands.length < 2) {
      return true;
    } else {
      tss = ands[0].trim();
      tse = ands[1].trim().split(" ")[0].trim();
      tssSub = tss.replaceAll("'", "").replaceAll("\"", "").trim();
      tseSub = tse.replaceAll("'", "").replaceAll("\"", "").trim();
    }
    if (tssSub.length() != 8 || tseSub.length() != 8) {
      return false;
    }
    try {
      long days = dayStamp(tssSub, tseSub);
      if (days + 1 > ds) {
        return false;
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private static boolean isLegalEquals(String s) {
    String[] split = s.split("=");
    String t = split[1].trim().split(" ")[0].trim();
    String tSub = t.replaceAll("'", "").replaceAll("\"", "").trim();
    if (tSub.length() != 8) {
      return false;
    }
    return true;
  }

  private static boolean isLegalGe(String s, int ds) {
    //todo distinguish '>' or '>='
    boolean isG = true;
    String t = null;

    if (s.length() > s.indexOf(">") + 1) {
      if (s.charAt(s.indexOf(">") + 1) == '=') {
        isG = false;
      }
    } else {
      return true;
    }
    if (isG) {
      t = s.split(">")[1].trim().split(" ")[0].trim();
    } else {
      t = s.split(">=")[1].trim().split(" ")[0].trim();
    }
    String tSub = t.replaceAll("'", "").replaceAll("\"", "").trim();
    if (tSub.length() != 8) {
      return false;
    }
    try {
      long days = geDays(tSub, isG);
      if (days > ds) {
        return false;
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private static boolean isLegalSs(String s) {
    //String tb = s.substring(s.indexOf("substr") + 6, s.length()).trim();
    String ta = s.split("=")[1].trim().split(" ")[0].trim();
    String strdate = ta.replaceAll("'", "").replaceAll("\"", "").trim();
    if (strdate.length() != 8) {
      return false;
    }
    return true;
  }

  //todo --start where条件的结构处理以及解析层级and/or关系~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  public static void checkPt_dForeachBT(Expression e, List<BTLimitEntity> entities) {  //map的size就是一共关联的大表数，只有为1的时候，可以不需要考虑pt_d的前缀
    if (null != e.getOrMap()) {
      for (Map.Entry<Integer, List<Expression>> entry : e.getOrMap().entrySet()) {
        List<Expression> values = entry.getValue();
        if (entities.size() == 1) {
          boolean check = false;
          for (Expression es : values) {
            if (null == es.getOrMap()) {  //判断此时的Expression是否是单一节点
              String format = es.getFormat();
              String s = format.trim().split(" ")[0].trim().split("[=<>]")[0].trim();
              String checkField = entities.get(0).checkField;
              if ((null != checkField && !"".equals(checkField)) && s.contains(checkField)) {
                check = true;
                break;
              }
            } else {  //否则此时的Expression是一个节点组
              checkPt_dForeachBT(es, entities);
            }
          }
          entities.get(0).check = check;
        } else {
          for (int i = 0; i < entities.size(); i++) {
            boolean check = false;
            for (Expression es : values) {
              if (null != es.getOrMap()) {
                String s = es.getFormat().trim().split(" ")[0].trim().split("[=<>]")[0].trim();
                String checkField = entities.get(i).checkField;
                if ((null != checkField && !"".equals(checkField)) && s.contains(checkField) && s.contains(entities.get(i).name)) {
                  check = true;
                  break;
                }
              } else {
                checkPt_dForeachBT(es, entities);
              }
            }
            entities.get(i).check = check;
          }
        }
      }
    }
  }

  public static void recursiveMap(Expression expression) {   //递归计算出每一层的所有or关系的分层结构
    List<Expression> levelList = expression.getLevelList();
    if (null != levelList && levelList.size() > 0) {
      Map<Integer, List<Expression>> orMap = expression.getOrMap();
      if (null == orMap) expression.setOrMap(new HashMap<Integer, List<Expression>>());
      orMap = expression.getOrMap();
      List<Integer> indexList = new ArrayList<>();  // 存储每一个or的下标  index
      Map<Integer, Integer> indexMap = new HashMap<>();  // 记录每一段or条件的起始index下标
      for (int i = 0; i < levelList.size() - 1; i++) {
        if (levelList.get(i).getJoiner().equals("or")) {  //joiner 是标记和下一个节点关联的连接符(and/or)   所以只需循环到倒数第二个元素即可
          indexList.add(i);
        }
      }
      if (indexList.size() == 0) {
        orMap.put(0, levelList);
      } else {
        for (int i = 0; i < indexList.size(); i++) {
          if (i == 0) {
            if (indexList.size() == 1) {
              orMap.put(0, levelList.subList(0, indexList.get(0) + 1));
              orMap.put(i + 1, levelList.subList(indexList.get(0) + 1, levelList.size()));
            } else {
              orMap.put(0, levelList.subList(0, indexList.get(0) + 1));
            }
          } else if (i == indexList.size() - 1) {
            orMap.put(i, levelList.subList(indexList.get(i - 1) + 1, indexList.get(i) + 1));
            orMap.put(i + 1, levelList.subList(indexList.get(i) + 1, levelList.size()));
          } else {
            orMap.put(i, levelList.subList(indexList.get(i - 1) + 1, indexList.get(i) + 1));
          }
        }
      }

      for (Map.Entry<Integer, List<Expression>> entry : orMap.entrySet()) {
        List<Expression> values = entry.getValue();
        if (null != values && values.size() > 0) {
          for (Expression e : values) {
            recursiveMap(e);
          }
        }
      }
    }
  }

  public static void recursiveLevel(Expression expression) {  //递归获得条件表达式的层级关系
    List<Expression> levelList = expression.getLevelList();
    if (null == levelList) expression.setLevelList(new ArrayList<Expression>());
    levelList = expression.getLevelList();
    getExpressionList(levelList, expression);

    if (levelList.size() > 0) {
      for (int i = 0; i < levelList.size(); i++) {
        if (null != levelList.get(i).getLeftExpression() && null != levelList.get(i).getRightExpression())
          recursiveLevel(levelList.get(i));
      }
    }
    // return expression;
  }

  private static List<Expression> getExpressionList(List<Expression> list, Expression e) {   //递归获取每一层的所有节点，包括单节点和组合节点
    if (null == e.getLeftExpression() || null == e.getRightExpression()) {
      list.add(cloneNewExpression(e));
    } else if (e.getRightExpression().isEntirety()
        || ((null == e.getLeftExpression().getLeftExpression() || null == e.getLeftExpression().getRightExpression())
        && (null == e.getRightExpression().getLeftExpression() || null == e.getRightExpression().getRightExpression()))) {
      list.add(e.getLeftExpression());
      list.add(e.getRightExpression());
    } else {
      list.add(e.getLeftExpression());
      getExpressionList(list, e.getRightExpression());
    }
    return list;
  }

  private static Expression cloneNewExpression(Expression e) {
    return new Expression(e.isEntirety(), e.isNonTaken(), e.isFunction(), e.getConditions(), e.getFormat(), e.getJoiner(), e.getSubJoiner(), e.getSelfAndOr(), e.getLeftExpression(), e.getRightExpression());
  }

//  public static Expression recursiveStruct(Expression expression) {   // 递归获得条件表达式的整体结构
//    String conditions = expression.getConditions();
//    conditions = removeBrackets(conditions);
//    String trim = conditions.trim();
//    if (!"".equals(trim)) {
//      expression.setFormat(trim);
//      // String joiner = null;
//      String format = expression.getFormat();
//      if (format.startsWith("!(") || format.startsWith("! (")) {
//        expression.setNonTaken(true);
//        format = format.substring(1, format.length()).trim();
//      }
//      if (format.startsWith("(")) {
//        int index = 0;
//        String back = indexOfEndRight(format);
//        if (back.startsWith("#")) {   //处理左括号多出来出现堆栈溢出的问题
//          int i = Integer.parseInt(back.substring(1, back.length()));
//          format = format.substring(i, format.length());
//          index = format.length() - 1;
//        } else {
//          index = Integer.parseInt(back);
//        }
//        int sub = index + 1; // 截取的范围
//        Expression leftExpression = getLeft(sub, format, expression);
//        Expression rightExpression = getRight(sub, format, expression);
//        leftExpression.setJoiner(expression.getSubJoiner());
//        recursiveStruct(leftExpression);
//        if (null != rightExpression) recursiveStruct(rightExpression);
//      } else {
//        boolean outLoop = false;
//        int index = 0;   // 记录or/and/between..and..条件的下标
//        boolean isBetween = false;
//        StringBuilder builder = new StringBuilder();
//        for (int i = 1; i <= format.length(); i++) {
//          if (i + 8 < format.length()) {    // ' between '
//            // String between = format.substring(i - 1, i + 8);
//            if (builder.length() > 0) builder.delete(0, builder.length());
//            builder.append(format.substring(i - 1, i + 8));
//            if (builder.toString().equals(" between ")) {
//              isBetween = true;
//            }
//          }
//          if (i + 4 < format.length()) {  // ' and '
//            if (builder.length() > 0) builder.delete(0, builder.length());
//            builder.append(format.substring(i - 1, i + 4));
//            // String and = format.substring(i - 1, i + 4);
//            if (builder.toString().equals(" and ")) {
//              if (isBetween) {
//                isBetween = false;
//              } else {
//                expression.setSubJoiner("and");
//                outLoop = true;
//                index = i;
//              }
//            }
//          }
//          if (i + 3 < format.length()) {   // ' or '
//            if (builder.length() > 0) builder.delete(0, builder.length());
//            builder.append(format.substring(i - 1, i + 3));
//            // String or = format.substring(i - 1, i + 3);
//            if (builder.toString().equals(" or ")) {
//              expression.setSubJoiner("or");
//              outLoop = true;
//              index = i;
//            }
//          } else {
//            index = i;
//          }
//          if (outLoop) break;  //跳出循环
//        }
//
//        if (index > 0 && index < format.length()) {
//          Expression leftExpression = getLeft(index, format, expression);
//          Expression rightExpression = getRight(index, format, expression);
//          leftExpression.setJoiner(expression.getSubJoiner());
//          recursiveStruct(leftExpression);
//          if (null != rightExpression) recursiveStruct(rightExpression);
//        }
//      }
//    }
//    return expression;
//  }

  public static Expression recursiveStruct(Expression expression) {   // 递归获得条件表达式的整体结构
    String conditions = expression.getConditions();
    conditions = removeBrackets(conditions);
    expression.setFormat(conditions.trim());
    String joiner = null;
    String format = expression.getFormat();
    if (format.startsWith("!(") || format.startsWith("! (")) {
      expression.setNonTaken(true);
      format = format.substring(1, format.length()).trim();
    }
    if (format.startsWith("(")) {
      int index = 0;
      String back = indexOfEndRight(format);
      if (back.startsWith("#")) {   //处理左括号多出来出现堆栈溢出的问题
        int i = Integer.parseInt(back.substring(1, back.length()));
        format = format.substring(i, format.length());
        index = format.length() - 1;
      } else {
        index = Integer.parseInt(back);
      }
      int sub = index + 1; // 截取的范围
      Expression leftExpression = getLeft(sub, format, expression, false);
      Expression rightExpression = getRight(sub, format, expression);
      leftExpression.setJoiner(expression.getSubJoiner());
      recursiveStruct(leftExpression);
      if (null != rightExpression) recursiveStruct(rightExpression);
    } else {
      if (!expression.isFunction()) {
        int index = 0;
        Map<Integer, Boolean> map = getIndex(format, expression);
        Iterator<Integer> it = map.keySet().iterator();
        if (it.hasNext()) {
          index = it.next();
        }
        if (index > 0 && index < format.length()) {
          Boolean isFunction = map.get(index);
          Expression leftExpression = getLeft(index, format, expression, isFunction);
          Expression rightExpression = getRight(index, format, expression);
          leftExpression.setJoiner(expression.getSubJoiner());
          recursiveStruct(leftExpression);
          if (null != rightExpression) recursiveStruct(rightExpression);
        }
      }
    }
    return expression;
  }

  private static Map<Integer, Boolean> getIndex(String format, Expression expression) {
    Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    boolean isFunction = false;
    boolean outLoop = false;
    int index = 0;   // 记录or/and/between..and..条件的下标
    boolean isBetween = false;
    StringBuilder builder = new StringBuilder();
    for (int i = 1; i <= format.length(); i++) {
      if (i + 8 < format.length()) {    // ' between '
        // String between = format.substring(i - 1, i + 8);
        if (builder.length() > 0) builder.delete(0, builder.length());
        builder.append(format.substring(i - 1, i + 8));
        if (builder.toString().equals(" between ")) {
          isBetween = true;
        }
      }
      if (i + 4 < format.length()) {  // ' and '
        if (builder.length() > 0) builder.delete(0, builder.length());
        builder.append(format.substring(i - 1, i + 4));
        // String and = format.substring(i - 1, i + 4);
        if (builder.toString().equals(" and ")) {
          if (isBetween) {
            isBetween = false;
          } else {
            expression.setSubJoiner("and");
            outLoop = true;
          }
        }
      }
      if (i + 3 < format.length()) {   // ' or '
        if (builder.length() > 0) builder.delete(0, builder.length());
        builder.append(format.substring(i - 1, i + 3));
        // String or = format.substring(i - 1, i + 3);
        if (builder.toString().equals(" or ")) {
          expression.setSubJoiner("or");
          outLoop = true;
        }
      }
      index = i;
      // 判断是否为函数
      if (!outLoop) {
        if (format.charAt(i - 1) == '(') {
          isFunction = true;
          break;
        }
      } else {
        break;  //跳出循环
      }
    }
    if (isFunction) {
      String after = format.substring((index - 1), format.length());
      String back = indexOfEndRight(after);
      if (back.startsWith("#")) {   //左侧的函数括号比右侧多, 语法不对, 说明右边末尾也没有找到闭合的右括号
        index = format.length();
      } else {
        int i = Integer.parseInt(back) + index;
        String sub = format.substring(i, format.length());
        int andIndex = -1;
        int orIndex = -1;
        if (sub.contains(" or ") || sub.contains(" and ")) {
          orIndex = sub.indexOf(" or ");
          andIndex = sub.indexOf(" and ");
        }
        if (andIndex > orIndex) {
          if (orIndex >= 0) {
            index = i + orIndex;
          } else {
            index = i + andIndex;
          }
        } else if (andIndex < orIndex) {
          if (andIndex >= 0) {
            index = i + andIndex;
          } else {
            index = i + orIndex;
          }
        } else {
          index = format.length();
        }
      }
    }
    map.put(index, isFunction);
    return map;
  }

  private static Expression getLeft(int sub, String format, Expression expression, boolean isFunction)
  {
    String leftSub = format.substring(0, sub);  //左边的条件表达式--> leftExpression
    Expression leftExpression = expression.getLeftExpression();   //左侧条件
    if (null == leftExpression) expression.setLeftExpression(new Expression());
    leftExpression = expression.getLeftExpression();
    leftExpression.setConditions(leftSub);
    leftExpression.setEntirety(true);
    leftExpression.setFunction(isFunction);
    return leftExpression;
  }

  private static Expression getRight(int sub, String format, Expression expression) {
    Expression rightExpression = expression.getRightExpression();   //右侧条件
    String rightSub = "";
    if (sub < format.length()) rightSub = format.substring(sub, format.length()).trim(); // 右边的条件表达式--> rightExpression
    if (rightSub.length() > 0) {  //确定是否有右侧，并且确定左侧和右侧的joiner是什么(and, or)，理论上右侧有条件的话一定是以or/and开头，否则没有右侧表达式
      String trim = "";
      if (rightSub.startsWith("or ")) {
        expression.setSubJoiner("or");
        trim = rightSub.substring(3, rightSub.length()).trim();
        if (trim.length() == 0) trim = " ";  //防止or/and后是空字符串
      } else if (rightSub.startsWith("and ")) {
        expression.setSubJoiner("and");
        trim = rightSub.substring(4, rightSub.length()).trim();
        if (trim.length() == 0) trim = " ";
      }
      if (trim.length() > 0) {
        if (null == rightExpression) expression.setRightExpression(new Expression());
        rightExpression = expression.getRightExpression();
        rightExpression.setConditions(trim);
        if (trim.startsWith("(") && trim.endsWith(")")) rightExpression.setEntirety(true);  //以()判断右侧是不是一个整体
      }
    }
    return rightExpression;
  }

  private static String indexOfEndRight(String s) {
    Map<Integer, String> map = new HashMap<Integer, String>();
    String back = "";
    int leftCount = 0; // 统计左括号的数量
    // int rightCount = 0; // 统计右括号的数量
    int endRight = 0;  //记录最后一个右括号的下标
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '(') {  //
        leftCount += 1;
        map.put(leftCount, "(");
      } else if (s.charAt(i) == ')') {
        map.remove(leftCount);
        leftCount -= 1;
      }
      if (i != 0 && map.size() == 0) { // 这边通过map的size等于0或者leftCount等于0来判断 起始的(左括号有没有被最后一个右括号)消除掉   所以s的起始字符必须是(
        endRight = i;  // 如果一直走不进 map.size() == 0  则说明少一个或多个右括号)  要么在前面的语法解析过不去，要么这里再处理
        break;
      }
    }
    if (endRight == 0) {
      endRight = map.size();
      back = "#" + String.valueOf(endRight);
    } else {
      back = String.valueOf(endRight);
    }
    return back;
  }

  private static String removeBrackets(String s) {  //去除左右括号
    if (s.startsWith("(") && s.endsWith(")")) {
      s = s.substring(1, s.length() - 1).trim();
      s = removeBrackets(s);
    }
    return s;
  }

  //todo --end   where条件的结构处理以及解析层级and/or关系~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  private static long geDays(String s, boolean b) throws ParseException {
    long days = dayStamp(s, null);
    // todo Here the end date is the day before the current date!
    if (b) {
      days = days - 1;
    }
    return days;
  }

  private static long dayStamp(String st, String ed) throws ParseException {
    long days = 0;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date std = format.parse(st);
    Date edd = null;
    if (null != ed) {
      edd = format.parse(ed);
    } else {
      ed = format.format(new Date());
      edd = format.parse(ed);
    }
    if (null != edd) {
      days = (edd.getTime() - std.getTime()) / (1000 * 60 * 60 * 24);
    }
    return days;
  }

  private static long dayStamp(String left, String right, int isCL, int isCR) throws ParseException {
    long days = 0;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date std = format.parse(left);
    Date etd = format.parse(right);
    if (isCL == 1 && isCR == 1) {
      days = (etd.getTime() - std.getTime()) / (1000 * 60 * 60 * 24) - 1;
    } else if ((isCL == 2 && isCR == 1) || (isCL == 1 && isCR == 2)) {
      days = (etd.getTime() - std.getTime()) / (1000 * 60 * 60 * 24);
    } else if (isCL == 2 && isCR == 2) {
      days = (etd.getTime() - std.getTime()) / (1000 * 60 * 60 * 24) + 1;
    }
    if (days < 0) {
      days = 0;
    }
    return days;
  }
}
