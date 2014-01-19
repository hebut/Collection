package com.uniwin.webkey.infoExtra.infosubject;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.model.WkTSubject;

public class EditSubject extends Window implements AfterCompose{

	Textbox subjectTitlle,subjectCon,suburl;
	WkTSubject wkTSubject;
	private InfoSubjectService infosubjectService=(InfoSubjectService)SpringUtil.getBean("infosubjectService");
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin(WkTSubject subject) {
		// TODO Auto-generated method stub
		this.wkTSubject=subject;
		subjectTitlle.setValue(subject.getSubTitle());
		suburl.setValue(subject.getSubUrl());
		subjectCon.setValue(subject.getSubContent());
	}

	
	public void onClick$sure(){
		
		String title=subjectTitlle.getValue();
		String con=subjectCon.getValue();
		String subu = suburl.getValue();
		if(title.equals("")|| con.equals("")){
			
			try {
				Messagebox.show("将信息填充完整！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			wkTSubject.setSubTitle(title);
			wkTSubject.setSubUrl(subu);
			wkTSubject.setSubContent(con);
			infosubjectService.update(wkTSubject);
			refurbish();
			try {
				Messagebox.show("更改成功！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
