package com.huawei.spring.api.test;

import com.huawei.spring.catalogue.SQLScan.ReportMap;
import com.huawei.spring.catalogue.SQLScan.ResultReport;
import com.huawei.spring.catalogue.SQLScan.ScanResult;
import com.huawei.spring.catalogue.User;
import com.huawei.spring.core.interfaces.Apple;
import com.huawei.spring.mappers.SQLScanMapper.ScanResultMapper;
import com.huawei.spring.mappers.SQLScanMapper.ScanRuleMapper;
import com.huawei.spring.mappers.UserMapper;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@RestController
@EnableScheduling
@RequestMapping("/api/v1/test")
public class TestController {
  @Autowired private UserMapper userMapper;
  @Autowired private ScanRuleMapper ruleMapper;
  @Autowired private Apple apple;
  @Autowired private StringEncryptor stringEncryptor;
  @Autowired private ScanResultMapper resultMapper;

  @Autowired private ThreadPoolTaskScheduler scheduler;
  private ScheduledFuture<?> future;
  private Map<String, ScheduledFuture<?>> futureMap;
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${spring.datasource.url:jdbc:mysql://localhost:3306}")
  private String url;

  @RequestMapping(value = "first", method = RequestMethod.GET)
  public String test() {
    //System.out.println("url: " + url);
    System.out.println("Hello World!");
    return "Hello World!";
  }

  @RequestMapping(value = "/user")
  @ResponseBody
  public String user() {
    User user = userMapper.findUserByName("jack");
    log.debug("this is my first log!");
    return user.getName() + "-----" + user.getAge();
  }

  @RequestMapping(value = "/test")
  public String test1() {
    List<ReportMap> reportMaps = resultMapper.getBusinessGroup(new ResultReport());
    for (ReportMap map : reportMaps) {
      System.out.println(map.toString());
    }
    return "success";
  }

  @RequestMapping(value = "/count")
  public int testCount() {
    return ruleMapper.getCounts();
  }

  @RequestMapping(value = "/encrypt/{password}", method = RequestMethod.GET)
  public String testEncrypt(@PathVariable("password") String password) {
    String test = stringEncryptor.encrypt(password);
    System.out.println("stringEncryptor======>" + test);
    return test;
  }

  @RequestMapping(value = "startTest", method = RequestMethod.GET)
  public void StartTest(){
    future = scheduler.scheduleAtFixedRate(new Runnable() {
      int count = 0;
      @Override
      public void run() {
        count++;
        System.out.println("--------------------------------------");
        System.out.println("当前数量是每三秒增加一次：" + count);
      }
    }, 3000);
    // futureMap.put("1", future);
  }

  @RequestMapping(value = "timer", method = RequestMethod.GET)
  public void timerTest(){
    System.out.println(System.currentTimeMillis());
    long time = new Date().getTime();
    long l = time + 10 * 1000;
    Date next = new Date(l);
    future = scheduler.schedule(new Runnable() {
      int count = 0;
      @Override
      public void run() {
        count++;
        System.out.println("--------------------------------------");
        System.out.println("我已经执行了在当前时间的后10秒的任务了");
        System.out.println(System.currentTimeMillis());
      }
    }, next);
    // futureMap.put("1", future);
  }

  @RequestMapping(value = "endTask", method = RequestMethod.GET)
  public String endTask(){
    if(future!=null){
      // future.cancel(true);
      future.cancel(true);
    }
    System.out.println("endTask");
    return "endTask";
  }

  @RequestMapping(value = "testUrl", method = RequestMethod.GET)
  public void TEST(HttpServletResponse response) throws IOException {
    System.out.println("------测试springboot url 路径重定向------");
    // response.sendRedirect("https://www.baidu.com");
    response.sendRedirect("http://localhost:8080");
  }

}
