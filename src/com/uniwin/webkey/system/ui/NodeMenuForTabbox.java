package com.uniwin.webkey.system.ui;

import java.util.List;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

/**
 * nodeservice.com
 * 
 * @author tong_linzhi
 * 
 */
public abstract class NodeMenuForTabbox extends org.zkoss.zul.Tabbox implements
        AfterCompose
{

    private ContentProvider contentProvider = null;

    private LabelProvider   labelProvider   = null;

    public ContentProvider getContentProvider()
    {
        return contentProvider;
    }

    public void setContentProvider(ContentProvider contentProvider)
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

    /**
     * 初始化提供数据源
     */
    public void afterCompose()
    {
        initContentProvider();
        initLabelProvider();
        rebuildMenu();
    }

    public void rebuildMenu()
    {
        if (contentProvider != null)
        {
            createRoot(contentProvider.getRoot());
        }
    }

    protected abstract void initContentProvider();

    protected abstract void initLabelProvider();

    /**
     * 创建根节点
     * 
     * @param root
     */
    private void createRoot(Object root)
    {
        List models = contentProvider.getChildren(root);
        if (models != null && models.size() == 0)
        {
            return;
        }
        Tabs tabs = new Tabs();
        Tabpanels tabpanels = new Tabpanels();
        for (int i = 0; i < models.size(); i++)
        {
            Object model = models.get(i);
            Tab tab = new Tab();
            tab.setAttribute("value", model);
            tab.setLabel(labelProvider.getLabel(model));
            tab.setImage(labelProvider.getImage(model));
            tab.setHoverImage(labelProvider.getHoverImage(model));
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setVisible(createChild(model, tabpanel));
            tabs.appendChild(tab);
            tabpanels.appendChild(tabpanel);
        }
        this.appendChild(tabs);
        this.appendChild(tabpanels);
    }

    public boolean createChild(Object parent, Tabpanel tabpanel)
    {
        Tree tree = new Tree();
        if (createTreeRoot(parent, tree))
        {
            tabpanel.appendChild(tree);
            return true;
        }
        return false;

    }

    /**
     * 创建树的根节点
     * 
     * @param root
     * @param tree
     * @return
     */
    public boolean createTreeRoot(Object root, Tree tree)
    {
        Treechildren treechildren = new Treechildren();
        Treeitem item = new Treeitem();
        Treerow row = new Treerow();
        Treecell cell = new Treecell();
        cell.setLabel(labelProvider.getLabel(root));
        cell.setImage(labelProvider.getImage(root));
        cell.setHoverImage(labelProvider.getHoverImage(root));
        row.appendChild(cell);
        item.appendChild(row);
        item.setValue(root);
        item.setVisible(false);
        if (createTreeChild(root, item))
        {
            treechildren.appendChild(item);
            tree.appendChild(treechildren);
            return true;
        }
        return false;
    }

    /**
     * 创建树的子节点
     * 
     * @param parent
     * @param parentItem
     * @return
     */
    private boolean createTreeChild(Object parent, Treeitem parentItem)
    {
        List models = contentProvider.getChildren(parent);
        if (models != null && models.size() == 0)
        {
            return false;
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
            cell.setHoverImage(labelProvider.getHoverImage(model));
            row.appendChild(cell);
            item.appendChild(row);
            item.setValue(model);
            treeChildren.appendChild(item);
            createTreeChild(model, item);
        }
        parentItem.appendChild(treeChildren);
        return true;
    }
}
