import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Login_Register extends JFrame{    
	
	
	Login_Register() {
		init();
		
	}
	//��¼�����ʼ��
	public void init() {
	final JFrame frame = new JFrame("��¼");
        frame.setLayout(null);
        
        JLabel nameStr = new JLabel("�˺�:");
        nameStr.setBounds(250, 200, 100, 25);
        frame.add(nameStr);
        
        JLabel passwordStr = new JLabel("����:");
        passwordStr.setBounds(250, 250, 100, 25);
        frame.add(passwordStr);  
        
        final JTextField userID = new JTextField();
        userID.setBounds(300, 200, 150, 25);
        frame.add(userID);
        
        final JPasswordField password = new JPasswordField();
        password.setBounds(300, 250, 150, 25);
        frame.add(password);
        
        JButton buttonlogin = new JButton("��¼");
        buttonlogin.setBounds(275, 300, 70, 25);
        frame.add(buttonlogin);
        
        JButton buttonregister = new JButton("ע��");
        buttonregister.setBounds(375, 300, 70, 25);
        frame.add(buttonregister);  
        
        frame.setBounds(400, 100, 800, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        //Ϊ��¼��ť��Ӽ�����
         buttonlogin.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {
                String ID = userID.getText();
                String passwd = new String (password.getPassword());
                  
                //����һ��Admin�û�����������е��û�������������
                Admin admin = new Admin();
                admin.setID(ID);
                admin.setPassword(passwd);
                
                //��¼
                Login login = new Login();
                login.setAdmin(admin);
          
                if(login.JudgeAdmin()==0) {
                	//�����˺Ż��������Ĵ���
                	JOptionPane.showMessageDialog(null, "�˺Ż��������", "�˺Ż��������", JOptionPane.WARNING_MESSAGE);
                	//���������е���Ϣ
                	password.setText("");
                	//����˺ſ��е���Ϣ
                	userID.setText("");
                	
                	//System.out.println("��½ʧ��");
                }  
                else {
                	//������¼�ɹ��Ĵ���
                	JOptionPane.showMessageDialog(null, "��½�ɹ�", "��½�ɹ�", JOptionPane.NO_OPTION);
                	//���ȷ�������ת��������
                	frame.setVisible(false);
                    deng ar = new deng();
                    ar.show();
                    
                        
                }
            }
            
        });
         
         //Ϊע�ᰴť��Ӽ�����
         buttonregister.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent e) {
        		 //ע��ҳ��
                 frame.setVisible(false);
        		 AdminRegister ar = new AdminRegister(); 
        	 }
         });
	}
	
    public static void main(String []args) { 
       //������
       //��¼����
    	Login_Register login_register = new Login_Register();
    }
}