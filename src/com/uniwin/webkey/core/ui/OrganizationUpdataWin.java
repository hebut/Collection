package com.uniwin.webkey.core.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.util.StringUtil;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.infoExtra.newspub.NewspubEditWindow;

public class OrganizationUpdataWin extends Window implements AfterCompose
{
    private Textbox              name, description, url, email, postalAddress,
            postalCode, code, fullName, engName, site, telephoneNumber; //资源名称,URL

    private Intbox               order;

    private Combobox             upOrganization, type;                 // 资源类别

    private Label                email_label, url_label;

    private List                 resourceData;                         // 资源数据

    private String               strCode = "";
    
    private IOrganizationManager organizationManager;
    
    private  Window organizationUpdata_wind;
    private OrganizationListWin oListWin ;

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    private Organization organization; // 要更新的资源对象

    private Map          mOP;

    public Map getmOP()
    {
        return mOP;
    }

    public void setmOP(Map mOP)
    {
        this.mOP = mOP;
    }

    private IResourceManager resourceManager;                           // 资源管理服务接口

    private Logger           log = Logger.getLogger(UsersListWin.class); // 日志管理

    public OrganizationUpdataWin()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean(
                "resourceManager");
        organizationManager = (IOrganizationManager) SpringUtil.getBean(
                "organizationManager");
        // usersManager=(IUsersManager)scaDomain.getService(IUsersManager.class,
        // "User");
        // CalculatorService
        // cs=(CalculatorService)scaDomain.getService(CalculatorService.class,
        // "CalculatorComponent");

