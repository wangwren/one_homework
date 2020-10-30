package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 封装request请求
 */
public class Request {

    private String method;

    private String url;

    private InputStream inputStream;

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        //从输入流中获取请求，封装请求
        int count = 0;
        while (count == 0) {
            //获取请求到的流，如果有输入数据，那么count>0
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);

        String inputStr = new String(bytes);
        //获取输入流的第一行数据  GET / HTTP/1.1
        String firstLine = inputStr.split("\\n")[0];
        String[] split = firstLine.split(" ");
        this.method = split[0];
        this.url = split[1];

        System.out.println("method=====>>>>" + method);
        System.out.println("url========>>>>" + url);
    }

    public Request() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
