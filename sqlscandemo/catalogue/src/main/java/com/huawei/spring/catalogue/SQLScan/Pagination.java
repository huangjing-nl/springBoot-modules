package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.List;

public class Pagination implements Serializable {
  private int rows;
  private int pageSize;
  private int total;
  private int offset;
  private String sort;
  private String sortOrder;
  private String business;
  private String operType;
  private String taskId;
  private String search;

  public Pagination() {
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getOffset() {
    offset = rows * (pageSize - 1);
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public String getOperType() {
    return operType;
  }

  public void setOperType(String operType) {
    this.operType = operType;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  @Override
  public String toString() {
    return "Pagination{" +
        "rows=" + rows +
        ", pageSize=" + pageSize +
        ", total=" + total +
        ", offset=" + offset +
        ", sort='" + sort + '\'' +
        ", sortOrder='" + sortOrder + '\'' +
        ", business='" + business + '\'' +
        ", operType='" + operType + '\'' +
        ", taskId='" + taskId + '\'' +
        ", search='" + search + '\'' +
        '}';
  }
}
