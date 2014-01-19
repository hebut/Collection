package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IWelcomeManager;
import com.uniwin.webkey.core.model.Welcome;
import com.uniwin.webkey.system.ui.WelcomeWin;

public class WelcomeaddListWin extends Window
{
    private IWelcomeManager welcomeManager = (IWelcomeManager) SpringUtil
                                                   .getBean("welcomeManager");

    private List<Welcome>   welcomeList;

    public WelcomeaddListWin()
    {
        try
        {
            welcomeList = welcomeManager.getAllWelcome();
            List<Welcome> listTemp = ((WelcomeWin) Sessions.getCurrent()
                    .getAttribute("WelcomeWin")).getNewWelcomeList();
            for (int i = 0; i < welcomeList.size(); i++)
            {
                Welcome w = (Welcome) welcomeList.get(i);
                for (Welcome wt : listTemp)
                {
                    if (w.getId() == wt.getId())
                    {
                        welcomeList.remove(w);
                        i--;
                    }
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Welcome> getWelcomeList()
    {
        return welcomeList;
    }

    public void setWelcomeList(List<Welcome> welcomeList)
    {
        this.welcomeList = welcomeList;
    }

    public void addWelcomeApp(Event event)
    {
        Welcome welcome = (Welcome) ((Listitem) event.getTarget().getParent()
                .getParent()).getValue();
        ((WelcomeWin) this.getDesktop().getAttribute("WelcomeWin"))
                .reAddwelcomepage(welcome);
        ;
        this.getFellow("toolbar" + welcome.getId()).setVisible(false);
        this.getFellow("div" + welcome.getId()).setVisible(true);
    }

}
