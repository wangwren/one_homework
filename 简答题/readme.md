#### Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？

##### 1.动态sql

动态sql是MyBatis强大特性之一，可以根据不同条件拼接sql语句。

##### 2.动态sql标签

###### \<if\>

if是为了判断传入的值是否符合某种规则，比如是否不为空。

###### \<where\>

where标签可以用来做动态拼接查询条件，当和if标签配合的时候，不用显示的声明类似`where 1=1`这种无用的条件。

###### \<choose\>\<when\>\<otherwise\>

这是一组组合标签，它们的作用类似于 Java 中的 `switch`、`case`、`default`。只有一个条件生效，也就是只执行满足的条件 when，没有满足的条件就执行 otherwise，表示默认条件。

###### \<foreach\>

foreach标签可以把传入的集合对象进行遍历，然后把每一项的内容作为参数传到sql语句中，里面涉及到 **item**(具体的每一个对象), **index**(序号), **open**(开始符), **close**(结束符), **separator**(分隔符)。

###### \<include\>

include可以把大量重复的代码整理起来，当使用的时候直接include即可，减少重复代码的编写。

###### \<set\>

适用于更新中，当匹配某个条件后，才会对该字段进行更新操作。

###### \<trim\>

是一个格式化标签，主要有4个参数:**prefix**(前缀); **prefixOverrides**(去掉第一个标记)；**suffix**(后缀)；**suffixOverrides**(去掉最后一个标记)。

对应MyBatis官网中也有动态sql标签的解释：[动态SQL](https://mybatis.org/mybatis-3/zh/dynamic-sql.html)

##### 动态sql执行原理

- 在解析xml配置文件时，在解析`parseConfiguration(parser.evalNode("/configuration"));`方法中，解析mapper标签时`mapperElement(root.evalNode("mappers"));`进入到这个方法中。该方法先获取到mapper的输入流，之后执行`mapperParser.parse();`进行解析。
- 进入`parse()` -> `configurationElement(parser.evalNode("/mapper"));` ->`buildStatementFromContext(context.evalNodes("select|insert|update|delete"));` -> `buildStatementFromContext(list, null);`最后这个方法会遍历节点，之后进行解析，进入到`statementParser.parseStatementNode();`方法中。
- 在`parseStatementNode()`方法中，通过语言驱动接口，调用`SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);`创建SqlSource对象，进入到该方法中。
- 通过`XMLLanguageDriver`XML语言驱动实现类实现了上述的方法。之后创建了`XMLScriptBuilder`对象，该对象是XML 动态语句( SQL )构建器，负责将 SQL 解析成 SqlSource 对象。在创建该对象时会进行初始化，初始化代码如下：

![XMLScriptBuilder](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2020-07-28_23-40-44.png)

- 之后执行该对象的`parseScriptNode()`方法，该方法中调用了`parseDynamicTags(XNode node)`方法解析sql，此方法会获取到xml文件中的动态sql，通过上面截图得到对应的Handler对象，执行`handler.handleNode(child, contents);`解析内部的 SQL 节点，最后将解析的`sqlSource`返回。
- 构建MappedStatement对象，进行存储，最后在执行sql时，解析BoundSql的sql语句。
- 借助功能强大的OGNL表达式。

#### Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？





#### Mybatis都有哪些Executor执行器？它们之间的区别是什么？





#### 简述下Mybatis的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？





#### 简述Mybatis的插件运行原理，以及如何编写一个插件？

##### 1.原理

MyBatis在四大组建`Executor`、`StatementHandler`、`ParameterHandler`、`ResultSetHandler`处提供了简单易用的的插件扩展机制，对MyBatis来说插件就是拦截器，用来增强核心对象的功能，增强功能本质上是借助底层的动态代理实现的。

- 在四大对象创建的时候，每个创建出来的对象不是直接返回的，而是通过`interceptorChain.pluginAll(parameterHandler);`放入链中。
- 获取到所有的插件需要实现的接口`Interceptor`(拦截器)，调用`interceptor.plugin(target)`，返回`target`包装后的对象。
- 可以使用插件为目标对象创建一个代理对象，插件可以为四大对象创建出代理对象，代理对象就可以拦截到四大对象的每一个执行。

##### 2.编写插件

MyBatis插件接口-`Interceptor`，想要实现插件，就要实现该接口。

- 代码实现

```java
/**
 * 注解声明mybatis当前插件拦截哪个对象的哪个方法
 * <p>
 * type表示要拦截的目标对象 Executor.class、StatementHandler.class、ParameterHandler.class、 ResultSetHandler.class
 * method表示要拦截的方法，
 * args表示要拦截方法的参数
 *
 * @author niuanfei
 */
@Intercepts({ //这个大括号说明可以定义多个@Singature对多个地方拦截，都用这个拦截器
        @Signature(type = Executor.class, //指拦截哪个接口
                   method = "query", //指拦截接口内的哪个方法
                   args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})}) //指接口内拦截方法的参数，不要多也不要少
public class TestInterceptor implements Interceptor {
 
    /**
     * 拦截目标对象的目标方法执行
     * 每次执行操作的时候，都会进入这个拦截器方法内
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //被代理对象
        Object target = invocation.getTarget();
        //代理方法
        Method method = invocation.getMethod();
        //方法参数
        Object[] args = invocation.getArgs();
        // do something ...... 方法拦截前执行代码块
        //执行原来方法
        Object result = invocation.proceed();
        // do something .......方法拦截后执行代码块
        return result;
    }
 
    /**
     * 包装目标对象：为目标对象创建代理对象
     * 主要为了把这个拦截器生成的一个代理对象放到拦截器链中
     * @param target 要拦截的对象，目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        System.out.println("MySecondPlugin为目标对象" + target + "创建代理对象");
        //this表示当前拦截器，target表示目标对象，wrap方法利用mybatis封装的方法为目标对象创建代理对象（没有拦截的对象会直接返回，不会创建代理对象）
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }
 
    /**
     * 设置插件在配置文件中配置的参数值
     * 插件初始化的时候调用，也只调用一次
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        System.out.println(properties);
    }
}
```

- 插件写好后，需要在配置文件中进行配置

```xml
<plugins>
  <!--配置插件-->
  <plugin interceptor="com.wangwren.TestInterceptor">
    <!--设置参数-->
    <property name="name" value="name"/>
  </plugin>
</plugins>
```

















