package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.SqlPackage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface SqlPackageMapper {
  int addSqlPackage(SqlPackage sqlPackage);
  List<SqlPackage> findByTaskId(String taskId);
  void deleteByTaskId(String taskId);
}
