package com.uniwin.webkey.infoExtra.newspub;
/**
 * 控制对不可更改的信息的查看
 * @author whm
 * 2010-3-18
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTFlog;



public class NewsUnchangedDetailWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	WkTInfocnt Infocnt ;
	WkTInfo info,inf;
	WkTDistribute dis;
	WkTInfocntId infocntid;
	WkTFlog flog;
	//信息数据访问接口b
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
	InfoIdAndDomainId idAndDomainIdService=(InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainIdService");
	//不可修改的界面所用到的各种组件
	Textbox kititle,kititle2,kikeys,kisource,kcid,share;
	Textbox fbtitle,kitype,infoname,kivaliddate,kiSort;
	Button del,down,download;
	Toolbarbutton back,chegao,delete;
	Listbox upList;
	Label file;
	Html kicontent;
	Hbox wdnews,tupian,pics;
	ListModelList modelListbox;
	List nameList=new ArrayList();
	List flist;
	Window unchangednewsWin;
	
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
	//判断信息状态，控制界面显示，初始化信息值
	public void initWindow(WkTDistribute dis) {
		this.dis=dis;
		 WkTInfo info=newsService.getWkTInfo(dis.getKiId());
		 WkTExtractask task=(WkTExtractask) taskService.getTaskBykeId(dis.getKeId()).get(0);
		 List<WkTInfoDomain> sortList=idAndDomainIdService.findByInfoId(info.getKiId());
		
		 String state=dis.getKbStatus().trim();
		 chegao.setVisible(false);
		 if(dis.getKbStatus().toString().trim().equals("0"))
		 {
			 chegao.setVisible(true);
		 }
		 if(state.equals("0"))
		 {
			 delete.setVisible(false);
		 }
		initInfocnt(newsService.getChildNewsContent(info.getKiId()));
		
		kititle.setValue(info.getKiTitle());
		kititle2.setValue(info.getKiTitle2());
		kisource.setValue(info.getKiSource());
		kikeys.setValue(info.getKiKeys());
		subjectTerm.setValue(info.getWkTSubjectTermNames());
		if(sortList!=null && sortList.size()>0){
			WkTInfoDomain domain;
			StringBuffer sb=new StringBuffer("");
			for(int d=0;d<sortList.size();d++){
				domain=sortList.get(d);
				sb.append(domain.getKiName()+";");
			}
			kiSort.setValue(sb.toString());
		}
		
		kcid.setValue(task.getKeName());	
		kivaliddate.setValue(info.getKiValiddate());
		  //加载附件
		   if(info.getKiType().trim().equals("1"))
        		 {
			   flist=newsService.getFile(dis.getKiId());
        		 modelListbox.addAll(flist);
        	     upList.setModel(modelListbox);
        		 }
	}
	
	public WkTInfocntId getInfocntid()
	{
		return infocntid;
	}
	public void  initInfocntid(WkTInfocntId infocntid)
		{
			this.infocntid=infocntid;	
		}
	//对已发布信息撤稿
	public void onClick$chegao() throws InterruptedException
	{
		if(Messagebox.show("确定撤稿吗？", "确认", 
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
		{
		dis.setKbStatus("1");
		newsService.update(dis);
		//共享信息撤稿
		if(newsService.getDistributeShare(dis.getKiId()).size()!=0)
		{
			 List list=new ArrayList();
			 list=newsService.getDistributeShare(dis.getKiId());
			for(int i=0;i<list.size();i++)
			{ 
				WkTDistribute d=(WkTDistribute)list.get(i);
				d.setKbStatus("1");
				newsService.update(d);
			}
		}
		Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
		this.detach();	
		Events.postEvent(Events.ON_CHANGE, this, null);
		}
	}
	//下载或查看图片
	 public void onClick$kiimage() throws FileNotFoundException
		{
			 String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
			   File f=new File(path.trim()+"\\".trim()+newsService.getWkTInfo(dis.getKiId()).getKiImage().toString().trim());  
			   Filedownload.save(f,null);
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
public WkTInfocnt getInfocnt()
{
	return Infocnt;
}
//获取信息文档内容
public void  initInfocnt(List rlist)
	{
		String con="";
		for(int i=0;i<rlist.size();i++){
			WkTInfocnt inf=(WkTInfocnt)rlist.get(i);
			con+=inf.getKiContent();
		}
		kicontent.setContent(con);
	}
//删除单条信息
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
	 if((newsService.getWkTInfo(dis.getKiId())).getKiType().trim().equals("1"))
	 {
	List cnt=newsService.getInfocnt(dis.getKiId());
	if(cnt.size()!=0)
	{
	for(int i=0;i<cnt.size();i++)
	{
	 newsService.delete((WkTInfocnt)cnt.get(i));
	}
	}
	 }
	List d=newsService.getDistributeList(dis.getKiId());
	for(int i=0;i<d.size();i++)
	{
	 newsService.delete((WkTDistribute)d.get(i));
	}
	 newsService.delete(newsService.getWkTInfo(dis.getKiId()));
	}
	else 
	 {
		 newsService.delete(dis);
	 }
	Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
	unchangednewsWin.detach();
     Events.postEvent(Events.ON_CHANGE, this, null);
	}
    }
}
