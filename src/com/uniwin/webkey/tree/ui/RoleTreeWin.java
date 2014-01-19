package com.uniwin.webkey.tree.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.util.ui.RoleContentProvider;

public class RoleTreeWin extends Window implements AfterCompose
{
    private RoleTree           noOpera;

    private IPermissionManager permissionManager;

    private Role               roleSelected;

    public RoleTreeWin()
    {
        permissionManager = (IPermissionManager) SpringUtil.getBean(
                "permissionManager");
    }

    public void afterCompose()
    {
        roleSelected = (Role) Executions.getCurrent().getArg().get("role");
        noOpera = (RoleTree) this.getFellow("noOpera");
        noOpera.setTreeitemlContentProvider(new RoleContentProvider());
        noOpera.rebuildTree();
        addPermission();
        initSeledtedItem();
    }

    /**
     * 根据当前角色初始化并选中复选框
     * 
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
     * 初始化选中项
     */
    public void initSeledtedItem()
    {
        try
        {
//            List<Integer> permissionIds = permissionManager
//            .getPermissionIdsByRoleId(roleSelected.getRoleId());
            List<Integer> permissionIds = new ArrayList<Integer>();
            Checkbox box = null;
            for (Integer id : permissionIds)
            {
                box = (Checkbox) noOpera.getFellow("" + id.intValue());
                box.setChecked(true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
