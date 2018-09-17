package com.huawei.spring.mappers.SQLScanMapper;

import com.huawei.spring.catalogue.SQLScan.SQLBlock;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface SQLBlockMapper {
  int addBlock(SQLBlock sqlBlock);
}
