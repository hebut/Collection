package com.uniwin.webkey.tree.ui;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.uniwin.webkey.system.ui.TreeitemlContentProvider;

public abstract class TreeZK extends Tree implements AfterCompose
{

    private TreeitemlContentProvider contentProvider = null;

    public TreeitemlContentProvider getTreeitemlContentProvider()
    {
        return contentProvider;
    }

    public void setTreeitemlContentProvider(
            TreeitemlContentProvider contentProvider)
    {
        this.contentProvider = contentProvider;
    }

    public void afterCompose()
    {
    }

    public void rebuildTree()
    {
        if (contentProvider != null)
        {
            createRoot();
        }
    }

    private void createRoot()
    {
        Treechildren treechildren = new Treechildren();
        Treeitem item = contentProvider.getRoot();
        createChild(item.getValue(), item);
        item.setVisible(false);
        treechildren.appendChild(item);
        this.appendChild(treechildren);
    }

    private void createChild(Object parent, Treeitem parentItem)
    {
        List<Treeitem> models = contentProvider.getChildrenTreeitems(parent);
        if (models.size() == 0)
        {
            return;
        }
        Treechildren treeChildren = new Treechildren();
        Treeitem model = null;
        for (int i = 0; i < models.size(); i++)
        {
            model = models.get(i);
            treeChildren.appendChild(model);
            createChild(model.getValue(), model);
        }
        if (Sessions.getCurrent().getAttribute("themeName").toString().equals(
                "defult"))
        {
            parentItem.getTreerow().setSclass("treecell");
        }
        parentItem.appendChild(treeChildren);
    }

}
