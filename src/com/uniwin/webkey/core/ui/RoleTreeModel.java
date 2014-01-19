package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.zul.SimpleTreeModel;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.event.TreeDataEvent;

public class RoleTreeModel extends SimpleTreeModel
{
	private static final long serialVersionUID = -413723632728436373L;

	public RoleTreeModel(SimpleTreeNode root)
    {
        super(root);
    }

	/**
     * <li>功能描述：添加树节点
     * 
     * @author bobo
     * @serialData 2010-7-21
     * 
     * @param parent
     *            父节点
     * @param newNodes
     *            新的子节点
     * @return null
     */
    public void add(SimpleTreeNode parent, SimpleTreeNode newNodes)
    {

        List<SimpleTreeNode> children = parent.getChildren();
        int length = children.size();
        children.add(newNodes);
        fireEvent(parent, length, length, TreeDataEvent.INTERVAL_ADDED);
    }
}
