package com.cn.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.cn.shop.util.FenciUtil;
import com.cn.shop.util.WordFilter;



public class Keyword {
	
	@Test
	
	public void fenci( )
	{
		String text = "�Ըе��·��ÿ���Ь�Ӻ�ɫñ��";
		
		
		//�ִ�����
		List<String> keylist = new ArrayList<String>();
		
		//�����������ļ���filtermap
		
		 Map<String,String > filtermap = new HashMap<String ,String >( );
		
		
		//��ȡ�ؼ��ֵ�keymap
		 
		 Map<String , String > keymap = new HashMap<String, String >( );
		 
		 //�ؼ���
		 
		 String word = null;
		 
		 
		 
		 try {
			 
			 //1�����зִ�
			 FenciUtil.fenci(text, keylist  );
			
			for( int i = 0 ; i < keylist.size() ; i++ )
				
			{
				System.out.println( keylist.get(i) );
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//2���������������ļ�
		 
		 WordFilter.readType(filtermap);
		 
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
		 
		
	}

}
