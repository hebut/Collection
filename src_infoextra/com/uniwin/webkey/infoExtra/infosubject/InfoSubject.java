package com.uniwin.webkey.infoExtra.infosubject;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IOrganizationDAO;
import com.uniwin.webkey.core.itf.IUserroleDAO;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.model.WkTSubject;
import com.uniwin.webkey.infoExtra.model.WkTUser;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

public class InfoSubject extends Window implements AfterCompose{

	
	Listbox subListbox,choose;
	Listitem all,undone,done;
	Integer viewNum=2;
	Label uname,dname;
	Listheader operation;
	ListModelList modelList;
	Textbox subjectname,name,deptname;
	private InfoSubjectService infosubjectService=(InfoSubjectService)SpringUtil.getBean("infosubjectService");
	Datebox begintime,endtime;
	Toolbarbutton add,delete;
	Users user;
	Organization organization;
	
	private IUserroleDAO userroleDAO=(IUserroleDAO) SpringUtil.getBean("userroleDAO");
	private IOrganizationDAO organizationDAO=(IOrganizationDAO)SpringUtil.getBean("organizationDAO");
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users) Sessions.getCurrent().getAttribute("users");
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		if(judgeShow())
		{
			add.setVisible(false);
			delete.setVisible(false);
			
		}
		
		loadSubject();
		subListbox.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem item, Object arg1) throws Exception {
				
				final WkTSubject wkTSubject=(WkTSubject)arg1;
				item.setValue(wkTSubject);
				//Listcell c=new Listcell();
				//item.appendChild(c);
				Listcell c0=new Listcell(item.getIndex()+1+"");
				
				
				Users subUser=infosubjectService.findByUid(wkTSubject.getSubSource());
				try {
					 organization=organizationDAO.get(subUser.getOrganizationId());
				} catch (DataAccessException e) {
					e.printStackTrace();
				} catch (ObjectNotExistException e) {
					e.printStackTrace();
				}
				
				Listcell c1=new Listcell();
				c1.setLabel(subUser.getName());
				Listcell c2=new Listcell();
				c2.setLabel(organization.getName());
				String con=wkTSubject.getSubContent();
				if(con!=null && con.length()>50){
					con=con.substring(0,50)+"...";
				}
				Listcell c3=new Listcell(wkTSubject.getSubTitle());
				Listcell c4=new Listcell();
				if((con+wkTSubject.getSubUrl()).length()>40)
				{
					c4=new Listcell((con+wkTSubject.getSubUrl()).substring(0, 40)+"...");
					c4.setTooltiptext(con+wkTSubject.getSubUrl());
				}
				else
				{
					c4=new Listcell(con+wkTSubject.getSubUrl());
				}
				Integer s=wkTSubject.getSubStatus();
				
				

				Listcell c7 = new Listcell(wkTSubject.getSubTime());
				
				Listcell c5;
				String status;
				if(s==0){
					status="未受理";
					c5=new Listcell(status);
					c5.setStyle("color:red");
				}else{
					status="已受理";
					String date = wkTSubject.getSubATime();
					c5=new Listcell(status+date);
					c5.setStyle("color:blue");
				}
				
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
				item.appendChild(c4);
				item.appendChild(c7);
				item.appendChild(c5);
				
				c1.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						ViewSubject viewSubject=(ViewSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/view.zul", null, null);
						viewSubject.doHighlighted();
						viewSubject.initWin(wkTSubject);
					}
					
				});
				c3.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						ViewSubject viewSubject=(ViewSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/view.zul", null, null);
						viewSubject.doHighlighted();
						viewSubject.initWin(wkTSubject);
					}
					
				});
				
				Listcell c6=new Listcell();
				
				Hbox hbox = new Hbox();
				
				if(/*isShow*/user.getLoginName().equals("admin") || user.getLoginName().trim().equals("superman")){
					
					Toolbarbutton button=new Toolbarbutton();
					button.setImage("/images/content/shouli.png");
					button.setTooltiptext("受理");
					if(wkTSubject.getSubStatus()==0){
						button.setDisabled(false);
					}else{
						button.setDisabled(true);
					}
					button.addEventListener(Events.ON_CLICK, new EventListener(){
						public void onEvent(Event arg0) throws Exception {
							
							SubjectTree subjectTree=(SubjectTree)Executions.createComponents("/apps/infoExtra/content/infosubject/subjectTree.zul", null, null);
							subjectTree.initWin(wkTSubject);
							subjectTree.doHighlighted();
							subjectTree.addEventListener(Events.ON_CHANGE, new EventListener(){

								public void onEvent(Event arg0)
										throws Exception {
									loadSubject();
								}
								
							});
							
						}
						
					});
					hbox.appendChild(button);
				}
				Image editImage = new Image();
				editImage.setType("edit");
				Image delImage = new Image();
				delImage.setType("delList");
				
				Toolbarbutton buttonfk=new Toolbarbutton();
				buttonfk.setImage("/css/default/images/n-btn-add.gif");
				buttonfk.setTooltiptext("反馈");
				if(wkTSubject.getSubStatus()==0){
					buttonfk.setDisabled(true);
				}else{
					buttonfk.setDisabled(false);
				}
				buttonfk.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						
						AdviceWindow adWin=(AdviceWindow)Executions.createComponents("/apps/infoExtra/content/infosubject/fankui.zul", null, null);
						adWin.doHighlighted();
						adWin.initWindow(wkTSubject);
					}
					
				});
				if(user.getLoginName().equals("superman")){
				    editImage.setVisible(true);
				    editImage.setVisible(true);
					//editbutton.setDisabled(false);
			        //deletebutton.setDisabled(false);
		        }else if(wkTSubject.getSubStatus()==1 && wkTSubject.getSubSource().equals(user.getUserId())){
				    editImage.setVisible(false);
				    editImage.setVisible(true);
		        	// editbutton.setDisabled(true);				    
				   // deletebutton.setDisabled(false);
			    }else if(wkTSubject.getSubStatus()==1 && !wkTSubject.getSubSource().equals(user.getUserId())){
				    editImage.setVisible(false);
				    editImage.setVisible(false);
			    	//editbutton.setDisabled(true);
				   // deletebutton.setDisabled(true);
			    }
			
				editImage.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						
						EditSubject editSubject=(EditSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/editsubject.zul", null, null);
					    editSubject.doHighlighted();						
					    editSubject.initWin(wkTSubject);
					}
					
				});
				delImage.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						if(Messagebox.show("确定删除此采集主题?", "警告", Messagebox.OK|Messagebox.CANCEL,Messagebox.QUESTION)==Messagebox.OK){
						   infosubjectService.delete(wkTSubject);
					       loadSubject();
					    }
						
					}
					
				});
				hbox.appendChild(editImage);
				hbox.appendChild(delImage);
				hbox.appendChild(buttonfk);
				//Hbox hbox=new Hbox();
