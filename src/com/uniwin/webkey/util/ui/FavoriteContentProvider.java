package com.uniwin.webkey.util.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.TreeitemlContentProvider;

public class FavoriteContentProvider implements TreeitemlContentProvider
{
    private List<Resource> resourceList = FrameCommonDate.getResourceList();

    private Object         root;

    private List<Checkbox> checkList    = new ArrayList<Checkbox>();

    public FavoriteContentProvider(Object root)
    {
        this.root = root;
    }

    /**
     * 根据父节点获得Treeitem子节点的集合
     */
    public List<Treeitem> getChildrenTreeitems(Object parent)
    {
        List<Treeitem> items = new ArrayList<Treeitem>();
        Resource res = (Resource) parent;
        try
        {
            Treeitem item = null;
            Treerow treerow = null;
            Treecell cell = null;
            Checkbox checkbox = null;
            for (final Resource resource : resourceList)
            {
                if (res.getId().intValue() != resource.getParentId().intValue())
                {
                    continue;
                }
                item = new Treeitem();
                treerow = new Treerow();
                cell = new Treecell();
                cell.setLabel(resource.getName());

                if (resource.getResourceType() != null
                        && !resource.getResourceType().trim().equals("03"))
                {
                    checkbox = new Checkbox();
                    checkbox.setId(resource.getId().intValue() + "");
                    checkbox.setLabel(org.zkoss.util.resource.Labels
                            .getLabel("favorite.ui.join"));
                    checkbox.addEventListener("onClick", new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                        }
                    });
                    checkList.add(checkbox);
                    cell.appendChild(checkbox);
                }
                treerow.appendChild(cell);
                item.appendChild(treerow);
                item.setValue(resource);
                items.add(item);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 获得跟节点
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
            Resource res = (Resource) this.root;
            cell.setLabel(res.getName());
            item.setValue(res);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return item;
    }

    public List<Checkbox> getCheckList()
    {
        return checkList;
    }

}
