package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultTop implements Serializable {
  private String title;
  private List<ReportMap> reportMaps;
  private XAxisTop xAxis;
  private YAxisTop yAxis;

  public ResultTop() {
  }

  public ResultTop(String title, List<ReportMap> reportMaps) {
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

  public XAxisTop getxAxis() {
    return xAxis;
  }

  public void setxAxis(XAxisTop xAxis) {
    this.xAxis = xAxis;
  }

  public YAxisTop getyAxis() {
    return yAxis;
  }

  public void setyAxis(YAxisTop yAxis) {
    this.yAxis = yAxis;
  }

  public class XAxisTop {
    private String name;
    private List<Long> xKeys;

    public XAxisTop() {
    }

    public XAxisTop(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Long> getxKeys() {
      return xKeys;
    }

    public void setxKeys(List<Long> xKeys) {
      this.xKeys = xKeys;
    }

    public void calculate() {
      if (null != reportMaps && reportMaps.size() > 0) {
        List<Long> list = new ArrayList<>();
        for (ReportMap reportMap : reportMaps) {
          list.add(reportMap.getValue());
        }
        this.xKeys = list;
      }
    }
  }

  public class YAxisTop {
    private String name;
    private List<String> yValues;
    private String company;

    public YAxisTop() {
    }

    public YAxisTop(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<String> getyValues() {
      return yValues;
    }

    public void setyValues(List<String> yValues) {
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
        List<String> list = new ArrayList<>();
        for (ReportMap reportMap : reportMaps) {
          list.add(reportMap.getKey());
        }
        this.yValues = list;
      }
    }
  }
}
