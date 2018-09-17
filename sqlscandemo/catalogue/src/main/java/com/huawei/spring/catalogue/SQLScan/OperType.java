package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Set;

/**
 * 块类型
 * Created by xWX522916 on 2017/11/20.
 */
public class OperType implements Serializable {
  private String id;
  private String name;
  private String description;
  private Set<ScanRule> scanRules;

  public OperType() {
  }

  public OperType(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<ScanRule> getScanRules() {
    return scanRules;
  }

  public void setScanRules(Set<ScanRule> scanRules) {
    this.scanRules = scanRules;
  }

  @Override
  public String toString() {
    return "OperType{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", scanRules=" + scanRules +
        '}';
  }
}
