package com.huawei.spring.catalogue.SQLScan.custom;

public class CompleteRange {
  private Range leftRange;
  private Range rightRange;

  public CompleteRange() {
  }

  public CompleteRange(Range leftRange, Range rightRange) {
    this.leftRange = leftRange;
    this.rightRange = rightRange;
  }

  public Range getLeftRange() {
    return leftRange;
  }

  public void setLeftRange(Range leftRange) {
    this.leftRange = leftRange;
  }

  public Range getRightRange() {
    return rightRange;
  }

  public void setRightRange(Range rightRange) {
    this.rightRange = rightRange;
  }

  @Override
  public String toString() {
    return "CompleteRange{" +
        "leftRange=" + leftRange +
        ", rightRange=" + rightRange +
        '}';
  }
}
