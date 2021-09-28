package com.cn.shop.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.cn.shop.model.Product;
import com.cn.shop.service.ProductService;
import com.cn.shop.util.FenciUtil;
import com.cn.shop.util.WordFilter;
import com.opensymphony.xwork2.ActionSupport;

public class FindAction  extends ActionSupport {
	
	//��Ʒservice IOC
	private ProductService productService;
	
	//�����ؼ���
	private String keyword;
	
	//���������ƪ����
	
	private List< Product > findlist;
	
	//������ҳ
	
	private int page;
	 
	 private int maxpage;
	
	private HttpServletRequest request;
	
	
	
	public int getPage() {
		return page;
	}



	public void setPage(int page) {
		this.page = page;
	}



	public int getMaxpage() {
		return maxpage;
	}



	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}



	public String getKeyword() {
		return keyword;
	}



	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	
	
	

	public ProductService getProductService() {
		return productService;
	}



	public void setProductService(ProductService productService) {
		this.productService = productService;
	}



	public List<Product> getFindlist() {
		return findlist;
	}



	public void setFindlist(List<Product> findlist) {
		this.findlist = findlist;
	}



	//��Ʒ������
	public String findproduct( )
	{
			if( keyword == null )
			{
				return "restart";
			}
		
			System.out.println("�ؼ���"+keyword);
		
				//�ִ�����
				List<String> keylist = new ArrayList<String>();
				
				//�����������ļ���filtermap
				
				 Map<String,String > filtermap = new HashMap<String ,String >( );
				
				
				//��ȡ�ؼ��ֵ�keymap
				 
				 Map<String , String > keymap = new HashMap<String, String >( );
				 
				 //�ؼ���
				 
				 String word = null;
				 
				 //�����ļ���ַ
				 
				 String url  = ServletActionContext.getServletContext().getRealPath("/WEB-INF/classes/filter.dic") ;  
						 
							 
				 System.out.println( url  );
				 
				 
				 
				 
				 try {
					 
					 //1�����зִ�
					 FenciUtil.fenci(keyword, keylist  );
					
					for( int i = 0 ; i < keylist.size() ; i++ )
						
					{
						System.out.println( keylist.get(i) );
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//2���������������ļ� ָ�������ļ�url
				 
				 WordFilter.readType(filtermap , url   );
				 
				 for( String key : filtermap.keySet() )
				 {
					 
					 System.out.println("key" + key + "value" + filtermap.get(key));
				 }
				 
				 //3����ȡ�ؼ���
				 
				 for( int i = 0 ; i < keylist.size(); i++ )
				 {
					 //��ȡ�ؼ���
					 word = keylist.get(i);
					 
					 //��ȡ�ؼ���
					 
					 WordFilter.filterword(word, filtermap, keymap);
					 
					 
					 
				 }
				 
				 //���鿴 keymap
				 
				 System.out.println("=====================");
				 
				 for( String key : keymap.keySet() )
				 {
					 
					 System.out.println("key" + key + "value" + keymap.get(key));
				 }
				 
				

				 this.request = ServletActionContext.getRequest();
					
					int maxpage = this.productService.findnum(keymap);
					
					int pageNo  =1;
					
					int pageSize = 5;
					
					if( page >  0 )
					{
						pageNo = page;
					}
					
					
					findlist = this.productService.find(keymap, pageNo, pageSize);
					
					//��list.jspҳ��page��  maxpage  �ظ����»���  bug
					
					request.setAttribute("fpage", pageNo);
					
					request.setAttribute("fmaxpage", maxpage);
		
		
					System.out.println( "��С" + findlist.size() );
		
		
		
		

		
					return "success";
	}
	

}
