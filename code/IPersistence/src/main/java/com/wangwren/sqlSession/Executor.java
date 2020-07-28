package com.wangwren.sqlSession;

import com.wangwren.pojo.Configuration;
import com.wangwren.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement,Object... params) throws Exception;

    int doUpdate(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;
}
