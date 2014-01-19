package com.uniwin.webkey.system.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;

public class WorkbenchWin extends Tabbox implements AfterCompose
{
    private IOperationManager  operationManager  = (IOperationManager) SpringUtil.getBean("operationManager");
    private IAuthManager       authManager       = (IAuthManager) SpringUtil.getBean("authManager");
  //维护页面中的标签页组件 2010-12-30
	Tabbox workbench;  
	Tabs indextab;
	
    public void afterCompose()
    {
        this.getDesktop().setAttribute("WorkbenchWin", this);
        Users users = (Users)Sessions.getCurrent().getAttribute("users");
     
        WkTWebsite website = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");
        List<Resource> rsList = (List<Resource>)Sessions.getCurrent().getAttribute("rsList");
        try
        {
            initOp(users,website,rsList);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        if (Sessions.getCurrent().getAttribute("themeName").toString().equals(
                "defult"))
        {
//            this.getTabs().setZclass("r-tabs");
//            this.getTabpanels().setSclass("r-tabpanels");
//            ((Tab) this.getFellow("-1")).setZclass("r-tab");
//            ((Tabpanel) this.getFellow("welcome_tab")).setSclass("r-tabpanel");
        }
        
       
    }

    /**
     * 打开一个新的功能窗口时，在tabs中添加信息的tab
     * 
     * @param resource
     */
    public void addTab(Resource resource)
    {

        if (resource == null)
        {
            return;
        }
        checkPermission(resource);
        boolean isOpen = false;
        Tabs tabs = this.getTabs();

        //2010-12-30 bobo
        Sessions.getCurrent().setAttribute("centerTabbox", tabs.getParent());
        
        List<Tab> tabList = (List<Tab>) tabs.getChildren();        
        Tabpanels tabpanels = this.getTabpanels();
        List<Tabpanel> panelsList = (List<Tabpanel>) tabpanels.getChildren();
        if ("02".equals(resource.getResourceType()))
        {
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
                            String frontUrl = resource.getUrl();
                            if (resource.getUrl().indexOf("#") != -1
                                    && resource.getUrl().indexOf("#") != resource
                                            .getUrl().length() - 1)
                            {
                                a = resource.getUrl().substring(
                                        resource.getUrl().lastIndexOf("#"));
                                frontUrl = resource.getUrl().substring(0,
                                        resource.getUrl().lastIndexOf("#"));
                            }
                            url = frontUrl + "?radom="
                                    + new Date().toLocaleString() + a;
                            iframe.setSrc(url);
                            isOpen = true;
                            Tab tab = tabList.get(i);
                            tab.setSelected(true);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        for (Tab t : (List<Tab>) tabs.getChildren())
        {
            if (resource.getId().toString().equals(t.getId().trim()))
            {
                isOpen = true;
                t.setSelected(true);
                break;
            }
        }
        if (!isOpen)
        {
            Tab tab = new Tab();
            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
//                tab.setZclass("r-tab");
            }
            tab.setAttribute("resource", resource);
            tab.setClosable(true);
            tab.setSelected(true);
            tab.setId(resource.getId() + "");            
            tab.setLabel(resource.getName());
            tab.setAttribute("uri", resource.getUrl());
            tabs.appendChild(tab);
            Tabpanel tabpanel = new Tabpanel();
            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
//                tabpanel.setSclass("r-tabpanel");
            }
            if ("02".equals(resource.getResourceType()))
            {
                tab.setLabel("系统帮助");
                Iframe iframe = new Iframe();
                iframe.setWidth("100%");
                iframe.setHeight("100%");
                iframe.setSrc(resource.getUrl());
                tabpanel.appendChild(iframe);
            } else
            {
                if (resource.getUrl().indexOf(".zul") == -1
                        && !resource.getUrl().substring(
                                resource.getUrl().lastIndexOf(".")).equals(
                                ".zul"))
                {
                    Iframe iframe = new Iframe();
                    iframe.setWidth("100%");
                    iframe.setHeight("100%");
                    iframe.setSrc(resource.getUrl());
                    tabpanel.appendChild(iframe);
                } else
                {
                    try
                    {
//                        Window wind = null;
                        Component com = Executions.getCurrent().createComponents(resource.getUrl(), this.getParent(), null);
//                        if (com instanceof Window)
//                        {
//                            wind = (Window)com;
//                        }
                        tabpanel.appendChild(com);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    
                }
            }
            tabpanel.setId(resource.getId().intValue() + "_window");
            tabpanels.appendChild(tabpanel);
        }
    }

    /**
     * 已经打开的功能页，重新定位
     */
    public void reOpenTab()
    {
        boolean isOpen = false;
        Tabs tabs = this.getTabs();
        Resource resouce = new Resource();
        for (Tab t : (List<Tab>) tabs.getChildren())
        {
            if (t.isSelected())
            {
                resouce.setId(new Integer(t.getId()));
                resouce.setName(t.getLabel());
                resouce.setUrl((String) t.getAttribute("uri"));
                tabs.removeChild(t);
                break;
            }
        }
        Tabpanels tabpanels = this.getTabpanels();
        tabpanels.removeChild(tabpanels.getFellow(resouce.getId().intValue()
                + "_window"));
        addTab(resouce);
        
    }

    public void close(Event event)
    {

    }

    /**
     * 检查许可
     */
    public void checkPermission(Resource resource)
    {
        try
        {
            List<Role> roleList = (List<Role>) Sessions.getCurrent()
                    .getAttribute("rbac_roleList");
            Integer[] roleIds = new Integer[roleList.size()];
            int i = 0;
            Role role = null;
            for (Integer roleId : roleIds)
            {
                role = roleList.get(i);
                roleIds[i] = role.getRoleId();
                i++;
            }
//            List<Permission> permissions = permissionManager
//            .getPermissionsByRoleIdsAndResource(roleIds, resource);
            List<Permission> permissions = new ArrayList<Permission>();
            for (Permission p : permissions)
            {

                String code = p.getCode();
                if (code == null)
                {
                    code = "";
                }
                Sessions.getCurrent().setAttribute(code, true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void connectHelp(Resource resource)
    {
        if (resource == null)
        {
            return;
        }
        boolean isOpen = false;
        Tabs tabs = this.getTabs();

        List<Tab> tabList = (List<Tab>) tabs.getChildren();

        Tabpanels tabpanels = this.getTabpanels();
        List<Tabpanel> panelsList = (List<Tabpanel>) tabpanels.getChildren();

        for (Tab t : (List<Tab>) tabs.getChildren())
        {
            if (Integer.parseInt(t.getId()) == resource.getId())
            {
                isOpen = true;
                t.setSelected(true);
                break;
            }
        }
        
       
        if (!isOpen)
        {

            Tab tab = new Tab();
            tab.setClosable(true);
            tab.setSelected(true);
            tab.setId(resource.getId() + "");
            tab.setLabel(resource.getName());
            checkPermission(resource);
            tab.setAttribute("uri", resource.getUrl());
            tabs.appendChild(tab);
            Tabpanel tabpanel = new Tabpanel();
            if ("02".equals(resource.getResourceType()))
            {
                tab.setLabel("系统帮助");
                Iframe iframe = new Iframe();
                iframe.setWidth("100%");
                iframe.setHeight("100%");
                iframe.setSrc(resource.getUrl());
                tabpanel.appendChild(iframe);
            } else
            {
                if (resource.getUrl().indexOf(".zul") == -1
                        && (resource.getUrl().indexOf(".") != -1 && !resource
                                .getUrl().substring(
                                        resource.getUrl().lastIndexOf("."))
                                .equals(".zul")))
                {
                    Iframe iframe = new Iframe();
                    iframe.setWidth("100%");
                    iframe.setHeight("100%");
                    iframe.setSrc(resource.getUrl());
                    tabpanel.appendChild(iframe);
                } else
                {
                    Window wind = (Window) Executions.getCurrent()
                            .createComponents(resource.getUrl(),
                                    this.getParent(), null);
                    tabpanel.appendChild(wind);
                }
            }
            tabpanel.setId(resource.getId().intValue() + "_window");
            tabpanels.appendChild(tabpanel);

        }
    }

    /**
     * 
     * <li>功能描述：根据当前登录用户,当前管理网站,当前用户在该网站上可以访问的资源,获取在这些资源上的全部有权限的操作。
     * 
     * @param users 当前登录用户对象
     * @param website 当前管理网站对象
     * @param rsList 资源对象集合
     * @throws DataAccessException
     * @throws ObjectNotExistException void
     * @author zr
     */
    private void initOp(Users users,WkTWebsite website,List<Resource> rsList) throws DataAccessException, ObjectNotExistException{
    	//建立资源列表的迭代器
        Iterator rsIt = rsList.iterator();
        Map rsMap = new HashMap();
      //定义一个"当前拥有操作集合"
        List<Operation> opList = new ArrayList<Operation>();
      //定义并获取"全部操作集合"
        List allOpList = operationManager.getAllOperation();
      //开始遍历资源列表
        while (rsIt.hasNext())
        {
        	//定义一个opMap,存储某个资源下的操作和权限标识
            Map opMap = new HashMap();
            Resource resource = (Resource) rsIt.next();
          //将用户id,网站id,资源id传入getResourecOperationByUWR方法
            opList = authManager.getResourecOperationByUWR(users.getUserId(), website.getKwId().intValue(), resource.getId(),0);
          //遍历"全部操作集合"
            for (int i = 0; i < allOpList.size(); i++)
            {
            	//获取"全部操作集合"索引i位置的操作对象
                Operation opAll = (Operation) allOpList.get(i);
              //定义一个标识
                boolean flag = false;
              //遍历"当前拥有操作集合"
                for (int j = 0; j < opList.size(); j++)
                {
                	//获取"全部操作集合"索引i位置的操作对象
                    Operation opHave = (Operation) opList.get(j);
                  //当"当前拥有操作集合"和"全部操作集合"相等时,将flag标识置成true并跳出(遍历"当前拥有操作集合")
                    if (opAll.getOpCode().equals(opHave.getOpCode()))
                    {
                        flag = true;
                        break;
                    }
                }
              //将操作代码和标识存入opMap
                opMap.put(opAll.getOpCode(), flag);
            }
          //向rsMap中插入每个资源的全部有权限的操作
            rsMap.put("rs"+resource.getId(), opMap);
        }
        Sessions.getCurrent().setAttribute("rsopMap", rsMap);
    }
}
