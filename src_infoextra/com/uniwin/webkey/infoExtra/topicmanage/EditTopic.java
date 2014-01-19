package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;


public class EditTopic extends Window implements AfterCompose{

	Toolbarbutton save,back;
	Textbox topicName,topicCon,sortName;
	Window ew;
	PersonTopicService personTopicS;
	WKTPersonTopic p;
	TopicListbox topiclist;
	Users user;

	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users)Sessions.getCurrent().getAttribute("users");
	}
    
	
	public void initWindow(WKTPersonTopic p){
	    this.p = p;
	    List<WKTPersonTopic> pt = personTopicS.findBykptId(p.getKptPid());
	    if(pt.size()!=0)
	    {
	    	 topiclist.initAllTopicSortSelect((WKTPersonTopic)pt.get(0),p,user.getUserId());
	    }
	    else
	    {
	    	 topiclist.initAllTopicSortSelect(null,p,user.getUserId());
	    	 //topiclist.setDisabled(true);
	    }
	    topicName.setValue(p.getKptName());
	    sortName.setValue(p.getKiName());
	    topicCon.setValue(p.getKptIssue());
	}
	
	public void onSelect$topiclist() throws InterruptedException
	{
		Listitem item=topiclist.getSelectedItem();
		WKTPersonTopic topic=(WKTPersonTopic) item.getValue();
		if(topic.getKptId().toString().trim().equals(p.getKptId().toString().trim()))
		{
			List<WKTPersonTopic>  wpt=personTopicS.findBykptId(p.getKptPid());
			topiclist.initAllTopicSortSelect(wpt.get(0),p,user.getUserId());
		}
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
			p.setKptName(topicName.getValue());
			Listitem item = topiclist.getSelectedItem();
			WKTPersonTopic newPTopic = (WKTPersonTopic)item.getValue();
			p.setKptPid(newPTopic.getKptId());
			p.setKiName(sortName.getValue());
			personTopicS.update(p);
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
		ew.detach();
	}
	
	public void refreshTree(){
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
}
