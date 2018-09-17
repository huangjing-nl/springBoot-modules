package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.OperType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface OperTypeMapper {
  OperType findByName(String name);
  List<OperType> findAll();
  List<OperType> findByBusiness(String business);
  Set<OperType> findOpers();
}
