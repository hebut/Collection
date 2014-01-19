package com.uniwin.webkey.infoExtra.newsmanage;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Html;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;



public class NewsConEditWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map params;	
	Window newsconWindow;
	WkTOrinfo oinfo;
	Textbox kititle,kititle2,kikeys,kisource,kimemo,ptime;
	Html kicontent;
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	Toolbarbutton delete;
	public void afterCompose()
	{
		params=this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}
	public void initWindow(WkTOrinfo oinfo)
	{
		this.oinfo=oinfo;
		kititle.setValue(oinfo.getKoiTitle());
		kititle2.setValue(oinfo.getKoiTitle2());
		kisource.setValue(oinfo.getKoiSource());
		kimemo.setValue(oinfo.getKoiMemo());
		ptime.setValue(oinfo.getKoiPtime());
		initOinfocnt(orinewsService.getOriInfocnt(oinfo.getKoiId()));
		
	}
	public void  initOinfocnt(List<WkTOrinfocnt> rlist)
	{
		String con="";
		for(int i=0;i<rlist.size();i++)
		{
			WkTOrinfocnt infcnt=rlist.get(i);
			con+=infcnt.getKoiContent();
		}
		kicontent.setContent(con);
	}
	public void onClick$delete() throws InterruptedException
	{
		if(Messagebox.show("确定要删除吗？", "确认", 
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
		{
			List<WkTOrinfocnt> clist=orinewsService.getOriInfocnt(oinfo.getKoiId());
			if(clist.size()!=0)
			{
				for(int i=0;i<clist.size();i++)
				{
					WkTOrinfocnt infocnt=clist.get(i);
					orinewsService.delete(infocnt);
				}
			}
			orinewsService.delete(oinfo);
			
			 Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
	    	 this.detach();
	    	 Events.postEvent(Events.ON_CHANGE, this, null);
		}
	}
	
	public void onClick$back(){
		newsconWindow.detach();
	}
}