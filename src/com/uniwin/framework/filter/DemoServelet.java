package com.uniwin.framework.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.uniwin.contextloader.BeanFactory;
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
import com.uniwin.webkey.core.util.OperationLogUtil;

public class DemoServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	   private IUserroleManager      userroleManager      = (IUserroleManager) BeanFactory.getBean("userroleManager");

	    boolean                       isInputPassWord      = false;

	    private static IUsersManager  usersManager         = (IUsersManager) BeanFactory.getBean("usersManager");

	    private IResourceManager      resourceManager      = (IResourceManager) BeanFactory.getBean("resourceManager");

	    private IRoleManager          roleManager          = (IRoleManager) BeanFactory.getBean("roleManager");

	    private IUsersfavorateManager usersfavorateManager = (IUsersfavorateManager) BeanFactory.getBean("usersfavorateManager");
	    
	    private IOrganizationManager  organizationManager  = (IOrganizationManager) BeanFactory.getBean("organizationManager");

	    private IAuthManager          authManager          = (IAuthManager) BeanFactory.getBean("authManager");
	    
	    HttpSession session;
	
	public DemoServelet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		    session=request.getSession();
		    if(request.getParameter("name")!=""&&request.getParameter("name")!=null)
		    {
		    Login(request.getParameter("name"),request,response);
		    }
		    else
		    {
		    		 response.sendRedirect("error.zul");
		    }
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	 public void Login(String loginname,HttpServletRequest request,HttpServletResponse response)
	 {
		  boolean isSuccess = true;
			try {
				User user = usersManager.getUserByUserName(loginname);
				session.setAttribute("themeName",user.getSkinName() == null|| user.getSkinName().trim().equals("") ? "defult": user.getSkinName());
	            Users users = usersManager.getUsersByLoginName(loginname);
	            List<WkTWebsite> wsList = new ArrayList<WkTWebsite>();
	            wsList = roleManager.getKwByUserId(users.getUserId());
	            List<Resource> rsList = authManager.getResourceByUserIdAndWebsite(
	                users.getUserId(), wsList.get(0).getKwId().intValue());
	      
	            session.setAttribute("domain_defult", wsList.get(0));
	            session.setAttribute("website", wsList.get(0));
	            session.setAttribute("rsList", rsList);
	            session.setAttribute("wsList", wsList);
	            session.setAttribute("users", users);
	            session.setAttribute("rbac_user", user);
	            List deptList = new ArrayList();
	            List deptIdList = new ArrayList();
	            
	            deptList = organizationManager.getChildrenByParentId(user.getOrganizationId());
	            Iterator it = deptList.iterator();
	            while (it.hasNext())
	            {
	                Organization org = (Organization) it.next();
	                deptIdList.add(Long.parseLong(org.getOrganizationId()+""));
	            }
	            session.setAttribute("userDeptList", deptIdList);
	            updateUserLtimes(user);
			} catch (Exception e)
		    {
		        try
		        {
		            response.sendRedirect(request.getContextPath()+"/error.zul");
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
		            User user = (User) session.getAttribute("rbac_user");
		            if ("锁定".equals(user.getState()))
		            {
		            	 response.sendRedirect(request.getContextPath()+"/error.zul");
		                return;
		            }

		            initFrameworkSessionData(request,response);		
		            response.sendRedirect(request.getContextPath()+"/admin/index.zul");
		            
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }

		    } else
		    {
		        try
		        {
		        	System.out.println("用户名或密码错误！");
		            response.sendRedirect(request.getContextPath()+"/login.zul");
		        } catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		    }
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
	           // usersManager.update(users);
	        } catch (DataAccessException e)
	        {
	            e.printStackTrace();
	        } catch (ObjectNotExistException e)
	        {
	            e.printStackTrace();
	        }
	    }
	    /**
	     * 初始化框架的session数据，将数据按照一定的结构方式保存到内存中，为将来更方便的访问session中的数据做准备。
	     * session中的数据都通过FrameCommonData的静态方法暴暴露出来
	     */
	    public void initFrameworkSessionData(HttpServletRequest request,HttpServletResponse response)
	    {
	    	// 获得session中的用户信息
	        User user = (User) session.getAttribute("rbac_user");
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

	            session.setAttribute("rbac_roleList", roleList);

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
	            session.setAttribute("rbac_permissionList",permissionList);

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
	            session.setAttribute("rbac_permissionMap",
	                    permissionMap);
	            

	            List<Resource> resList = (List<Resource>)session.getAttribute("rsList");
	            session.setAttribute("rbac_resourceList", resList);
	            /* 根据许可id获取所有的资源信息，并保存到session中 */
	            session.setAttribute("rbac_resourceTreeList", resList);

	            /* 根据一级资源名称来生成对应的树，保存到map中便于以后使用直接获取树 */
//	            Menu menu = null;

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
	            	response.sendRedirect(request.getContextPath()+"/login.zul");
	                return;
	            }
	            session.setAttribute("domain_list", domainList);
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
//	                    System.out.println("loginwin.menusSize======"+menus.size());
	                }
	            }
	            WkTWebsite curWS = (WkTWebsite)session.getAttribute("domain_defult");
	            session.setAttribute("ws_"+curWS.getKwId(),menus);
	            // login Log
	            OperationLogUtil.addLog4WS("用户"
	                    + user.getName() + "登录成功", user.getUserId() + "",user,"");
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
	        session.setAttribute("rbac_FavorateResourceList",
	                resources);
	    }
	 
}
