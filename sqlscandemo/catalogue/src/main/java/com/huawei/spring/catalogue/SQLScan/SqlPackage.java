package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class SqlPackage implements Serializable {
  private String id;
  private String name;
  private String version;
  private long size;
  private String path;
  /**baseType 为数据库类型*/
  private String baseType;
  private String type;
  private String taskId;
  private Date created;
  private Set<SqlScript> scripts;

  public SqlPackage() {
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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public Set<SqlScript> getScripts() {
    return scripts;
  }

  public void setScripts(Set<SqlScript> scripts) {
    this.scripts = scripts;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return "SqlPackage{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", size=" + size +
        ", path='" + path + '\'' +
        ", baseType='" + baseType + '\'' +
        ", type='" + type + '\'' +
        ", taskId='" + taskId + '\'' +
        ", created=" + created +
        ", scripts=" + scripts +
        '}';
  }
}
