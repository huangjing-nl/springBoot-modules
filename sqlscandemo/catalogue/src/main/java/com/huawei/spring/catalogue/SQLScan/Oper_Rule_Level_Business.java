package com.huawei.spring.catalogue.SQLScan;

import com.huawei.spring.catalogue.util.IdGenerator;

import java.io.Serializable;

public class Oper_Rule_Level_Business implements Serializable {
  private String id;
  private Business business;
  private OperType operType;
  private ScanRule rule;
  private RuleLevel level;

  public Oper_Rule_Level_Business() {
  }

  public Oper_Rule_Level_Business(Business business, OperType operType, ScanRule rule, RuleLevel level) {
    this.id = IdGenerator.createUUID();
    this.business = business;
    this.operType = operType;
    this.rule = rule;
    this.level = level;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Business getBusiness() {
    return business;
  }

  public void setBusiness(Business business) {
    this.business = business;
  }

  public OperType getOperType() {
    return operType;
  }

  public void setOperType(OperType operType) {
    this.operType = operType;
  }

  public ScanRule getRule() {
    return rule;
  }

  public void setRule(ScanRule rule) {
    this.rule = rule;
  }

  public RuleLevel getLevel() {
    return level;
  }

  public void setLevel(RuleLevel level) {
    this.level = level;
  }

  @Override
  public String toString() {
    return "Oper_Rule_Level_Business{" +
        "id='" + id + '\'' +
        ", business=" + business +
        ", operType=" + operType +
        ", rule=" + rule +
        ", level=" + level +
        '}';
  }
}
