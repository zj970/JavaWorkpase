package com.cn.shop.dto;


/*�û����ݴ����*/

public class UserDTO {

	
	private String name; //�û���
	
	private  int password1; //����
	
	private int password2; //ȷ������
	
	private String address; //��ַ
	
	private String postCode; //�ʱ�
	
	private String email; //email��ַ
	
	private String homePhone;// ��ͥ�绰����
	
	private String cellPhone; //�ֻ�����
	
	private String officePhone; //�칫�ҵ绰����
	
	
	private String rand;
	
	

	public String getRand() {
		return rand;
	}

	public void setRand(String rand) {
		this.rand = rand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPassword1() {
		return password1;
	}

	public void setPassword1(int password1) {
		this.password1 = password1;
	}

	public int getPassword2() {
		return password2;
	}

	public void setPassword2(int password2) {
		this.password2 = password2;
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
	
	
	
	
	
	
	
	
}
