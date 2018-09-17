package com.huawei.spring.catalogue.parse;


import java.util.ArrayList;

public class ParseQuery {

  public static ArrayList<String> kwList;
  public static StringBuffer kwords;
  public static String kwords1;
  static {
    kwords = new StringBuffer();
    kwords.append(" left out join ");
    kwords.append("\n");
    kwords.append(" join ");
    kwords.append("\n");
    kwords.append(" left join ");
    kwords.append("\n");
    kwords.append(" right join ");
    kwords.append("\n");
    kwords.append(" right outer join ");
    kwords.append("\n");
   // kwords.append(" union all ");
    kwords.append("\n");
    kwords.append(" full outer join ");
    kwords.append("\n");
    kwords.append(" left semi join ");
    kwords.append("\n");
    kwords.append(" inner join ");
    kwords1=kwords.toString().replaceAll("\n","");
    kwList = new ArrayList<String>();
    kwList.add("  left out join | on ");
    kwList.add(" left join | on ");
    kwList.add(" join | on");
    kwList.add(" right join | on ");
    kwList.add(" right outer join | on ");
    kwList.add(" union all ");
    kwList.add(" full outer join | on ");
    kwList.add(" left semi join | on ");
    kwList.add(" inner join | on ");
  }

  /**
   * 提取括号中内容，忽略括号的中括号
   *
   * @param msg
   * @return
   */
  public QueryBean cutBlocksAndParse(String msg) {
    QueryBean queryBean = new QueryBean();
    ArrayList<Block> blocks = new ArrayList<Block>();
    queryBean.setSqlContent(msg);
    // 去除注释部分
    msg = ParseHiveSQL.removeComment(msg);
    String sqlNewContent = msg;
    /**
     * 按照（）切块
     */
    int start = 0;
    int startFlag = 0;
    int endFlag = 0;
    for (int i = 0; i < msg.length(); i++) {
      if (msg.charAt(i) == '(') {
        startFlag++;
        if (startFlag == endFlag + 1) {
          start = i;
        }
      } else if (msg.charAt(i) == ')') {
        endFlag++;
        if (endFlag == startFlag) {
          if (isSubquery(msg.substring(start + 1, i))) {
            Block block = new Block();
            String content = msg.substring(start + 1, i);
            String content1 = msg.substring(start, i + 1);
            sqlNewContent = sqlNewContent.replace(content1,
                " ${bl-ock} ");
            block.setBlockContent(content);
            block.setBlockLength(content.length());
            block.setBlockStartNum(start + 1);
            block.setBloockEndNum(i);
            blocks.add(block);

          }

        }
      }

    }
    // 按照union all 切块
    sqlNewContent = ParseHiveSQL.removeMulSpace(sqlNewContent.toLowerCase());
    if (sqlNewContent.contains(
        "union all ")) {
      String[] cc = sqlNewContent.split("union all ");
      for (int i = 0; i < cc.length; i++) {
        if (isSubquery(cc[i])) {
          Block block = new Block();
          block.setBlockContent(cc[i]);
          blocks.add(block);
          sqlNewContent.replace(cc[i], "$unionall{bl-ock} ");
        }
      }
    }
    queryBean.setSqlNewContent(sqlNewContent);
    if (blocks.size() > 0) {
      queryBean.setHasSubQuery(true);
    }
    queryBean = parseSingleQuery(queryBean);
    queryBean.setBlocks(blocks);
    return queryBean;
  }



//  /**
//   * 提取括号中内容，忽略括号的中括号
//   *
//   * @param msg
//   * @return
//   */
//  public QueryBean cutOneLayerBlocks(String msg) {
//    QueryBean queryBean = new QueryBean();
//    ArrayList<Block> blocks = new ArrayList<Block>();
//    queryBean.setSqlContent(msg);
//    // 去除注释部分
//    msg = ParseHiveSQL.removeComment(msg);
//    String sqlNewContent = msg;
//    /**
//     * 按照（）切块
//     */
//    int start = 0;
//    int startFlag = 0;
//    int endFlag = 0;
//    for (int i = 0; i < msg.length(); i++) {
//      if (msg.charAt(i) == '(') {
//        startFlag++;
//        if (startFlag == endFlag + 1) {
//          start = i;
//        }
//      } else if (msg.charAt(i) == ')') {
//        endFlag++;
//        if (endFlag == startFlag) {
//          if (isSubquery(msg.substring(start + 1, i))) {
//            Block block = new Block();
//            String content = msg.substring(start + 1, i);
//            String content1 = msg.substring(start, i + 1);
//            sqlNewContent = sqlNewContent.replace(content1,
//                    " ${bl-ock} ");
//            block.setBlockContent(content);
//            block.setBlockLength(content.length());
//            block.setBlockStartNum(start + 1);
//            block.setBloockEndNum(i);
//            blocks.add(block);
//
//          }
//
//        }
//      }
//
//    }
//    // 按照union all 切块
//    sqlNewContent=ParseHiveSQL.removeMulSpace(sqlNewContent.toLowerCase());
//    if (sqlNewContent.contains(
//            "union all ")) {
//      String[] cc = sqlNewContent.split("union all ");
//      for (int i = 0; i < cc.length; i++) {
//        if (isSubquery(cc[i])) {
//          Block block = new Block();
//          block.setBlockContent(cc[i]);
//          blocks.add(block);
//          sqlNewContent.replace(cc[i],"$unionall{bl-ock} ");
//        }
//      }
//    }
//    queryBean.setSqlNewContent(sqlNewContent);
//    if (blocks.size() > 0) {
//      queryBean.setHasSubQuery(true);
//    }
//    queryBean.setBlocks(blocks);
//    return queryBean;
//  }

