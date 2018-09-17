package com.huawei.spring.api.SQLScan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@RestController
@RequestMapping(value = "/api/v1/security")
public class SecurityController {
  @Value("${sqlscan.exclude.path:L2FwaS92MS8qKg==}")
  private String excludePath;

  @RequestMapping(value = "check",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean security() throws UnsupportedEncodingException {
    String[] split = excludePath.split(",");
    String path = new String(Base64.getDecoder().decode(split[0].getBytes("UTF-8")));
    if (path.equals("/api/v1/**")) {
      return true;
    } else {
      return false;
    }
  }
}
