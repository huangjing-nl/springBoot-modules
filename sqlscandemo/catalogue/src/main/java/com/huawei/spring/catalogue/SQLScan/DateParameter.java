package com.huawei.spring.catalogue.SQLScan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParameter {

  private static Logger log = LoggerFactory.getLogger(DateParameter.class);

  private static Map<String, String> dateMap;

  public Map<String, String> getDateMap() {
    return dateMap;
  }

  //静态方法，获取txt中的map值。
  static {
    dateMap = new HashMap<>();
    InputStream stream = DateParameter.class.getResourceAsStream("/dateScan.txt");
    InputStreamReader ir = new InputStreamReader(stream);
    BufferedReader br = new BufferedReader(ir);
    String line = null;
    try {
      while ((line = br.readLine()) != null) {
        if (line.contains("=")) {
          String[] split = line.split("=");
          if (split.length == 2) {
            dateMap.put(split[0].trim(), split[1].trim());
          } else {
            log.warn("配置文件格式有误,请检查");
          }
        }
      }
    } catch (IOException e) {
      log.error("Reading dateScan.txt error: " + e);
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        log.error("Reading dateScan.txt error: " + e);
      }
      try {
        ir.close();
      } catch (IOException e) {
        log.error("Reading dateScan.txt error: " + e);
      }
      try {
        stream.close();
      } catch (IOException e) {
        log.error("Reading dateScan.txt error: " + e);
      }
    }
  }

//  public static String replaceDate(Map<String, String> dateMap, String targetStr) {
//    for (Map.Entry<String, String> entry : dateMap.entrySet()) {
//      String key = entry.getKey().toString();
//      if (targetStr.contains(key) && (key.equals("$date") || key.equals("$date_ep"))) {
//        String datestr = DateParameter.getDateStr();
//        targetStr.replace(key, datestr);
//      } else if (targetStr.contains(key) && (key.equals("$last_date") || key.equals("$date_ep"))) {
//        String datestr = DateParameter.getLastDateStr();
//        targetStr.replace(key, datestr);
//      } else if (targetStr.contains(key) && key.equals("$hour")) {
//        String datestr = DateParameter.getHourStr();
//        targetStr.replace(key, datestr);
//      } else if (targetStr.contains(key) && key.equals("$lastHour")) {
//        String datestr = DateParameter.getLastHour();
//        targetStr.replace(key, datestr);
//      } else if (targetStr.contains(key) && (key.equals("$last_hm_date") || key.equals("$last_hm_date_ep"))) {
//        String datestr = DateParameter.getLastHmDateStr();
//        targetStr.replace(key, datestr);
//      } else if (targetStr.contains(key) && key.equals("$month")) {
//        String datestr = DateParameter.getMonthStr();
//        targetStr.replace(key, datestr);
//      }
//    }
//    return null;
//  }


//  public static void main(String args[]) {
//
//    DateParameter dateParameter = new DateParameter();
//    //$date, $date_ep
//    String str = dateParameter.getDateStr();
//    System.out.println("$date, $date_ep    " + str);
//    //hour
//    String str2 = dateParameter.getHourStr();//
//    System.out.println("$hour  " + str2);
//    //lastDate
//    String str3 = dateParameter.getLastDateStr();
//    System.out.println("$lastDate  " + str3);
//
//    //last_hm_date,last_hm_date_ep
//    String str4 = dateParameter.getLastHmDateStr();
//    System.out.println("last_hm_date,last_hm_date_ep   " + str4);
//    ////lastHour
//    String str5 = dateParameter.getLastHour();
//    System.out.println("//lastHour  " + str5);
//    //$month
//    String str6 = dateParameter.getMonthStr();
//    System.out.println("$month   " + str6);
//
////		  String str7 = DateParameter.get;
////		  System.out.println(str);
////
////		  String str8 = DateParameter.getDateStr();
////		  System.out.println(str);
////
////		  String str9 = DateParameter.getDateStr();
////		  System.out.println(str);
//    String str1 = "$date";
//    for (Map.Entry<String, String> entry : dateMap.entrySet()) {
//      String key = entry.getKey().toString();
//      String valueMethod = entry.getValue().toString() + "()";
//      String valueDate = dateParameter.getLastHmDateStr();
//      if (str1.contains(key)) {
//        str1.replace(key, valueDate);
//      }
//    }
//  }
}
