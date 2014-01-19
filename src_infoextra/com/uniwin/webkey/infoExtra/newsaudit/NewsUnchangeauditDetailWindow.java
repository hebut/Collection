package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * 控制信息审核中不可修改的界面
 * 2010-3-20
 * @author whm
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;



public class NewsUnchangeauditDetailWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	//信息数据访问接口
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	IUsersManager userService;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	
	WkTInfocnt Infocnt ;
	WkTInfo info,inf;
	WkTDistribute dis;
	//Users user;
	WkTInfocntId infocntid;
	//不可修改的界面所用到的各种组件
	Textbox kititle,kititle2,kiordno,kikeys,kisource,kcid,share,kibdadd,kiSort;
	Textbox fbtitle,kitype,wselected,cselected,infoname;
	Datebox kivaliddate;
	Html kiaddress,kicontent;
	Toolbarbutton download,chegao;
	Label kishow,inorout,kisite,bdfil;
	Hbox bdnews,ljnews,wdnews,tupian,wd,isite,site;
	
	Separator sep1,sep2;
	Listbox upList;
	List nameList=new ArrayList();
	List flist;
	ListModelList modelListbox;
	
	InfoIdAndDomainId infoIdAndDomainIdService=(InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainIdService");
	List selectList;
	
	private Textbox subjectTerm;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		  modelListbox=new ListModelList(nameList);
			upList.setItemRenderer(new ListitemRenderer(){
				public void render(Listitem arg0, Object arg1) throws Exception {
					if(arg1 instanceof Media){
					 Media media=(Media)arg1;
					 arg0.setValue(arg1);
					 arg0.setLabel(media.getName());
					}else{
					 WkTFile f=(WkTFile)arg1;
					 arg0.setValue(arg1);
					 arg0.setLabel(f.getId().getKfShowname());
					}
				}			
			});		
	}
	public WkTInfo getInfo() {
		return info;
	}
	//判断状态,控制页面显示
	public void initWindow(WkTDistribute dist) throws IOException {
		this.dis= dist;
		 WkTInfo info=newsService.getWkTInfo(dis.getKiId());
		 WkTExtractask task=(WkTExtractask) taskService.getTaskBykeId(dis.getKeId()).get(0);
		 List sortList=infoIdAndDomainIdService.findByInfoId(info.getKiId());
		 //	user=(Users)userService.getUserByuid(info.getKuId());
			initInfocnt(newsService.getChildNewsContent(dis.getKiId()));
		if(dis.getKbStatus().toString().trim().equals("2")||dis.getKbStatus().toString().trim().equals("3"))
		{
			chegao.setVisible(false);
		}
		kititle.setValue(info.getKiTitle());
		kititle2.setValue(info.getKiTitle2());
		kisource.setValue(info.getKiSource());
		subjectTerm.setValue(info.getWkTSubjectTermNames());
		kikeys.setValue(info.getKiKeys());
		
		if(sortList!=null && sortList.size()>0){
			WkTInfoDomain domain;
			StringBuffer sb=new StringBuffer("");
			for(int d=0;d<sortList.size();d++){
				domain=(WkTInfoDomain)sortList.get(d);
				sb.append(domain.getKiName()+";");
			}
			kiSort.setValue(sb.toString());
		}
		
		kivaliddate.setValue(ConvertUtil.convertDate(info.getKiValiddate()));	
        kcid.setValue(task.getKeName());
		
		 //加载附件
	     if(info.getKiType().trim().equals("1")){
	    	 flist=newsService.getFile(dis.getKiId());
        	 modelListbox.addAll(flist);
        	 upList.setModel(modelListbox);
        }
	     
	}
	


	
	public WkTInfocnt getInfocnt(){
		return Infocnt;
	}
	
	//获取信息主要内容
	public void  initInfocnt(List rlist){
		String con="";
		for(int i=0;i<rlist.size();i++){
			WkTInfocnt inf=(WkTInfocnt)rlist.get(i);
			con+=inf.getKiContent();
		}
		kicontent.setContent(con);
	}
	
	
//下载附件文件
public void onClick$down() throws InterruptedException, FileNotFoundException{
	
	 if(modelListbox.getSize()==0) return;
	 
	 Listitem it=upList.getSelectedItem();		 
	 if(it==null) {
		 if(modelListbox.getSize()>0){
			 it=upList.getItemAtIndex(0);
		 }
	 }
	 if(it.getValue() instanceof Media){
		 Filedownload.save((Media)it.getValue());
	 }else{
	  WkTFile f=(WkTFile)it.getValue();
	  String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
	  Filedownload.save(new File(path+"\\"+f.getId().getKfName()), null);
	 }
}
//下载或查看图片
public void onClick$kiimage() throws FileNotFoundException
	{
		 String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
		   File f=new File(path.trim()+"\\".trim()+newsService.getWkTInfo(dis.getKiId()).getKiImage().toString().trim());  
		   Filedownload.save(f,null);
	}
//对已发布信息撤稿
public void onClick$chegao() throws InterruptedException
{
	if(Messagebox.show("确定撤稿吗？", "确认", 
			Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
	{
	dis.setKbStatus("4");
	newsService.update(dis);
	
	//共享信息撤稿
	if(dis.getKbFlag().toString().trim().equals("0")&&newsService.getDistributeShare(dis.getKiId()).size()!=0)
	{System.out.println(dis);
		List list=new ArrayList();
        list=newsService.getDistributeShare(dis.getKiId());
		for(int i=0;i<list.size();i++)
		{   
			WkTDistribute d=(WkTDistribute)list.get(i);
			d.setKbStatus("2");
			newsService.update(d);
		}
	}
	Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
	this.detach();	
	Events.postEvent(Events.ON_CHANGE, this, null);
	}
}
//删除信息
public void onClick$delete() throws InterruptedException
{  
	 if(Messagebox.show("确定要删除吗？", "确认", 
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
		{
		 if(dis.getKbFlag().toString().trim().equals("0"))
		{ 
		 if(newsService.getFile(dis.getKiId()).size()!=0)
		 {
			 List annex=new ArrayList();
			 annex=newsService.getFile(dis.getKiId());
			 for(int i=0;i<annex.size();i++)
			 {
				 newsService.delete((WkTFile)annex.get(i));
			 }
		 }
		 WkTInfo info=newsService.getWkTInfo(dis.getKiId());
		 if(info.getKiType().trim().equals("1"))
		 {
		 List cnt=newsService.getInfocnt(dis.getKiId());
		 for(int j=0;j<cnt.size();j++)
		 newsService.delete((WkTInfocnt)cnt.get(j));
		 }
		List d=newsService.getDistributeList(dis.getKiId());
		for(int i=0;i<d.size();i++)
		{
		 newsService.delete((WkTDistribute)d.get(i));
		}
		 newsService.delete(newsService.getWkTInfo(dis.getKiId()));
		
		 //Users user=(Users)Sessions.getCurrent().getAttribute("user");
		 //mlogService.saveMLog(WkTMlog.FUNC_CMS, "删除信息，id:"+dis.getKiId(), user);
		}
		 else
		 {
			 newsService.delete(dis);
			// WkTUser user=(WkTUser)Sessions.getCurrent().getAttribute("user");
		//mlogService.saveMLog(WkTMlog.FUNC_CMS, "删除信息，id:"+dis.getKiId(), user);
		 }
		 Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
	         this.detach();
	     	Events.postEvent(Events.ON_CHANGE, this, null);
		}
		
}

}
