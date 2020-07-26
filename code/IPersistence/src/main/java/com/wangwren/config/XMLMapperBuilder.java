package com.wangwren.config;

import com.wangwren.pojo.Configuration;
import com.wangwren.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 解析mapper.xml封装至mappedStatement
     * @param mapperInputStream
     */
    public void parse(InputStream mapperInputStream) throws DocumentException {
        Document document = new SAXReader().read(mapperInputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectElement = rootElement.selectNodes("//select");

        for (Element element : selectElement) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sql = element.getTextTrim();

            //封装至mappedStatement
            MappedStatement statement = new MappedStatement();
            statement.setId(id);
            statement.setResultType(resultType);
            statement.setParamterType(paramterType);
            statement.setSql(sql);

            //封装至configuration
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key,statement);
        }
    }
}
