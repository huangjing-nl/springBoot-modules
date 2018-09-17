package com.huawei.spring.mappers;

import com.huawei.spring.catalogue.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by xWX522916 on 2017/11/22.
 */
@Component
@Mapper
public interface UserMapper {
//  @Select("select * from USER where name = #{name}")
//  User findUserByName(@Param("name")String name);
  User findUserByName(String name);

  List<Map<String, Object>> testForMap();
}
