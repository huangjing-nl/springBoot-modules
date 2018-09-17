package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessReport implements Serializable {

  private String name;
  private String type;
  private String radius;
  private List<ReportMap> reportMaps;
  private Map<Long, String> dataMap;

  public BusinessReport() {
  }

  public BusinessReport(String name, String type, List<ReportMap> reportMaps) {
    this.name = name;
    this.type = type;
    this.reportMaps = reportMaps;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRadius() {
    return radius;
  }

  public void setRadius(String radius) {
    this.radius = radius;
  }

  public List<ReportMap> getReportMaps() {
    return reportMaps;
  }

  public void setReportMaps(List<ReportMap> reportMaps) {
    this.reportMaps = reportMaps;
  }

  public void setMap() {
    if (this.reportMaps != null && reportMaps.size() > 0) {
      Map<Long, String> hashMap = new HashMap<>();
      for (ReportMap reportMap : reportMaps) {
        hashMap.put(reportMap.getValue(), reportMap.getKey());
      }
      this.dataMap = hashMap;
    }
  }

  @Override
  public String toString() {
    return "BusinessReport{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", radius='" + radius + '\'' +
        ", reportMaps=" + reportMaps +
        '}';
  }
}
