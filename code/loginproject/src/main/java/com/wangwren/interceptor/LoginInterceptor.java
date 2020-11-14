package com.wangwren.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器,springboot的拦截器需要在config配置类中单独配置
 * @author wwr
 */
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        if (user == null) {

            response.sendRedirect("/login");

            return false;
        }

        return true;
    }
}
