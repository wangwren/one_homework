package com.wangwren.sqlSession;

import com.wangwren.config.XMLConfigBuilder;
import com.wangwren.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuild {

    /**
     * 根据输入流解析配置文件内容，并返回session工厂
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream) throws PropertyVetoException, DocumentException {

        //1.使用dom4j解析文件，将解析的内容封装至Configuration
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parse(inputStream);

        //2.生成sqlSessionFactory对象，即生成工厂，工厂再生成sqlSession
        SqlSessionFactory sessionFactory = new DefaultSqlSessionFactory(configuration);

        return sessionFactory;
    }

}
