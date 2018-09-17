package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ResultReport implements Serializable {
  private int top;
  private Date startTime;
  private Date endTime;
  private String startTimeStr;
  private String endTimeStr;
  private String business;
  private List<String> businesses;
  private BusinessReport businessReport;
  private RuleReport ruleReport;
  private ResultTop resultTop;

  public ResultReport() {
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public List<String> getBusinesses() {
    return businesses;
  }

  public void setBusinesses(List<String> businesses) {
    this.businesses = businesses;
  }

  public BusinessReport getBusinessReport() {
    return businessReport;
  }

  public void setBusinessReport(BusinessReport businessReport) {
    this.businessReport = businessReport;
  }

  public RuleReport getRuleReport() {
    return ruleReport;
  }

  public void setRuleReport(RuleReport ruleReport) {
    this.ruleReport = ruleReport;
  }

  public ResultTop getResultTop() {
    return resultTop;
  }

  public void setResultTop(ResultTop resultTop) {
    this.resultTop = resultTop;
  }

  @Override
  public String toString() {
    return "ResultReport{" +
        "top=" + top +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", startTimeStr='" + startTimeStr + '\'' +
        ", endTimeStr='" + endTimeStr + '\'' +
        ", business='" + business + '\'' +
        ", businesses=" + businesses +
        ", businessReport=" + businessReport +
        ", ruleReport=" + ruleReport +
        ", resultTop=" + resultTop +
        '}';
  }
}
