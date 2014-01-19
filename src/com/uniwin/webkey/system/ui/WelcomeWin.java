package com.uniwin.webkey.system.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.itf.IWelcomeManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Welcome;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class WelcomeWin extends Window implements AfterCompose
{
    private Rows            welcomeRows;

    private Portallayout    portalContent;

    private Portalchildren  leftColumPortchildren;

    private Portalchildren  rightColumPortchildren, centerPortchildren;

    private List<Panel>     panelList;

    private IWelcomeManager welcomeManager = (IWelcomeManager) SpringUtil
                                                   .getBean("welcomeManager");

    private IUsersManager   userManager;

    private Div             workbenchSeting;

    public WelcomeWin()
    {
        SCADomain scaDomain = null;
        scaDomain = SCADomain.connect("http://localhost/rbac");
        userManager = (IUsersManager) scaDomain.getService(IUsersManager.class,
                "User");
    }

    public void afterCompose()
    {
        panelList = new ArrayList<Panel>();
        leftColumPortchildren = (Portalchildren) this
                .getFellow("leftColumPortchildren");
        centerPortchildren = (Portalchildren) this
                .getFellow("centerPortchildren");
        rightColumPortchildren = (Portalchildren) this
                .getFellow("rightColumPortchildren");
        workbenchSeting = (Div) this.getFellow("workbenchSeting");
        portalContent = (Portallayout) this.getFellow("portalContent");
        this.getDesktop().setAttribute("WelcomeWin", this);
        Sessions.getCurrent().setAttribute("WelcomeWin", this);
        loadWelcomeList();
        workbenchSeting.setVisible(true);
    }

    /**
     * 添加新的welcome元素
     * 
     * @param welcome
     */
    public void reAddwelcomepage(Welcome welcome)
    {
        try
        {
            int leftchildrenSize = leftColumPortchildren.getChildren().size();
            int rightchildrenSize = rightColumPortchildren.getChildren().size();
            int centerchildrenSize = this.centerPortchildren.getChildren()
                    .size();
            Portalchildren temp = null;
            if (leftchildrenSize == rightchildrenSize
                    && centerchildrenSize == rightchildrenSize)
            {
                temp = leftColumPortchildren;
            } else if (leftchildrenSize > rightchildrenSize
                    && centerchildrenSize > rightchildrenSize)
            {
                temp = rightColumPortchildren;
            } else
            {
                temp = centerPortchildren;
            }
            this.addPanel(welcome, temp);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 分别查找每个栏位的Welcome元素
     * 
     * @return welcome的集合
     */
    public List<Welcome> getNewWelcomeList()
    {
        List<Welcome> tempList = new ArrayList<Welcome>();
        getPortchildrenWelcome(0, tempList, this.leftColumPortchildren);
        getPortchildrenWelcome(1, tempList, this.centerPortchildren);
        getPortchildrenWelcome(2, tempList, this.rightColumPortchildren);
        return tempList;
    }

    /**
     * 查找每个Portalchildren中的Welcome元素
     * 
     * @param colnum
     *            要存储到数据列的编号
     * @param list
     *            放置Welcome元素
     * @param por
     *            要查找的Portalchildren
     * @return welcome的集合
     */
    public List<Welcome> getPortchildrenWelcome(int colnum, List<Welcome> list,
            Portalchildren por)
    {
        List listLeft = por.getChildren();
        for (int i = 0; i < listLeft.size(); i++)
        {
            Object obj = listLeft.get(i);
            if (obj instanceof Panel)
            {
                obj = (Panel) obj;
                Welcome welcome = (Welcome) ((Panel) obj)
                        .getAttribute("welcome");
                welcome.setColnum(colnum);
                welcome.setRownum(i);
                list.add(welcome);
            }
        }
        return list;
    }

    /**
     * 加载welcome元素
     */
    public void loadWelcomeList()
    {
        try
        {
            List<Welcome> welcomeList = userManager
                    .getWelcomeByUserId(FrameCommonDate.getUser().getUserId());
            for (Welcome welcome : welcomeList)
            {
                if (welcome.getColnum() == 0)
                {
                    this.addPanelOrder(welcome, this.leftColumPortchildren);
                } else if (welcome.getColnum() == 1)
                {
                    this.addPanelOrder(welcome, this.centerPortchildren);
                } else
                {
                    this.addPanelOrder(welcome, this.rightColumPortchildren);
                }
            }

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 按照一定序列添加在指定位置
     * 
     * @param welcome
     * @param portalchildren
     */
    public void addPanelOrder(Welcome welcome, Portalchildren portalchildren)
    {
        Panel panel = new Panel();
        Panelchildren panelchildren = new Panelchildren();
        panel.setSclass("temppanel");
        Iframe iframe = new Iframe();
        iframe.setZclass("border:0 none;");
        panel.appendChild(panelchildren);
        panel.setTitle(welcome.getName());
        panel.setAttribute("welcome", welcome);
        panel.setWidth("99%");
        panel.setHeight("99%");
        panel.setClosable(welcome.getVisible() == 0);
        panel.setBorder("normal");
        panel.setCollapsible(true);
        panel.setMaximizable(true);
        panelchildren.appendChild(iframe);
        iframe.setWidth("100%");
        iframe.setHeight("100%");
        iframe.setSrc(welcome.getUrl());

        if (welcome.getUrl().startsWith("/apps/core/wkthelp.zul"))
        {
            Resource resourceSearch = new Resource();
            resourceSearch.setResourceType("02");
            resourceSearch.setUrl(welcome.getUrl());
            try
            {
                IResourceManager resourceManager;
                resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");
                List list = resourceManager.getList(resourceSearch);
                if (list != null && list.size() > 0)
                {
                    Resource currResource = (Resource) list.get(0);
                    Executions.getCurrent().getDesktop().getSession()
                            .setAttribute("currResource", currResource);
                }
            } catch (DataAccessException e1)
            {
                e1.printStackTrace();
            }
        }
        portalchildren.appendChild(panel);
        this.panelList.add(panel);

    }

    public void addPanel(Welcome welcome, Portalchildren portalchildren)
    {
        Panel panel = new Panel();
        panel.setSclass("temppanel");
        Panelchildren panelchildren = new Panelchildren();
        Iframe iframe = new Iframe();
        iframe.setZclass("border:0 none;");
        panel.appendChild(panelchildren);
        panel.setTitle(welcome.getName());
        panel.setAttribute("welcome", welcome);
        panel.setWidth("99%");
        panel.setHeight("99%");
        panel.setClosable(welcome.getVisible() == 0);
        panel.setBorder("normal");
        panel.setCollapsible(true);
        panel.setMaximizable(true);
        panelchildren.appendChild(iframe);
        iframe.setWidth("100%");
        iframe.setHeight("100%");
        iframe.setSrc(welcome.getUrl());
        portalchildren.appendChild(panel);
        this.panelList.add(panel);

    }

    public void saveWelcomePageCustomer()
    {
        try
        {
            userManager.addUserWelcome(getNewWelcomeList(), FrameCommonDate
                    .getUser().getUserId());
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"));
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.savefailed1"));
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public String getMaxHeight()
    {
        String height = "";
        return this.leftColumPortchildren.getHeight();
    }

    public void showWelcomeaddList()
    {
        Window window = (Window) Executions.getCurrent().createComponents(
                "/apps/core/welcomeaddList.zul", this.getParent(),
                new HashMap());
        window.setClosable(true);
        try
        {
            window.setPosition("center");
            window.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
