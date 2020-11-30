package com.wangwren.client;

import com.wangwren.handler.UserClientHandler;
import com.wangwren.pojo.JSONSerializer;
import com.wangwren.pojo.RpcDecoder;
import com.wangwren.pojo.RpcEncoder;
import com.wangwren.pojo.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端调用代理方法，返回一个实现了 HelloService 接口的代理对象，调用代理对象的方法，返回结果。
 * 我们需要在代理中做手脚，当调用代理方法的时候，我们需要初始化 Netty 客户端，还需要向服务端请求数据，并返回数据。
 */
public class RpcConsumer {

    /**
     * 创建一个线程池
     * 定义线程池初始数量为cpu核数
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 定义事件处理器
     */
    private static UserClientHandler handler;

    /**
     * 初始化客户端
     */
    public static void initClient() throws InterruptedException {

        handler = new UserClientHandler();

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //指定传输类型
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        //客户端使用RpcEncoder编码，服务端就要使用RpcDecoder解码；
                        //服务端传的是string字符串，就需要用StringDecoder解码
                        pipeline.addLast(new RpcEncoder(RpcRequest.class,new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(handler);
                    }
                });

        bootstrap.connect("localhost",8888).sync();
    }


    /**
     * 创建代理对象
     * @param serviceClass 需要代理的service对象
     * @param providerParam 定义传输的规则
     * @return 生成指定的serviceClass代理对象
     */
    public static Object createProxy(Class<?> serviceClass, final String providerParam) {
        Object instance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        //初始化initClient，启动客户端，连接上服务端
                        if (handler == null) {
                            initClient();
                        }
                        //设置参数 args是代理对象方法中的参数
                        handler.setParam(providerParam + args[0]);
                        //线程池调用,调用call方法，向服务器端写数据
                        Object o = executorService.submit(handler).get();

                        return o;
                    }
                });


        return instance;
    }

    /**
     * 创建代理对象2.0
     * @param serviceClass 需要代理的service对象
     * @return
     */
    public static Object createProxyNew(final Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        //初始化
                        if (handler == null) {
                            initClient();
                        }

                        //设置rpcRequest,定义新的传输规则，封装rpcRequest传递给服务端
                        RpcRequest rpcRequest = new RpcRequest();
                        rpcRequest.setRequestId(UUID.randomUUID().toString());
                        rpcRequest.setClassName(serviceClass.getName());
                        rpcRequest.setMethodName(method.getName());
                        rpcRequest.setParameters(args);

                        Class<?>[] parameterTypes = new Class[args.length];
                        for (int i = 0; i < args.length; i++) {
                            parameterTypes[i] = args[i].getClass();
                        }
                        rpcRequest.setParameterTypes(parameterTypes);

                        handler.setRpcRequest(rpcRequest);

                        //线程池调用,调用call方法，向服务器端写数据
                        Object o = executorService.submit(handler).get();
                        return o;
                    }
                });
    }

}
