/*
 * rthree.java
 *
 * Created on __DATE__, __TIME__
 */

package refer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author  __USER__
 */
public class rthree extends javax.swing.JFrame {

	/** Creates new form rthree */
	public rthree() {
		initComponents();
		int x, y;
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		x = (size.width - 950) / 2;
		y = (size.height - 600) / 2;
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

		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		jButton2 = new javax.swing.JButton();
		jTextField1 = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		jComboBox1 = new javax.swing.JComboBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null, null } },
				new String[] { "姓名", "联系电话", "家庭住址", "宠物信息", "购买时间" }));
		jScrollPane1.setViewportView(jTable1);

		jButton2.setText("\u9000\u51fa");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jButton1.setText("\u67e5\u8be2");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"全部查询", "关键字" }));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										469, Short.MAX_VALUE).addContainerGap())
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGap(65, 65, 65)
								.addComponent(jButton2)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										23, Short.MAX_VALUE)
								.addComponent(jComboBox1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jTextField1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										123,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18).addComponent(jButton1)
								.addGap(44, 44, 44)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 534, Short.MAX_VALUE)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										464,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jButton1)
												.addComponent(jButton2)
												.addComponent(
														jTextField1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														jComboBox1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(32, 32, 32)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:	Vector data = new Vector();
		Vector<Object> v = new Vector();
		Vector data = new Vector();
		Vector names = new Vector();
		names.add("姓名");
		names.add("联系电话");
		names.add("家庭住址");
		names.add("宠物信息");
		names.add("购买时间");

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			//注册驱动
			Class.forName("com.mysql.jdbc.Driver");
			//建立连接
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/pet?useSSL=false", "root",
					"admin");
			//3.创建语句
			st = conn.createStatement();
			//4.执行语句 
			String a = this.jTextField1.getText();
			if (this.jComboBox1.getSelectedIndex() == 0) {
			rs = st.executeQuery("select * from three");
			}
			if (this.jComboBox1.getSelectedIndex() == 1) {
				rs = st.executeQuery("select * from three where 购买人 like'" + a
						+ "%'");
				}
			//5.处理结果 
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getObject(2)
						+ "\t" + rs.getObject(3) + "\t" + rs.getObject(4)
						+ "\t" + rs.getObject(5));

				v.clear();
				v.add(rs.getString(1));
				v.add(rs.getString(2));
				v.add(rs.getString(3));
				v.add(rs.getString(4));
				v.add(rs.getString(5));
				data.add(v.clone());
				jTable1.setModel(new javax.swing.table.DefaultTableModel(data,
						names));

			}

		} catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		}
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.dispose();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new rthree().setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	private javax.swing.JTextField jTextField1;
	// End of variables declaration//GEN-END:variables

}