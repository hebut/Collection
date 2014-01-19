package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class UserroleWin extends Window implements AfterCompose
{
    private Bandbox          organizationId, organizationUse;

    private Role             role;

    private Organization     selectedOrganization;

    private OrganizationTree parentData, organizations;

    private IUserroleManager userroleManager = (IUserroleManager) SpringUtil
                                                     .getBean("userroleManager");

    private IUsersManager    usersManager;

    private Listbox          user_listbox, userrole_listbox;

    private List<Users>      users, userroleUsers, seachUsers, SeachUseUsers;

    public List<Users> getUsers()
    {
        return users;
    }

    public void setUsers(List<Users> users)
    {
        this.users = users;
    }

    public List<Users> getUserroleUsers()
    {
        return userroleUsers;
    }

    public void setUserroleUsers(List<Users> userroleUsers)
    {
        this.userroleUsers = userroleUsers;
    }

    /**
	 * 构造初始化数据
	 */
    public UserroleWin()
    {
        try
        {
            usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
            role = (Role) Executions.getCurrent().getArg().get("role");
            userroleUsers = userroleManager.getUsersByRoleId(role.getRoleId());
            users = usersManager.getAllUser();
            for (Users ru : userroleUsers)
            {
                for (int i = 0; i < users.size(); i++)
                {
                    Users au = users.get(i);
                    if (ru.getUserId().equals(au.getUserId()))
                    {
                        users.remove(au);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void afterCompose()
    {
        organizationId = (Bandbox) this.getFellow("organizationId");
        organizationUse = (Bandbox) this.getFellow("organizationUse");
        parentData = (OrganizationTree) this.getFellow("parentData");
        organizations = (OrganizationTree) this.getFellow("organizations");
        this.parentData.setLabelProvider(new OrganizationLabelProvider());
        this.parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        this.parentData.rebuildTree();
        this.organizations.setLabelProvider(new OrganizationLabelProvider());
        this.organizations
                .setContentProvider(new OrganizationThirdContentProvider());
        this.organizations.rebuildTree();
        fullBandBox1();
        fullBandBox2();
        user_listbox = (Listbox) this.getFellow("users_listbox");
        userrole_listbox = (Listbox) this.getFellow("userrole_listbox");
        fullBandBox1();
        this.setTitle(org.zkoss.util.resource.Labels
                .getLabel("resource.ui.youbeing")
                + "‘"
                + role.getName()
                + "’"
                + org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.addauthorizeuser"));
    }

    /**
	 * 填充bandbox
	 */
    public void fullBandBox1()
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
                            UserroleWin.this.setOrganization1();
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
	 * 填充bandbox
	 */
    public void fullBandBox2()
    {
        Iterator children = organizations.getItems().iterator();
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
                            UserroleWin.this.setOrganization2();
                        }
                    });
            re = (Organization) treeItem.getValue();
            if (re.getOrganizationId() == 0)
            {
                treeItem.setSelected(true);
            }

        }
    }

    public void setOrganization1()
    {
        selectedOrganization = (Organization) parentData.getSelectedItem()
                .getValue();
        organizationId.setValue(selectedOrganization.getName());
        organizationId.close();
        seachUsers = doSeach(users, seachUsers, selectedOrganization
                .getOrganizationId());
        fullListbox(seachUsers, user_listbox);
    }

    public void setOrganization2()
    {
        selectedOrganization = (Organization) organizations.getSelectedItem()
                .getValue();
        organizationUse.setValue(selectedOrganization.getName());
        organizationUse.close();
        SeachUseUsers = doSeach(userroleUsers, SeachUseUsers,
                selectedOrganization.getOrganizationId());
        fullListbox(SeachUseUsers, userrole_listbox);
    }
    /**
	 * 查询
	 * @param us用户列表
	 * @param seachResource资源
	 * @param organizationId组织的ID
	 * @return用户列表
	 */
    public List<Users> doSeach(List<Users> us, List<Users> seachResource,
            int organizationId)
    {
        if (us == null)
            return null;
        if (seachResource == null)
        {
            seachResource = new ArrayList<Users>();
        }
        seachResource.clear();// clear List
        for (Users u : us)
        {
            if (u.getOrganizationId() == organizationId)
            {
                seachResource.add(u);
            }
        }
        return seachResource;
    }

    /**
	 * 填充listbox
	 * @param users用户
	 * @param listbox要填充的listbox
	 */
    public void fullListbox(List<Users> users, Listbox listbox)
    {
        if (users == null)
            return;
        listbox.getItems().clear();
        Listcell cellsel,cellName, cellLoginName;
        for (Users u : users)
        {
        	cellsel = new Listcell();
            cellName = new Listcell();
            cellLoginName = new Listcell();
            cellLoginName.setLabel(u.getLoginName());
            cellName.setLabel(u.getName());
            Listitem item = new Listitem();
            item.setValue(u);
            item.appendChild(cellsel);
            item.appendChild(cellName);
            item.appendChild(cellLoginName);
            listbox.appendChild(item);
        }
    }

    /**
	 * 清空用户信息
	 * @param event
	 */
    public void clearUserUser(Event event)
    {
        Bandbox bandbox = (Bandbox) event.getTarget();
        if (bandbox.getValue() == null || bandbox.getValue().trim().equals(""))
        {
            fullListbox(users, user_listbox);
        }
    }
    /**
	 * 清空用户信息
	 * @param event
	 */
    public void clearUser(Event event)
    {
        Bandbox bandbox = (Bandbox) event.getTarget();
        if (bandbox.getValue() == null || bandbox.getValue().trim().equals(""))
        {
            fullListbox(userroleUsers, userrole_listbox);
        }
    }
    /**
	 * 向右移动
	 */
    public void toRight()
    {
        Set<Listitem> selectedItems = (Set<Listitem>) user_listbox
                .getSelectedItems();
        List<Listitem> itemsList = new ArrayList<Listitem>(selectedItems);
        for (Listitem item : itemsList)
        {
            item.setParent(userrole_listbox);
        }
    }
    /**
	 * 向左移动
	 */
    public void toLeft()
    {
        Set<Listitem> selectedItems = (Set<Listitem>) userrole_listbox
                .getSelectedItems();
        List<Listitem> itemsList = new ArrayList<Listitem>(selectedItems);
        for (Listitem item : itemsList)
        {
            item.setParent(user_listbox);
        }
    }

    public void allToRight()
    {
        List<Listitem> itemsList = user_listbox.getItems();
        for (int i = 0; i < itemsList.size(); i++)
        {
            Listitem item = itemsList.get(i);
            item.setParent(userrole_listbox);
            i--;
        }
    }

    /**
	 * 全部向左移动
	 */
    public void allToLeft()
    {
        List<Listitem> itemsList = userrole_listbox.getItems();
        for (int i = 0; i < itemsList.size(); i++)
        {
            Listitem item = itemsList.get(i);
            item.setParent(user_listbox);
            i--;
        }
    }
    /**
	 * 保存
	 */
    public void save()
    {
        try
        {
            List<Listitem> items = userrole_listbox.getItems();
            List<Users> users = new ArrayList<Users>();
            for (int i = 0; i < items.size(); i++)
            {
                for (int j = (i + 1); j < items.size(); j++)
                {
                    Users u1 = (Users) items.get(i).getValue();
                    Users u2 = (Users) items.get(j).getValue();
                    if (u1.getUserId() == u2.getUserId())
                    {
                        items.remove(j);
                    }
                }
                users.add((Users) items.get(i).getValue());
            }
            userroleManager.updateUserRole(role.getRoleId(), users);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
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
                        .getLabel("system.commom.ui.savefailed1"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e.printStackTrace();
            }
        }
    }
}
