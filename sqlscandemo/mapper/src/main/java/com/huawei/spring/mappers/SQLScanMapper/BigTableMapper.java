package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.BigTable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface BigTableMapper {
  List<BigTable> findAll();
}
