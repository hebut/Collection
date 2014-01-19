package com.uniwin.webkey.infoExtra.email;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.ReceiveMailService;
import com.uniwin.webkey.infoExtra.itf.SendMailService;
import com.uniwin.webkey.infoExtra.model.ReceiveMail;


public class EmailReceiveWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2052909862175973615L;
	// 已收到邮件列表框
	private Listbox noticeList;
	private Users user;
	private ReceiveMailService receiveMailService = (ReceiveMailService)SpringUtil.getBean("ReceiveMailService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        user = (Users)Sessions.getCurrent().getAttribute("users");
		if(user.getKuReceivePop()!=null){
			List list=receiveMailService.query("select uid from ReceiveMail where user = ? ", user);
			Set<String> hasReadSet = new HashSet<String>();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				hasReadSet.add((String) iterator.next());
			}
			List<ReceiveMail> mailList=ReceiveEmail.receive(user.getKuReceivePop(), user.getKuReceiveMail(), user.getKuReceivePassword(), hasReadSet , user);
			for (ReceiveMail receiveMail : mailList) {
				receiveMailService.save(receiveMail);
			}
		}
		
		noticeList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				
				final ReceiveMail message = (ReceiveMail) data;
				item.setValue(message);
				item.setHeight("25px");
				Listcell c = new Listcell("");
				Listcell c0 = new Listcell(item.getIndex() + 1 + "");	
				Listcell c_from = new Listcell(message.getMailfrom());
				
				
				Listcell c_subject = new Listcell();
				String str=message.getSubject();
				if(str!=null&&!str.equals("")){
					int len = str.trim().length();
					if (len > 20) {
						str = str.substring(0, 20);
					} 
					InnerButton inb = new InnerButton();
					inb.setLabel(str.trim());
					inb.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event arg0) throws Exception {
							ReceiveMailViewWindow nvw = (ReceiveMailViewWindow)Executions.createComponents("/apps/infoExtra/content/email/receive/view.zul", null, null);
							nvw.doHighlighted();
							nvw.initWindow(message);
						}
					});				
					c_subject.appendChild(inb);
				}
				
				Listcell c_time = new Listcell(message.getMaildate());
				item.appendChild(c);
				item.appendChild(c0);
				item.appendChild(c_from);
				item.appendChild(c_subject);
				item.appendChild(c_time);
			}
		});
		noticeList.setModel(new ListModelList(receiveMailService.query("from ReceiveMail where user = ?", user)));
	}
}
