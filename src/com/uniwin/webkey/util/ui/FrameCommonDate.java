package com.uniwin.webkey.util.ui;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.system.ui.LeftPageContentProvider;
import com.uniwin.webkey.tree.ui.LeftPageTree;

public class FrameCommonDate
{
    private static Map              noCheckUrl = null;

    private static IResourceManager resourceManager;

    private static String           context;

    public FrameCommonDate()
    {

    }

    public static String getWebContext()
    {
        if (context == null)
        {
            context = Executions.getCurrent().getContextPath();
        }
        return context;
    }

    public IResourceManager getResourceManager()
    {
        SCADomain scaDomain = null;
        scaDomain = SCADomain.connect("http://localhost/rbac");
        resourceManager = (IResourceManager) scaDomain.getService(
                IResourceManager.class, "Resource");
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public static Object getBean(String beanId)
    {
        return SpringUtil.getBean(beanId);
    }

    public static User getUser()
    {
        return (User) Sessions.getCurrent().getAttribute("rbac_user");
    }

    public static List<Menu> getFirstMenu(String domain)
    {// domain_resource
        return (List<Menu>) Sessions.getCurrent().getAttribute(domain);
    }

    public static LeftPageTree getTree(Integer firstMenuId,List<Resource> lResource)
    {
        SCADomain scaDomain = null;
        scaDomain = SCADomain.connect("http://localhost/rbac");
        resourceManager = (IResourceManager) scaDomain.getService(
                IResourceManager.class, "Resource");
        List<Resource> resourceList = getResourceList();
        int resourceId = 0;
        for (Resource resource : lResource)
        {
            resourceId = resource.getId().intValue();
            if (resourceId == firstMenuId.intValue())
            {
                LeftPageTree tree = new LeftPageTree();
                LeftPageContentProvider contentProvider = new LeftPageContentProvider(
                        resource);
                tree = new LeftPageTree();
                tree.setTreeitemlContentProvider(contentProvider);
                tree.rebuildTree();
                return tree;
            }
        }
        return null;
    }

    public static final List<Permission> getPermissions()
    {

        return null;
    }

    public static boolean hasPermission(int permissionId)
    {
        Map map = (Map) Sessions.getCurrent().getAttribute("selectedNavgation");
        return map.containsKey(permissionId + "");
    }

    public static List<Resource> getResourceList()
    {
        return (List<Resource>) Sessions.getCurrent().getAttribute(
                "rsList");
    }

    public static void removeUser()
    {
        Sessions.getCurrent().removeAttribute("rbac_user");
        Sessions.getCurrent().removeAttribute("rbac_roleList");
        // Sessions.getCurrent().invalidate();
    }

    public static List<Resource> getFavorateResourceList()
    {
        return (List<Resource>) Sessions.getCurrent().getAttribute(
                "rbac_FavorateResourceList");
    }

   /* public static List<AlarmclockEx> getThreeAlarmclockExs()
    {
        return (List<AlarmclockEx>) Sessions.getCurrent().getAttribute(
                "rbac_alarmclockExs");
    }*/

    public static void openTabByResourceId(int resourceId)
    {
        Treerow treeRow = (Treerow) ((Map) Sessions.getCurrent().getAttribute(
                "rbac_treeRow")).get("treerow" + resourceId);
        try
        {

            Events.postEvent("onClick", treeRow, null);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // public static boolean isAccessed(String resourceUrl) {
    // Map noCheckUrl = new HashMap();
    // try {
    // List<Resource> resourceList = (List<Resource>) resourceManager
    // .getResourceNocheckList();
    // for (Resource resource : resourceList) {
    // noCheckUrl.put(resource.getUrl().trim(), 1);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // boolean isOk = false;
    // if (resourceUrl != null) {
    // isOk = noCheckUrl.containsKey(resourceUrl.trim());
    // }
    // return isOk;
    // }

    // public static List<Welcome> getWelcomepages(){
    // return
    // (List<Welcome>)Sessions.getCurrent().getAttribute("framework_welcomePages");
    // }
    public static Resource getResourceByResourceName(String resourceName)
    {

        List<Resource> resourceList = getResourceList();
        for (Resource resource : resourceList)
        {
            if (resource.getName().equals(resourceName))
            {
                return resource;
            }
        }
        return null;
    }

}