        organization = (Organization) Sessions.getCurrent().getAttribute(
                "organization");
        mOP = (Map) ((Map) Sessions.getCurrent().getAttribute("rsopMap"))
                .get("rs"
                        + ((Resource) Sessions.getCurrent().getAttribute(
                                "currResource")).getId());
        Sessions.getCurrent().setAttribute("OrgMap", mOP);
    }

    /**
     * 资源信息更新
     */
    public void updataResource()
    {
        try
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
            Organization organizationUpdata = (Organization) Sessions
                    .getCurrent().getAttribute("organization");
            organizationUpdata.setDescription(description.getText());
            organizationUpdata.setName(name.getText());
            if (order.getValue() != null)
            {
                organizationUpdata.setOrder(order.getValue());
            }
            organizationUpdata.setFullName(fullName.getValue());
            organizationUpdata.setEngName(engName.getValue());

            if (code.getValue() != null && !code.getValue().equals(""))
            {
                if (!strCode.equals(code.getValue()))
                {
                    int num = -1;
                    num = this.organizationManager.getOrgNumByCode(code
                            .getValue());
                    if (num != 0)
                    {
                        Messagebox.show(org.zkoss.util.resource.Labels
                                .getLabel("organization.zul.codeValidate"),
                                org.zkoss.util.resource.Labels
                                        .getLabel("system.commom.ui.prompt"),
                                Messagebox.OK, Messagebox.EXCLAMATION);
                        return;
                    }
                }
            }
            organizationUpdata.setCode(code.getValue());
            organizationUpdata.setSite(site.getValue());
            organizationUpdata.setType((String) type.getSelectedItem()
                    .getValue());
            if (!email.getText().equals(""))
            {
                if (!StringUtil.isEmail(email.getText()))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("organization.zul.emailValidate"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            organizationUpdata.setEmail(email.getText());
            organizationUpdata.setPostalAddress(postalAddress.getText());
            if (!postalCode.getText().equals(""))
            {
                if (!StringUtil.IsPostalcode(postalCode.getText()))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("organization.zul.zipcodeValidate"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            organizationUpdata.setPostalCode(postalCode.getText());
            if (!telephoneNumber.getText().equals(""))
            {
                if (!StringUtil.IsTelephone(telephoneNumber.getText()))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("organization.zul.telValidate"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            organizationUpdata.setTelephoneNumber(telephoneNumber.getText());
            //更改状态
            organizationUpdata.setState(0);
            organizationManager.update(organizationUpdata);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.updatesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            OrganizationListWin organizationListWin = ((OrganizationListWin) this
                    .getDesktop().getAttribute("OrganizationListWin"));
            organizationListWin.rebuilderTree(organizationUpdata);
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.updatefailed"),
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
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.resourceupdate")
                    + e.getMessage());
            e.printStackTrace();
        }
    }
  
    public void afterCompose()
    {
        Organization organizationUpdata = (Organization) Sessions.getCurrent()
                .getAttribute("organization");
        organizationUpdata_wind=(Window)this.getFellow("organizationUpdata_wind");
        name = (Textbox) this.getFellow("name");
        fullName = (Textbox) this.getFellow("fullName");
        engName = (Textbox) this.getFellow("engName");
        code = (Textbox) this.getFellow("code");
        this.strCode = code.getValue();
        site = (Textbox) this.getFellow("site");
        order = (Intbox) this.getFellow("order");
        type = (Combobox) this.getFellow("type");
        type.setSelectedIndex(0);
        if (organizationUpdata.getType() != null
                && !organizationUpdata.getType().equals(""))
        {
            if (organizationUpdata.getType().equals("1"))
            {
                type.setSelectedIndex(1);
            }
        }
        if (organizationUpdata.getOrder() != 0)
        {
            order.setValue(organizationUpdata.getOrder());
        }
        email = (Textbox) this.getFellow("email");
        postalAddress = (Textbox) this.getFellow("postalAddress");
        postalCode = (Textbox) this.getFellow("postalCode");
        telephoneNumber = (Textbox) this.getFellow("telephoneNumber");
        description = (Textbox) this.getFellow("description");
        email_label = (Label) this.getFellow("email_label");
        url_label = (Label) this.getFellow("url_label");
        organizationUpdata_wind.setTitle(org.zkoss.util.resource.Labels
              .getLabel("organization.ui.operatingOrg")
                + "【"
                + name.getValue()
                + "】");
        // updateOrganinzation_cation.setLabel(org.zkoss.util.resource.Labels
        // .getLabel("organization.ui.operatingOrg")
        // + this.organization.getName());
        // viewChange();
    }
    //重置
    public void resetResource()
     {
    	 Organization organizationUpdata = (Organization) Sessions.getCurrent()
                 .getAttribute("organization");
 	  name.setValue(organizationUpdata.getName());
 	 fullName.setValue(organizationUpdata.getFullName());
 	engName.setValue(organizationUpdata.getEngName());
 	 if (organizationUpdata.getType() != null
             && !organizationUpdata.getType().equals(""))
     {
         if (organizationUpdata.getType().equals("1"))
         {
             type.setSelectedIndex(1);
         }
         else
         {
        	 type.setSelectedIndex(0);
         }
     }
 	email.setValue(organizationUpdata.getEmail());
 	telephoneNumber.setValue(organizationUpdata.getTelephoneNumber());
 	postalCode.setValue(organizationUpdata.getPostalCode());
 	site.setValue(organizationUpdata.getSite());
 	code.setValue(organizationUpdata.getCode());
 	postalAddress.setValue(organizationUpdata.getPostalAddress());
 	description.setValue(organizationUpdata.getDescription());
 	   }

    /**
     * 删除
     */
    public void resourceDelete()
    {
        OrganizationListWin listWin = (OrganizationListWin) this.getDesktop()
                .getAttribute("OrganizationListWin");
        listWin.resourceDelete();
    }

    /**
     * 添加下级组织
     */
    public void resourceAdd()
    {
        OrganizationListWin organizationListWin = (OrganizationListWin) this
                .getDesktop().getAttribute("OrganizationListWin");
        organizationListWin.resourceAdd();
    }
    /**
     * 排序
     * @throws DataAccessException 
     */
    public void resourceSort() throws DataAccessException
    {
    	OrgnazationSortWindow	osWindow= (OrgnazationSortWindow)Executions.createComponents("/apps/core/orgnazationsort.zul",null, null);
    	osWindow.doHighlighted();
    	Organization organizationUpdata = (Organization) Sessions.getCurrent()
                 .getAttribute("organization");
    	osWindow.initWindow(organizationUpdata);
    	 final OrganizationListWin organizationListWin = ((OrganizationListWin) this
                 .getDesktop().getAttribute("OrganizationListWin"));
    	osWindow.addEventListener(Events.ON_CHANGE, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				Organization organizationUpdata = (Organization) Sessions.getCurrent()
		                 .getAttribute("organization");
				organizationListWin.rebuilderTree(organizationUpdata);
			}
		});
 }
}
