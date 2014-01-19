package com.uniwin.webkey.infoExtra.registeraudit;


import java.util.Date;
import java.util.Iterator;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Register;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;



public class RegisterEdit extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8095314243251644708L;

	Register reg;
	
	IUsersManager       usersManager= (IUsersManager) SpringUtil.getBean("usersManager");
	
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	
	//用户输入框组件
    private Textbox kuName,kuAnswer,kuQuestion,kuEmail,kuPhone,kuLid,password,surepass,kuCompany,kulevel,kuduty,kumajor;
    
    //用户日期输入框组件
 	private Datebox kuBirthday;
 	
 	private Radio man,woman;
 	
    private OrganizationTree    parentData;
    
    private Organization        selectedOrganization;
    
    private Bandbox             organizationId;
    
    private Window auditwin;
    
	public void afterCompose() 
	{
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		  parentData.setLabelProvider(new OrganizationLabelProvider());
	        parentData.setContentProvider(new OrganizationThirdContentProvider());
	        parentData.rebuildTree();
	}
	/**
	 * 选中组织
	 */
	public void fullBandBox(Integer id)
	{
	    Iterator children = parentData.getItems().iterator();
	    Treeitem treeItem = null;
	    Organization re = null;
	    Object obj = null;
	    while (children.hasNext())
	    {
	        obj = children.next();
	        treeItem = (Treeitem) obj;
	        treeItem.getTreerow().addEventListener(Events.ON_CLICK,new EventListener()
	                {
	                    public void onEvent(Event arg0) throws Exception
	                    {
	                        setOrganizationId();
	                    }
	                });
	        re = (Organization) treeItem.getValue();
	        if (re.getOrganizationId() == id)
	        {
	            treeItem.setSelected(true);
	            setOrganizationId();
	        }
	    }
	}
	
	/**
	 * 将选择的组织添加到文本框中
	 */
	public void setOrganizationId()
	{
	    selectedOrganization = (Organization) parentData.getSelectedItem()
	            .getValue();
	    organizationId.setValue(selectedOrganization.getName());
	    organizationId.close();
	  

	}
	public void initWindow(Register r)
	{
		this.reg=r;
		kuName.setValue(reg.getUsername());
		kuAnswer.setValue(reg.getKuPassAnswer());
		kuQuestion.setValue(reg.getKuPassProblem());
		kuEmail.setValue(reg.getKuEmail());
		kuPhone.setValue(reg.getKuPhone());
		kuLid.setValue(reg.getLoginName());
		password.setValue(reg.getPassword());
		surepass.setValue(reg.getPassword());
		kuBirthday.setText(reg.getKuBirthday());
		kuCompany.setValue(reg.getKuCompany());
		kulevel.setValue(reg.getKuLevel());
		kuduty.setValue(reg.getKuDuty());
		kumajor.setValue(reg.getKuMajor());
		if(reg.getKuSex().equals("0")) man.setChecked(true);
		else if(reg.getKuSex().equals("1")) woman.setChecked(true);
		 fullBandBox(reg.getOrganizationId());
	}
	
	public void onClick$close()
	{
		auditwin.detach();
	}
	
	public void onClick$delete() throws InterruptedException
	{
		 if(Messagebox.show("确定要删除吗？", "确认", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
			{ 
			 newsService.delete(reg);
			 Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			 Events.postEvent(Events.ON_CHANGE, this, null);
			 this.detach();
			}
	}
	
	public void onClick$pass() throws InterruptedException
	{
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
		        user.setKuCompany(kuCompany.getValue());
		        user.setKuDuty(kuduty.getValue());
		        user.setKuJobLevel(kulevel.getValue());
		        user.setKuMajor(kumajor.getValue());
		        user.setKuEmail(reg.getKuEmail());
		        user.setKuForm("0");
		        user.setKuKeyLogin("0");
		        user.setKuLimit(0);
		        user.setKuPhone(reg.getKuPhone());
		        user.setKuPicPath("");
		        user.setKuSkinname("default");
		        user.setKuAutoEnter("0");
		        user.setKustyle("default");
		        if(reg.getKuSex().equals("1"))  user.setKuSex("2");
		        else if (reg.getKuSex().equals("0"))  user.setKuSex("1");
		       
		        try {
		        	  usersManager.add(user);
		        	  newsService.delete(reg);
		        	
		        } catch (Exception e) {
					  e.printStackTrace();
				}
		        Events.postEvent(Events.ON_CHANGE, this, null);
		        this.detach();
		        Messagebox.show("操作成功,请进入'用户管理'分配用户所属角色！", "Information", Messagebox.OK, Messagebox.INFORMATION);	
		}
	}
			
}