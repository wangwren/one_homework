package com.wangwren.dao;

import com.wangwren.pojo.User;

import java.util.List;

public interface IUserDao {

    /**
     * 查询所有
     * @return
     */
    List<User> findAll();

    /**
     * 根据条件查询
     * @param user
     * @return
     */
    User findByCondition(User user);

    /**
     * 插入
     * @param user
     */
    void addUser(User user);

    /**
     * 根据id更新
     * @param user
     */
    void updateUserById(User user);

    /**
     * 根据id删除
     * @param user
     */
    void deleteUserById(User user);

}
