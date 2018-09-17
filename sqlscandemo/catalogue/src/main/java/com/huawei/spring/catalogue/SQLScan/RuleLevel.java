package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;

public class RuleLevel implements Serializable {

  private String id;
  private String level;

  public RuleLevel() {
  }

  public RuleLevel(String id, String level) {
    this.id = id;
    this.level = level;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  @Override
  public String toString() {
    return "RuleLevel{" +
        "id='" + id + '\'' +
        ", level='" + level + '\'' +
        '}';
  }
}
