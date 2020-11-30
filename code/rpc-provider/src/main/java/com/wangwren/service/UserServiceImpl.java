package com.wangwren.service;

import com.wangwren.handler.UserServiceHandler;
import com.wangwren.pojo.JSONSerializer;
import com.wangwren.pojo.RpcDecoder;
import com.wangwren.pojo.RpcEncoder;
import com.wangwren.pojo.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Override
    public String sayHello(String msg) {
        System.out.println("this is provider " + msg);
        return "success";
    }


    /**
     * 定义服务端启动方法
     * @param hostName
     * @param port
     */
    public static void serverStart(String hostName,int port) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        //注意这里的编码，服务端接收到客户端需要使用RpcDecoder解码；传给客户端数据用StringEncoder
                        //那么在客户端，就需要使用StringDecoder解码
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new RpcDecoder(RpcRequest.class,new JSONSerializer()));

                        pipeline.addLast(new UserServiceHandler());
                    }
                });

        System.out.println("服务器启动");
        serverBootstrap.bind(hostName,port).sync();
    }
}
