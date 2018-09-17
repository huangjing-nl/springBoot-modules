package com.huawei.spring.core.implement;

import org.springframework.stereotype.Service;

@Service
public class AppleImpl implements com.huawei.spring.core.interfaces.Apple {
  @Override
  public String test() {
    return "Hello World!";
  }
}
