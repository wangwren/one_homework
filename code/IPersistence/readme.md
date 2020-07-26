# 框架端

- 本质就是对JDBC代码进行了封装
- 加载配置文件：根据配置文件的路径，加载配置文件成字节输入流，存储在内存中。
    - 创建Resource类，提供方法：InputStream getResourceAsStream(String path)
- 创建两个javaBean，存放的是对两个配置文件解析出来的内容。
    - Configuration：核心配置，存放sqlMapConfig.xml解析出来的内容。
    - MappedStatement：映射配置类，存放mapper.xml解析出来的内容。
- 解析配置文件
    - 创建类SqlSessionFactoryBuilder 方法：SqlSessionFactory build(InputStream in)
    - 第一：使用dom4j解析配置文件，将解析出来的内容封装到容器对象中。
    - 第二：创建SqlSessionFactory对象;生产sqlSession会话对象。(工厂模式)
- 创建SqlSessionFactory接口及实现类DefaultSqlSessionFactory
    - 方法：openSession();生产sqlSession
- 创建SqlSession接口及实现类DefaultSession，定义数据库CRUD操作
    - selectList()
    - selectOne()
    - update()
    - delete()
- 创建Executor接口及实现类SimpleExecutor
    - query(Configuration,MappedStatement,Object... params);该方法内执行的就是JDBC代码
    
    
## 需要去查的知道点
### 反射getDeclaredField和getField的区别
- getDeclaredFiled 仅能获取类本身的属性成员**包括私有、共有、保护** 
- getField 仅能获取类(及其父类) **public属性成员**

### 内省PropertyDescriptor
https://blog.csdn.net/u014082714/article/details/82220510

## Resource类遇到的问题
![](media/15956588882813/15956826150644.jpg)
在加载配置文件时，需要写成`Resource.class.getClassLoader().getResourceAsStream(path)`，就是要加上`getClassLoader`这样在编写测试类时，在传入路径上不需要加 "/"。

![](media/15956588882813/15956827081385.jpg)

加`getClassLoader`与不加的区别：
- 如果加上，那么在传入路径时不需要加"/"，即`InputStream inputStream = Resource.getResourceAsStream("sqlMapConfig.xml");`
- 如果没有加，那么在传入路径时：`InputStream inputStream = Resource.getResourceAsStream("/sqlMapConfig.xml");`