package com.cn.shop.dao;

import com.cn.shop.model.User;

public interface UserDAO extends BaseDAO  {
	
	// ��¼��֤�����ʧ�ܷ���null
		public User loginCheck(String name, String password);
		
		public void save( User user );
		

}
