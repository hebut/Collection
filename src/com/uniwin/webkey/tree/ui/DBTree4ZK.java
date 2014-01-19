package com.uniwin.webkey.tree.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.uniwin.webkey.core.itf.ITreeDao;
import com.uniwin.webkey.system.ui.ContentProvider;
import com.uniwin.webkey.system.ui.LabelProvider;

public abstract class DBTree4ZK extends Tree implements AfterCompose
{
    private String tablename;        // 数据库表名字

    private String parentfield;      // 标示父级的表的字段名字

    private String labelfield;       // 显示文本的字段名字

    private String imagefield;       // 图片的字段名字

    private String hoverimgagefield; // 鼠标悬停时图片的字段名字

    private String pkfield;          // 数据库表的主键字段

    private String startpk;          // 根节点的id

    public String getStartpk()
    {
        return startpk;
    }

    public void setStartpk(String startpk)
    {
        this.startpk = startpk;
    }

    public String getTablename()
    {
        return tablename;
    }

    public void setTablename(String tablename)
    {
        this.tablename = tablename;
    }

    public String getParentfield()
    {
        return parentfield;
    }

    public void setParentfield(String parentfield)
    {
        this.parentfield = parentfield;
    }

    public String getLabelfield()
    {
        return labelfield;
    }

    public void setLabelfield(String labelfield)
    {
        this.labelfield = labelfield;
    }

    public String getImagefield()
    {
        return imagefield;
    }

    public void setImagefield(String imagefield)
    {
        this.imagefield = imagefield;
    }

    public String getHoverimgagefield()
    {
        return hoverimgagefield;
    }

    public void setHoverimgagefield(String hoverimgagefield)
    {
        this.hoverimgagefield = hoverimgagefield;
    }

    public String getPkfield()
    {
        return pkfield;
    }

    public void setPkfield(String pkfield)
    {
        this.pkfield = pkfield;
    }

    private ContentProvider contentProvider = new ContentProvider()
                                            {
                                                private ITreeDao dao = (ITreeDao) SpringUtil
                                                                             .getBean("treeDaoUtil");

                                                public Object getRoot()
                                                {
                                                    String sql = "select "
                                                            + labelfield + " ,"
                                                            + imagefield + " ,"
                                                            + hoverimgagefield
                                                            + "," + pkfield
                                                            + " from "
                                                            + tablename
                                                            + " where "
                                                            + pkfield + "="
                                                            + startpk;
                                                    List list = dao
                                                            .executeSQL(sql);
                                                    if (list == null
                                                            || list.size() == 0)
                                                    {
                                                        return null;
                                                    }
                                                    Map map = (Map) list.get(0);
                                                    return getBean(
                                                            map.get(labelfield)
                                                                    + "",
                                                            map.get(imagefield)
                                                                    + "",
                                                            map
                                                                    .get(hoverimgagefield)
                                                                    + "",
                                                            map.get(pkfield)
                                                                    + "");
                                                }

                                                public Object getParent(
                                                        Object node)
                                                {

                                                    return null;
                                                }

                                                public List getChildren(
                                                        Object parent)
                                                {
                                                    List childrens = new ArrayList();
                                                    String sql = "select "
                                                            + labelfield
                                                            + " ,"
                                                            + imagefield
                                                            + " ,"
                                                            + hoverimgagefield
                                                            + ","
                                                            + pkfield
                                                            + " from "
                                                            + tablename
                                                            + " where "
                                                            + parentfield
                                                            + "="
                                                            + ((TreeBean) parent)
                                                                    .getId();
                                                    List list = dao
                                                            .executeSQL(sql);
                                                    if (list == null
                                                            || list.size() == 0)
                                                    {
                                                        return null;
                                                    }
                                                    for (Map map : (List<Map>) list)
                                                    {
                                                        childrens
                                                                .add(getBean(
                                                                        map
                                                                                .get(labelfield)
                                                                                + "",
                                                                        map
                                                                                .get(imagefield)
                                                                                + "",
                                                                        map
                                                                                .get(hoverimgagefield)
                                                                                + "",
                                                                        map
                                                                                .get(pkfield)
                                                                                + ""));
                                                    }
                                                    return childrens;
                                                }

                                                public TreeBean getBean(
                                                        String name,
                                                        String image,
                                                        String hoverImage,
                                                        String perentId)
                                                {
                                                    return new TreeBean(name,
                                                            image, hoverImage,
                                                            perentId);
                                                }
                                            };

    private LabelProvider   labelProvider   = new LabelProvider()
                                            {

                                                public String getLabel(
                                                        Object obj)
                                                {
                                                    return ((DBTree4ZK.TreeBean) obj)
                                                            .getName();
                                                }

                                                public String getImage(
                                                        Object obj)
                                                {
                                                    return ((DBTree4ZK.TreeBean) obj)
                                                            .getImage();
                                                }

                                                public String getHoverImage(
                                                        Object obj)
                                                {
                                                    return ((DBTree4ZK.TreeBean) obj)
                                                            .getHoverImage();
                                                }
                                            };

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

    private void createRoot(Object root)
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
        createChild(root, item);
        treechildren.appendChild(item);
        this.appendChild(treechildren);
    }

    private void createChild(Object parent, Treeitem parentItem)
    {
        List models = contentProvider.getChildren(parent);
        if (models == null || models.size() == 0)
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
            cell.setHoverImage(labelProvider.getHoverImage(model));
            row.appendChild(cell);
            item.appendChild(row);
            item.setValue(model);
            treeChildren.appendChild(item);
            createChild(model, item);
        }
        parentItem.appendChild(treeChildren);

    }

    public class TreeBean implements LabelProvider
    {
        private String name;

        private String image;

        private String hoverImage;

        private String id;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public TreeBean(String name, String image, String hoverImage, String id)
        {
            this.name = name;
            this.image = image;
            this.hoverImage = hoverImage;
            this.id = id;
        }

        public TreeBean()
        {
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getImage()
        {
            return image;
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        public String getHoverImage()
        {
            return hoverImage;
        }

        public void setHoverImage(String hoverImage)
        {
            this.hoverImage = hoverImage;
        }

        public String getHoverImage(Object obj)
        {
            return this.getHoverImage();
        }

        public String getImage(Object obj)
        {
            return this.getImage();
        }

        public String getLabel(Object obj)
        {
            return this.getName();
        }
    }
}
