package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.PageResult;
import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ProductInfo;
import com.huawei.spring.catalogue.SQLScan.ScanTask;
import com.huawei.spring.exceptions.AlreadyExistingException;
import com.huawei.spring.exceptions.FileOperationException;
import com.huawei.spring.exceptions.NotAllowedException;

import java.util.List;

public interface ScanTaskManagement {
  ScanTask addScanTask(ScanTask task) throws AlreadyExistingException;
  ScanTask findById(String id);
  ScanTask updateTask(ScanTask task) throws NotAllowedException;
  List<ScanTask> findAll();
  PageResult findByPage(Pagination page);
  void delete(String id) throws FileOperationException;
}
