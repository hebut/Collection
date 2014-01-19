package com.uniwin.webkey.system.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class LeftPageContentProvider implements TreeitemlContentProvider
{
    private List<Resource> resourceList = FrameCommonDate.getResourceList();

    private Object         root;

    private List<Treerow>  treeRowList  = new ArrayList<Treerow>();

    public LeftPageContentProvider(Object root)
    {
        this.root = root;
    }

    public List<Treeitem> getChildrenTreeitems(Object parent)
    {
        List<Treeitem> items = new ArrayList<Treeitem>();
        Resource res = (Resource) parent;
        try
        {
            Treeitem item = null;
            Treerow treerow = null;
            Treecell cell = null;

            for (final Resource resource : resourceList)
            {
                if (res.getId().intValue() != resource.getParentId().intValue())
                {
                   continue;
                }
                treerow = new Treerow();
                if (resource.getUrl() != null
                        && !resource.getUrl().trim().equals(""))
                {
                    treerow.addEventListener("onClick", new EventListener()
                    {

                        public void onEvent(Event arg0) throws Exception
                        {
                            Executions.getCurrent().getDesktop().getSession()
                                    .setAttribute("currResource", resource);
                            ((WorkbenchWin) Executions.getCurrent()
                                    .getDesktop().getAttribute("WorkbenchWin"))
                                    .addTab(resource);
                        }
                    });
                }
                treeRowList.add(treerow);
                item = new Treeitem();
                cell = new Treecell();
                cell.setLabel(resource.getName());
                cell.setImage(resource.getInactiveImageurl());
                cell.setHoverImage(resource.getActiveImageurl());
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

    public Treeitem getRoot()
    {
        Treeitem item = new Treeitem();
        try
        {
            Treecell cell = new Treecell();
            Treerow treerow = new Treerow();
            treerow.appendChild(cell);
            final Resource res = (Resource) this.root;
            if (res.getUrl() != null && !res.getUrl().trim().equals(""))
            {
                treerow.addEventListener("onClick", new EventListener()
                {

                    public void onEvent(Event arg0) throws Exception
                    {
                        Executions.getCurrent().getDesktop().getSession()
                                .setAttribute("currResource", res);
                        ((WorkbenchWin) Executions.getCurrent().getDesktop()
                                .getAttribute("WorkbenchWin")).addTab(res);
                    }
                });
            }
            item.appendChild(treerow);
            cell.setLabel(res.getName());
            item.setValue(res);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return item;
    }

}
