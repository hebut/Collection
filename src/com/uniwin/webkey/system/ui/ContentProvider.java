package com.uniwin.webkey.system.ui;

import java.util.List;

public interface ContentProvider
{
	/**
	 * 根据父节点获得子节点的集合
	 * @param parent 父级对象
	 * @return 子节点对象的集合
	 */
    List getChildren(Object parent);

    /**
	 * 根据节点获得上级对象
	 * @param node 当前节点
	 * @return 父节点对象（没有返回NULL）
	 */
    Object getParent(Object node);

    /**
	 * 获取根节点
	 * @return 根节点对象
	 */
    Object getRoot();

}
