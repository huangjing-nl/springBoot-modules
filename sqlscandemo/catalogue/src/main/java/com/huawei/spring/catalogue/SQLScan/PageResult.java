package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
  private int total;
  private List<?> rows;

  public PageResult() {
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public List<?> getRows() {
    return rows;
  }

  public void setRows(List<?> rows) {
    this.rows = rows;
  }

}
