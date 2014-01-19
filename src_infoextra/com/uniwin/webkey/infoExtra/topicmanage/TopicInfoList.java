package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.itf.TopicIdAndDomainIdService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.topicmanage.InfoShow;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

public class TopicInfoList extends Window implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TopicIdAndDomainIdService topicIdAndDomainid;
	private Users user;
	private Listbox infoListbox;
	private ListModelList modelList;
	Datebox begintime,endtime;
	Textbox keys;
	Listitem title,content;
	Toolbarbutton searchnews,resetnews;
	WKTPersonTopic pt;
	String bt,et,k,s,flag;
	PersonTopicService personTopicS;
	private NewsServices info_newsService=(NewsServices) SpringUtil.getBean("info_newsService");
	public void afterCompose() {
	
		Components.wireVariables(this, this);
	    Components.addForwards(this, this);
	    user=(Users)Sessions.getCurrent().getAttribute("users");
	    Date date0 = new Date();
	    begintime.setValue(date0);
	    Date date1 = new Date();
	    endtime.setValue(date1);
	    
		
	}
	
	public void reLoadList(){
         modelList.clear();
         initWindows(pt);
	}
    
	public void initWindows(WKTPersonTopic personTopic){
		this.pt = personTopic;
		List<WkTDistribute> infoList = new ArrayList<WkTDistribute>();
		if(personTopic.getKptPid()==0){
			infoList = topicIdAndDomainid.findByDomainIdAndUserId(user.getUserId());
		}
		else{
			List clist=personTopicS.getChildType(pt.getKptId(),user.getUserId());
			List cdlist=new ArrayList();
			int m=1;
			cdlist.add(0,pt.getKptId());
			for(int i=0;i<clist.size();i++)
			{
				WKTPersonTopic ch=(WKTPersonTopic)clist.get(i);
				cdlist.add(m, ch.getKptId());
				m++;
				List c1list=personTopicS.getChildType(ch.getKptId(),user.getUserId());
				for(int j=0;j<c1list.size();j++)
				{
					WKTPersonTopic ch1=(WKTPersonTopic)c1list.get(i);
					cdlist.add(m, ch1.getKptId());
					m++;
					List c2list=personTopicS.getChildType(ch1.getKptId(),user.getUserId());
					for(int k=0;k<c2list.size();k++)
					{
						WKTPersonTopic ch2=(WKTPersonTopic)c2list.get(i);
						cdlist.add(m, ch2.getKptId());
						m++;
						List c3list=personTopicS.getChildType(ch2.getKptId(),user.getUserId());
						for(int l=0;l<c3list.size();l++)
						{
							WKTPersonTopic ch3=(WKTPersonTopic)c3list.get(i);
							cdlist.add(m, ch3.getKptId());
							m++;
						}
					}
				}
				
			}
			infoList = topicIdAndDomainid.findByTopicId(cdlist,user.getUserId());
		}
		
		if(infoList.size()!=0){
			modelList = new ListModelList(infoList);
			infoListbox.setModel(modelList);
			infoListbox.setItemRenderer(new ListitemRenderer(){

				public void render(Listitem item, Object arg1) throws Exception {
					final WkTDistribute distribute = (WkTDistribute)arg1;
					item.setValue(distribute);
					Listcell c0 = new Listcell(item.getIndex()+1+"");
					String con = distribute.getKbTitle();
					if(con!=null && con.length()>30){
						con = con.substring(0, 30)+"...";
					}
					Listcell c1 = new Listcell(con);
					c1.setTooltiptext(distribute.getKbTitle());
					WkTInfo info=info_newsService.getWkTInfo(distribute.getKiId());
					Listcell c2 = new Listcell(info.getKiSource());
					Listcell c3 = new Listcell(info.getKuName());
					Listcell c4 = new Listcell(distribute.getKbDtime());
					item.appendChild(c0);
					item.appendChild(c1);
					item.appendChild(c2);
					item.appendChild(c3);
					item.appendChild(c4);
					c1.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							
							WkTInfo wkTInfo=info_newsService.getWkTInfo(distribute.getKiId());
							InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/topicmanage/showdetail.zul", null, null);
							infoShow.initWindow(wkTInfo);
							infoShow.doHighlighted();
							
						}
						
					});
				}
				
			});
		}
		else{
			modelList = new ListModelList(infoList);
			infoListbox.setModel(modelList);
		}

}
		
	
	public void onClick$searchnews(){
		
		if(keys.getValue()!=null && !keys.getValue().equals("")){
			k=keys.getValue();
			if(content.isSelected()){
				flag="1";
			}
  			else{
  				flag="2";
  			}
		}else{
			k="";
		}
		Date b=begintime.getValue();
		Date en=endtime.getValue();
		if(b==null || en==null){
			try {
				Messagebox.show("起止时间不允许为空！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		String bt = ConvertUtil.convertDateAndTimeString(b);
		String et = ConvertUtil.convertDateAndTimeString(en);
		if(bt.compareTo(et)>0)
 		 {
 			try {
				Messagebox.show("开始时间不能大于截止时间！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
 			return;
 		 }
 		if(pt.getKptPid()==0){
 			List<WkTDistribute> infolist = topicIdAndDomainid.NewsSearch(flag,k,bt,et,user.getUserId());
 			searchInfo(infolist);
 		}
 		else{
 			List<WkTDistribute> infolist = topicIdAndDomainid.NewsSearch(flag,k,bt,et,pt.getKptId(),user.getUserId());
 			searchInfo(infolist);
 		}
	}
	
	public void searchInfo(List<WkTDistribute> infolist){
		modelList.clear();
		modelList.addAll(infolist);
		infoListbox.setModel(modelList);
		infoListbox.setItemRenderer(new ListitemRenderer(){

			public void render(Listitem item, Object arg1) throws Exception {
				final WkTDistribute distribute = (WkTDistribute)arg1;
				item.setValue(distribute);
				Listcell c0 = new Listcell(item.getIndex()+1+"");
				String con = distribute.getKbTitle();
				if(con!=null && con.length()>30){
					con = con.substring(0, 30)+"...";
				}
				Listcell c1 = new Listcell(con);
				c1.setTooltiptext(distribute.getKbTitle());
				WkTInfo info=info_newsService.getWkTInfo(distribute.getKiId());
				Listcell c2 = new Listcell(info.getKiSource());
				Listcell c3 = new Listcell(info.getKuName());
				Listcell c4 = new Listcell(distribute.getKbDtime());
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
				item.appendChild(c4);
				c1.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						
						WkTInfo wkTInfo=info_newsService.getWkTInfo(distribute.getKiId());
						InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/topicmanage/showdetail.zul", null, null);
						infoShow.initWindow(wkTInfo);
						infoShow.doHighlighted();
						
					}
					
				});
			}
			
		});
	}

	public void onClick$resetnews(){
		Date date0 = new Date();
		begintime.setValue(date0);
		Date date1 = new Date();
		endtime.setValue(date1);
		keys.setValue("");
		reLoadList();
	}
}
