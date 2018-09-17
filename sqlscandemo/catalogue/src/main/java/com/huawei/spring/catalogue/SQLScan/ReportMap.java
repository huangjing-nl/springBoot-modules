package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;

public class ReportMap implements Serializable {

  private String keyword;
  private float percentage;
  private String key;
  private long value;

  public ReportMap() {
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public float getPercentage() {
    return percentage;
  }

  public void setPercentage(float percentage) {
    this.percentage = percentage;
  }

  public void calculate(long sum) {
    this.percentage = (float) value/ (float) sum * 100;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "ReportMap{" +
        "keyword='" + keyword + '\'' +
        ", percentage=" + percentage +
        ", key='" + key + '\'' +
        ", value=" + value +
        '}';
  }
}
