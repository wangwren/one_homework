package com.wangwren.pojo;

import com.alibaba.fastjson.JSON;

/**
 * 采用JSON的方式，定义JSONSerializer的实现类
 */
public class JSONSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {

        return JSON.toJSONBytes(object);

    }



    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {

        return JSON.parseObject(bytes, clazz);

    }

}
