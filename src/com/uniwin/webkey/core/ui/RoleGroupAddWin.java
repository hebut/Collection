package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;

public class RoleGroupAddWin extends Window implements AfterCompose
{
	private static final long serialVersionUID = 3531940362508911723L;

	private Textbox            name, description;

    private Checkbox           isDefult;

    private IRoleManager       roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
    
    WkTWebsite website = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");

    private Radio              isUse;
    
    private Toolbarbutton     save;

    public void afterCompose()
    {
    	Components.wireVariables(this, this);
        Components.addForwards(this, this);
        
        name = (Textbox) this.getFellow("name");
        isDefult = (Checkbox) this.getFellow("isDefult");
        isUse = (Radio) this.getFellow("isUse");
        description = (Textbox) this.getFellow("description");        
        this.addForward(Events.ON_OK, save, Events.ON_CLICK);
    }

    /**
	 * 保存添加的角色组
	 * @throws InterruptedException 
	 */
    public void onClick$save() throws InterruptedException
    {

        if (name.getText().trim().equals(""))
        {
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("role.ui.rolenamevalidate"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        if (name.getText().trim().length() > 40)
        {
        	Messagebox.show(org.zkoss.util.resource.Labels.getLabel("role.ui.rolenamelimit"),
					org.zkoss.util.resource.Labels
							.getLabel("system.commom.ui.prompt"),
					Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        try
        {
            Role role = new Role();
            role.setDescription(description.getText());
            role.setName(name.getText());
            role.setRoleName(name.getText());
            role.setState(isUse.isChecked() ? 0 : 1);
            role.setIsDefult(isDefult.isChecked() ? 1 : 0);
            role.setKwid(website.getKwId().intValue());
            role.setpId(0);
            if (role.getpId() != 0)
            {
                Role parentRole = roleManager.get(role.getpId());
                List childrenList = roleManager.getchildrenByParetId(role
                        .getpId());
                parentRole.setChildrenNum(childrenList.size());
                roleManager.update(parentRole);
            }
            roleManager.add(role);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            Events.postEvent(Events.ON_CHANGE, this, null);
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.addfailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
    }

    public void onClick$renturn()
    {
        this.detach();
    }
    
}
