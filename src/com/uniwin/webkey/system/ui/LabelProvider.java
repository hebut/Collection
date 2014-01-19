package com.uniwin.webkey.system.ui;

public interface LabelProvider
{
	/**
	 * 获得图片的路径
	 * @param obj 要显示成树节点的对象
	 * @return 图片的路径
	 */
	String getImage(Object obj);
	/**
	 * 获得鼠标悬停时的图片路径
	 * @param obj要显示成树节点的对象
	 * @return 鼠标悬停时的图片路径
	 */
	String getHoverImage(Object obj);
	/**
	 * 获得要显示到菜单的文本信息
	 * @param obj要显示成树节点的对象
	 * @return 显示到菜单的文本信息
	 */
    String getLabel(Object obj);
}
