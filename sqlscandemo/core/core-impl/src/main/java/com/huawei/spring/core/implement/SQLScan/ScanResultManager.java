package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ScanResult;
import com.huawei.spring.core.interfaces.SQLScan.ScanResultManagement;
import com.huawei.spring.mappers.SQLScanMapper.ScanResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanResultManager implements ScanResultManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private ScanResultMapper resultMapper;

  @Override
  public List<ScanResult> findAll() {
    return resultMapper.findAll();
  }

  @Override
  public List<ScanResult> findByTaskId(String taskId) {
    return resultMapper.findByTaskId(taskId);
  }

  @Override
  public List<ScanResult> findByPage(Pagination page) {
    return resultMapper.findByPage(page);
  }

  @Override
  public List<ScanResult> findByPageAndTaskId (Pagination page) {
    return resultMapper.findByPageAndTaskId(page);
  }
}
