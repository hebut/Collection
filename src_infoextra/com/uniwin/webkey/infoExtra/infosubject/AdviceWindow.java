package com.uniwin.webkey.infoExtra.infosubject;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.model.WkTSubject;


public class AdviceWindow extends Window implements AfterCompose{

	/**
	 * 反馈意见
	 */
	private static final long serialVersionUID = 1L;
	Textbox fankui;
	WkTSubject wkTSubject;
	Users user;
	Toolbarbutton sure; 
	private InfoSubjectService infosubjectService=(InfoSubjectService)SpringUtil.getBean("infosubjectService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users) Sessions.getCurrent().getAttribute("users");
	}

	public void initWindow(WkTSubject subject)
	{
		this.wkTSubject=subject;
		fankui.setValue(wkTSubject.getAdvice());
		if(!user.getLoginName().equals("superman") &&!user.getLoginName().equals("admin"))
		{
			sure.setVisible(false);
		}
		else
		{
			sure.setVisible(true);
		}
	}
	
	public void onClick$sure() throws InterruptedException
	{
		wkTSubject.setAdvice(fankui.getValue());
		infosubjectService.update(wkTSubject);
		Messagebox.show("保存成功！");
		this.detach();
	}
	public void onClick$canel()
	{
		this.detach();
	}
}