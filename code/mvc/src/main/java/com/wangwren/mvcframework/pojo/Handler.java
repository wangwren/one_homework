package com.wangwren.mvcframework.pojo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 封装controller类中方法的相关信息
 * 保证使用反射执行方法时顺利
 */
public class Handler {

    /**
     * method.invoke(obj,)
     * 反射方法执行时，需要该方法的对象
     */
    private Object controller;

    /**
     * 要被执行的方法
     */
    private Method method;


    /**
     * 正则表达式对象，存放url，也可以换成string对象
     */
    private Pattern pattern;

    /**
     * 访问controller或handler的权限
     */
    private String[] username;

    /**
     * 方法中参数的映射
     * key:参数名称
     * value:参数的位置
     *
     * 比如： <name,2>
     */
    private Map<String,Integer> mappingHandlerParams;

    public Handler(Object controller, Method method, Pattern pattern,String[] username) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        this.username = username;
        this.mappingHandlerParams = new HashMap<>();
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getMappingHandlerParams() {
        return mappingHandlerParams;
    }

    public void setMappingHandlerParams(Map<String, Integer> mappingHandlerParams) {
        this.mappingHandlerParams = mappingHandlerParams;
    }

    public String[] getUsername() {
        return username;
    }

    public void setUsername(String[] username) {
        this.username = username;
    }
}
