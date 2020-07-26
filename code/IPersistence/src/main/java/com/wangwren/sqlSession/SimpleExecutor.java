package com.wangwren.sqlSession;

import com.wangwren.config.BoundSql;
import com.wangwren.pojo.Configuration;
import com.wangwren.pojo.MappedStatement;
import com.wangwren.util.GenericTokenParser;
import com.wangwren.util.ParameterMapping;
import com.wangwren.util.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    /**
     * 执行jdbc
     * @param configuration
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        //封装jdbc相关

        //1.注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        //2.获取sql语句：select * from user where id = #{id} and username = #{username} 这样的sql jdbc是执行不了的
        //将sql转成 select * from user where id = ? and username = ? 转换过程中还需要对#{}中的值进行存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3.获取预处理对象
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());

        //4.设置参数
        //获取参数的全路径
        String paramterType = mappedStatement.getParamterType();
        //使用反射获取到该对象
        Class<?> paramterTypeClass = getClassType(paramterType);

        //获取sql中的占位符参数名称
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            //获取到参数名称
            String content = parameterMapping.getContent();

            //通过参数名称拿到对应的字段,getFields只能获取到public的字段
            Field declaredField = paramterTypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //sql中的参数传递是一个对象，所以取第一个下标值。get方法：方法返回指定对象上由此Field表示的字段的值。就是如果目前该字段是id，那么get方法就会取get参数中的id字段的值
            Object o = declaredField.get(params[0]);
            System.out.println("参数：" + o);
            //设置参数从1开始计算
            preparedStatement.setObject(i + 1,o);
        }

        //5.执行
        ResultSet resultSet = preparedStatement.executeQuery();

        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        List<Object> list = new ArrayList<>();

        while (resultSet.next()) {

            //实例化，一行数据实例化一个
            Object resultTypeObject = resultTypeClass.newInstance();

            //获取元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //getColumnCount 获取到返回的结果有多少列, **元数据下表从1开始，注意要小于等于**

                //获取到列的名称
                String columnName = metaData.getColumnName(i);
                //获取列的值
                Object value = resultSet.getObject(columnName);

                //使用反射或内省，根据数据库表和实体的映射关系，完成封装，需要注意：数据库字段名与pojo实体的属性名一致。
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,resultTypeClass);
                //获取到写方法
                Method writeMethod = propertyDescriptor.getWriteMethod();
                //将value值写入resultType中
                writeMethod.invoke(resultTypeObject,value);
            }

            list.add(resultTypeObject);

        }
        return (List<E>) list;
    }

    /**
     * 根据参数的全路径，通过反射技术获取到对应的class
     * @param paramterType
     * @return
     */
    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {

        if (paramterType != null) {
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }

        return null;
    }

    /**
     * 转换sql，转成BoundSql对象
     *
     * 完成#{}的解析工作：
     *  1.使用 ? 代替 #{}
     *  2.解析出 #{} 中的值存入BoundSql对象
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配合标记解析器完成对占位符的解析处理工作
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{","}",tokenHandler);
        //解析出来的sql
        sql = tokenParser.parse(sql);
        //获取解析出来的占位符#{} 中的参数名称
        List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();


        BoundSql boundSql = new BoundSql(sql,parameterMappings);
        return boundSql;
    }
}
