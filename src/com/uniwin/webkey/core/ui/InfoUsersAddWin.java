package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Group;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.common.util.IdcardValidator;
import com.uniwin.webkey.common.util.StringUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Userrole;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.util.FileTool;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class InfoUsersAddWin extends Window implements AfterCompose
{
    private IUsersManager       usersManager;

    private IUserroleManager    userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");                  ;

    private Logger              log             = Logger
                                                        .getLogger(InfoUsersAddWin.class);

    private Textbox             name, loginName, password, configPassword;            // 用户名；登录别名；密码；确认密码

    private Button               addUser;

    private OrganizationTree    parentData;

    private Organization        selectedOrganization;

    private Bandbox             organizationId;

    private Radio               isNotLock, nan, nv;

    private Radio               isLock,outdate;

    private Datebox             opentime,closetime;

    private Textbox             kuEmail;

    private Textbox             kuPhone,keyWord;

    public InfoUsersAddWin()
    {
        usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
    }

    public void afterCompose()
    {
    	Components.wireVariables(this, this);
        Components.addForwards(this, this);
        opentime.setValue(new Date());
        parentData.setLabelProvider(new OrganizationLabelProvider());
        parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        parentData.rebuildTree();
        fullBandBox();
    }


    /**
     * 添加用户信息
     * 
     * @param event
     */
    public void addUser(Event event)
    {
        try
        {
            if (name.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.namevalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            else
            {
                if(!StringUtil.checkValue(name.getText().trim(), "UserName"))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("users.ui.namevalidatetype"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            if (loginName.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            else
            {
                if(!StringUtil.checkValue(loginName.getText().trim(), "loginName"))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("users.ui.loginnamevalidatetype"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            if (!usersManager.isExistLoginName(loginName.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate1"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (password.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.password"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (!password.getText().equals(configPassword.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.passwordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if(kuPhone.getValue().equals(""))
            {
            	 Messagebox.show("请输入电话！",
                         org.zkoss.util.resource.Labels
                                 .getLabel("system.commom.ui.prompt"),
                         Messagebox.OK, Messagebox.EXCLAMATION);
            	 kuEmail.focus();
                 return;
            }
            if(kuEmail.getValue().equals(""))
            {
            	 Messagebox.show("请输入电子邮箱！",
                         org.zkoss.util.resource.Labels
                                 .getLabel("system.commom.ui.prompt"),
                         Messagebox.OK, Messagebox.EXCLAMATION);
            	 kuEmail.focus();
                 return;
            }
            if (organizationId.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.organizationvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Users user = new Users();
        user.setName(name.getText());
        user.setLoginName(loginName.getText());
        if(!closetime.getText().equals("")&&!closetime.getText().equals(null))
        {
            user.setAccountEndTime(DateUtil.strToDate(closetime.getText(),"yyyy-MM-dd"));
        }
        else
        {
        	user.setAccountEndTime(DateUtil.strToDate("2050-12-30"));
        }
        user.setOrganizationId(selectedOrganization.getOrganizationId());
        user.setPasswordEndTime(new Date());
        user.setEnable("1");
        user.setLockTime(new Date());
        user.setPassword(Encryption.encryption(password.getText()));
        if(outdate.isChecked())
        {
        	  user.setIsLocked("2");
        }
        else
        {
              user.setIsLocked(isNotLock.isChecked() ? "0" : "1");
        }
        
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
            treeItem.getTreerow().addEventListener("onClick",
                    new EventListener()
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


}
