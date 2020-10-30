package server;

import java.io.IOException;

public class LagouServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {

        String context = "<h1>Demo2 Lagou GET</h1>";
        try {
            response.outPut(HttpProtocolUtils.getHttpHeader200(context.length()) + context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String context = "<h1>Lagou POST</h1>";
        try {
            response.outPut(HttpProtocolUtils.getHttpHeader200(context.length()) + context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void destory() {

    }
}
