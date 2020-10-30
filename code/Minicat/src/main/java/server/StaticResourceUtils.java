package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 静态资源请求工具类
 */
public class StaticResourceUtils {

    /**
     * 获取静态资源文件的绝对路径
     * @param path
     * @return
     */
    public static String getAbsolutePath(String path) {
        String absolutePath = StaticResourceUtils.class.getResource("/").getPath();

        return absolutePath.replace("\\\\","/") + path;
    }

    /**
     * 读取静态资源文件，输出资源，通过输入输出流
     * @param inputStream
     * @param outputStream
     */
    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {

        int count = 0;
        while(count == 0) {
            count = inputStream.available();
        }

        int resourceSize = count;
        // 输出http请求头,然后再输出具体内容
        outputStream.write(HttpProtocolUtils.getHttpHeader200(resourceSize).getBytes());

        //输出静态资源，读取内容输出

        // 已经读取的内容⻓度
        long written = 0 ;
        // 计划每次缓冲的⻓度
        int byteSize = 1024;

        byte[] bytes = new byte[byteSize];

        while(written < resourceSize) {
            if(written + byteSize > resourceSize) {
                //说明剩余未读取⼤⼩⾜⼀个1024⻓度，那就按真实⻓度处理
                byteSize = (int) (resourceSize - written);
                bytes = new byte[byteSize];

            }

            inputStream.read(bytes);
            outputStream.write(bytes);

            outputStream.flush();

            written+=byteSize;

        }

    }
}
