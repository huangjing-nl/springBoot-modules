package com.huawei.spring.catalogue.SQLScan;

import java.util.HashMap;
import java.util.Map;

public class OutputComment {
  private String output;
  private Map<String, String> tNameMap;

  public OutputComment() {
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public Map<String, String> gettNameMap() {
    return tNameMap;
  }

  public void settNameMap(Map<String, String> tNameMap) {
    this.tNameMap = tNameMap;
  }

  public void setMapByOutput() {
    Map<String, String> map = new HashMap<>();
    if (this.output.indexOf(":") > 0) {
      String names = this.output.substring(this.output.indexOf(":") + 1, this.output.length());
      if (names.trim().length() > 0) {
        names = names.trim().replaceAll("[ ]+", " ");
        String[] split = names.split("[;, ]");
        for (String s : split) {
          map.put(s, s);
        }
        this.tNameMap = map;
      }
    }
  }

  @Override
  public String toString() {
    return "OutputComment{" +
        "output='" + output + '\'' +
        ", tNameMap=" + tNameMap +
        '}';
  }
}
