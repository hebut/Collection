package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.itf.InfoGeneratedService;
import com.uniwin.webkey.infoExtra.model.WKTInfoEmail;
import com.uniwin.webkey.infoExtra.newscenter.InfoShow;

public class SendedInfo extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -74062160343039238L;
	private Listbox sendMsglistbox;
	private Users user;
	private InfoGeneratedService infoGeneratedService;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}
	
	public void initWindow(){
		List<WKTInfoEmail> infoList = infoGeneratedService.findSendedInfoByUserId(user.getUserId());
		if(infoList!=null&&infoList.size()!=0){
			sendMsglistbox.setModel(new ListModelList(infoList));
		}
		else{
			sendMsglistbox.setModel(new ListModelList());
		}
		sendMsglistbox.setItemRenderer(new ListitemRenderer() {
			
			public void render(Listitem item, Object data) throws Exception {
				final WKTInfoEmail u = (WKTInfoEmail)data;
				item.setValue(u);
				Listcell c0 = new Listcell();
				Listcell c1 = new Listcell();
				c1.setLabel(item.getIndex()+1+"");
				Listcell c2 = new Listcell();
				c2.setLabel(u.getMailto());
				Listcell c3 = new Listcell();
				String str = u.getSubject();
				if (str != null && !str.equals("")) {
					int len = str.trim().length();
					if (len > 20) {
						str = str.substring(0, 20);
					}
					InnerButton inb = new InnerButton();
					inb.setLabel(str.trim());
					inb.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event arg0) throws Exception {
							InfoEmailShow infoEmailShow=(InfoEmailShow)Executions.createComponents("/apps/infoExtra/content/infogenerated/sended/showemail.zul", null, null);
							infoEmailShow.initWindow(u);
							infoEmailShow.doHighlighted();							
						}
					});
					c3.appendChild(inb);
				}
				Listcell c4 = new Listcell();
				Date d = new Date(u.getTime());				
				c4.setLabel(DateUtil.dateToStr(d));
				Listcell c5 = new Listcell();
	            Image del = new Image();
	            del.setType("delList");
	            del.addEventListener("onClick", new EventListener()
	            {

	                public void onEvent(Event event) throws Exception
	                {
	                	infoGeneratedService.delete(u);
	                	initWindow();
	                }
	            });
	            c5.appendChild(del);            
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
				item.appendChild(c4);
				item.appendChild(c5);
				
			}
		});
	}

	public void onClick$refresh(){
		initWindow();
	}
	
	public void onClick$deleteInfo() throws InterruptedException{
		if (sendMsglistbox.getSelectedItem() == null) {
			Messagebox.show("请您选择要删除的消息！", "提示", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		} else {
			@SuppressWarnings("unchecked")
			Set<Listitem> isets = sendMsglistbox.getSelectedItems();
			final Iterator<Listitem> ite = isets.iterator();
			final List<WKTInfoEmail> hlist = new ArrayList<WKTInfoEmail>();
			Messagebox.show("确定删除吗?", "确定", Messagebox.OK
					| Messagebox.IGNORE | Messagebox.CANCEL,
					Messagebox.QUESTION,
					new org.zkoss.zk.ui.event.EventListener() {
						public void onEvent(Event evt)
								throws InterruptedException {
							if (evt.getName().equals("onOK")) {
								while (ite.hasNext()) {
									Listitem item = (Listitem) ite.next();
									hlist.add((WKTInfoEmail) item.getValue());
								}
								for (WKTInfoEmail mi : hlist) {			
									infoGeneratedService.delete(mi);
								}
							} else if (evt.getName().equals("onIgnore")) {
								Messagebox.show("忽略操作！", "警告",
										Messagebox.OK,
										Messagebox.EXCLAMATION);
							} else {
								Messagebox.show("取消删除操作！", "提示",
										Messagebox.OK,
										Messagebox.INFORMATION);
							}
						}
					});

		}
		initWindow();
	}
	
}
