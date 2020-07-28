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

        //解析select标签
        parse4Select(rootElement);

        //解析insert标签
        parse4Insert(rootElement);

        //解析update标签
        parse4Update(rootElement);

        //解析delete标签
        parse4Delete(rootElement);

    }


    /**
     * 解析select标签
     * @param rootElement
     */
    private void parse4Select(Element rootElement) {

        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectElement = rootElement.selectNodes("//select");

        //封装标签
        packageConfig(namespace, selectElement);
    }

    /**
     * 解析insert标签
     * @param rootElement
     */
    private void parse4Insert(Element rootElement) {

        String namespace = rootElement.attributeValue("namespace");
        List<Element> insertElement = rootElement.selectNodes("//insert");

        packageConfig(namespace,insertElement);
    }

    /**
     * 解析updte标签
     * @param rootElement
     */
    private void parse4Update(Element rootElement) {
        String namespace = rootElement.attributeValue("namespace");
        List<Element> updateElement = rootElement.selectNodes("//update");

        packageConfig(namespace,updateElement);
    }

    /**
     * 解析delete标签
     * @param rootElement
     */
    private void parse4Delete(Element rootElement) {
        String namespace = rootElement.attributeValue("namespace");
        List<Element> deleteElement = rootElement.selectNodes("//delete");

        packageConfig(namespace,deleteElement);
    }

    /**
     * 封装标签至MappedStatement和Configuration
     * @param namespace
     * @param elements
     */
    private void packageConfig(String namespace, List<Element> elements) {
        for (Element element : elements) {
            String sqlType = element.getName();
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sql = element.getTextTrim();

            //封装至mappedStatement
            MappedStatement statement = new MappedStatement();
            statement.setSqlType(sqlType);
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
