package com.wangwren.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放sqlMapConfig.xml解析的内容
 */
public class Configuration {

    /**
     * 存放数据源相关
     */
    private DataSource dataSource;

    /**
     * key：namespace.id
     * value: mapper.xml中的每一个sql对应的信息
     *
     * 根据key来对应每一个sql语句相关的信息
     */
    private Map<String,MappedStatement> mappedStatementMap = new HashMap<>(16);

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
