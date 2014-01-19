package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.UsersEx;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.tree.ui.RoleTreeWin;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class UsersListWin extends Window implements AfterCompose
{
    private IUsersManager    usersManager;                                         // 用户信息管理

    private IUserroleManager userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");

    private Logger           log             = Logger
                                                     .getLogger(UsersListWin.class);

    private Textbox          name, loginName;                                       //  用户名字；用户登录名字

    private Button           searchUsers;                                          // 页面初始化查询

    private Listbox          usersData;

    public List              resUsers;                                             // 查询返回的结果集

    private OrganizationTree parentData;

    private Bandbox          organizationId;

	private IOrganizationManager organizationManager  ; // 组织管理服务接口
	
    private Organization     selectedOrganization;

    public int               selectId;

    private Paging           userPaging;
    
    private Resource         currResource;
    
    private Map              rsopMap = new HashMap();
    
    private Map              ulopMap = new HashMap();
    
    private Organization organi;

    public UsersListWin()
    {
        usersManager = (IUsersManager)SpringUtil.getBean("usersManager");
        organizationManager = (IOrganizationManager) SpringUtil.getBean("organizationManager");
        currResource = (Resource) Sessions.getCurrent().getAttribute("currResource");
        rsopMap = (Map)Sessions.getCurrent().getAttribute("rsopMap");
        if (null!=currResource)
        {
            if (null!=rsopMap.get("rs"+currResource.getId()))
            {
                ulopMap = (Map)rsopMap.get("rs"+currResource.getId());
            }
        }
        ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).setAttribute("ulop", ulopMap);
    }

    /**
	 * 转换成泛型
	 * 
	 * @param objList
	 * @return
	 */
    private List<UsersEx> fullUserEx(List objList)
    {
        Object[] object = null;
        UsersEx user = null;
        List<UsersEx> usersList = new ArrayList<UsersEx>();
        for (int j = 0; j < objList.size(); j++)
        {
            object = (Object[]) objList.get(j);
            user = new UsersEx();
            user.setName((String) object[0]);
            user.setLoginName((String) object[1]);
            user.setIsLocked((String) object[2]);
            user.setOrganizationName((String) object[3]);
            user.setUserId((Integer) object[4]);
            user.setPassword(object[5].toString());
            user.setOrganizationId((Integer) object[6]);
            user.setKuSex((String) object[7]);
            user.setKuBirthday((String) object[8]);
            user.setKuEmail((String) object[9]);
            user.setKuPhone((String) object[10]);
            user.setKuCertificatetype((String) object[11]);
            user.setKuCardNumber((String) object[12]);
            user.setKuPicPath((String) object[13]);
            user.setKuCompany((String) object[14]);
            user.setKuLtime((String) object[15]);
            user.setKuRtime((String) object[16]);
            user.setKuLastAddr((String) object[17]);
            user.setKuOnline((String) object[18]);
            user.setKuLtimes((Integer) object[19]);
            user.setKuLimit((Integer) object[20]);
            user.setKuAutoShow((String) object[21]);
            user.setKuBindType((String) object[22]);
            user.setKuBindAddr((String) object[23]);
            user.setKuAutoEnter((String) object[24]);
            user.setKuKeyLogin((String) object[25]);
            user.setKuCertId((String) object[26]);
            user.setKuCertInfo((String) object[27]);
            user.setKuForm((String) object[28]);
            user.setKuSkinname((String) object[29]);
            user.setKuPassProblem((String) object[30]);
            user.setKuPassAnswer((String) object[31]);
            usersList.add(user);
        }
        return usersList;
    }

    /**
	 * 查询用户信息
	 */
    public void searchUsers()
    {
        try
        {
        	final ArrayList oilist=new ArrayList();
        	if(organizationId.getValue() != null&&!organizationId.getValue().equals(""))
        	{
        	List olist=organizationManager.getChildrenByParentId(selectedOrganization.getOrganizationId()); 
        	oilist.add(0, selectedOrganization.getOrganizationId());
        	int m=1;
        	for(int i=0;i<olist.size();i++)
        	{
        		Organization o=(Organization) olist.get(i);
        		oilist.add(m, o.getOrganizationId());
        		m++;
        		List oclist=organizationManager.getChildrenByParentId(o.getOrganizationId()); 
        		if(oclist.size()!=0)
        		{
        			for(int j=0;j<oclist.size();j++ )
        			{
        			Organization oc=(Organization) oclist.get(i);
        			oilist.add(m, oc.getOrganizationId());
        			m++;
        			List oc2list=organizationManager.getChildrenByParentId(oc.getOrganizationId()); 
        			if(oc2list.size()!=0)
        			{
        				for(int h=0;h<oc2list.size();h++)
        				{
        				Organization oc2=(Organization) oc2list.get(i);
            			oilist.add(m, oc2.getOrganizationId());
            			m++;
            			List oc3list=organizationManager.getChildrenByParentId(oc2.getOrganizationId()); 
            			if(oc3list.size()!=0)
            			{
            			for(int k=0;k<oc3list.size();k++)
            			{
            				Organization oc3=(Organization) oc3list.get(i);
                			oilist.add(m, oc3.getOrganizationId());
                			m++;
            			}
            			}
        			}
        			}
        		}
        		}
        	}
        	}
            resUsers = usersManager.seachUser1(name.getText(), organizationId
                    .getValue() == null
                    || organizationId.getValue().equals("") ? null
                    : oilist , loginName
                    .getValue());
            int totalSize = 0;
            if (resUsers.size() > 0)
            {
                totalSize = ((Long) resUsers.get(0)).intValue();
            }
            final int totalSizeB = totalSize;
            userPaging.addEventListener("onPaging", new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    int fromint = (userPaging.getActivePage())
                            * userPaging.getPageSize();
                    int toint = (userPaging.getActivePage() + 1)
                            * userPaging.getPageSize();
                    if (totalSizeB < toint)
                        toint = totalSizeB;
                    resUsers = usersManager
                            .seachUserByPage1(name.getText(),
                                    organizationId.getValue() == null
                                            || organizationId.getValue()
                                                    .equals("") ? null
                                            : oilist, loginName.getValue(),
                                    fromint, userPaging.getPageSize());
                    rebuildListbox();
                }

            });
            userPaging.setTotalSize(totalSize);
            int fromint = (userPaging.getActivePage())* userPaging.getPageSize();
            int toint = (userPaging.getActivePage() + 1)* userPaging.getPageSize();
            if (totalSize < toint)
                toint = totalSize;
            resUsers = usersManager.seachUserByPage1(name.getText(),
                    organizationId.getValue() == null
                            || organizationId.getValue().equals("") ? null
                            : oilist,
                    loginName.getValue(), fromint, userPaging.getPageSize());
            rebuildListbox();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.userManager") + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
	 * 加载查询的用户信息
     * @throws DataAccessException 
	 */
    public void rebuildListbox() throws DataAccessException
    {
        List<UsersEx> usersList = fullUserEx(resUsers);
        resUsers = usersList;
        for (UsersEx ex : (List<UsersEx>) resUsers)
        {
            try
            {
                ex.setRoseSet(userroleManager.getRoleByUser(ex.getUserId()));
            } catch (DataAccessException e)
            {
                e.printStackTrace();
            }
        }
        List children = usersData.getItems();
        int len = children.size();
        Object object = null;
        Listitem lt = null;
        for (int j = 0; j < len; j++)
        {
            object = children.get(0);
            if (object instanceof Listitem)
            {
                lt = (Listitem) object;
                usersData.removeChild(lt);
            }
        }
        Listitem item = null;
        Listcell cellNum = null;
        Listcell cell0 = null;
        Listcell cell = null;
        Listcell cell2 = null;
        Listcell cell3 = null;
        Listcell cell4 = null;
        Listcell cell5 = null;
        Listcell cell6 = null;
//        Listcell cell7 = null;
        Image edit = null;
        // Toolbarbutton toolbarbutton2 = null;
        Image del = null;
        Toolbarbutton view = null;
        Space space = null;
        
        for (int i = 0; i < usersList.size(); i++)
        {
            UsersEx userd = usersList.get(i);
            item = new Listitem();
            item.setValue(userd);
            cell0 = new Listcell();
            cellNum = new Listcell();
            cell = new Listcell();
            cell.setLabel(userd.getName());
            cell2 = new Listcell();
            
            List olist= organizationManager.getOrgById(userd.getOrganizationId());
            Organization o=(Organization) olist.get(0);
            if(o.getParentId()!=-1)
            {
            	List orglist= organizationManager.getOrgById(o.getParentId());
            	organi=(Organization) orglist.get(0);
            	 cell2.setLabel(organi.getName()+"---"+userd.getOrganizationName());
            }
            else
            {
            cell2.setLabel(userd.getOrganizationName());
            }
            cell3 = new Listcell();
            if(userd.getIsLocked().equals("2"))
            {
            	cell3.setLabel("无效");
            }
            else
            {
            	 cell3.setLabel(userd.getIsLocked().equals("0")? org.zkoss.util.resource.Labels
                         .getLabel("system.commom.ui.normal")
                         : org.zkoss.util.resource.Labels
                                 .getLabel("users.ui.locking"));
            }
           
            cell4 = new Listcell();
            cell4.setLabel(userd.getLoginName());
            cell5 = new Listcell();
            cell6 = new Listcell();
//            cell7 = new Listcell();
//            if (Sessions.getCurrent().getAttribute("themeName").toString().equals("defult"))
//            {
//                cell.setSclass("r-listitem-bor1");
//                cell0.setSclass("r-listitem-bor1");
//                cell3.setSclass("r-listitem-bor1");
//                cell2.setSclass("r-listitem-bor1");
//                cell4.setSclass("r-listitem-bor1");
//                cell5.setSclass("r-listitem-bor1");
//                cell6.setSclass("r-listitem-bor1");
////                cell7.setSclass("r-listitem-bor1");
//                cellNum.setSclass("r-listitem-bor1");
//            }
            Checkbox cbo = new Checkbox();
            if (userd.getRoseSet().size() == 0)
            {
                cell5.setLabel(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.noauthorizerole"));
            }
//            for (Role r : userd.getRoseSet())
//            {
//                Toolbarbutton roleBtn = new Toolbarbutton();
//                roleBtn.setLabel(r.getName());
//                roleBtn.setAttribute("roleId", r.getRoleId());
//                roleBtn.addEventListener("onClick", new EventListener()
//                {
//                    public void onEvent(Event event) throws Exception
//                    {
//                        UsersListWin.this.showResourceTree(Integer
//                                .parseInt(((Toolbarbutton) event.getTarget())
//                                        .getAttribute("roleId").toString()));
//                    }
//                });
//                Space spaceRole = new Space();
//                cell5.appendChild(roleBtn);
//                cell5.appendChild(spaceRole);
//            }
            
//            Vbox vb1=new Vbox();
            for (Role r : userd.getRoseSet())
              {
            	Label rl=new Label(r.getName());
            	 Space spaceRole = new Space();
               cell5.appendChild(rl);
               cell5.appendChild(spaceRole);
        }
    		Hbox hb=new Hbox(); 
    		cell6.appendChild(hb);  
            edit = new Image();
            edit.setType("edit");
            edit.addEventListener("onClick", new EventListener()
            {
                public void onEvent(Event event) throws Exception
                {
                    UsersListWin.this.updataUser(event);
                }
            });
            del = new Image();
            del.setType("delList");
            del.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event event) throws Exception
                {
                    UsersListWin.this.deleteUser(event);
                }
            });
            
            view =new Toolbarbutton();
            view.setImage("/images/content/19.gif");
            view.setTooltiptext("查看任务分配情况");
            view.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event event) throws Exception
                {
                    UsersListWin.this.viewUser(event);
                }
            });
            hb.appendChild(edit);
