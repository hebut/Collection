package com.uniwin.webkey.system.ui;

import java.util.List;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public abstract class NodeMenu extends org.zkoss.zul.Menubar implements
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

    public void afterCompose()
    {
        initContentProvider();
        initLabelProvider();
        rebuildMenu();
    }

    /**
     * 创建菜单组件
     */
    public void rebuildMenu()
    {
        if (contentProvider != null)
        {
            createRoot(contentProvider.getRoot(), null);
        }
    }

    protected abstract void initContentProvider();

    protected abstract void initLabelProvider();

    /**
     * 创建根节点
     * 
     * @param root
     * @param menupopup
     */
    private void createRoot(Object root, Menupopup menupopup)
    {
        List models = contentProvider.getChildren(root);
        if (models != null && models.size() == 0)
        {
            return;
        }
        for (int i = 0; i < models.size(); i++)
        {
            Object model = models.get(i);
            Menu menu = new Menu();
            menu.setLabel(labelProvider.getLabel(model));
            menu.setImage(labelProvider.getImage(model));
            menu.setHoverImage(labelProvider.getHoverImage(model));
            createChild(model, menu);
            if (menupopup == null)
            {
                this.appendChild(menu);
            } else
            {
                menupopup.appendChild(menu);
            }
        }
    }

    /**
     * 创建menu
     * 
     * @param parent
     *            父级对象
     * @param menu
     *            要显示在容器的父组件
     */
    public void createChild(Object parent, Menu menu)
    {
        Menupopup menupopup = new Menupopup();
        boolean ishas = createMenuitem(parent, menupopup);
        if (ishas)
        {
            menu.appendChild(menupopup);
        }
    }

    /**
     * 创建Menuitem
     * 
     * @param root
     *           父级对象
     * @param menupopup
     *            要显示在容器的父组件
     * @return
     */
    public boolean createMenuitem(Object root, Menupopup menupopup)
    {
        List models = contentProvider.getChildren(root);
        if (models != null && models.size() == 0)
        {
            return false;
        }
        for (int i = 0; i < models.size(); i++)
        {
            Object model = models.get(i);
            Menuitem menuitem = new Menuitem();
            Menu menu = new Menu();
            menu.setLabel(labelProvider.getLabel(model));
            menu.setImage(labelProvider.getImage(model));
            menu.setHoverImage(labelProvider.getHoverImage(model));
            // here
            menuitem.setLabel(labelProvider.getLabel(model));
            menuitem.setImage(labelProvider.getImage(model));
            menuitem.setHoverImage(labelProvider.getHoverImage(model));
            if (createMenuitemChild(model, menupopup))
            {
                createChild(model, menu);
                menupopup.appendChild(menu);
            } else
            {
                menupopup.appendChild(menuitem);
            }
        }
        return true;
    }

    /**
     * 判断创建menuitem还是menu
     * 
     * @param parent父级对象
     * @param menupopup
     * @return
     */
    private boolean createMenuitemChild(Object parent, Menupopup menupopup)
    {
        List models = contentProvider.getChildren(parent);
        if (models != null && models.size() == 0)
        {
            return false;
        }
        return true;
    }

}