//				Button	editbutton=new Button("编辑");
//				Button	deletebutton=new Button("删除");
//					
//					if(user.getLoginName().equals("superman")){
//						editbutton.setDisabled(false);
//						deletebutton.setDisabled(false);
//					}else if(wkTSubject.getSubStatus()==1 && wkTSubject.getSubSource().equals(user.getUserId())){
//						editbutton.setDisabled(true);
//						deletebutton.setDisabled(false);
//					}else if(wkTSubject.getSubStatus()==1 && !wkTSubject.getSubSource().equals(user.getUserId())){
//						editbutton.setDisabled(true);
//						deletebutton.setDisabled(true);
//					}
//					
//					editbutton.addEventListener(Events.ON_CLICK, new EventListener(){
//
//						public void onEvent(Event arg0) throws Exception {
//							EditSubject editSubject=(EditSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/editsubject.zul", null, null);
//							editSubject.doHighlighted();
//							editSubject.initWin(wkTSubject);
//						}
//						
//					});
//					
//					deletebutton.addEventListener(Events.ON_CLICK, new EventListener(){
//
//						public void onEvent(Event arg0) throws Exception {
//							
//							if(Messagebox.show("确定删除此采集主题?", "警告", Messagebox.OK|Messagebox.CANCEL,Messagebox.QUESTION)==Messagebox.OK){
//								infosubjectService.delete(wkTSubject);
//								loadSubject();
//							}
//							
//						}
//						
//					});
//					hbox.appendChild(editbutton);
//					hbox.appendChild(deletebutton);
					
//				boolean isShow=judgeShow();
				
                
				c6.appendChild(hbox);

				
				item.appendChild(c6);
			}
			
		});
