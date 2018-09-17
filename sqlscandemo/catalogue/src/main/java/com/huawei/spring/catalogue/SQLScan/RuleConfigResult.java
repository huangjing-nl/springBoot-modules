package com.huawei.spring.catalogue.SQLScan;

public class RuleConfigResult {
  private String status;
  private String message;
  private Oper_Rule_Level_Business operRuleLevelBusiness;

  public RuleConfigResult() {
  }

  public RuleConfigResult(String status, String message, Oper_Rule_Level_Business operRuleLevelBusiness) {
    this.status = status;
    this.message = message;
    this.operRuleLevelBusiness = operRuleLevelBusiness;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Oper_Rule_Level_Business getOperRuleLevelBusiness() {
    return operRuleLevelBusiness;
  }

  public void setOperRuleLevelBusiness(Oper_Rule_Level_Business operRuleLevelBusiness) {
    this.operRuleLevelBusiness = operRuleLevelBusiness;
  }

  @Override
  public String toString() {
    return "RuleConfigResult{" +
        "status='" + status + '\'' +
        ", message='" + message + '\'' +
        ", operRuleLevelBusiness=" + operRuleLevelBusiness +
        '}';
  }
}
