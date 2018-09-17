package com.huawei.spring;

import com.huawei.spring.mappers.UserMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by xWX522916 on 2017/11/24.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlscanApplicationTests {
    @Autowired private UserMapper userMapper;

    @org.junit.Test
    public void testForMap() {
        List<Map<String, Object>> maps = userMapper.testForMap();
        System.out.println(maps);
    }
}
