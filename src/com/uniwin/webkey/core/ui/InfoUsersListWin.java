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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
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
import com.uniwin.webkey.core.model.InfoUser;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.UsersEx;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.tree.ui.RoleTreeWin;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class InfoUsersListWin extends Window implements AfterCompose
{
    private IUsersManager    usersManager;                                         // 用户信息管理

    private IUserroleManager userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");

    private Logger           log  = Logger.getLogger(InfoUsersListWin.class);

    private Textbox          name, loginName;                                       //  用户名字；用户登录名字

    private Button           searchUsers;                                          // 页面初始化查询

    private Listbox          usersData;

    public List              resUsers;                                             // 查询返回的结果集

    private OrganizationTree parentData;

    private Bandbox          organizationId;

	private IOrganizationManager organizationManager  ; // 组织管理服务接口
	
    private Organization     selectedOrganization;

    public int               selectId;

    
    private Resource         currResource;
    
    private Map              rsopMap = new HashMap();
    
    private Map              ulopMap = new HashMap();
    
    private Organization organi;
    
    private List<InfoUser>   fuList = null;
    
    
    
    public void afterCompose()
    {
        organizationId = (Bandbox) this.getFellow("organizationId");
        name = (Textbox) this.getFellow("name");
        loginName = (Textbox) this.getFellow("loginName");
        usersData = (Listbox) this.getFellow("usersData");
        parentData = (OrganizationTree) this.getFellow("parentData");
        this.parentData.setLabelProvider(new OrganizationLabelProvider());
        this.parentData.setContentProvider(new OrganizationThirdContentProvider());
        this.parentData.rebuildTree();
        fullBandBox();
        this.getDesktop().setAttribute("InfoUsersListWin", this);
        loadInfoUserList();
    }
    
    
    
    
    public void loadInfoUserList(){
		try {
			final List oilist=new ArrayList();
			if(organizationId.getValue() != null&&!organizationId.getValue().equals(""))
			{
			List olist = organizationManager.getChildrenByParentId(selectedOrganization.getOrganizationId());
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
			
	    	//List<InfoUser> list  = usersManager.findAllInfoUser();
	    	fuList = usersManager.findInfoUserForPage(loginName.getValue(),oilist,name.getValue());
	        reloadList();	
		} catch (DataAccessException e) {
			e.printStackTrace();
		} 
	 	
    }
    
    public void reloadList(){
    	usersData.setModel(new ListModelList(fuList));
    	usersData.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				InfoUser u = (InfoUser)data;
				item.setValue(u);
				Listcell c00 = new Listcell();
				Listcell c0 = new Listcell();
				c0.setLabel((item.getIndex()+1)+"");
				Listcell c1 = new Listcell();
				c1.setLabel(u.getKuLid());
				Listcell c2 = new Listcell();
				c2.setLabel(u.getKuName());
				Listcell c3 = new Listcell();
				c3.setLabel(u.getKdName());
				Listcell c4 = new Listcell();
				c4.setLabel(u.getKuPhone());
				Listcell c5 = new Listcell();
				c5.setLabel(u.getKuEmail());
				Listcell c6 = new Listcell();
				c6.setLabel(u.getKrName());
				Listcell c7 = new Listcell();
	    		Hbox hb=new Hbox(); 
	            Image edit = new Image();
	            edit.setType("edit");
	            edit.addEventListener("onClick", new EventListener()
	            {
	                public void onEvent(Event event) throws Exception
	                {
	                    InfoUsersListWin.this.updataUser(event);
	                }
	            });
	            Image del = new Image();
	            del.setType("delList");
	            del.addEventListener("onClick", new EventListener()
	            {

	                public void onEvent(Event event) throws Exception
	                {
	                    InfoUsersListWin.this.deleteUser(event);
	                }
	            });
	            
	            Toolbarbutton view =new Toolbarbutton();
	            view.setImage("/images/content/19.gif");
	            view.setTooltiptext("查看关注内容");
	            view.addEventListener("onClick", new EventListener()
	            {

	                public void onEvent(Event event) throws Exception
	                {
	                    InfoUsersListWin.this.viewUser(event);
	                }
	            });
	            hb.appendChild(edit);
	            hb.appendChild(del);
	            hb.appendChild(view);
	            c7.appendChild(hb);
				item.appendChild(c00);
				item.appendChild(c0);
				item.appendChild(c1);
				item.appendChild(c2);
				item.appendChild(c3);
				item.appendChild(c4);
				item.appendChild(c5);
				item.appendChild(c6);
				item.appendChild(c7);
			}
		});
    } 
    
    public InfoUsersListWin()
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
        InfoUser infoUser = (InfoUser) item.getValue();
        Users userDel = new Users();
        userDel.setUserId(infoUser.getKuId());
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
        map.put("infoUser", (InfoUser)((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
        usersData.setSelectedItem(((Listitem) (event.getTarget().getParent().getParent().getParent())));
        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents("/apps/core/infoUsersFocus.zul", this, map);
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
        map.put("infoUser", (InfoUser)((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
        usersData.setSelectedItem(((Listitem) (event.getTarget().getParent().getParent().getParent())));
        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents("/apps/core/infoUsersUpdata.zul", this, map);
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
    	InfoUsersAddWin win = (InfoUsersAddWin) Executions.createComponents("/apps/core/InfoUsersAdd.zul", null, null);
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
                            InfoUsersListWin.this.setOrganizationId();
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
                InfoUser user = (InfoUser) item.getValue();
                if (user != null)
                {
                    if (user.getIsLocked().trim().equals("2"))
                    {
                        Messagebox.show("admin用户不允许删除！");
                        return;
                    }
                    ids += user.getKuId() + ",";
                }

            }
            usersManager.deleteUserByIds(ids.substring(0, ids.length() - 1));

            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.loadInfoUserList();
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
