package com.huawei.spring.core.implement.SQLScan.util;

import com.huawei.spring.catalogue.SQLScan.ScanTask;

import java.text.SimpleDateFormat;

public class ScanTaskUtil {
  public static ScanTask getTask(String baseType, String business, String baseDir, String middle) {
    ScanTask scanTask = new ScanTask(baseType, business);
    scanTask.setName(business + "_" + middle +  "_" + baseType + "_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(scanTask.getCreated()));
    String taskPath = baseDir + business + "/" + scanTask.getId();
    scanTask.setTaskPath(taskPath);
    return scanTask;
  }

//  public static void test() {
//    new ParseDriver()
//  }
}
