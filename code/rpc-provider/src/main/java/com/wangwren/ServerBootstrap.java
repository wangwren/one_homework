package com.wangwren;

import com.wangwren.service.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication
public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(ServerBootstrap.class,args);

        UserServiceImpl.serverStart("localhost",8888);
    }
}
