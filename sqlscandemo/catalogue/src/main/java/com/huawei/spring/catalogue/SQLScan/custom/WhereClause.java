package com.huawei.spring.catalogue.SQLScan.custom;

public class WhereClause {

  private String origin;

  private String format;

  private Expression expression;

  public WhereClause() {
  }

  public WhereClause(String origin, String format) {
    this.origin = origin;
    this.format = format;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public String toString() {
    return "WhereClause{" +
        "origin='" + origin + '\'' +
        ", format='" + format + '\'' +
        ", expression=" + expression +
        '}';
  }
}
