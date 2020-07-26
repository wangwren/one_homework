package com.wangwren.config;

import com.wangwren.util.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

public class BoundSql {

    private String sql;

    private List<ParameterMapping> parameterMappingList = new ArrayList<>();

    public BoundSql(String sql,List<ParameterMapping> parameterMappingList) {
        this.sql = sql;
        this.parameterMappingList = parameterMappingList;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }
}
