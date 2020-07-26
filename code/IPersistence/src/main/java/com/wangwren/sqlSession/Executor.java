package com.wangwren.sqlSession;

import com.wangwren.pojo.Configuration;
import com.wangwren.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement,Object... params) throws SQLException, Exception;
}
