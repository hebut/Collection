package com.uniwin.webkey.core.ui;

import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.StringUtil;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.model.Organization;

public class OrganizationAddWin extends Window implements AfterCompose
{
    private Textbox              name, description, email, postalAddress,
            postalCode, telephoneNumber, code, fullName, engName, site;                   // 组织名称，说明，email

    private Intbox               order;

    private Combobox             type;                                                    //资源类别

    private IOrganizationManager organizationManager;                                     // 资源管理服务接口

    private Logger               log               = Logger
                                                           .getLogger(UsersListWin.class); // 日志管理

    // private List treeModel; // 树的数据模型 
    private Organization         organizationSession;

    private Window organizationUpdata_wind;
    private IPermissionManager   permissionManager = (IPermissionManager) SpringUtil
                                                           .getBean("permissionManager");

    public OrganizationAddWin()
    {
        try
        {
            organizationManager = (IOrganizationManager) SpringUtil
                    .getBean("organizationManager");
            permissionManager = (IPermissionManager) SpringUtil.getBean(
                    "permissionManager");
            organizationSession = (Organization) Sessions.getCurrent()
                    .getAttribute("organization");
          
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.initresource")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 返回
     */
    public void back()
    {
        this.rebuilderTree(organizationSession.getOrganizationId());
    }

    /**
     * 添加组织信息
     * 
     * @throws InterruptedException
     */
    public void addResource() throws InterruptedException
    {
        if (name.getText().trim().equals(""))
        {
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("organization.ui.validateOrgName"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        Organization addOrganization = new Organization();
        addOrganization.setState(0);
        addOrganization.setName(name.getText());
        if (order.getValue() != null)
        {
            addOrganization.setOrder(order.getValue());
        }
        addOrganization.setFullName(fullName.getValue());
        addOrganization.setEngName(engName.getValue());
        if(code.getValue()!=null && !code.getValue().equals(""))
        {
            int num = -1;
            num = this.organizationManager.getOrgNumByCode(code.getValue());
            if(num !=0)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("organization.zul.codeValidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        }
        addOrganization.setCode(code.getValue());
        addOrganization.setSite(site.getValue());
        addOrganization.setType((String) type.getSelectedItem().getValue());
        addOrganization.setDescription(description.getText());
        if(!email.getText().equals(""))
        {
            if(!StringUtil.isEmail(email.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("organization.zul.emailValidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        }
        addOrganization.setEmail(email.getText());
        addOrganization.setParentId(organizationSession.getOrganizationId());
        addOrganization.setPostalAddress(postalAddress.getText());
        if(!postalCode.getText().equals(""))
        {
            if(!StringUtil.IsPostalcode(postalCode.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("organization.zul.zipcodeValidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        }
        addOrganization.setPostalCode(postalCode.getText());
        if(!telephoneNumber.getText().equals(""))
        {
            if(!StringUtil.IsTelephone(telephoneNumber.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("organization.zul.telValidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        }
        addOrganization.setTelephoneNumber(telephoneNumber.getText());
        try
        {
            this.organizationManager.add(addOrganization);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
        } catch (Exception e)
        {
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.addfailed"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
        // 重新加载树
        rebuilderTree(addOrganization.getOrganizationId());

    }

    /**
     * 重新显示树
     */
    public void rebuilderTree(int id)
    {
        OrganizationListWin organizationListWin = ((OrganizationListWin) this
                .getDesktop().getAttribute("OrganizationListWin"));
        Organization organization = new Organization();
        organization.setOrganizationId(id);
        organizationListWin.rebuilderTree(organization);
    }

    public void afterCompose()
    {
        // name, description, email,postAddress,postCode,telephoneNumber;
    	organizationUpdata_wind=(Window)this.getFellow("organizationUpdata_wind");
        name = (Textbox) this.getFellow("name");
        fullName = (Textbox) this.getFellow("fullName");
        engName = (Textbox) this.getFellow("engName");
        code = (Textbox) this.getFellow("code");
        site = (Textbox) this.getFellow("site");
        order = (Intbox) this.getFellow("order");
        type = (Combobox) this.getFellow("type");
        type.setSelectedIndex(0);
        email = (Textbox) this.getFellow("email");
        postalAddress = (Textbox) this.getFellow("postalAddress");
        postalCode = (Textbox) this.getFellow("postalCode");
        telephoneNumber = (Textbox) this.getFellow("telephoneNumber");
        description = (Textbox) this.getFellow("description");
        organizationUpdata_wind.setTitle(org.zkoss.util.resource.Labels
                .getLabel("system.commom.ui.currently")
                + "【"
                + organizationSession.getName()
                + "】"
                + org.zkoss.util.resource.Labels
                        .getLabel("organization.zul.addorganization"));
        // addOrganization_cation.setLabel(org.zkoss.util.resource.Labels
        // .getLabel("system.commom.ui.currently")
        // + "\"" + organizationSession.getName() + "\"添加下级组织");
    }
}
