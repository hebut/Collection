package com.uniwin.webkey.util.ui;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.ContentProvider;

public class ResourceContentProvider implements ContentProvider
{
    private IResourceManager resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");

    private IRoleManager     roleManager;

    private Logger           log             = Logger.getLogger(ResourceContentProvider.class);

    public ResourceContentProvider()
    {
        roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
    }

	/**
	 * 根据父节点获得子节点的集合
	 */
    public List getChildren(Object parent)
    {
        List children = null;
        try
        {
            children = resourceManager
                    .getThredResourceByParentId(((Resource) parent).getId());
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
            res = resourceManager.get(new Integer(0));
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
