package com.huawei.spring.catalogue.SQLScan.custom;

import java.util.List;

public class BTLimitEntity {  //作为大表检查是否含有pt_d的实体类

  public BTLimitEntity() {
  }

  public BTLimitEntity(String name, String tName, boolean check, boolean mustBigTable) {
    this.name = name;
    this.tName = tName;
    this.check = check;
    this.mustBigTable = mustBigTable;
    if (mustBigTable) {
      this.checkField = "pt_d";
    } else {
      if (tName.endsWith("_dm")) {
        this.checkField = "pt_d";
      } else if (tName.endsWith("_wm")) {
        this.checkField = "pt_w";
      } else if (tName.endsWith("_mm")) {
        this.checkField = "pt_m";
      }
    }
  }

  public String name; //实际字段前的名称
  public String tName; //from 后的表名
  public String emptyName; // 只有出现在一张表的查询时，这个空名字的值为'NULL'
  public String checkField;
  public boolean check = false;   // 检查大表是否存在pt_d
  public boolean mustBigTable; // 是否必须为大表

  private List<CompleteRange> completeRanges;

  public List<CompleteRange> getCompleteRanges() {
    return completeRanges;
  }

  public void setCompleteRanges(List<CompleteRange> completeRanges) {
    this.completeRanges = completeRanges;
  }

}
