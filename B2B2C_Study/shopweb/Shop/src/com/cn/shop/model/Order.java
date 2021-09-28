package com.cn.shop.model;

import java.util.HashMap;
import java.util.Map;




import com.cn.shop.common.Constants;


public class Order {
	
	private Long id; //����
	
	private String orderNum; //������
	
	private Integer status; //����״̬
	
	private User user; //�����û�
	
	private Double cost; //�ܼ�
	
	private Map<Long,Item > items = new HashMap<Long, Item >( ); //�����Ķ������ƷidΪkey

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	//����״ֵ̬�������û��Ĺ��췽��
	public Order( Integer status , User user )
	{
		super( );
		
		this.status = status;
		
		this.user = user;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}


	
	public Integer getStatus( ) {
		return  status;
	}
	
/*	public String  getStatus( ) {
		
		String text = null;
		switch (status) {
		case Constants.ORDER_STATUS_POST:
			text = "�������ύ";
			break;
		case Constants.ORDER_STATUS_SEND:
			text = "�ѷ���";
			break;
		case Constants.ORDER_STATUS_RECEIVE:
			text = "�û����ջ�";
			break;
		case Constants.ORDER_STATUS_END:
			text = "�������";
			break;
		default:
			break;
		}
		return text;
	 
	}
	 */
	

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getCost() {
		
		double sum = 0;
		
		for( Long id : items.keySet() )
		{
			Item item = items.get(id);
			
			sum += item.getCost();
		}
		
		this.cost = sum;
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Map<Long, Item> getItems() {
		return items;
	}

	public void setItems(Map<Long, Item> items) {
		this.items = items;
	}
	
	
	
	

}
