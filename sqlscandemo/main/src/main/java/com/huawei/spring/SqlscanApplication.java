package com.huawei.spring;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
/**
 * Created by xWX522916 on 2017/11/22.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.huawei.spring")
@EnableEncryptableProperties
//@MapperScan(basePackages = "com.huawei.spring")
public class  SqlscanApplication {
  public static void main(String[] args) {
    SpringApplication.run(SqlscanApplication.class, args);
  }

//  private CorsConfiguration buildConfig() {
//    CorsConfiguration corsConfiguration = new CorsConfiguration();
//    corsConfiguration.addAllowedOrigin("*");
//    corsConfiguration.addAllowedHeader("*");
//    corsConfiguration.addAllowedMethod("*");
//    //corsConfiguration.addExposedHeader(HttpHeaderConStant.X_TOTAL_COUNT);
//    return corsConfiguration;
//  }
//
//  /**
//   * 跨域过滤器
//   *
//   * @return
//   */
//  @Bean
//  public CorsFilter corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", buildConfig()); // 4
//    return new CorsFilter(source);
//  }

}
