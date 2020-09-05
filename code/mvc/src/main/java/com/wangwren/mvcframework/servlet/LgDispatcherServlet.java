package com.wangwren.mvcframework.servlet;

import com.wangwren.mvcframework.annotations.*;
import com.wangwren.mvcframework.pojo.Handler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wwr
 */
public class LgDispatcherServlet extends HttpServlet {

    /**
     * 读取配置文件
     */
    private Properties properties = new Properties();

    /**
     * 用于保存指定包下的全限定类名
     */
    private List<String> classNames = new ArrayList<>();

    /**
     * ioc容器，存放实例化的对象
     */
    private Map<String,Object> ioc = new HashMap<>();

    /**
     * 存放handler方法的相关信息
     */
    //private List<Handler> handlerMapping = new ArrayList<>();

    private Map<String,Handler> handlerMapping = new HashMap<>();

    /**
     * 容器初始化
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");

        //1. 加载配置文件 springmvc.properties
        doLoadConfig(contextConfigLocation);

        //2. 扫描类，扫描注解(需要先根据指定的包找到类，之后才能知道类上有什么注解)
        doScan(properties.getProperty("scanPackage"));

        //3. 将相应的bean进行初始化(ioc容器，基于容器)
        doInstance();


        //4. 实现依赖注入
        doAutowired();


        //5. 构造一个handlerMapping处理器映射器，建立url和method的映射关系,很重要
        initHandlerMapping();

        System.out.println("mvc 初始化完成...");

        //处理请求，就会去掉doGet或doPost方法了

    }

    /**
     * 构造一个处理器映射器
     * 存放url和method的映射关系，但是这种方式会有问题，在最后执行的时候拿不到执行method的对象
     *
     * 封装一个handler方法的对象，里面存放handler的相关信息
     *
     * 这个方法很重要
     */
    private void initHandlerMapping() {

        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(LgController.class)) {
                //如果该类上没有LgController注解(说明是service),执行下一个对象
                //不能通过LgRequestMapping注解去判断，因为有的controller上也可以不写该注解
                continue;
            }

            String baseUrl = "";
            if (aClass.isAnnotationPresent(LgRequestMapping.class)) {
                //获取类上的基础url
                LgRequestMapping annotation = aClass.getAnnotation(LgRequestMapping.class);
                baseUrl = annotation.value();
            }

            String[] username = null;
            if (aClass.isAnnotationPresent(Security.class)) {
                //获取类上的
                Security annotation = aClass.getAnnotation(Security.class);
                username = annotation.value();
            }

