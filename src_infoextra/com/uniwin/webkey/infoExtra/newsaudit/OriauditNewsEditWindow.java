package com.uniwin.webkey.infoExtra.newsaudit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.core.DataBaseSet;
import com.uniwin.webkey.infoExtra.infosort.Sort2;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrifile;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;



public class OriauditNewsEditWindow extends Window implements AfterCompose {
	
	
	private static final long serialVersionUID = 1L;
	Map params;	
	WkTOrinfo oinfo;
	Textbox kititle,kititle2,kikeys,kisource,ptime,taskname,sort;
	FCKeditor kicontent;
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	private OriNewsService orinewsService=(OriNewsService)SpringUtil.getBean("info_orinewsService");
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	Toolbarbutton reset,choose;
	Users user;
	List flist;
	Listbox upList;
	ListModelList modelListbox;
	List slist=new ArrayList();
	
	Iframe preView;
	Textbox url;
	
	public void afterCompose()
	{
		params=this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
		//选择发布频道
		choose.addEventListener(Events.ON_CLICK,  new EventListener(){
			public void onEvent(Event event) throws Exception {
				Executions.getCurrent().setAttribute("mul","0");
				NewsauditTaskSelectWindow w=(NewsauditTaskSelectWindow)Executions.createComponents("/apps/infoExtra/content/newsaudit/taskselect.zul", null,null);
				w.doHighlighted();
				w.initWindow();
				w.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						NewsauditTaskSelectWindow ns=(NewsauditTaskSelectWindow) arg0.getTarget();
						slist=ns.getSlist();
						WkTExtractask et=(WkTExtractask) slist.get(0);
						taskname.setValue(et.getKeName());
					}
				});
			}	
		});
	}
	public void initWindow(WkTOrinfo oinfo)
	{
		this.oinfo=oinfo;
		WkTExtractask e=(WkTExtractask) taskService.getTaskBykeId(oinfo.getKeId()).get(0);
		kititle.setValue(oinfo.getKoiTitle());
		kititle2.setValue(oinfo.getKoiTitle2());
		kisource.setValue(oinfo.getKoiSource());
		kikeys.setValue(oinfo.getKoiKeys());
		taskname.setValue(e.getKeName());
		ptime.setValue(oinfo.getKoiPtime());
		initOinfocnt(orinewsService.getOriInfocnt(oinfo.getKoiId()));
		  //加载附件
        flist=newsService.getOrifile(oinfo.getKoiId());
        if(flist.size()!=0)
        {
		 modelListbox.addAll(flist);
	     upList.setModel(modelListbox);
        }
//		pics.getChildren().clear();
		//初始化显示图片
		/*if(oinfo.getKoiImage()!=null&&oinfo.getKoiImage().trim().length()>0){
		   Image img=new Image();
	 		String pa = "/upload/info"+"/"+oinfo.getKoiImage().trim();
	 		img.setSrc(pa);
		   img.setWidth("25px");
		   img.setHeight("25px");
		   img.setParent(pics);
		    Button b=new Button();
			b.setLabel("删除");
			b.addEventListener(Events.ON_CLICK,new org.zkoss.zk.ui.event.EventListener(){
			  public void onEvent(Event event) throws Exception {
			    pics.getChildren().clear();
			  }
			});
			b.setParent(pics);
		}*/
		
		String path=oinfo.getKoiUrl();
		url.setValue(path);
		preView.setSrc(path);
		
	}
	
	public void onClick$refresh(){
		
		String path=oinfo.getKoiUrl();
		url.setValue(path);
		preView.setSrc(path);
	}
	
	
	public void  initOinfocnt(List<WkTOrinfocnt> rlist)
	{
		String con="";
		for(int i=0;i<rlist.size();i++)
		{
			WkTOrinfocnt infcnt=rlist.get(i);
			con+=infcnt.getKoiContent();
		}
		kicontent.setValue(con);
	}
	//保存信息
	public void onClick$save() throws InterruptedException
	{
		if(kititle.getValue().equals(""))
		{
			Messagebox.show("标题不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		if(taskname.getValue().equals(""))
		{
			Messagebox.show("请选择发布频道！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		WkTInfo info=new WkTInfo();
		info.setKiTitle(kititle.getValue().trim());
		info.setKiTitle2(kititle2.getValue());
		if(slist.size()!=0)
		{
		WkTExtractask et=(WkTExtractask) slist.get(0);
		info.setKeId(et.getKeId());
		}
		else
		{
			info.setKeId(oinfo.getKeId());
		}
		info.setKiAuthname(oinfo.getKoiAuthname());
		info.setKiCtime(oinfo.getKoiCtime().trim());
		info.setKiHits(Integer.parseInt("0"));
		info.setKiKeys(kikeys.getValue());
		info.setKiOrdno(Integer.parseInt("10"));
		info.setKiPtime(oinfo.getKoiPtime());
		info.setKiAddress(oinfo.getKoiAddress());
		info.setKiImage(oinfo.getKoiImage());
		info.setKiValiddate("1900-01-01");
		info.setKiShow("1");
		info.setKiSource(kisource.getValue());
		info.setKiTop("0");
		info.setKiUrl(oinfo.getKoiUrl());
		info.setKiType("1");
		info.setKuId(Long.parseLong(user.getUserId().toString()));
		info.setKuName(user.getLoginName());
         newsService.save(info);
	     Long len=DataBaseSet.data_Len;
	     Long max=kicontent.getValue().length()/len+1;
	     for(Long i=0L;i<max;i++){
	    	 WkTInfocnt infocnt=new WkTInfocnt();
	    	 WkTInfocntId infocntid=new WkTInfocntId(info.getKiId(),i+1);
		     infocnt.setId(infocntid);
		     Long be=i*len;
		     if(i==(max-1)){
		    	 infocnt.setKiContent(kicontent.getValue().substring(be.intValue())); 
		     }else{
		       Long en=(i+1)*len;
		       infocnt.setKiContent(kicontent.getValue().substring(be.intValue(),en.intValue()));
		     }
		     newsService.save(infocnt);
	     }
	     WkTExtractask extractask=taskService.findById(info.getKeId());
	     WkTDistribute dis=new WkTDistribute();
	     dis.setKiId(info.getKiId());
	     dis.setKeId(info.getKeId());
	     dis.setKbTitle(info.getKiTitle());
	     dis.setKbRight("0");
	     dis.setKbStatus("1");
	     dis.setKbMail("0");
	     dis.setKbFlag("0");
	     dis.setKcId(extractask.getKcId());
	     
	     Date date=new Date();
	     dis.setKbDtime(ConvertUtil.convertDateAndTimeString(date.getTime()));
	     dis.setKbEm("0");
	     newsService.save(dis);
	     Messagebox.show("操作成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		 this.detach();
		 //删除原始信息表内容
	     List<WkTOrinfocnt> oinfolist=orinewsService.getOriInfocnt(oinfo.getKoiId());
	     for(int i=0;i<oinfolist.size();i++)
	     {
	     WkTOrinfocnt oinfocnt=oinfolist.get(i);
	     newsService.delete(oinfocnt);
	     }
	     List<WkTOrifile> ofilelist=orinewsService.getOrifile(oinfo.getKoiId());
	     if(ofilelist.size()!=0)
	     {
	    	 for(int j=0;j<ofilelist.size();j++)
	    	 {
	    	 WkTOrifile file=ofilelist.get(j);
	    	 newsService.delete(file);
	    	 }
	     }
	     newsService.delete(oinfo);
		 Events.postEvent(Events.ON_CHANGE, this, null);
	}
	//发布
	public void onClick$saudit() throws InterruptedException
	{
		if(kititle.getValue().equals(""))
		{
			Messagebox.show("标题不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		if(taskname.getValue().equals(""))
		{
			Messagebox.show("请选择发布频道！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		WkTInfo info=new WkTInfo();
		info.setKiTitle(kititle.getValue());
		info.setKiTitle2(kititle2.getValue());
		if(slist.size()!=0)
		{
		WkTExtractask et=(WkTExtractask) slist.get(0);
		info.setKeId(et.getKeId());
		}
		else
		{
			info.setKeId(oinfo.getKeId());
		}
		info.setKiAuthname(oinfo.getKoiAuthname());
		info.setKiCtime(oinfo.getKoiCtime());
		info.setKiHits(Integer.parseInt("0"));
		info.setKiKeys(kikeys.getValue());
		info.setKiOrdno(Integer.parseInt("10"));
		info.setKiPtime(oinfo.getKoiPtime());
		info.setKiAddress(oinfo.getKoiAddress());
		info.setKiImage(oinfo.getKoiImage());
		info.setKiShow("1");
		info.setKiValiddate("1900-01-01");
		info.setKiSource(kisource.getValue());
		info.setKiTop("0");
		info.setKiUrl(oinfo.getKoiUrl());
		info.setKiType("1");
		info.setKuId(Long.parseLong(user.getUserId().toString()));
		info.setKuName(user.getLoginName());
         newsService.save(info);
	     Long len=DataBaseSet.data_Len;
	     Long max=kicontent.getValue().length()/len+1;
	     for(Long i=0L;i<max;i++){
	    	 WkTInfocnt infocnt=new WkTInfocnt();
	    	 WkTInfocntId infocntid=new WkTInfocntId(info.getKiId(),i+1);
		     infocnt.setId(infocntid);
		     Long be=i*len;
		     if(i==(max-1)){
		    	 infocnt.setKiContent(kicontent.getValue().substring(be.intValue())); 
		     }else{
		       Long en=(i+1)*len;
		       infocnt.setKiContent(kicontent.getValue().substring(be.intValue(),en.intValue()));
		     }
		     newsService.save(infocnt);
	     }
	     WkTExtractask extractask=taskService.findById(info.getKeId());
	     WkTDistribute dis=new WkTDistribute();
	     dis.setKiId(info.getKiId());
	     dis.setKeId(info.getKeId());
	     dis.setKbTitle(info.getKiTitle());
	     dis.setKbRight("0");
	     dis.setKbFlag("0");	   
	     dis.setKbStatus("0");
	     dis.setKcId(extractask.getKcId());
	     Date date=new Date();
	     dis.setKbDtime(ConvertUtil.convertDateAndTimeString(date.getTime()));
	     dis.setKbEm("0");
	     newsService.save(dis);
	     
	   //发布html
//	 	Map root = new HashMap();
//	 	Sessions.getCurrent().setAttribute("root", root);
//	 	WKT_DOCLIST dList = new WKT_DOCLIST();
//	 	dList.singleNewsPublic(info, dis.getKcId());
	     
	     Messagebox.show("操作成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
	     //删除原始信息表内容
	     List<WkTOrinfocnt> oinfolist=orinewsService.getOriInfocnt(oinfo.getKoiId());
	     for(int i=0;i<oinfolist.size();i++)
	     {
	     WkTOrinfocnt oinfocnt=oinfolist.get(i);
	     newsService.delete(oinfocnt);
	     }
	     List<WkTOrifile> ofilelist=orinewsService.getOrifile(oinfo.getKoiId());
	     if(ofilelist.size()!=0)
	     {
	    	 for(int j=0;j<ofilelist.size();j++)
	    	 {
	    	 WkTOrifile file=ofilelist.get(j);
	    	 newsService.delete(file);
	    	 }
	     }
	     newsService.delete(oinfo);
		 this.detach();
		 Events.postEvent(Events.ON_CHANGE, this, null);
		
	}
	
	public void onClick$chooseSort(){
		
		/*Sort s=(Sort)Executions.createComponents("/apps/infoExtra/content/infosort/sort.zul", null, null);
		s.doHighlighted();
		s.initWin(sort);*/
		
		Sort2 s=(Sort2)Executions.createComponents("/apps/infoExtra/content/infosort/sort.zul", null, null);
		s.doHighlighted();
		
	}
	
	
	//重置
	public void onClick$reset()
	{
		initWindow(oinfo);
	}

}