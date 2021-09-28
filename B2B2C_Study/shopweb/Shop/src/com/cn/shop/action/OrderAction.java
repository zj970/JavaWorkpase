package com.cn.shop.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.cn.shop.common.Constants;
import com.cn.shop.model.Cart;
import com.cn.shop.model.Order;
import com.cn.shop.model.User;
import com.cn.shop.service.OrderService;
import com.opensymphony.xwork2.ActionSupport;

//����Action
public class OrderAction extends ActionSupport {
	
	private OrderService orderService;

	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	private HttpServletRequest request ;

	private HttpSession session ;
	
	
	//������ҳ
	public String index( )
	{
		String ret = "";
		
		this.request = ServletActionContext.getRequest();

		 this.session = this.request.getSession();
		 
		 User user = (User)this.session.getAttribute(Constants.SESSION_USER);
			
		 if( user == null )
		 {
			 ret ="login";
		 }
		 else
		 {
			 ret ="order";
		 }
		
		return ret;
	}
	
	//�ύ����
	public String postOrder( )
	{
		
		this.request = ServletActionContext.getRequest();

		 this.session = this.request.getSession();
		 
		 User user = (User)this.session.getAttribute(Constants.SESSION_USER);
		 
		 Cart cart = ( Cart )this.session.getAttribute(Constants.SESSION_CART);
		 
		 Order order = new Order( );
		 
		 order.setUser(user);
		 
		 order.setItems( cart.getItems() );
		 
		 order.setStatus( Constants.ORDER_STATUS_POST );
		 
		 //����ҵ��㣬�����¶���
		 
		 String orderNum = orderService.generateOrder(order);
		 
		 this.request.setAttribute(Constants.REQ_ORDER_NUMBER, orderNum );
		 
		
		return "success";
	}

}