            //获取该类中的所有方法
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {

                if (!method.isAnnotationPresent(LgRequestMapping.class)) {
                    //如果该方法上没有LgRequestMapping注解，不处理，执行下一个
                    continue;
                }

                //处理方法上的@LgRequestMapping注解
                LgRequestMapping annotation = method.getAnnotation(LgRequestMapping.class);
                String methodUrl = annotation.value();
                //完整的url
                String url = baseUrl + methodUrl;

                //处理方法上的@Security注解
                Security security = method.getAnnotation(Security.class);
                String[] value = security.value();
                //将两个数组合并为一个数组
                username = ArrayUtils.addAll(username,value);

                //将method中的相关信息封装到handler对象中
                Handler handler = new Handler(entry.getValue(),method, Pattern.compile(url),username);

                //封装方法中的参数query(HttpServletRequest request, HttpServletResponse response, String name)
                //这里就先只处理request，response，string类型的参数，别的太多类型就不处理了
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        //如果方法中的参数是request或response，单独做下处理
                        //存入handler对象中;存入固定的名称，指定其在method中的位置
                        handler.getMappingHandlerParams().put(parameter.getType().getSimpleName(),i);
                    } else {
                        //如果是别的参数，string
                        //获取到参数名称
                        String name = parameter.getName();
                        //<name,2>
                        handler.getMappingHandlerParams().put(name,i);
                    }
                }

                //handlerMapping.add(handler);
                handlerMapping.put(url,handler);
            }
        }
    }

    /**
     * 依赖注入
     * 获取到对象中的所有属性，将属性上有注入注解的进行赋值
     */
    private void doAutowired() {

        if (ioc.isEmpty()) {
            return;
        }

        //遍历容器中的对象
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            //获取到容器中的对象
            Object object = entry.getValue();
            //获取对象中的所有属性
            Field[] declaredFields = object.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(LgAutowired.class)) {
                    continue;
                }

                //如果属性上有LgAutowired注解；private IDemoService demoService;
                //LgAutowired注解有value属性，需要判断一下
                LgAutowired annotation = field.getAnnotation(LgAutowired.class);
                String beanName = annotation.value();
                if ("".equals(beanName)) {
                    //如果没写value属性，那么就需要获取属性的类型(是一个接口),通过类名去ioc容器中获取
                    //IDemoService,getName()获取的是全限定名
                    //beanName = field.getType().getName();
                    beanName = field.getType().getSimpleName();
                }

                //为属性赋值，从ioc容器中通过beanName获取到对象
                field.setAccessible(true);
                try {
                    //第一个值表示要为哪一个对象的属性赋值；第二个属性表示要赋的值
                    field.set(object,ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化容器
     * 根据类的全限定名，判断类上是否有注解，是否实现接口，来进行实例化，存入容器中
     */
    private void doInstance() {
        if (classNames.size() == 0) {
            return;
        }

        try {

            for (String className : classNames) {

                Class<?> aClass = Class.forName(className);
                if (aClass.isAnnotationPresent(LgController.class)) {

                    //如果当前类被LgController注解标识
                    //对于LgController注解，就不单独去解析value属性值了，使用默认
                    //使用类名首字母小写的方式存入容器中
                    //DemoController
                    String simpleName = aClass.getSimpleName();
                    //demoController
                    String beanName = lowerFirst(simpleName);
                    //实例化存入容器中
                    ioc.put(beanName,aClass.newInstance());

                } else if (aClass.isAnnotationPresent(LgService.class)) {

                    //如果当前类被LgService注解标识
                    //对于LgService注解，考虑value属性
                    LgService annotation = aClass.getAnnotation(LgService.class);
                    String beanName = annotation.value().trim();
                    if (!"".equals(beanName)) {

                        //如果value属性不为空
                        ioc.put(beanName,aClass.newInstance());

                    } else {

                        //如果为空，那么类名首字母小写存入ioc容器中
                        beanName = lowerFirst(aClass.getSimpleName());
                        ioc.put(beanName,aClass.newInstance());

                    }

                    //service层往往都会实现接口，面向接口开发，此时再以接口名为id，存入ioc容器中，便于后期根据接口类型注入
                    Class<?>[] interfaces = aClass.getInterfaces();

                    for (Class<?> anInterface : interfaces) {

                        beanName = anInterface.getSimpleName();
                        //以接口名再存入一份对象，注意这里接口名是没有转小写的
                        ioc.put(beanName,aClass.newInstance());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 扫描类，将包下类的全限定名保存起来，用于之后的初始化容器操作
     * @param scanPackage 要扫描的包
     */
    private void doScan(String scanPackage) {
        //com.wangwren.demo 需要找到磁盘上的路径

        //获取类加载器的方式很多；.getResource("").getPath()是获取到项目的路径,之后再加上要扫描的包路径，将 . 替换成 / ，因为在文件磁盘上是 com/wangwren/demo
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.", "/");

        //获取到文件路径后，就可以借助 File 了
        File pack = new File(scanPackagePath);
        //获取到路径下的所有文件
        File[] files = pack.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //如果该file又是一个目录，递归；com.wangwren.demo.controller
                doScan(scanPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")){
                //如果该文件的后缀是class结尾
                //com.wangwren.demo.controller.DemoController
                String className = scanPackage + "." + file.getName().replaceAll(".class","");
                //缓存起来
                classNames.add(className);
            }
        }
    }

    /**
     * 加载配置文件
     * @param contextConfigLocation 文件路径
     */
    private void doLoadConfig(String contextConfigLocation) {
        //使用类加载器，读取文件路径，转化成输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            //读取配置文件至内存
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将传递进来的字符串进行首字母小写
     * 因为大小写首字母之间相差32，可以利用这个特性
     * @param simpleName
     * @return
     */
    private String lowerFirst(String simpleName) {
        char[] chars = simpleName.toCharArray();
        if ('A' <= chars[0] && chars[0] <= 'Z') {

            //将首字母转小写；不能写 chars[0] = chars[0] + 32; 可以 chars[0] = (char) (chars[0] + 32);
            chars[0] += 32;
        }

        return String.valueOf(chars);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //处理请求，获取到请求的uri
        String uri = req.getRequestURI();
        //获取handler
        Handler handler = getHandler(uri);

        if (handler == null) {
            //如果handler为空，直接404返回
            resp.getWriter().write("404 not found");
            return;
        }

        //获取请求链接中的username参数，校验是否可以访问当前链接
        String username = req.getParameter("username");
        List<String> list = Arrays.asList(handler.getUsername());
        if (!list.contains(username)) {
            //如果list中不包含请求中的数据，不放行,提示没权限
            resp.getWriter().write("500 No permission");
            return;
        }

        //参数绑定，就这块比较难处理
        //获取方法中的参数
        Parameter[] parameters = handler.getMethod().getParameters();
        //方法中的参数个数，就是要执行的参数个数
        Object[] args = new Object[parameters.length];

        //获取请求中的参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        //根据请求中的参数，封装参数,除了request和response参数
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {

            //name=1&name=2 ===> name [1,2] 这种参数一样的值(数组)，需要处理
            String[] value = param.getValue();
            //将value数组中的值使用 "," 连接起来,如同 1,2
            String paramValues = StringUtils.join(value, ",");

            if (!handler.getMappingHandlerParams().containsKey(param.getKey())) {
                //handle中参数的关系中是否包含请求参数的key
                continue;
            }

            //如果包含，拿到参数在method的位置
            Integer index = handler.getMappingHandlerParams().get(param.getKey());
            //把值设置到对应的位置
            args[index] = paramValues;
        }

        //单独处理request，response
        Integer requestIndex = handler.getMappingHandlerParams().get(HttpServletRequest.class.getSimpleName());
        args[requestIndex] = req;

        Integer responseIndex = handler.getMappingHandlerParams().get(HttpServletResponse.class.getSimpleName());
        args[responseIndex] = resp;


        //执行method方法
        try {
            handler.getMethod().invoke(handler.getController(),args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据uri获取到对应的handler
     * @param uri
     * @return
     */
    private Handler getHandler(String uri) {

        if (handlerMapping.isEmpty()) {
            return null;
        }


        /*for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(uri);
            if (!matcher.matches()) {
                //如果uri不匹配，下一个
                continue;
            }

            return handler;
        }*/

        Handler handler = handlerMapping.get(uri);
        return handler;
    }
}
