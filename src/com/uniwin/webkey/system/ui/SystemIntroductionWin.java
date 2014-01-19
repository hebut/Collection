package com.uniwin.webkey.system.ui;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

import com.uniwin.webkey.system.SystemIntroductionService;

public class SystemIntroductionWin extends Window implements AfterCompose
{
    private String con = "";

    public String getCon()
    {
        return con;
    }

    public void setCon(String con)
    {
        this.con = con;
    }

    public SystemIntroductionWin()
    {
        String path = ((HttpServletRequest) Executions.getCurrent()
                .getNativeRequest()).getSession().getServletContext()
                .getRealPath("/")
                + "apps\\sysconfig\\";
        String content = SystemIntroductionService.readSystemIntroduction(path);
        this.setCon(content);
    }

    public void afterCompose()
    {

    }

}
