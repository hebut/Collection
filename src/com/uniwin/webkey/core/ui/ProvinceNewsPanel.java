package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Panel;

import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.topicmanage.InfoShow;

public class ProvinceNewsPanel extends Panel implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InfoIdAndDomainId infoIdAndDomainId = (InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainId");
	private Listbox provinceNewsList;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		List<WkTInfo> pinfolist0 = infoIdAndDomainId.provinceNews();
		List<WkTInfo> pinfolist1 = new ArrayList<WkTInfo>();
		if(pinfolist0.size()>7)
		{
		for(int i=0;i <8;i++){
			pinfolist1.add(pinfolist0.get(i));
		}
		}
		else
		{
			for(int i=0;i <pinfolist0.size();i++){
				pinfolist1.add(pinfolist0.get(i));
			}
		}
		provinceNewsList.setModel(new ListModelList(pinfolist1));
		provinceNewsList.setItemRenderer(new ListitemRenderer(){

			public void render(Listitem item, Object arg1) throws Exception {
				final WkTInfo info = (WkTInfo)arg1;
				item.setValue(info);
				item.setHeight("28px");
				Listcell c_index = new Listcell();
				c_index.setLabel("▪");
				Listcell c_title = new Listcell();
				InnerButton newbutton = new InnerButton();
				String con = info.getKiTitle();
				if(con.length()>30){
					con = con.substring(0, 30);
				}
				newbutton.setLabel(con);
				newbutton.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/core/showdetail.zul", null, null);
						infoShow.initWindow(info);
						infoShow.doHighlighted();	
					}
					
				});
				c_title.appendChild(newbutton);
				Listcell c_date = new Listcell();
				c_date.setLabel(info.getKiCtime().toString().substring(0, 10));
				Listcell c_source = new Listcell();
				c_source.setLabel(info.getKiSource());
				item.appendChild(c_index);
				item.appendChild(c_title);
				item.appendChild(c_date);
				item.appendChild(c_source);
			}
			
		});
		
	}
}
