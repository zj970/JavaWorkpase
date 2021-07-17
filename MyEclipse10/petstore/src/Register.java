import java.sql.*;
import javax.swing.JOptionPane;

public class Register {
    String name;
    String ID;
    String password;
    String confirmpassword;
    
    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/pet?useSSL=false";
    private String user = "root";
    private String sqlpassword = "admin";
    
    void setName(String name) {
        this.name = name;
    }
    void setID(String ID) {
        this.ID = ID;
    }
    void setPassword(String password) {
        this.password = password;
    }
    void setconfirmpasswd(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }
    
    
    //�ж�ע����˺��Ƿ���Ϲ���
    boolean JudgeRegister() throws SQLException, ClassNotFoundException {
        
        if(this.name.equals("")) {
            JOptionPane.showMessageDialog(null, "�û�������Ϊ�գ�", "�û���", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(this.ID.equals("")) {
            JOptionPane.showMessageDialog(null, "�˺Ų���Ϊ�գ�", "�˺�Ϊ��", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(this.password.equals("")) {
            JOptionPane.showMessageDialog(null, "���벻��Ϊ�գ�", "����Ϊ��", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(!this.password.equals(this.confirmpassword)) {
            JOptionPane.showMessageDialog(null, "������������벻һ��!", "���벻һ��", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        //���Ϲ��򣬵���ע��ɹ��Ĵ��ڣ������˺�������ݿ�
        JOptionPane.showMessageDialog(null, "ע��ɹ�");
        addAdmin();
        return true;
    }
    
    //�����ݿ����Admin�˻�
    void addAdmin() throws ClassNotFoundException, SQLException {
    	String sql="insert into admin (id, name, password) values (?,?,?)";
    	Class.forName(driver);
    	try {
	    	Connection conn = DriverManager.getConnection(url, user, sqlpassword);
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setString(1, this.ID);
	        ps.setString(2, this.name);
	        ps.setString(3, this.password);
	        ps.executeUpdate();
	        ps.close();	
	        conn.close();
	        
    	}catch(SQLException ex) {
    		System.out.println("����û�ʧ�ܣ�");
    	}
    	
    }
}