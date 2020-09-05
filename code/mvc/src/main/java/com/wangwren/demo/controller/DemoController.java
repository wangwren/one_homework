package com.wangwren.demo.controller;

import com.wangwren.demo.service.IDemoService;
import com.wangwren.mvcframework.annotations.LgAutowired;
import com.wangwren.mvcframework.annotations.LgController;
import com.wangwren.mvcframework.annotations.LgRequestMapping;
import com.wangwren.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LgController
@LgRequestMapping("/demo")
@Security("zhangsan")
public class DemoController {

    @LgAutowired
    private IDemoService demoService;

    @LgRequestMapping("/query")
    @Security({"lisi"})
    public void query(HttpServletRequest request, HttpServletResponse response, String name) {
        demoService.get(name);
    }


    @LgRequestMapping("/list")
    @Security({"lisi","wangwu"})
    public void list(HttpServletRequest request, HttpServletResponse response, String name) {
        demoService.get(name);
    }
}
