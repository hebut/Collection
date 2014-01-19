package com.uniwin.webkey.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;


import com.opensymphony.xwork2.ActionSupport;
import com.uniwin.framework.ui.system.CryptUtils;
import com.uniwin.framework.ui.system.EncodeUtil;
import com.uniwin.webkey.cms.action.WebkeyAction;
import com.uniwin.webkey.cms.action.WebkeyActionUtil;
import com.uniwin.webkey.cms.itf.ChanelService;
import com.uniwin.webkey.cms.itf.NewsService;
import com.uniwin.webkey.cms.itf.WebsiteService;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.cms.prepdata.WKT_DOCLIST;
import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.cms.util.IPrepareData;
import com.uniwin.webkey.cms.util.PrepDataFactory;
import com.uniwin.webkey.cms.util.XmlParser;
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
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTSite;
import com.uniwin.webkey.core.util.OperationLogUtil;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.WKTInfoView;
import com.uniwin.webkey.system.ui.LeftPageContentProvider;
import com.uniwin.webkey.tree.ui.LeftPageTree;
import com.uniwin.webkey.util.ui.FrameCommonDate;
import com.uniwin.webkey.util.ui.IPUtil;
import com.uniwin.webkey.util.ui.ResourceContentProvider;

public class WelcomeAction extends WebkeyAction {
	private static final long serialVersionUID = 6251287960458567952L;
	private HttpServletRequest request;
	WKT_DOCLIST wkt_doclist;
	int page = 1;
	int pageCount;
	HttpServletResponse response;
	NewsService newsService;
	ChanelService chanelService;
	WebsiteService websiteService; 
	
	IAuthManager authManager;
	String cssFile;
	String templet = "index";
    private IUsersManager  usersManager;
    private IUsersfavorateManager usersfavorateManager;
    HttpSession   curSession;
    private IResourceManager      resourceManager;

    private IRoleManager          roleManager ;
    private IUserroleManager      userroleManager ;
    private IOrganizationManager  organizationManager;
    private String userName = "";
    private String password = "";
    private String infoId="";
    private String[] infoDetail;

	public String[] getInfoDetail() {
		return infoDetail;
	}

	public void setInfoDetail(String[] infoDetail) {
		this.infoDetail = infoDetail;
	}

	public String login() throws Exception 
	{
		System.out.println("用户登陆成功！");
		return this.SUCCESS;
	}

