package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * 选择引用信息时查看详情
 * @author whm
 * 2010-9-9
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTFlog;



public class NewsAuditSelDetailWindow extends Window implements AfterCompose {
	private static final long serialVersionUID = 1L;
	WkTInfocnt Infocnt ;
	WkTInfo info,inf;
	//WkTChanel chanel;
	WkTDistribute dis;
	WkTFlog flog;
	//信息数据访问接口
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	//管理日志数据访问接口
	//MLogService mlogService;
	//不可修改的界面所用到的各种组件
	Textbox kititle,kititle2,kitype;
	Button down,download;
	Listbox upList;
	Html kiaddress,kicontent;
	Separator sep1,sep2;
	Hbox bdnews,ljnews,wdnews,tupian,pics,wd;
	ListModelList modelListbox;
	List nameList=new ArrayList();
	Window wins;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        modelListbox=new ListModelList(nameList);
        //附件列表
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
		newsService=(NewsServices)SpringUtil.getBean("newsService");
        dis=(WkTDistribute)Executions.getCurrent().getAttribute("kiTitle");
        initInfo(dis);
	}

	public WkTInfo getInfo() {
		return info;
	}
	//判断信息状态，控制界面显示，初始化信息值
	public void initInfo(WkTDistribute dis) 
	{
		 this.dis= dis;
		 WkTInfo info=newsService.getWkTInfo(dis.getKiId());
		 String state=dis.getKbStatus().trim();
		if(info.getKiType().trim().equals("1"))
		{
			ljnews.setVisible(false);
			bdnews.setVisible(false);
			initInfocnt(newsService.getChildNewsContent(info.getKiId()));
			kitype.setValue("输入");   
			sep1.setVisible(true);
			sep2.setVisible(true);
		}
		else if(info.getKiType().trim().equals("2"))
		{
			ljnews.setVisible(false);
			wdnews.setVisible(false);
			wd.setVisible(false);
			tupian.setVisible(false);
            kitype.setValue("上传文件");   
		}
		else if(info.getKiType().trim().equals("3"))
		{
			wdnews.setVisible(false);
			wd.setVisible(false);
			bdnews.setVisible(false);
			tupian.setVisible(false);
			kiaddress.setContent("<a href=\""+info.getKiAddress()+"\" target=\"_blank\">"+info.getKiAddress()+"</a>");
			kitype.setValue("外部链接"); 
		}
		kititle.setValue(info.getKiTitle());
		kititle2.setValue(info.getKiTitle2());
		  //加载附件
        List flist=newsService.getFile(dis.getKiId());
        modelListbox.addAll(flist);
        upList.setModel(modelListbox);
	}



	//下载或查看图片
	 public void onClick$kiimage() throws FileNotFoundException
		{
			 String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
			   File f=new File(path.trim()+"\\".trim()+newsService.getWkTInfo(dis.getKiId()).getKiImage().toString().trim());  
			   Filedownload.save(f,null);
		}
	//下载上传的文件
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
		  String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/");
		  Filedownload.save(new File(path+"\\"+f.getId().getKiId()+"\\"+f.getId().getKfName()), null);
		 }
	}
	//下载本地文件
	public void onClick$download() throws FileNotFoundException
	{
		  String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info").trim();
		String fileName=(newsService.getWkTInfo(dis.getKiId())).getKiAddress().trim();
		String name =path.trim()+"\\".trim()+fileName.trim();
		File down=new File(name);
		Filedownload.save(down,null);
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
public void onClick$back()
{
	wins.detach();
}
}
