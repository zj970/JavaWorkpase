package com.cn.shop.model;

import com.cn.shop.dto.UserDTO;



/*�û�*/

public class User {
	
	private Long id; //����   ������manager��������
	
	private String name; //�û���
	
	private  int password; //����
	
	private String address; //��ַ
	
	private String postCode; //�ʱ�
	
	private String email; //email��ַ
	
	private String homePhone;// ��ͥ�绰����
	
	private String cellPhone; //�ֻ�����
	
	private String officePhone; //�칫�ҵ绰����
	
	/*
	 * 
	 * manager����
	 * private String type;
	
	private String workNo;*/
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	//�����û���������Ĺ��췽��
	public User(String name, int password) {
		super();
		this.name = name;
		this.password = password;
	}
	
	//Id���ɲ��� ������


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	
	
	
	
	public void set( UserDTO userDTO )
	{
		this.name = userDTO.getName();
		
		this.password = userDTO.getPassword1();
		
		this.address = userDTO.getAddress();

		
		this.postCode = userDTO.getPostCode();
		
		this.email = userDTO.getEmail();
		
		this.homePhone = userDTO.getHomePhone();
		
		this.cellPhone = userDTO.getCellPhone();
		
		this.officePhone = userDTO.getOfficePhone();
	}
	
	

}
