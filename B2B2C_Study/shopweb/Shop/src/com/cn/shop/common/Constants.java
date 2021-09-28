package com.cn.shop.common;

public class Constants {
	
	//order status
		public static final int ORDER_STATUS_POST = 1; 			//�����Ѿ��ύ
		public static final int ORDER_STATUS_SEND = 2;			//�ѷ���
		public static final int ORDER_STATUS_RECEIVE = 3;		//�û��Ѿ��ջ�
		public static final int ORDER_STATUS_END = 4;			//��������
		
		//session attribute name
		public static final String SESSION_CART = "cart";		//������Session������û�
		public static final String SESSION_USER = "user";		//������Session����Ĺ��ﳵ
		
		//request attribute name
		public static final String REQ_PRODUCTS = "products";	//���������������Ʒ
		public static final String REQ_ORDER_NUMBER = "orderNum";//��������Ķ�����
		public static final String REQ_ORDERS = "orders";		//������������ж���

		public static  String  OrderStatus( int status ) {
		
		String text = null;
		
		switch( status )
		{
		
			case Constants.ORDER_STATUS_POST:
			
				text="�������ύ";
			
				break;
			case Constants.ORDER_STATUS_SEND:
			
				text="�ѷ���";
			
				break;
			
			case  Constants.ORDER_STATUS_RECEIVE:
	
				text="�û����ջ�";
	
				break;
	
			case  Constants.ORDER_STATUS_END:
	
				text="�������";
	
				break;
			default:
				
				break;
						
		}
		
		
		return text;
	}
}
