/*
�����û���¼
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Login {

	Admin admin;
	
	void setAdmin(Admin admin) {
		this.admin=admin;
		//System.out.println(this.admin.getPassword()+"   " + this.admin.getID());
	}
	/*
	 * JudgeAdmin()����
	 * �ж�Admin��ID�������Ƿ���ȷ�������ȷ����ʾ��¼�ɹ�
	 * ������󣬵���һ�����ڣ���ʾ�˺Ż��������
	 */
	  private String driver = "com.mysql.jdbc.Driver";
	    private String url = "jdbc:mysql://localhost:3306/pet?useSSL=false";
	    private String user = "root";
	    private String sqlpassword = "admin";
    
    
	
	 public boolean login(Admin admin) throws SQLException, ClassNotFoundException {
	    	String sql="select * from admin where id=? and password=?";
	        
	    	Class.forName(driver);
	    	Connection conn = DriverManager.getConnection(url, user, sqlpassword);
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	
	        ps.setString(1, admin.getID());
	        ps.setString(2, admin.getPassword());
	        ResultSet rs = ps.executeQuery();
	        int ans = 0;
	        if(rs.next()) {
	        	ans = 1;
	        }  
	        rs.close();
	        ps.close();
	        conn.close();
	        if(ans == 1) {
	        	return true;
	        }
	        else return false;
	    }
	int JudgeAdmin() {
		
		    try {
		        if(login(this.admin)) {
		        	System.out.println("��¼�ɹ�");
		        	
		        	return 1;
		        }else {
		            return 0;
		        }
		    }catch(Exception e) {
		        //e.printStackTrace();
		    	//System.out.println("!!!!!!!!!");
		    }
		return 0;
		
	}	
}