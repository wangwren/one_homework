package com.wangwren.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
public class LoginController {

    /**
     * 首页访问
     * @return
     */
    @GetMapping("/login")
    public String login() {

        return "index";
    }

    /**
     * 登录表单提交
     * @param username
     * @param pwd
     * @return
     */
    @PostMapping("/login")
    public String login(HttpSession session, String username, String pwd) {
        if ("admin".equals(username) && "admin".equals(pwd)) {

            session.setAttribute("user","admin");

            //重定向到查询所有
            return "redirect:list";
        }

        //登录失败，返回index界面
        return "index";
    }

    /**
     * 跳转到查询所有页面
     * @return
     */
    @RequestMapping("list")
    public String list() {
        return "list";
    }

    @RequestMapping("/insert")
    public String insertJsp() {
        return "insert";
    }
}
