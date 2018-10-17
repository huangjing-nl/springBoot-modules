package com.huawei.spring.core.implement.SQLScan.util;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTitle {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";
}
