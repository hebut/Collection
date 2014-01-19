package com.uniwin.webkey.infoExtra.infogenerated;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

import com.uniwin.framework.ui.system.CryptUtils;
import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.ui.InfoUsersListWin;
import com.uniwin.webkey.infoExtra.email.InnerButton;
import com.uniwin.webkey.infoExtra.email.Mail;
import com.uniwin.webkey.infoExtra.email.MessageSelectUserWindow;
import com.uniwin.webkey.infoExtra.itf.InfoGeneratedService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.TemplateFile;
import com.uniwin.webkey.infoExtra.model.WKTInfoEmail;
import com.uniwin.webkey.infoExtra.newscenter.InfoShow;

public class NewInfo extends Window implements AfterCompose {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8393495553754132094L;
	private Listbox newinfolistbox,templateList;
	private Users user;
	private Textbox mToInner,mailSubject;
	// 暂存收件人列表
	private List<Users> recelist = new ArrayList<Users>();
	private InfoGeneratedService infoGeneratedService;
	private NewsServices info_newsService=(NewsServices) SpringUtil.getBean("info_newsService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}
	
	//初始显示已订阅的资讯
	public void initWindow(){
		List<WkTInfo> newinfolist = infoGeneratedService.findCollectionWkTInfo();
		if(newinfolist!=null&&newinfolist.size()!=0){
			newinfolistbox.setModel(new ListModelList(newinfolist));
		}
		else{
			newinfolistbox.setModel(new ListModelList());
		}
		newinfolistbox.setItemRenderer(new ListitemRenderer() {
			
			public void render(Listitem item, Object data) throws Exception {
				final WkTInfo info = (WkTInfo)data;
				item.setValue(info);
				Listcell c0 = new Listcell();
				Listcell c1 = new Listcell();
				c1.setLabel(item.getIndex()+1+"");
				Listcell c2 = new Listcell();
				String str = info.getKiTitle();
				if (str != null && !str.equals("")) {
					int len = str.trim().length();
					if (len > 20) {
						str = str.substring(0, 20);
					}
					InnerButton inb = new InnerButton();
					inb.setLabel(str.trim());
					inb.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event arg0) throws Exception {
							WkTInfo wkTInfo=info_newsService.getWkTInfo(info.getKiId());
							InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/infocenter/showdetail.zul", null, null);
							infoShow.initWindow(wkTInfo);
							infoShow.doHighlighted();						
						}
					});
					c2.appendChild(inb);
				}	
				Listcell c3 = new Listcell();
				c3.setLabel(info.getKiPtime());
				Listcell c4 = new Listcell();
				c4.setLabel(info.getKiSource());
				Listcell c5 = new Listcell();
	            Image del = new Image();
	            del.setType("delList");
	            del.addEventListener("onClick", new EventListener()
	            {

	                public void onEvent(Event event) throws Exception
	                {
	                	info.setKiCState(0);
	                	infoGeneratedService.update(info);
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
		
		//初始化模板下拉列表
		List<TemplateFile> templateItems = infoGeneratedService.findTemplateAllFile();
		if(templateItems!=null&&templateItems.size()!=0){
			templateList.setModel(new ListModelList(templateItems));
			templateList.setSelectedIndex(0);
		}
		else{
			templateList.setModel(new ListModelList());
		}
		templateList.setItemRenderer(new ListitemRenderer() {
			
			public void render(Listitem item, Object data) throws Exception {
				final TemplateFile info = (TemplateFile)data;
				item.setValue(info);
				Listcell c0 = new Listcell();
				c0.setLabel(info.getName());
				
				item.appendChild(c0);
			}
		});
		
	}
	
	public void onClick$in(){
		MessageSelectUserWindow addwin = (MessageSelectUserWindow) Executions.createComponents("/apps/infoExtra/content/email/new/selectUser.zul", null, null);
		addwin.doHighlighted();
		addwin.setTitle("选择收件人");
		try {
			addwin.initWindow(recelist);
		} catch (Exception e) {
		}
		addwin.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				MessageSelectUserWindow addwin = (MessageSelectUserWindow) arg0.getTarget();
				addReceiver(addwin.getSelectUsers());
				addwin.detach();
			}
		});
	}

	public void addReceiver(List<Users> userList) {
		recelist = userList;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < recelist.size(); i++) {
			Users u = (Users) recelist.get(i);
			sb.append(u.getLoginName());
			if (i < (recelist.size() - 1)) {
				sb.append(",");
			}
		}
		mToInner.setValue(sb.toString());
	}
	
	public void onClick$refresh(){
    	initWindow();
	}

