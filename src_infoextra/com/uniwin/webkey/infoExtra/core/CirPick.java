package com.uniwin.webkey.infoExtra.core;

/**
 * 循环采集
 */
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.util.HtmlTags;


public class CirPick {

	public List<CirEntity> cirExtract(String path,String cir,List<WkTPickreg> pList){
		
		Parser parser;
		String source = null;
		List<CirEntity> lastList=new ArrayList<CirEntity>();
		List<String> rList = null;
		try {
			
		parser = new Parser(path);
		NodeList list=parser.parse(null);
		source=list.toHtml();
		rList=new ArrayList<String>();
		
		} catch (ParserException e1) {
			System.out.println("循环解析失败！");
		}
		source=source.replaceAll("\r|\n","");
		String ss;
		Integer b=0,e=0;
		while((source.indexOf(cir)!=-1) && (source.indexOf(cir, source.indexOf(cir)+cir.length())!=-1)){
			b=source.indexOf(cir);
			e=source.indexOf(cir,b+cir.length());
			ss=source.substring(b,e);
			source=source.substring(e,source.length());
			rList.add(ss);
		}
		String last = null;
		if(source.indexOf(cir)!=-1){
			last=source.substring(source.indexOf(cir),source.indexOf(cir)+(e-b));
		}
//		String last=source.substring(source.indexOf(cir),source.indexOf(cir)+(e-b));
		if(last!=null){
			rList.add(last);
		}
		
		
		String[] extractResult;
		CirEntity cirEntity;
		for(int r=0;r<rList.size();r++){
			extractResult=extractCir(path,rList.get(r),pList);
			cirEntity=new CirEntity();
			cirEntity.setCEntity(extractResult);
			lastList.add(cirEntity);
		}
		
		return lastList;
		
	}
	
	
	
	
	private String[] extractCir(String path,String source,List<WkTPickreg> pList){
		
		WkTPickreg pReg;
		String regBegin,regEnd;
		String[] extract=new String[pList.size()];
		
		for(int t=0;t<pList.size();t++){
			
			pReg=(WkTPickreg)pList.get(t);
			regBegin=pReg.getKpRegbegin();
			regEnd=pReg.getKpRegend();
			
		
			Integer beg=source.indexOf(regBegin);
			Integer e=source.indexOf(regEnd,beg+regBegin.length());
			String result = null;
			if(beg!=-1 && e!=-1){
			 
			 beg=beg+regBegin.length();
			 result=source.substring(beg, e);
			
			}else{
			  System.out.println(path+pReg.getKpRegname()+"规则采集失败，查看采集规则设置！");
			}
		if(result==null || result.equals("")|| result.equals(null)){
				extract[t]="";
	
		}else{
		
			if(pReg.getKpRetainTags()!=null){
			
			List tagList=retainTags(result);
			String tagSource=pReg.getKpRetainTags().replace("false,", "").trim();
			HtmlTags htmlTags=new HtmlTags();
			List tList=htmlTags.analyTags(tagSource);
			
			String sourceTag,dataTag;
			for(int a=0;a<tList.size();a++){
				sourceTag=(String) tList.get(a);
				for(int b=0;b<tagList.size();b++){
					dataTag=(String) tagList.get(b);
					if(sourceTag.trim().equals(dataTag.trim())){
						tagList.remove(b);
					}
				}
			}
			
			extract[t]=removeTag(result, tagList);
			
		}else{
			
		try {
			
			final StringBuffer sb=new StringBuffer();
			
			Parser parser=new Parser(result);
			NodeVisitor visitor=new NodeVisitor(){
				public void visitStringNode(Text text){
					String t=text.getText();
					sb.append(t);
				}
			};
			parser.visitAllNodesWith(visitor);
			
			String rr=sb.toString();
			rr=rr.replace("\t", "");
			rr=rr.replace("\n", "");
			rr=rr.replace("\r", "");
			extract[t]=rr;
		
			source=source.substring(e+regEnd.length(),source.length());
			
		} catch (ParserException e1) {
			extract[t]=result;
			source=source.substring(e+regEnd.length(),source.length());
		}
	}
  }
		

}

		return extract;
	}
	
	
	/*提取符合标记的标签 */
	private List<String> retainTags(String source){
		
		final List<String> tagList=new ArrayList<String>();
		try {
			Parser parser=new Parser(source);
			NodeVisitor visitor=new NodeVisitor(){
				public void visitTag(Tag tag){
					if(!tagList.contains(tag.getTagName())){
						tagList.add(tag.getTagName());
					}
				}
			};
			parser.visitAllNodesWith(visitor);
			
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return tagList;
	}
	
	//此方法需要改进
	private String removeTag(String source,List<String> tagList){
		String tag;
		for(int i=0;i<tagList.size();i++){
			tag=tagList.get(i);
			source=source.replaceAll("(<"+tag+"[^>]*>|</"+tag+">)","").trim();
			source=source.replaceAll("(<"+tag.toLowerCase()+"[^>]*>|</"+tag.toLowerCase()+">)","").trim();
		}
		return source;
	}
	
}
