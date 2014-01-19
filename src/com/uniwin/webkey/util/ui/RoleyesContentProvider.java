package com.uniwin.webkey.util.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IResourceDAO;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.ContentProvider;

public class RoleyesContentProvider implements ContentProvider
{
    private IResourceDAO resourceDAO = (IResourceDAO) SpringUtil
                                             .getBean("resourceDAO");

    private Logger       log         = Logger
                                             .getLogger(RoleyesContentProvider.class);

    public List getChildren(Object parent)
    {
        List children = new ArrayList();
        return children;
    }

    public Object getParent(Object node)
    {
        return null;
    }

	/**
	 * 获得根节点
	 */
    public Object getRoot()
    {
        Resource res = null;
        try
        {
            res = resourceDAO.get(new Integer(0));
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
        return res;
    }

}
