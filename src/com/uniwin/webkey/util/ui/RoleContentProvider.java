package com.uniwin.webkey.util.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.TreeitemlContentProvider;

public class RoleContentProvider implements TreeitemlContentProvider
{
    private IResourceManager resourceManager;

    private Logger           log = Logger.getLogger(RoleContentProvider.class);

    public RoleContentProvider()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");

    }

	/**
	 * 获得根节点
	 */
    public Treeitem getRoot()
    {
        Treeitem item = new Treeitem();
        try
        {
            Treecell cell = new Treecell();
            Treerow treerow = new Treerow();
            treerow.appendChild(cell);
            item.appendChild(treerow);
            Resource res = resourceManager.get(new Integer(0));
            cell.setLabel(res.getName());
            item.setValue(res);
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.resurcetreeroot")
                    + e.getMessage());
            e.printStackTrace();
        }
        return item;
    }

	/**
	 * 根据父节点获得子节点的集合
	 */
    public List<Treeitem> getChildrenTreeitems(Object parent)
    {
        List<Resource> resources = null;
        List<Treeitem> items = new ArrayList<Treeitem>();
        try
        {
            resources = resourceManager
                    .getResourceListByResourceParentId(((Resource) parent)
                            .getId().intValue());
            Treeitem item = null;
            Treerow treerow = null;
            Treecell cell = null;
            Checkbox checkbox = null;
            for (Resource resource : resources)
            {
                item = new Treeitem();

                treerow = new Treerow();
                cell = new Treecell();
                cell.setLabel(resource.getName());
                treerow.appendChild(cell);
                item.appendChild(treerow);
                item.setValue(resource);
                if (Sessions.getCurrent().getAttribute("themeName").toString()
                        .equals("defult"))
                {
                    item.getTreerow().setSclass("treecell");
                    cell.setSclass("treecell");
                }
                items.add(item);
            }

        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.resurcetree")
                    + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

}
