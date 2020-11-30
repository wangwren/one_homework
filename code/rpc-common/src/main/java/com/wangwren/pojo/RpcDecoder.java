package com.wangwren.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private Serializer serializer;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {

        this.clazz = clazz;
        this.serializer = serializer;

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();
        if (clazz != null&&length>0){
            byte [] bytes = new byte[length];
            ByteBuf byteBuf1 = byteBuf.readBytes(bytes);

            list.add(serializer.deserialize(clazz,bytes));
        }

    }
}
