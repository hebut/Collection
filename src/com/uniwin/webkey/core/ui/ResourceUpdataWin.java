package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.listbox.ResourceListbox;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.util.FileTool;


public class ResourceUpdataWin extends Window implements AfterCompose
{
    
	
	
    private static final long  serialVersionUID      = 1L;

    private Textbox            name, description, url, helpUrl; // 资源名称,URL  domain,
    private Combobox           resourceType; // 资源类别
    private Label              url_label;
    private IPermissionManager permissionManager;
    private IOperationManager  operationManager = (IOperationManager) SpringUtil.getBean("operationManager");
    private Resource           resource; // 要更新的资源对象
    private IResourceManager   resourceManager; // 资源管理服务接口
    private Logger             log              = Logger.getLogger(UsersListWin.class); // 日志管理

    private List<Permission>   permissions;

    private List               operationList;

    private String             activeImageurl   = "";

    private String             inactiveImageurl = "";
    
    private ResourceListbox    rSelect;

    private Image              activeImage;

    private Image              inactiveImage;

    private Label              activeLabel;

    private Label              inactiveLabel;

    private Image              upFileActive;

    private Image              upFileInactive;

    private Intbox             weizhi;

    private Map                mOP;
    
//    private Row                selectRow;
    private Listitem selectItem;
    
    public Map getmOP()
    {
        return mOP;
    }

    public void setmOP(Map mOP)
    {
        this.mOP = mOP;
    }

	
    
    public ResourceUpdataWin()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean(
                "resourceManager");
        permissionManager = (IPermissionManager) SpringUtil.getBean(
                "permissionManager");
//        resource = (Resource) Sessions.getCurrent().getAttribute("resource");
        
        mOP = (Map) ((Map) Sessions.getCurrent().getAttribute("rsopMap"))
        .get("rs"
            + ((Resource) Sessions.getCurrent().getAttribute(
            "currResource")).getId());
        resource = (Resource) Sessions.getCurrent().getAttribute("resource");
        activeImageurl = resource.getActiveImageurl();
        inactiveImageurl = resource.getInactiveImageurl();
        
