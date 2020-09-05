# SpringMVC的url-pattern配置及原理剖析

## 配置

### 方式一

带后缀，比如`*.action`、`*.do`、`*.aaa`。

该方式比较精确、方便，在以前和现在企业中都有很大的使用比例。

### 方式二

`/`
配置`/`不会拦截 `.jsp`，但是会拦截`.html`等静态资源(静态资源：除了servlet和jsp之外的js、css、png、html等)



### 方式三

`/*`拦截所有，包括`.jsp`



## 原理

- 为什么配置为`/`会拦截静态资源？
  - **因为tomcat容器中**有一个web.xml文件(父)，我们做的项目中也有一个web.xml文件(子)，是一个继承关系。
  - 父web.xml中有一个`DefaultServlet`，其对应的`url-pattern`配置的是一个 `/`
  - 而此时，我们自己的web.xml中也配置了一个`/`，导致覆盖了父`web.xml`。
- 为什么配置为`/`不拦截`.jsp`？
  - 因为父web.xml中有一个`JspServlet`，这个servlet拦截`.jsp`文件，而我们的web.xml中并没有覆写这个配置，所以springmvc此时不拦截jsp，jsp的处理交给了tomcat。



![image-20200820000114843](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2020-08-20_00-00-31.png)



![image-20200820000201403](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2020-08-20_00-01-45.png)



![image-20200820000235557](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2020-08-20_00-02-14.png)





## 解决方案

### 方案一

在spring的xml配置文件中加入注解`<mvc:default-servlet-handler/>`，即可解决。

原理：添加该标签配置之后，会在SpringMVC上下文中定义一个`DefaultServletHttpRequestHandler`对象，这个对象如同一个检查人员，对进入`DispatcherServlet`的url请求进行过滤筛查，如果发现是一个静态资源请求，那么会把请求转由web应用服务器(tomcat)默认的`DefaultServlet来处理，如果不是静态资源请求，那么继续由SpringMVC来处理。`

需要注意的是，这种方式的静态资源，只能放在项目的webapp目录下。

### 方案二

SpringMVC自己处理静态资源：

```xml
<!--
	mapping:约定的静态资源的url规则，比如想要访问时，需要在浏览器输入 /resources/xxx.html
  location:指定的静态资源的存放位置
-->
<mvc:resources location="classpath:/" mapping="/resources/**" />
<mvc:resources location="/WEB-INF/js" mapping="/js/**" />
```

