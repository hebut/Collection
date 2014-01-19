package com.uniwin.webkey.infoExtra.core;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


public class PageNext {

	
	private String encond;

	public String getEncond() {
		return encond;
	}


	public void setEncond(String encond) {
		this.encond = encond;
	}


	/* 提取链接的下一页  */
	public  void extractNextPage(LinkCollection Collection,String encond,String pageSign,Integer pageCount){
		
		
		Integer count=Collection.getunVisitedUrlNum();
		this.encond=encond;
		
		List<String> uList=new ArrayList<String>();
		for(int i=0;i<count;i++){
			String u=Collection.unVisitedUrlDeQueue();
			uList.add(u);
		}
		
		
		
		String u;
		Integer bCoun;
		for(int j=0;j<uList.size();j++){
			//循环下一页                 
			bCoun=pageCount;
			Collection.addUnvisitedUrl(uList.get(j).toString(),3);
			u=uList.get(j).toString();
			pageSign=pageSign.replace("*", ".*");
			
			if(bCoun==0){
//				while(Collection.getunVisitedUrlNum()<=100){
				while(true){	
					String extractNextUrl1 = null;
					Parser parser;
					try {
						parser = new Parser(u);
						parser.setEncoding(encond);
						NodeFilter filter=new LinkRegexFilter(pageSign);
						NodeList list=parser.extractAllNodesThatMatch(filter);
						if(list.size()==0){
							System.out.println("提取下一层网址失败！");
							break;
						}else{
							LinkTag linkTag=(LinkTag)list.elementAt(0);
							extractNextUrl1=linkTag.extractLink();
							Collection.addUnvisitedUrl(extractNextUrl1,1);
							u=extractNextUrl1;
							parser.reset();
						}
						
						} catch (ParserException e) {
							System.out.println("提取下一层网址失败！");
					    }
						
				}
				
			}else{
				while(bCoun>0){
					String extractNextUrl1 = null;
					Parser parser;
					System.out.println("请求: "+u);
					try {
						parser = new Parser(u);
						parser.setEncoding(encond);
						NodeFilter filter=new LinkRegexFilter(pageSign);
						NodeList list=parser.extractAllNodesThatMatch(filter);
						if(list.size()==0){
							System.out.println("提取下一层网址失败！");
							break;
						}else{
							LinkTag linkTag=(LinkTag)list.elementAt(0);
							extractNextUrl1=linkTag.extractLink();
							Collection.addUnvisitedUrl(extractNextUrl1,1);
							u=extractNextUrl1;
						}
						parser.reset();
							
						} catch (ParserException e) {
							System.out.println("提取网址失败！");
						}
					bCoun--;
				}
				
			}
			
		}//for
	
		
	}
	
	
	/* 提取内容的下一页 */
	public  List<String> extractNextConPage(String u,String _encond,String pageSign,Integer bCoun){
		
		List<String> uList=new ArrayList<String>();
		
		//循环下一页                 
		pageSign=pageSign.replace("*", ".*");
			
			if(bCoun==0){

				while(true){	
					String extractNextUrl1 = null;
					Parser parser;
					try {
						parser = new Parser(u);
						parser.setEncoding(_encond);
						NodeFilter filter=new LinkRegexFilter(pageSign);
						NodeList list=parser.extractAllNodesThatMatch(filter);
						if(list.size()==0){
//							System.out.println("内容下一页提取失败！");
							break;
						}else{
							LinkTag linkTag=(LinkTag)list.elementAt(0);
							extractNextUrl1=linkTag.extractLink();
							uList.add(extractNextUrl1);
							u=extractNextUrl1;
							parser.reset();
						}
						
						} catch (ParserException e) {
							System.out.println("内容下一页提取失败！");
					    }
						
				}
				
			}else{
				while(bCoun>0){
					String extractNextUrl1 = null;
					Parser parser;
					System.out.println("请求: "+u);
					try {
						parser = new Parser(u);
						parser.setEncoding(_encond);
						NodeFilter filter=new LinkRegexFilter(pageSign);
						NodeList list=parser.extractAllNodesThatMatch(filter);
						if(list.size()==0){
//							System.out.println("内容下一页提取失败！");
							break;
						}else{
							LinkTag linkTag=(LinkTag)list.elementAt(0);
							extractNextUrl1=linkTag.extractLink();
							uList.add(extractNextUrl1);
							u=extractNextUrl1;
						}
						parser.reset();
							
						} catch (ParserException e) {
							System.out.println("内容下一页提取失败！");
						}
					bCoun--;
				}
				
			}
			return uList;
			
	}
	
	
	
	
}

