package com.huawei.spring.catalogue.SQLScan.custom;

import java.util.List;
import java.util.Map;

public class Expression { // 左边一直取第一个

  // private int a = 10;
  private boolean isEntirety = false;  // 主要判断rightExpression是不是一个整体表达式组 例如pt_d = '' 或者(pt_d = '' or id = '')    左侧一直是整体(leftExpression)
  private boolean nonTaken = false; //取非判断 例如 !(condition)  一般针对括号整体
  private boolean isFunction = false;  //判断是否为函数表达式
  private String conditions; // 这个表达式中的整个条件的字符串
  private String format;
  private String joiner;
  private String subJoiner;
  private String selfAndOr;
  private Expression leftExpression; // 左边的表达式
  private Expression rightExpression; // 右边的表达式
  private List<Expression> levelList;   //真正的层级关系
  private Map<Integer, List<Expression>> orMap;  //区分每一层的or分组，map收集

  public Expression() {
  }

  public Expression(boolean isEntirety, boolean nonTaken, boolean isFunction, String conditions, String format, String joiner, String subJoiner, String selfAndOr, Expression leftExpression, Expression rightExpression) {
    this.isEntirety = isEntirety;
    this.nonTaken = nonTaken;
    this.isFunction = isFunction;
    this.conditions = conditions;
    this.format = format;
    this.joiner = joiner;
    this.subJoiner = subJoiner;
    this.selfAndOr = selfAndOr;
    this.leftExpression = leftExpression;
    this.rightExpression = rightExpression;
  }

  public boolean isEntirety() {
    return isEntirety;
  }

  public void setEntirety(boolean entirety) {
    isEntirety = entirety;
  }

  public boolean isNonTaken() {
    return nonTaken;
  }

  public void setNonTaken(boolean nonTaken) {
    this.nonTaken = nonTaken;
  }

  public boolean isFunction() {
    return isFunction;
  }

  public void setFunction(boolean function) {
    isFunction = function;
  }

  public String getConditions() {
    return conditions;
  }

  public void setConditions(String conditions) {
    this.conditions = conditions;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getJoiner() {
    return joiner;
  }

  public void setJoiner(String joiner) {
    this.joiner = joiner;
  }

  public String getSubJoiner() {
    return subJoiner;
  }

  public void setSubJoiner(String subJoiner) {
    this.subJoiner = subJoiner;
  }

  public String getSelfAndOr() {
    return selfAndOr;
  }

  public void setSelfAndOr(String selfAndOr) {
    this.selfAndOr = selfAndOr;
  }

  public Expression getLeftExpression() {
    return leftExpression;
  }

  public void setLeftExpression(Expression leftExpression) {
    this.leftExpression = leftExpression;
  }

  public Expression getRightExpression() {
    return rightExpression;
  }

  public void setRightExpression(Expression rightExpression) {
    this.rightExpression = rightExpression;
  }

  public List<Expression> getLevelList() {
    return levelList;
  }

  public void setLevelList(List<Expression> levelList) {
    this.levelList = levelList;
  }

  public Map<Integer, List<Expression>> getOrMap() {
    return orMap;
  }

  public void setOrMap(Map<Integer, List<Expression>> orMap) {
    this.orMap = orMap;
  }

  @Override
  public String toString() {
    return "Expression{" +
        "isEntirety=" + isEntirety +
        ", nonTaken=" + nonTaken +
        ", isFunction=" + isFunction +
        ", conditions='" + conditions + '\'' +
        ", format='" + format + '\'' +
        ", joiner='" + joiner + '\'' +
        ", subJoiner='" + subJoiner + '\'' +
        ", selfAndOr='" + selfAndOr + '\'' +
        '}';
  }
}
