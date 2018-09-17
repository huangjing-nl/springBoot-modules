package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;

public class BaseType implements Serializable {
  private String id;
  private String name;

  public BaseType() {
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

  @Override
  public String toString() {
    return "BaseType{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
