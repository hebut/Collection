package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.tree.ui.ResourceTree;
import com.uniwin.webkey.util.ui.ResourceLabelProvider;
import com.uniwin.webkey.util.ui.ResourceThirdContentProvider;

public class ResourceListWin extends Window implements AfterCompose
{
    private Center             center;

    private List<Permission>   permissions;

    private Listbox resourceList; //资源信息列表
	private IResourceManager resourceManager; // 资源管理服务接口
	private Logger log = Logger.getLogger(UsersListWin.class); // 日志管理

    private ResourceTree       resourceTree;

    private List<Resource>     resources         = new ArrayList<Resource>();

    private List               operationList;

    private Include            permission_include;

    private IOperationManager  operationManager  = (IOperationManager) SpringUtil
                                                         .getBean("operationManager");

    private IPermissionManager permissionManager = (IPermissionManager) SpringUtil
                                                         .getBean(
                                                                 "permissionManager");

    public ResourceTree getResourceTree()
    {
        return resourceTree;
    }

    public void setResourceTree(ResourceTree resourceTree)
    {
        this.resourceTree = resourceTree;
    }

    private Include  updataPage;

    private Resource selectedResource;

    public ResourceListWin()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean(
                "resourceManager");

    }


    /**
	 * 调用资源信息添加页面
	 * 
	 * @param event
	 */
    public void resourceAdd()
    {
        try
        {
            if (selectedResource.getResourceType().equals(
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.page")))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.nolowerresorce"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        updataPage.setSrc("/apps/core/resourceAdd.zul?radom="
                + new Date().toLocaleString());

    }
 // /**
	// * 隐藏操作按钮
	// */
	// public void hiddenActionButton() {
	// this.addBu.setVisible(false);
	// }

	/**
	 * 调用资源信息修改页面
	 * 
	 * @param event
	 */
    public void resourceUpdata()
    {
        ResourceUpdataWin updata = (ResourceUpdataWin) this.getDesktop()
                .getPage("updata").getFellow("resourceUpdata_wind");
        updata.updataResource();

    }

    /**
	 * 删除资源信息
	 * 
	 * @param event
	 */
    public void resourceDelete()
    {
        try
        {
            Resource resource = (Resource) resourceTree.getSelectedItem()
                    .getValue();
//            Map useInfo = resourceManager.getPermissionInfoByResource(resource
//                    .getId());
            Map useInfo = new HashMap();
            if (useInfo.size() != 0)
            {
                int i = 0;
                String message = org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.toremove")
                        + "'"
                        + resource.getName()
                        + "'"
                        + org.zkoss.util.resource.Labels
                                .getLabel("resource.ui.wasresource") + "'";
                Set keySet = useInfo.keySet();
                Set roleSet = new HashSet();
                for (Permission p : (Set<Permission>) keySet)
                {
                    roleSet.addAll((List) useInfo.get(p));
                }
                for (Role r : (Set<Role>) roleSet)
                {
                    if (i == roleSet.size() - 1)
                    {
                        message += r.getName() + "\t";
                    } else
                    {
                        message += r.getName() + ",\t";
                    }
                    i++;
                }
                message += "'"
                        + org.zkoss.util.resource.Labels
                                .getLabel("resource.ui.notdelete");
                Messagebox.show(message, org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("resource.ui.isconfirmdelete"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }

            if (0 == resource.getId())
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("resource.ui.changeName"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            resourceManager.removeTreeMenu(resource.getId());
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            selectedResource = resourceManager.get(resource.getParentId());
            this.rebuilderTree(selectedResource, 1);
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
                            .getLabel("resource.ui.resourceManager")
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    public void inforView()
    {
        if (resourceTree.getSelectedItem() == null)
        {
            return;
        }
        Resource re = (Resource) ((Treeitem) resourceTree.getSelectedItem()
                .getParent().getParent()).getValue();

    }


    /**
	 * 根据选择菜单显示菜单相关内容
	 */
    public void viewChange(Resource res)
    {
        if (resourceTree.getSelectedItem() == null)
        {
            Sessions.getCurrent().setAttribute("resource", null);
            return;
        }
        center.setTitle(org.zkoss.util.resource.Labels
                .getLabel("resource.ui.operatingresourceName")
                + res.getName());
        selectedResource = res;
        Sessions.getCurrent().setAttribute("resource", selectedResource);
        String selectValue = selectedResource.getResourceType();
        updataPage.setSrc("/apps/core/resourceUpdata.zul?random="
                + new Date().toLocaleString());
        try
        {
            permissions = permissionManager.getPermissionByKridAndKrtype(selectedResource.getId()+"","1");
//            permissions = new ArrayList<Permission>();
            operationList = operationManager
                    .getAllOperationListOrderByOperationName();
//            for (Operation op : (List<Operation>) operationList)
//            {
//                for (Permission per : permissions)
//                {
//                    if (per.getOperationId() == op.getOperationId())
//                    {
//                        per.setOperation(op.getName());
//                    }
//                }
//            }
            ((HttpServletRequest) Executions.getCurrent().getNativeRequest())
                    .setAttribute("permissions", permissions);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        // addBu.setVisible(true);
        permission_include.setSrc("/apps/core/permissionList.zul?random="
                + new Date().toLocaleString());

    }


	/**
	 * 初始化树被选中的节点
	 */
    public void initTreeSelected(Integer id)
    {
        Iterator children = resourceTree.getItems().iterator();
        Treeitem treeItem = null;
        Resource re = null;
        Object obj = null;
        while (children.hasNext())
        {
            obj = children.next();
            treeItem = (Treeitem) obj;
            treeItem.setOpen(true);
//            treeItem.getTreerow().setDraggable("true");
//            treeItem.getTreerow().setDroppable("true");
//            treeItem.getTreerow().addEventListener("onDrop",
//                    new EventListener()
//                    {
//
//                        public void onEvent(Event DropEvent) throws Exception
//                        {
//                            DropEvent event = (DropEvent) DropEvent;
//                            Treerow rowEvent = (Treerow) event.getDragged();
//                            Treeitem itemEvent = (Treeitem) rowEvent.getParent();
//                            Treerow tagget = (Treerow) event.getTarget();
//                            Treeitem itemTagget = (Treeitem) tagget.getParent();
//                            Resource resourceEvent = (Resource) itemEvent
//                                    .getValue();
//                            Resource resourceTagget = (Resource) itemTagget
//                                    .getValue();
//                            if (resourceTagget.getResourceType() == null)
//                            {
//                                return;
//                            }
//                            if (resourceTagget.getResourceType().trim().equals(
//                                    "03")
//                                    && !resourceEvent.getResourceType().trim()
//                                            .equals("03"))
//                            {
//                                return;
//                            }
//                            if (resourceEvent.getResourceType().trim().equals(
//                                    "03")
//                                    && !resourceTagget.getResourceType().trim()
//                                            .equals("03"))
//                            {
//                                return;
//                            }
//                            resourceEvent.setParentId(resourceTagget
//                                    .getParentId());
//                            resourceManager.update(resourceEvent);
//                            Treechildren treechildren = (Treechildren) tagget
//                                    .getParent().getParent();
//                            treechildren.removeChild(itemEvent);
//                            treechildren.insertBefore(itemEvent, itemTagget);
//                            List items = treechildren.getChildren();
//                            for (int i = 0; i < items.size(); i++)
//                            {
//                                Treeitem item = (Treeitem) items.get(i);
//                                Resource resource = (Resource) item.getValue();
//                                resourceManager.updateResourceWeizhi(resource
//                                        .getId(), i);
//                            }
//                        }
//                    });
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
                            ResourceListWin.this
                                    .viewChange(((Resource) ((Treeitem) arg0
                                            .getTarget().getParent())
                                            .getValue()));
                        }
                    });
            re = (Resource) treeItem.getValue();
            if (re.getId().intValue() == id)
            {
                treeItem.setSelected(true);
            }

        }
        // checkHasTreeitem(resourceTree, id);

    }

    /**
	 * 重新展开树
	 * 
	 * @param tree
	 * @param selectId
	 */
    public void initTree()
    {
        Collection<Treeitem> items = (Collection<Treeitem>) resourceTree
                .getItems();
        for (Treeitem item : items)
        {
            item.setOpen(false);
            if (((Resource) item.getValue()).getLevel() == 0)
            {
                item.setOpen(true);
            } else
            {
                item.setOpen(false);
            }
        }
    }

    public void checkHasTreeitem(ResourceTree tree, Integer selectId)
    {
        Collection<Treeitem> items = (Collection<Treeitem>) tree.getItems();
        Treeitem currTreeitem = new Treeitem();
        for (Treeitem item : items)
        {
            item.setOpen(false);
            if (((Resource) item.getValue()).getId().equals(selectId))
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

    public void afterCompose()
    {
        center = (Center) this.getFellow("center");
        permission_include = (Include) this.getFellow("permission_include");
        resourceTree = (ResourceTree) this.getFellow("parentData");
        updataPage = (Include) this.getFellow("updataPage");
        resourceTree.setLabelProvider(new ResourceLabelProvider());
        ResourceThirdContentProvider content = new ResourceThirdContentProvider();
        resourceTree.setContentProvider(content);
        this.resources = content.getResources();
        resourceTree.rebuildTree();
        initTreeSelected(0);
        viewChange((Resource) resourceTree.getSelectedItem().getValue());
        this.getDesktop().setAttribute("ResourceListWint", this);
        initTree();
    }

    /**
	 * 重新加载树
	 * 
	 * @param id
	 */
    public void rebuilderTree(Resource res, int i)
    {
        Treeitem item = resourceTree.getSelectedItem();
        Treeitem itemParent = item.getParentItem();
        if (i == 1)
        {
            item.setParent(null);
            itemParent.setSelected(true);
        } else if(i == 2)
        {
            boolean con = true;
            Treeitem addItem = new Treeitem();
            addItem.setLabel(res.getName());
            Treerow addRow = new Treerow();
            Treecell addCell = new Treecell();
            addCell.setLabel(res.getName());
            addCell.setParent(addRow);
            addItem.setValue(res);

            Treechildren addChd = new Treechildren();
            addItem.setParent(addChd);
            
            List allItems = new ArrayList(resourceTree.getItems());
            for (Object obj : allItems){
                if (obj instanceof Treeitem)
                {
                    Treeitem tempItem = (Treeitem) obj;
                    Resource tempRec = (Resource) tempItem.getValue();
                    if (res.getParentId().equals(tempRec.getId()))
                    {
                        for (Object obj1 : tempItem.getChildren())
                        {
                            if (obj1 instanceof Treechildren)
                            {
                                obj1 = (Treechildren) obj1;
                                ((Treechildren) obj1).appendChild(addItem);
                                con = false;
                            }
                        }
                        if (con)
                        {
                            item.appendChild(addChd);
                        }
                    }
                }
            }
            addItem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            ResourceListWin.this
                                    .viewChange(((Resource) ((Treeitem) arg0
                                            .getTarget().getParent())
                                            .getValue()));
                        }
                    });
            addItem.setSelected(true);
        } else if(i == 3)
        {
            boolean con = true;
            
            List itemList = new ArrayList(resourceTree.getItems());
            for (Object obj : itemList)
            {
                if (obj instanceof Treeitem)
                {
                    Treeitem treeitem = (Treeitem) obj;
                    Resource resource = (Resource) treeitem.getValue();
                    if (res.getId().equals(resource.getId()))
                    {
                        treeitem.setParent(null);
                        Treechildren treechildren = new Treechildren();
                        treeitem.setLabel(res.getName());
                        treeitem.setParent(treechildren);
                        for (Object obj1 : itemList)
                        {
                            if (obj1 instanceof Treeitem)
                            {
                                Treeitem treeitem1 = (Treeitem) obj1;
                                Resource resource1 = (Resource) treeitem1.getValue();
                                if (res.getParentId().equals(resource1.getId()))
                                {
                                    for (Object obj2 : treeitem1.getChildren())
                                    {
                                        if (obj2 instanceof Treechildren)
                                        {
                                            obj2 = (Treechildren) obj2;
                                            ((Treechildren) obj2).appendChild(treeitem);
                                            con = false;
                                        }
                                    }
                                    if (con)
                                    {
                                        treeitem1.appendChild(treechildren);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        viewChange(res);

    }
	
    public void rebuilderTree(Resource res, int i,int k)
    {
        Treeitem item = resourceTree.getSelectedItem();
        Treeitem itemParent = item.getParentItem();
        if (i == 1)
        {
            item.setParent(null);
            itemParent.setSelected(true);
        } else
        {
            boolean con = true;
            Treeitem treeitem = new Treeitem();
            treeitem.setLabel(res.getName());
            Treerow row = new Treerow();
            Treecell cellTreecell = new Treecell();
            cellTreecell.setLabel(res.getName());
            cellTreecell.setParent(row);
            treeitem.setValue(res);
            item.setOpen(true);

            Treechildren children = new Treechildren();
            treeitem.setParent(children);
            for (Object obj : item.getChildren())
            {
                if (obj instanceof Treechildren)
                {
                    obj = (Treechildren) obj;
                    ((Treechildren) obj).appendChild(treeitem);
                    con = false;
                }
            }
            if (con)
            {
                item.appendChild(children);
            }
            treeitem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            ResourceListWin.this
                                    .viewChange(((Resource) ((Treeitem) arg0
                                            .getTarget().getParent())
                                            .getValue()));
                        }
                    });
            treeitem.setSelected(true);

        }
        viewChange(res);

    }
    /**
	 * 重新加载树
	 * 
	 * @param id
	 */
    public void rebuilderTree(final Resource res)
    {
        Treeitem item = this.resourceTree.getSelectedItem();
        Resource resource = (Resource) item.getValue();
        resource = res;
        item.setValue(res);
        item.setLabel(res.getName());
        item.getTreerow().addEventListener("onClick",
                new EventListener()
                {
                    public void onEvent(Event arg0) throws Exception
                    {
                        ResourceListWin.this
                                .viewChange(res);
                    }
                });
        item.setSelected(true);
        viewChange(res);

		// // ����������к��ӽڵ�
		// List children = resourceTree.getChildren();
		// int childrenCount = children.size();
		// Object obj = null;
		// for (int i = 0; i < childrenCount; i++) {
		// obj = children.get(i);
		// if (obj instanceof Treechildren) {
		// resourceTree.removeChild((Component) obj);
		// }
		// }
		// // ��������µ����
		// this.resourceTree.setLabelProvider(new ResourceLabelProvider());
		// this.resourceTree
		// .setContentProvider(new ResourceThirdContentProvider());
		// this.resourceTree.rebuildTree();
		// ѡ��ָ���Ĳ˵�
		// this.initTreeSelected(res.getId());
		// viewChange(res);
    }

    /**
	 * 重新加载树
	 * 
	 * @param id
	 */
	public void rebuilderTree() {
		// 移走树的所有孩子节点
		List children = resourceTree.getChildren();
		int childrenCount = children.size();
		Object obj = null;
		for (int i = 0; i < childrenCount; i++) {
			obj = children.get(i);
			if (obj instanceof Treechildren) {
				resourceTree.removeChild((Component) obj);
			}
		}
		//  给树添加新的数据
		this.resourceTree.setLabelProvider(new ResourceLabelProvider());
		this.resourceTree.setContentProvider(new ResourceThirdContentProvider());
		this.resourceTree.rebuildTree();
		// 初始化选中的菜单
		initTreeSelected(0);
	}

    public List<Resource> getResources()
    {
        return resources;
    }

    public void setResources(List<Resource> resources)
    {
        this.resources = resources;
    }

}
