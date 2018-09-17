package com.huawei.spring.core.implement.SQLScan.util;

import com.huawei.spring.catalogue.SQLScan.DateParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
  private static Logger log = LoggerFactory.getLogger(DateUtil.class);

  //"yyyy-MM-dd HH:mm:ss"
  //$date 当前天
  public static String getDateStr() {
    //得到long类型当前时间
    long l = System.currentTimeMillis();
    //new日期对象
    Date dateNow = new Date(l);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    return dateFormat.format(dateNow);
  }

  //$last_date, $date_ep, last_hm_date 前一天
  public static String getLastDateStr() {
    //一天24小时 * 60分* 60秒*1000毫秒
    long lastLong = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
    //new日期对象
    Date lastDate = new Date(lastLong);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    return dateFormat.format(lastDate);
  }

  //$last_date_ep, last_hm_date_ep 前两天
  public static String getLast2DateStr() {
    //一天24小时 * 60分* 60秒*1000毫秒
    long lastLong = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000;
    //new日期对象
    Date lastDate = new Date(lastLong);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    return dateFormat.format(lastDate);
  }

  //hour
  public static String getHourStr() {
    long lastLong = System.currentTimeMillis();
    //new日期对象
    Date hourDate = new Date(lastLong);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
    String hourStr = dateFormat.format(hourDate);
    int index = hourStr.length();
    hourStr = hourStr.substring(index - 2, index);
    return hourStr;
  }

  //lastHour
  public static String getLastHour() {
    long lastHourLong = System.currentTimeMillis() - 60 * 60 * 1000;
    //new日期对象
    Date lastHourDate = new Date(lastHourLong);
    //转换提日期输出格式
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
    String format = dateFormat.format(lastHourDate);
    format = format.substring(format.length() - 2, format.length());
    return format;
  }

//  //last_hm_date, last_hm_date_ep
//  public static String getLastHmDateStr() {
//    long lastLong = System.currentTimeMillis() - 60 * 60 * 1000;
//    //new日期对象
//    Date hourDate = new Date(lastLong);
//    //转换提日期输出格式
//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH");
//    return dateFormat.format(hourDate);
//  }

  //$month 当前月
  public static String getMonthStr() {
    long monthLong = System.currentTimeMillis();
    Date monthDate = new Date(monthLong);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
    //    int index = monthStr.length();
//    monthStr = monthStr.substring(0, index - 2);
    return dateFormat.format(monthDate);
  }
}
