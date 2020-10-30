package server;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * 请求处理器
 */
public class RequestProcessor extends Thread {

    private Socket socket;

    private Map<String,HttpServlet> httpServletMap;

    public RequestProcessor(Socket socket, Map<String,HttpServlet> httpServletMap) {
        this.socket = socket;
        this.httpServletMap = httpServletMap;
    }

    @Override
    public void run() {

        try {
            //获取请求的输入流
            InputStream inputStream = socket.getInputStream();
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            //输出时，先查找url是否在httpServletMap中有
            if (httpServletMap.get(request.getUrl()) == null) {
                //如果不存在，输出静态资源
                response.outPutHtml(request.getUrl());
            } else {
                //如果存在，调用service方法;由service方法决定之后的操作
                httpServletMap.get(request.getUrl()).service(request,response);
            }


            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
