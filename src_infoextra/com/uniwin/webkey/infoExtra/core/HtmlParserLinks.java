package com.uniwin.webkey.infoExtra.core;
/**
 * @author hh
 * htmlparser提取网页链接
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;



public class HtmlParserLinks {

	public  List<String> extractUrl(String path,String encond,String reg, String urlBegin, String urlEnd){
		
		List<String> urls=new ArrayList<String>();
	try {
		Parser parser;
		HttpClientSource clientSource;
		String source = null;
		if((urlBegin!=null && !urlBegin.equals("")) ||( urlEnd!=null && !urlEnd.equals(""))){
			
			/*parser=new Parser(path);
			NodeList list=parser.parse(null);
			source=list.toHtml();*/
			clientSource=new HttpClientSource();
			source=clientSource.AnalySource(path, encond);
			
			Pattern pattern=Pattern.compile("\r|\n");
	 	    Matcher matcher=pattern.matcher(source);
	 	    source=matcher.replaceAll("");
	 	    Integer s=source.length();
	 	    Integer b = null,e = null;
	 	    if((urlBegin!=null && !urlBegin.equals(""))&& (urlEnd!=null && !urlEnd.equals(""))){
	 	    	b=source.indexOf(urlBegin);
				e=source.indexOf(urlEnd,b+urlBegin.length());
	 	    }else if((urlBegin!=null && !urlBegin.equals("")) &&(urlEnd==null || urlEnd.equals("")) ){
	 	    	b=source.indexOf(urlBegin);
	 	    	e=s;
	 	    }else if((urlBegin==null || urlBegin.equals(""))&& (urlEnd!=null && !urlEnd.equals(""))){
	 	    	b=0;
	 	    	e=source.indexOf(urlEnd);
	 	    }
	 	    
			if(b==-1 || e==-1){
				System.out.println("网址提取范围标志错误！");
			}else{
				
				b=b+urlBegin.length();
				source=source.substring(b,e);
			
//				parser.reset();
				
				source="<html>"+source;
				
				parser=new Parser(source);
				parser.setEncoding(encond);
				NodeFilter scopeFilter=new NodeClassFilter(LinkTag.class);
				NodeList scopeList=parser.extractAllNodesThatMatch(scopeFilter);
			
			for(int i=0;i<scopeList.size();i++){
				LinkTag linkTag=(LinkTag)scopeList.elementAt(i);
				String link=linkTag.extractLink();
				if(!link.startsWith("http://")){
					try{
					URL url=new URL(new URL(path),link);
					link=url.toString();
					}catch (MalformedURLException urle) {
						 System.out.println("非法网址！");
					}
				}
				if(link.matches(reg)&& !urls.contains(link)){
					urls.add(link);
				}
			}
		}//!=-1
			
		}else{
			
		parser = new Parser(path);
		parser.setEncoding(encond);
		NodeFilter framefilter=new NodeFilter(){
			private static final long serialVersionUID = -6227459203025605651L;

			public boolean accept(Node node) {
				if(node.getText().startsWith("frame")){
					return true;
				}else{
					return false;
				}
			}
		};
		
		OrFilter filter=new OrFilter(framefilter,new NodeClassFilter(LinkTag.class));
		NodeList list=parser.extractAllNodesThatMatch(filter);
		List<String> frameUrls=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			Node node=(Node)list.elementAt(i);
			String link;
			if(node instanceof LinkTag){
				LinkTag linkTag=(LinkTag)node;
				link=linkTag.extractLink().trim();
				if(!link.startsWith("http://")){
					try{
						URL url=new URL(new URL(path),link);
						link=url.toString();
					 }catch (MalformedURLException e) {   
			            System.out.println("非法网址！");
					 }  
				}
				if(link.matches(reg) && !urls.contains(link)){
					urls.add(link);
				}
				
			}else{
				String src=node.getText();
				Integer begin=src.indexOf("src")+5;
				Integer end=src.indexOf("\"",src.indexOf("src")+5);
				String frameUrl=src.substring(begin,end);
				URL url=new URL(new URL(path),frameUrl);
				link=url.toString();
				frameUrls.add(link);
				parser.reset();
				List<String> rList=AnalyLink(frameUrls,encond,reg);
				urls.addAll(rList);
			}
			
		}
	}//else	
		} catch (ParserException e) {
			System.out.println("提取网址失败！");
		} catch (MalformedURLException e) {
			System.out.println("非法网址！");
		}
		return urls;
		
 }

	private synchronized List<String> AnalyLink(List<String> frameUrls,String encond,String reg) {
		
		List<String> fList=new ArrayList<String>();
		for(int i=0;i<frameUrls.size();i++){
			try {
				Parser parser=new Parser(frameUrls.get(i).toString());
				parser.setEncoding(encond);
				NodeFilter filter=new NodeClassFilter(LinkTag.class);
				NodeList list=parser.extractAllNodesThatMatch(filter);
				LinkTag linkTag;
				for(int j=0;j<list.size();j++){
					linkTag=(LinkTag)list.elementAt(j);
					String link=linkTag.extractLink();
					if(!fList.contains(link)&& link.matches(reg)){
						fList.add(linkTag.extractLink());
					}
					
				}
				
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
		return fList;
		
	}
	
	
	
}
