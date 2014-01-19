package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.itf.TopicIdAndDomainIdService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTTopicIdAndDomainId;


public class SortEdit extends Window implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Toolbarbutton save,back;
	TopicListbox topicList;
	Textbox sortName;
	Window wt;
	List<WkTInfoDomain> list;
	TopicIdAndDomainIdService topicIdAndDomainIdService;
	Tree topicTree;
	TopicTreeData topicData;
	Users user;
	String listName;
	PersonTopicService personTopicS;
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}
    
	
	public void initWindow(List<WkTInfoDomain> dList){
	    
		this.list = dList;
		topicList.initAllTopicSortSelect(null, null,user.getUserId());
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<dList.size();i++){
			sb.append(dList.get(i).getKiName()+";");
		}
		sortName.setValue(sb.toString());
		
	}
	
	public void onClick$save() throws InterruptedException{
		if(topicList.getSelectedItem()==null){
			Messagebox.show("请选择所属专题！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		else{
			WKTPersonTopic p = (WKTPersonTopic)topicList.getSelectedItem().getValue();
			String oname= p.getKiName();
			if(p.getKptPid()==0){
				return;
			}
			else{
				for(int i=0;i<list.size();i++){
					WkTTopicIdAndDomainId tad = new WkTTopicIdAndDomainId();
					tad.setKptId(p.getKptId());
					tad.setKiId(list.get(i).getKiId());
					tad.setKuId(user.getUserId());
					if(oname==null){
						oname=list.get(i).getKiName()+";";
					}
					else{
						oname = oname+list.get(i).getKiName()+";";
					}
					topicIdAndDomainIdService.save(tad);
				}
				p.setKiName(oname);
				personTopicS.update(p);
				try 
				{
					Messagebox.show("保存成功！", "Information", Messagebox.OK,
							Messagebox.INFORMATION);
					Events.postEvent(Events.ON_CHANGE,this, null);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				} 
				    this.detach();
				}
		}
	}
	
	public void onClick$back(){
		wt.detach();
	}
}
