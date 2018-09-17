package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ReportMap;
import com.huawei.spring.catalogue.SQLScan.ResultReport;
import com.huawei.spring.catalogue.SQLScan.ScanResult;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import javax.swing.event.ListDataEvent;
import java.util.List;
import java.util.Map;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface ScanResultMapper {
  List<ScanResult> findAll();
  List<ScanResult> findByTaskId(String taskId);
  List<ScanResult> findByPage(Pagination page);
  List<ScanResult> findByPageAndTaskId(Pagination page);
  int addScanResult(ScanResult result);
  void addSqlResultByBatch(List<ScanResult> results);
  void deleteByTaskId(String taskId);

  long getCountByTime(ResultReport report);
  long getCountByTimeAndBusiness(ResultReport report);
  List<ReportMap> getRuleGroup(ResultReport report);
  List<ReportMap> getBusinessGroup(ResultReport report);
  List<ReportMap> getTopGroup(ResultReport report);
}
