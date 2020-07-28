package com.wangwren.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wangwren.io.Resource;
import com.wangwren.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 解析Configuration,并封装
 */
public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder(){
        configuration = new Configuration();
    }

    /**
     * 使用dom4j解析
     * @param inputStream
     * @return
     */
    public Configuration parse(InputStream inputStream) throws DocumentException, PropertyVetoException {

        //使用dom4j读取输入流，得到document
        Document document = new SAXReader().read(inputStream);

        //获取到根节点 <configuration>
        Element rootElement = document.getRootElement();
        //使用xpath表达式// 选取节点，从根目录下选取proprety节点信息,数据库信息
        List<Element> propretyElements = rootElement.selectNodes("//proprety");
        Properties properties = new Properties();
        for (Element element : propretyElements) {
            //获取节点属性值
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");

            //这就是key-value的形式，使用properties存储
            properties.setProperty(name,value);
        }

        //properties中存放的是数据源信息
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(properties.getProperty("driverClass"));
        dataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        dataSource.setUser(properties.getProperty("user"));
        dataSource.setPassword(properties.getProperty("password"));

        //configuration设置数据源信息
        configuration.setDataSource(dataSource);


        //读取mapper信息
        List<Element> mapperElements = rootElement.selectNodes("//mapper");
        for (Element mapperElement : mapperElements) {
            //获取到mapper的路径
            String resource = mapperElement.attributeValue("resource");
            InputStream mapperInputStream = Resource.getResourceAsStream(resource);
            //解析mapper，封装mappedStatement，还要放到configuration中
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);

            xmlMapperBuilder.parse(mapperInputStream);
        }

        return configuration;
    }
}