  /**
   * 判断是否是有效的子查询块
   *
   * @param str
   * @return
   */
  public static boolean isSubquery(String str) {

    str = str.toLowerCase();
    if (str.contains("select") && str.contains("from")) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * 是否包含连接关键字
   * @param contents
   * @param index
   * @return
   */
  public String[] isContainKeyWords(String[] contents,int index) {
    String result="";
    String[] resultArrys=new String[2];
    if (contents.length > index + 2) {
      String ss1 = " "+contents[index]+" ";
      String ss2 = " "+contents[index + 1]+" ";
      String ss3 = " "+contents[index + 2]+" ";
      if (kwords1.contains(ss1)) {
        result+=ss1+" ";
        if (kwords1.contains(ss2)){
          result+=ss2+" ";
          index++;
          if (kwords1.contains(ss3)){
            index++;
            result+=ss3+" ";
          }
        }
        result=ParseHiveSQL.removeMulSpace(result);
      }
    }
    resultArrys[0]=result;
    resultArrys[1]=index+"";
    return resultArrys;
  }
  /**
   * 不带有块的查询 注意解析union all语句
   *
   * @param queryBean
   */
  public QueryBean parseSingleQuery(QueryBean queryBean) {
    //对content再次此切块

    String content = queryBean.getSqlNewContent();
    content=ParseHiveSQL.removeMulSpace(content);
    //对queryBean再次此切块，为了解析带有关键字的关联语句
    //QueryBean queryBean1=cutOneLayerBlocks(content);
    //content=queryBean1.getSqlNewContent();
    String contentLower = content.toLowerCase();

    int a = contentLower.contains("select ") ? contentLower
        .indexOf("select ") : contentLower.indexOf("select \n");
    int b = contentLower.indexOf("from ") > 6 ? contentLower
        .indexOf("from ") : contentLower.indexOf("from \n");
    String columnStr = content.substring(a + 6, b);
    //需要考虑关联关系 取where 关联关系之后的where
    //解析关键字（left outer join /right outer join等）以及where条件、group by 、order by
    ArrayList<String> keyWordsList=new ArrayList<>();
    ArrayList<String> joinTableNames=new ArrayList<>();
    StringBuffer onConditions=new StringBuffer();
    int start = 0;
    int startFlag = 0;
    int endFlag = 0;
    //是否是on后面的
    boolean isOnAfter=false;
    //最后一个on之后的where的序号
    int count=0;
    String[] contents=content.split(" ");
    if (contents!=null&&contents.length>0){

      for (int i = 0; i < contents.length; i++) {

        String[] tempRes=isContainKeyWords(contents,i);
        if (!"".equals(tempRes[0])) {
          startFlag++;
          //获得关键字join
          if (startFlag == endFlag + 1) {
            isOnAfter=false;
            keyWordsList.add(tempRes[0]);
            if (tempRes[1]!=null&&!"".equals(tempRes[1])){
              i=Integer.parseInt(tempRes[1]);
            }
            start = i;

          }
        } else if ("on".equals(contents[i])) {
          endFlag++;
          if (endFlag == startFlag) {
            isOnAfter=true;
            //获得join 和on之间的表名称
            StringBuffer sb=new StringBuffer();
            for (int j = start+1; j <i ; j++) {
              sb.append(contents[j]+" ");
            }
            joinTableNames.add(sb.toString());
          }
        }
        if (isOnAfter){
            if (!("where".equals(contents[i])||"group".equals(contents[i])||"order".equals(contents[i]))){
              onConditions.append(contents[i]+" ");
              count=i;
            }else{
              isOnAfter=false;
            }
        }
      }
    }

    queryBean.setKeyWords(keyWordsList);
    queryBean.setJoinTableName(joinTableNames);
    queryBean.setOnConditionsStr(onConditions.toString());
    //截取where条件之后的
    int whereAfter=0;
    for (int i = 0; i < count; i++) {
      whereAfter+=contents[i].length()+1;
    }

    int c = contentLower.lastIndexOf("where ") > whereAfter ? contentLower
        .lastIndexOf("where ") :-1;
    int d = contentLower.lastIndexOf("group by ") > whereAfter ? contentLower
        .lastIndexOf("group by") : -1;
    int e = contentLower.lastIndexOf("order by ") > whereAfter ? contentLower
        .lastIndexOf("order by") : -1;
    String tableNameStr = "";
    String whereStr = "";
    String groupByStr = "";
    String orderbyStr = "";

    if (c > 0) {
      //有where 条件
      tableNameStr = content.substring(b + 4, c);
      if (d > 0 && e > 0) {
        if (d > e) {
          orderbyStr = content.substring(e + 8, d);
          groupByStr = content.substring(d + 8, content.length());
          //防止此情况的出现select video_name, video_first_class, video_second_class, release_year, video_id, content_encode, produce_area, language_cd, score, pt_d, row_number() over (partition by video_id order by pt_d desc) as num from biads.ads_movie_video_info_dm where pt_d>='20170801' and pt_d<='20170831'
          if (c < e) {
            whereStr = content.substring(c + 5, e);
          }

        } else {
          groupByStr = content.substring(d + 8, e);
          orderbyStr = content.substring(e + 8, content.length());
          if (c < d) {
            whereStr = content.substring(c + 5, d);
          }
        }
      } else {
        if (e > 0) {
          orderbyStr = content.substring(e + 8, content.length());
          //有order by
          if (c < e) {
            whereStr = content.substring(c + 5, e);
          } else {
            whereStr = content.substring(c + 5, content.length());
          }
        } else if (d > 0) {
          //有 group by
          groupByStr = content.substring(d + 8, content.length());
          if (c < d) {
            whereStr = content.substring(c + 5, d);
          }

        } else {
          //都没有
          whereStr = content.substring(c + 5, content.length());
        }
      }
    } else {
      //没where

      if (d > 0 && e > 0 && d > (b +4) && e > (b + 4)) {
        if (d > e) {
          orderbyStr = content.substring(e + 8, d);
          groupByStr = content.substring(d + 8, content.length());
          tableNameStr = content.substring(b + 4, e);
        } else {
          groupByStr = content.substring(d + 8, e);
          orderbyStr = content.substring(e + 8, content.length());
          tableNameStr = content.substring(b + 4, d);
        }
      } else {
        if (e > 0 && e > (b + 4)) {
          //有order by
          orderbyStr = content.substring(e + 8, content.length());
          tableNameStr = content.substring(b + 4, e);
        } else if (d > 0 && d > (b + 4)) {
          //有 group by
          groupByStr = content.substring(d + 8, content.length());
          tableNameStr = content.substring(b + 4, d);
        } else {
          //都没有
          tableNameStr = content.substring(b + 4, content.length());
        }
      }
    }
    queryBean.setGroupBy(groupByStr);
    queryBean.setOrderBy(orderbyStr);

    // 截取字段
    queryBean.setColumns(getColumsFromString(columnStr));
    // 获得表名称以及别名
    getTableNameFromString(tableNameStr, queryBean);
    // queryBean.setFromTableName(tableNameStr);
    // 获得where条件
    queryBean.setWhereConditionsStr(whereStr);
    //
    // getWhereConditionsFromStr(whereStr, queryBean);
    return queryBean;

  }

  /**
   * 根据‘，’分割解析字段的名称；排除括号中的，
   *
   * @param str
   */
  public ArrayList<String> getColumsFromString(String str) {

    ArrayList<String> list = new ArrayList<String>();
    str = ParseHiveSQL.removeMulSpace(str);

    String strtemp = str;
    int start = 0;
    int startFlag = 0;
    int endFlag = 0;
    for (int i = 0; i < str.length(); i++) {

      if (str.charAt(i) == '(') {
        startFlag++;
        if (startFlag == endFlag + 1) {
          start = i;
        }
      } else if (str.charAt(i) == ')') {

        endFlag++;
        if (endFlag == startFlag) {
          String content = str.substring(start + 1, i);
          strtemp = strtemp.replace(content,
              content.replaceAll(",", "huAwei@#123"));
        }
      }
    }
    String[] colums = str.split(",");
    if (colums.length > 0) {
      for (int i = 0; i < colums.length; i++) {
        list.add(colums[i].replaceAll(",", "huAwei@#123"));
        // System.out.println(colums[i].replaceAll(",", "huAwei@#123"));
      }
    }
    return list;

  }

  /**
   * 解析表名和别名
   *
   * @param str
   * @param queryBean
   * @return
   */
  public QueryBean getTableNameFromString(String str, QueryBean queryBean) {
    // 根据关键字切块后list
    ArrayList<String> blocks = new ArrayList<String>();
    // 关键字列表
    ArrayList<String> kws = new ArrayList<String>();
    ArrayList<String> relations = new ArrayList<String>();
    StringBuffer sb = new StringBuffer();
    str = ParseHiveSQL.removeMulSpace(str);
    String aa[] = str.split(" ");
    StringBuffer keywords;
    int pos = 0;
    // 按照关键字分块
    for (int i = 0; i < aa.length; i++) {
      aa[i] = " " + aa[i] + " ";
      if (kwords.toString().contains(aa[i].toLowerCase())) {
        pos = i;
        blocks.add(sb.toString());
        sb = new StringBuffer();
        keywords = new StringBuffer();
        keywords.append(aa[i]);
        if (i + 1 < aa.length
            && kwords.toString().contains(
            aa[i + 1].toLowerCase())) {
          keywords.append(" ").append(aa[i + 1]).append(" ");
          i = i + 1;
          if (i + 1 < aa.length
              && kwords.toString().contains(
              aa[i + 1].toLowerCase())) {
            keywords.append(" ").append(aa[i + 1]).append(" ");
            i = i + 1;
          }
        }
        kws.add(keywords.toString());
        sb.append(keywords).append("%");

      } else {
        sb.append(aa[i]);
      }

    }
    if (pos < aa.length) {
      StringBuilder sb1 = new StringBuilder();
      for (int i = pos; i < aa.length; i++) {

        sb1.append(" ").append(aa[i]).append(" ");
      }
      blocks.add(sb1.toString());
    }
    queryBean.setKeyWords(kws);
    // 解析关联块中的信息
    String fromTable = "";
    for (int i = 0; i < blocks.size(); i++) {
      String tempString = ParseHiveSQL.removeMulSpace(blocks.get(i));
      String[] cc = tempString.split(" ");
      StringBuilder sbBuilder = new StringBuilder();
      if (i == 0) {
        if (cc.length == 2) {
          queryBean.setFromTableAliasName(cc[1]);
        }
        queryBean.setFromTableName(cc[0]);
        fromTable = cc[0];
      } else if (i == (blocks.size() - 1)) {

        tempString = tempString.replace(
            ParseHiveSQL.removeMulSpace(kws.get(i - 1)),
            ParseHiveSQL.removeMulSpace(kws.get(i - 1)) + "%");
        tempString = tempString.replace(" on ", " %on% ");
        sbBuilder.append(fromTable).append("%").append(tempString);
        relations.add(sbBuilder.toString());

      } else {
        tempString = tempString.replace(" on ", " %on% ");
        sbBuilder.append(fromTable + "%" + tempString);
        relations.add(sbBuilder.toString());
        if (tempString.indexOf("%") > 0
            && tempString.indexOf(" %on% ") > 0) {
          fromTable = tempString.substring(
              tempString.indexOf("%") + 1,
              tempString.indexOf(" %on% "));
        } else {
          fromTable = "";
        }

      }

    }
    queryBean.setRelations(relations);
    return queryBean;
  }



  /**
   * 以 and和or作为解析条件的依据 只解析第一层
   *
   * @param str
   * @param queryBean
   * @return
   */
  public QueryBean getWhereConditionsFromStr(String str, QueryBean queryBean) {
    str = ParseHiveSQL.removeMulSpace(str);
    str = str.toLowerCase();
    // between and
    // in / not in
    ArrayList<String> cutList = new ArrayList<String>();
    // 字符串（以" "分割的字符串）
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
        if (!"".equals(builder.toString())) {
          cutList.add(builder.toString());
          builder = new StringBuilder();
        }
      } else {
        builder.append(node).append(" ");
      }
    }
    if (!"".equals(builder.toString())) {
      cutList.add(builder.toString());
    }
    if (!cutList.isEmpty()) {
      queryBean.setWhereConditions(cutList);
    }
    return queryBean;
  }

  /**
   * 迭代解析QueryBean
   */
  public QueryBean itratorQuery(String msg) {
    QueryBean queryBean = cutBlocksAndParse(msg);
    ArrayList<QueryBean> queryBeans = new ArrayList<QueryBean>();
    if (queryBean.isHasSubQuery()) {
      // cutBlocksAndParse(queryBean.getBlocks())
      ArrayList<Block> blocks = queryBean.getBlocks();
      for (int i = 0; i < blocks.size(); i++) {
        queryBeans.add(itratorQuery(blocks.get(i).getBlockContent()));
      }
    }
    queryBean.setQueryBeans(queryBeans);
    return queryBean;
  }

}
