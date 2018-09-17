package com.huawei.spring.catalogue.parse;

public class Column {

  /** 列名称 */
  private String colName;
  /** 列类型 */
  private String colType;
  /** 类型值 */
  private String colTypeVal;
  /** 列注释 */
  private String colComment;
  /** 列别名 */
  private String colAlias;

  private String column;
  public String getColName() {
    return colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  public String getColType() {
    return colType;
  }

  public void setColType(String colType) {
    this.colType = colType;
  }

  public String getColTypeVal() {
    return colTypeVal;
  }

  public void setColTypeVal(String colTypeVal) {
    this.colTypeVal = colTypeVal;
  }

  public String getColComment() {
    return colComment;
  }

  public void setColComment(String colComment) {
    this.colComment = colComment;
  }

  public String getColAlias() {
    return colAlias;
  }

  public void setColAlias(String colAlias) {
    this.colAlias = colAlias;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }
}
