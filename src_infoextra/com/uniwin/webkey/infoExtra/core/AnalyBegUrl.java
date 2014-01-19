package com.uniwin.webkey.infoExtra.core;
/**
 * 分析起始地址
 */
import java.util.ArrayList;
import java.util.List;

public class AnalyBegUrl {

	
	public List checkBeginUrl(String tags){
		 List<String> all=new ArrayList<String>();//分析后网址list
		 List<Integer> l=new ArrayList<Integer>();//，所在位置
		 l.add(-1);
		 for(int i=0;i<tags.length();i++){
			 if(tags.charAt(i)=='$'){
				 l.add(i);
			 }
		 }
		 String eneryUrl;
		 for(int j=0;j+1<l.size();j++){
			 eneryUrl=tags.substring(l.get(j)+1,l.get(j+1));
			 all.add(eneryUrl);
		 }
		 
		 if(all.size()==1){
			List rList=analyUrls(all.get(0).toString());
			all.clear();
			all.addAll(rList);
			return all;
		 }else if(all.size()>1){
			 return all;
		 }else{
			 System.out.println("起始网址错误！");
		 }
		 
		return null;
	}
	
	public List<String> analyUrls(String path){
		int dcount=0;
		List<String> uList=new ArrayList<String>();
		if(path.indexOf("{")!=-1 && path.indexOf("}")!=-1){
			String p=path.substring(path.indexOf("{")+1,path.lastIndexOf("}"));
			for(int j=0;j<p.length();j++){
				if(p.charAt(j)==','){
					dcount++;
				}
			}
			String base=path.substring(0,path.indexOf("{"));
			String left=path.substring(path.indexOf("}")+1,path.length());
			String begin=path.substring(path.indexOf("{")+1,path.indexOf(","));
			if(dcount==1){
				String end=path.substring(path.indexOf(",")+1,path.indexOf("}"));
				int b=Integer.parseInt(begin);int e=Integer.parseInt(end);
				while(b<=e){
					String aUrl=(base+b+left).trim();
					uList.add(aUrl);
					System.out.println("增加起始网址"+aUrl);
					b++;
				}
//				System.out.println(uList);
			}if(dcount==2){
				String add=path.substring(path.lastIndexOf(",")+1,path.indexOf("}"));
				String end=path.substring(path.indexOf(",")+1,path.lastIndexOf(","));
				int bInteger=Integer.parseInt(begin);
				int endInteger=Integer.parseInt(end);
				int addInteger=Integer.parseInt(add);
				//倒排
				if(addInteger<0){
					while(bInteger>=endInteger){
						String aUrl=(base+bInteger+left).trim();
						uList.add(aUrl);
						System.out.println("增加起始网址"+aUrl);
						bInteger+=addInteger;
					}
				}else if(addInteger==0){
					System.out.println("起始地址的页面不正确！");
				}else{
				
				if(bInteger<endInteger && addInteger<(endInteger-bInteger)){
				while(bInteger<=endInteger){
					String aUrl=(base+bInteger+left).trim();
					uList.add(aUrl);
					bInteger+=addInteger;
				}
				
				}else{
					System.out.println("起始地址不合法！");
				}
			}//add条件结束
		}
			return uList;
			
		}else{
			uList.add(path);
			return uList;
			
		}
	}
}
