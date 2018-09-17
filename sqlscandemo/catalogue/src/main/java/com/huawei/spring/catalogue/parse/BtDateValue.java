package com.huawei.spring.catalogue.parse;

public class BtDateValue {
  private String dateValue;
  private boolean isQuotes = true;
  private int IntervalSymbol; //0:'=';  1:'<';  2:'<=';  3:'>';  4:'>=';  5:'<>'

  public BtDateValue() {
  }

  public BtDateValue(String dateValue, boolean isQuotes, int intervalSymbol) {
    this.dateValue = dateValue;
    this.isQuotes = isQuotes;
    IntervalSymbol = intervalSymbol;
  }

  public String getDateValue() {
    return dateValue;
  }

  public void setDateValue(String dateValue) {
    this.dateValue = dateValue;
  }

  public boolean isQuotes() {
    return isQuotes;
  }

  public void setQuotes(boolean quotes) {
    isQuotes = quotes;
  }

  public int getIntervalSymbol() {
    return IntervalSymbol;
  }

  public void setIntervalSymbol(int intervalSymbol) {
    IntervalSymbol = intervalSymbol;
  }

  @Override
  public String toString() {
    return "BtDateValue{" +
        "dateValue='" + dateValue + '\'' +
        ", IntervalSymbol=" + IntervalSymbol +
        '}';
  }
}
