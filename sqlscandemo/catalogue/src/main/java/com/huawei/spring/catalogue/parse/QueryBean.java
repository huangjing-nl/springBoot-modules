package com.huawei.spring.catalogue.parse;

import java.util.ArrayList;
import java.util.List;

public class QueryBean {
  /**
   * 层
   */
  private int layer;
  /**
   * 原始的sql语句
   */
  private String sqlContent;
  /**
   * 切块后替代块之后组成的sql
   */
  private String sqlNewContent;

  /**
   * 表名称
   */
  private String fromTableName;
  /**
   * 别名
   */
  private String fromTableAliasName;
  /**
   * 列名称
   */
  private ArrayList<String> columns;
  /**
   * 连接关键字
   */
  private ArrayList<String> keyWords;
  /**
   * 连接的表名字/或者查询块
   */
  private  ArrayList<String> joinTableName;
  /**
   * 连接条件
   */
  private ArrayList<ConditionBean> onConditions;
  /**
   * 连接条件(字符串形式)
   */
  private String  onConditionsStr;
  /**
   * where 条件（List）
   * List<String>  string:
   * 多个条件时按顺序拼接 其中最后一个不带拼接符号   如:pt_d in ('20171201','20171203')
   * 其他的带有拼接符（and|or）如：pt_d=20171207 and
   */
  private ArrayList<String> whereConditions;

  private List<BtSWCNode> btSWCNodes;
  /**
   * where条件（String）
   */
  private String whereConditionsStr;

  /**
   * 子查询
   */
  private ArrayList<QueryBean> queryBeans;
  /**
   * 块信息
   */
  private ArrayList<Block> blocks;
  /**
   * 关联关系
   * from表名|关键字|关联的表名|关联条件1 关联条件2
   */
  private ArrayList<String> relations;

  /**
   * 是否有子查询
   */
  private boolean hasSubQuery;
  /**
   * group by
   *
   * @return
   */
  private String groupBy;
  private String orderBy;

  public String getSqlContent() {
    return sqlContent;
  }

  public void setSqlContent(String sqlContent) {
    this.sqlContent = sqlContent;
  }

  public String getSqlNewContent() {
    return sqlNewContent;
  }

  public void setSqlNewContent(String sqlNewContent) {
    this.sqlNewContent = sqlNewContent;
  }

  public String getFromTableName() {
    return fromTableName;
  }

  public void setFromTableName(String fromTableName) {
    this.fromTableName = fromTableName;
  }

  public String getFromTableAliasName() {
    return fromTableAliasName;
  }

  public void setFromTableAliasName(String fromTableAliasName) {
    this.fromTableAliasName = fromTableAliasName;
  }

  public ArrayList<String> getColumns() {
    return columns;
  }

  public void setColumns(ArrayList<String> columns) {
    this.columns = columns;
  }

  public int getLayer() {
    return layer;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public ArrayList<String> getKeyWords() {
    return keyWords;
  }

  public void setKeyWords(ArrayList<String> keyWords) {
    this.keyWords = keyWords;
  }

  public ArrayList<String> getJoinTableName() {
    return joinTableName;
  }

  public void setJoinTableName(ArrayList<String> joinTableName) {
    this.joinTableName = joinTableName;
  }

  public ArrayList<ConditionBean> getOnConditions() {
    return onConditions;
  }

  public void setOnConditions(ArrayList<ConditionBean> onConditions) {
    this.onConditions = onConditions;
  }

  public String getOnConditionsStr() {
    return onConditionsStr;
  }

  public void setOnConditionsStr(String onConditionsStr) {
    this.onConditionsStr = onConditionsStr;
  }

  public ArrayList<String> getWhereConditions() {
    return whereConditions;
  }

  public void setWhereConditions(ArrayList<String> whereConditions) {
    this.whereConditions = whereConditions;
  }

  public List<BtSWCNode> getBtSWCNodes() {
    return btSWCNodes;
  }

  public void setBtSWCNodes(List<BtSWCNode> btSWCNodes) {
    this.btSWCNodes = btSWCNodes;
  }

  public String getWhereConditionsStr() {
    return whereConditionsStr;
  }

  public void setWhereConditionsStr(String whereConditionsStr) {
    this.whereConditionsStr = whereConditionsStr;
  }

  public ArrayList<QueryBean> getQueryBeans() {
    return queryBeans;
  }

  public void setQueryBeans(ArrayList<QueryBean> queryBeans) {
    this.queryBeans = queryBeans;
  }

  public boolean isHasSubQuery() {
    return hasSubQuery;
  }

  public void setHasSubQuery(boolean hasSubQuery) {
    this.hasSubQuery = hasSubQuery;
  }

  public ArrayList<Block> getBlocks() {
    return blocks;
  }

  public void setBlocks(ArrayList<Block> blocks) {
    this.blocks = blocks;
  }

  public ArrayList<String> getRelations() {
    return relations;
  }

  public void setRelations(ArrayList<String> relations) {
    this.relations = relations;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }


  @Override
  public String toString() {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    sb.append("Query[\n\ttablename:" + this.fromTableName + " " + this.fromTableAliasName + " ,\n");
    sb.append("\tColumns:" + this.columns + " ,\n");
    if (relations != null && relations.size() > 0) {
      for (int i = 0; i < relations.size(); i++) {
        sb.append("\t");
        sb.append("relation" + (i + 1) + ":" + relations.get(i));
        sb.append(",\n");
      }
    }
    sb.append("\tWhere: " + this.whereConditions + "\n");
    sb.append("]");

    return sb.toString();
  }


}
