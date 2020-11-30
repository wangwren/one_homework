package com.wangwren.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器实现：(现在传输的是RpcRequest, 需要编码器将请求对象转换为适合于传输的格式)
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    private Serializer serializer;

    public RpcEncoder(Class<?> clazz, Serializer serializer) {

        this.clazz = clazz;
        this.serializer = serializer;

    }



    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {

        if (clazz != null && clazz.isInstance(msg)) {

            byte[] bytes = serializer.serialize(msg);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }

    }
}
