package com.wangwren.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author wwr
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LgAutowired {
    String value() default "";
}
