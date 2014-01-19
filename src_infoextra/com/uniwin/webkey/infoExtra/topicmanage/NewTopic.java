package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;


public class NewTopic extends Window implements AfterCompose{

	Toolbarbutton save,back;
	Textbox topicName,topicCon;
	Window wt;
	PersonTopicService persontopicService;
	WKTPersonTopic personTopic;
	Users user;

	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users)Sessions.getCurrent().getAttribute("users");
	}
    
	
	public void initWindow(WKTPersonTopic p){
	    this.personTopic = p;
	}
	
	public void onClick$save(){
		if(topicName.getValue().equals("")){
			try{
				Messagebox.show("请输入专题名称");
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		else{
			WKTPersonTopic newPTopic = new WKTPersonTopic();
			newPTopic.setKptName(topicName.getValue());
			newPTopic.setKptPid(personTopic.getKptId());
			newPTopic.setKptIssue(topicCon.getValue());
			newPTopic.setKuId(user.getUserId());
			persontopicService.save(newPTopic);
			try 
			{
				Messagebox.show("保存成功！", "Information", Messagebox.OK,Messagebox.INFORMATION);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			refreshTree();
			this.detach();
		}
			
	}
	
	public void onClick$back(){
		wt.detach();
	}
	
	public void refreshTree(){
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
}
