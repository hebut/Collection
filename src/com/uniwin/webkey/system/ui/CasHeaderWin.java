package com.uniwin.webkey.system.ui;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class CasHeaderWin extends Window implements AfterCompose
{
    private Label         userInfo_label, roleInfo_label;

    private Image         img_loginOff;

    private Div           simeMap_div;

    private Menupopup     menupopup;

    private IUsersManager usersManager = (IUsersManager) SpringUtil
                                               .getBean("usersManager");

    public void afterCompose()
    {
        img_loginOff = (Image) this.getFellow("img_loginOff");
        roleInfo_label = (Label) this.getFellow("roleInfo_label");
        simeMap_div = (Div) this.getFellow("simeMap_div");
        userInfo_label = (Label) this.getFellow("userInfo_label");
        menupopup = (Menupopup) this.getFellow("menupopup");
        this.addNewNavigation();
        this.getDesktop().setAttribute("HeaderWin", this);
        showUserInfo();
    }

    public void showUserInfo()
    {
        User user = FrameCommonDate.getUser();
        this.userInfo_label.setValue("欢迎你：" + user.getName());
        this.roleInfo_label.setValue("角色：" + user.getRoleName());
    }

	/**
	 * 添加新的导航工具
	 */
    public void addNewNavigation()
    {
        List<Resource> resouceList = FrameCommonDate.getFavorateResourceList();
        Image navitaionIm = null;
        try
        {
            List list = simeMap_div.getChildren();
            for (int i = 0; i < list.size(); i++)
            {
                Component com = (Component) list.get(i);
                simeMap_div.removeChild(com);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        int i = 0;
        for (final Resource resource : resouceList)
        {
            i++;
            navitaionIm = new Image();
            String imgId = navitaionIm.getUuid();
            Label lbl = new Label();
            lbl.setAction("onMouseOver:lblPassImg(this,'" + imgId + "')");
            lbl.setValue(resource.getName());
            lbl.setStyle("cursor:hand;font-style:normal;color:#113F95;");
            lbl.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    ((WorkbenchWin) Executions.getCurrent().getDesktop()
                            .getAttribute("WorkbenchWin")).addTab(resource);

                }
            });
            navitaionIm.setHover(resource.getActiveImageurl());
            navitaionIm.setSrc(resource.getInactiveImageurl());
            navitaionIm.setHeight("16px");
            navitaionIm.setWidth("16px");// toolBar
            navitaionIm.setSclass("toolBar");
            navitaionIm.addEventListener("onClick", new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    ((WorkbenchWin) Executions.getCurrent().getDesktop()
                            .getAttribute("WorkbenchWin")).addTab(resource);
                }
            });
            Space space = new Space();
            simeMap_div.appendChild(navitaionIm);
            simeMap_div.appendChild(lbl);
            simeMap_div.appendChild(space);
            if (resouceList.size() != 1 && i != resouceList.size())
            {
                Separator separator = new Separator();
                separator.setBar(true);
                separator.setOrient("vertical");
                simeMap_div.appendChild(separator);
            }
        }
    }

    public void showUsersInfo()
    {
        Window usersWindow = (Window) Executions.createComponents(
                "/apps/core/users.zul", this, null);
        usersWindow.setClosable(true);
        try
        {
            usersWindow.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void showUsersUpdatePassword()
    {

        Window usersWindow = (Window) Executions.createComponents(
                "/apps/core/usersUpdatePassword.zul", this, null);
        usersWindow.setClosable(true);
        try
        {
            usersWindow.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loginOff()
    {
        try
        {
            int isOk = Messagebox.show("你确定要注销吗?",
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }
            FrameCommonDate.removeUser();

            Executions.sendRedirect("http://192.168.1.249:8080/cas/logout");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showFavorite()
    {
        Window favWindow = (Window) Executions.createComponents(
                "/apps/core/favorite.zul", this, null);
        favWindow.setClosable(true);
        try
        {
            favWindow.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void changeSkin(Event event)
    {
        try
        {
            int userId = ((User) Sessions.getCurrent()
                    .getAttribute("rbac_user")).getUserId();
            String skinName = ((Menuitem) event.getTarget()).getValue();
            usersManager.saveUserSkin(skinName, userId);
            Executions.getCurrent().getDesktop().getSession().setAttribute(
                    "themeName", skinName);
            Executions.sendRedirect("/admin/index.zul");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showSkin()
    {
        this.menupopup.open(this.userInfo_label);
    }

    public void test()
    {
        Events.postEvent("onClick", img_loginOff, null);
    }
}
