package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.Pagination;
import com.huawei.spring.catalogue.SQLScan.ScanRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface ScanRuleMapper {
  ScanRule findById(String id);
  List<ScanRule> findAll();
  int addRule(ScanRule rule);
  int update(ScanRule rule);
  void delete(String id);
  int getCounts();
  int getCountsBySearch(Pagination page);
  List<ScanRule> findByPage(Pagination page);
  List<ScanRule> findByPageAndSearch(Pagination page);
  List<ScanRule> findByOperAndBusiness(@Param("operName") String operName, @Param("businessId") String business);
}
