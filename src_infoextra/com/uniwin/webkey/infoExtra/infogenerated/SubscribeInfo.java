package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
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
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.itf.InfoGeneratedService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.newscenter.InfoShow;


public class SubscribeInfo extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8086059540670285507L;
	private Listbox infolistbox;
	@SuppressWarnings("unused")
	private Users user;
	private Textbox title,source;
	private InfoGeneratedService infoGeneratedService;
	private NewsServices info_newsService=(NewsServices) SpringUtil.getBean("info_newsService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");		

	}
	
	public void initWindow(){
		List<WkTInfo> infoList = infoGeneratedService.findDistributeInfoByTitleAndSource(title.getValue(), source.getValue());
		if(infoList!=null&&infoList.size()!=0){
			infolistbox.setModel(new ListModelList(infoList));
		}
		else{
			infolistbox.setModel(new ListModelList());
		}
		infolistbox.setItemRenderer(new ListitemRenderer() {
			
			public void render(Listitem item, Object data) throws Exception {
				final WkTInfo u = (WkTInfo)data;
				item.setValue(u);
				Listcell c0 = new Listcell();
				Listcell c1 = new Listcell();
				c1.setLabel(item.getIndex()+1+"");
				Listcell c2 = new Listcell();
				String str = u.getKiTitle();
				if (str != null && !str.equals("")) {
					int len = str.trim().length();
					if (len > 20) {
						str = str.substring(0, 20);
					}
					InnerButton inb = new InnerButton();
					inb.setLabel(str.trim());
					inb.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event arg0) throws Exception {
							WkTInfo wkTInfo=info_newsService.getWkTInfo(u.getKiId());
							InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/infocenter/showdetail.zul", null, null);
							infoShow.initWindow(wkTInfo);
							infoShow.doHighlighted();						
						}
					});
					c2.appendChild(inb);
				}	
				
				//
				WkTDistribute dis = info_newsService.getDistriByiid(u.getKiId());
				//
				Listcell c3 = new Listcell();
				c3.setLabel(u.getKiPtime());
				//c3.setLabel(dis.getKbDtime());
				Listcell c4 = new Listcell();
				c4.setLabel(u.getKiSource());
				Listcell c5 = new Listcell();
				Toolbarbutton collection =new Toolbarbutton();
				collection.setImage("/images/content/issue_ico.gif");
				collection.addEventListener(Events.ON_CLICK, new EventListener() {
					
					public void onEvent(Event arg0) throws Exception {						
						List<WkTInfo> list = infoGeneratedService.findWKTInfoByKiId(u.getKiId());
						if(list!=null&&list.size()!=0){
							WkTInfo info = list.get(0);
							info.setKiCState(1);
							infoGeneratedService.update(info);
							Messagebox.show("该信息订阅成功!!");
						}
						else{
							Messagebox.show("该信息已订阅!!");
						}
					}
				});
				c5.appendChild(collection);
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
				item.appendChild(c4);
				item.appendChild(c5);
			}
		});
	}
	
	public void onClick$search(){
		initWindow();
	}

}
