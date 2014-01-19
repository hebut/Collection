package com.uniwin.webkey.system.ui;

import java.util.List;

public interface ContentProvider1
{

    List getChildren(Object parent);

    Object getParent(Object node);

    List getRoot();

}
