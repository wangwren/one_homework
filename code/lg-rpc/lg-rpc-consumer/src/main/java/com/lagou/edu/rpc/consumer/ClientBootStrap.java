package com.lagou.edu.rpc.consumer;


import com.lagou.edu.rpc.api.UserService;
import com.lagou.edu.rpc.common.ConfigKeeper;
import com.lagou.edu.rpc.common.registry.RpcRegistryHandler;
import com.lagou.edu.rpc.registry.handler.impl.ZookeeperRegistryHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端启动类
 */
public class ClientBootStrap {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> instanceCacheMap = new HashMap<>();
        instanceCacheMap.put(UserService.class.getName(), UserService.class);

        ConfigKeeper.getInstance().setConsumerSide(true);
        // 启动一个定时的线程池，每隔xx秒开始自动上报统计数据到注册中心
        ConfigKeeper.getInstance().setInterval(5);

        RpcRegistryHandler rpcRegistryHandler = new ZookeeperRegistryHandler("120.26.187.50:2181");
        RpcConsumer consumer = new RpcConsumer(rpcRegistryHandler, instanceCacheMap);

        UserService userService = (UserService) consumer.createProxy(UserService.class);

        while (true) {
            Thread.sleep(2000);
            userService.sayHello("are you ok?");
        }
    }
}
