package com.uniwin.webkey.core.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.api.West;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.cpr.exception.LicenseCommonException;
import com.uniwin.cpr.exception.LicenseExtendsException;
import com.uniwin.cpr.license.LicenseQO;
import com.uniwin.cpr.license.SignatureManager;
import com.uniwin.framework.ui.system.EncodeUtil;
import com.uniwin.webkey.cms.itf.WebsiteService;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Menu;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.itf.IUsersfavorateManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.Usersfavorate;
import com.uniwin.webkey.core.model.WkTSite;
import com.uniwin.webkey.core.util.OperationLogUtil;
import com.uniwin.webkey.system.Sysconfig;
import com.uniwin.webkey.system.ui.LeftPageContentProvider;
import com.uniwin.webkey.tree.ui.LeftPageTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;
import com.uniwin.webkey.util.ui.ResourceContentProvider;

/***
 * 登陆win类
 * 
 * @author Administrator
 * 
 */
public class ShowWindow extends Window implements AfterCompose
{
	public Toolbarbutton passCpa;

    public Textbox getUserName()
    {
        return userName;
    }

    public void setUserName(Textbox userName)
    {
        this.userName = userName;
    }

    public Textbox getPassword()
    {
        return password;
    }

    public void setPassword(Textbox password)
    {
        this.password = password;
    }

    private boolean               needCode             = false;

    private Captcha               cpa;

    private Label                 loginCode_label;

    private Textbox               userName, loginCode, password,loginName;

    private IUserroleManager      userroleManager      = (IUserroleManager) SpringUtil.getBean("userroleManager");

    boolean                       isInputPassWord      = false;

    private static IUsersManager  usersManager         = (IUsersManager) SpringUtil.getBean("usersManager");

    private IResourceManager      resourceManager      = (IResourceManager) SpringUtil.getBean("resourceManager");

    private IRoleManager          roleManager          = (IRoleManager) SpringUtil.getBean("roleManager");

//    private IAlarmclockManager    alarmclockManager    = (IAlarmclockManager) SpringUtil.getBean("alarmclockManager");

    private IUsersfavorateManager usersfavorateManager = (IUsersfavorateManager) SpringUtil.getBean("usersfavorateManager");
    
    private IOrganizationManager  organizationManager  = (IOrganizationManager) SpringUtil.getBean("organizationManager");

    private IAuthManager          authManager          = (IAuthManager) SpringUtil.getBean("authManager");
    
    
    private Session               curSession              = Sessions.getCurrent();

    private WebsiteService websiteService=(WebsiteService)org.zkoss.spring.SpringUtil.getBean("websiteService");
    
    Window win;
    private Menuitem mitem0,m70,m67,m63,m43,m124,m123,m275,m151,mm4,m71,mm6,mm7,mm8,m274;
    private West westLeft;


    public void afterCompose(){
        
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		
        loginCode_label = (Label) this.getFellow("loginCode_label");
        passCpa = (Toolbarbutton) this.getFellow("passCpa");
        needCode = Sysconfig.isNeedVerificationCode();
        loginCode = (Textbox) this.getFellow("loginCode");
        cpa = (Captcha) this.getFellow("cpa");
        userName = (Textbox) this.getFellow("loginName");
        password = (Textbox) this.getFellow("password");
        userName.focus();
        cpa.setLength(Sysconfig.getVerificationLength());
        loginCode.setVisible(needCode);
        cpa.setVisible(needCode);
        passCpa.setVisible(needCode);
        loginCode_label.setVisible(needCode);
        mitem0.setStyle("font-size:20px");
        mitem0.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				Executions.getCurrent().sendRedirect("login.zul");
			}
        	
        });
        m70.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(70);	
			}
        	
        });
        
        m67.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(67);	
			}
        	
        });
        
        m63.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(63);	
			}
        	
        });
        
        m43.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(43);	
			}
        	
        });
        
        m124.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(124);	
			}
        	
        });
        
        m123.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(123);	
			}
        	
        });
        
        m275.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(275);	
			}
        	
        });
        
        m151.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(151);	
			}
        	
        });
        
        m71.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(71);	
			}
        	
        });
        
        m274.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				westLeft.getChildren().clear();
				ShowNews showNews = (ShowNews)Executions.getCurrent().createComponents("/apps/core/shownews.zul", westLeft, null);
				showNews.initWindow(274);	
			}
        	
        });
        
        
        
        cpa.setAlign("center");

		cpa.setValue(String.valueOf(Math.random()*9000+1000).substring(0, 4));
