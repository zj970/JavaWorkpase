package com.cn.shop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



public class Product {
	
	private Long id; // ����
	
	private String name; //��Ʒ����
	
	private String description; //��Ʒ����
	
	private String imageSrc; //ͼƬ��ַ
	
	private Double price; //�۸� 

	//�޲ι��캯��
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	//������Ʒ����
	public Product(String name) {
		super();
		this.name = name;
	}



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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	
	
	
	

}
