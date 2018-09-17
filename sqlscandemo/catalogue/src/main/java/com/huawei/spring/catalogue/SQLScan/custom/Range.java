package com.huawei.spring.catalogue.SQLScan.custom;

import java.io.Serializable;

public class Range implements Serializable {

  private String direction;
  private String value;

  public Range() {
  }

  public Range(String direction) {
    this.direction = direction;
  }

  public Range(String direction, String value) {
    this.direction = direction;
    this.value = value;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Range{" +
        "direction='" + direction + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
