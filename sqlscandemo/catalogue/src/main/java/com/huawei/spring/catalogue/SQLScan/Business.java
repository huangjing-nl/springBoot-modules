package com.huawei.spring.catalogue.SQLScan;

import java.io.Serializable;
import java.util.Set;

public class Business implements Serializable {
  private String id;
  private String name;
  private Set<ScanTask> tasks;

  public Business() {
  }

  public Business(String id, String name) {
    this.id = id;
    this.name = name;
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

  public Set<ScanTask> getTasks() {
    return tasks;
  }

  public void setTasks(Set<ScanTask> tasks) {
    this.tasks = tasks;
  }

  @Override
  public String toString() {
    return "Business{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", tasks=" + tasks +
        '}';
  }
}
