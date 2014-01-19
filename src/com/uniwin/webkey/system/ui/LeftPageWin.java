package com.uniwin.webkey.system.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.tree.ui.LeftPageTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class LeftPageWin extends Window implements AfterCompose
{
    private IResourceManager resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");

    private Tabs             firstMenu;

    private Tabpanels        firstTree;

    private LeftPageTree     myTree;

    public LeftPageWin()
    {

    }

    public void afterCompose()
    {
        initTree();
    }

    public void initTree()
    {
        try
        {
            firstMenu = (Tabs) this.getFellow("firstMenu");
            firstTree = (Tabpanels) this.getFellow("firstTree");
            List<Menu> menus = FrameCommonDate
                    .getFirstMenu("ws_"+((WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult")).getKwId());
            List<Resource> rsList = new ArrayList<Resource>();
            rsList = (List)Sessions.getCurrent().getAttribute("rsList");
            if (rsList == null)
            {
                return;
            }
            if (menus==null)
            {
                return;
            }
            Tab tab = null;
            Tabpanel tabpanel = null;
            for (Menu menu : menus)
            {
                tab = new Tab();
                tab.setLabel(menu.getName());
                tab.setAction("onClick:passImg(this)");
                tab.setAttribute("menu", menu);
                tab.setImage(menu.getInactiveImageurl());
                tab.setHoverImage(menu.getActiveImageurl());
                tab.addEventListener("onClose", new EventListener()
                {

                    public void onEvent(Event arg0) throws Exception
                    {
                        arg0.stopPropagation();
                    }
                });
                if (menu.getPageUrl() != null
                        && !menu.getPageUrl().trim().equals(""))
                {
                    tab.addEventListener("onClick", new EventListener()
                    {
//                        public void onEvent(Event arg0) throws Exception
//                        {
//                            Tab tab = (Tab) arg0.getTarget();
//                            Menu m = (Menu)tab.getAttribute("menu");
//                            Resource r = new Resource();
//                            Executions.getCurrent().getDesktop().getSession()
//                                    .setAttribute("currResource", m);
//                            ((WorkbenchWin) Executions.getCurrent()
//                                    .getDesktop().getAttribute("WorkbenchWin"))
//                                    .addTab(m);
//                        }
                        public void onEvent(Event arg0) throws Exception
                        {
                            Tab tab = (Tab) arg0.getTarget();
                            Menu m = (Menu) tab.getAttribute("menu");
                            Resource r = new Resource();
                            r.setId(m.getId());
                            r.setActiveImageurl(m.getActiveImageurl());
                            r.setInactiveImageurl(m.getInactiveImageurl());
                            r.setName(m.getName());
                            r.setUrl(m.getPageUrl());
                            Resource resource = resourceManager.get(r.getId());
                            r.setResourceType(resource.getResourceType());
                            Executions.getCurrent().getDesktop().getSession()
                                    .setAttribute("currResource", r);
                            ((WorkbenchWin) Executions.getCurrent()
                                    .getDesktop().getAttribute("WorkbenchWin"))
                                    .addTab(r);
                        }
                    });
                }
                if (FrameCommonDate.getTree(menu.getId(),rsList) != null)
                {
                    firstMenu.appendChild(tab);
                    tabpanel = new Tabpanel();
                    tabpanel.setStyle("border:0");
                    tabpanel.appendChild(FrameCommonDate.getTree(menu.getId(),rsList));
                    firstTree.appendChild(tabpanel);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
