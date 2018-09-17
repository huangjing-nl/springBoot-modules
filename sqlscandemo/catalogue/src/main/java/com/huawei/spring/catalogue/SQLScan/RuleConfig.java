package com.huawei.spring.catalogue.SQLScan;

import java.util.Set;

public class RuleConfig {
  private String businessId;
  private String operName;
  private Set<RuleRelationLevel> ruleRelationLevels;

  public RuleConfig() {
  }

  public String getBusinessId() {
    return businessId;
  }

  public void setBusinessId(String businessId) {
    this.businessId = businessId;
  }

  public String getOperName() {
    return operName;
  }

  public void setOperName(String operName) {
    this.operName = operName;
  }

  public Set<RuleRelationLevel> getRuleRelationLevels() {
    return ruleRelationLevels;
  }

  public void setRuleRelationLevels(Set<RuleRelationLevel> ruleRelationLevels) {
    this.ruleRelationLevels = ruleRelationLevels;
  }

  @Override
  public String toString() {
    return "RuleConfig{" +
        "businessId='" + businessId + '\'' +
        ", operName='" + operName + '\'' +
        ", ruleRelationLevels=" + ruleRelationLevels +
        '}';
  }
}
