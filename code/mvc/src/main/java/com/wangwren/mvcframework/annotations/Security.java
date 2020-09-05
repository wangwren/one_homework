package com.wangwren.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author wwr
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {
    String[] value();
}
