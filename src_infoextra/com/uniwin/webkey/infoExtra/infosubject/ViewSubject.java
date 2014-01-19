package com.uniwin.webkey.infoExtra.infosubject;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.model.WkTSubject;

public class ViewSubject extends Window implements AfterCompose{

	
	Textbox subjectTitlle,subjectCon,suburl;
	WkTSubject subject;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin(WkTSubject wkTSubject) {
		// TODO Auto-generated method stub
		this.subject=wkTSubject;
		subjectTitlle.setValue(wkTSubject.getSubTitle());
		suburl.setValue(wkTSubject.getSubUrl());
		subjectCon.setValue(wkTSubject.getSubContent());
		
	}

	public void onClick$close(){
		this.detach();
	}
	
}
