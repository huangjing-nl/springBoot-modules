package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.SqlScript;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface SqlScriptMapper {

  List<SqlScript> findBytaskId(String taskId);
  int addSqlScript(SqlScript sqlScript);
  void addSqlScriptByBatch(List<SqlScript> scripts);
  void deleteByTaskId(String taskId);
}
