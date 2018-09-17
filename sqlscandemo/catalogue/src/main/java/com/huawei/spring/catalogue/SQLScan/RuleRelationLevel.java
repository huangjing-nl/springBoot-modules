package com.huawei.spring.catalogue.SQLScan;

public class RuleRelationLevel {
  private String ruleId;
  private String levelId;

  public RuleRelationLevel() {
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public String getLevelId() {
    return levelId;
  }

  public void setLevelId(String levelId) {
    this.levelId = levelId;
  }

  @Override
  public String toString() {
    return "RuleRelationLevel{" +
        "ruleId='" + ruleId + '\'' +
        ", levelId='" + levelId + '\'' +
        '}';
  }
}
