package com.wangwren.handler;

import com.wangwren.pojo.RpcRequest;
import com.wangwren.service.IUserService;
import com.wangwren.service.UserServiceImpl;
import com.wangwren.utils.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * 自定义channle事件处理
 */
public class UserServiceHandler extends ChannelInboundHandlerAdapter {

    /**
     * 监听到读请求时会执行该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //判断是否符合约定，约定为 UserService#sayHello#msg
        /*if (msg.toString().startsWith("UserService")) {
            UserServiceImpl userService = new UserServiceImpl();
            //调用方法
            String result = userService.sayHello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));

            //写会到客户端
            ctx.writeAndFlush(result);
        }*/

        //判断约定
        if (msg instanceof RpcRequest) {

            RpcRequest rpcRequest = (RpcRequest) msg;
            String className = rpcRequest.getClassName();
            String methodName = rpcRequest.getMethodName();

            //通过反射获取到对象
            Class<?> aClass = Class.forName(className);
            Method method = aClass.getMethod(methodName, rpcRequest.getParameterTypes());

            Object bean = SpringUtils.getBean(aClass);
            //执行方法
            Object result = method.invoke(bean, rpcRequest.getParameters());

            //将方法执行结果写回到客户端
            ctx.writeAndFlush(result);

        }

    }
}
