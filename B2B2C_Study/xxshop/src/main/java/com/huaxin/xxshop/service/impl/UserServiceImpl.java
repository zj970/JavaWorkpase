package com.huaxin.xxshop.service.impl;

import java.util.List;

import com.huaxin.xxshop.dao.UserDao;
import com.huaxin.xxshop.entity.User;
import com.huaxin.xxshop.service.UserService;
import com.huaxin.xxshop.util.XXShopUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserService 接口ude实现类
 * @author 没有蜡笔的小新 2015/12/21
 */

@Service("userService")
public class UserServiceImpl implements UserService {
	//@Resource("userDao")
	@Autowired
    private UserDao userDao = null;

//	// getter和setter
//	public UserDao getUserDao() {
//		return userDao;
//	}
//
//	public void setUserDao(UserDao userDao) {
//		this.userDao = userDao;
//	}


	@Override
	public void addLogin(User user){
		user.setLoginId(XXShopUtil.getId());
		user.setLoginTime(XXShopUtil.getNow());

		userDao.addLogin(user.getLoginId(),user.getIp(),user.getName(),user.getLoginTime());
	}

	@Override
	public void updateEmail(String id, String email) {
		userDao.updateEmail(id, email);
	}
	@Override
	public void updatePassword(String id, String password) {
		userDao.updatePassword(id, password);
	}

	@Override
	public void updatePhoneNum(String id, String phoneNum) {
		System.out.println("ServiceLmpl :"+phoneNum);
		userDao.updatePhoneNum(id, phoneNum);
	}

	@Override
	public void register(User user) {
		user.setId(XXShopUtil.getId());
		user.setRegTime(XXShopUtil.getNow());
		user.setRole("u");
		user.setMoney(0);
		userDao.addUser(user);
	}

//	@Override
//	public User login(String name, String password) {
//		return userDao.getUserByNameAndPwd(name, password);
//	}
	// 通过账号和密码查询用户
	@Override
	public User findUser(String name, String password) {
		User user = this.userDao.getUserByNameAndPwd(name, password);
		return user;
	}


	@Override
	public boolean isexist(String name) {
		int num = userDao.getNumByName(name);
		return num != 0;
	}

	@Override
	public void updateAvatar(String id, String avar) {
		userDao.updateAvatar(id, avar);
	}

	@Override
	public void updateMoney(String id, float money) {
		userDao.updateMoney(id, money);
	}

	@Override
	public List<User> getAllUser() {
		return userDao.getAllUser();
	}

	@Override
	public void deleteUser(String id) {
		userDao.deleteUser(id);
	}

	@Override
	public User getUser(String id) {
		return userDao.getUser(id);
	}

	@Override
	public void updateStatus(String id, int status) {
		userDao.updateStatus(id, status);
	}

	@Override
	public void updateMember(String memberId, Integer status, String memberId1, String password, String role) {
		userDao.updateStatus(memberId, status);
		userDao.updatePassword(memberId, password);
		userDao.updateRole(memberId, role);
	}
}