//        win=(Window)this.getFellow("win");
//        win.setContentStyle("background-image:url(/back.jpg)");
		passCpa.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				cpa.setValue(String.valueOf(Math.random() * 9000 + 1000)
						.substring(0, 4));
			}
		});
		curSession
        .setAttribute("themeName","default");
    }
    
    /**
     * 登录系统
     * @param logintoUrl
     */
    public void loginTosystem(){
    	
    	// 屏蔽版权查看代码。---版权控制代码
        /*boolean b = false;
        LicenseQO licenseQo = new LicenseQO("webkey", "V50",String.valueOf((usersManager.getAllUser().size())));
        b = SignatureManager.verify(licenseQo);*/
        
		/*if(!b)
		{
		    try 
		    {
		        Messagebox.show(org.zkoss.util.resource.Labels
		                .getLabel("system.common.version.errors"));
		    } 
		    catch (InterruptedException e) 
		    {
		        e.printStackTrace();
		    }
		}
		else{*/
			
		  //  Clients.showBusy("正在登陆中，请稍后......", true);
		    boolean isSuccess = true;
		    try
		    {
		        if (needCode && loginCode.getText().equals(""))
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.password_is_null"),
		                    org.zkoss.util.resource.Labels
		                            .getLabel("system.commom.ui.prompt"),
		                    Messagebox.OK, Messagebox.INFORMATION);
		            return;
		        }
		        if (needCode
		                && !loginCode.getText().equalsIgnoreCase(cpa.getValue()))
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.codenottrue"),
		                    org.zkoss.util.resource.Labels
		                            .getLabel("system.commom.ui.prompt"),
		                    Messagebox.OK, Messagebox.INFORMATION);
