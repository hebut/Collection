package com.uniwin.webkey.core.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;

import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.common.util.StringUtil;

import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.InfoUser;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.UsersEx;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

/**
 * 
 * @author gbDong
 * 
 */
public class InfoUsersUpdataWin extends Window implements AfterCompose
{
    private IUsersManager    usersManager;

    private IUserroleManager userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");

    private Logger           log             = Logger
                                                     .getLogger(InfoUsersUpdataWin.class);

    private Textbox          name, loginName, password, configPassword;               // 用户名；登录别名；密码；确认密码,密码提示问题,密码提示答案

    private Button           updataUser;                                              // 更新用户按钮

    private InfoUser          infoUser;                                                    // 要更新的用户对象

    private OrganizationTree parentData;

    private Organization     selectedOrganization;

    private Bandbox          organizationId;

    private Textbox          email;

    private Textbox          phone;
 
    private Textbox             keyWord;


    public Bandbox getOrganizationId()
    {
        return organizationId;
    }

    public void setOrganizationId(Bandbox organizationId)
    {
        this.organizationId = organizationId;
    }

    public InfoUsersUpdataWin()
    {
        usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
        Map map = Executions.getCurrent().getArg();
        infoUser = (InfoUser) map.get("infoUser");
    }

    public void afterCompose()
    {
        organizationId = (Bandbox) this.getFellow("organizationId");
        name = (Textbox) this.getFellow("name");
        loginName = (Textbox) this.getFellow("loginName");
        password = (Textbox) this.getFellow("password");
        configPassword = (Textbox) this.getFellow("configPassword");
        parentData = (OrganizationTree) this.getFellow("parentData");
        keyWord = (Textbox) this.getFellow("keyWord");
        
        this.parentData.setLabelProvider(new OrganizationLabelProvider());
        this.parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        this.parentData.rebuildTree();
        fullBandBox();
        organizationId.setValue(infoUser.getKdName());
        Users u= usersManager.getUserByuid(Long.parseLong(infoUser.getKuId()+""));
        selectedOrganization = new Organization();
        selectedOrganization.setOrganizationId(infoUser.getKdId());
        email = (Textbox) this.getFellow("email");
        phone = (Textbox) this.getFellow("phone");

    }



    /**
     *修改用户信息
     * 
     * @param event
     */
    public void updataUser(Event event)
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
            if (!checkLoginName(loginName.getText(), infoUser.getKuId()))
                return;

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
            
            if (!password.getText().equals(configPassword.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.passwordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (password.getText().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.password"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (email.getValue().equals(""))
            {
                Messagebox.show("请输入电子邮箱！",
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                email.focus();
                return;
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            Users updateUser = new Users();
            updateUser = usersManager.get(infoUser.getKuId());
            updateUser.setOrganizationId(this.selectedOrganization
                    .getOrganizationId());
            updateUser.setName(name.getText());
            updateUser.setLoginName(loginName.getText());
            //User user = FrameCommonDate.getUser();
            if (password.getText().equals(this.infoUser.getKuPasswd()))
            {
                updateUser.setPassword(password.getText());
            } else
            {
                updateUser.setPassword(Encryption
                        .encryption(password.getText()));
            }

            
           
            updateUser.setFocusContent(keyWord.getValue());
            updateUser.setKuEmail(email.getValue());

            updateUser.setKuPhone(phone.getValue());


            usersManager.update(updateUser);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((InfoUsersListWin) this.getDesktop().getAttribute("InfoUsersListWin")).selectId = updateUser
                    .getUserId();
            ((InfoUsersListWin) this.getDesktop().getAttribute("InfoUsersListWin")).loadInfoUserList();
            this.detach();
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.changefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.userInfochange")
                    + e.getMessage());
            e.printStackTrace();
        }
    }



    public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
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




    /**
     * 检查用户名
     * 
     * @param loginName
     * @param userId
     * @return
     */
    public boolean checkLoginName(String loginName, int userId)
    {
        try
        {
            List userList = usersManager.getUserByLoginName(loginName);
            if (userList.size() == 0)
                return true;
            else if (userList.size() == 1
                    && ((Users) userList.get(0)).getUserId() == userId)
            {
                return true;
            } else
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate1"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
