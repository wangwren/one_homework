package com.wangwren.sqlSession;

import com.wangwren.pojo.Configuration;
import com.wangwren.pojo.MappedStatement;
import org.apache.log4j.Logger;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private static final Logger LOGGER = Logger.getLogger(DefaultSqlSession.class);

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration){
        this.configuration = configuration;
    }

    /**
     * 查询所有
     * @param statementId
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> selectList(String statementId,Object... params) throws Exception {

        Executor executor = new SimpleExecutor();
        List<E> query = executor.query(configuration, configuration.getMappedStatementMap().get(statementId), params);

        return query;
    }

    /**
     * 查询单个
     * @param statementId
     * @param params
     * @param <E>
     * @return
     */
    @Override
    public <E> E selectOne(String statementId, Object... params) throws Exception {

        //调用selectList
        List<E> objects = this.selectList(statementId, params);
        if (objects.size() == 1) {

            return objects.get(0);

        } else if (objects.size() > 1) {

            throw new RuntimeException("查询的结果还有多条数据！");
        }

        return null;
    }

    /**
     * 插入
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int inser(String statementId, Object... params) throws Exception {

        return this.doUpdate(statementId,params);
    }

    /**
     * 更新
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int update(String statementId, Object... params) throws Exception {

        return this.doUpdate(statementId,params);
    }

    /**
     * 删除
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return this.doUpdate(statementId,params);
    }


    /**
     * 使用JDK动态代理为Dao接口生成代理对象
     * @param mapperClass
     * @param <T>
     * @return 返回值是根据传参的dao接口生成的代理对象
     */
    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        //第一个参数是一个classLoader;第二个参数是一个接口的Class对象;第三个参数要实现一个接口
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            /**
             * mapperClass就是一个接口，当这个接口在使用时调用了其自身的方法时，就会触发调用invoke方法
             * @param proxy 当前代理对象的引用(很少用)
             * @param method 当前被调用方法的引用(常用)
             * @param args 传递的参数
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //底层还是去执行JDBC代码，只是根据不同情况来调用selectList还是selectOne
                //由于这种方式是得不到statementId的，这样就不知道执行哪个方法了，所以我们增加了新的规定
                //即mapper.xml中的namespace为dao接口的全限定名，select标签的id为对应的dao接口方法名
                //namespace.id = 接口全限定名.方法名

                //获取到被调用方法的方法名
                String name = method.getName();
                //被调用方法所在类的全限定名
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + name;

                MappedStatement statement = configuration.getMappedStatementMap().get(statementId);
                String sqlType = statement.getSqlType();
                LOGGER.debug("sqlType is " + sqlType);

                Object o;

                switch (sqlType) {
                    case "select" : {
                        Type genericReturnType = method.getGenericReturnType();
                        if (genericReturnType instanceof ParameterizedType) {
                            //判断是否进行了 泛型类型参数化;即方法的返回值是否带有泛型，如果有则执行selectList
                            List<Object> objects = selectList(statementId, args);
                            return objects;
                        }

                        //如果没有则调用selectOne
                        o = selectOne(statementId, args);
                    }
                        break;

                    case "insert" : o = inser(statementId,args);
                        break;
                    case "update" : o = update(statementId,args);
                        break;
                    case "delete" : o = delete(statementId,args);
                        break;
                    default: throw new RuntimeException("语法错误 sqlType is " + sqlType);
                }

                return o;

            }
        });


        //返回的是一个代理对象
        return (T) proxyInstance;
    }


    /**
     * 插入、更新、删除
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    private int doUpdate(String statementId, Object... params) throws Exception {
        Executor executor = new SimpleExecutor();
        int count = executor.doUpdate(configuration,configuration.getMappedStatementMap().get(statementId),params);

        return count;
    }
}
