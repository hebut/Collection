package com.uniwin.webkey.infoExtra.core;

/**
 * 保存到数据库
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listcell;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.itf.InfoService;
import com.uniwin.webkey.infoExtra.itf.LinkService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTLinkurl;
import com.uniwin.webkey.infoExtra.model.WkTOriInfocntId;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.util.BeanFactory;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;


public class MyCallableSave{

	private WkTExtractask eTask;
	private List<WkTGuidereg> gList;
	private List<WkTPickreg> pList;
	private LinkService linkService;
	private InfoService infoService;
	private String path,path1;
	
	private NewsServices newsService=(NewsServices)BeanFactory.getBean("info_newsService");
	private Listcell _cell;
	
	LinkCollection linkCollection;
	
	public MyCallableSave(WkTExtractask eTask,List<WkTGuidereg> gList,List<WkTPickreg> pList,LinkService linkService,InfoService infoService,String path,String path1){
		super();
		this.eTask=eTask;
		this.gList=gList;
		this.pList=pList;
		this.linkService=linkService;
		this.infoService=infoService;
		this.path=path;
		this.path1=path1;
	}
	
	public synchronized void call(){
		
		linkCollection=new LinkCollection();
		
		String cirSign = null;
		String urlTags=eTask.getKeBeginurl();
		AnalyBegUrl analyBegUrl=new AnalyBegUrl();
		List<String> bList=analyBegUrl.checkBeginUrl(urlTags);
	
//		WebEncoding wEncoding=new WebEncoding();
//		String encond1=wEncoding.AnalyEnconding(bList.get(0));
		String encond1=eTask.getKeUrlencond();
		
		for(String u:bList){
			linkCollection.addUnvisitedUrl(u,0);
		}
		
		WkTGuidereg gReg;
		String reg;
		String urlBegin,urlEnd;
		String gLevel,gModel;
		String gCirculate;
		
		for(int g=0;g<gList.size();g++){
			
			gReg=(WkTGuidereg)gList.get(g);
			gLevel=gReg.getKgLevel();
			gModel=gReg.getKgModel();
			gCirculate=gReg.getKgCirculate();
			urlBegin=gReg.getKgUrlbegin();
			urlEnd=gReg.getKgUrlend();
			//提取下一页导航
			if(gReg.getKgPagesign()!=null && gReg.getKgPagecount()!=null && gReg.getKgNextpage().trim().equals("Y")){
				String pageSign=gReg.getKgPagesign();
				Integer pageCount=gReg.getKgPagecount();
				if(gLevel.equals("N")){
					PageNext pNext=new PageNext();
					pNext.extractNextPage(linkCollection,encond1, pageSign, pageCount);
				}
				
			}
			
			//网页模板url导航
			if( gModel!=null && !gModel.equals("") && gLevel.trim().equals("N")){
				int unVisitCon=linkCollection.getunVisitedUrlNum();
				reg=gReg.getKgModel().replace("*",".*").replace("?","\\?").trim();
				for(int w=0;w<unVisitCon;w++){
				
					HtmlParserLinks hParserLinks=new HtmlParserLinks();
					String u=linkCollection.unVisitedUrlDeQueue();
					
					if(u==null){
						break;
					}
					
					List<String> list=hParserLinks.extractUrl(u,encond1, reg,urlBegin,urlEnd);
					int k=1;
					k++;
					String unVisitUrl;
//					Md5 md5;
//					String urlMd5;
					for(int v=0;v<list.size();v++){
						unVisitUrl=list.get(v);
						if(linkService.findByIdAndUrl(unVisitUrl, eTask.getKeId()).size()==0){
//							md5=new Md5();
//							urlMd5=md5.MD5(unVisitUrl);
							WkTLinkurl linkurl=new WkTLinkurl();
							linkurl.setKeId(eTask.getKeId());
							linkurl.setKlUrl(unVisitUrl);
//							linkurl.setKlMd5(urlMd5);
							linkurl.setKlStatus(Long.parseLong("0"));
							linkService.save(linkurl);
						}
						linkCollection.addUnvisitedUrl(unVisitUrl,2);
					}
				
			}
		}
			if((gModel==null || gModel.equals(""))&& gCirculate!=null && !gCirculate.equals("")){
				CirPick cirPick;
				String u;
				String[] extractResult;
				while (!linkCollection.unVisitedUrlsEmpty() && linkCollection.getVisitedUrlNum()<=1000) {
					u=linkCollection.unVisitedUrlDeQueue();
					cirPick=new CirPick();
					CirEntity cirEntity;
					List<CirEntity> cirList=cirPick.cirExtract(u, gCirculate, pList);
					extractResult=new String[pList.size()];
					for(int cir=0;cir<cirList.size();cir++){
						cirEntity=cirList.get(cir);
						extractResult=cirEntity.getCEntity();
					}
				}
			
			}
			cirSign=gCirculate;
		
   }
		
		List<WkTLinkurl> urlList = null;

		if(eTask.getKeRepeat().equals("false")){
			urlList=linkService.findByKeId(eTask.getKeId());
		}else{
			 urlList=linkService.findByIdAndStatus(eTask.getKeId(), Long.parseLong("0"));
		}
		
		
		//分类：1.直接发布 2.审核后发布
		if(eTask.getKeAutopub().equals("false")){
			
			infoPackage(urlList,pList,eTask);
			
		}else{//直接发布
			
			infoPublish(urlList,pList,eTask);
		}
		
		
  }

	
	
	//信息审核流程
	private void infoPackage(List<WkTLinkurl> urlList,List<WkTPickreg> pList,WkTExtractask extractask){
		
		infoPick pick=new infoPick();
		
		WkTLinkurl linkurl;
		String content = null;
		WkTOrinfo orinfo;
//		boolean hasContent=false;
		String encond=extractask.getKeConencond();
		
		DateFormat dateform=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		String[] extractResult;
		for(int p=0;p<urlList.size();p++){
			linkurl=urlList.get(p);
			extractResult=new String[pList.size()];
			extractResult=pick.extractByTags(linkurl.getKlUrl(),eTask,encond,pList,gList.get(gList.size()-1));
			if(extractResult!=null){

				String Ctime=dateform.format(date);
				boolean hasContent=false;
				orinfo=new WkTOrinfo();
				orinfo.setKeId(eTask.getKeId());
				orinfo.setKoiStatus(Long.parseLong("0"));
				orinfo.setKoiUrl(linkurl.getKlUrl());
				orinfo.setKoiCtime(Ctime);
				WkTPickreg pickreg = null;
//				Long len = null;
				
				for(int f=0;f<pList.size();f++){
					pickreg=(WkTPickreg)pList.get(f);
					if(pickreg.getKpDataname().equals("title")){
						orinfo.setKoiTitle(extractResult[f]);
					}else if(pickreg.getKpDataname().equals("title2")){
						orinfo.setKoiTitle2(extractResult[f]);
					}else if(pickreg.getKpDataname().equals("source")){
						orinfo.setKoiSource(extractResult[f]);
					}else if(pickreg.getKpDataname().equals("koiAuthname")){
						orinfo.setKoiAuthname(extractResult[f]);
					}else if(pickreg.getKpDataname().equals("pTime")){
						orinfo.setKoiPtime(extractResult[f]);
					}else if(pickreg.getKpDataname().equals("content")){
						content=extractResult[f];
						hasContent=true;
//						len=pickreg.getKpConlength();
					}
				}
				
//			try{
				
				infoService.save(orinfo);
				linkurl.setKlStatus(Long.parseLong("1"));
				linkService.update(linkurl);
				
				
				if(hasContent){
//					  if(len!=null && len>0){
					  Long len=DataBaseSet.data_Len;
					  Long max=content.length()/len+1;
					  WkTOrinfocnt infocnt;
					  WkTOriInfocntId infocntid;
					  for(Long i=0L;i<max;i++){
					    	 infocnt=new WkTOrinfocnt();
					    	 infocntid=new WkTOriInfocntId(orinfo.getKoiId(),i+1);
					    	 infocnt.setId(infocntid);
						     Long be=i*len;
						     if(i==(max-1)){
						    	 infocnt.setKoiContent(content.substring(be.intValue())); 
						     }else{
						    	 Long en=(i+1)*len;
						    	 infocnt.setKoiContent(content.substring(be.intValue(),en.intValue()));
						     }
						     try{
						    	 infoService.save(infocnt);
						     }catch (Exception e) {
						    	 e.printStackTrace();
								System.out.println("内容存储失败！");
							}
						     
					     }
					  
					  }else{
//						  System.out.println("输入存储内容字段长度！");
					  }
				
			     //保存网页快照
				Html2MHTCompiler html2;
				String code=JQueryCode.getHtmlText(linkurl.getKlUrl(), eTask.getKeConencond());
				if(path!=""||path!=null)
				{}
				else
				{
				 path=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/mht");
				}
				if(path1!=""||path1!=null)
				{}
				else
				{
				 path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/html");
				}
				String fileName=orinfo.getKoiId()+"-"+DateUtil.getDateTimeString(new Date())+".mht";
				String fileName1=orinfo.getKoiId()+"-"+DateUtil.getDateTimeString(new Date())+".html";
				html2 =new Html2MHTCompiler(code,linkurl.getKlUrl(),eTask.getKeConencond(),path+"//"+fileName.trim());
				html2.compile();
				//html2.mht2html(path+"//"+fileName.trim(), path1+"//"+fileName1.trim());
				orinfo.setKoiMht(fileName);
				infoService.update(orinfo);
				
				
			/*}catch (Exception e) {
				hasContent=false;//关键点---------继续测试(⊙v⊙)嗯----  考虑是否更新链接的状态。。。
				System.out.println("信息存储失败！");
			}*/

					  
