package com.huawei.spring.catalogue.parse;

public class Block {
  /**
   * 层
   */
  private int layer;
  /**
   * 第几块
   */
  private String blockIndex;
  /**
   * 块内容
   */
  private String blockContent;
  /**
   * 块开始Num
   */
  private int blockStartNum;
  /**
   * 块大小
   */
  private int blockLength;
  /**
   * 块结束Num
   */
  private int bloockEndNum;

  public Block() {
  }

  public String getBlockContent() {
    return blockContent;
  }

  public void setBlockContent(String blockContent) {
    this.blockContent = blockContent;
  }

  public int getBlockStartNum() {
    return blockStartNum;
  }

  public void setBlockStartNum(int blockStartNum) {
    this.blockStartNum = blockStartNum;
  }

  public int getBlockLength() {
    return blockLength;
  }

  public void setBlockLength(int blockLength) {
    this.blockLength = blockLength;
  }

  public int getBloockEndNum() {
    return bloockEndNum;
  }

  public void setBloockEndNum(int bloockEndNum) {
    this.bloockEndNum = bloockEndNum;
  }

  public int getLayer() {
    return layer;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public String getBlockIndex() {
    return blockIndex;
  }

  public void setBlockIndex(String blockIndex) {
    this.blockIndex = blockIndex;
  }

  @Override
  public String toString() {
    return "Block{" +
        "layer=" + layer +
        ", blockIndex='" + blockIndex + '\'' +
        ", blockContent='" + blockContent + '\'' +
        ", blockStartNum=" + blockStartNum +
        ", blockLength=" + blockLength +
        ", bloockEndNum=" + bloockEndNum +
        '}';
  }
}