        Sessions.getCurrent().setAttribute("ResourceMap", mOP);
    }

    /**
	 * 添加许可信息
	 */
    public void addPermission()
    {

        Window permissionAdd = (Window) Executions.getCurrent()
                .createComponents("/apps/core/permissionAdd.zul", this,
                        null);
        permissionAdd.setClosable(true);
        try
        {
            permissionAdd.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

	/**
	 * 资源信息更新
	 */
    public void updataResource()
    {
        try
        {
            if (name.getText().trim().equals("")
                    && name.getText().trim().length() <= 255)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.validateResourceName"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (name.getText().trim().length() > 255)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.validateResourceNameLength"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
//            if (resourceType.getSelectedItem() != null
//                    && resourceType.getSelectedItem().getValue().equals("03"))
//            {
//                Resource domainResource = this.resourceManager
//                        .getResourceByDomain(domain.getValue().trim());
//                if (domainResource != null
//                        && (!domainResource.getId().equals(resource.getId())))
//                {
//                	Messagebox.show("您输入的域名已被" + domainResource.getName()
//							+ "使用，不能进行修改。", org.zkoss.util.resource.Labels
//							.getLabel("system.commom.ui.prompt"),
//							Messagebox.OK, Messagebox.EXCLAMATION);
//                    return;
//                }
//            }
            Resource resourceUp = new Resource();
            resourceUp.setId(resource.getId());
//            resourceUp.setParentId(resource.getParentId());
            resourceUp.setName(name.getText());
            resourceUp.setWeizhi(Integer.parseInt(weizhi.getText()));
            resourceUp.setDescription(this.description.getText());
            resourceUp
                    .setResourceType(resourceType.getSelectedItem() != null ? resourceType
                            .getSelectedItem().getValue().toString()
                            : "");// 资源类型
            resourceUp.setLevel(resource.getLevel());
            Resource pResource = (Resource) rSelect.getSelectedItem().getValue();
            resourceUp.setParentId(pResource.getId());
            resourceUp.setUrl(url.getText());
            resourceUp.setActiveImageurl(activeImageurl);
            resourceUp.setInactiveImageurl(inactiveImageurl);
//            resourceUp.setDomain(domain.getValue());
            resourceUp.setHelpUrl(helpUrl.getValue());
            resourceManager.update(resourceUp);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.updatesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            
            rebuilderTree(resourceUp);
            Sessions.getCurrent().setAttribute("resource", resourceUp);
            if (resourceUp.getResourceType().equals("03"))
            {
                resourceManager.updateDomain(resourceUp.getDomain(), resource
                        .getDomain());
            }
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.updatefailed"),
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
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.resourceupdate")
                    + e.getMessage());
            e.printStackTrace();
        }
    }

//    public void treeitemUp1()
//    {
//        ResourceTree tree = ((ResourceListWin) this.getDesktop().getAttribute(
//                "ResourceListWin")).getResourceTree();
//        Treeitem items = tree.getSelectedItem().getParentItem();
//        List children = items.getChildren();
//        Object obj = null;
//        int childrenCount = children.size();
//        for (int i = 0; i < childrenCount; i++)
//        {
//            obj = children.get(i);
//            if (obj instanceof Treechildren)
//            {
//                Treechildren treechildren = (Treechildren) obj;
//                List treechilrenList = treechildren.getChildren();
//                int childrenCount2 = treechilrenList.size();
//                Object obj2 = null;
//                for (int j = 0; j < childrenCount2; j++)
//                {
//                    obj2 = treechilrenList.get(j);
//                }
//
//                items.removeChild((Component) obj);
//                break;
//            }
//        }
//    }
//
//    /**
//	 * 移动资源
//	 */
//    public void treeitemUp()
//    {
//        if (resource.getWeizhi() == 0 || resource.getId().intValue() == 0)
//        {
//            return;
//        }
//        int weizhi = resource.getWeizhi() - 1;
//        resource.setWeizhi(weizhi);
//        List<Resource> resources = ((ResourceListWin) this.getDesktop()
//                .getAttribute("ResourceListWin")).getResources();
//        boolean has = true;
//        for (Resource re : resources)
//        {
//            if (re.getId().intValue() == resource.getId().intValue())
//            {
//                re.setWeizhi(weizhi);
//            }
//            if (re.getParentId().intValue() == resource.getParentId()
//                    .intValue()
//                    && re.getWeizhi().intValue() == weizhi
//                    && re.getId().intValue() != resource.getId().intValue())
//            {
//                re.setWeizhi(re.getWeizhi() + 1);
//                has = false;
//            }
//        }
//        if (has)
//        {
//            resource.setWeizhi(weizhi + 1);
//            return;
//        }
//        ResourceTree tree = ((ResourceListWin) this.getDesktop().getAttribute(
//                "ResourceListWin")).getResourceTree();
//        Treeitem items = tree.getSelectedItem().getParentItem();
//        List<Treeitem> orderTreeitem = new ArrayList<Treeitem>();
//        List children = items.getChildren();
//        Object obj = null;
//        int childrenCount = children.size();
//        for (int i = 0; i < childrenCount; i++)
//        {
//            obj = children.get(i);
//            if (obj instanceof Treechildren)
//            {
//                Treechildren treechildren = (Treechildren) obj;
//                List treechilrenList = treechildren.getChildren();
//                int childrenCount2 = treechilrenList.size();
//                Object obj2 = null;
//                for (int j = 0; j < childrenCount2; j++)
//                {
//                    obj2 = treechilrenList.get(j);
//                    if (obj2 instanceof Treeitem)
//                    {
//                        orderTreeitem.add((Treeitem) obj2);
//                    }
//                }
//                items.removeChild((Component) obj);
//                break;
//            }
//        }
//        Treechildren tchildren = new Treechildren();
//        items.appendChild(tchildren);
//        dataTreeitemOrder(orderTreeitem);
//        for (Treeitem treeitem : orderTreeitem)
//        {
//            tchildren.appendChild(treeitem);
//        }
//    }
//    /**
//	 * 向下移动资源
//	 */
//    public void treeitemDown()
//    {
//        if (resource.getId().intValue() == 0)
//        {
//            return;
//        }
//        int weizhi = resource.getWeizhi() + 1;
//        resource.setWeizhi(weizhi);
//        List<Resource> resources = ((ResourceListWin) this.getDesktop()
//                .getAttribute("ResourceListWin")).getResources();
//        boolean has = true;
//        for (Resource re : resources)
//        {
//            if (re.getId().intValue() == resource.getId().intValue())
//            {
//                re.setWeizhi(weizhi);
//            }
//            if (re.getParentId().intValue() == resource.getParentId()
//                    .intValue()
//                    && re.getWeizhi().intValue() == weizhi
//                    && re.getId().intValue() != resource.getId().intValue())
//            {
//                re.setWeizhi((weizhi - 1));
//                has = false;
//            }
//        }
//        if (has)
//        {
//            resource.setWeizhi(weizhi - 1);
//            return;
//        }
//        ResourceTree tree = ((ResourceListWin) this.getDesktop().getAttribute(
//                "ResourceListWin")).getResourceTree();
//        Treeitem items = tree.getSelectedItem().getParentItem();
//        List<Treeitem> orderTreeitem = new ArrayList<Treeitem>();
//        List children = items.getChildren();
//        Object obj = null;
//        int childrenCount = children.size();
//        for (int i = 0; i < childrenCount; i++)
//        {
//            obj = children.get(i);
//            if (obj instanceof Treechildren)
//            {
//                Treechildren treechildren = (Treechildren) obj;
//                List treechilrenList = treechildren.getChildren();
//                int childrenCount2 = treechilrenList.size();
//                Object obj2 = null;
//                for (int j = 0; j < childrenCount2; j++)
//                {
//                    obj2 = treechilrenList.get(j);
//                    if (obj2 instanceof Treeitem)
//                    {
//                        orderTreeitem.add((Treeitem) obj2);
//                    }
//                }
//                items.removeChild((Component) obj);
//                break;
//            }
//        }
//        Treechildren tchildren = new Treechildren();
//        items.appendChild(tchildren);
//        dataTreeitemOrder(orderTreeitem);
//        for (Treeitem treeitem : orderTreeitem)
//        {
//            tchildren.appendChild(treeitem);
//        }
//
//    }

	/**
	 * 排序资源树
	 * 
	 * @param data
	 *            要排序的树组件
	 */
//    public void dataTreeitemOrder(List<Treeitem> data)
//    {
//        Treeitem reTemp = null;
//        for (int j = 0; j < data.size(); j++)
//        {
//            reTemp = data.get(j);
//            for (int i = j + 1; i < data.size(); i++)
//            {
//                if (((Resource) reTemp.getValue()).getWeizhi().intValue() > ((Resource) data
//                        .get(i).getValue()).getWeizhi())
//                {
//                    reTemp = data.get(i);
//                    data.set(i, data.get(j));
//                    data.set(j, reTemp);
//                }
//            }
//        }
//    }

	/**
	 * 添加下级资源
	 */
    public void resourceAdd()
    {
        ((ResourceListWin) this.getDesktop().getAttribute("ResourceListWint"))
                .resourceAdd();
    }

	/**
	 * 重新显示树
	 */
    public void rebuilderTree(Resource res)
    {
        ((ResourceListWin) this.getDesktop().getAttribute("ResourceListWint"))
                .rebuilderTree(res,3);
    }

    public void afterCompose()
    {

        try
        {
        	selectItem = (Listitem) this.getFellow("selectItem");
            helpUrl = (Textbox) this.getFellow("helpUrl");
            activeImage = (Image) this.getFellow("activeImage");
            inactiveImage = (Image) this.getFellow("inactiveImage");
            activeLabel = (Label) this.getFellow("activeLabel");
            inactiveLabel = (Label) this.getFellow("inactiveLabel");
            upFileActive = (Image) this.getFellow("upFileActive");
            upFileInactive = (Image) this.getFellow("upFileInactive");
            rSelect = (ResourceListbox) this.getFellow("rSelect");
            name = (Textbox) this.getFellow("name");
//            domain = (Textbox) this.getFellow("domain");
            resourceType = (Combobox) this.getFellow("resourceType");
            resource = (Resource) Sessions.getCurrent().getAttribute("resource");
            activeImageurl = resource.getActiveImageurl();
            inactiveImageurl = resource.getInactiveImageurl();
            if (null==resource||!(resource.getId()>0))
            {
            	selectItem.setVisible(false);
            }else{
                rSelect.initResourceSelect(resource, true);
            }
            
            if (resource.getParentId().intValue() != -1)
            {
                Resource pResource = resourceManager
                        .get(resource.getParentId());
                if (resource.getResourceType().equals("00")
                        || resource.getResourceType().equals("01")
                        || resource.getResourceType().equals("02")
                        || resource.getResourceType().equals("04"))
                {
                    for (Comboitem item : (List<Comboitem>) resourceType
                            .getItems())
                    {
                        if (item.getValue().equals("03"))
                        {
                            resourceType.removeChild(item);
                            break;
                        }
                    }
                }
            }
            for (Comboitem item : (List<Comboitem>) resourceType.getItems())
            {
                if (resource.getResourceType().equals(item.getValue()))
                {
                    resourceType.setSelectedItem(item);
                    break;
                }

            }
            weizhi= (Intbox) this.getFellow("weizhi");
            url = (Textbox) this.getFellow("url");
            description = (Textbox) this.getFellow("description");
            url_label = (Label) this.getFellow("url_label");
            List children = resourceType.getChildren();

            for (int j = 0; j < children.size(); j++)
            {
                if (((Comboitem) children.get(j)).getLabel().equals(
                        resource.getResourceType()))
                {
                    resourceType.setSelectedIndex(j);
                    break;
                }

            }
            viewChange();
            if (resource.getParentId() == -1)
            {
//                domain.setReadonly(true);
                resourceType.setDisabled(true);
            } else
            {

//                domain.setReadonly(!resource.getResourceType().equals("03"));
                resourceType.setDisabled(resource.getResourceType()
                        .equals("03"));
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

	/**
	 * 初始化树被选中的节点
	 */
    public void initTreeSelected()
    {
        Iterator children = null;// ((Treechildren)(parentData.getChildren().get(0))).getItems().iterator();
        Treeitem treeItem = null;
        Resource re = null;
        while (children.hasNext())
        {
            treeItem = (Treeitem) children.next();
            re = (Resource) treeItem.getValue();
            if (re.getId().intValue() == this.resource.getId().intValue())
            {
                treeItem.setSelected(true);
                break;
            }
        }

    }

	/**
	 * 根据资源类型构造树菜单
	 */
    public void viewChange()
    {
        String selectValue = resource.getResourceType();
        url_label.setVisible(true);
        url.setVisible(true);
        activeImage.setVisible(true);
        inactiveImage.setVisible(true);
        activeLabel.setVisible(true);
        inactiveLabel.setVisible(true);
        upFileActive.setVisible(true);
        upFileInactive.setVisible(true);
        try
        {
            if (resource.getId() == 0)
            {
                resourceType.getItemAtIndex(0).setVisible(false);
            }
            if (resource.getId() != 0
                    && resource.getResourceType().equals(
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.menu")))
            {
                List chilren = this.resourceManager
                        .getResourceListByResourceParentId(resource.getId()
                                .intValue());
                if (chilren != null && chilren.size() != 0)
                {
                    resourceType.getItemAtIndex(0).setVisible(false);
                }
            }
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

    }

    public void viewChangeByresoucetype()
    {
        String selectValue = (String) resourceType.getSelectedItem().getValue();
        url_label.setVisible(true);
        url.setVisible(true);
        if (!selectValue.equals(resource.getResourceType()))
        {
            url.setValue("");
        }
    }

	/**
	 * 修改许可信息
	 * 
	 * @param event
	 */
    public void updatePermission(Event event)
    {
        Map map = new HashMap();
        map.put("permission", ((Listitem) (event.getTarget().getParent()
                .getParent())).getValue());
        Window permissionUpdata = (Window) Executions.getCurrent()
                .createComponents("/apps/core/permissionUpdata.zul",
                        this, map);
        permissionUpdata.setClosable(true);
        try
        {
            permissionUpdata.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	/**
	 * 删除许可信息
	 * 
	 * @param event事件源
	 */
    public void deletePermission(Event event)
    {
        try
        {
            Permission perMissionDel = (Permission) ((Listitem) (event
                    .getTarget().getParent().getParent())).getValue();
//            List roleList = permissionroleManager
//                    .getRolesByPermissionIdInPermissionRole(perMissionDel
//                            .getKpid());
            List roleList = new ArrayList();
            if (roleList.size() != 0)
            {
                String roleNameString = "";
                for (int i = 0; i < roleList.size(); i++)
                {
                    Role r = (Role) roleList.get(i);
                    if (i == roleList.size() - 1)
                    {
                        roleNameString += r.getName() + "";
                        continue;
                    }
                    roleNameString += r.getName() + ",\t";
                }
                Messagebox.show(perMissionDel.getName()
                        + org.zkoss.util.resource.Labels
                                .getLabel("resource.ui.was")
                        + "”"
                        + roleNameString
                        + "“"
                        + org.zkoss.util.resource.Labels
                                .getLabel("resource.ui.rolevalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);

                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("resource.ui.isconfirmdeletelicense"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }
//            permissionManager.removePermission(perMissionDel);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            rebuilderTree(resource);
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
            e.printStackTrace();
        }
    }

    public List<Permission> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions)
    {
        this.permissions = permissions;
    }

    public Resource getResource()
    {
        return resource;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

	/**
	 * 验证URL是否存在
	 * 
	 * @param urlStr
	 * @return 是否存在
	 * @throws Exception
	 */
    public boolean checkUrl(String urlStr, int updateResourceId)
            throws Exception
    {
        List resourceList = resourceManager.getResourceByUrl(urlStr);
        if (resourceList.size() != 0)
        {
            if (resourceList.size() == 1
                    && ((Resource) resourceList.get(0)).getId() == updateResourceId)
                return false;
         // 提示
            String message = "";
            for (Resource r : (List<Resource>) resourceList)
            {
                if (r.getId() != updateResourceId)
                {
                    message += r.getName();
                }
            }
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("resource.ui.resourceURL")
                    + "'"
                    + message
                    + "'"
                    + org.zkoss.util.resource.Labels
                            .getLabel("resource.ui.validateURL"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.EXCLAMATION);
            return true;
        }
        return false;
    }

	/**
	 * 删除
	 */
    public void resourceDelete()
    {
        ((ResourceListWin) this.getDesktop().getAttribute("ResourceListWint"))
                .resourceDelete();
    }


	/**
	 * 上传图片
	 * 
	 * @param imageType标示
	 */
    public void addImage(String imageType)
    {
        String fileName = "";
        try
        {
            Media media = Fileupload.get();
            if (media != null)
            {
                fileName = media.getName();

                String fileT[] = fileName.split(".");
                fileName = fileName.substring(0, fileName.indexOf('.'))
                        + new Date().getTime()
                        + fileName.substring(fileName.indexOf('.'), fileName
                                .length());

                String filePath = Executions.getCurrent().getDesktop()
                        .getWebApp().getRealPath("images/system")
                        + "/" + fileName;
                File file = new File(filePath);
                if (!file.exists())
                {
                    file.createNewFile();
                }
                FileTool.writeToFile(media.getStreamData(), file);
                if ("activeImageurl".equals(imageType))
                {
                    activeImage.setContent((AImage) media);
                    activeImageurl = fileName.length() == 0 ? fileName
                            : "/images/system/" + fileName;
                }
                if ("inactiveImageurl".equals(imageType))
                {
                    inactiveImage.setContent((AImage) media);
                    inactiveImageurl = fileName.length() == 0 ? fileName
                            : "/images/system/" + fileName;
                }
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.uploadsuccess"));
            }
        } catch (Exception e)
        {
            activeImageurl = "";
            inactiveImageurl = "";
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("resource.ui.rootmenumanager")
                    + e.getMessage());
            e.printStackTrace();
        }
    }


	/**
	 * 打开角色列表
	 * 
	 * @param event事件源
	 */
    public void openRoleList(Event event)
    {
        try
        {
            Map map = new HashMap();
            Permission permission = (Permission) ((Listitem) (event.getTarget()
                    .getParent().getParent())).getValue();
            map.put("permission", permission);
            Window win = (Window) Executions.getCurrent().createComponents(
                    "apps/core/RoleAddPermission.zul", this, map);
            win.setWidth("400xp");
            win.setClosable(true);
            win.doModal();
        } catch (SuspendNotAllowedException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
	 * 功能：跳入资源授权页面
	 * 
	 */
    public void addAuthorizeResouce(){
        Map map = new HashMap();
        map.put("sourceName", resource.getName());
        map.put("sourceId",resource.getId());
        AuthorizeResourceWindow  win = (AuthorizeResourceWindow)Executions.getCurrent().createComponents("/apps/core/authorizeResource.zul",this,map);
        win.doHighlighted();
        win.setClosable(true);
	}		 
	
}
