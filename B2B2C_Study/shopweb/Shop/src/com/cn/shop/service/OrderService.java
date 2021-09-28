package com.cn.shop.service;

import java.util.List;

import com.cn.shop.model.Order;

public interface OrderService {
	
	//���ɶ���
	
	public String generateOrder( Order order );
	
	//������ж���
	
	public List<Order> getAllOrder( );
	
	//ͨ��ID��ö�������
	
	public Order getOrderById( long id );
	
	//��������״̬ת����һ��
	
	public void nextOrderStatus( long id );
	
	
	

}
