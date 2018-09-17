package com.huawei.spring.api.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration  extends WebMvcConfigurerAdapter{

  @Autowired private AuthorizeInterceptor authorizeInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.
        addInterceptor(authorizeInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v1/jenkins/error")
        .excludePathPatterns("/api/v1/security/check");
  }
}
