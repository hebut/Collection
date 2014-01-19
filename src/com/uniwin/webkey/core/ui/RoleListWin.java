package com.uniwin.webkey.core.ui;

import java.util.Map;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

import com.uniwin.webkey.component.ui.RoleListGrid;
import com.uniwin.webkey.core.model.Resource;

public class RoleListWin extends Window implements AfterCompose
{
    private Map mRole;

    public Map getmRole()
    {
        return mRole;
    }

    public void setmRole(Map mRole)
    {
        this.mRole = mRole;
    }

    /**
     * 构造初始化数据
     */
    public RoleListWin()
    {
        mRole = (Map) ((Map) Sessions.getCurrent().getAttribute("rsopMap"))
            .get("rs"+ ((Resource) Sessions.getCurrent().getAttribute("currResource")).getId());
    }

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        Sessions.getCurrent().setAttribute("roleListWin",this);
    }
    
    /**
     * 添加角色信息
     */
    public void addRole()
    {
        try
        {
            Sessions.getCurrent().setAttribute("role", null);
            Window roleAddWin = (Window) Executions.getCurrent().createComponents("/apps/core/roleAdd.zul", null, null);
            roleAddWin.setParent(this);
            roleAddWin.setClosable(true);
            roleAddWin.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void refresh(){
        RoleListGrid roleListGrid = (RoleListGrid) this.getFellow("roleListGrid");
        roleListGrid.getChildren().clear();
        roleListGrid.invalidate();
        roleListGrid.afterCompose();
    }
}
