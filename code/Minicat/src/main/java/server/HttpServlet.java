package server;

public abstract class HttpServlet implements Servlet {

    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);

    public void service(Request request, Response response) {

        if ("GET".equals(request.getMethod())) {
            this.doGet(request,response);
        } else if ("POST".equals(request.getMethod())) {
            this.doPost(request,response);
        }
    }
}
