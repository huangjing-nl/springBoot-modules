package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;

public class OperRelationRule implements Serializable {
  private String operName;
  private String ruleId;

  public OperRelationRule() {
  }

  public String getOperName() {
    return operName;
  }

  public void setOperName(String operName) {
    this.operName = operName;
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  @Override
  public String toString() {
    return "OperRelationRule{" +
        "operName='" + operName + '\'' +
        ", ruleId='" + ruleId + '\'' +
        '}';
  }
}
