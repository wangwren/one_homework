package com.wangwren.handler;

import com.wangwren.pojo.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {


    private ChannelHandlerContext context;

    /**
     * 接收服务端传来的数据
     */
    private String msg;

    /**
     * 1.0方式
     * 需要写给服务端的数据
     */
    private String param;

    /**
     * 2.0方式传递给服务器端
     */
    private RpcRequest rpcRequest;


    /**
     * 当客户端连接上服务端时会调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    /**
     * 读取服务端数据时会调用该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.msg = msg.toString();
        //接收服务端的数据，唤醒等待的call方法
        notify();
    }

    /**
     * 线程池会调用的方法，相当于Thread中的run方法
     * 使用call方法是因为该方法有返回值，run方法没有返回值
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {

        context.writeAndFlush(rpcRequest);
        //此处等待服务端写回来数据时唤醒
        wait();

        return msg;
    }

    /**
     * 设置参数，传递给服务端
     * @param param
     */
    public void setParam(String param) {
        this.param = param;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }
}