//		            cpa.randomValue();
		            cpa.setValue(String.valueOf(Math.random()*9000+1000).substring(0, 4));
		            return;
		        }
		        if (userName == null || password == null)
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.loginpage"));
		            return;
		        }
		        User user = usersManager.getUserByUserNameandpwd(
		                userName.getText(), password.getText());
		        if (user.getName() == null)
		        {
		            isSuccess = false;
		        } else
		        {
		            boolean isPass = this.checkUserCondition(user);
		            
		            if (isPass)
		            {
		                curSession
		                        .setAttribute(
		                                "themeName",
		                                user.getSkinName() == null
		                                        || user.getSkinName().trim()
		                                                .equals("") ? "defult"
		                                        : user.getSkinName());
		                Users users = usersManager.getUsersByLoginName(user.getLoginName());
		                List<WkTWebsite> wsList = new ArrayList<WkTWebsite>();
		                wsList = roleManager.getKwByUserId(users.getUserId());
		                List<Resource> rsList = authManager.getResourceByUserIdAndWebsite(
		                    users.getUserId(), wsList.get(0).getKwId().intValue());
		          
		                curSession.setAttribute("domain_defult", wsList.get(0));
		                curSession.setAttribute("website", wsList.get(0));
		                curSession.setAttribute("rsList", rsList);
		                curSession.setAttribute("wsList", wsList);
		                curSession.setAttribute("users", users);
		                curSession.setAttribute("rbac_user", user);
		                List deptList = new ArrayList();
		                List deptIdList = new ArrayList();
		                
		                deptList = organizationManager.getChildrenByParentId(user.getOrganizationId());
		                Iterator it = deptList.iterator();
		                while (it.hasNext())
		                {
		                    Organization org = (Organization) it.next();
		                    deptIdList.add(Long.parseLong(org.getOrganizationId()+""));
		                }
		                curSession.setAttribute("userDeptList", deptIdList);
		                updateUserLtimes(user);
		            } else
		            {
		                return;
		            }

		        }
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

		    if (isSuccess)
		    {
		        try
		        {
		            // 检查用户是否被锁定
		            User user = FrameCommonDate.getUser();
//		            if (org.zkoss.util.resource.Labels.getLabel("users.ui.locking")
//		                    .equals(user.getState()))
//		            {
//		                Messagebox.show(org.zkoss.util.resource.Labels
//		                        .getLabel("login.ui.account"));
//		                return;
//		            }
		            Users users = usersManager.getUsersByLoginName(user.getLoginName());
		            if(users.getIsLocked().equals("2"))
			           {
			        	   Messagebox.show("您的账户已失效");
			                return;  
			           }
		           if(users.getAccountEndTime().compareTo(new Date())<0)
		           {
		        	   Messagebox.show("您的账户已过期");
		                return;  
		           }
		           
		            initFrameworkSessionData();
		            Executions.sendRedirect("/admin/index.zul");
		            
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }

		    } else
		    {
		        try
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.usernameOrpassworderror"));
		            Executions.sendRedirect("login.zul");
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
		    
		    
//		}       
    }

    
    
	/**
     * @author gb
     * @return
     */
    private boolean checkUserMac() {
    	
    	String mac= EncodeUtil.encodeByDES(getMacOnWindow().trim());
    	List lists=websiteService.findByMac(mac);
		if(lists.size()==0){
			throw new WrongValueException(userName, "该网站尚未授权，请联系管理员");
		}else{
			WkTSite site=(WkTSite) lists.get(0);
			Long endtime=Long.valueOf(EncodeUtil.decodeByDESStr(site.getKsEndtime()));
			Date now=new Date();
			 if(endtime+24*3600*1000<now.getTime()){
					throw new WrongValueException(userName, "该网站授权时间已到，请联系管理员");
			}else{
				return true;
			}
		}
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
           
        	/*curSession.setAttribute(
                    "rbac_alarmclockExs",
                    this.alarmclockManager.getThreeAlidAlarmclocksByUserId(user
                            .getUserId().intValue()));*/
        	
         // 获取当前用户的导航菜单的资源信息，并保存到session中
            this.setFavorateResourceList(user.getUserId().intValue());

         // 获取当前用户所有角色数，并放到session中.
            List<Role> roleList = userroleManager
                    .getByHQL("select role from Userrole user,Role role where role.state<>1 and role.roleId=user.roleId and user.userId="
                            + user.getUserId());

            curSession.setAttribute("rbac_roleList", roleList);

            /* 获取所有的角色id，根据角色id获取当前用户的所有许可，并将所有的许可数据保存入session中 */
            Integer[] roleIds = new Integer[roleList.size()];
            int i = 0;
            Role role = null;
            for (Integer roleId : roleIds)
            {
                role = roleList.get(i);
                roleIds[i] = role.getRoleId();
                i++;
            }

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
            curSession.setAttribute("rbac_permissionMap",
                    permissionMap);
            

            List<Resource> resList = (List<Resource>)curSession.getAttribute("rsList");
            curSession.setAttribute("rbac_resourceList", resList);
            /* 根据许可id获取所有的资源信息，并保存到session中 */
            curSession.setAttribute("rbac_resourceTreeList", resList);

            /* 根据一级资源名称来生成对应的树，保存到map中便于以后使用直接获取树 */
//            Menu menu = null;

            List<Resource> domainList = new ArrayList<Resource>();
            for (Resource resource : resList)
            {
                if (resource.getParentId().equals(0))
                {
                    domainList.add(resource);
                }
            }
            if (domainList == null || domainList.size() == 0)
            {
                Executions.sendRedirect("login.zul");
                return;
            }
            curSession.setAttribute("domain_list", domainList);
            List<Menu> menus = new ArrayList<Menu>();
            for (Resource resourceTop : resList)
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
//                    System.out.println("loginwin.menusSize======"+menus.size());
                }
            }
            WkTWebsite curWS = (WkTWebsite)curSession.getAttribute("domain_defult");
            curSession.setAttribute("ws_"+curWS.getKwId(),menus);
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
        curSession.setAttribute("rbac_FavorateResourceList",
                resources);
    }

    /**
     * 刷新所有用户信息
     * 
     * @param session
     */
    public void initFrameworkSessionDataByUserId(HttpSession session)
    {
        User user = FrameCommonDate.getUser();
        try
        {
        	// 获取三个距离发生时间最近的闹钟
//            session.setAttribute("rbac_alarmclockExs", this.alarmclockManager
//                    .getThreeAlidAlarmclocksByUserId(user.getUserId()
//                            .intValue()));
        	
         // 获取当前用户的导航菜单的资源信息，并保存到session中
            this.setFavorateResourceList(user.getUserId().intValue());

         // 获取当前用户所有角色数，并放到session中.
            List<Role> roleList = userroleManager
                    .getByHQL("select role from Userrole user,Role role where role.roleId=user.roleId and user.userId="
                            + user.getUserId());
            ;
            session.setAttribute("rbac_roleList", roleList);

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
//            List<Permission> permissionList = permissionManager.getPermissionListByRoleIds(roleIds);
            List<Permission> permissionList = new ArrayList<Permission>();
            session.setAttribute("rbac_permissionList", permissionList);

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
            session.setAttribute("rbac_permissionMap", permissionMap);
            
            List<Resource> resList = resourceManager
                    .getResourceListByPermissionIds(permissionIds);
            session.setAttribute("rbac_resourceList", resList);
            /* 根据许可id获取所有的资源信息，并保存到session中 */
            List<Resource> resourceList = resourceManager
                    .getResourceListByPermissionIds(permissionIds);
            session.setAttribute("rbac_resourceTreeList", resList);

            /* 根据一级资源名称来生成对应的树，保存到map中便于以后使用直接获取树 */
            Map treeMap = new HashMap();
            Menu menu = null;

            Resource r = new Resource();
            r.setId(0);
            List domainList = new ResourceContentProvider().getChildren(r);
            session.setAttribute("domain_list", domainList);
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
                    session.setAttribute(resourceTop.getDomain(), menus);
                }
            }
        } catch (Exception e)
        {
        }

    }

    /**
     * 判断用户登录条件
     * 
     * @param user
     *            当前登录用户
     */
    public boolean checkUserCondition(User user)
    {
        boolean isPass = true;
        try
        {
            // 判断绑定地址
            if (user.getKuBindType() != null
                    && !user.getKuBindType().equals("0"))
            {
                HttpServletRequest request = (HttpServletRequest) Executions
                        .getCurrent().getNativeRequest();
                String ip = request.getRemoteAddr();
                if (user.getKuBindAddr() == null
                        || !user.getKuBindAddr().equals(ip))
                {
                    Messagebox.show("对不起，您登录的地址与绑定的地址不同！", "提示信息",
                            Messagebox.OK, Messagebox.INFORMATION);
                    isPass = false;
                }
            }

            // 判断登录次数
            if (user.getKuLimit() != null && user.getKuLtimes() != null
                    && user.getKuLimit() != 0
                    && user.getKuLtimes() >= user.getKuLimit())
            {
                Messagebox.show("对不起，您已经超出了允许的登录次数！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                isPass = false;
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return isPass;
    }

    public void updateUserLtimes(User user)
    {
        int ltimes = 1;
        if (user.getKuLtimes() != null)
        {
            ltimes = user.getKuLtimes() + 1;
        }
        try
        {
            Users users = usersManager.get(user.getUserId());
            users.setKuLtimes(ltimes);
            usersManager.update(users);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 忘记密码
     */
    public void forgetPass()
    {
        try
        {
            Map map = new HashMap();
            map.put("loginWin", ShowWindow.this);
            ForgetPassWin win = (ForgetPassWin) Executions.createComponents(
                    "/apps/core/forgetPass.zul", null, map);
            win.setClosable(true);
            win.doModal();
        } catch (SuspendNotAllowedException e)
        {
            // e.printStackTrace();
        } catch (Exception e)
        {
            // e.printStackTrace();
        }
    }

    public void loginToSysByPassQuestion(Users users)
    {
    	//Clients.showBusy("正在登陆中，请稍后......", false);
        boolean isSuccess = true;
        try
        {

            User user = usersManager.getCurrUserByLoginName(users
                    .getLoginName());
            if (user.getName() == null)
            {
                isSuccess = false;

            } else
            {
                boolean isPass = this.checkUserCondition(user);
                if (isPass)
                {
                    curSession.setAttribute("themeName", user.getSkinName() == null
                        || user.getSkinName().trim().equals("") ? "defult" : user.getSkinName());
                    curSession.setAttribute("rbac_user", user);
                    curSession.setAttribute("users", users);
                    List<WkTWebsite> wsList = new ArrayList<WkTWebsite>();
                    // wsList = roleManager.getKwidsByUserId(users.getUserId());
                    wsList = roleManager.getKwByUserId(users.getUserId());
                    List<Resource> rsList = authManager.getResourceByUserIdAndWebsite(users
                        .getUserId(), wsList.get(0).getKwId().intValue());
                    // initWs(users,wsList);
                    curSession.setAttribute("domain_defult", wsList.get(0));
                    curSession.setAttribute("website", wsList.get(0));
                    curSession.setAttribute("rsList", rsList);
                    curSession.setAttribute("wsList", wsList);
                    curSession.setAttribute("users", users);
                    curSession.setAttribute("rbac_user", user);
                    List deptList = new ArrayList();
                    List deptIdList = new ArrayList();

                    deptList = organizationManager.getChildrenByParentId(user.getOrganizationId());
                    Iterator it = deptList.iterator();
                    while (it.hasNext())
                    {
                        Organization org = (Organization) it.next();
                        deptIdList.add(Long.parseLong(org.getOrganizationId() + ""));
                    }
                    curSession.setAttribute("userDeptList", deptIdList);
                    updateUserLtimes(user);
                } else
                {
                    return;
                }
            }
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

        if (isSuccess)
        {
            try
            {
                // ����û��Ƿ���
                User user = FrameCommonDate.getUser();
//                if (org.zkoss.util.resource.Labels.getLabel("users.ui.locking")
//                        .equals(user.getState()))
//                {
//                    Messagebox.show(org.zkoss.util.resource.Labels
//                            .getLabel("login.ui.account"));
//                    return;
//                }
                Users u= usersManager.getUsersByLoginName(user.getLoginName());
	            if(u.getIsLocked().equals("2"))
		           {
		        	   Messagebox.show("您的账户已失效");
		                return;  
		           }
	           if(u.getAccountEndTime().compareTo(new Date())<0)
	           {
	        	   Messagebox.show("您的账户已过期");
	                return;  
	           }
                initFrameworkSessionData();
                Executions.sendRedirect("../../admin/index.zul");
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        } else
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("login.ui.usernameOrpassworderror"));
                Executions.sendRedirect("login.zul");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void initWs(Users users,List<WkTWebsite> wsList) throws DataAccessException, ObjectNotExistException{
        Iterator it = wsList.iterator();
        Map wsMap = new HashMap();
        while (it.hasNext())
        {
            WkTWebsite tWebsite = (WkTWebsite) it.next();
            List<Resource> rsList = authManager.getResourceByUserIdAndWebsite(
                users.getUserId(),tWebsite.getKwId().intValue());
            wsMap.put(tWebsite.getKwId(), rsList);
        }
        curSession.setAttribute("wsMap", wsMap);
    }
    

    //��ȡ�������mac��ַ
	private static String getMacOnWindow() {
        String s = "";
        try {
            String s1 = "ipconfig /all";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine;
            for (String line = bufferedreader.readLine(); line != null; line = nextLine) {
                nextLine = bufferedreader.readLine();
                if (line.indexOf("Physical Address") <= 0) {
                    continue;
                }
                int i = line.indexOf("Physical Address") + 36;
                s = line.substring(i);
                break;
            }
            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim();
    }
	 public void resetTosystem()
	 {
		 loginCode.setValue("");
		 password.setValue(""); 
		 userName.setValue("");
	 }
	 public void register()
	 {
		  Window win = (Window) Executions.createComponents(
                  "/apps/core/register.zul", null, null);
		  try {
			win.doModal();
		} catch (SuspendNotAllowedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	 }
	 public void Login(String loginname)
	 {
		  boolean isSuccess = true;
			try {
				User	user = usersManager.getUserByUserName(loginname);
			
	            curSession
	                    .setAttribute(
	                            "themeName",
	                            user.getSkinName() == null
	                                    || user.getSkinName().trim()
	                                            .equals("") ? "defult"
	                                    : user.getSkinName());
	            Users users = usersManager.getUsersByLoginName(loginname);
	            List<WkTWebsite> wsList = new ArrayList<WkTWebsite>();
	            wsList = roleManager.getKwByUserId(users.getUserId());
	            List<Resource> rsList = authManager.getResourceByUserIdAndWebsite(
	                users.getUserId(), wsList.get(0).getKwId().intValue());
	      
	            curSession.setAttribute("domain_defult", wsList.get(0));
	            curSession.setAttribute("website", wsList.get(0));
	            curSession.setAttribute("rsList", rsList);
	            curSession.setAttribute("wsList", wsList);
	            curSession.setAttribute("users", users);
	            curSession.setAttribute("rbac_user", user);
	            List deptList = new ArrayList();
	            List deptIdList = new ArrayList();
	            
	            deptList = organizationManager.getChildrenByParentId(user.getOrganizationId());
	            Iterator it = deptList.iterator();
	            while (it.hasNext())
	            {
	                Organization org = (Organization) it.next();
	                deptIdList.add(Long.parseLong(org.getOrganizationId()+""));
	            }
	            curSession.setAttribute("userDeptList", deptIdList);
	            updateUserLtimes(user);
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

		    if (isSuccess)
		    {
		        try
		        {
		            // 检查用户是否被锁定
		            User user = FrameCommonDate.getUser();
//		            if (org.zkoss.util.resource.Labels.getLabel("users.ui.locking")
//		                    .equals(user.getState()))
//		            {
//		                Messagebox.show(org.zkoss.util.resource.Labels
//		                        .getLabel("login.ui.account"));
//		                return;
//		            }
		            Users users = usersManager.getUsersByLoginName(user.getLoginName());
		            if(users.getIsLocked().equals("2"))
			           {
			        	   Messagebox.show("您的账户已失效");
			                return;  
			           }
		           if(users.getAccountEndTime().compareTo(new Date())<0)
		           {
		        	   Messagebox.show("您的账户已过期");
		                return;  
		           }
		           
		            initFrameworkSessionData();
		            Executions.sendRedirect("../../admin/index.zul");
		            
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }

		    } else
		    {
		        try
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.usernameOrpassworderror"));
		            Executions.sendRedirect("login.zul");
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
	 }
}
