package com.huawei.spring.catalogue.SQLScan;

import com.huawei.spring.catalogue.util.IdGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class ScanTask implements Serializable {
  private String id;
  private String name;
  private String baseType;
  private Date created;
  private String business;
  private String version;
  private String taskPath;
  private String fileName;
  private String sqlString;
  private String status = "NULL";
  private Set<SQLBlock> blocks;
  private Set<SqlScript> scripts;
  private Set<SqlPackage> packages;
  private Set<ScanResult> results;

  public ScanTask() {
  }

  public ScanTask(String baseType, String business) {
    this.id = IdGenerator.createUUID();
    this.baseType = baseType;
    this.created = new Date();
    this.business = business;
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

  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTaskPath() {
    return taskPath;
  }

  public void setTaskPath(String taskPath) {
    this.taskPath = taskPath;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getSqlString() {
    return sqlString;
  }

  public void setSqlString(String sqlString) {
    this.sqlString = sqlString;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Set<SQLBlock> getBlocks() {
    return blocks;
  }

  public void setBlocks(Set<SQLBlock> blocks) {
    this.blocks = blocks;
  }

  public Set<SqlScript> getScripts() {
    return scripts;
  }

  public void setScripts(Set<SqlScript> scripts) {
    this.scripts = scripts;
  }

  public Set<SqlPackage> getPackages() {
    return packages;
  }

  public void setPackages(Set<SqlPackage> packages) {
    this.packages = packages;
  }

  public Set<ScanResult> getResults() {
    return results;
  }

  public void setResults(Set<ScanResult> results) {
    this.results = results;
  }

  @Override
  public String toString() {
    return "ScanTask{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", baseType='" + baseType + '\'' +
        ", created=" + created +
        ", business='" + business + '\'' +
        ", version='" + version + '\'' +
        ", taskPath='" + taskPath + '\'' +
        ", fileName='" + fileName + '\'' +
        ", sqlString='" + sqlString + '\'' +
        ", status='" + status + '\'' +
        ", blocks=" + blocks +
        ", scripts=" + scripts +
        ", packages=" + packages +
        ", results=" + results +
        '}';
  }
}