//            hb.appendChild(del);
            
            space = new Space();
//            cell6.appendChild(edit);
            // cell6.appendChild(space);
            // cell6.appendChild(toolbarbutton2);
            if (userd.getIsLocked().trim().equals("1"))
            {
            	hb.appendChild(new Label());
            } else
            {
            	hb.appendChild(del);
            }
            hb.appendChild(view);
//            vb1.appendChild(hb);
            cellNum.setLabel(((i + 1)+ (userPaging.getActivePage() * userPaging.getPageSize()) + ""));
            item.appendChild(cell0);
            item.appendChild(cellNum);
            item.appendChild(cell);
            item.appendChild(cell2);
            item.appendChild(cell3);
            item.appendChild(cell4);
            item.appendChild(cell5);
            if (null!=ulopMap.get("EDIT")&&Boolean.parseBoolean(ulopMap.get("EDIT").toString()))
            {
                item.appendChild(cell6);
            }
            if (null!=ulopMap.get("EDIT")&&Boolean.parseBoolean(ulopMap.get("EDIT").toString()))
            {
                item.appendChild(cell6);
            }
            if (selectId == userd.getUserId())
            {
                item.setSelected(true);
            }
            usersData.appendChild(item);
        }
    }

    public void closeAddWindow()
    {
        Iterator iter = this.getDesktop().getPages().iterator();
    }
    /**
	 * 删除用户
	 * 
	 * @param event
	 */
    public void deleteUser(Event event)
    {
        try
        {
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("users.ui.isconfirmdeleteuser"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Listitem item = (Listitem) event.getTarget().getParent().getParent().getParent();
        UsersEx user = (UsersEx) item.getValue();
        Users userDel = new Users();
        userDel.setUserId(user.getUserId());
        try
        {
            userroleManager.updateUserRole(null, userDel.getUserId());
            usersManager.remove(userDel);
            usersData.removeChild(item);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.usermanagerdelete")
                    + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //查看用户分配到的任务
    public void viewUser(Event event)
    {

        Map map = new HashMap();
        map.put("user", (UsersEx)((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
        usersData.setSelectedItem(((Listitem) (event.getTarget().getParent().getParent().getParent())));
        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents("/apps/core/usersTask.zul", this, map);
        try
        {
        	
            win.setClosable(true);
            win.doModal();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.usermanageropen")
                    + e.getMessage());
            e.printStackTrace();
        }

    
    }
    /**
	 * 调用更新用户页面
	 * 
	 * @param event
	 */
    public void updataUser(Event event)
    {
        Map map = new HashMap();
        map.put("user", (UsersEx)((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
        usersData.setSelectedItem(((Listitem) (event.getTarget().getParent().getParent().getParent())));
        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents("/apps/core/usersUpdata.zul", this, map);
        try
        {
        	
            win.setClosable(true);
            win.doModal();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.usermanageropen")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
	 * 调用添加新用户页面
	 * 
	 * @param event
	 */
    public void addUser(Event event)
    {
    	UsersAddWin win = (UsersAddWin) Executions.createComponents("/apps/core/usersAdd.zul", null, null);
        try
        {
            win.setClosable(true);
            win.doModal();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + ""
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.usermanageropenaddpage")
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public List getResUsers()
    {
        return resUsers;
    }

    public void setResUsers(List resUsers)
    {
        this.resUsers = resUsers;
    }

    public void afterCompose()
    {
        organizationId = (Bandbox) this.getFellow("organizationId");
        userPaging = (Paging) this.getFellow("userPaging");
        name = (Textbox) this.getFellow("name");
        loginName = (Textbox) this.getFellow("loginName");
        usersData = (Listbox) this.getFellow("usersData");
        parentData = (OrganizationTree) this.getFellow("parentData");
        this.parentData.setLabelProvider(new OrganizationLabelProvider());
        this.parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        this.parentData.rebuildTree();
        fullBandBox();
        this.getDesktop().setAttribute("UsersListWin", this);
        searchUsers();
    }

    /**
	 * 加载组织信息
	 */
    public void fullBandBox()
    {
        Iterator children = parentData.getItems().iterator();
        Treeitem treeItem = null;
        Organization re = null;
        Object obj = null;
        while (children.hasNext())
        {
            obj = children.next();
            treeItem = (Treeitem) obj;
            treeItem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            UsersListWin.this.setOrganizationId();
                        }
                    });
            re = (Organization) treeItem.getValue();
            if (re.getOrganizationId() == 0)
            {
                treeItem.setSelected(true);
            }

        }
    }

    /**
	 * 将选择的组织添加到文本框中
	 */
	public void setOrganizationId() {
		selectedOrganization = (Organization) parentData.getSelectedItem()
				.getValue();
		organizationId.setValue(selectedOrganization.getName());
		organizationId.close();

	}

	/**
	 * 打开资源树
	 * 
	 * @param roleId
	 */
    public void showResourceTree(int roleId)
    {
        Role roleS = new Role();
        roleS.setRoleId(roleId);
        Map map = new HashMap();
        map.put("role", roleS);
        RoleTreeWin resourceT = (RoleTreeWin) Executions.createComponents(
                "/apps/core/roleTree.zul", this, map);
        resourceT.setClosable(true);
        resourceT.setPosition("center");
        try
        {
            resourceT.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
	 * 删除选中的用户
	 */
    public void deleteCheckedUsers()
    {
        try
        {
            Set items = usersData.getSelectedItems();
            if (items.size() == 0)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.selectusers"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("users.ui.isconfirmdelete"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }

            String ids = "";
            for (Listitem item : (Set<Listitem>) items)
            {
                UsersEx user = (UsersEx) item.getValue();
                if (user != null)
                {
                    if (user.getIsLocked().trim().equals("2"))
                    {
                        Messagebox.show("admin用户不允许删除！");
                        return;
                    }
                    ids += user.getUserId() + ",";
                }

            }
            usersManager.deleteUserByIds(ids.substring(0, ids.length() - 1));

            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.searchUsers();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
            }
        }
    }

}
