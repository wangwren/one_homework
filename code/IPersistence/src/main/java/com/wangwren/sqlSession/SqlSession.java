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
     * 插入数据
     * @param statementId
     * @param params
     * @throws Exception
     */
    int inser(String statementId,Object... params) throws Exception;

    /**
     * 更新语句
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    int update(String statementId,Object... params) throws Exception;

    /**
     * 删除语句
     * @param statementId
     * @param params
     * @return
     * @throws Exception
     */
    int delete(String statementId,Object... params) throws Exception;


    /**
     * 动态代理
     */
     <T> T getMapper(Class<?> mapperClass);
}
