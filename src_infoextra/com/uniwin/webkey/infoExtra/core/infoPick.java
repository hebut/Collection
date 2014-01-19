package com.uniwin.webkey.infoExtra.core;
/**
 * 具体字段采集
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.junit.Test;

import com.uniwin.webkey.infoExtra.itf.ReplaceService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.model.WkTReplace;
import com.uniwin.webkey.infoExtra.util.BeanFactory;
import com.uniwin.webkey.infoExtra.util.BeginUrl;
import com.uniwin.webkey.infoExtra.util.HtmlTags;


public class infoPick {
	
	
	ReplaceService replaceService=(ReplaceService)BeanFactory.getBean("replaceService");
	private WkTExtractask wkTExtractask;
	private URL strWeb = null;  
	public WkTExtractask getWkTExtractask() {
		return wkTExtractask;
	}

	public void setWkTExtractask(WkTExtractask wkTExtractask) {
		this.wkTExtractask = wkTExtractask;
	}
	public String get_encond() {
		return _encond;
	}

	public void set_encond(String _encond) {
		this._encond = _encond;
	}

	public WkTGuidereg getWkTGuidereg() {
		return wkTGuidereg;
	}

	public void setWkTGuidereg(WkTGuidereg wkTGuidereg) {
		this.wkTGuidereg = wkTGuidereg;
	}

	private String _encond;
	private List<WkTPickreg> _pList;
	public List<WkTPickreg> get_pList() {
		return _pList;
	}

	public void set_pList(List<WkTPickreg> list) {
		_pList = list;
	}

	private WkTGuidereg wkTGuidereg;
	
	public synchronized String[] extractByTags(String path,WkTExtractask extractask,String encond,List<WkTPickreg> pList,WkTGuidereg guidereg){
		
		this.wkTExtractask=extractask;
		this._encond=encond;
		this._pList=pList;
		this.wkTGuidereg=guidereg;
		
		WkTPickreg pReg;
		String[] extract=new String[pList.size()];
		String regBegin,regEnd;
		String source = null;
		Integer count=pList.size();
		
//			Parser parser = null;
			HttpClientSource clientSource=new HttpClientSource();
		    source =clientSource.AnalySource(path, encond);
			
		/*try {
			parser = new Parser(path);
			if(encond!=null){
				parser.setEncoding(encond);
			}
			NodeList list;
			list = parser.parse(null);
			source=list.toHtml();*/
			
		if(source!=null && !source.equals("")){
			Pattern pattern = Pattern.compile("\r|\n");
			Matcher matcher=pattern.matcher(source);
			source=matcher.replaceAll("");
		}else{
			return null;
		}
		//内容处理范围
		String conScopeBegin=guidereg.getKgConbegin();
		String conScopeEnd=guidereg.getKgConend();
		if(((conScopeBegin!=null && !conScopeBegin.equals(""))|| (conScopeEnd!=null && !conScopeEnd.equals(""))) && guidereg.getKgLevel().equals("true")){
			
			Integer begin,end;
			if(conScopeBegin!=null && conScopeEnd==null){
				begin=source.indexOf(conScopeBegin);
				end=source.length();
			}else if(conScopeBegin==null && conScopeEnd!=null){
				begin=0;
				end=source.indexOf(conScopeEnd);
			}else{
				begin=source.indexOf(conScopeBegin);
				end=source.indexOf(conScopeEnd,begin+conScopeBegin.length());
			}
			if(begin==-1 || end==-1){
				source=null;
				System.out.println("内容提取范围标志错误！");
			}else{
				begin=begin+conScopeBegin.length();
				source=source.substring(begin,end);
			}
		}
		String imageDown;
		for(int t=0;t<pList.size();t++){
			
			pReg=(WkTPickreg)pList.get(t);
			regBegin=pReg.getKpRegbegin();
			regEnd=pReg.getKpRegend();
			imageDown=pReg.getKpImagedownload();
			
			Integer beg=source.indexOf(regBegin);
			Integer e=source.indexOf(regEnd,beg+regBegin.length());
			String result = null;
			if(beg!=-1 && e!=-1 && (beg<e)){
			 
			 beg=beg+regBegin.length();
			 result=source.substring(beg, e);
			
			}else{
			  System.out.println(path+pReg.getKpRegname()+"规则采集失败！");
			}
		if(result==null || result.equals("")|| result.equals(null)){
				extract[t]="";
				count--;
		}else{
		
			if(pReg.getKpRetainTags()!=null && !pReg.getKpRetainTags().equals("")){
			
			String tagSource=pReg.getKpRetainTags().replace("false,", "").trim();
			HtmlTags htmlTags=new HtmlTags();
			List<String> tList=htmlTags.analyTags(tagSource);
			
			boolean isScript=true;//剔除JS标示
			boolean isStyle=true;//剔除样式显示
			
			StringBuffer sb=new StringBuffer();
			String sourceTag;
			for(int a=0;a<tList.size();a++){
				sourceTag=(String) tList.get(a).trim();
				if(sourceTag.equalsIgnoreCase("SCRIPT")|| sourceTag.equals("script") || sourceTag.equals("Script")){
					isScript=false;
				}else if(sourceTag.equalsIgnoreCase("STYLE") || sourceTag.equals("style") || sourceTag.equals("Style")){
					isStyle=false;
				}
				sb.append("(?!(?i)");
				sb.append(sourceTag);
				sb.append("|/(?i)");
				sb.append(sourceTag);
				sb.append(")");
			}
			String regExp="<"+sb.toString()+"[^>]*>";
			if(isScript){
				result=result.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>", "");
			}
			if(isStyle){
				result=result.replaceAll("<style[^>]*>([\\s\\S]*)</style>", "");
			}
			
			result=result.replaceAll(regExp, "");
			result="<html>"+result;
			List<String> tlList=retainTags(result);
			
			String tag;
			for(int w=0;w<tlList.size();w++){
				tag=(String) tlList.get(w);
				if(tag.equals("A")){
					result=showLink(result, path,encond,extractask,pReg);
				}else if(tag.equals("IMG") && imageDown.equals("false")){
					result=showContentPic(result, extractask, encond, path);
				}else if(tag.equals("IMG") && imageDown.equals("true") && extractask.getKePubtype()==Long.parseLong("1")){
					result=imageLocation(result,extractask,encond,path);
				}
				
			}
			
			result=result.replace("<html>", "");
			
			extract[t]=extractByReplace(result,pReg);
		
			/*String sourceTag,dataTag;
			for(int a=0;a<tList.size();a++){
				sourceTag=(String) tList.get(a);
				for(int b=0;b<tagList.size();b++){
					dataTag=(String) tagList.get(b);
					if(sourceTag.trim().equals(dataTag.trim())){
						
						if(sourceTag.equals("IMG") && downImage.equals("true") && extractask.getKePubtype()==Long.parseLong("1")){
							
							result=showContentPic(result,extractask,encond,path);
						}
						tagList.remove(b);
					}
				}
			}
			
			extract[t]=removeTag(result, tagList);*/
			
		}else{
			
			result=result.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>", "");
			result=result.replaceAll("<style[^>]*>([\\s\\S]*)</style>", "");
			
			result=result.replaceAll("\t|\n|\r", "");
			result=result.replaceAll("</?[^>]+>", "");
			
			extract[t]=extractByReplace(result,pReg);
			
			Integer totalLen=source.length();
			if((e+regEnd.length()<source.length())){
				source=source.substring(e+regEnd.length(),totalLen);
			}
		
	}
  }
		
		
		//---------------------
		if(pReg.getKpIsMerage().equals("true")){
			
			List<WkTPickreg> mList=new ArrayList<WkTPickreg>();
			String pageSign=guidereg.getKgPagesign();
			Integer pageCount=guidereg.getKgPagecount();
			mList.add(pReg);
			extract[t]=MerageNextPage(path,pageSign,pageCount,pReg.getKpMerage(),t,extract[t],mList);
			
		}
		//-----------------------
		
		
}//采集for结束
		
		if(count==0){
				return null;
			}else{
				return extract;
			}
		
		/*} catch (ParserException e3) {
			e3.printStackTrace();
		}*/
			
