package com.uniwin.webkey.util.ui;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.ContentProvider;

public class OrganizationFirstContentProvider implements ContentProvider
{
    private IOrganizationManager organizationManager;

    private Logger               log = Logger
                                             .getLogger(OrganizationContentProvider.class);

    public OrganizationFirstContentProvider()
    {
        organizationManager = (IOrganizationManager) SpringUtil.getBean("organizationManager");
    }
	/**
	 * 根据父节点获得子节点
	 */
    public List getChildren(Object parent)
    {
        List children = null;
        try
        {
            children = organizationManager
                    .getChildrenByParentId(((Organization) parent)
                            .getOrganizationId());
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("organization.ui.getOrgtree")
                    + e.getMessage());
            e.printStackTrace();
        }
        return children;
    }

    /**
	 * 
	 */
    public Object getParent(Object node)
    {
        return null;
    }
	/**
	 * 获得根节点
	 */
    public Object getRoot()
    {
        Resource res = new Resource();
        res.setName(org.zkoss.util.resource.Labels
                .getLabel("system.commom.ui.menu"));
        res.setResourceType(org.zkoss.util.resource.Labels
                .getLabel("organization.ui.rootmenu"));
        res.setId(0);
        return res;
    }

}
