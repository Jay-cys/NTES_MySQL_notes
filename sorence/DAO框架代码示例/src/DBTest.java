import java.sql.*;

/**
 * 使用JDBC连接MySQL
 */
public class DBTest {
    
    public static Connection getConnection() throws SQLException, 
            java.lang.ClassNotFoundException 
    {
        //第一步：加载MySQL的JDBC的驱动
        Class.forName("com.mysql.jdbc.Driver");
        
        //设置MySQL连接字符串,要访问的MySQL数据库 ip,端口,用户名,密码
        String url = "jdbc:mysql://localhost:3306/blog";        
        String username = "blog_user";
        String password = "blog_pwd";
        
        //第二步：创建与MySQL数据库的连接类的实例
        Connection con = DriverManager.getConnection(url, username, password);        
        return con;        
    }
    
    
    public static void main(String args[]) {
    	Connection con = null;
        try
        {
            //第三步：获取连接类实例con，用con创建Statement对象类实例 sql_statement
        	con = getConnection();            
        	Statement sql_statement = con.createStatement();
            
            /************ 对数据库进行相关操作 ************/                
            //如果同名数据库存在，删除
            sql_statement.executeUpdate("drop table if exists user;");            
            //执行了一个sql语句生成了一个名为user的表
            sql_statement.executeUpdate("create table user (id int not null auto_increment," +
            		" name varchar(20) not null default 'name', age int not null default 0, primary key (id) ); ");
            
            //向表中插入数据
            System.out.println("JDBC 插入操作:");
            String sql = "insert into user(name,age) values('liming', 18)";
            
            int num = sql_statement.executeUpdate("insert into user(name,age) values('liming', 18)");
            System.out.println("execute sql : " + sql);
            System.out.println(num + " rows has changed!");
            System.out.println("");
            
            //第四步：执行查询，用ResultSet类的对象，返回查询的结果
            String query = "select * from user";            
            ResultSet result = sql_statement.executeQuery(query);
			
            /************ 对数据库进行相关操作 ************/
            
            System.out.println("JDBC 查询操作:");
            System.out.println("------------------------");
            System.out.println("userid" + " " + "name" + " " + "age ");
            System.out.println("------------------------");
            
            //对获得的查询结果进行处理，对Result类的对象进行操作
            while (result.next()) 
            {
                int userid	=	result.getInt("id");
                String name	=	result.getString("name");
                int age		=	result.getInt("age");
                //取得数据库中的数据
                System.out.println(" " + userid + " " + name + " " + age);                
            }
            
            //关闭 result,sql_statement
            result.close();
            sql_statement.close();
            
            //使用PreparedStatement更新记录
            sql = "update user set age=? where name=?;"; 
            PreparedStatement pstmt = con.prepareStatement(sql); 
            
            //设置绑定变量的值
            pstmt.setInt(1, 15); 
            pstmt.setString(2, "liming"); 
            
            //执行操作
            num = pstmt.executeUpdate(); 
            
            System.out.println("");
            System.out.println("JDBC 更新操作:");
            System.out.println("execute sql : " + sql);
            System.out.println(num + " rows has changed!");
            
            //关闭PreparedStatement
            pstmt.close();
            
            
            //流式读取result，row-by-row
            query = "select * from user";            
            PreparedStatement ps = (PreparedStatement) con.prepareStatement
            (query,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);  
            
			ps.setFetchSize(Integer.MIN_VALUE);  
			
			result = ps.executeQuery();  
			
            /************ 对数据库进行相关操作 ************/
            
            System.out.println("JDBC 查询操作:");
            System.out.println("------------------------");
            System.out.println("userid" + " " + "name" + " " + "age ");
            System.out.println("------------------------");
            
            //对获得的查询结果进行处理，对Result类的对象进行操作
            while (result.next()) 
            {
                int userid	=	result.getInt("id");
                String name	=	result.getString("name");
                int age		=	result.getInt("age");
                //取得数据库中的数据
                System.out.println(" " + userid + " " + name + " " + age);                
            }
            
            //关闭 result,ps
            result.close();
            ps.close();
            con.close();
            
        } catch(java.lang.ClassNotFoundException e) {
            //加载JDBC错误,所要用的驱动没有找到
            System.err.print("ClassNotFoundException");
            //其他错误
            System.err.println(e.getMessage());
        } catch (SQLException ex) {
            //显示数据库连接错误或查询错误
            System.err.println("SQLException: " + ex.getMessage());
        }
	
		
    }

}