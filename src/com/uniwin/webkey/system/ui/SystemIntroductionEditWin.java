package com.uniwin.webkey.system.ui;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.system.SystemIntroductionService;

public class SystemIntroductionEditWin extends Window implements AfterCompose
{
    private Textbox systemIntroduction_textbox;

    private String  path = ((HttpServletRequest) Executions.getCurrent()
                                 .getNativeRequest()).getSession()
                                 .getServletContext().getRealPath("/")
                                 + "apps\\sysconfig\\";

    public void afterCompose()
    {
        systemIntroduction_textbox = (Textbox) this
                .getFellow("systemIntroduction_textbox");
        String content = SystemIntroductionService.readSystemIntroduction(path);
        this.systemIntroduction_textbox.setValue(content);
    }

    public void save()
    {
        try
        {
            String content = this.systemIntroduction_textbox.getText();
            SystemIntroductionService.writeSystemIntroduction(content, path);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
