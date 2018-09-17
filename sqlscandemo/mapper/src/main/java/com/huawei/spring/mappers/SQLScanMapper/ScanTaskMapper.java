package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.OperType;
import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ProductInfo;
import com.huawei.spring.catalogue.SQLScan.ScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface ScanTaskMapper {
  List<ScanTask> findTasks();
  int getCounts();
  int getCountsBySearch(Pagination page);
  List<ScanTask> findByPage(Pagination page);
  List<ScanTask> findByPageAndSearch(Pagination page);
  int addTask(ScanTask task);
  ScanTask findById(String id);
  void update(ScanTask task);
  ScanTask findByNameAndBusinessAndBaseType(ScanTask task);
  void deleteById(String taskId);
}
