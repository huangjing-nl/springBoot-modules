package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ScanResult;

import java.util.List;

public interface ScanResultManagement {
  List<ScanResult> findAll();
  List<ScanResult> findByTaskId(String taskId);
  List<ScanResult> findByPage(Pagination page);
  List<ScanResult> findByPageAndTaskId(Pagination page);
}
