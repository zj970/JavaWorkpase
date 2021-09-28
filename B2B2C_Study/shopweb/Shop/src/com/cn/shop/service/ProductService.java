package com.cn.shop.service;

import java.util.List;
import java.util.Map;

import com.cn.shop.model.Product;

public interface ProductService {
	
	//ͨ����Ʒid�����Ʒ����
	public Product getProduct( long productId );
	
	//������е���Ʒ
	
	public List<Product> getALLProducts( );
	
	//2014.8.28 ��Ʒ����ɾ�Ĳ�
	
	public void addProduct( Product product );
	
	public void deleteProduct( long productId );
	
	public void UpdateProduct( Product product );
	
	//��ҳ��ʾ 2014.8.29
	
	public List<Product> getByPage( int pageNo, int pageSize  );
	
	public int maxSize( );
	
	//ģ����ѯ
	
	public List<Product> find ( Map<String , String >keymap, int pageNo, int pageSize  );
	
	
	//��ѯ����
	
	public int findnum( Map<String , String >keymap );
	
	
	

}
