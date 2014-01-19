package com.uniwin.webkey.system.ui;

import java.util.List;

import org.zkoss.zul.Treeitem;

public interface TreeitemlContentProvider
{
    List<Treeitem> getChildrenTreeitems(Object parent);

    Treeitem getRoot();
}
