package com.huawei.spring.catalogue.parse;

public class ConditionBean {


  private String oriCondition;
  /**
   * 条件的父ID
   */
  private String parentId;
  /**
   * 条件的id
   */
  private String id;
  /**
   * 字段名称
   */
  private String colName;
  /**
   * 关系运算符（=，<>,>,<,<=,>=,in）
   */
  private String relationCharOne;
  /**
   * 字段值
   */
  private String colValueOne;
  /**
   * between and(当条件为between and时有效)
   */
  private String relationCharTwo;
  /**
   * between and(当条件为between and时有效)
   */
  private String colValueTwo;
  /**
   * 连接字符串
   * and | or
   * 第一个条件为连接字符串为空
   */
  private String connStr;

  /**
   * @return
   */
  private boolean hasChild;


  public String getOriCondition() {
    return oriCondition;
  }

  public void setOriCondition(String oriCondition) {
    this.oriCondition = oriCondition;
  }

  public boolean isHasChild() {
    return hasChild;
  }

  public void setHasChild(boolean hasChild) {
    this.hasChild = hasChild;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getColName() {
    return colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  public String getRelationCharOne() {
    return relationCharOne;
  }

  public void setRelationCharOne(String relationCharOne) {
    this.relationCharOne = relationCharOne;
  }

  public String getColValueOne() {
    return colValueOne;
  }

  public void setColValueOne(String colValueOne) {
    this.colValueOne = colValueOne;
  }

  public String getRelationCharTwo() {
    return relationCharTwo;
  }

  public void setRelationCharTwo(String relationCharTwo) {
    this.relationCharTwo = relationCharTwo;
  }

  public String getColValueTwo() {
    return colValueTwo;
  }

  public void setColValueTwo(String colValueTwo) {
    this.colValueTwo = colValueTwo;
  }

  public String getConnStr() {
    return connStr;
  }

  public void setConnStr(String connStr) {
    this.connStr = connStr;
  }


}
