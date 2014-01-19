package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zhtml.Caption;
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
import org.zkoss.zul.Window;
import org.zkoss.zul.api.Panel;

import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.topicmanage.InfoShow;

public class ShowNews extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Listbox newsList;
	private Panel news;
	private Integer id;
	private InfoIdAndDomainId infoIdAndDomainId = (InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainId");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		
	}
	
	public void initWindow(Integer id){
		this.id = id;
		List<WkTInfoDomain> domain = infoIdAndDomainId.findDomainById(id);
		news.setTitle(domain.get(0).getKiName());
		List<WkTInfo> ninfolist0 = infoIdAndDomainId.findByIdNews(id);
		System.out.println(ninfolist0.size());
		newsList.setModel(new ListModelList(ninfolist0));
		newsList.setItemRenderer(new ListitemRenderer(){

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
