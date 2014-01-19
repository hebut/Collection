package com.uniwin.webkey.infoExtra.infosubject;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.model.WkTSubject;

public class AddSubject extends Window implements AfterCompose{

	Textbox subject,subjectCon;

	Textbox suburl;
	
	private InfoSubjectService infosubjectService=(InfoSubjectService)SpringUtil.getBean("infosubjectService");
	Users user;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}

	
	public void onClick$sure(){
		
		String sub=subject.getValue();
		String subu = suburl.getValue();
		String subCon=subjectCon.getValue();
		if(sub.equals("") || subCon.equals("")){
			
			try {
				Messagebox.show("请将信息填充完整！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else{
			
			WkTSubject wkTSubject=new WkTSubject();
			wkTSubject.setSubTitle(sub);
			wkTSubject.setSubUrl(subu);
			wkTSubject.setSubContent(subCon);
			Date date=new Date();
			String subTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			wkTSubject.setSubTime(subTime);
			wkTSubject.setSubStatus(0);
			wkTSubject.setSubSource(user.getUserId());
			infosubjectService.save(wkTSubject);
			refurbish();
			this.detach();
		}
		
	}
	
	public void onClick$canel(){
		this.detach();
	}
	
	private void refurbish(){
		Events.postEvent(Events.ON_CHANGE,this,null);
	}
	
}
