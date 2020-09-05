package com.wangwren.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author wwr
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LgService {
    String value() default "";
}
