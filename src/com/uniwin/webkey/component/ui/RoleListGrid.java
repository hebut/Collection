package com.uniwin.webkey.component.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;

public class RoleListGrid extends Grid implements AfterCompose
{
    private List<Role>   roles;

    private IRoleManager roleManager = (IRoleManager) SpringUtil.getBean("roleManager");

    private int          iPid        = -1;
    
    private Window       roleListWin = (Window) Sessions.getCurrent().getAttribute("roleListWin");
    
    public int getiPid()
    {
        return iPid;
    }

    public void setiPid(int iPid)
    {
        this.iPid = iPid;
    }

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        init(iPid);
    }
    
    public RoleListGrid()
    {
        setiPid(0);
    }
    
    public RoleListGrid(int iPid)
    {
        init(iPid);
    }
    
    public void init(int pid){
        try
        {
            roles = roleManager.getParentRoleList(pid);
            
            
            System.out.println("---pid-----"+pid);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        Columns columns = new Columns();
        Column column1 = new Column();
        Column column2 = new Column();
        Column column3 = new Column();
        Column column4 = new Column();
        Column column5 = new Column();
        
        column1.setWidth("20px");
        column2.setLabel("角色名称");
        column3.setLabel("状态");
        column3.setWidth("70px");
        column4.setLabel("是否默认角色");
        column4.setWidth("100px");
        column4.setAlign("center");
        column5.setLabel("操作");
        column5.setWidth("62px");
        column5.setAlign("center");
        if (roles.size()>0)
        {
            columns.appendChild(column1);
            columns.appendChild(column2);
            columns.appendChild(column3);
            columns.appendChild(column4);
            columns.appendChild(column5);
            this.appendChild(columns);
        }
        
        if (pid!=0)
        {
            this.setStyle("border:0px");
            
        }
        
        this.setRowRenderer(new RowRenderer()
        {
            public void render(final Row row, Object data) throws Exception
            {
                final Role role = (Role) data;
                Detail detail = new Detail();
                detail.setOpen(false);
                RoleListGrid grid = new RoleListGrid(role.getRoleId());
                detail.appendChild(grid);
                row.setStyle("border-color:#DADADA;");
                detail.setParent(row);
                String isDef = "";
                new org.zkoss.zul.Label(role.getName()).setParent(row);
                new org.zkoss.zul.Label(role.getState()+"").setParent(row);
                if (role.getIsDefult() == 1)
                {
                    isDef = "是";
                } else
                {
                    isDef = "否";
                }
                new org.zkoss.zul.Label(isDef).setParent(row);
                
                Hbox hbox = new Hbox();
                Image iDel = new Image();
                Image iEdit = new Image();
                Image iAuth = new Image();
                iDel.setType("delList");
                iEdit.setType("edit");
                iAuth.setType("authorize");
                iDel.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        int i = delRole(role);
                        if (i>0)
                        {
                            row.detach();   
                        }
                    }
                });
                iEdit.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        editRole(role);
                    }
                });
                iAuth.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        authRole(role);
                    }
                });
                iEdit.setParent(hbox);
                iDel.setParent(hbox);
                iAuth.setParent(hbox);
                hbox.setParent(row);
            }
        });
        ListModelList listModel = new ListModelList(roles);
        this.setModel(listModel);
    }
    
    public int delRole(Role role){
        try
        {
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                .getLabel("resource.ui.isconfirmdeleterole"), org.zkoss.util.resource.Labels
                .getLabel("system.commom.ui.prompt"), Messagebox.OK | Messagebox.CANCEL,
                Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return -1;
            }
            roleManager.remove(role);
            Messagebox.show(org.zkoss.util.resource.Labels
                .getLabel("system.commom.ui.deletesuccess"), org.zkoss.util.resource.Labels
                .getLabel("system.commom.ui.prompt"), Messagebox.OK, Messagebox.INFORMATION);
            return 1;
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
            }
            return -1;
        }
    }
    
    public void editRole(Role role){
        if (role.getpId()!=0)
        {
            Sessions.getCurrent().setAttribute("role",role);
            Map params = new HashMap();
            params.put("editRole", role);
            Window roleEditWin = (Window) Executions.createComponents("/apps/core/roleUpdata.zul",roleListWin,params);
            roleEditWin.setClosable(true);
            roleEditWin.addEventListener(Events.ON_CHANGE, new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    init(0);
                }
            });
            try
            {
                roleEditWin.doModal();
            } catch (SuspendNotAllowedException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }else{
            try
            {
                Messagebox.show("不能修改角色组!","警告",
                        Messagebox.OK, Messagebox.EXCLAMATION);
            } catch (Exception e2)
            {
            }
            return;
        }
    }
    
    public void authRole(Role role){
        try
        {
            Map map = new HashMap();
            map.put("role", role);
            Window window = (Window) Executions.createComponents(
                    "/apps/core/userrole.zul", roleListWin, map);
            window.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
