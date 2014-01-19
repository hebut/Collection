package com.uniwin.webkey.util.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.tree.ui.RoleTree;

public class RoleLicensingWin extends Window implements AfterCompose
{
    private RoleTree           noOpera;

    private IPermissionManager permissionManager;

    private IRoleManager       roleManager;

    private Role               sessionRole;

    private Role               selectRole, parentRole;

    public RoleLicensingWin()
    {
        roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
        permissionManager = (IPermissionManager) SpringUtil.getBean("permissionManager");
        this.sessionRole = (Role) Sessions.getCurrent().getAttribute("role");
    }

    public Role getSessionRole()
    {
        return sessionRole;
    }

    public void setSessionRole(Role sessionRole)
    {
        this.sessionRole = sessionRole;
    }

    public void afterCompose()
    {
        noOpera = (RoleTree) this.getFellow("noOpera");
        noOpera.setTreeitemlContentProvider(new RoleContentProvider());
        noOpera.rebuildTree();
        if (sessionRole != null && sessionRole.getpId() != 0)
        {
            try
            {
                parentRole = roleManager.get(sessionRole.getpId());
                selectRole = parentRole;
            } catch (DataAccessException e)
            {
                e.printStackTrace();
            } catch (ObjectNotExistException e)
            {
                e.printStackTrace();
            }
        }

        addPermission();
        initSeledtedItem();
        // isDefult.setChecked(sessionRole.getIsDefult()==1);
    }
	/**
	 * 添加样式
	 * @param list
	 */
    private void setClass(List list)
    {
        for (Object o : list)
        {
            if (o instanceof Treeitem)
            {
                ((Treeitem) o).setSclass("treecell");
                ((Treeitem) o).getTreerow().setSclass("treecell");
                setClass(((Treeitem) o).getChildren());
            }

        }
    }

	/**
	 * 初始化选中项
	 */
    public void initSeledtedItem()
    {
        if (sessionRole != null)
        {
            try
            {
//                List<Integer> permissionIds = permissionManager
//                .getPermissionIdsByRoleId(sessionRole.getRoleId());
                List<Integer> permissionIds = new ArrayList<Integer>();
                Checkbox box = null;
                for (Integer id : permissionIds)
                {
                    try
                    {
                        box = (Checkbox) noOpera.getFellow("" + id.intValue());
                        box.setChecked(true);
                    } catch (Exception e)
                    {
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
        	System.out.println("要操作的ROle对象为空！");
        }

    }
	/**
	 * 添加复选按钮并初始化选中
	 * @param item
	 */
    public void addCheckbox(Treeitem item)
    {
        try
        {
            List list = item.getChildren();
            for (Object o : list)
            {
                if (o instanceof Treerow)
                {
                    o = (Treerow) o;
                    Treecell cell = (Treecell) ((Treerow) o).getChildren().get(
                            0);
                    Resource re = (Resource) item.getValue();
//                    List<Permission> permissionList = permissionManager
//                    .getPermissionByResourceId(re.getId());
                    List<Permission> permissionList = new ArrayList<Permission>();
                    for (Permission per : permissionList)
                    {
                        Checkbox checkbox = new Checkbox();
                        checkbox.setLabel(per.getName());
                        checkbox.setStyle("color:purple");
                        checkbox.setStyle("color:purple");
                        checkbox.setId(per.getKpid() + "");
                        cell.appendChild(checkbox);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	/**
	 * 添加许可信息
	 */
    public void addPermission()
    {
        Collection collection = noOpera.getItems();
        Object[] obs = collection.toArray();
        Resource re = null;
        for (int i = 0; i < obs.length; i++)
        {
            Treeitem item = (Treeitem) obs[i];
            addCheckbox(item);
        }
    }

	/**
	 *修改许可信息
	 */
    public void permissionUpdata()
    {
        Role role = new Role();
        if (sessionRole != null)
        {
            role = sessionRole;
            try
            {
                if (role.getChildrenNum() != 0)
                {
                    List childrenList = roleManager.getchildrenByParetId(role
                            .getRoleId());
                    for (int i = 0; i < childrenList.size(); i++)
                    {
                        Role childrenRole = (Role) childrenList.get(i);
                        if (childrenRole.getChildrenNum() == 0)
                        {
//                            roleManager.updateRoleAndPermission(childrenRole,
//                                    this.getPermissionIds());
                        }
                    }
                }
                if (role.getChildrenNum() == 0)
                {
//                    roleManager.updateRoleAndPermission(role, this
//                            .getPermissionIds());
                }
                // CheckLoginFilter.reloadSessionsData();
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.updatesuccess"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
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
        } else
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.pleaseaddrole"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
	/**
	 * 组合许可信息的id
	 * @return
	 */
    public String getPermissionIds()
    {
        String ids = "";
        Iterator iters = noOpera.getItems().iterator();
        Permission per = null;
        Checkbox checkbox = null;
        while (iters.hasNext())
        {
            Treeitem item = (Treeitem) iters.next();
            List boxList = ((Treecell) item.getTreerow().getChildren().get(0))
                    .getChildren();
            for (Object box : boxList)
            {
                if (box instanceof Checkbox)
                {
                    box = (Checkbox) box;
                    if (((Checkbox) box).isChecked())
                    {
                        ids += ((Checkbox) box).getId() + ",";
                    }
                }
            }
        }

        if (ids.length() > 0)
        {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

	/**
	 * 根据id选中许可信息
	 * @param id
	 */
    public void setPermissionIdSelected(int id)
    {
        Iterator iters = noOpera.getItems().iterator();
        Permission per = null;
        Checkbox checkbox = null;
        String ids = "";
        while (iters.hasNext())
        {
            Treeitem item = (Treeitem) iters.next();
            if (item.getValue() instanceof Permission)
            {
                per = (Permission) item.getValue();
                checkbox = (Checkbox) item
                        .getFellow(per.getKpid() + "");
            }
        }
    }

}
