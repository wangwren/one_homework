package com.wangwren.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     * @param <E>
     * @return
     */
    <E> List<E> selectList(String statementId,Object... params) throws Exception;

    /**
     * 查询单个
     * @param <E>
     * @return
     */
    <E> E selectOne(String statementId,Object... params) throws Exception;


    /**
     * 动态代理
     */
     <T> T getMapper(Class<?> mapperClass);
}
