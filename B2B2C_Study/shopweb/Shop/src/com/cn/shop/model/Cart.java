package com.cn.shop.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

//���ﳵ
public class Cart {
	
	//����һ����־
	private Logger logger = Logger.getLogger(this.getClass());
	
	//��Ʒid ��Ŀ��¼
	private Map< Long, Item > items = new HashMap<Long, Item >( );
	
	//�����Ʒ�����ﳵ
	
	public  void addItem( Product product , int number )
	{
		if( items.containsKey(product.getId()))
		{
			return;
		}
		Item item = new Item( number, product );
		
		items.put(product.getId(),  item );
		
	}
	
	//�޸���Ʒ����
	public void modifyNumberBYProductId( long productId, int number )
	{
		Item item = items.get(productId);
		
		item.setNumber(number);
		
	}
	
	//ɾ����Ʒ��Ŀ
	
	public void deleteItemByProductId( long productId )
	{
		items.remove(productId);
		
	}

	//ɾ�������Ʒ��Ŀ
	
	public void deleteItemByProductId( Long [] productIds )
	{
		for( Long id : productIds )
		{
			items.remove(id );
		}
	}
	
	
	//��չ��ﳵ
	
	public void clear( )
	{
		items.clear();
		
		logger.info("Cart cleared. size="+ items.size() );
		
	}
	
	//���������Ŀ
	
	
	public  Map<Long , Item > getCartItems( )
	{
		return items;
		
	}
	
	//���ù��ﳵ��Ŀ����Ŀ
	
	public int getItemNumber( )
	{
		return items.size();
	}
	
	//�жϹ��ﳵ�Ƿ�Ϊ��
	public boolean isEmpty( )
	{
		return items.isEmpty();
		
	}
	
	//���ﳵ������Ʒ���ܼ�
	
	public Double getPrice( )
	{
		double sum = 0;
		
		for( Long id : items.keySet() )
		{
			Item item = items.get(id);
			
			sum+=item.getCost();
			
		}
		
		return sum;
	}

	public Map<Long, Item> getItems() {
		return items;
	}

	public void setItems(Map<Long, Item> items) {
		this.items = items;
	}
	
	
	
	
}

