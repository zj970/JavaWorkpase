package com.cn.shop.action;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.cn.shop.dto.ProductDTO;
import com.cn.shop.dto.UserDTO;
import com.cn.shop.model.Product;
import com.cn.shop.model.User;
import com.cn.shop.service.ProductService;
import com.cn.shop.util.UpLoadUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


/*��̨����Ʒ����ɾ�Ĳ�2014.8.28*/
public class ProductAction  extends ActionSupport  implements ModelDriven {
	
	//��Ʒservice IOC
	private ProductService productService;
	
	//bug  ����ģʽ���½��ܲ����ӳ�  implements ModelDriven
	
	private ProductDTO productDTO = new ProductDTO( );
	
	private Product product ;

	//��̨��Ʒ��� 2014.8.29
	
	//��Ʒ���� 
	private List< Product > productlist;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	//��ӷ�ҳ 
	 
	private int page;
		 
	private int maxpage;
	
	//�޸���Ʒ 2014.8.29
	
	private Product updateproduct;
	
	private long productId;
	
	//�������ģʽ bug
	
	/*private String name; //��Ʒ����
	
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
	}*/
		
	/*����ģʽbug*/

	public Product getUpdateproduct() {
		return updateproduct;
	}

	public void setUpdateproduct(Product updateproduct) {
		this.updateproduct = updateproduct;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public List<Product> getProductlist() {
		return productlist;
	}

	public void setProductlist(List<Product> productlist) {
		this.productlist = productlist;
	}

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

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	
	public String addProduct( )
	{
	
		
		product = new Product( );
		
		String imagepath = UpLoadUtil.upload( productDTO.getFile());
		
		product.setImageSrc(imagepath);
		
		product.setName(productDTO.getName());
		
		product.setPrice( productDTO.getPrice() );
		
		product.setDescription( productDTO.getDescription() );
		
		productService.addProduct(product);
			
		return "success";
		
	}
	
	
	//��Ʒ��� 2014.8.29
	public String productlist( )
	{
		this.request = ServletActionContext.getRequest();
		
		int maxpage = this.productService.maxSize();
		
		int pageNo  =1;
		
		int pageSize = 5;
		
		if( page >  0 )
		{
			pageNo = page;
		}
		
		
		productlist = this.productService.getByPage(pageNo , pageSize );
		
		request.setAttribute("page", pageNo);
		
		request.setAttribute("maxpage", maxpage);
		
		return"list";
	}
	
	//�޸�
	public String updateProduct( )
	{
		
		updateproduct = this.productService.getProduct(productId );
		
				
		return "update";
	}
	
	
	// �����޸���Ʒ
	
	public String saveProduct( )
	{
		
		
		String imagepath = UpLoadUtil.upload(productDTO.getFile() );
		
		updateproduct.setImageSrc(imagepath);
		
		updateproduct.setName( productDTO.getName() );
		
		updateproduct.setPrice( productDTO.getPrice() );
		
		updateproduct.setDescription( productDTO.getDescription() );
		
		this.productService.UpdateProduct(updateproduct);
		
		return "save";
	}
	
	//ɾ����Ʒ
	
	public String deleteProduct( )
	{
		
		this.productService.deleteProduct(productId);
		
		return "delete";
		
	}
	
	
	
	

	@Override
	public Object getModel() {
		// TODO Auto-generated method stub
		return productDTO;
	}
	

}
