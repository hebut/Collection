package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.itf.IUsersfavorateManager;
import com.uniwin.webkey.core.itf.IWelcomeManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Usersfavorate;
import com.uniwin.webkey.core.util.OperationLogUtil;
import com.uniwin.webkey.system.ui.LeftPageContentProvider;
import com.uniwin.webkey.tree.ui.LeftPageTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;
import com.uniwin.webkey.util.ui.ResourceContentProvider;

public class LoginCasWin extends Window implements AfterCompose
{
    private Toolbarbutton         passCpa;

    private boolean               needCode             = false;

    private Div                   centerDiv;

    private Captcha               cpa;

    private Label                 loginCode_label;

    private Textbox               userName, loginCode, password;

    boolean                       isInputPassWord      = false;

    private IUsersManager         usersManager         = (IUsersManager) SpringUtil
                                                               .getBean("usersManager");

    private IResourceManager      resourceManager      = (IResourceManager) SpringUtil
                                                               .getBean("resourceManager");

    private IRoleManager          roleManager          = (IRoleManager) SpringUtil
                                                               .getBean("roleManager");

    private IPermissionManager    permissionManager    = (IPermissionManager) SpringUtil
                                                               .getBean("permissionManager");

    private IWelcomeManager       welcomeManager       = (IWelcomeManager) SpringUtil
                                                               .getBean("welcomeManager");

//    private IAlarmclockManager    alarmclockManager    = (IAlarmclockManager) SpringUtil
//                                                               .getBean("alarmclockManager");

    private IUsersfavorateManager usersfavorateManager = (IUsersfavorateManager) SpringUtil
                                                               .getBean("usersfavorateManager");

    private IUserroleManager      userroleManager      = (IUserroleManager) SpringUtil
                                                               .getBean(
                                                                       "userroleManager");

    public LoginCasWin()
    {
        String username = "";
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth.getPrincipal() == null)
        {
        } else if (auth.getPrincipal() instanceof UserDetails)
        {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else
        {
            username = auth.getPrincipal().toString();
        }
        // String username=(String)Sessions.getCurrent().getAttribute("name");
        // HttpServletRequest request = (HttpServletRequest)
        // Executions.getCurrent().getNativeRequest();
        // AttributePrincipal principal =
        // (AttributePrincipal)request.getUserPrincipal();
        // String username = principal.getName();
        loginTosystem(username);
    }

    public void afterCompose()
    {
    }

