package com.wangwren.dao;

import com.wangwren.pojo.User;

import java.util.List;

public interface IUserDao {

    List<User> findAll();


    User findByCondition(User user);

}
