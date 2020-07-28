package com.wangwren.pojo;

/**
 * 存放解析mapper.xml的内容
 */
public class MappedStatement {

    private String id;

    /**
     * 返回的类型
     */
    private String resultType;

    /**
     * 参数类型
     */
    private String paramterType;

    /**
     * sql语句
     */
    private String sql;

    /**
     * sql语句类型：select insert update delete
     */
    private String sqlType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamterType() {
        return paramterType;
    }

    public void setParamterType(String paramterType) {
        this.paramterType = paramterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }
}
