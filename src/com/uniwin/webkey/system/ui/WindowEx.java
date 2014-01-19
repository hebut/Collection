package com.uniwin.webkey.system.ui;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class WindowEx extends Window
{

    public Object getBean(String beanId)
    {
        return FrameCommonDate.getBean(beanId);
    }

    public User getUser()
    {
        return FrameCommonDate.getUser();
    }

    public List<Menu> getFirstMenu()
    {
        return FrameCommonDate.getFirstMenu("ws_"+((WkTWebsite) Sessions.getCurrent()
                .getAttribute("domain_defult")).getKwId());
    }

    public final List<Permission> getPermissions()
    {
        return FrameCommonDate.getPermissions();
    }

    public boolean hasPermission(int permissionId)
    {
        return FrameCommonDate.hasPermission(permissionId);
    }

}
