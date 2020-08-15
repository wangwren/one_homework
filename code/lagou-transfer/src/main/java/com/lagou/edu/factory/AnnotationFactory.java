package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationFactory {

    /**
     * 存储对象
     */
    private static Map<String,Object> map = new HashMap<>();

    static {
        //初始化指定包下的注解
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                //扫描的包
                .forPackages("com.lagou.edu")
                //添加属性注解扫描工具
                .addScanners(new FieldAnnotationsScanner())
                //添加方法注解扫描工具
                .addScanners(new MethodAnnotationsScanner()));


        try {

            //获取到service注解
            Set<Class<?>> serviceClass = reflections.getTypesAnnotatedWith(Service.class);

            for (Class<?> service : serviceClass) {

                Object o = service.newInstance();
                Service serviceAnnotation = service.getAnnotation(Service.class);
                if ("".equals(serviceAnnotation.value())) {
                    //如果没有写value属性
                    String name = service.getName();
                    String[] split = name.split("\\.");
                    String value = split[split.length - 1];
                    map.put(value,o);
                } else {
                    //放入map容器
                    map.put(serviceAnnotation.value(),o);
                }
            }


            //获取autowired注解
            Set<Field> fieldsAnnotatedWith = reflections.getFieldsAnnotatedWith(Autowired.class);
            for (Field field : fieldsAnnotatedWith) {
                field.setAccessible(true);
                //属性名称
                String fieldName = field.getName();
                //属性所在的类
                Class<?> declaringClass = field.getDeclaringClass();
                //属性所在类的全限定名
                String name = declaringClass.getName();
                String[] split = name.split("\\.");
                String key = split[split.length - 1];
                //获取属性所在类的实例化对象
                Object o = map.get(key);
                //获取属性的对象
                Object value = map.get(fieldName);
                field.set(o,value);

                map.put(key,o);

            }

            //获取Transactional注解，该注解在方法上
            Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(Transactional.class);
            for (Method method : methodsAnnotatedWith) {
                //获取方法所在类的全限定名
                String name = method.getDeclaringClass().getName();
                String[] split = name.split("\\.");
                String key = split[split.length - 1];
                //获取属性所在类的实例化对象
                Object o = map.get(key);
                Class<?>[] interfaces = o.getClass().getInterfaces();

                //获取代理对象
                ProxyFactory proxyFactory = (ProxyFactory) map.get("ProxyFactory");
                Object proxy = null;
                if (interfaces != null && interfaces.length > 0) {
                    //如果实现了接口
                    proxy = proxyFactory.getJdkProxy(o);
                } else {
                    //如果没有实现接口
                    proxy = proxyFactory.getCglibProxy(o);
                }

                map.put(key,proxy);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据注解名获取实例
     * @return
     */
    public static Object getBean(String name) {
        return map.get(name);
    }
}
