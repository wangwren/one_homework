package com.wangwren.io;

import java.io.InputStream;

/**
 * 加载配置文件
 */
public class Resource {

    /**
     * 加载配置文件，返回一个输入流
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path){

        InputStream inputStream = Resource.class.getClassLoader().getResourceAsStream(path);
        return inputStream;
    }
}
