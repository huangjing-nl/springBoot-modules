package com.huawei.spring.catalogue.parse;

import java.util.List;

public class BtSWCNode {
  private boolean isAnd;
  private String origin;
  private String node;
  private String partition;
  private boolean isLegal = true;
  private boolean isPt_d;
  private boolean isPt_h;
  private int groupNum;
  private List<BtDateValue> values;

  public BtSWCNode() {
  }

  public BtSWCNode(String node) {
    this.node = node;
  }

  public BtSWCNode(boolean isAnd, String node) {
    this.isAnd = isAnd;
    this.node = node;
  }

  public boolean isAnd() {
    return isAnd;
  }

  public void setAnd(boolean and) {
    isAnd = and;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  public String getPartition() {
    return partition;
  }

  public void setPartition(String partition) {
    this.partition = partition;
  }

  public boolean isLegal() {
    return isLegal;
  }

  public void setLegal(boolean legal) {
    isLegal = legal;
  }

  public boolean isPt_d() {
    return isPt_d;
  }

  public void setPt_d(boolean pt_d) {
    isPt_d = pt_d;
  }

  public boolean isPt_h() {
    return isPt_h;
  }

  public void setPt_h(boolean pt_h) {
    isPt_h = pt_h;
  }

  public int getGroupNum() {
    return groupNum;
  }

  public void setGroupNum(int groupNum) {
    this.groupNum = groupNum;
  }

  public List<BtDateValue> getValues() {
    return values;
  }

  public void setValues(List<BtDateValue> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return "BtSWCNode{" +
        "isAnd=" + isAnd +
        ", origin='" + origin + '\'' +
        ", node='" + node + '\'' +
        ", partition='" + partition + '\'' +
        ", isLegal=" + isLegal +
        ", isPt_d=" + isPt_d +
        ", isPt_h=" + isPt_h +
        ", groupNum=" + groupNum +
        ", values=" + values +
        '}';
  }
}
