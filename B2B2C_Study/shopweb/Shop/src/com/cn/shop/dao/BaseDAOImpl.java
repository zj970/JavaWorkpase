package com.cn.shop.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class BaseDAOImpl implements BaseDAO  {
	
	private HibernateTemplate hibernateTemplate;
	
	// ����һ��Logger���������������������־��
	protected Logger logger = Logger.getLogger(this.getClass());
	
	

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@Override
	public <T> void saveEntity(T entity) {
		// TODO Auto-generated method stub
		
		this.hibernateTemplate.saveOrUpdate(entity);
		
	}

	@Override
	public <T> void deleteEntity(T entity) {
		// TODO Auto-generated method stub
		
		this.hibernateTemplate.delete(entity);
		
		
	}

	

	@Override
	public <T> void updateEntity(T entity) {
		// TODO Auto-generated method stub
		
		this.hibernateTemplate.saveOrUpdate(entity);
		
	
		
		
	}

	@Override
	public <T> List<T> getAllEntity( String  entityName) {
		// TODO Auto-generated method stub
		
		return (List<T>)this.hibernateTemplate.find("from" +  " "+ entityName );
		
		//return (List<T>)this.hibernateTemplate.find("from Product");
	}

	@Override
	public <T> T getEntityById(Class<T> entityClass, Serializable id) {
		// TODO Auto-generated method stub
		return (T) this.hibernateTemplate.get(entityClass, id);
	}
	
	@Override
	public <T> void deleteEntityById(Class<T> entityClass, Serializable id) {
		// TODO Auto-generated method stub
		
		this.hibernateTemplate.delete( this.getEntityById(entityClass, id) );
		
	}
	
	//2014.8.29 �����Ʒ��ҳ����

	@Override
	public <T> List<T> getbyPage(String hql, int pageNo, int pageSize ,int real ) {
		// TODO Auto-generated method stub
		
		List<T> list = new ArrayList<T>();
		
		int begin =  (pageNo-1 )*pageSize;
		
		int end =  (pageNo )*pageSize;
		
		//��ҳ��ʾ���ܴ���bug
		
		/*if( real < 5 )
		{
			end =real;
		}
		else
		{
			if(real < end )
			{
				//end = real -1;
				
				end = real;
			}
			
		}*/
		
		//bug �޸���ʾbug
		if( end > real )
		{
			end = real;
		}
		
		
		
		
		
		list = this.hibernateTemplate.find(hql).subList( begin , end );
		
		return list;
		

	}

	//ʵ��ҳ��
	@Override
	public <T> int realPage(String hql) {
		// TODO Auto-generated method stub
		
		int count = 0;
		
		
		
		count = this.hibernateTemplate.find(hql).size();
		
		
		
		return count;
	}

}
