package com.huawei.spring.catalogue.SQLScan;


import com.huawei.spring.catalogue.util.IdGenerator;

public class BigTable {
  private String id;
  private String name;
  private int isPt_d;
  private int isPt_h;

  public BigTable() {
  }

  public BigTable(String name, int isPt_d, int isPt_h) {
    this.id = IdGenerator.createUUID();
    this.name = name;
    this.isPt_d = isPt_d;
    this.isPt_h = isPt_h;
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

  public int getIsPt_d() {
    return isPt_d;
  }

  public void setIsPt_d(int isPt_d) {
    this.isPt_d = isPt_d;
  }

  public int getIsPt_h() {
    return isPt_h;
  }

  public void setIsPt_h(int isPt_h) {
    this.isPt_h = isPt_h;
  }

  @Override
  public String toString() {
    return "BigTable{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", isPt_d=" + isPt_d +
        ", isPt_h=" + isPt_h +
        '}';
  }
}
