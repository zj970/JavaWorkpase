package cn.sdut.dao;
 
import  java.sql.*;

import java.sql.Statement;
 
public class BaseDao {
   // 1 �������ݿ���ʹ�������
   Connection con;
   PreparedStatement pst;
   ResultSet rs;
   // 2 �������ݿ�����ӷ���
   public Connection getConn() {
      // 1 ����jdbc����
 
      try {
         Class.forName("com.mysql.jdbc.Driver");
      }catch(ClassNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Connection con = null;
	// 2 �õ����ݿ������
      try {
    	  con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/school?useSSL=false", "root", "admin");
      }catch(Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return con;
   }
 
   // 3 �ر�conn,pst,rs
   public void closeAll() {
      try {
		if (rs !=null) {
			rs.close();
         }
         if (pst !=null) {
            pst.close();
         }
         if (con !=null) {
            ((Connection) con).close();
         }
      }catch(SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}