package com.uniwin.webkey.util.ui;


	import org.apache.commons.mail.EmailException;  
import org.apache.commons.mail.HtmlEmail;
	 
	public class Email   
	{  
	    public Email()  
	    {  
	          
	    }  
	      
	    public static void main(String[] args)  
	    {  
	        send();  
	    }  
	      
	    public static void send()  
	    {  
	    	HtmlEmail email = new HtmlEmail();     
	        email.setTLS(true);          
	        email.setHostName("smtp.qq.com");        
	        email.setAuthentication("563275696@qq.com", "86920gb");     
	          
	        try   
	        {  
	            email.addTo("gbfd2009@163.com");  
	            email.setFrom("563275696@qq.com");       //我方     
	              
	            email.setSubject("Java发送邮件测试");                 //标题     
	              
	            email.setCharset("GB2312");                     //设置Charset  
	              
	            email.setHtmlMsg("这是一封Java程序发出的<strong>测试邮件</strong>。");     //内容     
	              
	            email.send();
	            System.out.println("发送成功！");
	              
	        } catch (EmailException e) {  
	            e.printStackTrace();  
	        }   
	    }  
	    
	    
}  