	public void onClick$send(){
		if(mailSubject.getValue()==null||mailSubject.getValue().trim().equals("")){
			try {
				Messagebox.show("请填写主题!!");
			} catch (InterruptedException e) {
			}
			return;
		}
		if(recelist==null||recelist.size()==0){
			try {
				Messagebox.show("请选择联系人!!");
			} catch (InterruptedException e) {
			}
			return;
		}
		//选择的模板
		String templatefile_str = "/template/default.jsp";
		String templatestyle = "";
		
		if(templateList!=null&&templateList.getSelectedItem()!=null){
			TemplateFile template_Choosed = (TemplateFile)templateList.getSelectedItem().getValue();
			 templatefile_str =  template_Choosed.getFileName();
			 templatestyle = template_Choosed.getStyle();
		}
		
		////
		@SuppressWarnings("unchecked")
		Set<Listitem> newset = newinfolistbox.getSelectedItems();
		Iterator<Listitem> infoite = newset.iterator();
		List<WkTInfo> infolist = new ArrayList<WkTInfo>();
		while(infoite.hasNext()){
			Listitem item = infoite.next();
			infolist.add((WkTInfo)item.getValue());
		}
		if(infolist.size()==0){
			try {
				Messagebox.show("请选择资讯!!");
			} catch (InterruptedException e) {
			}
			return;
		}
		String ipAddr ="127.0.0.1";
		try {
			ipAddr = InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/")+1);
		} catch (UnknownHostException e1) {
		}
		if(infolist==null||infolist.size()==0){
			try {
				Messagebox.show("请选择资讯!!");
				return;
			} catch (InterruptedException e) {
			}
		}
		StringBuilder userStr = new StringBuilder();
		int i=0;
		String infoCon = "";
		if(recelist!=null&&recelist.size()!=0){
			for(Users sendUser : recelist){
				userStr.append(sendUser.getLoginName()).append(",");
				Boolean flag = true;
				StringBuilder infoStr = new StringBuilder();
				infoStr.append("<ul>");
				for(WkTInfo info:infolist){
				//  String url = "http://"+ipAddr+":8080/InfoCollection/infodetail.do?userName="+CryptUtils.encrypt(sendUser.getLoginName())+"&password="+sendUser.getPassword()+"&infoId="+CryptUtils.encrypt(String.valueOf(info.getKiId().longValue()));
				//	String url = "http://"+ipAddr+":8080/InfoCollection/query_newsdetail.jsp?userName="+CryptUtils.encrypt(sendUser.getLoginName())+"&password="+sendUser.getPassword()+"&infoId="+CryptUtils.encrypt(String.valueOf(info.getKiId().longValue()));					
					//String url = "http://"+ipAddr+":8080/InfoCollection/query_newsdetail.jsp?userName="+CryptUtils.encrypt(sendUser.getLoginName())+"&password="+sendUser.getPassword()+"&infoId="+CryptUtils.encrypt(String.valueOf(info.getKiId().longValue()))+"&template="+templatefile_str;		
					String url = "http://"+ipAddr+":8080/InfoCollection/query_newsdetail.jsp?userName="+CryptUtils.encrypt(sendUser.getLoginName())+"&infoId="+CryptUtils.encrypt(String.valueOf(info.getKiId().longValue()))+"&template="+templatefile_str+"&style="+templatestyle;	
					infoStr.append("<li><a href='"+url+"' target='_blank'>"+info.getKiTitle()+"("+info.getKiSource()+")"+info.getKiPtime()+"</a></li>");
				}
				infoStr.append("</ul>");
				infoCon = infoStr.toString();
				//<ul><li><a href='http://10.1.15.222:8080/InfoCollection/query_newsdetail.jsp?userName=46BB3ECD0BE69B09&password=900150983cd24fb0d6963f7d28e17f72&infoId=FB279FB302207F4D' target='_blank'>
				//报道称法国也拥有类似“棱镜”的监控计划()2013年07月05日01:42</a></li></ul>
				//Mail sendmail=new Mail(sendUser.getKuEmail(),"iti_213@126.com","smtp.126.com","iti_213@126.com","iti213","新闻定制",infoStr.toString());
				Mail sendmail=new Mail(sendUser.getKuEmail(),"iti_213@126.com","smtp.126.com","iti_213@126.com","iti213",mailSubject.getValue(),infoStr.toString());
				if(!sendmail.startSend()){
					flag=false;
				}
			    if(flag){
				    try {
						Messagebox.show(sendUser.getName()+"发送成功!");
					} catch (InterruptedException e) {
					}
			    }
			    else{
				    try {
				    	i++;
						Messagebox.show(sendUser.getName()+"发送失败!");
					} catch (InterruptedException e) {
					}
		        }
			}
			if(i==0){
				//存入已发资讯
				WKTInfoEmail infom = new WKTInfoEmail();
				infom.setContent(infoCon);
				infom.setMailto(userStr.toString());
				infom.setSubject(mailSubject.getValue());
				infom.setTime(System.currentTimeMillis());
				infom.setTimeStamp(System.currentTimeMillis());
				infom.setUserId(user.getUserId());
				infoGeneratedService.save(infom);
			}			
		}
	}
		
}
