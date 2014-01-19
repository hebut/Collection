package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Role;

public class RoleAddPermissionWin extends Window implements AfterCompose
{
    /**
	 * 
	 */
    private static final long      serialVersionUID      = 1L;

    private Listbox                roleListbox;

    private List<Role>             roles;

    private IRoleManager           roleManager;

    private Permission             permission;

    private List<Role>             useRoles;

    public RoleAddPermissionWin()
    {
        roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
        try
        {
            Map map = Executions.getCurrent().getArg();
            this.permission = (Permission) map.get("permission");
            roles = roleManager.getRoleListAllOrderbyRoleId();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void afterCompose()
    {
        roleListbox = (Listbox) this.getFellow("roleListbox");
        fullRoleListbox();
        this.setTitle(org.zkoss.util.resource.Labels
                .getLabel("resource.ui.being")
                + "‘"
                + permission.getName()
                + "’"
                + org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.addauthorizerole"));
    }

    /**
	 * 填充角色列表
	 */
    public void fullRoleListbox()
    {
        try
        {
//            useRoles = permissionroleManager
//                    .getRolesByPermissionIdInPermissionRole(permission
//                            .getKpid());

            useRoles = new ArrayList();
            Listcell cell1 = null;
            Listcell cell2 = null;
            Listitem item = null;
            for (Role r : roles)
            {
                cell1 = new Listcell();
                cell2 = new Listcell();
                cell1.setLabel(r.getName());
                cell2.setLabel(r.getDescription());
                item = new Listitem();
                for (Role tempRole : useRoles)
                {
                    if (r.getRoleId() == tempRole.getRoleId())
                    {
                        item.setSelected(true);
                    }
                }
                item.setValue(r);
                item.appendChild(cell1);
                item.appendChild(cell2);
                roleListbox.appendChild(item);
            }
        } catch (org.springframework.dao.DataAccessException e)
        {
            e.printStackTrace();
        }

    }

    /**
	 * 保存角色许可信息
	 */
//    public void saveRolePermission()
//    {
//        try
//        {
//            if (useRoles.size() != 0)
//            {
//                this.permissionroleManager.deletePermissionRole(permission
//                        .getKpid(), useRoles);
//            }
//            Set<Listitem> set = roleListbox.getSelectedItems();
//            Permissionrole pr = null;
//            for (Listitem item : set)
//            {
//                Role role = (Role) item.getValue();
//                pr = new Permissionrole();
//                pr.setPermissionId(permission.getKpid());
//                pr.setRoleId(role.getRoleId());
//                permissionroleManager.add(pr);
//            }
//            Messagebox.show(org.zkoss.util.resource.Labels
//                    .getLabel("system.commom.ui.savesuccess"),
//                    org.zkoss.util.resource.Labels
//                            .getLabel("system.commom.ui.prompt"),
//                    Messagebox.OK, Messagebox.EXCLAMATION);
//            this.detach();
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }

}
