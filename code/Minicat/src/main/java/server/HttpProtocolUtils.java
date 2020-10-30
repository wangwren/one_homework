package server;

/**
 * http协议工具类，主要提供响应头信息，暂时只提供200和404的情况
 */
public class HttpProtocolUtils {

    /**
     * 200的响应头
     * @param contentLength
     * @return
     */
    public static String getHttpHeader200(long contentLength) {
        return "HTTP/1.1 200 OK \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + contentLength + " \n" +
                "\r\n";
    }


    /**
     * 404的响应头
     * @return
     */
    public static String getHttpHeader404() {
        String str404 = "<h1>404 not found</h1>";
        return "HTTP/1.1 404 NOT Found \n" +
                "Content-Type: text/html \n" +
                "Content-Length: " + str404.getBytes().length + " \n" +
                "\r\n" + str404;

    }
}
