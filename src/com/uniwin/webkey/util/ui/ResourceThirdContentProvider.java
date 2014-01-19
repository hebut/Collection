package com.uniwin.webkey.util.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IResourceDAO;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.ContentProvider;

public class ResourceThirdContentProvider implements ContentProvider
{
    private IResourceDAO     resourceDAO     = (IResourceDAO) SpringUtil
                                                     .getBean("resourceDAO");

    private IRoleManager     roleManager;

    private IResourceManager resourceManager = null;

    private Logger           log             = Logger
                                                     .getLogger(ResourceContentProvider.class);

    private List<Resource>   resources       = new ArrayList<Resource>();

    public ResourceThirdContentProvider()
    {
        roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
        resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");
    }

	/**
	 * 根据父节点获得子节点的集合
	 */
    public List getChildren(Object parent)
    {
        List children = null;
        try
        {
            children = roleManager.getChildrenByParetId(((Resource) parent)
                    .getId());
            for (Object obj : children)
            {
                resources.add((Resource) obj);
            }
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.resurcetree")
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
            resources.add(res);
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

    public List<Resource> getResources()
    {
        return resources;
    }

    public void setResources(List<Resource> resources)
    {
        this.resources = resources;
    }

}
