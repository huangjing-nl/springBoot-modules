package com.huawei.spring.catalogue.SQLScan;

import com.huawei.spring.catalogue.parse.QueryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomParameter {
  private String baseType;
  private ScanRule rule;
  private SQLBlock block;
  private List<SQLBlock> blocks;
  private List<SqlScript> scripts;
  private List<BigTable> bigTables;
  private int longDays;
  private int shortDays;
  private int hourDays;
  private Map<String, OperType> typeMap;
  private List<QueryBean> bigTableBeans;
  public static Map<String, String> rootCode;

  private static Logger log = LoggerFactory.getLogger(CustomParameter.class);

  public CustomParameter() {
  }

  public CustomParameter(List<SQLBlock> blocks, List<SqlScript> scripts, List<BigTable> bigTables, int longDays, int shortDays, int hourDays, Map<String, OperType> typeMap) {
    this.blocks = blocks;
    this.scripts = scripts;
    this.bigTables = bigTables;
    this.longDays = longDays;
    this.shortDays = shortDays;
    this.hourDays = hourDays;
    this.typeMap = typeMap;
  }

  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }

  public ScanRule getRule() {
    return rule;
  }

  public void setRule(ScanRule rule) {
    this.rule = rule;
  }

  public SQLBlock getBlock() {
    return block;
  }

  public void setBlock(SQLBlock block) {
    this.block = block;
  }

  public List<SQLBlock> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<SQLBlock> blocks) {
    this.blocks = blocks;
  }

  public List<SqlScript> getScripts() {
    return scripts;
  }

  public void setScripts(List<SqlScript> scripts) {
    this.scripts = scripts;
  }

  public List<BigTable> getBigTables() {
    return bigTables;
  }

  public void setBigTables(List<BigTable> bigTables) {
    this.bigTables = bigTables;
  }

  public int getLongDays() {
    return longDays;
  }

  public void setLongDays(int longDays) {
    this.longDays = longDays;
  }

  public int getShortDays() {
    return shortDays;
  }

  public void setShortDays(int shortDays) {
    this.shortDays = shortDays;
  }

  public int getHourDays() {
    return hourDays;
  }

  public void setHourDays(int hourDays) {
    this.hourDays = hourDays;
  }

  public Map<String, OperType> getTypeMap() {
    return typeMap;
  }

  public void setTypeMap(Map<String, OperType> typeMap) {
    this.typeMap = typeMap;
  }

  public List<QueryBean> getBigTableBeans() {
    return bigTableBeans;
  }

  public void setBigTableBeans(List<QueryBean> bigTableBeans) {
    this.bigTableBeans = bigTableBeans;
  }

  static {
    rootCode = new HashMap<>();
    InputStream stream = CustomParameter.class.getResourceAsStream("/colRoot.txt");
    InputStreamReader ir = new InputStreamReader(stream);
    BufferedReader br = new BufferedReader(ir);
    String line = null;
    try {
      while ((line = br.readLine()) != null) {
        if (line.contains("=")) {
          String[] split = line.split("=");
          if (split.length == 2) {
            rootCode.put(split[0].trim(), split[1].trim());
          }
        }
      }
    } catch (IOException e) {
      log.error("Reading colRoot.txt error: " + e);
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        log.error("Reading colRoot.txt error: " + e);
      }
      try {
        ir.close();
      } catch (IOException e) {
        log.error("Reading colRoot.txt error: " + e);
      }
      try {
        stream.close();
      } catch (IOException e) {
        log.error("Reading colRoot.txt error: " + e);
      }
    }
  }
}
