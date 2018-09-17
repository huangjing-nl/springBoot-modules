package com.huawei.spring.catalogue.parse;

import java.util.List;
import java.util.Map;

public class BtSWNodeGroup {
  private String name;
  private String btName;
  private String alias;
  private boolean isPt_d = false;
  private boolean isPt_h = false;
  private List<BtSWCNode> btSWCNodes;

  private Map<Integer, List<BtSWCNode>> OrGroupMapNodes;

  public BtSWNodeGroup() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBtName() {
    return btName;
  }

  public void setBtName(String btName) {
    this.btName = btName;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public List<BtSWCNode> getBtSWCNodes() {
    return btSWCNodes;
  }

  public void setBtSWCNodes(List<BtSWCNode> btSWCNodes) {
    this.btSWCNodes = btSWCNodes;
  }

  public Map<Integer, List<BtSWCNode>> getOrGroupMapNodes() {
    return OrGroupMapNodes;
  }

  public void setOrGroupMapNodes(Map<Integer, List<BtSWCNode>> orGroupMapNodes) {
    OrGroupMapNodes = orGroupMapNodes;
  }

  @Override
  public String toString() {
    return "BtSWNodeGroup{" +
        "name='" + name + '\'' +
        ", btName='" + btName + '\'' +
        ", alias='" + alias + '\'' +
        ", isPt_d=" + isPt_d +
        ", isPt_h=" + isPt_h +
        ", btSWCNodes=" + btSWCNodes +
        ", OrGroupNodes=" + OrGroupMapNodes +
        '}';
  }
}
