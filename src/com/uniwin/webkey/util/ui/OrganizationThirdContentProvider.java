package com.uniwin.webkey.util.ui;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;

import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.system.ui.ContentProvider;

public class OrganizationThirdContentProvider implements ContentProvider
{
    private IOrganizationManager organizationManager;

    private Logger               log = Logger
                                             .getLogger(OrganizationContentProvider.class);

    public OrganizationThirdContentProvider()
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
            e.printStackTrace();
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

    public Object getParent(Object node)
    {
        return null;
    }

    /**
     *获得根节点
     */
    public Object getRoot()
    {
        Organization res = null;
        try
        {
            res = organizationManager.get(new Integer(0));
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("organization.ui.gettreeroot")
                    + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

}
