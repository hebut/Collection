package com.uniwin.webkey.infoExtra.core;

import java.security.MessageDigest;

public class Md5 {

	public static void main(String[] args){
		
		System.out.println(Md5.MD5("http://www.hebut.edu.cn/html/xiaonaxinwen/201012/01-2845.html"));
	}
	
	public final static String MD5(String s){
		
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b',
				'c','d','e','f'};//用来将字节转换成16进制表示的字符
		
		try {
			
			byte[] strTemp=s.getBytes();
			MessageDigest mdTemp=MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md=mdTemp.digest();
			int j=md.length;
			char str[]=new char[j*2];
			int k=0;
			for(int i=0;i<j;i++){
				byte byte0=md[i];
				str[k++]=hexDigits[byte0>>> 4 & 0xf];
				str[k++]=hexDigits[byte0 & 0xf];
			}
			
			return new String(str);
		} catch (Exception e) {
			return null;
		}
		
	}
	
}