	public String goLogin() throws Exception 
	{
		if (request.getSession().isNew())
        {
        	
            if (counter == 0)
            {
                gainCounter(getPath() + "WEB-INF/counter.txt");
            }
            ++counter;
            saveCounter(getPath() + "WEB-INF/counter.txt");
            // System.out.println(counter);
        } else if (counter == 0)
        {
            gainCounter(getPath() + "WEB-INF/counter.txt");
        }
		
		WkTWebsite site = (WkTWebsite) websiteService.get(WkTWebsite.class, 31L);
		if(null==site)
		{
			cssFile = "default";
		}else{
			
			cssFile = site.getkwStyle();
		}
		if (WebkeyActionUtil.checkSession(request))
        {
            Users user = WebkeyActionUtil.getUser(request);
            cssFile = user.getKustyle();
        }
		templet = request.getParameter("templet");
		if(null==templet||"".equals(templet))
		{
			templet="index";
		}
		try {
			// 解决模版数据配置XML文件。
			String rootPath = request.getRealPath("/");
			XmlParser xp = new XmlParser(rootPath + "WEB-INF/templet/" + cssFile + "/" + templet + ".xml");
			// 解板界面元素
			for (int i = 0; i < xp.size(); i++) {
				Map data = (Map) xp.getItem(i);
				System.out.println("---------------------------------");
				System.out.println("-------data----"+data);
				
				System.out.println("---------------------------------");
				data.put("newsService", newsService);				
				data.put("chanelService", chanelService);
				data.put("authManager", authManager);
				data.put("websiteService", websiteService);
				data.put("kwId", 31L);
				IPrepareData ipd = PrepDataFactory.getPrepareData(data.get("type").toString());
				ipd.addData(request, response, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("开始进入登陆----->>>");
		return this.SUCCESS;
	}
	
    /**
     * 登录系统
     * @param logintoUrl
     */
    public String searchInfoNewsDetail(){
    	curSession = this.request.getSession();
		    boolean isSuccess = true;
		    try
		    {
		        if (userName == null || password == null)
		        {
		            Messagebox.show(org.zkoss.util.resource.Labels
		                    .getLabel("login.ui.loginpage"));
		            return ActionSupport.ERROR;
		        }
		        User user = usersManager.getUserByUserNameandpwdFromEmail(
		                userName.trim(), password.trim());
		        if (user.getName() == null)
		        {
		            isSuccess = false;
		        } else
		        {
		            boolean isPass = this.checkUserCondition(user);
		            
		            if (isPass)
		            {
		          
		                curSession.setAttribute("themeName",user.getSkinName() == null|| user.getSkinName().trim().equals("") ? "defult"
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
		                curSession.setAttribute("infoId", infoId);
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
			            return ActionSupport.ERROR;
		            }

		        }
		    } catch (Exception e)
		    {
		        try
		        {
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
		            User user = (User) curSession.getAttribute("rbac_user");
		            Users users = usersManager.getUsersByLoginName(user.getLoginName());
		            if(users.getIsLocked().equals("2"))
			           {
			        	   Messagebox.show("您的账户已失效");
				            return ActionSupport.ERROR;  
			           }
		           if(users.getAccountEndTime().compareTo(new Date())<0)
		           {
		        	   Messagebox.show("您的账户已过期");
			            return ActionSupport.ERROR;  
		           }
		           WKTInfoView infoView = new WKTInfoView();
		           infoView.setLoginName(userName);
		           infoView.setInfoId(Long.valueOf(infoId));
		           infoView.setTime(System.currentTimeMillis());
		           infoView.setTimeStamp(System.currentTimeMillis());
		           String ipAddr = IPUtil.getRemortIP(request);
		           infoView.setIpAddr(ipAddr);
		           newsService.save(infoView);
		            initFrameworkSessionData();
		            return ActionSupport.SUCCESS;
		            
		        } catch (Exception e)
		        {
		        }

		    } else
		    {
		        try
		        {
		            return ActionSupport.ERROR;
		        } catch (Exception e)
		        {
		        }
		    }
		   	return ActionSupport.ERROR;	      
    }

      
    public String searchInfoDetail(){
    	if(userName!=null&&!userName.trim().equals("")){
            WKTInfoView infoView = new WKTInfoView();
            String name = CryptUtils.decrypt(userName);
            infoView.setLoginName(name);
            String infoNo = CryptUtils.decrypt(infoId);
            infoView.setInfoId(Long.valueOf(infoNo));
            infoView.setTime(System.currentTimeMillis());
            infoView.setTimeStamp(System.currentTimeMillis());
            String ipAddr = IPUtil.getRemortIP(request);
            infoView.setIpAddr(ipAddr);
            newsService.save(infoView);
    		if(infoNo!=null&&!infoNo.trim().equals("")){
    			List<String[]>list = newsService.findWkTInfoByInfoId(Long.valueOf(infoNo.trim()));
    			if(list!=null&&list.size()!=0){
        			this.infoDetail = list.get(0);
    			}
    		}
    		else{
        		return ActionSupport.ERROR;
    		}
        	return ActionSupport.SUCCESS;
    	}
    	else{
    		return ActionSupport.ERROR;
    	}
    }
    
	/**
     * @author gb
     * @return
     */
    @SuppressWarnings("unused")
	private boolean checkUserMac() {
    	
    	String mac= EncodeUtil.encodeByDES(getMacOnWindow().trim());
    	List lists=websiteService.findByMac(mac);
		if(lists.size()==0){
			//throw new WrongValueException(userName, "该网站尚未授权，请联系管理员");
			return false;
		}else{
			WkTSite site=(WkTSite) lists.get(0);
			Long endtime=Long.valueOf(EncodeUtil.decodeByDESStr(site.getKsEndtime()));
			Date now=new Date();
			 if(endtime+24*3600*1000<now.getTime()){
					//throw new WrongValueException(userName, "该网站授权时间已到，请联系管理员");
				 return false;
			}else{
				return true;
			}
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
     * 设置session中的FavorateResourceList
     * 
     * @throws DataAccessException
     * @throws ObjectNotExistException
     */
    public void setFavorateResourceList(int userId) throws DataAccessException,
            ObjectNotExistException
    {
    	curSession = this.request.getSession();
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
     * 初始化框架的session数据，将数据按照一定的结构方式保存到内存中，为将来更方便的访问session中的数据做准备。
     * session中的数据都通过FrameCommonData的静态方法暴暴露出来
     */
    public void initFrameworkSessionData()
    {
    	curSession = this.request.getSession();
    	// 获得session中的用户信息
        User user = (User) curSession.getAttribute("rbac_user");
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
                }
            }
            WkTWebsite curWS = (WkTWebsite)curSession.getAttribute("domain_defult");
            curSession.setAttribute("ws_"+curWS.getKwId(),menus);
        } catch (Exception e)
        {
        }

    }
    
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
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
     * <li>功能描述：从计数器文件中读取计数值。
     * 
     * @param fileName - 计数器文件名
     * @author 张英
     */
    private synchronized void gainCounter(String fileName)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line = in.readLine();
            if (line != null)
            {
                counter = Integer.parseInt(line);
            } else
            {
                counter = 0;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            counter = 0;
        }
    }

    /**
     * <li>功能描述：保存点击计数。
     * 
     * @param fileName - 计数器文件名
     * @author 张英
     */
    private synchronized void saveCounter(String fileName)
    {
        try
        {
            FileUtil.writerText(fileName, String.valueOf(counter));

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
	
	public String getTemplet() {
		return templet;
	}

	public void setTemplet(String templet) {
		this.templet = templet;
	}

	public String getCssFile() {
		return cssFile;
	}

	public void setCssFile(String cssFile) {
		this.cssFile = cssFile;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int pageNumber) {
		this.page = pageNumber;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public WKT_DOCLIST getWkt_doclist() {
		return wkt_doclist;
	}

	public void setWkt_doclist(WKT_DOCLIST wkt_doclist) {
		this.wkt_doclist = wkt_doclist;
	}

	public ChanelService getChanelService() {
		return chanelService;
	}

	public void setChanelService(ChanelService chanelService) {
		this.chanelService = chanelService;
	}

	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public IAuthManager getAuthManager() {
		return authManager;
	}

	public void setAuthManager(IAuthManager authManager) {
		this.authManager = authManager;
	}

	public WebsiteService getWebsiteService() {
		return websiteService;
	}

	public void setWebsiteService(WebsiteService websiteService) {
		this.websiteService = websiteService;
	}


	public IUsersfavorateManager getUsersfavorateManager() {
		return usersfavorateManager;
	}

	public void setUsersfavorateManager(IUsersfavorateManager usersfavorateManager) {
		this.usersfavorateManager = usersfavorateManager;
	}

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IUserroleManager getUserroleManager() {
		return userroleManager;
	}

	public void setUserroleManager(IUserroleManager userroleManager) {
		this.userroleManager = userroleManager;
	}

	public IOrganizationManager getOrganizationManager() {
		return organizationManager;
	}

	public void setOrganizationManager(IOrganizationManager organizationManager) {
		this.organizationManager = organizationManager;
	}

	public IUsersManager getUsersManager() {
		return usersManager;
	}

	public void setUsersManager(IUsersManager usersManager) {
		this.usersManager = usersManager;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}



	
}