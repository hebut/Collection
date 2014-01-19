package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.tree.ui.RoleTreeLabelProvider;
import com.uniwin.webkey.tree.ui.RolegroupTree;
import com.uniwin.webkey.tree.ui.RoletreeContentProvider;
import com.uniwin.webkey.util.ui.CheckLoginFilter;

public class RoleUpdataWin extends Window implements AfterCompose
{
    private Checkbox           isDefult;

    private Textbox            name, description;

    private IRoleManager       roleManager;

    private Radio              isUse, notUse;

    private Bandbox            roleBanbox;

    private RolegroupTree      roletree;

    private Role               selectRole, parentRole,editRole;
    
    public Role getEditRole()
    {
        return editRole;
    }

    public void setEditRole(Role editRole)
    {
        this.editRole = editRole;
    }

    public RoleUpdataWin()
    {
        roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
        Map params = Executions.getCurrent().getArg();
        if (null!=params&&!params.isEmpty())
        {
            editRole = (Role) params.get("editRole");
        }
    }

    public void afterCompose()
    {
        isDefult = (Checkbox) this.getFellow("isDefult");
        name = (Textbox) this.getFellow("name");
        isUse = (Radio) this.getFellow("isUse");
        notUse = (Radio) this.getFellow("notUse");
        description = (Textbox) this.getFellow("description");
        roleBanbox = (Bandbox) this.getFellow("roleBanbox");
        roletree = (RolegroupTree) this.getFellow("parentData");
        creatroletree();
        fullBandBox();
        if (editRole.getpId() != 0)
        {
            try
            {
                name.setValue(editRole.getName());
                description.setValue(editRole.getDescription());
                if (editRole.getState()==0)
                {
                    isUse.setChecked(true);
                }else{
                    notUse.setChecked(true);
                }
                if (editRole.getIsDefult()==1)
                {
                    isDefult.setChecked(true);
                }
                parentRole = roleManager.get(editRole.getpId());
                roleBanbox.setValue(parentRole.getName());
                selectRole = parentRole;
            } catch (DataAccessException e)
            {
                e.printStackTrace();
            } catch (ObjectNotExistException e)
            {
                e.printStackTrace();
            }
        }
         isDefult.setChecked(editRole.getIsDefult()==1);
    }
	/**
	 * 填充bandbox
	 */
    public void fullBandBox()
    {
        Iterator children = roletree.getItems().iterator();
        Treeitem treeItem = null;
        Role re = null;
        Object obj = null;
        while (children.hasNext()){
            obj = children.next();
            treeItem = (Treeitem) obj;
            treeItem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            RoleUpdataWin.this.setKrId();
                        }
                    });
            re = (Role) treeItem.getValue();            
            if (re.getRoleId() == editRole.getpId()){
                treeItem.setSelected(true);
            }
            
            if(re.getpId()!=0){
            	treeItem.setVisible(false);
            }
        }

    }
    /**
	 * 将选择的组织添加到文本框中
	 */
    public void setKrId()
    {
        selectRole = (Role) roletree.getSelectedItem().getValue();
        roleBanbox.setValue(selectRole.getName());
        roleBanbox.close();
    }

    /**
	 * 创建角色树
	 */
    public void creatroletree()
    {
        roletree.setLabelProvider(new RoleTreeLabelProvider());
        RoletreeContentProvider content = new RoletreeContentProvider();
        roletree.setContentProvider(content);
        roletree.rebuildTree();
    }

    public List<Role> checkgroup(int pid, List<Role> list)
            throws DataAccessException, ObjectNotExistException
    {

        Role role = roleManager.get(pid);
        list.add(role);
        List<Role> roles1 = roleManager.getchildrenByParetId(pid);
        for (Role r : roles1)
        {
            list.add(r);
            checkgroup(r.getRoleId(), list);
        }
        return list;

    }

    /**
	 * 角色信息修改
	 * 
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
    public void roleUpdata() throws DataAccessException,
            ObjectNotExistException
    {
        Role role = new Role();
        role.setRoleId(editRole.getRoleId());
        role.setState(isUse.isChecked() ? 0 : 1);
        role.setDescription(description.getText());
        role.setIsDefult(isDefult.isChecked() ? 1 : 0);
        role.setName(name.getText());
        role.setRoleName(name.getText());
        role.setChildrenNum(editRole.getChildrenNum());
        if (roleBanbox.getValue() != null && !roleBanbox.getValue().equals(""))
        {
            Role checkrole = new Role();
            checkrole.setName(roleBanbox.getValue());
            checkrole.setRoleName(roleBanbox.getValue());
            List list = roleManager.getList(checkrole);
            if (list != null && list.size() > 0)
            {
                selectRole = (Role) list.get(0);
                List<Role> rs = this.checkgroup(editRole.getRoleId(),new ArrayList());
                boolean key = false;
                for (int i = 0; i < rs.size(); i++)
                {
                    Role r = rs.get(i);
                    if (selectRole.getRoleId() == r.getRoleId())
                    {
                        key = true;
                        break;
                    }
                }
                if (selectRole.getRoleId() == editRole.getRoleId() || key)
                {
                    try
                    {
                        Messagebox.show(org.zkoss.util.resource.Labels
                                .getLabel("role.ui.rolegroupvalidate"),
                                org.zkoss.util.resource.Labels
                                        .getLabel("system.commom.ui.prompt"),
                                Messagebox.OK, Messagebox.EXCLAMATION);
                        return;
                    } catch (WrongValueException e)
                    {
                        e.printStackTrace();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (selectRole != null && roleBanbox.getValue() != null
                && !roleBanbox.getValue().equals(""))
        {
            role.setpId(selectRole.getRoleId());
        } else
        {
            role.setpId(0);
        }
        try
        {
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
            if (parentRole != null && parentRole.getRoleId() != role.getpId())
            {
                List childrenList = roleManager.getchildrenByParetId(parentRole
                        .getRoleId());
                parentRole.setChildrenNum(childrenList.size());
                roleManager.update(parentRole);
            }
            if (role.getpId() != 0)
            {
                Role parentrole = roleManager.get(role.getpId());
                List childrenList = roleManager.getchildrenByParetId(role
                        .getpId());
                parentrole.setChildrenNum(childrenList.size());
                roleManager.update(parentrole);
            }

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
}
