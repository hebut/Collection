package com.uniwin.webkey.system.ui;

/* inaryTreeModel.java

 {{IS_NOTE
 Purpose:

 Description:

 History:
 Aug 10 2007, Created by Jeff Liu
 }}IS_NOTE

 Copyright (C) 2005 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.component.model.TreeModelBean;
import com.uniwin.webkey.component.service.TreeManager;

/**
 * A simple implementation of binary tree model by an arraylist
 * 
 * @author Jeff Liu
 */
public class BaseTreeModel extends AbstractTreeModel
{

    // private ArrayList _tree =;
    private List<Object> data       = new ArrayList<Object>();

    private Map          dataMap    = new HashMap();

    private int          orderIndex = 0;

    private Object       node;

    /**
     * Constructor
     * 
     * @param tree
     *            the list is contained all data of nodes.
     */
    public BaseTreeModel(TreeModelBean u)
    {
        super(u);

    }

    // -- TreeModel --//
    public Object getChild(Object parent, int index)
    {
        int key = 0;// ((TreeModelBean)parent).getNodeId();
        return ((TreeModelBean) ((List) dataMap.get(key)).get(index));
    }

    // -- TreeModel --//
    public int getChildCount(Object parent)
    {
        List tempData = null;
        try
        {
            // if(orderIndex==0){
            tempData = ((TreeManager) SpringUtil
                    .getBean(((TreeModelBean) parent).getBeanName()))
                    .getChildren(((TreeModelBean) parent).getUrl());
            dataMap.put(orderIndex, tempData);
            TreeModelBean temp = null;
            for (Object object : tempData)
            {
                ++orderIndex;
                temp = (TreeModelBean) object;
                // temp.setNodeId(orderIndex);
            }
            ++orderIndex;
            return tempData.size();
            // }else{
            // parent = this.node;
            // tempData =
            // ((TreeManager)SpringUtil.getBean(((TreeModelBean)parent).getBeanName())).getChildren(((TreeModelBean)parent).getUrl());
            // dataMap.put(orderIndex,tempData);
            // TreeModelBean temp = null;
            // for (Object object : tempData) {
            // ++orderIndex;
            // temp = (TreeModelBean)object;
            // temp.setNodeId(orderIndex);
            // }
            // ++orderIndex;
            // return tempData.size();
            //				
            // }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return tempData.size();
    }

    // -- TreeModel --//
    public boolean isLeaf(Object node)
    {
        this.node = node;
        TreeModelBean modelBean = (TreeModelBean) node;
        if (modelBean.getBeanName() == null || modelBean.getUrl() == null
                || "".equals(modelBean.getBeanName())
                || "".equals(modelBean.getUrl()))
        {
            return true;
        }
        List temp = null;
        try
        {
            temp = ((TreeManager) SpringUtil.getBean(modelBean.getBeanName()))
                    .getChildren(modelBean.getUrl());
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }
        return temp.size() == 0;
    }

}
