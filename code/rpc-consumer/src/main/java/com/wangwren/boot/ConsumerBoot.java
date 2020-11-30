package com.wangwren.boot;

import com.wangwren.client.RpcConsumer;
import com.wangwren.service.IUserService;

public class ConsumerBoot {

    private static final String PROVIDER_PARAM = "UserService#sayHello#";

    public static void main(String[] args) throws InterruptedException {

        //获取代理对象
        //IUserService userService = (IUserService) RpcConsumer.createProxy(IUserService.class, PROVIDER_PARAM);

        //新方式
        IUserService userService = (IUserService) RpcConsumer.createProxyNew(IUserService.class);


        while (true) {
            //代理对象调用方法时，会调用动态代理的invoke方法
            String s = userService.sayHello("hello server");
            System.out.println(s);
            Thread.sleep(2000);
        }
    }
}
