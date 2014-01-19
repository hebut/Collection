package com.uniwin.webkey.content.newspub.win;

/**
 * <li>初始化左侧的栏目树
 * @2010-3-16
 * @author whm
 */
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.cms.itf.ChanelService;
import com.uniwin.webkey.cms.itf.NewsService;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTWebsite;

public class NewsTreeModel extends AbstractTreeModel
{

    NewsService   newsService = (NewsService) SpringUtil.getBean("newsService");

    ChanelService chanelService = (ChanelService) SpringUtil.getBean("chanelService");

    public NewsTreeModel(Object root, NewsService newsService,
            ChanelService chanelService)
    {
        super(root);
        this.newsService = newsService;
        this.chanelService = chanelService;

    }

    private static final long serialVersionUID = 5665716328057383820L;

    //有了父节点后，获得孩子节点，index为第几个孩子
    public Object getChild(Object parent, int index)
    {
        if (parent instanceof WkTWebsite)
        {
            WkTWebsite n = (WkTWebsite) parent;
            return chanelService.getChanel(n.getKwId()).get(index);
        } else if (parent instanceof WkTChanel)
        {
            WkTChanel n = (WkTChanel) parent;
            return newsService.getChildNews(n.getKcId()).get(index);
        } else if (parent instanceof List)
        {
            List clist = (List) parent;
            return clist.get(index);
        }
        return null;
    }

    // 返回父节点的孩子节点数目
    public int getChildCount(Object parent)
    {
        if (parent instanceof WkTWebsite)
        {
            WkTWebsite n = (WkTWebsite) parent;
            return chanelService.getChanel(n.getKwId()).size();
        }

        else if (parent instanceof WkTChanel)
        {
            WkTChanel n = (WkTChanel) parent;
            return newsService.getChildNews(n.getKcId()).size();
        } else if (parent instanceof List)
        {
            List clist = (List) parent;
            return clist.size();
        }
        return 0;
    }

    // 判断某节点是否为叶子节点
    public boolean isLeaf(Object node)
    {

        if (node instanceof WkTWebsite)
        {
            WkTWebsite n = (WkTWebsite) node;
            return chanelService.getChanel(n.getKwId()).size() > 0 ? false: true;
        } else if (node instanceof WkTChanel)
        {
            WkTChanel n = (WkTChanel) node;
            return newsService.getChildNews(n.getKcId()).size() > 0 ? false: true;
        } else if (node instanceof List)
        {
            List clist = (List) node;
            return clist.size() > 0 ? false : true;
        }
        return true;
    }
}