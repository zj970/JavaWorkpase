package com.cn.shop.dto;

import java.io.File;

public class ProductDTO {
	
	private String name; //��Ʒ����
	
	private Double price; //�۸�
	
	private String description; //��Ʒ����
	
	private File file;  //ͼƬ

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	
	
	
	
	

}
