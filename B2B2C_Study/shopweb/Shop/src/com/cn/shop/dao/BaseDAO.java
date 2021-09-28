package com.cn.shop.dao;

import java.io.Serializable;
import java.util.List;


//����DAO������������ʵ������ɾ�Ĳ����

public interface BaseDAO {
	
	//����һ��ʵ�����
	public <T> void saveEntity( T entity );
	
	//ɾ��һ��ʵ�����
	
	public <T> void deleteEntity( T entity );
	
	//����ʵ�����ͺ�IDɾ��һ��ʵ�����
	public <T> void deleteEntityById( Class <T> entityClass , Serializable id );
	
	//�޸�һ��ʵ�����
	
	public <T> void updateEntity( T entity );
	
	//��ѯĳ��ʵ���ȫ��ʵ��
	
	public <T> List<T> getAllEntity( String entityName );
	
	//ͨ���������һ��ʵ�����
	
	public <T> T getEntityById( Class<T> entityClass , Serializable id );
	
	//2014.8.29�����Ʒ��ҳ����
	
	//��ҳ
	
	public <T> List<T> getbyPage(String hql, int pageNo, int pageSize  ,int real  );
	
	//��ҳ��
	
	public <T> int realPage( String hql );
	
	
	

}
