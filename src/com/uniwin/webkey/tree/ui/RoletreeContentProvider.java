package com.uniwin.webkey.tree.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IResourceDAO;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.system.ui.ContentProvider1;
import com.uniwin.webkey.util.ui.ResourceContentProvider;

public class RoletreeContentProvider implements ContentProvider1
{
    private IResourceDAO     resourceDAO     = (IResourceDAO) SpringUtil.getBean("resourceDAO");

    private IRoleManager     roleManager     = null;

    private IResourceManager resourceManager = null;

    private Logger           log             = Logger
                                                     .getLogger(ResourceContentProvider.class);

    private List<Role>       resources       = new ArrayList<Role>();

    public RoletreeContentProvider()
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
            children = roleManager.getchildrenByParetId(((Role) parent)
                    .getRoleId());
            for (Object obj : children)
            {
                resources.add((Role) obj);
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
    public List getRoot()
    {
        List<Role> res = null;
        try
        {
            res = roleManager.getParents();
            for (Object obj : res)
            {
                resources.add((Role) obj);
            }
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

    public List<Role> getResources()
    {
        return resources;
    }

    public void setResources(List<Role> resources)
    {
        this.resources = resources;
    }
}
