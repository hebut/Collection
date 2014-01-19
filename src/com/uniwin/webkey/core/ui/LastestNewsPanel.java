package com.uniwin.webkey.core.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Panel;
import org.zkoss.zul.api.Toolbarbutton;

import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.topicmanage.InfoShow;

public class LastestNewsPanel extends Panel implements AfterCompose{
    
	private InfoIdAndDomainId infoIdAndDomainId = (InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainId");
	private Listbox lastNewsList;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		List<WkTInfo> infolist0 = infoIdAndDomainId.LastestNews();
		List<WkTInfo> infolist1 = new ArrayList<WkTInfo>();
		if(infolist0.size()>6)
		{
		for(int i=0;i<7;i++){
			infolist1.add(infolist0.get(i));
		}
		}
		else
		{
			for(int i=0;i<infolist1.size();i++){
				infolist1.add(infolist0.get(i));
			}
		}
		lastNewsList.setModel(new ListModelList(infolist1));
		lastNewsList.setItemRenderer(new ListitemRenderer(){

			public void render(Listitem item, Object arg1) throws Exception {
				final WkTInfo info = (WkTInfo)arg1;
				item.setValue(info);
				item.setHeight("30px");
				Listcell c_index = new Listcell();
				c_index.setImage("/css/default/images/new.gif");
				Listcell c_news = new Listcell();
				InnerButton newbutton = new InnerButton();
				String con = info.getKiTitle();
				if(con!=null && con.length()>30){
					con = con.substring(0, 30)+"...";
				}
				newbutton.setLabel(con);
				newbutton.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/core/showdetail.zul", null, null);
						infoShow.initWindow(info);
						infoShow.doHighlighted();	
					}
					
				});
				c_news.appendChild(newbutton);
				Listcell c_date = new Listcell();
				String date = info.getKiCtime().toString().substring(0,10);
				c_date.setLabel(date);
				Listcell c_source = new Listcell();
				c_source.setLabel(info.getKiSource());
				item.appendChild(c_index);
				item.appendChild(c_news);
				item.appendChild(c_date);
				item.appendChild(c_source);
				
			}
			
		});
		
		
		
	}

}
