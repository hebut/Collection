   package com.uniwin.webkey.core.ui;


import java.io.IOException;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;



public class InfoShow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	
	private NewsServices info_newsService=(NewsServices) SpringUtil.getBean("info_newsService");
	
	Label title,source,author,time,url;
	Html content;
	Button print;
	WkTInfo info;
	Hbox pics;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	

	public void initWindow(WkTInfo info) throws IOException
	{
		this.info=info;
		title.setValue(info.getKiTitle());
		source.setValue(info.getKiSource());
		author.setValue(info.getKiAuthname());
		time.setValue(info.getKiCtime().substring(0, 10));
		final String u=info.getKiUrl();
		String r = null;
		if(u!=null && u.length()>20){
			r=u.substring(0,20)+"...";
		}
		url.setValue(r);
		url.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				Executions.getCurrent().sendRedirect(u,"_blank");
			}
			
		});
		
		List list=info_newsService.getInfocnt(info.getKiId());
		initInfocnt(list);
		List flist=info_newsService.getFile(info.getKiId());
		if(flist.size()!=0)
		{ for(int i=0;i<flist.size();i++)
		{
			WkTFile file=(WkTFile) flist.get(i);
			 Image img=new Image();
			 String pa = "/upload/info"+"/"+file.getId().getKfName().trim();
				img.setSrc(pa);
				img.setWidth("200px");
				   img.setHeight("200px");
				   img.setParent(pics);
		}
		}
	}	


	public void  initInfocnt(List rlist){
		String con="";
		for(int i=0;i<rlist.size();i++)
		{
			WkTInfocnt inf=(WkTInfocnt)rlist.get(i);
			con+=inf.getKiContent();
		}
		content.setContent(con);
	}


	public void onClick$close(){
		this.detach();
	}

}
