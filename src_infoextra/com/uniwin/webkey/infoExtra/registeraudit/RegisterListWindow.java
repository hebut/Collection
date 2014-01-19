package com.uniwin.webkey.infoExtra.registeraudit;

import java.util.Date;
import java.util.List;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Register;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.NewsServices;


public class RegisterListWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8095314243251644708L;

	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	IUsersManager       usersManager= (IUsersManager) SpringUtil.getBean("usersManager");
	ListModelList auditListModel;
	Listbox registerListbox;
	public void afterCompose() 
	{
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		reloadList();
	}
	//加载列表
		public void reloadList()
		{ 
			List ulist=newsService.findRegister();
				auditListModel=new ListModelList();
				auditListModel.addAll(ulist);
				registerListbox.setModel(auditListModel);
				registerListbox.setItemRenderer(new ListitemRenderer(){
					public void render(Listitem item, Object arg1) throws Exception {
						final Register reg=(Register)arg1;
						item.setValue(reg);
						Listcell c=new Listcell();
						Listcell c0=new Listcell(item.getIndex()+1+"");
						Listcell c1=new Listcell(reg.getUsername());
						Listcell c2=new Listcell(reg.getLoginName());
						Listcell c3=new Listcell();
						if(reg.getKuSex().equals("1"))
						{
						 c3=new Listcell("女");
						}
						else if(reg.getKuSex().equals("0"))
						{
					     c3=new Listcell("男");
						}
						Listcell c4=new Listcell(reg.getOrganizationName());
						Listcell c5=new Listcell(reg.getKuEmail());
						Listcell c6=new Listcell(reg.getKuPhone());
						Listcell c7=new Listcell(reg.getKuRtime());
						Listcell c8=new Listcell();
						
						Toolbarbutton pass=new Toolbarbutton();
						pass.setImage("/images/content/shouli.png");
						pass.setTooltiptext("通过");
						pass.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								 if(Messagebox.show("确定要审核通过吗？", "确认", 
											Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
									{ 
									  Users user = new Users();
								        user.setName(reg.getUsername());
								        user.setLoginName(reg.getLoginName());
								        user.setAccountEndTime(new Date());
								        user.setOrganizationId(reg.getOrganizationId());
								        user.setPasswordEndTime(new Date());
								        user.setKuPassProblem(reg.getKuPassProblem());
								        user.setKuPassAnswer(reg.getKuPassAnswer());
								        user.setEnable("1");
								        user.setAccountEndTime(DateUtil.strToDate("2050-12-30"));
								        user.setKuRegDate(DateUtil.dateToStr(new Date(),"yyyy-MM-dd"));
								        user.setLockTime(new Date());
								        user.setPassword(Encryption.encryption(reg.getPassword()));
								        user.setIsLocked("0");

								        /*** 扩展属性 */
								        // user.setKuAutoEnter((String)kuAutoEnter.getSelectedItem().getValue());
								        user.setKuAutoShow("0");
								        user.setKuBindAddr("");
								        user.setKuBindType("0");
								        user.setKuBirthday(reg.getKuBirthday());
								        user.setKuCardNumber("");
								        user.setKuCertId("");
								        user.setKuCertificatetype("0");
								        user.setKuCertInfo("");
								        user.setKuCompany(reg.getKuCompany());
								        user.setKuEmail(reg.getKuEmail());
								        user.setKuForm("0");
								        user.setKuKeyLogin("0");
								        user.setKuLimit(0);
								        user.setKuPhone(reg.getKuPhone());
								        user.setKuPicPath("");
								        user.setKuSkinname("default");
								        user.setKuAutoEnter("0");
								        user.setKustyle("default");
								        user.setKuMajor(reg.getKuMajor());
								        user.setKuJobLevel(reg.getKuLevel());
								        user.setKuDuty(reg.getKuDuty());
								        if(reg.getKuSex().equals("1"))  user.setKuSex("2");
								        else if (reg.getKuSex().equals("0"))  user.setKuSex("1");
								       
								        try {
								        	  usersManager.add(user);
								        	  newsService.delete(reg);
								        	  reloadList();
								        	  Messagebox.show("操作成功,请进入'用户管理'分配用户所属角色！", "Information", Messagebox.OK, Messagebox.INFORMATION);
								        } catch (Exception e) {
											  e.printStackTrace();
										}
									}
							}
						});
						Toolbarbutton edit=new Toolbarbutton();
						edit.setImage("/css/default/images/inactEdit.gif");
						edit.setTooltiptext("查看");
						edit.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								RegisterEdit rege=(RegisterEdit)Executions.createComponents("/apps/infoExtra/content/registeraudit/registeredit.zul", null, null);
								rege.initWindow(reg);
								rege.doModal();
								rege.addEventListener(Events.ON_CHANGE, new EventListener(){
									public void onEvent(Event arg0)
											throws Exception {
										reloadList();
									}
								});
							}
							});
						Toolbarbutton delete=new Toolbarbutton();
						delete.setImage("/images/content/del.gif");
						delete.setTooltiptext("删除");
						delete.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								 if(Messagebox.show("确定要删除吗？", "确认", 
											Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
									{ 
									 try {
									 newsService.delete(reg);
									  reloadList();
									  Messagebox.show("删除成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
									 }catch (Exception e) {
										  e.printStackTrace();
									}
									 
									}
							}
						});
							
						Hbox hbox = new Hbox();
						hbox.appendChild(pass);
						hbox.appendChild(edit);
						hbox.appendChild(delete);
						c8.appendChild(hbox);
						item.appendChild(c);
						item.appendChild(c0);
						item.appendChild(c1);
						item.appendChild(c2);
						item.appendChild(c3);
						item.appendChild(c4);
						item.appendChild(c5);
						item.appendChild(c6);
						item.appendChild(c7);
						item.appendChild(c8);
						c1.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								RegisterEdit rege=(RegisterEdit)Executions.createComponents("/apps/infoExtra/content/registeraudit/registeredit.zul", null, null);
								rege.initWindow(reg);
								rege.doModal();
								rege.addEventListener(Events.ON_CHANGE, new EventListener(){

									public void onEvent(Event arg0)
											throws Exception {
										reloadList();
									}
								});
							}
				});
}
		});
}
}