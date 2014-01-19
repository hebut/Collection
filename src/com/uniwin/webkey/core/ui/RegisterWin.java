package com.uniwin.webkey.core.ui;



import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;

import com.uniwin.webkey.cms.baseUtil.DateUtil;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Register;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;


public class RegisterWin extends Window implements AfterCompose {
	

	private static final long serialVersionUID = 8419781828109467033L;

	//用户数据访问接口
		private IUsersManager usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
		NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	//用户输入框组件
    private Textbox kuName,kuAnswer,kuQuestion,kuEmail,kuPhone,kuLid,password,surepass,kuCompany,kulevel,kuduty,kumajor;
    
    //用户日期输入框组件
 	private Datebox kuBirthday;
 	
 	private Radio man,woman;
    
    private OrganizationTree    parentData;
    
    private Organization        selectedOrganization;
    
    private Bandbox             organizationId;
    
public void afterCompose(){		
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);		
		kuBirthday.setText("1900-01-01");
		kuLid.focus();
	    parentData.setLabelProvider(new OrganizationLabelProvider());
        parentData.setContentProvider(new OrganizationThirdContentProvider());
        parentData.rebuildTree();
        fullBandBox();
        password.addEventListener(Events.ON_FOCUS, new EventListener(){
    		public void onEvent(Event arg0) throws Exception {
    			
    			List ulist=usersManager.getUserByLoginName(kuLid.getValue());
    			if(ulist.size()!=0)
    			{
    				 Messagebox.show("登录名已存在，请更换！", Labels.
    						   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
    				kuLid.focus();
    				
    			}
    		}
        });
        
        kuName.addEventListener(Events.ON_FOCUS, new EventListener(){
    		public void onEvent(Event arg0) throws Exception {
    			
    			if(!password.getValue().trim().equals(surepass.getValue().trim()))
    			{
    				 Messagebox.show("两次输入的密码不一致！", Labels.
    						   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
    				 password.focus();
    				 password.setValue("");
    				 surepass.setValue("");
    			}
    		}
        });
        
}

/**
 * 选中组织
 */
public void fullBandBox()
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
        if (re.getOrganizationId() == 0)
        {
            treeItem.setSelected(true);
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
public void onClick$save() throws InterruptedException, WrongValueException, DataAccessException
{
	
	 if(kuLid.getValue().equals(""))
	 {	
		   Messagebox.show("登录名不能为空！",
				   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   kuLid.focus();
		   return;
	  }
	 if(kuLid.getValue().length()<6)
	 {	
		   Messagebox.show("登录名不能小于六位！",
				   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   kuLid.focus();
		   return;
	  }
	List ulist=usersManager.getUserByLoginName(kuLid.getValue());
		if(ulist.size()!=0)
		{
			 Messagebox.show("登录名已存在，请更换！", Labels.
					   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
			kuLid.focus();
		}
	
	 if(password.getValue().equals(""))
	 {	
		   Messagebox.show("登录密码不能为空！",
				   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   password.focus();
		   return;
	  }
	 if(surepass.getValue().equals(""))
	 {	
		   Messagebox.show("请输入确认密码！",
				   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   surepass.focus();
		   return;
	  }
	 if(kuName.getValue().equals(""))
	 {	
		   Messagebox.show("用户名不能为空！",
				   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   kuName.focus();
		   return;
	  }
	 if(kuEmail.getValue().equals(""))
	 {
		   Messagebox.show(Labels.getLabel("users.ui.kuEmailvalidate"), Labels.
				   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   kuEmail.focus();
		   return;
	 }
    if(kuPhone.getValue().equals(""))
    {	
		   Messagebox.show(Labels.getLabel("users.ui.kuPhonevalidate"), Labels.
				   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		   kuPhone.focus();			  
		   return;
	 }
    if(organizationId.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.organizationvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
    Register r=new Register();
    String birthTime=new SimpleDateFormat("yyyy-MM-dd").format(kuBirthday.getValue());
    r.setKuBirthday(birthTime);
    r.setKuEmail(kuEmail.getValue());
    r.setKuPassAnswer(kuAnswer.getValue());
    r.setKuPassProblem(kuQuestion.getValue());
    r.setKuPhone(kuPhone.getValue());
    r.setKuMajor(kumajor.getValue());
    r.setKuLevel(kulevel.getValue());
    r.setKuDuty(kuduty.getValue());
    r.setKuCompany(kuCompany.getValue());
    Date d=new Date();
    String subTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    r.setKuRtime(subTime);
    if(man.isChecked())  r.setKuSex("0");
    else if (woman.isChecked())  r.setKuSex("1");
   r.setLoginName(kuLid.getValue());
   r.setOrganizationId(selectedOrganization.getOrganizationId());
   r.setOrganizationName(selectedOrganization.getName());
   r.setPassword(password.getValue());
   r.setUsername(kuName.getValue());
   
   try {
	   newsService.save(r);
	   Messagebox.show("提交成功，请等待管理员的审核", Labels.
			   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
	   this.detach();
} catch (Exception e) {
	e.printStackTrace();
}
}

public void onClick$reset()
{
	kuName.setValue("");
	kuAnswer.setValue("");
	kuQuestion.setValue("");
	kuEmail.setValue("");
	kuPhone.setValue("");
	kuLid.setValue("");
	password.setValue("");
	surepass.setValue("");
	kuBirthday.setContext("");
	kumajor.setValue("");
	kulevel.setValue("");
	kuduty.setValue("");
	kuCompany.setValue("");
}
}