package com.wangwren.demo.service;

import com.wangwren.mvcframework.annotations.LgService;

@LgService
public class DemoServiceImpl implements IDemoService {
    @Override
    public void get(String name) {
        System.out.println("service方法中的 name is " + name);
    }
}
