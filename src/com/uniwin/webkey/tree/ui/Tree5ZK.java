package com.uniwin.webkey.tree.ui;

import java.util.List;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.system.ui.ContentProvider1;
import com.uniwin.webkey.system.ui.LabelProvider;

public abstract class Tree5ZK extends Tree implements AfterCompose
{

    private ContentProvider1 contentProvider = null;

    private LabelProvider    labelProvider   = null;

    public ContentProvider1 getContentProvider()
    {
        return contentProvider;
    }

    public void setContentProvider(ContentProvider1 contentProvider)
    {
        this.contentProvider = contentProvider;
    }

    public LabelProvider getLabelProvider()
    {
        return labelProvider;
    }

    public void setLabelProvider(LabelProvider labelProvider)
    {
        this.labelProvider = labelProvider;
    }

    public void afterCompose()
    {
        initContentProvider();
        initLabelProvider();
        rebuildTree();
    }

    public void rebuildTree()
    {
        if (contentProvider != null)
        {
            createRoot(contentProvider.getRoot());
        }
    }

    protected abstract void initContentProvider();

    protected abstract void initLabelProvider();

    private void createRoot(List root)
    {
        Treechildren treechildren = new Treechildren();
        for (int i = 0; i < root.size(); i++)
        {
            Object role = root.get(i);
            Treeitem item = new Treeitem();
            Treerow row = new Treerow();
            Treecell cell = new Treecell();
            cell.setLabel(labelProvider.getLabel(role));
            cell.setImage(labelProvider.getImage(role));
            row.appendChild(cell);
            item.appendChild(row);
            item.setValue(role);
            createChild(role, item);
            treechildren.appendChild(item);
        }
        this.appendChild(treechildren);
    }

    private void createChild(Object parent, Treeitem parentItem)
    {
        List models = contentProvider.getChildren(parent);
        if (models != null && models.size() == 0)
        {
            return;
        }
        Treechildren treeChildren = new Treechildren();
        for (int i = 0; i < models.size(); i++)
        {
            Object model = models.get(i);
            Treeitem item = new Treeitem();
            Treerow row = new Treerow();
            Treecell cell = new Treecell();
            cell.setLabel(labelProvider.getLabel(model));
            cell.setImage(labelProvider.getImage(model));
            row.appendChild(cell);
            item.appendChild(row);
            item.setValue(model);
            treeChildren.appendChild(item);
            createChild(model, item);
        }
        parentItem.appendChild(treeChildren);

    }
}