//		viewSub.addEventListener(Events.ON_SELECT, new EventListener(){
//			public void onEvent(Event arg0) throws Exception {
//				String select=(String) viewSub.getSelectedItem().getValue();
//				if(select.equals("all")){
//					viewNum=2;
//					loadSubject();
//				}else if(select.equals("accept")){
//					viewNum=1;
//					loadSubject();
//				}else if(select.equals("unAccept")){
//					viewNum=0;
//					loadSubject();
//				}
//			}
//			
//		});
		
	}

	private void loadSubject(){
		
		List<WkTSubject> subList;
		if(user.getLoginName().equals("superman") || user.getLoginName().equals("admin")){
			
			subList=infosubjectService.findAll(viewNum);
		}else{
			name.setVisible(false);
			deptname.setVisible(false);
			uname.setVisible(false);
			dname.setVisible(false);
			subList=infosubjectService.findUserSubList(user.getUserId(),viewNum);
		}

		modelList=new ListModelList(subList);
		subListbox.setModel(modelList);
   }
	
	public void onClick$search()
	{
		Integer ALL=2,DONE=1,UNDONE=0;
		List<WkTSubject> serList;
		String begin=ConvertUtil.convertDateAndTimeString(begintime.getValue());
		String end=ConvertUtil.convertDateAndTimeString(endtime.getValue());
		if(user.getLoginName().equals("superman") || user.getLoginName().equals("admin")){
			if(done.isSelected())
			{
			serList=infosubjectService.findAll(viewNum,name.getValue(),deptname.getValue(),subjectname.getValue(),DONE,begin,end);
			}
			else if(undone.isSelected())
			{
				serList=infosubjectService.findAll(viewNum,name.getValue(),deptname.getValue(),subjectname.getValue(),UNDONE,begin,end);	
			}
			else
			{
				serList=infosubjectService.findAll(viewNum,name.getValue(),deptname.getValue(),subjectname.getValue(),ALL,begin,end);	
			}
		}else{
			if(done.isSelected())
			{
				serList=infosubjectService.findUserSubList(user.getUserId(),subjectname.getValue(),DONE,begin,end);
			}
			else if (undone.isSelected())
			{
				serList=infosubjectService.findUserSubList(user.getUserId(),subjectname.getValue(),UNDONE,begin,end);
			}
			else
			{
				serList=infosubjectService.findUserSubList(user.getUserId(),subjectname.getValue(),ALL,begin,end);
			}
		}

		modelList=new ListModelList(serList);
		subListbox.setModel(modelList);
	}
	
	//对应多个管理角色的用户，，受理权限的判断。
	private boolean judgeShow(){
		
		boolean isAdmin=false;
		List<Role> rList = null;
		try {
			
			rList=userroleDAO.getRoleByUser(user.getUserId());
			
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		Role role;
		if(rList!=null){
			
			for(int r=0;r<rList.size();r++){
				role=(Role)rList.get(r);
				if(role.getName().trim().contains("管理角色")){
					isAdmin=true;
				}
			}
			
		}
		
		return isAdmin;
		
	}
	
	
	
	public void onClick$add(){
	
		AddSubject subject=(AddSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/addsubject.zul", null, null);
		subject.doHighlighted();
		subject.addEventListener(Events.ON_CHANGE, new EventListener(){
			
			public void onEvent(Event arg0) throws Exception {
				loadSubject();
			}
			
		});
		
	}
	
	public void onClick$view(){
		
		if(subListbox.getSelectedItem()==null){
			try {
				Messagebox.show("选择查看项！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			WkTSubject wkTSubject=(WkTSubject) subListbox.getSelectedItem().getValue();
			ViewSubject viewSubject=(ViewSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/view.zul", null, null);
			viewSubject.doHighlighted();
			viewSubject.initWin(wkTSubject);
		}
		
	}
	
	
	public void onClick$edit(){
		
		if(subListbox.getSelectedItem()!=null){
			WkTSubject subject=(WkTSubject)subListbox.getSelectedItem().getValue();
			EditSubject editSubject=(EditSubject)Executions.createComponents("/apps/infoExtra/content/infosubject/editsubject.zul", null, null);
			editSubject.doHighlighted();
			editSubject.initWin(subject);
			
			editSubject.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					loadSubject();
				}
				
			});
		}else{
			try {
				Messagebox.show("请选择编辑项！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void onClick$delete(){
		
		if(subListbox.getSelectedItem()==null){
			try {
				Messagebox.show("请选择删除项！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else{
			WkTSubject subject=(WkTSubject)subListbox.getSelectedItem().getValue();
			try {
				if(Messagebox.show("确实要删除该分类 ?", "请确认", Messagebox.OK
						| Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK){
					
					infosubjectService.delete(subject);
					loadSubject();
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
}
