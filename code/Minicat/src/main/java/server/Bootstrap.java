package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Minicat的主程序类
 * @author wwr
 */
public class Bootstrap {

    /**
     * 定义socket监听的端口号
     */
    //private int port = 8080;

    private Map<String,HttpServlet> httpServletMap = new HashMap<>();

    private Mapper mapper = new Mapper();

    /*public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }*/

    /**
     * Minicat启动操作
     */
    public void start() throws IOException, ClassNotFoundException, InstantiationException, DocumentException, IllegalAccessException {

        //加载server.xml
        loadServer();


        //加载web.xml
        loadServlet(mapper);

        //定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10,50,100L
                , TimeUnit.SECONDS,new ArrayBlockingQueue<>(50), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

        ServerSocket serverSocket = new ServerSocket(mapper.getPort());
        System.out.println("=====>>Minicat start on port:" + mapper.getPort());

        /*
            完成Minicat 1.0版本
            需求：浏览器请求http://localhost:8080,返回一个固定的字符串到页面"Hello Minicat!"
         */

        /*//持续监听
        while (true) {
            //接收请求
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();

            String data = "Hello Minicat!";
            String responseText = HttpProtocolUtils.getHttpHeader200(data.length()) + data;
            outputStream.write(responseText.getBytes());
            socket.close();
        }*/


        /*
            完成Minicat 2.0版本
            需求：封装Request和Response对象，返回html静态资源文件
         */
        /*while (true) {
            Socket socket = serverSocket.accept();
            //获取请求的输入流
            InputStream inputStream = socket.getInputStream();
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            //输出静态资源
            response.outPutHtml(request.getUrl());

            socket.close();
        }*/

        /*
         * 完成Minicat 3.0版本
         * 需求：能请求动态资源(servlet)
         */
        /*while (true) {
            Socket socket = serverSocket.accept();
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
        }*/


        /**
         * 使用线程池
         */
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,httpServletMap);
            executor.execute(requestProcessor);
        }

    }

    /**
     * 加载server.xml文件，将信息保存至Mapper对象中
     */
    private void loadServer() throws DocumentException {
        InputStream serverxml = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(serverxml);
        Element rootElement = document.getRootElement();
        List<Element> serviceElements = rootElement.selectNodes("//service");
        for (Element serviceElement : serviceElements) {
            //获取service下的节点
            Element connector = (Element) serviceElement.selectSingleNode("connector");
            //获取到connector节点的port属性值
            String port = connector.attributeValue("port");

            List<Element> engineElements = serviceElement.selectNodes("//engine");
            for (Element engineElement : engineElements) {
                Element host = (Element) engineElement.selectSingleNode("host");
                String hostName = host.attributeValue("name");
                String appBase = host.attributeValue("appBase");

                //封装mapper
                mapper.setPort(Integer.valueOf(port));
                mapper.setHost(hostName);
                mapper.setAppBase(appBase);
            }
        }
    }

    /**
     * 加载web.xml文件，使用dom4j
     */
    private void loadServlet(Mapper mapper) throws DocumentException, ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException {

        //读取appBase路径
        String appBase = mapper.getAppBase();
        File webapps = new File(appBase);
        if (!webapps.exists()) {
            System.out.println("appBase指定文件不存在");
            return;
        }

        File[] files = webapps.listFiles();
        //String[] list = file.list();
        for (File file : files) {
            //mac系统中有一个隐藏文件 .DS_Strore
            if (!file.isFile()) {
                //不是文件，是demo1或demo2
                String path = file.getPath();
                //读取该path文件夹下的web.xml
                InputStream webxml = new FileInputStream(path + "/web.xml");

                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(webxml);
                //获取到根节点
                Element rootElement = document.getRootElement();
                List<Element> selectELements = rootElement.selectNodes("//servlet");
                for (Element selectELement : selectELements) {
                    //获取servlet节点下的servlet-name
                    Element servletNameElement = (Element) selectELement.selectSingleNode("servlet-name");
                    String servletNameValue = servletNameElement.getStringValue();

                    //获取servlet节点下的
                    Element servletClassElement = (Element) selectELement.selectSingleNode("servlet-class");
                    String servletClassValue = servletClassElement.getStringValue();

                    //根据servletNameValue获取servlet-mapping对应的url-pattern
                    //找到根节点下web-app下的servlet-mapping节点的属性servlet-name = servletNameValue的节点
                    Element servletMappingElement = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletNameValue + "']");
                    Element urlPatternElement = (Element) servletMappingElement.selectSingleNode("url-pattern");
                    String urlPatternValue = urlPatternElement.getStringValue();

                    //实例化servlet-class中的类
                    //HttpServlet httpServlet = (HttpServlet) Class.forName(servletClassValue).newInstance();

                    WebAppClassLoader webAppClassLoader = new WebAppClassLoader();
                    Class<?> aClass = webAppClassLoader.findClass(path + "/" + servletClassValue.replace(".","/"));
                    //key:/demo1/lagou   value:对象
                    httpServletMap.put("/" + file.getName() + urlPatternValue,(HttpServlet) aClass.newInstance());
                }
            }
        }

        //InputStream webxml = this.getClass().getClassLoader().getResourceAsStream("web.xml");

    }

    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();
        try {

            bootstrap.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