//				}//if
			}else{
				linkurl.setKlStatus(Long.parseLong("2"));
				linkService.update(linkurl);
			}
			
		}
		
			linkCollection.getUnVisitedUrl().deleteAll();
			linkCollection.getVisitedUrl().clear();
			System.out.println(eTask.getKeName()+"采集结束！");
		
}
	
	
	
	//信息直接发布流程
	//此处对内容存储字段，没有定死，读取数据库长度。
	private void infoPublish(List<WkTLinkurl> urlList,List<WkTPickreg> pList,WkTExtractask extractask){
		
		
		infoPick pick=new infoPick();
		
		boolean hasContent=false;
		String encond=extractask.getKeConencond();
		WkTLinkurl linkurl;
		String[] extractResult;
		Date date=new Date();;
		WkTInfo info;
		WkTPickreg pickreg = null;
		WkTDistribute distribute;
		for(int p=0;p<urlList.size();p++){
			linkurl=urlList.get(p);
			extractResult=new String[pList.size()];
			extractResult=pick.extractByTags(linkurl.getKlUrl(),eTask,encond,pList,gList.get(gList.size()-1));
			if(extractResult!=null){
				
				String time=ConvertUtil.convertDateAndTimeString(date.getTime());
				info=new WkTInfo();
				String content = null;
				Long len = null;
				for(int a=0;a<pList.size();a++){
					pickreg=(WkTPickreg)pList.get(a);
					if(pickreg.getKpDataname().equals("title")){
						info.setKiTitle(extractResult[a]);
					}else if(pickreg.getKpDataname().equals("title2")){
						info.setKiTitle2(extractResult[a]);
					}else if(pickreg.getKpDataname().equals("source")){
						info.setKiSource(extractResult[a]);
					}else if(pickreg.getKpDataname().equals("koiAuthname")){
						info.setKiAuthname(extractResult[a]);
					}else if(pickreg.getKpDataname().equals("pTime")){
						info.setKiPtime(extractResult[a]);
					}else if(pickreg.getKpDataname().equals("content")){
						content=extractResult[a];
						hasContent=true;
						len=pickreg.getKpConlength();
					}
				}
				info.setKeId(extractask.getKeId());
				info.setKiUrl(linkurl.getKlUrl());
				info.setKiShow("1");
				info.setKiHits(Integer.parseInt("0"));
				info.setKiValiddate("1900-01-01");
				info.setKiOrdno(Integer.parseInt("10"));
				info.setKuName("admin");
				info.setKuId(Long.parseLong(45+""));
				info.setKiTop("0");
				info.setKiType("1");
				info.setKiCtime(time);
				try {
					newsService.save(info);
					
					
					linkurl.setKlStatus(Long.parseLong("1"));
					linkService.update(linkurl);
				} catch (Exception e) {
					System.out.println("信息存储失败！");
				}
				
				if(hasContent){
					  
					  if(len!=null && len>0){
					  
					  Long max=content.length()/len+1;
					  WkTInfocnt infocnt;
					  WkTInfocntId infocntid;
					  for(Long i=0L;i<max;i++){
					    	 infocnt=new WkTInfocnt();
					    	 infocntid=new WkTInfocntId(info.getKiId(),i+1);
					    	 infocnt.setId(infocntid);
						     Long be=i*len;
						     if(i==(max-1)){
						    	 infocnt.setKiContent(content.substring(be.intValue()));
						     }else{
						       Long en=(i+1)*len;
						       infocnt.setKiContent(content.substring(be.intValue(),en.intValue()));
						     }
						     
						     try{
						    	 newsService.save(infocnt);
						     }catch (Exception e) {
								System.out.println(linkurl+"内容存储失败！");
							}
						     
					     }	 
					  
					  }else{
						  System.out.println("输入存储内容字段长度！");
					  }
					  
				}
				
				distribute=new WkTDistribute();
				distribute.setKiId(info.getKiId());
				distribute.setKeId(info.getKeId());
				distribute.setKbTitle(info.getKiTitle());
				distribute.setKbRight("0");
				distribute.setKbFlag("0");	   
				distribute.setKbStatus("0");
				distribute.setKcId(extractask.getKcId());
				distribute.setKbDtime(time);
				distribute.setKbEm("0");
				distribute.setKbMail("1");
				distribute.setKbStrong("0");
				distribute.setKbAutopub(1);
				newsService.save(distribute);
				
				 //保存网页快照
//				Html2MHTCompiler html2;
//				String code=JQueryCode.getHtmlText(linkurl.getKlUrl(), eTask.getKeConencond());
//				if(path!=""||path!=null)
//				{
//					
//				}
//				else
//				{
//				 path=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/mht");
//				}
//				
//				String fileName=info.getKiId()+"-"+DateUtil.getDateTimeString(new Date())+".mht";
//				html2 =new Html2MHTCompiler(code,linkurl.getKlUrl(),eTask.getKeConencond(),path+"//"+fileName.trim());
//				html2.compile();
//				info.setKiMht(fileName);
//				infoService.update(info);
				 //保存网页快照
				Html2MHTCompiler html2;
				String code=JQueryCode.getHtmlText(linkurl.getKlUrl(), eTask.getKeConencond());
				if(path!=""||path!=null)
				{}
				else
				{
				 path=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/mht");
				}
				if(path1!=""||path1!=null)
				{}
				else
				{
				 path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/html");
				}
				String fileName=info.getKiId()+"-"+DateUtil.getDateTimeString(new Date())+".mht";
				String fileName1=info.getKiId()+"-"+DateUtil.getDateTimeString(new Date())+".html";
				html2 =new Html2MHTCompiler(code,linkurl.getKlUrl(),eTask.getKeConencond(),path+"//"+fileName.trim());
				html2.compile();
				//html2.mht2html(path+"//"+fileName.trim(), path1+"//"+fileName1.trim());
				info.setKiMht(fileName); 
				infoService.update(info);
			}//extractResult!=null结束
		
		}//外层for结束
			
  }


	
	
}
