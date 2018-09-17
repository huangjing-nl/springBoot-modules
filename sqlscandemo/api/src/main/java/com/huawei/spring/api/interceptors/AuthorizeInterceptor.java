package com.huawei.spring.api.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Service
public class AuthorizeInterceptor extends HandlerInterceptorAdapter {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  // L2FwaS92MS8qKg==  (base64)--> /api/v1/**
  @Value("${sqlscan.exclude.path:L2FwaS92MS8qKg==}")
  private String excludePath;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // org.apache.catalina.connector.Request;
    System.out.println("test cookie: xingtingbiao  123456789");
//    Cookie xingtingbiao = new Cookie("xingtingbiao", "123456789");
//    xingtingbiao.setPath("/");
//    response.addCookie(xingtingbiao);
    response.setHeader("Content-Security-Policy", "script-src 'unsafe-inline' 'self' 'unsafe-eval'");
    response.setHeader("Set-Cookie", "xingtingbiao=123456789; HttpOnly; Path=/");
    if (alwaysAllowedPath(request)) {
      return true;
    } else {
      response.sendRedirect("/api/v1/jenkins/error");
      return false;
    }
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    super.afterCompletion(request, response, handler, ex);
  }

  @Override
  public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    super.afterConcurrentHandlingStarted(request, response, handler);
  }

  private boolean alwaysAllowedPath(HttpServletRequest request) throws UnsupportedEncodingException {
    boolean check = false;
    String[] split = excludePath.split(",");
    for (String base64Path : split) {
      String path = new String(Base64.getDecoder().decode(base64Path.getBytes("UTF-8")));
      if (path.endsWith("**")) {
        path = path.substring(0, path.length() - 2);
        if (request.getRequestURI().startsWith(path)) {
          check = true;
          break;
        }
      } else if (request.getRequestURI().equals(path)){
        check = true;
        break;
      }
    }
    return check;
    // request.getMethod().equalsIgnoreCase("post") && request.getRequestURI().equals("/admin/v1/vnfm-register")
  }
}
