package com.uniwin.webkey.infoExtra.core;
/**
 * 网页编码（不用）
 */
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.net.MalformedURLException;
import java.net.URL;

import org.htmlparser.lexer.Page;

public class WebEncoding {

	public String AnalyEnconding(String path){
	URL url=null;
	try {
		url=new URL(path);
	} catch (MalformedURLException e) {
		System.out.println("网址解析错误！");
	}
	CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); 
	detector.add(new ParsingDetector(false)); 
	java.nio.charset.Charset charset = null;  
	try {  
	      charset = detector.detectCodepage(url);  
	} catch (Exception ex) {
		System.out.println("链接超时！");
//		ex.printStackTrace();
	}
	if(charset!=null){
		
	 if(charset.name().equalsIgnoreCase("utf-8")||charset.name().equals("UTF-8")){
		 Page.GaoBinDEFAULT_CHARSET="utf-8";
      }else{
    	  Page.GaoBinDEFAULT_CHARSET="gb2312";
     }
	   return Page.getGaoBinDEFAULT_CHARSET();
	   
	}else{
		System.out.println("链接超时，网站编码未能解析！");
		return null;
	}
	
	   
	}
	
	public static void main(String[] args){
		
		//http://www.nsfc.gov.cn/Portal0/InfoModule_396/31705.htm
		String path="http://www.nsfc.gov.cn/Portal0/InfoModule_396/32490.htm";
		URL url=null;
		try {
			url=new URL(path);
		} catch (MalformedURLException e) {
			System.out.println("网址解析错误！");
		}
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); 
		detector.add(new ParsingDetector(false)); 
		detector.add(ASCIIDetector.getInstance());                                     
		detector.add(UnicodeDetector.getInstance());                                   
		
		java.nio.charset.Charset charset = null;  
		try {  
		      charset = detector.detectCodepage(url);  
		} catch (Exception ex) {
			System.out.println("链接超时！");
		}
		System.out.println(charset.toString());
		
	}
	
	
}
