package com.uniwin.webkey.system.parameters.template.win;

import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Treeitem;

public class TreeitemRenderer implements org.zkoss.zul.TreeitemRenderer
{

	/**
     * <li>功能描述：渲染数据
     * 
     * @author bobo
     * @serialData 2010-7-21
     * 
     * @param item
     *            树节点
     * @param data
     *            实体对象
     * @return null
     */
    public void render(Treeitem item, Object data) throws Exception
    {
        if (data == null)
            return;

        SimpleTreeNode t = (SimpleTreeNode) data;
        final TempTree tem = (TempTree) t.getData(); // 类型
        item.setValue(data);

        int length = tem.getName().trim().length();
        if (length > 5)
        {
            String s = tem.getName().trim();
            s = s.substring(0, 5);
            item.setLabel(s + "...");
        } else
        {
            item.setLabel(tem.getName().trim());
        }
        item.setTooltiptext(tem.getName().trim());

    }
}
