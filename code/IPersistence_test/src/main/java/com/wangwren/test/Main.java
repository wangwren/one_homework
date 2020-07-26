package com.wangwren.test;


import com.wangwren.pojo.User;

import java.sql.*;

/**
 * 原始JDBC的操作
 */
public class Main {

    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        try {

            //加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            //通过驱动管理类获取数据库连接
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf-8"
                    ,"root"
                    ,"root");

            //定义sql语句，? 表示占位符
            String sql = "select * from user where id = ?";

            //获取预处理对象statement
            statement = connection.prepareStatement(sql);
            //设置sql语句中的参数,第一个参数表示sql语句中参数的序号(从1开始);第二个参数表示对应的值
            statement.setInt(1,1);

            //执行sql，返回结果集
            resultSet = statement.executeQuery();

            //遍历结果集，封装对象
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");

                User user = new User();
                user.setId(id);
                user.setUsername(username);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
