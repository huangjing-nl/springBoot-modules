package com.huawei.spring.core.interfaces.SQLScan;

import com.huawei.spring.catalogue.SQLScan.BigTable;

import java.util.List;

public interface BigTableManagement {
  List<BigTable> findAll();
}
