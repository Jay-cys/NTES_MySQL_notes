import java.sql.*;

/**
 * ʹ��JDBC����MySQL
 */
public class DBTest {
    
    public static Connection getConnection() throws SQLException, 
            java.lang.ClassNotFoundException 
    {
        //��һ��������MySQL��JDBC������
        Class.forName("com.mysql.jdbc.Driver");
        
        //����MySQL�����ַ���,Ҫ���ʵ�MySQL���ݿ� ip,�˿�,�û���,����
        String url = "jdbc:mysql://localhost:3306/blog";        
        String username = "blog_user";
        String password = "blog_pwd";
        
        //�ڶ�����������MySQL���ݿ���������ʵ��
        Connection con = DriverManager.getConnection(url, username, password);        
        return con;        
    }
    
    
    public static void main(String args[]) {
    	Connection con = null;
        try
        {
            //����������ȡ������ʵ��con����con����Statement������ʵ�� sql_statement
        	con = getConnection();            
        	Statement sql_statement = con.createStatement();
            
            /************ �����ݿ������ز��� ************/                
            //���ͬ�����ݿ���ڣ�ɾ��
            sql_statement.executeUpdate("drop table if exists user;");            
            //ִ����һ��sql���������һ����Ϊuser�ı�
            sql_statement.executeUpdate("create table user (id int not null auto_increment," +
            		" name varchar(20) not null default 'name', age int not null default 0, primary key (id) ); ");
            
            //����в�������
            System.out.println("JDBC �������:");
            String sql = "insert into user(name,age) values('liming', 18)";
            
            int num = sql_statement.executeUpdate("insert into user(name,age) values('liming', 18)");
            System.out.println("execute sql : " + sql);
            System.out.println(num + " rows has changed!");
            System.out.println("");
            
            //���Ĳ���ִ�в�ѯ����ResultSet��Ķ��󣬷��ز�ѯ�Ľ��
            String query = "select * from user";            
            ResultSet result = sql_statement.executeQuery(query);
			
            /************ �����ݿ������ز��� ************/
            
            System.out.println("JDBC ��ѯ����:");
            System.out.println("------------------------");
            System.out.println("userid" + " " + "name" + " " + "age ");
            System.out.println("------------------------");
            
            //�Ի�õĲ�ѯ������д�����Result��Ķ�����в���
            while (result.next()) 
            {
                int userid	=	result.getInt("id");
                String name	=	result.getString("name");
                int age		=	result.getInt("age");
                //ȡ�����ݿ��е�����
                System.out.println(" " + userid + " " + name + " " + age);                
            }
            
            //�ر� result,sql_statement
            result.close();
            sql_statement.close();
            
            //ʹ��PreparedStatement���¼�¼
            sql = "update user set age=? where name=?;"; 
            PreparedStatement pstmt = con.prepareStatement(sql); 
            
            //���ð󶨱�����ֵ
            pstmt.setInt(1, 15); 
            pstmt.setString(2, "liming"); 
            
            //ִ�в���
            num = pstmt.executeUpdate(); 
            
            System.out.println("");
            System.out.println("JDBC ���²���:");
            System.out.println("execute sql : " + sql);
            System.out.println(num + " rows has changed!");
            
            //�ر�PreparedStatement
            pstmt.close();
            
            
            //��ʽ��ȡresult��row-by-row
            query = "select * from user";            
            PreparedStatement ps = (PreparedStatement) con.prepareStatement
            (query,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);  
            
			ps.setFetchSize(Integer.MIN_VALUE);  
			
			result = ps.executeQuery();  
			
            /************ �����ݿ������ز��� ************/
            
            System.out.println("JDBC ��ѯ����:");
            System.out.println("------------------------");
            System.out.println("userid" + " " + "name" + " " + "age ");
            System.out.println("------------------------");
            
            //�Ի�õĲ�ѯ������д�����Result��Ķ�����в���
            while (result.next()) 
            {
                int userid	=	result.getInt("id");
                String name	=	result.getString("name");
                int age		=	result.getInt("age");
                //ȡ�����ݿ��е�����
                System.out.println(" " + userid + " " + name + " " + age);                
            }
            
            //�ر� result,ps
            result.close();
            ps.close();
            con.close();
            
        } catch(java.lang.ClassNotFoundException e) {
            //����JDBC����,��Ҫ�õ�����û���ҵ�
            System.err.print("ClassNotFoundException");
            //��������
            System.err.println(e.getMessage());
        } catch (SQLException ex) {
            //��ʾ���ݿ����Ӵ�����ѯ����
            System.err.println("SQLException: " + ex.getMessage());
        }
	
		
    }

}