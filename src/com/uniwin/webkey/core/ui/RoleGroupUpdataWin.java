package com.uniwin.webkey.core.ui;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.util.ui.CheckLoginFilter;

public class RoleGroupUpdataWin extends Window implements AfterCompose
{
	private static final long serialVersionUID = 9218294711559030635L;

	private Checkbox           isDefult;

    private Textbox            name, description;

    private Radio              isUse, notUse;
    
    private Button             save;
    
    private IRoleManager       roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
    
    Role editRoleGroup = (Role)Executions.getCurrent().getArg().get("editRoleGroup");
    
    WkTWebsite website = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");

    public void afterCompose(){
    	Components.wireVariables(this, this);
        Components.addForwards(this, this);
                
        name.setValue(editRoleGroup.getName());
        description.setValue(editRoleGroup.getDescription());
        if (editRoleGroup.getState()==0)
        {
            isUse.setChecked(true);
        }else{
            notUse.setChecked(true);
        }
        if (editRoleGroup.getIsDefult()==1)
        {
            isDefult.setChecked(true);
        }
            
         isDefult.setChecked(editRoleGroup.getIsDefult()==1);         
         this.addForward(Events.ON_OK, save, Events.ON_CLICK);
    }	
    /**
	 * 角色信息修改
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
    public void onClick$save() throws DataAccessException,ObjectNotExistException{
        Role role = new Role();
        role.setRoleId(editRoleGroup.getRoleId());
        role.setState(isUse.isChecked() ? 0 : 1);
        role.setDescription(description.getText());
        role.setIsDefult(isDefult.isChecked() ? 1 : 0);
        role.setName(name.getText());
        role.setRoleName(name.getText());
        role.setChildrenNum(editRoleGroup.getChildrenNum());
//        role.setpId(editRoleGroup.getRoleId());
        role.setKwid(website.getKwId().intValue());
//        if (role.getpId() != 0)
//        {
//            Role parentRole = roleManager.get(role.getpId());
//            List childrenList = roleManager.getchildrenByParetId(role
//                    .getpId());
//            parentRole.setChildrenNum(childrenList.size());
//            roleManager.update(parentRole);
//        }
        
        try{
            if (name.getText().trim().equals("")
                    && name.getText().trim().length() < 40)
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
            roleManager.update(role);
            Events.postEvent(Events.ON_CHANGE, this, null);

            CheckLoginFilter.reloadSessionsData();
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.updatesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
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
        }
    }
    
    public void onClick$renturn()
    {
        this.detach();
    }
    
}
