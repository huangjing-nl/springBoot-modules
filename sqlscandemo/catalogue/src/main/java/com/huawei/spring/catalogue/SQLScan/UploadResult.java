package com.huawei.spring.catalogue.SQLScan;

import java.util.List;

public class UploadResult {
  private String status;
  private String message;
  private List<SqlScript> scripts;

  public UploadResult() {
  }

  public UploadResult(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public UploadResult(String status, String message, List<SqlScript> scripts) {
    this.status = status;
    this.message = message;
    this.scripts = scripts;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<SqlScript> getScripts() {
    return scripts;
  }

  public void setScripts(List<SqlScript> scripts) {
    this.scripts = scripts;
  }

  @Override
  public String toString() {
    return "UploadResult{" +
        "status='" + status + '\'' +
        ", message='" + message + '\'' +
        ", scripts=" + scripts +
        '}';
  }
}
