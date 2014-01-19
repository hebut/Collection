package com.uniwin.webkey.component.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.component.model.TreeModelBean;
import com.uniwin.webkey.component.service.TreeManager;

public class BaseTreeZk extends Tree
{
    private TreeModelBean root;

    private TreeManager   treeManager     = null;

    private Map           idsMap          = new HashMap();

    private boolean       sameManagerBean = false;

    /**
     * 设置跟节点
     */
    public void setRoot(TreeModelBean root, boolean sameManagerBean)
    {
        if (sameManagerBean)
        {
            this.createRoot(root);
        } else
        {
            this.root = root;
            this.addRoot(root);
            String codeData[] = root.getUrl().split("=");
            if (codeData.length == 1)
            {
                return;
            }
            try
            {
                List beans = ((TreeManager) SpringUtil.getBean(root
                        .getBeanName())).getChildren(root.getUrl());
                TreeModelBean temp = null;
                Treeitem itemTemp = null;
                TreeModelBean temp2 = null;
                for (Object object : beans)
                {
                    temp = (TreeModelBean) object;
                    addTreechildren(this.addTreeitemChildren((Treeitem) this
                            .getTreechildren().getFirstChild(), temp), temp);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加根节点
     */
    public void addRoot(TreeModelBean treeModelBean)
    {

        if (this.idsMap.containsKey(treeModelBean.getLabel()))
        {
            return;
        }
        Treechildren treechildren = new Treechildren();
        Treeitem item = new Treeitem();
        idsMap.put(treeModelBean.getLabel(), item);
        Treerow row = new Treerow();
        Treecell cell = new Treecell();
        cell.setLabel(treeModelBean.getLabel());
        row.appendChild(cell);
        item.appendChild(row);
        item.setValue(treeModelBean);
        treechildren.appendChild(item);
        this.appendChild(treechildren);
    }

    /**
     * 在根节点下添加孩子
     */
    public Treeitem addTreeitemChildren(Treeitem item,
            TreeModelBean treeModelBean)
    {
        if (this.idsMap.containsKey(treeModelBean.getLabel()))
        {
            return null;
        }
        Treechildren treechildren = item.getTreechildren() == null ? new Treechildren()
                : item.getTreechildren();
        Treeitem item2 = new Treeitem();
        this.idsMap.put(treeModelBean.getLabel(), item2);
        Treerow row = new Treerow();
        this.addListener(item2, row, treeModelBean);
        Treecell cell = new Treecell();
        cell.setLabel(treeModelBean.getLabel());
        row.appendChild(cell);
        item2.appendChild(row);
        item2.setValue(treeModelBean);
        treechildren.appendChild(item2);
        item.appendChild(treechildren);

        return item2;

    }

    /**
     * 给树上的菜单添加监听
     */
    public void addListener(Treeitem item, Treerow row,
            final TreeModelBean treeModelBean)
    {
        String codeData[] = treeModelBean.getUrl().split("=");
        if (codeData.length == 1)
        {
            return;
        }

        item.addEventListener("onOpen", new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                TreeModelBean temp = (TreeModelBean) ((Treeitem) arg0
                        .getTarget()).getValue();
                BaseTreeZk.this.searchNode((Treeitem) arg0.getTarget(), temp);
            }
        });

        row.addEventListener("onClick", new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                TreeModelBean temp = (TreeModelBean) ((Treeitem) (arg0
                        .getTarget().getParent())).getValue();
                BaseTreeZk.this.searchNode((Treeitem) (arg0.getTarget()
                        .getParent()), temp);
            }
        });
    }

