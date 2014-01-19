package com.uniwin.webkey.infoExtra.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


import com.zkoss.org.messageboxshow.MessageBox;


public class IeExtract extends Window implements AfterCompose{

	Textbox content,tagName,attriName,valueName,url,source;
	Label ID;
	Iframe frame;
	Button regnoize;
	Iframe hiddenWin;
	Checkbox head,remark,script,style;
	String sourceData;
	Textbox tag,key;
	
	//搜索
	boolean ischaoZhao=false;
	List<Integer> dataList;
	Integer jiShu=0;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this,this);
	}

	public void onClick$show(){
		
		String u=url.getValue().trim();
		if(u==null || u.equals("")){
			MessageBox.showWarning("请输入网址！");
		}else if(u.matches("http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?")){
		
		String path=url.getValue().trim();
		hiddenWin.setSrc(path);
		
		try {
			Parser parser=new Parser(path);
			WebEncoding wEncoding=new WebEncoding();
			String encond=wEncoding.AnalyEnconding(path);
			parser.setEncoding(encond);
			NodeList list=parser.parse(null);
//			String characters=list.toHtml();
			sourceData=list.toHtml();
//			byte[] bs=characters.getBytes();
//			String r=new String(bs,encond);
			
			/*if(sb==null || sb.toString().equals("")){
				sb.append(r);
			}*/
//			String result=showTags();
//			String rr=sb.toString();
			
			Pattern pattern=Pattern.compile("\r|\n");
			Matcher matcher=pattern.matcher(sourceData);
			sourceData=matcher.replaceAll("");
			source.setValue(sourceData);
			
		} catch (ParserException e) {
			e.printStackTrace();
		} /*catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		
	}else{
		MessageBox.showWarning("输入的网址不合法！");
	}
		
}
	
	
	public void onClick$clear(){
		sourceData="";
		url.setValue("");
		source.setValue("");
	}
	
	public void onCheck$head(){
		
		if(head.isChecked()==true){
			String htmlSource=source.getValue();
			if(htmlSource!=null && !htmlSource.trim().equals("")){
				htmlSource=htmlSource.replaceAll("<head[^>]*>([\\s\\S]*)</head>", "");
				source.setValue(htmlSource);
			}
		
		}
		else{
			String result=showTags();
			source.setValue(result);
		}
		
 }
	public void onCheck$remark(){
		
		if(remark.isChecked()==true){
			
			if(source.getValue()!=null && !source.getValue().trim().equals("")){
				String htmlSource=source.getValue();
				htmlSource=htmlSource.replaceAll("<!--.*-->", "");
				source.setValue(htmlSource);
			}
			
		}else{
			String result=showTags();
			source.setValue(result);
		}
	}
	
	public void onCheck$script(){
		
		if(script.isChecked()==true){
			
			if(source.getValue()!=null && !source.getValue().trim().equals("")){
				String htmlSource=source.getValue();
				htmlSource=htmlSource.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>", "");
				source.setValue(htmlSource);
			}
			
		}else{
			String result=showTags();
			source.setValue(result);
		}
	}
	
	public void onCheck$style(){
		
		if(style.isChecked()==true){
			
			if(source.getValue()!=null && !source.getValue().trim().equals("")){
				String htmlSource=source.getValue();
				htmlSource=htmlSource.replaceAll("<style[^>]*>([\\s\\S]*)</style>", "");
				source.setValue(htmlSource);
			}
			
		}else{
			String result=showTags();
			source.setValue(result);
		}
	}
	
	private String showTags(){
		
		if(sourceData!=null && !sourceData.equals("")){
			
			String bSource=sourceData;
			/*if(remark.isChecked()==true){
				String regEx="<!-->[^-]*-->";
				Pattern p=Pattern.compile(regEx);
				Matcher m=p.matcher(bSource);
				bSource=bSource.replaceAll("<!--.*-->", "");
			}*/if(style.isChecked()==true){
	        	bSource=bSource.replaceAll("<style[^>]*>([\\s\\S]*)</style>", ""); 
			}if(script.isChecked()==true){
				bSource=bSource.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>", ""); 
			}if(head.isChecked()==true){
	        	bSource=bSource.replaceAll("<head[^>]*>([\\s\\S]*)</head>", ""); 
			}
			
			return bSource;
		}else{
			return null;
		}
	}
	
	public void onClick$all(){
	
    		head.setChecked(true);//remark.setChecked(true);
        	script.setChecked(true);style.setChecked(true);
    		String result=showTags();
    		source.setValue(result);
	}
	
	public void onClick$notAll(){
		
		head.setChecked(false);//remark.setChecked(false);
    	script.setChecked(false);style.setChecked(false);
    	String result=sourceData;
    	if(result!=null && !result.equals("")){
			source.setValue(result);
    	}
	}
	
	public void onClick$showTags(){
		
		final String t=tag.getValue();
		if(t==null || t.trim().equals("")){
			MessageBox.showWarning("请输入标签！");
		}else{
			String s=sourceData;
			final StringBuffer sBuffer=new StringBuffer();
			try {
				Parser parser=new Parser(s);
				NodeVisitor visitor=new NodeVisitor(){
					public void visitTag(Tag tag){
						if(tag.getTagName().equalsIgnoreCase(t)){
							sBuffer.append(tag.toHtml());
						}
					}
				};
				parser.visitAllNodesWith(visitor);
			} catch (ParserException e) {
				e.printStackTrace();
			}
			
			source.setValue(sBuffer.toString());
		}
		
	}
	
	public void onClick$clearTag(){
		tag.setValue("");
		source.setValue(sourceData);
	}
	
	/*public void onClick$extract(){
		
		String id=ID.getValue();
		try {
			DomTree dTree=new DomTree();
			String result=dTree.AnalysisTree(url.getValue().trim());
			Parser parser=new Parser(result);
			String Tag=tagName.getValue().trim();
			String Attri=attriName.getValue().trim();
			String Value=valueName.getValue().trim();
			NodeFilter filter=null;
			if(Attri!=null && Value!=null){
			 filter=new AndFilter(new TagNameFilter(Tag),new HasAttributeFilter(Attri,Value));
			}else{
			 filter=new TagNameFilter(Tag);
			}
			NodeList nodeList=parser.extractAllNodesThatMatch(filter);
			Node node=(Node)nodeList.elementAt(Integer.valueOf(id));
			if(!node.toPlainTextString().trim().equals("")){
			String con=node.toPlainTextString();
			ShowExtract extract=(ShowExtract)Executions.createComponents("/ExtractInfo/CheckTags/Extract.zul",null, null);
			extract.LoadContent(con);
			extract.doModal();
			
		}//else
			parser.reset();
			} catch (SuspendNotAllowedException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParserException e) {
				e.printStackTrace();
		    } catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			
		}
			
  }*/
	
	public void onClick$search(){
		
		String keyWord=key.getValue().trim();
		if(source.getValue()==null || source.getValue().equals("") ){
			MessageBox.showWarning("搜索内容为空！");
		}else{
			
			dataList=new ArrayList<Integer>();
			String sourceWord=source.getText();
			String ceshiSource=source.getText();
		if(keyWord!=null && !keyWord.equals("")){
			ischaoZhao=true;
			jiShu=0;
			if(sourceWord.indexOf(keyWord)!=-1){
				Integer first=0;
				while(ceshiSource.indexOf(keyWord,first)!=-1){
					Integer begin=ceshiSource.indexOf(keyWord,first);
					dataList.add(begin);
//					System.out.println("位置："+begin);
					first=first+begin+keyWord.length();
				}
				source.setSelectedText(dataList.get(0), dataList.get(0)+keyWord.length(), keyWord, true);
				
			}else{
				MessageBox.showWarning("没有发现！");
			}
			
		}else{
			MessageBox.showError("请输入检索字！");
		}
			
		}
		
	}
	
	
	public void onClick$nextOne(){
		
		if(ischaoZhao==true){
			
			if(dataList.size()==1){
				MessageBox.showWarning("没有下一个！");
				source.setSelectedText(dataList.get(jiShu),dataList.get(jiShu)+key.getValue().length(), key.getValue(), true);
			}else{
				jiShu++;
				if(jiShu>dataList.size()-1){
					MessageBox.showWarning("没有下一个！");
					source.setSelectedText(dataList.get(jiShu-1),dataList.get(jiShu-1)+key.getValue().length() , key.getValue(), true);
				}else{
					source.setSelectedText(dataList.get(jiShu),dataList.get(jiShu)+key.getValue().length() , key.getValue(), true);
				}
			}
			
		}else{
			onClick$search();
		}
		
	}
	
	public void onClick$beforeOne(){
		
		if(ischaoZhao==true){
			
			if(jiShu==0){
				MessageBox.showWarning("没有上一个！");
				source.setSelectedText(dataList.get(jiShu),dataList.get(jiShu)+key.getValue().length(), key.getValue(), true);
			}else{
				jiShu--;
				source.setSelectedText(dataList.get(jiShu),dataList.get(jiShu)+key.getValue().length(), key.getValue(), true);
			}
			
		}else{
			MessageBox.showWarning("请先查找！");
		}
	}
	
	
	
	/*public void onClick$regnoize(){
		String con=content.getValue();
		String path=url.getValue().trim();
		if(con.trim().equals("")){
			MessageBox.showInfo("文本框不能为空！");
		}else{
			  Pattern p=Pattern.compile("\\s*|\t|\r|\n");
			  Matcher matcher=p.matcher(con);
			  String r=matcher.replaceAll("");
			  r=r.replaceAll("&nbsp;","" );
					
			  		
					DomTreeAnaly analy=new DomTreeAnaly();
					try {
					ArrayList tags=analy.AnalysisTree(path);
					WebEncoding wEncoding=new WebEncoding();
					String encond=wEncoding.AnalyEnconding(path);
					
					
					TagCut cut=new TagCut();
					List cutlist=cut.AnalyTags(tags);
					
					RegnoizeTag regnoizeTag=new RegnoizeTag();
					try {
					//String rTag=regnoizeTag.regnoize(tags,url,encond,con);
					PartOfweb partOfweb=regnoizeTag.regnoize(cutlist,path,encond,r);
					if(partOfweb.getTagId().equals(-1)){
						MessageBox.showError("输入的文本框有特殊字符！");
					}else{
					String rTag=partOfweb.getTag();
					
					if(rTag==null||rTag.equalsIgnoreCase(null)){
						MessageBox.showWarning("没有符合条件的标签块！");
					}else{
						String tag,attri,value;
					
					 tag=(String) rTag.substring(rTag.indexOf("<")+1,rTag.indexOf("$")).trim();
					 attri=rTag.substring(rTag.indexOf("$")+1,rTag.indexOf("=")).trim();
					 value=rTag.substring(rTag.indexOf("\"")+1,rTag.indexOf(">")-2).trim();
					 tagName.setValue(tag);
					 attriName.setValue(attri);
					 valueName.setValue(value);
					 ID.setValue(partOfweb.getTagId()+"");
				}
					
			}	
					} catch (ParserException e) {
						e.printStackTrace();
					}} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	}
		
   }*/

	
}
