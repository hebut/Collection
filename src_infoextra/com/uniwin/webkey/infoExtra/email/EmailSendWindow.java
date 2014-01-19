package com.uniwin.webkey.infoExtra.email;

import java.util.ArrayList;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.SendMailService;
import com.uniwin.webkey.infoExtra.model.SendMail;


public class EmailSendWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7736291489756477226L;
	// 发送消息列表框
	private Listbox sendMsglistbox;
	// 编辑和删除消息组件
	private Button deleteMsg;
	private SendMailService sendMailService = (SendMailService)SpringUtil.getBean("SendMailService");
	private Users user;
	boolean group = true;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        user = (Users)Sessions.getCurrent().getAttribute("users");
		// 发件箱列表Listbox监听器
		sendMsglistbox.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				final SendMail message = (SendMail) data;
				item.setValue(message);
				item.setHeight("25px");
				Listcell c0 = new Listcell("");
				Listcell c1 = new Listcell();
				StringBuffer sb = new StringBuffer(message.getMailto());
				// 当收件人的名称长度超过10字符时，截取前10字符。当鼠标放其上时，显示全部
				if (sb.length() > 8) {
					String str0 = sb.substring(0, 8);
					c1 = new Listcell(str0 + "..");
				} else {
					c1 = new Listcell(sb.toString());
				}
				c1.setTooltiptext(sb.toString());
				Listcell c2 = new Listcell();
				InnerButton inb = new InnerButton();
				inb.setLabel(message.getSubject());
				c2.appendChild(inb);
				Label lb = new Label();
				String str = message.getContent();
				str = ConvertUtil.htmlTotxt(str); // 调用函数，过滤FCK编辑的样式，留下内容
				int len = str.trim().length();
				if (len > 20) {
					String str1 = ""; // 消息内容长度小于20字符，全部显示，否则截取前20字符显示
					str1 = str.substring(0, 20);
					lb.setValue(" " + str1);
				} else {
					lb.setValue(" " + str);
				}
				lb.setStyle("color:#888888");
				c2.appendChild(lb);
				inb.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						EmailViewWindow nvw = (EmailViewWindow) Executions
								.createComponents(
										"/apps/infoExtra/content/email/send/view.zul",
										null, null);
						nvw.doHighlighted();
						nvw.initWindow(message);
					}
				});
				Listcell c3 = new Listcell(message.getMaildate());
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
			}
		});
		// 删除消息功能
		deleteMsg.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (sendMsglistbox.getSelectedItem() == null) {
					Messagebox.show("请您选择要删除的消息！", "提示", Messagebox.OK,
							Messagebox.INFORMATION);
					return;
				} else {
					Set<Listitem> sel = sendMsglistbox.getSelectedItems();
					final List<SendMail> deleteList = new ArrayList<SendMail>();
					final Iterator<Listitem> it = sel.iterator();
					Messagebox.show("确定删除吗?", "确定", Messagebox.OK
							| Messagebox.IGNORE | Messagebox.CANCEL,
							Messagebox.QUESTION,
							new org.zkoss.zk.ui.event.EventListener() {
								public void onEvent(Event evt)
										throws InterruptedException {
									if (evt.getName().equals("onOK")) {
										while (it.hasNext()) {
											Listitem item = (Listitem) it
													.next();
											deleteList.add((SendMail) item
													.getValue());
										}
										for (int i = 0; i < deleteList.size(); i++) {
											SendMail me = (SendMail) deleteList
													.get(i);
											sendMailService.delete(me);
										}
										if (group) {
											reloadGrid();
										} else {
											sortlist();
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
			}
		});
		reloadGrid();
	}

	public void onSort$titleHeader() {
		if (group) {
			group = false;
			sortlist();
		}
	}

	public void onSort$keywordHeader() {
		if (group) {
			group = false;
			sortlist();
		}
	}

	public void onSort$targetHeader() {
		if (group) {
			group = false;
			sortlist();
		}
	}

	public void onSort$timeHeader() {
		if (group) {
			group = false;
			sortlist();
		}
	}

	private void sortlist() {

      	List<SendMail> msglist = sendMailService.query("from SendMail where user= ? ", user);      //query("from SendMail where user= ? ", user);
//		List<SendMail> msglist = sendMailService.findRank(user);
		//List<SendMail> msglist = sendMailService.findAll();

		sendMsglistbox.setModel(new ListModelList(msglist));
	}

	/**
	 * <li>功能描述：加载数据 void
	 * 
	 * @author bobo
	 * @2010-3-30
	 */
	public void reloadGrid() {
		sortlist();
	}
}
