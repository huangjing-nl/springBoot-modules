package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RuleReport implements Serializable {
  private String title;
  private List<ReportMap> reportMaps;
  private XAxis xAxis;
  private YAxis yAxis;

  public RuleReport() {
  }

  public RuleReport(String title, List<ReportMap> reportMaps) {
    this.title = title;
    this.reportMaps = reportMaps;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<ReportMap> getReportMaps() {
    return reportMaps;
  }

  public void setReportMaps(List<ReportMap> reportMaps) {
    this.reportMaps = reportMaps;
  }

  public XAxis getxAxis() {
    return xAxis;
  }

  public void setxAxis(XAxis xAxis) {
    this.xAxis = xAxis;
  }

  public YAxis getyAxis() {
    return yAxis;
  }

  public void setyAxis(YAxis yAxis) {
    this.yAxis = yAxis;
  }

  public class XAxis {
    private String name;
    private List<String> xKeys;

    public XAxis() {
    }

    public XAxis(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<String> getxKeys() {
      return xKeys;
    }

    public void setxKeys(List<String> xKeys) {
      this.xKeys = xKeys;
    }

    public void calculate() {
      if (null != reportMaps && reportMaps.size() > 0) {
        List<String> list = new ArrayList<>();
        for (ReportMap reportMap : reportMaps) {
          list.add(reportMap.getKey());
        }
        this.xKeys = list;
      }
    }
  }

  public class YAxis {
    private String name;
    private List<Long> yValues;
    private String company;

    public YAxis() {
    }

    public YAxis(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Long> getyValues() {
      return yValues;
    }

    public void setyValues(List<Long> yValues) {
      this.yValues = yValues;
    }

    public String getCompany() {
      return company;
    }

    public void setCompany(String company) {
      this.company = company;
    }

    public void calculate() {
      if (null != reportMaps && reportMaps.size() > 0) {
        List<Long> list = new ArrayList<>();
        for (ReportMap reportMap : reportMaps) {
          list.add(reportMap.getValue());
        }
        this.yValues = list;
      }
    }
  }
}
