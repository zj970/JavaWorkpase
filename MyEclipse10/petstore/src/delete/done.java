/*
 * done.java
 *
 * Created on __DATE__, __TIME__
 */

package delete;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.*;

/**
 *
 * @author  __USER__
 */
public class done extends javax.swing.JFrame {

	/** Creates new form done */
	public done() {
		initComponents();
		int x, y;
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		x = (size.width - 550) / 2;
		y = (size.height - 1000) / 2;
		setSize(550, 600);
		setLocation(x, y);
		//让程序界面显示在屏幕中央
		setMinimumSize(new Dimension(250, 150));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();
		jTextField1 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 2, 24));
		jLabel1.setForeground(new java.awt.Color(255, 51, 51));
		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/com/sun/java/swing/plaf/motif/icons/Inform.gif"))); // NOI18N
		jLabel1.setText("\u5220\u9664\u4fe1\u606f");

		jLabel3.setText("\u540d\u5b57");

		jLabel2.setText("\u7f16\u53f7");

		jButton1.setIcon(new javax.swing.ImageIcon(
				"F:\\Workspaces\\MyEclipse10\\petstore\\src\\images\\delete.png")); // NOI18N
		jButton1.setText("\u786e\u8ba4");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(302, Short.MAX_VALUE)
								.addComponent(jButton1).addContainerGap())
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(145, Short.MAX_VALUE)
								.addComponent(jLabel1).addGap(143, 143, 143))
				.addGroup(
						layout.createSequentialGroup()
								.addGap(60, 60, 60)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(jLabel3)
												.addComponent(jLabel2))
								.addGap(71, 71, 71)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														jTextField2,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														171, Short.MAX_VALUE)
												.addComponent(
														jTextField1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														171, Short.MAX_VALUE))
								.addGap(68, 68, 68)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLabel1)
																.addGap(59, 59,
																		59)
																.addComponent(
																		jTextField1,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(jLabel2))
								.addGap(47, 47, 47)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel3)
												.addComponent(
														jTextField2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										122, Short.MAX_VALUE)
								.addComponent(jButton1).addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:Connection conn = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			//注册驱动
			Class.forName("com.mysql.jdbc.Driver");
			//建立连接
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/pet", "root", "admin");
			//3.创建语句
			st = conn.createStatement();
			String a = this.jTextField1.getText();
			String b = this.jTextField2.getText();
			String sql = "delete from one where 编号='" + a + "'or 名字='" + b
					+ "'";
			//4.执行语句
			st.executeUpdate(sql);
			System.out.print("操作成功!");
		} catch (Exception e) {
			System.out.print("操作失败!");
			e.printStackTrace();
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new done().setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	// End of variables declaration//GEN-END:variables

}