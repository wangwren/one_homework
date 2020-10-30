package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装response请求
 *
 * 该对象提供核心方法，输出html
 */
public class Response {

    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 通用的输出
     */
    public void outPut(String context) throws IOException {
        outputStream.write(context.getBytes());
    }

    /**
     * 根据url获取静态资源，根据url来获取到静态资源的绝对路径，进⼀步根据绝对路径 读取该静态资源⽂件，
     * 最终通过输入输出流输出
     *
     * 一种情况，请求根路径 / ---> 定位到的是class文件夹
     * @param url
     */
    public void outPutHtml(String url) throws IOException {
        String absolutePath = StaticResourceUtils.getAbsolutePath(url);

        File file = new File(absolutePath);
        if (file.exists() && file.isFile()) {
            //如果file存在，并且是文件(不是文件夹)，输出静态资源
            StaticResourceUtils.outputStaticResource(new FileInputStream(file),outputStream);
        } else {
            //否则输出404
            outPut(HttpProtocolUtils.getHttpHeader404());
        }
    }
}
