package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.image.AImage;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.listbox.ResourceListbox;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.util.FileTool;

public class ResourceAddWin extends Window implements AfterCompose
{
    private Textbox            name, description, url, helpUrl;                        // 资源名称,资源类别,URL，域
                                                                                        // domain,
    private Label              parentData_label, url_label;
    private Combobox           resourceType;                                           // 资源类别
    private List               resourceData;                                           // 资源数据
    private IResourceManager   resourceManager;                                        // 资源管理服务接口
    private Logger             log              = Logger.getLogger(UsersListWin.class); // 日志管理
    // private List treeModel; // 树的数据模型
    private Resource           resourceSession;

    private List<Permission>   permissions;

    private IPermissionManager permissionManager;

    private String             activeImageurl   = "";

    private String             inactiveImageurl = "";

    private Image              activeImage;

    private Image              inactiveImage;

    private Label              activeLabel;

    private Label              inactiveLabel;

    private Image              upFileActive;

    private Image              upFileInactive;

    private Intbox             weizhi;
    
    private ResourceListbox    rSelect;
    
    private Row                selectRow;

    public ResourceAddWin()
    {
        try
        {
            resourceManager = (IResourceManager) SpringUtil.getBean(
                    "resourceManager");
            permissionManager = (IPermissionManager) SpringUtil.getBean(
                    "permissionManager");
            resourceSession = (Resource) Sessions.getCurrent().getAttribute(
                    "resource");
//            permissions = permissionManager
//            .getPermissionByResourceId(resourceSession.getId());
            permissions = new ArrayList();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.initresource")
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
	 * 添加许可信息
	 */
	public void addPermission() {
		Window permissionAdd = (Window) Executions
				.getCurrent()
				.createComponents("../permission/permissionAdd.zul", this, null);
		permissionAdd.setClosable(true);
		try {
			permissionAdd.doModal();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 资源信息添加
	 */
	public void addResource() {
		if (resourceSession.getId() == 0) {
			this.addResourceFirst();
			return;
		}
		this.addResourceSecond();
	}

	/**
	 * 返回
	 */
	public void back() {
		this.rebuilderTree(resourceSession);
	}

	/**
	 * 资源(二级菜单)信息添加
	 */
    public void addResourceSecond()
    {
        try
        {
            if (name.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.validateResourceName"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
         // 验证url
            if (this.resourceType.getText().trim().equals(
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.page"))
                    && checkUrl(url.getText()))
                return;
            Resource resource = new Resource();
            resource.setName(this.name.getText());
//            Integer parentId = resourceSession.getId();
//            resource.setParentId(parentId);
            resource.setUrl(url.getText());
            resource.setResourceType(this.resourceType.getSelectedItem()
                    .getValue()
                    + "");
            Resource pResource = (Resource) rSelect.getSelectedItem().getValue();
            resource.setParentId(pResource.getId());
            resource.setDescription(description.getText());
            resource.setLevel(resourceSession.getLevel() + 1);
            resource.setWeizhi(getWeizhi(resource.getParentId()));
            resource.setActiveImageurl(activeImageurl);
            resource.setInactiveImageurl(inactiveImageurl);
//            resource.setDomain(domain.getText());
            resource.setHelpUrl(helpUrl.getValue());
            resourceManager.add(resource);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.rebuilderTree(resource);
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("resource.ui.addresorceerror")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    public int getWeizhi(int parentId)
    {
        try
        {
            return this.resourceManager.getMaxWeizhi(parentId) + 1;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
	 * 资源(一级菜单)信息添加
	 */
    public void addResourceFirst()
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
//            if (resourceType.getSelectedItem().getValue().equals("03"))
//            {
//                Resource domainResource = this.resourceManager
//                        .getResourceByDomain(domain.getValue().trim());
//                if (domainResource != null)
//                {
//                	Messagebox.show("������������ѱ�" + domainResource.getName()
//							+ "ʹ�ã����ܽ��б��档", org.zkoss.util.resource.Labels
//							.getLabel("system.commom.ui.prompt"),
//							Messagebox.OK, Messagebox.EXCLAMATION);
//                    return;
//                }
//            }
            if (this.resourceType.getText().trim().equals(
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.page"))
                    && checkUrl(url.getText()))
                return;
            Resource resource = new Resource();
            resource.setName(this.name.getText());
            resource.setParentId(0);
            resource.setResourceType(this.resourceType.getSelectedItem()
                    .getValue()
                    + "");
            resource.setDescription(description.getText());
            resource.setLevel(1);
            resource.setWeizhi(getWeizhi(resource.getParentId()));
            resource.setUrl(url.getText());
            resource.setActiveImageurl(activeImageurl);
            resource.setInactiveImageurl(inactiveImageurl);
//            resource.setDomain(domain.getText());// ����ֵ
            resource.setHelpUrl(helpUrl.getValue());
            resourceManager.add(resource);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.rebuilderTree(resource);
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("resource.ui.addresorceerror")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
	 * 重新显示树
	 */
    public void rebuilderTree(Resource res)
    {
        ((ResourceListWin) this.getDesktop().getAttribute("ResourceListWint"))
                .rebuilderTree(res, 2);
    }

    public void viewChange()
    {
        String selectValue = resourceType.getSelectedItem().getLabel();
        url_label.setVisible(true);
        url.setVisible(true);
        activeImage.setVisible(true);
        inactiveImage.setVisible(true);

        activeLabel.setVisible(true);
        inactiveLabel.setVisible(true);
        upFileActive.setVisible(true);
        upFileInactive.setVisible(true);
        // if
        // (org.zkoss.util.resource.Labels.getLabel("system.commom.ui.menu").equals(selectValue))
        // {
        // url_label.setVisible(false);
        // url.setVisible(false);
        // activeImage.setVisible(false);
        // inactiveImage.setVisible(false);
        //			
        // activeLabel.setVisible(false);
        // inactiveLabel.setVisible(false);
        // upFileActive.setVisible(false);
        // upFileInactive.setVisible(false);
        // }
        // if
        // (org.zkoss.util.resource.Labels.getLabel("system.commom.ui.page").equals(selectValue))
        // {
        // url_label.setVisible(true);
        // url.setVisible(true);
        // activeImage.setVisible(true);
        // inactiveImage.setVisible(true);
        //			
        // activeLabel.setVisible(true);
        // inactiveLabel.setVisible(true);
        // upFileActive.setVisible(true);
        // upFileInactive.setVisible(true);
        //
        // }

    }

    public void afterCompose()
    {
        try
        {
            selectRow = (Row) this.getFellow("selectRow");
            rSelect = (ResourceListbox) this.getFellow("rSelect");
            helpUrl = (Textbox) this.getFellow("helpUrl");
            activeImage = (Image) this.getFellow("activeImage");
            inactiveImage = (Image) this.getFellow("inactiveImage");
            activeLabel = (Label) this.getFellow("activeLabel");
            inactiveLabel = (Label) this.getFellow("inactiveLabel");
            upFileActive = (Image) this.getFellow("upFileActive");
            upFileInactive = (Image) this.getFellow("upFileInactive");
            name = (Textbox) this.getFellow("name");
            resourceType = (Combobox) this.getFellow("resourceType");
            url = (Textbox) this.getFellow("url");
            url_label = (Label) this.getFellow("url_label");
            description = (Textbox) this.getFellow("description");
//            domain = (Textbox) this.getFellow("domain");
            resourceType.setSelectedIndex(0);
            boolean isHas = false;
            
            if (null==resourceSession)
            {
                selectRow.setVisible(false);
            }else {
                rSelect.initResourceSelect(resourceSession, false);
            }
            
            for (Comboitem item : (List<Comboitem>) resourceType.getItems())
            {
            	// 如果是二级节点则下拉框不可选，默认为域类型
                if (resourceSession.getParentId() == -1
                        && item.getValue().equals("03"))
                {
                    resourceType.setSelectedItem(item);
                    resourceType.setDisabled(true);
                    isHas = true;
                }
            }
            if (!isHas)
            {
                resourceType.removeItemAt(3);
//                domain.setText(resourceSession.getDomain());
//                domain.setReadonly(true);
            }
            selectKindMenu();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
	 * 默认选择菜单类型
	 */
    public void selectKindMenu()
    {
        resourceSession = (Resource) Sessions.getCurrent().getAttribute(
                "resource");
        String selectValue = resourceSession.getResourceType();
        viewChange();
    }

    public List<Permission> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions)
    {
        this.permissions = permissions;
    }

    /**
	 * 验证URL是否存在
	 * 
	 * @param urlStr
	 * @return 是否存在
	 * @throws Exception
	 */
    public boolean checkUrl(String urlStr) throws Exception
    {
        List resourceList = resourceManager.getResourceByUrl(urlStr);
        if (resourceList.size() != 0)
        {
            //提示
            String message = "";
            for (Resource r : (List<Resource>) resourceList)
            {
                message += r.getName();
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

}
