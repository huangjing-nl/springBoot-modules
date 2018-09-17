package com.huawei.spring.core.implement.SQLScan;

import com.huawei.spring.catalogue.SQLScan.BigTable;
import com.huawei.spring.core.interfaces.SQLScan.BigTableManagement;
import com.huawei.spring.mappers.SQLScanMapper.BigTableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BigTableManager implements BigTableManagement {
  private Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private BigTableMapper bigTableMapper;

  @Override
  public List<BigTable> findAll() {
    return bigTableMapper.findAll();
  }
}
