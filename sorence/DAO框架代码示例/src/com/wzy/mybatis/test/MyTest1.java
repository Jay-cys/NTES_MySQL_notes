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
        //mybatis�����ļ�·��
        String resource = "conf.xml";
        //ʹ��mybatis�ṩ��Resources�����mybatis�������ļ�
        Reader reader = Resources.getResourceAsReader(resource); 
        //ʵ�廯sqlSession������
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        //�¿�session����������
        SqlSession session = sessionFactory.openSession();
        
        try{
//        	//ָ��ӳ���ļ���Ӧ��getUser�����Ľڵ���
           String mapsql = "com.wzy.mybatis.mapping.userMapper.getUser";
//            //����getUser������Ӧ��sqlִ�в�ѯ����һ��Ψһuser����
            User user = session.selectOne(mapsql, 1);
//            System.out.println(user);
            

            
//            System.out.println("=====================");
//            //�����û�
//            User newuser = new User();
//            newuser.setAge(18);
//            newuser.setName("feng");
//            mapsql = "com.wzy.mybatis.mapping.userMapper.addUser";
//            session.insert(mapsql,newuser);
//            session.commit();
//            System.out.println("insert user success");
            
           
            
//            System.out.println("=====================");
//            //�����û�
//            user.setAge(50);
//            mapsql = "com.wzy.mybatis.mapping.userMapper.updateUser";
//            session.update(mapsql,user);
//            session.commit();
//            System.out.println("update user success");
            
           
            
//            System.out.println("=====================");
//            //ɾ���û�
//            mapsql = "com.wzy.mybatis.mapping.userMapper.deleteUser";
//            session.update(mapsql,4);
//            session.commit();
//            System.out.println("delete user success");
//            
//            System.out.println("=====================");
//            //��ȡһ��User���� List<User>
//            mapsql = "com.wzy.mybatis.mapping.userMapper.getUserList";
//            List<User> userlst = session.selectList(mapsql);
//            for(User myuser:userlst){
//            	System.out.println(myuser);
//            }
            
            System.out.println("=====================");
            //�����ѯ
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