    /**
     * 添加孩子节点
     */
    public void addTreechildren(Treeitem item, TreeModelBean bean)
    {
        String codeData[] = bean.getUrl().split("=");
        if (item == null || codeData.length == 1)
        {
            return;
        }
        try
        {
            List beans = ((TreeManager) SpringUtil.getBean(root.getBeanName())).getChildren(bean.getUrl());// treeManager.getChildren(bean.getUrl());
            if (beans.size() == 0)
            {
                return;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (item.getTreechildren() == null)
        {
            Treechildren children = new Treechildren();
            item.appendChild(children);
        }

    }

    public void searchNode(Treeitem item, TreeModelBean parent)
    {
        try
        {
            String codeData[] = parent.getUrl().split("=");
            if (codeData.length == 1)
            {
                addTreechildren(this.addTreeitemChildren(item, parent), parent);
                return;
            }
            List beans = ((TreeManager) SpringUtil.getBean(root.getBeanName()))
                    .getChildren(parent.getUrl());// treeManager.getChildren(parent.getUrl());
            TreeModelBean temp = null;
            Treeitem itemTemp = null;
            TreeModelBean temp2 = null;
            for (Object object : beans)
            {
                temp = (TreeModelBean) object;
                itemTemp = this.addTreeitemChildren(item, temp);
                addTreechildren(itemTemp, temp);
                if (itemTemp == null)
                {
                    continue;
                }
                String codeData2[] = parent.getUrl().split("=");
                if (codeData.length == 1)
                {
                    continue;
                }
                List beans2 = ((TreeManager) SpringUtil.getBean(root
                        .getBeanName())).getChildren(temp.getUrl());// treeManager.getChildren(temp.getUrl());
                for (Object object2 : beans2)
                {
                    temp2 = (TreeModelBean) object2;
                    addTreechildren(this.addTreeitemChildren(itemTemp, temp2),
                            temp2);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void putKeyword(TreeModelBean treeModelBean, Treeitem item)
    {
        this.idsMap.put(treeModelBean.getLabel(), item);
    }

    public List getLikeword(String keyword)
    {
        Iterator itertor = idsMap.keySet().iterator();
        int wordLen = keyword.length();
        List searchWords = new ArrayList();
        String object = "";
        String temp = "";
        int len = 0;
        while (itertor.hasNext())
        {
            object = (String) itertor.next();
            if (object.length() < wordLen)
            {
                len = object.length();
            } else
            {
                len = wordLen;
            }
            temp = object.substring(0, len);
            if ((keyword.substring(0, len)).equals(temp))
            {
                searchWords.add(object);
            }
        }
        return searchWords;
    }

    public void opentoSelectedNodeName(String nodeName)
    {
        Treeitem treeitem = (Treeitem) idsMap.get(nodeName);
        treeitem.setOpen(true);
        treeitem.setSelected(true);
        Treeitem item = treeitem.getParentItem();
        while (item != null)
        {
            item.setOpen(true);
            item = item.getParentItem();
        }
    }

    /**
     * 创建根节点
     */
    private void createRoot(TreeModelBean treeModelBean)
    {
        this.treeManager = (TreeManager) SpringUtil.getBean(treeModelBean
                .getBeanName());
        Treechildren treechildren = new Treechildren();
        Treeitem item = new Treeitem();
        Treerow row = new Treerow();
        Treecell cell = new Treecell();
        cell.setLabel(treeModelBean.getLabel());
        row.appendChild(cell);
        item.appendChild(row);
        item.setValue(treeModelBean);
        createChild(treeModelBean, item);
        treechildren.appendChild(item);
        idsMap.put(treeModelBean.getLabel(), item);
        this.appendChild(treechildren);
    }

    /**
     * 创建孩子节点
     */
    private void createChild(TreeModelBean parent, Treeitem parentItem)
    {
        List models = null;
        try
        {
            models = treeManager.getChildren(parent.getUrl());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (models != null && models.size() == 0)
        {
            return;
        }
        Treechildren treeChildren = new Treechildren();
        for (int i = 0; i < models.size(); i++)
        {
            TreeModelBean model = (TreeModelBean) models.get(i);
            Treeitem item = new Treeitem();
            Treerow row = new Treerow();
            Treecell cell = new Treecell();
            cell.setLabel(model.getLabel());
            row.appendChild(cell);
            item.appendChild(row);
            item.setValue(model);
            treeChildren.appendChild(item);
            createChild(model, item);
            idsMap.put(model.getLabel(), item);
        }
        parentItem.appendChild(treeChildren);

    }

}
