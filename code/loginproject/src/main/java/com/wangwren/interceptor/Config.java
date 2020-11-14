package com.wangwren.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * 实现了了WebMvcConfigurer接口。addInterceptors方法为拦截器实例注册方法
 *
 *
 * 也可以继承WebMvcConfigurerAdapter(标位废弃了)，重写addInterceptors方法，实例化拦截器类并将其添加到拦截器链中
 */
@Configuration
public class Config implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //指定拦截器，指定要拦截的路径，指定不需要拦截的路径
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login");
    }
}