    /**
     * 登录系统
     * 
     * @param logintoUrl
     */
    public void loginTosystem(String userName)
    {

        boolean isSuccess = true;
        try
        {

            User user = usersManager.getUser4Cas(userName, userName);
            if (user.getName() == null)
            {
                isSuccess = false;
            } else
            {
                Sessions.getCurrent().setAttribute("themeName",
                        user.getSkinName());
                Sessions.getCurrent().setAttribute("rbac_user", user);
            }
            initFrameworkSessionData();
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("login.ui.loginerror"));
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void loginTosystem(String userName, String passworld)
    {

    }

    /**
     * 初始化框架的session数据，将数据按照一定的结构方式保存到内存中，为将来更方便的访问session中的数据做准备。
     * session中的数据都通过FrameCommonData的静态方法暴暴露出来
     */
    public void initFrameworkSessionData()
    {
        // 获得session中的用户信息
        User user = FrameCommonDate.getUser();
        try
        {
            // 获取三个距离发生时间最近的闹钟
//            Sessions.getCurrent().setAttribute(
//                    "rbac_alarmclockExs",
//                    this.alarmclockManager.getThreeAlidAlarmclocksByUserId(user
//                            .getUserId().intValue()));
            // 获取当前用户的导航菜单的资源信息，并保存到session中
            this.setFavorateResourceList(user.getUserId().intValue());

            // 获取当前用户所有角色数，并放到session中.
            List<Role> roleList = userroleManager
                    .getByHQL("select role from Userrole user,Role role where role.roleId=user.roleId and user.userId="
                            + user.getUserId());
            ;
            Sessions.getCurrent().setAttribute("rbac_roleList", roleList);

            /* 获取所有的角色id，根据角色id获取当前用户的所有许可，并将所有的许可数据保存入session中 */
            Integer[] roleIds = new Integer[roleList.size()];
            int i = 0;
            Role role = null;
            for (Integer roleId : roleIds)
            {
                role = roleList.get(i);
                if (role.getState() == 0)
                {
                    roleIds[i] = role.getRoleId();
                } else
                {
                    roleIds[i] = -1990;
                }
                i++;
            }
//            List<Permission> permissionList = permissionManager
//            .getPermissionListByRoleIds(roleIds);
            List<Permission> permissionList = new ArrayList<Permission>();
            Sessions.getCurrent().setAttribute("rbac_permissionList",
                    permissionList);

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
            Sessions.getCurrent().setAttribute("rbac_permissionMap",
                    permissionMap);
            //
            List<Resource> resList = resourceManager
                    .getResourceListByPermissionIds(permissionIds);
            Sessions.getCurrent().setAttribute("rbac_resourceList", resList);
            /* 根据许可id获取所有的资源信息，并保存到session中 */
            List<Resource> resourceList = resourceManager
                    .getResourceListByPermissionIds(permissionIds);
            Sessions.getCurrent()
                    .setAttribute("rbac_resourceTreeList", resList);

            /* 根据一级资源名称来生成对应的树，保存到map中便于以后使用直接获取树 */
            Map treeMap = new HashMap();
            Menu menu = null;

            // Map treeRowMap = new HashMap<String, Treerow>();
            Resource r = new Resource();
            r.setId(0);
            List domainList = new ResourceContentProvider().getChildren(r);
            if (domainList == null || domainList.size() == 0)
            {
                Messagebox.show("您没有权限登录！");
                Executions.sendRedirect("login.zul");
                return;
            }
            Sessions.getCurrent().setAttribute("domain_list", domainList);
            Sessions.getCurrent().setAttribute("domain_defult",((Resource) domainList.get(0)));
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
                            LeftPageContentProvider contentProvider = new LeftPageContentProvider(
                                    resource);
                            tree = new LeftPageTree();
                            menu = new Menu();
                            menus.add(menu);
                            menu.setId(resource.getId());
                            menu.setName(resource.getName());
                            menu.setInactiveImageurl(resource
                                    .getInactiveImageurl());
                            menu
                                    .setActiveImageurl(resource
                                            .getActiveImageurl());
                            menu.setPageUrl(resource.getUrl());
                            tree.setTreeitemlContentProvider(contentProvider);
                            tree.rebuildTree();
                            // treeMap.put(resource.getId().intValue() + "",
                            // tree);
                        }
                    }
                    Sessions.getCurrent().setAttribute(resourceTop.getDomain(),
                            menus);
                }

            }
            // login Log
            OperationLogUtil.addLog(org.zkoss.util.resource.Labels
                    .getLabel("wkthelp.ui.user")
                    + user.getName() + "登录成功", user.getUserId() + "");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 设置session中的FavorateResourceList
     * 
     * @throws DataAccessException
     * @throws ObjectNotExistException
     */
    public void setFavorateResourceList(int userId) throws DataAccessException,
            ObjectNotExistException
    {
        List<Usersfavorate> usersfavorates = usersfavorateManager
                .getUsersfavoratesByUserid(userId);
        String resourceids[] = new String[usersfavorates.size()];
        for (int i = 0; i < usersfavorates.size(); i++)
        {
            resourceids[i] = usersfavorates.get(i).getResourceId().intValue()
                    + "";
        }
        List<Resource> resources = this.resourceManager
                .getResourceListByResourceIds(resourceids);
        Sessions.getCurrent().setAttribute("rbac_FavorateResourceList",
                resources);
    }

}
