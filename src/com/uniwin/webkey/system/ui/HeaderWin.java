package com.uniwin.webkey.system.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.uniwin.framework.filter.DemoServelet;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.cms.prepdata.WKT_DOCLIST;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.content.website.win.CurrentWebsiteWindow;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.ui.PersonalEditWin;
import com.uniwin.webkey.core.ui.UsersUpdatePasswordWin;
import com.uniwin.webkey.core.util.OperationLogUtil;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.skin.ThemeSkinProvider;
import com.uniwin.webkey.tree.ui.LeftPageTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;

import edu.emory.mathcs.backport.java.util.Collections;

public class HeaderWin extends Window implements AfterCompose
{
	private Label            userInfo_label;
	
    private Menupopup        role_menuitem;

    private Image            img_loginOff;

    private Combobox         hdSel;

    private Div              simeMap_div;

    private Menupopup        menupopup;

    private IUsersManager    usersManager = (IUsersManager) SpringUtil
                                                  .getBean("usersManager");

    private IAuthManager     authManager  = (IAuthManager) SpringUtil
                                                  .getBean("authManager");

    private IResourceManager resourceManager;                              // 资源管理服务接口

    private Session          curSession   = Sessions.getCurrent();
    
    private NewsServices   newsService=(NewsServices)org.zkoss.spring.SpringUtil.getBean("info_newsService");
    
    
    public HeaderWin()
    {
        try
        {
            SCADomain scaDomain = null;
            scaDomain = SCADomain.connect("http://localhost/rbac");
            usersManager = (IUsersManager) scaDomain.getService(IUsersManager.class, "User");
            resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void afterCompose()
    {
    	
        role_menuitem = (Menupopup) this.getFellow("role_menuitem");
//        img_loginOff = (Image) this.getFellow("img_loginOff");
        simeMap_div = (Div) this.getFellow("simeMap_div");
        userInfo_label = (Label) this.getFellow("userInfo_label");
        menupopup = (Menupopup) this.getFellow("menupopup");
        hdSel = (Combobox)this.getFellow("hdSel");
        List wsList = (List)curSession.getAttribute("wsList");
        initWs(wsList);
        ThemeSkinProvider tsp = new ThemeSkinProvider();
        Map map = tsp.readskin();
        Set set = map.keySet();
        Iterator it = set.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Menuitem menuitem = new Menuitem();
            menuitem.setLabel((String) map.get(key));
            menuitem.setValue(key);
            menuitem.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    HeaderWin.this.changeSkin(arg0);
                }

            });
            menupopup.appendChild(menuitem);
        }
        this.addNewNavigation();
        this.getDesktop().setAttribute("HeaderWin", this);
        showUserInfo();
        // List<Resource> domainList = (List<Resource>) Sessions.getCurrent()
        // .getAttribute("domain_list");
        // for (Resource resourceTemp : (List<Resource>) domainList)
        // {
        // Comboitem item = new Comboitem();
        // item.setValue(resourceTemp);
        // item.setLabel(resourceTemp.getName());
        // domain.appendChild(item);
        // }
        // if (domainList != null && domainList.size() != 0)
        // {
        // domain.setText(((Resource) Sessions.getCurrent().getAttribute(
        // "domain_defult")).getName());
        // }
    }

    
   /* public void onClick$siteInfo(){
    	
    	System.out.println("--------------");
    	CurrentWebsiteWindow cWin = (CurrentWebsiteWindow)Executions.createComponents
    		("/apps/cms/content/website/CurrentWebsite.zul", null, null);
    	try {
			cWin.doModal();
		} catch (SuspendNotAllowedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }*/
    
    
    
    /**
     * 头部内容显示的登录用户信息
     */
    public void showUserInfo()
    {
        User user = FrameCommonDate.getUser();
        this.userInfo_label.setValue("欢迎你："+user.getName() );
        List<Role> roleList = (List<Role>) curSession.getAttribute("rbac_roleList");
        final Integer[] ids = new Integer[roleList.size()];
        for (int i = 0; i < roleList.size(); i++)
        {
            Role r = roleList.get(i);
            Menuitem menuitem = new Menuitem();
            menuitem.setLabel(r.getName());
            menuitem.setValue(r.getRoleId() + "");
//            menuitem.addEventListener("onClick", new EventListener()
//            {
//
//                public void onEvent(Event arg0) throws Exception
//                {
//                    Menuitem menuitem = (Menuitem) arg0.getTarget();
//                    int roleId = Integer.parseInt(menuitem.getValue());
//                    initMenu(new Integer[] { roleId });
//                    menuitem.setChecked(true);
//                }
//            });
            role_menuitem.appendChild((menuitem));
            ids[i] = r.getRoleId();
        }
//        Menuitem menuitem = new Menuitem();
//        menuitem.setLabel("所有角色");
//        menuitem.addEventListener("onClick", new EventListener()
//        {
//
//            public void onEvent(Event arg0) throws Exception
//            {
//                Menuitem menuitem = (Menuitem) arg0.getTarget();
//                initMenu(ids);
//                menuitem.setChecked(true);
//            }
//        });
//        role_menuitem.appendChild((menuitem));
    }

    /**
     * 加载角色信息
     */
    public void initMenu(Integer[] ids)
    {
        try
        {
            // List<Permission> permissionList = permissionManager
            // .getPermissionListByRoleIds(ids);
            List<Permission> permissionList = new ArrayList<Permission>();
            curSession.setAttribute("rbac_permissionList",permissionList);
            /* 将所有许可一个map方式再保存一边，方便在页面中查找 */
            Integer[] permissionIds = new Integer[permissionList.size()];
            int j = 0;
            Map permissionMap = new HashMap();
            Permission permission = null;
            for (Integer permissionId : permissionIds)
            {
                permission = permissionList.get(j);
                permissionIds[j] = permission.getKpid();
                permissionMap.put(permissionIds[j].intValue() + "", permission);
                j++;
            }
            curSession.setAttribute("rbac_permissionMap",permissionMap);
            //
            List<Resource> resList = resourceManager.getResourceListByPermissionIds(permissionIds);
            curSession.setAttribute("rbac_resourceList", resList);
            /* 根据许可id获取所有的资源信息，并保存到session中 */
            List<Resource> resourceList = resourceManager.getResourceListByPermissionIds(permissionIds);
            curSession.setAttribute("rbac_resourceTreeList", resList);
            /* 根据一级资源名称来生成对应的树，保存到map中便于以后使用直接获取树 */
            Map treeMap = new HashMap();
            Menu menu = null;
            // List<Resource> domainList = new ArrayList<Resource>();
            // for (Resource resource : resList)
            // {
            // if (resource.getResourceType().equals("03"))
            // {
            // domainList.add(resource);
            // }
            // }
            // if (domainList == null || domainList.size() == 0)
            // {
            // Sessions.getCurrent().setAttribute("domain_defult",new
            // Resource());
            // } else
            // {
            // Sessions.getCurrent().setAttribute("domain_defult",((Resource)
            // domainList.get(0)));
            // }
            // Sessions.getCurrent().setAttribute("domain_list", domainList);

            for (Resource resourceTop : resourceList)
            {
                List<Menu> menus = new ArrayList<Menu>();
                if (resourceTop.getResourceType().equals("03"))
                {
                    for (Resource resource : resourceList)
                    {
                        if (resource.getParentId().equals(resourceTop.getId()))
                        {
                            LeftPageTree tree = new LeftPageTree();
                            LeftPageContentProvider contentProvider = new LeftPageContentProvider(resource);
                            tree = new LeftPageTree();
                            menu = new Menu();
                            menus.add(menu);
                            menu.setId(resource.getId());
                            menu.setWeizhi(resource.getWeizhi());
                            menu.setName(resource.getName());
                            menu.setInactiveImageurl(resource.getInactiveImageurl());
                            menu.setActiveImageurl(resource.getActiveImageurl());
                            menu.setPageUrl(resource.getUrl());
                            tree.setTreeitemlContentProvider(contentProvider);
                            tree.rebuildTree();
                            // treeMap.put(resource.getId().intValue() + "",
                            // tree);
                        }
                    }
                    Collections.sort(menus, new Comparator<Menu>()
                    {

                        public int compare(Menu o1, Menu o2)
                        {
                            return o1.getWeizhi().compareTo(o2.getWeizhi());
                        }
                    });
                    curSession.setAttribute(resourceTop.getDomain(),menus);
                }

            }
            Executions.sendRedirect("/admin/index.zul");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 添加新的导航工具
     */
    public void addNewNavigation()
    {
        List<Resource> resouceList = FrameCommonDate.getFavorateResourceList();
        List<Resource> resources = FrameCommonDate.getResourceList();
        if (resouceList == null)
        {
            return;
        }
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
            boolean isHasPermission = false;
            for (Resource resourcePermission : resources)
            {
                if (resourcePermission.getId().intValue() == resource.getId()
                        .intValue())
                {
                    isHasPermission = true;
                    break;
                }
            }
            if (isHasPermission)
            {
                navitaionIm = new Image();
                String imgId = navitaionIm.getUuid();
                Label lbl = new Label();
                lbl.setAction("onMouseOver:lblPassImg(this,'" + imgId + "')");
                lbl.setValue(resource.getName());
                lbl.setStyle("cursor:hand;font-style:normal;color:#034497;");
                lbl.addEventListener("onClick", new EventListener()
                {

                    public void onEvent(Event arg0) throws Exception
                    {
                        Executions.getCurrent().getDesktop().getSession()
                                .setAttribute("currResource", resource);
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
                        Executions.getCurrent().getDesktop().getSession()
                                .setAttribute("currResource", resource);
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
    }

    public void showUsersInfo()
    {
       /* Window usersWindow = (Window) Executions.createComponents(
                "/apps/core/users.zul", null, null);
        usersWindow.setClosable(true);
        try
        {
            usersWindow.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }*/
    	
    	PersonalEditWin per = (PersonalEditWin) Executions.createComponents(
				"/apps/core/personalEdit.zul", null, null);
		per.setClosable(true);
        try {
			per.doHighlighted();
		} catch (SuspendNotAllowedException e) {
			e.printStackTrace();
		}

    }

    public void showUsersUpdatePassword()
    {

    	UsersUpdatePasswordWin usersWindow = (UsersUpdatePasswordWin) Executions.createComponents(
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

    /**
     * 退出登录
     */
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
            User user = FrameCommonDate.getUser();
            OperationLogUtil.addLog(org.zkoss.util.resource.Labels
                    .getLabel("wkthelp.ui.user")
                    + user.getName() + "退出登录", user.getUserId() + "");
            FrameCommonDate.removeUser();
            Sessions.getCurrent().invalidate();
            
            Executions.sendRedirect("/apps/core/login.zul");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 重新定义要显示的菜单
     * @throws FileNotFoundException 
     */
    public void showFavorite() throws FileNotFoundException
    {
//        Window favWindow = (Window) Executions.createComponents(
//                "/apps/core/favorite.zul", this, null);
//        favWindow.setClosable(true);
//        try
//        {
//            favWindow.doModal();
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    	 String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/css");
		  Filedownload.save(new File(path+"\\"+"help.doc"), null);
    }

    /**
     * 重新加载选中的皮肤
     */
    public void changeSkin(Event event)
    {
        try
        {
            int userId = ((User) curSession.getAttribute("rbac_user")).getUserId();
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

    public void passDomain()
    {
        // Sessions.getCurrent().setAttribute("domain_defult",
        // domain.getSelectedItem().getValue());
        List<WkTWebsite> wsList = new ArrayList<WkTWebsite>();
        Users users = (Users) curSession.getAttribute("users");
        WkTWebsite website = (WkTWebsite) hdSel.getSelectedItem().getValue();
        List<Resource> rsList = new ArrayList();
        try
        {
            rsList = authManager.getResourceByUserIdAndWebsite(users
                    .getUserId(), website.getKwId().intValue());
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        }
        List<Menu> menus = new ArrayList<Menu>();
        for (Resource resourceTop : rsList)
        {
            if (resourceTop.getParentId().equals(0))
            {
                Menu menuTop = new Menu();
                menuTop.setId(resourceTop.getId());
                menuTop.setWeizhi(resourceTop.getWeizhi());
                menuTop.setName(resourceTop.getName());
                menuTop.setInactiveImageurl(resourceTop.getInactiveImageurl());
                menuTop.setActiveImageurl(resourceTop.getActiveImageurl());
                menuTop.setPageUrl(resourceTop.getUrl());
                menus.add(menuTop);
                Collections.sort(menus, new Comparator<Menu>()
                {
                    public int compare(Menu o1, Menu o2)
                    {
                        return o1.getWeizhi().compareTo(o2.getWeizhi());
                    }
                });
            }
        }
        curSession.setAttribute("ws_"+website.getKwId(),menus);
        curSession.setAttribute("domain_defult",hdSel.getSelectedItem().getValue());
        curSession.setAttribute("rsList",rsList);
        Executions.sendRedirect("/admin/index.zul");
    }

    /**
     * 连接到帮助文档
     */
    public void connectHelp()
    {

        WorkbenchWin workbeachWin = (WorkbenchWin) Executions.getCurrent()
                .getDesktop().getAttribute("WorkbenchWin");
        Tab tab = workbeachWin.getSelectedTab();
        Resource resource = (Resource) tab.getAttribute("resource");
        Executions.getCurrent().getDesktop().getSession().setAttribute(
                "resourceforhelp", resource);
        try
        {
            if (resource == null)
            {
                Messagebox.show("对不起，当前选中页没有找到对应的资源！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            } else
            {
                if (resource.getHelpUrl() == null
                        || resource.getHelpUrl().equals(""))
                {
                    Messagebox.show("对不起，当前资源还没有建立帮助文档！", "提示信息",
                            Messagebox.OK, Messagebox.INFORMATION);
                    return;
                }
            }
        } catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }

        Resource resourceSearch = new Resource();
        resourceSearch.setResourceType("02");
        resourceSearch.setUrl(resource.getHelpUrl());

        try
        {
            List list = resourceManager.getList(resourceSearch);
            if (list != null && list.size() > 0)
            {
                Resource currResource = (Resource) list.get(0);
                Executions.getCurrent().getDesktop().getSession().setAttribute(
                        "currResource", currResource);
            }
        } catch (DataAccessException e1)
        {
            e1.printStackTrace();
        }
        boolean isOpen = false;
        Tabs tabs = workbeachWin.getTabs();

        List<Tab> tabList = (List<Tab>) tabs.getChildren();

        Tabpanels tabpanels = workbeachWin.getTabpanels();
        List<Tabpanel> panelsList = (List<Tabpanel>) tabpanels.getChildren();
        for (int i = 0; i < panelsList.size(); i++)
        {
            Tabpanel panel = panelsList.get(i);
            List windowList = panel.getChildren();
            for (int j = 0; j < windowList.size(); j++)
            {
                if (windowList.get(j) instanceof Iframe)
                {
                    try
                    {
                        Iframe iframe = new Iframe();
                        iframe = (Iframe) windowList.get(j);
                        String url = "";
                        String a = "";
                        String frontUrl = resource.getHelpUrl();
                        if (resource.getHelpUrl().indexOf("#") != -1
                                && resource.getHelpUrl().indexOf("#") != resource
                                        .getHelpUrl().length() - 1)
                        {
                            a = resource.getHelpUrl().substring(
                                    resource.getHelpUrl().lastIndexOf("#"));
                            frontUrl = resource.getHelpUrl().substring(0,
                                    resource.getHelpUrl().lastIndexOf("#"));
                        }
                        url = frontUrl + "?header=true&radom="
                                + new Date().toLocaleString() + a;
                        iframe.setSrc(url);
                        isOpen = true;
                        Tab tab1 = tabList.get(i);
                        tab1.setSelected(true);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
        if (!isOpen)
        {
            int resourceId = 0;
            try
            {
                List resourceList = resourceManager.getResourceByType("02");
                if (resourceList != null && resourceList.size() > 0)
                {
                    resourceId = ((Resource) resourceList.get(0)).getId();
                }
            } catch (DataAccessException e)
            {
                e.printStackTrace();
            }
            Tab tab1 = new Tab();
            // if (Sessions.getCurrent().getAttribute("themeName").toString()
            // .equals("defult"))
            // {
            // }
            tab1.setClosable(true);
            tab1.setSelected(true);
            tab1.setId(resourceId + "");
            tab1.setLabel(resource.getName());
            workbeachWin.checkPermission(resource);
            tab1.setAttribute("uri", resource.getUrl());
            workbeachWin.getTabs().appendChild(tab1);
            Tabpanel tabpanel = new Tabpanel();
            tab1.setLabel("系统帮助");
            Iframe iframe = new Iframe();
            iframe.setWidth("100%");
            iframe.setHeight("100%");
            String a = "";
            String frontUrl = "";
            if (resource.getHelpUrl().indexOf("#") != -1
                    && resource.getHelpUrl().indexOf("#") != resource
                            .getHelpUrl().length() - 1)
            {
                a = resource.getHelpUrl().substring(
                        resource.getHelpUrl().lastIndexOf("#"));
                frontUrl = resource.getHelpUrl().substring(0,
                        resource.getHelpUrl().lastIndexOf("#"));
            }
            String url = frontUrl + "?header=true&radom=" + new Date().toLocaleString() + a;
            iframe.setSrc(url);
            tabpanel.appendChild(iframe);
            tabpanel.setId(tabpanel + "_window");
            workbeachWin.getTabpanels().appendChild(tabpanel);
        }

    }

    /**
     * 打开添加反馈内容的页面
     */
    public void showAddMessage()
    {
        WorkbenchWin workbeachWin = (WorkbenchWin) Executions.getCurrent()
                .getDesktop().getAttribute("WorkbenchWin");
        Tab tab = workbeachWin.getSelectedTab();
        Resource resource = (Resource) tab.getAttribute("resource");
        try
        {
            if (resource == null)
            {
                Messagebox.show("对不起，当前选中页没有找到对应的资源！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }
        } catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        Executions.getCurrent().getDesktop().getSession().setAttribute(
                "currResource", resource);
        Window window = (Window) Executions.getCurrent().createComponents(
                "/apps/core/wktmessagesAdd.zul", this, null);
        try
        {
            window.setClosable(true);
            window.doModal();
        } catch (SuspendNotAllowedException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 跳转到首页 
     */
    public void showIndex()
    {
    	
    	/* 自动发布转化新闻的html文件  */
        List autoList=newsService.findByAutoId(1);
        if(autoList!=null){
        	WkTDistribute distribute;
        	WkTInfo info;
        	for(int a=0;a<autoList.size();a++){
        		distribute=(WkTDistribute)autoList.get(a);
        		info=newsService.getWkTInfo(distribute.getKiId());
        		//发布
        		Map root = new HashMap();
			 	Sessions.getCurrent().setAttribute("root", root);
			 	WKT_DOCLIST dList = new WKT_DOCLIST();
			 	dList.singleNewsPublic(info, distribute.getKcId());
             }
        }
        Executions.getCurrent().sendRedirect("/admin/index.html","_blank");
    }

    void initWs(List wsList)
    {
        Iterator it = wsList.iterator();
        if (null != hdSel)
        {
            hdSel.getChildren().clear();
            while (it.hasNext())
            {
                WkTWebsite website = new WkTWebsite();
                website = (WkTWebsite) it.next();
                // int wsid = Integer.parseInt(it.next().toString());
                // website = websiteService.findBykwid(Long.parseLong(wsid+""));
                if (website.getkwStatus().equals("0"))
                {
                    Comboitem item = new Comboitem();
                    item.setValue(website);
                    item.setLabel(website.getkwName());
                    hdSel.appendChild(item);
                    hdSel.setText(((WkTWebsite)curSession.getAttribute("domain_defult")).getkwName());
                }
            }
        }
    }
    
}
