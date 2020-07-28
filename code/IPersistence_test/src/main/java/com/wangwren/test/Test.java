package com.wangwren.test;

import com.wangwren.dao.IUserDao;
import com.wangwren.io.Resource;
import com.wangwren.pojo.User;
import com.wangwren.sqlSession.SqlSession;
import com.wangwren.sqlSession.SqlSessionFactory;
import com.wangwren.sqlSession.SqlSessionFactoryBuild;
import org.dom4j.DocumentException;
import org.junit.Before;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        InputStream inputStream = Resource.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactoryBuild sqlSessionFactoryBuild = new SqlSessionFactoryBuild();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuild.build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        /*List<User> users = sqlSession.selectList("user.selectList");

        for (User user : users) {
            System.out.println(user);
        }*/


        /*User param = new User();
        param.setId(1);
        param.setUsername("张三");
        User user = sqlSession.selectOne("user.selectOne",param);

        System.out.println(user);*/


        //JDK动态代理的形式，不需要写dao接口的实现了，通过动态代理去执行方法;得到的是dao的代理对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        //当代理对象调用方法时，会触发invoke方法，根据情况选择调用哪个方法
        /*List<User> users = userDao.findAll();

        for (User user : users) {
            System.out.println(user);
        }*/

        User user = new User();
        user.setId(1);
        user.setUsername("张三");

        User user1 = userDao.findByCondition(user);
        System.out.println(user1);


        //inser方法
        /*User user = new User();
        user.setId(4);
        user.setUsername("wangwu");

        sqlSession.inser("com.wangwren.dao.IUserDao.addUser",user);*/

    }

    private SqlSession sqlSession;
    private IUserDao userDao;

    @Before
    public void before() throws PropertyVetoException, DocumentException {
        InputStream inputStream = Resource.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactoryBuild sqlSessionFactoryBuild = new SqlSessionFactoryBuild();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuild.build(inputStream);
        this.sqlSession = sqlSessionFactory.openSession();
        this.userDao = sqlSession.getMapper(IUserDao.class);
    }

    /**
     * 测试查询所有
     */
    @org.junit.Test
    public void selectAllTest() {
        List<User> all = userDao.findAll();
        for (User user : all) {
            System.out.println(user);
        }
    }

    /**
     * 根据条件查询
     */
    @org.junit.Test
    public void selectCondition() {
        User param = new User();
        param.setId(2);
        param.setUsername("zhangsan");

        User user = userDao.findByCondition(param);
        System.out.println(user);
    }

    /**
     * 测试插入
     */
    @org.junit.Test
    public void insertTest() {
        User param = new User();
        param.setId(4);
        param.setUsername("wangwu");

        userDao.addUser(param);
    }


    /**
     * 测试更新
     */
    @org.junit.Test
    public void updateTest() throws Exception {

        User param = new User();
        param.setId(4);
        param.setUsername("Tom");

        userDao.updateUserById(param);

    }


    /**
     * 测试删除
     */
    @org.junit.Test
    public void deleteTest() throws Exception {

        User param = new User();
        param.setId(4);

        userDao.deleteUserById(param);
    }

}
