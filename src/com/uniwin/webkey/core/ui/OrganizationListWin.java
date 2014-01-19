package com.uniwin.webkey.core.ui;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class OrganizationListWin extends Window implements AfterCompose
{
    private Center               center_update;

    private List organizationData; // 组织数据
	private Listbox resourceList; // 组织信息列表
	private IOrganizationManager organizationManager  ; // 组织管理服务接口
	private Logger log = Logger.getLogger(UsersListWin.class); // 日志管理
	private OrganizationTree parentData; // 挂接点（父节点）

    private Include              updataPage;

    private Organization         selectedOrganization;

    public OrganizationListWin()
    {
        try
        {
            organizationManager = (IOrganizationManager) SpringUtil
                    .getBean("organizationManager");
            organizationData = organizationManager
                    .getOrganizationListAllOrderbyOrganizationId();
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("organization.ui.initorgmenage")
                    + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
	 * 调用组织信息添加页面
	 * 
	 * @param event
	 */
    public void resourceAdd()
    {
        updataPage.setSrc("/apps/core/organizationAdd.zul?radom="
                + new Date().toLocaleString());
        // OrganizationListWin
//        center_update.setTitle(org.zkoss.util.resource.Labels
//                .getLabel("system.commom.ui.currently")
//                + "'"
//                + selectedOrganization.getName()
//                + "'"
//                + org.zkoss.util.resource.Labels
//                        .getLabel("organization.zul.addorganization"));
        // hiddenActionButton();

    }

    /**
	 * 调用组织信息修改页面
	 * 
	 * @param event
	 */
    public void resourceUpdata()
    {
        OrganizationUpdataWin updata = (OrganizationUpdataWin) this
                .getDesktop().getPage("updata").getFellow(
                        "organizationUpdata_wind");
        updata.updataResource();

    }

    /**
	 * 删除组织信息
	 * 
	 * @param event
	 */
    public void resourceDelete()
    {
        try
        {
            Organization resource = (Organization) parentData.getSelectedItem()
                    .getValue();
            if (-1 == resource.getParentId())
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("organization.ui.rootOrg"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            List userList = organizationManager.getUserByOrgan(resource
                    .getOrganizationId());
            if (userList.size() != 0)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.toremove")
                        + resource.getName()
                        + org.zkoss.util.resource.Labels
                                .getLabel("organization.ui.lowerOrg")
                        + userList.size()
                        + org.zkoss.util.resource.Labels
                                .getLabel("organization.ui.staff"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("organization.ui.isconfirmdelete"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }
            organizationManager
                    .removeOrganization(resource.getOrganizationId());
        	// 刷新树
			this.rebuilderTree();
			// 重新选择
            this.initTreeSelected(resource.getParentId() == -1 ? 0 : resource
                    .getParentId());
            this.viewChange();
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("organization.ui.deleteOrgmanager")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    public List getOrganizationData()
    {
        return organizationData;
    }

    public void setOrganizationData(List organizationData)
    {
        this.organizationData = organizationData;
    }

    public void inforView()
    {
        if (parentData.getSelectedItem() == null)
        {
            return;
        }
        Resource re = (Resource) ((Treeitem) parentData.getSelectedItem()
                .getParent().getParent()).getValue();

    }

    /**
	 * 根据选择菜单显示菜单相关内容
	 */
    public void viewChange()
    {
        if (parentData.getSelectedItem() == null)
        {
            Sessions.getCurrent().setAttribute("organization", null);
            return;
        }
        selectedOrganization = (Organization) ((Treeitem) (parentData
                .getSelectedItem())).getValue();
        Sessions.getCurrent()
                .setAttribute("organization", selectedOrganization);
        updataPage.setSrc("/apps/core/organizationUpdata.zul?random="
                + new Date().toLocaleString());
//        center_update.setTitle(org.zkoss.util.resource.Labels
//                .getLabel("organization.ui.operatingOrg")
//                + selectedOrganization.getName());
    }

    /**
	 * 初始化树被选中的节点
	 */
    public void initTreeSelected(Integer id)
    {
        Iterator children = parentData.getItems().iterator();
        Treeitem treeItem = null;
        Organization re = null;
        Object obj = null;
        while (children.hasNext())
        {
            obj = children.next();
            treeItem = (Treeitem) obj;
            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
                treeItem.getTreerow().setSclass("treecell");
            }
            treeItem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            OrganizationListWin.this.viewChange();
                        }
                    });
            re = (Organization) treeItem.getValue();
            if (re.getOrganizationId() == id)
            {
                treeItem.setSelected(true);
            }
        }
        // checkHasTreeitem(parentData, id);
    }

    public OrganizationTree getParentData()
    {
        return parentData;
    }

    public void setParentData(OrganizationTree parentData)
    {
        this.parentData = parentData;
    }

    public void afterCompose()
    {
        center_update = (Center) this.getFellow("center_update");
        parentData = (OrganizationTree) this.getFellow("parentData");
        updataPage = (Include) this.getFellow("updataPage");
        parentData.setLabelProvider(new OrganizationLabelProvider());
        parentData.setContentProvider(new OrganizationThirdContentProvider());
        parentData.rebuildTree();
        initTreeSelected(0);
        viewChange();
        this.getDesktop().setAttribute("OrganizationListWin", this);

    }

    /**
	 * 重新加载树
	 * 
	 * @param id
	 */
	public void rebuilderTree(Organization org) {
		// 移走树的所有孩子节点
		List children = parentData.getChildren();
		int childrenCount = children.size();
		Object obj = null;
		for (int i = 0; i < childrenCount; i++) {
			obj = children.get(i);
			if (obj instanceof Treechildren) {
				parentData.removeChild((Component) obj);
			}
		}
		// 给树添加新的数据
		this.parentData.setLabelProvider(new OrganizationLabelProvider());
		this.parentData
				.setContentProvider(new OrganizationThirdContentProvider());
		this.parentData.rebuildTree();
		// 选中指定的菜单
		this.initTreeSelected(org.getOrganizationId());
		viewChange();
	}

	/**
	 * 重新加载树
	 * 
	 * @param id
	 */
	public void rebuilderTree() {
		// 移走树的所有孩子节点
		List children = parentData.getChildren();
		int childrenCount = children.size();
		Object obj = null;
		for (int i = 0; i < childrenCount; i++) {
			obj = children.get(i);
			if (obj instanceof Treechildren) {
				parentData.removeChild((Component) obj);
			}
		}
		// 给树添加新的数据
		this.parentData.setLabelProvider(new OrganizationLabelProvider());
		this.parentData
				.setContentProvider(new OrganizationThirdContentProvider());
		this.parentData.rebuildTree();
		// 初始化选中的菜单
		initTreeSelected(0);
	}
	/**
	 * 重新展开树
	 * @param tree
	 * @param selectId
	 */
    public void checkHasTreeitem(OrganizationTree tree, Integer selectId)
    {
        Collection<Treeitem> items = (Collection<Treeitem>) tree.getItems();
        Treeitem currTreeitem = new Treeitem();
        for (Treeitem item : items)
        {
            item.setOpen(false);
            if (((Organization) item.getValue()).getOrganizationId() == selectId)
            {
                currTreeitem = item;
            }
        }
        while (true)
        {
            if (currTreeitem.getParentItem() != null)
            {
                currTreeitem.getParentItem().setOpen(true);
                currTreeitem = currTreeitem.getParentItem();
            } else
            {
                return;
            }
        }
    }
}
