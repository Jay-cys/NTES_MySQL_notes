package com.wzy.mybatis.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.wzy.mybatis.model.User;
import com.wzy.mybatis.model.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyTest1 {
	public static void main(String[] args) throws IOException {
        //mybatis配置文件路径
        String resource = "conf.xml";
        //使用mybatis提供的Resources类加载mybatis的配置文件
        Reader reader = Resources.getResourceAsReader(resource); 
        //实体化sqlSession工厂类
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        //新开session，建立连接
        SqlSession session = sessionFactory.openSession();
        
        try{
//        	//指定映射文件对应的getUser方法的节点名
           String mapsql = "com.wzy.mybatis.mapping.userMapper.getUser";
//            //根据getUser方法对应的sql执行查询返回一个唯一user对象
            User user = session.selectOne(mapsql, 1);
//            System.out.println(user);
            

            
//            System.out.println("=====================");
//            //新增用户
//            User newuser = new User();
//            newuser.setAge(18);
//            newuser.setName("feng");
//            mapsql = "com.wzy.mybatis.mapping.userMapper.addUser";
//            session.insert(mapsql,newuser);
//            session.commit();
//            System.out.println("insert user success");
            
           
            
//            System.out.println("=====================");
//            //更新用户
//            user.setAge(50);
//            mapsql = "com.wzy.mybatis.mapping.userMapper.updateUser";
//            session.update(mapsql,user);
//            session.commit();
//            System.out.println("update user success");
            
           
            
//            System.out.println("=====================");
//            //删除用户
//            mapsql = "com.wzy.mybatis.mapping.userMapper.deleteUser";
//            session.update(mapsql,4);
//            session.commit();
//            System.out.println("delete user success");
//            
//            System.out.println("=====================");
//            //获取一组User对象： List<User>
//            mapsql = "com.wzy.mybatis.mapping.userMapper.getUserList";
//            List<User> userlst = session.selectList(mapsql);
//            for(User myuser:userlst){
//            	System.out.println(myuser);
//            }
            
            System.out.println("=====================");
            //连表查询
            mapsql = "com.wzy.mybatis.mapping.userMapper.getBlogListByUid";
            int userid = 2;
            List<Blog> bloglst = session.selectList(mapsql,userid);
            for(Blog myblog:bloglst){
            	System.out.println(myblog);
            }
            
            
        }
        finally
        {
        	session.close();
        }
        
        
        
        
        
    }
}
