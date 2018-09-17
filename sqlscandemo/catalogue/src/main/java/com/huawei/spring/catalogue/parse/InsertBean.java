package com.huawei.spring.catalogue.parse;

public class InsertBean {

  /**
   * 原始的sql
   */
  private String oriSqlContent;
  /**
   * 表名称
   */
  private String tabName;
  /**
   * 1--insert into
   * 2--insert overwrite
   */

  private String operType;
  /**
   * queryBean
   */
  private QueryBean queryBean;
  /**
   * 插入的分区
   */
  private String tabPartCols;

  public InsertBean() {
  }

  public String getOriSqlContent() {
    return oriSqlContent;
  }

  public void setOriSqlContent(String oriSqlContent) {
    this.oriSqlContent = oriSqlContent;
  }

  public String getTabName() {
    return tabName;
  }

  public void setTabName(String tabName) {
    this.tabName = tabName;
  }

  public String getOperType() {
    return operType;
  }

  public void setOperType(String operType) {
    this.operType = operType;
  }

  public QueryBean getQueryBean() {
    return queryBean;
  }

  public void setQueryBean(QueryBean queryBean) {
    this.queryBean = queryBean;
  }

  public String getTabPartCols() {
    return tabPartCols;
  }

  public void setTabPartCols(String tabPartCols) {
    this.tabPartCols = tabPartCols;
  }

  @Override
  public String toString() {
    return "InsertBean{" +
        "oriSqlContent='" + oriSqlContent + '\'' +
        ", tabName='" + tabName + '\'' +
        ", operType='" + operType + '\'' +
        ", queryBean=" + queryBean +
        ", tabPartCols='" + tabPartCols + '\'' +
        '}';
  }
}