//		return null;
			
}
	
	@Test
	public static void main(String[] args){
		/*String len="1959年毕业于莫斯科动力学院电力系，同年到中国科学院电工研究所工作。1986年提升为研究员";
		String reg="\\d{4}";
		String old="hh";
		
		Pattern pattern=Pattern.compile(reg);
		Matcher matcher=pattern.matcher(len);
		if(matcher.find()){
			System.out.println(matcher.group(0));
		}
		len=len.replaceAll(reg, old);
		String path="http://oa.hebut.edu.cn//zlb/04250.nsf/0/1303FE6E218B9718482578A8000A9A2E/$file/（高校科技成果展洽会）议程.doc";
		String r=path.substring(path.lastIndexOf("/")+1, path.length());
		System.out.println(r);*/
		
		String path="http://oa.hebut.edu.cn//zlb$http://oa.hebut.edu.cn//zlb2$http://oa.hebut.edu.cn//zlb3$";
		BeginUrl bUrl=new BeginUrl();
		List t=bUrl.analyUrl(path);
		for(int i=0;i<t.size();i++){
			System.out.println(t.get(i));
		}
		
	}
	
	
	/* 合并内容的下一页 */
	private String MerageNextPage(String path,String sign,Integer count,String replaceCon,int i,String result0,List<WkTPickreg> list){
		
		StringBuffer buffer=new StringBuffer();
		List merageList = null;
		
		if(sign==null || count==null){
			System.out.println("内容合并下一页错误！");
			return result0;
		}else{
			boolean isNext=true;
//			List merageList = null;
			while(isNext){
				PageNext pageNext=new PageNext();
				merageList=pageNext.extractNextConPage(path,_encond,sign, count);
				isNext=false;
			}
			if(merageList!=null && merageList.size()>0){
				buffer.append(result0);
				
				String path2;
				for(int p=0;p<merageList.size();p++){
					path2=(String)merageList.get(p);
					String[] resutlt=extractByTags(path2, wkTExtractask, _encond, list, wkTGuidereg);
					if(resutlt!=null){
						buffer.append(replaceCon);
						buffer.append(resutlt[0].toString());
					}
					
				}
				return buffer.toString();
			}else{
				return result0;
			}
			
		}
		
	}
	
	
	
	/* 采集结果的替换规则 */
	private String extractByReplace(String source,WkTPickreg pickreg){
		
		List replaceList=replaceService.findByPid(pickreg.getKpId());//替换规则列表
		if(replaceList!=null){
			
			String result = null;
			
			WkTReplace replace;
			String oldValue;
			String newValue;
			String reg,old,all;
			
			for(int r=0;r<replaceList.size();r++){
				
				replace=(WkTReplace)replaceList.get(r);
				oldValue=replace.getOldValue();
				newValue=replace.getNewValue();
				reg=replace.getIsReg().trim();
				old=replace.getIsReplaceOld();
				all=replace.getIsReplaceAll();
				
				if(reg.equals("false")&& old.equals("false")&& all.equals("false")){
					source=source.replace(oldValue, newValue);
					return source;
				}if(reg.equals("true") && old.equals("false")&& all.equals("false")){
					source=source.replaceAll(oldValue, newValue);
					return source;
				}if(reg.equals("true") && old.equals("true") && all.equals("false")){
					Pattern pattern=Pattern.compile(old);
					Matcher matcher=pattern.matcher(source);
					if(matcher.find()){
						result=matcher.group(0);
					}
					return result;
				}if(reg.equals("true") && old.equals("false")&& all.equals("true")){
					source=source.replaceAll(oldValue, newValue);
				}if(reg.equals("false") && old.equals("false") && all.equals("true")){
					source=source.replace(oldValue, newValue);
					return source;
				}
				
				
			}
		}
		return source;
		
	}
	
	
	/*链接处理*/
	private String showLink(String result,String path,String encond,WkTExtractask extractask,WkTPickreg pickreg){
		
		try {
			
		Parser parser=new Parser(result);
		NodeFilter filter=new NodeClassFilter(LinkTag.class);
		NodeList linkList=parser.parse(null);
		NodeList list=linkList.extractAllNodesThatMatch(filter,true);
		if(list.size()>0){
		
		LinkTag linkTag;
		URL url;
			for(int l=0;l<list.size();l++){
				linkTag=(LinkTag) list.elementAt(l);
				url=new URL(new URL(path),linkTag.getLink());
				String httpUrl=url.toString();
				if(extractask.getKePubtype()==Long.parseLong("1") && pickreg.getKpDemodownload().equals("true")){
					//保存附近，默认路径
					String localPath=demoLoadDown(httpUrl,extractask);
					linkTag.setLink(localPath);
				}else{
					linkTag.setLink(httpUrl);//转化绝对路径
				}
				
			}
			result=linkList.toHtml();
	    }
		
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	/*图片显示处理并转化路径*/
	private String showContentPic(String result, WkTExtractask extractask,
			String encond, String path) {
		URL url;
	try {	
	
		Parser parser=new Parser(result);
		parser.setEncoding(encond);
		NodeFilter filter=new NodeClassFilter(ImageTag.class);
		NodeList sourceList=parser.parse(null);
		NodeList list=sourceList.extractAllNodesThatMatch(filter,true);
		
		if(list.size()>0){
		
			ImageTag iTag;
			for(int l=0;l<list.size();l++){
				iTag=(ImageTag)list.elementAt(l);
			try {
				url=new URL(new URL(path),iTag.getImageURL());
				String httpUrl=url.toString();
		        iTag.setImageURL(httpUrl);
		         
			} catch (MalformedURLException e) {
				System.out.println("网站图片相对网址解析错误！");
			}
			}//for
			result=sourceList.toHtml();
		}
		
	} catch (ParserException e) {
		System.out.println("源码不能解析！");
	}
		return  result;
}
	
	
	/* 图片下载并转化路径 */
	private String imageLocation(String result,WkTExtractask extractask,String encond,String path){
		
		URL url;
		String imagUrl;
		
		try {
			
			String pa=BeanFactory.getPicturePath();
			String web=BeanFactory.getWebName().trim();
			String address=BeanFactory.getAddress();
//			String pathLast="http://localhost:"+GetWebport()+"/"+pa.substring(pa.indexOf(web),pa.length())+"/"+extractask.getKeName()+"-"+extractask.getKeId()+"\\";
			String pathLast=address+"/"+pa.substring(pa.indexOf(web),pa.length())+"/"+extractask.getKeId()+"\\";
			pathLast=pathLast.replace("\\", "/");
			String filePath =pa+"\\"+extractask.getKeId()+"\\";
			File folder=new File(filePath);
			if(!folder.exists()){
				folder.mkdirs();
			}
			Parser parser=new Parser(result);
			parser.setEncoding(encond);
			NodeFilter filter=new NodeClassFilter(ImageTag.class);
			NodeList sourceList=parser.parse(null);
			NodeList list=sourceList.extractAllNodesThatMatch(filter,true);
			
			if(list.size()>0){
			
				ImageTag iTag;
			for(int l=0;l<list.size();l++){
				iTag=(ImageTag)list.elementAt(l);
				try {

					imagUrl=iTag.getImageURL();
					url=new URL(new URL(path),iTag.getImageURL());
				
					String httpUrl=url.toString();
					
					String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1);
					
					BufferedInputStream in= new BufferedInputStream(url.openStream());
				    FileOutputStream file= new FileOutputStream(new File(filePath+fileName));
			           int t;
			           while ((t = in.read()) != -1) {
			              file.write(t);
			           }
			           file.close();
			           in.close();
			           
			           iTag.setImageURL(pathLast+fileName);
//			           
//			           System.out.println(pathLast+fileName);
			         
				} catch (MalformedURLException e) {
					System.out.println("网站图片相对网址解析错误！");
				} catch (IOException e) {
					System.out.println("图片存储位置错误！");
				}
			}//for
				result=sourceList.toHtml();
		  }
			
		} catch (ParserException e) {
			System.out.println("源码不能解析！");
		}
		return  result;
		
	}
	
	
	/* 附近下载  */
	private String demoLoadDown(String path,WkTExtractask extractask){
		
		URL url;
		String demlUrl = null;
		String web=BeanFactory.getWebName().trim();
		String pa=BeanFactory.getDemoPath();
//		String pa=Sessions.getCurrent().getWebApp().getRealPath("/UserFiles/File/");
		String pathLast=BeanFactory.getAddress()+"/"+pa.substring(pa.indexOf(web),pa.length())+"/"+extractask.getKeName()+"-"+extractask.getKeId()+"\\";
		pathLast=pathLast.replace("\\", "/");
		String filePath =pa+"\\"+extractask.getKeName()+"-"+extractask.getKeId()+"\\";
		
		File f = new File(filePath);
		if(!f.exists()){
			f.mkdirs();
		}
		String fileName = null;
		try {
			
			url = new URL(path);
			InputStream is = url.openStream();
			
			fileName=path.substring(path.lastIndexOf("/")+1,path.length());
			demlUrl=pathLast + fileName;
			OutputStream os = new FileOutputStream(filePath+fileName);

			int bytesRead = 0;
			byte[] buffer = new byte[8192];

			while((bytesRead = is.read(buffer,0,8192)) != -1){
			os.write(buffer, 0, bytesRead);
			}
			
			os.close();
			is.close();
			
			} catch (Exception e2) {
			e2.printStackTrace();
			}
		return demlUrl;
		
	}
	
	
	
	
	/* 提取符合标记的标签  */
	private synchronized List<String> retainTags(String source){
		
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
	
	private synchronized String removeTag(String source,List<String> tagList){
		String tag;
		for(int i=0;i<tagList.size();i++){
			tag=tagList.get(i);
			source=source.replaceAll("(<"+tag+"[^>]*>|</"+tag+">)","").trim();
			source=source.replaceAll("(<"+tag.toLowerCase()+"[^>]*>|</"+tag.toLowerCase()+">)","").trim();
		}
		return source;
	}
	
	
	
	 /** 
     * 方法说明：抽取基础URL地址 
     * 输入参数：nodes 网页标签集合 
     */  
    private void extractAllScriptNodes(NodeList nodes) {  
        NodeList filtered = nodes.extractAllNodesThatMatch(new TagNameFilter(  
                "BASE"), true);  
        if (filtered != null && filtered.size() > 0) {  
            Tag tag = (Tag) filtered.elementAt(0);  
            String href = tag.getAttribute("href");  
            if (href != null && href.length() > 0) {  
                try {  
                    strWeb = new URL(href);  
                } catch (MalformedURLException e) {  
  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
}
