package com.huawei.spring.core.implement.SQLScan.custom;

import com.huawei.spring.catalogue.SQLScan.RuleLevel;
import com.huawei.spring.catalogue.SQLScan.SQLBlock;
import com.huawei.spring.catalogue.SQLScan.ScanResult;
import com.huawei.spring.catalogue.SQLScan.ScanRule;
import com.huawei.spring.core.implement.SQLScan.util.SqlScanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CustomUtils {
  private static Logger log = LoggerFactory.getLogger(SqlScanUtil.class);

  static ScanResult getResult(SQLBlock block, String ruleName, String ruleDes, String level) {
    ScanRule scanRule = new ScanRule();
    scanRule.setName(ruleName);
    scanRule.setLevel(new RuleLevel(null, level));
    scanRule.setDescription(ruleDes);
    return SqlScanUtil.getScanResult(block, scanRule, "Failed", "ACTIVE");
  }

  static List<String> columnCodes(String s) {
    ArrayList<String> list = new ArrayList<>();
    if (null != s && s.startsWith("_")) {
      s = s.substring(1, s.length()).toLowerCase();
    }
    String[] split = s.split("_+");
    for (int i = 1; i <= split.length ; i++) {
      for (int j = 0; j < split.length + 1 - i; j++) {
        StringBuilder builder = new StringBuilder();
        for (int k = 0; k < i; k++) {
          builder.append(split[j + k]).append("_");
        }
        String str = builder.toString();
        str = str.substring(0, str.length() - 1);
        list.add(str);
      }
    }
    return list;
  }

  static String formatSpecialCharacter(String s) {
    return s.replaceAll("[$]", "01")
        .replaceAll("[\\^]", "02")
        .replaceAll("[/]", "03")
        .replaceAll("[&]", "04")
        .replaceAll("[@]", "05")
        .replaceAll("[\\\\]", "06")
        .replaceAll("[*]", "07")
        .replaceAll("[+]", "08")
        .replaceAll("[=]", "09")
        .replaceAll("[<]", "10")
        .replaceAll("[>]", "11")
        .replaceAll("[?]", "12")
        .replaceAll("[(]", "13")
        .replaceAll("[)]", "14");
  }
}
