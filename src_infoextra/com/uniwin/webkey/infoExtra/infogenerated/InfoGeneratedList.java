package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Window;

public class InfoGeneratedList extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6684369143977330919L;
	private Treecell newInfo,sendedInfo,subscribeInfo,templateManage;
    private Tab tabNewInfo,tabSendedInfobox,tabSubscribeInfobox,tabTemplateManagebox;
    private Tabpanel tabPanelNewInfo,tablePanelSendedInfo,tablePanelSubscribeInfo,tablePanelTemplateManage;
	private Map<Treecell, Window> windowMap = new HashMap<Treecell, Window>();
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		newInfo.addEventListener(Events.ON_CLICK, new EventListener() {

			public void onEvent(Event arg0) throws Exception {
				tabNewInfo.setVisible(true);
				tabNewInfo.setSelected(true);
				String uri = "/apps/infoExtra/content/infogenerated/new_info/newInfo.zul";
				NewInfo newInfoWindow = (NewInfo) windowMap.get(newInfo);
				if (newInfoWindow == null) {
					newInfoWindow = (NewInfo) Executions.createComponents(uri,
							tabPanelNewInfo, null);
					windowMap.put(newInfo, newInfoWindow);
				}
				newInfoWindow.initWindow();
			}
		});
		sendedInfo.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				tabSendedInfobox.setVisible(true);
				tabSendedInfobox.setSelected(true);
				String uri = "/apps/infoExtra/content/infogenerated/sended/sended.zul";
				SendedInfo sendedMailWindow = (SendedInfo) windowMap
						.get(sendedInfo);
				if (sendedMailWindow == null) {
					sendedMailWindow = (SendedInfo) Executions
							.createComponents(uri, tablePanelSendedInfo, null);
					windowMap.put(sendedInfo, sendedMailWindow);
					
				}
				sendedMailWindow.initWindow();
			}
		});
		subscribeInfo.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {

				tabSubscribeInfobox.setVisible(true);
				tabSubscribeInfobox.setSelected(true);
				String uri = "/apps/infoExtra/content/infogenerated/subscribeinfo/subinfo.zul";
				SubscribeInfo subscribeMailWindow = (SubscribeInfo) windowMap
						.get(subscribeInfo);
				if (subscribeMailWindow == null) {
					subscribeMailWindow = (SubscribeInfo) Executions
							.createComponents(uri, tablePanelSubscribeInfo, null);
					windowMap.put(subscribeInfo, subscribeMailWindow);
					
				}
				subscribeMailWindow.initWindow();
			}
		});
		templateManage.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {

				tabTemplateManagebox.setVisible(true);
				tabTemplateManagebox.setSelected(true);
				String uri = "/apps/infoExtra/content/infogenerated/template/template.zul";
				TemplateManage templateWindow = (TemplateManage) windowMap
						.get(templateManage);
				if (templateWindow == null) {
					templateWindow = (TemplateManage) Executions
							.createComponents(uri, tablePanelTemplateManage, null);
					windowMap.put(templateManage, templateWindow);
					
				}
				templateWindow.initWindow();
			}
		});			
		tabSendedInfobox.setVisible(true);
		tabSendedInfobox.setSelected(true);
		String uri = "/apps/infoExtra/content/infogenerated/sended/sended.zul";
		SendedInfo sendedMailWindow = (SendedInfo) windowMap
				.get(sendedInfo);
		if (sendedMailWindow == null) {
			sendedMailWindow = (SendedInfo) Executions
					.createComponents(uri, tablePanelSendedInfo,
							null);
			windowMap.put(sendedInfo, sendedMailWindow);
			
		}	
		sendedMailWindow.initWindow();

	}
	

}
