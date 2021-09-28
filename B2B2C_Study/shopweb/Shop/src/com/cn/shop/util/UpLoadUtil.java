package com.cn.shop.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

public class UpLoadUtil {
	
	private static final int BUFFER_SIZE = 16 * 1024;  
	
	 private static void copy(File src, File dst) {  
	      try {  
	    	  		InputStream in = null;  
	    	  		OutputStream out = null;  
	    	  		try {  
	    	  			in = new BufferedInputStream(new FileInputStream(src),  
	    	  					BUFFER_SIZE);  
	    	  			out = new BufferedOutputStream(new FileOutputStream(dst),  
	    	  					BUFFER_SIZE);  
	    	  			byte[] buffer = new byte[BUFFER_SIZE];  
	    	  			while (in.read(buffer) > 0) {  
	    	  				out.write(buffer);  
	    	  			}  
	    	  		} finally {  
	               if (null != in) {  
	                 in.close();  
	               }  
	                if (null != out) {  
	                    out.close();  
	                }  
	           }  
	       } catch (Exception e) {  
          e.printStackTrace();  
          
	       }  
	    }  

  private static String getExtention(String fileName) {  
	        int pos = fileName.lastIndexOf(".");  
	       return fileName.substring(pos);  
	    }  
  
 
     public static String upload( File src ) {  
          if (src  == null)  
              return ""; 
          
          String tempName = new Date().getTime() +  getExtention( src.getName() );
          
          
          File imageFile = new File(ServletActionContext.getServletContext()  //�õ�ͼƬ�����λ��(����root���õ�ͼƬ�����·����tomcat�µĸù�����)  
        
                      .getRealPath("UploadImages")     
                      + "/" + tempName );   
              
          System.out.println(imageFile.getAbsolutePath()  );
          
          copy( src , imageFile);  //��ͼƬд�뵽�������õ�·����  
          
          //2014.9.2 �����ļ���
 
         // return  imageFile.getAbsolutePath() ;  
          
          //�޸Ŀͻ�����ʾͼƬbug ʹ����Ե�ַ  ֱ�ӷ����ļ��� mysql�洢���ļ���
          
          return tempName ;
          
          
      }  
    
   
 

}
