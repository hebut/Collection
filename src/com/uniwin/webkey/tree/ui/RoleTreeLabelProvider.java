package com.uniwin.webkey.tree.ui;

import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.system.ui.LabelProvider;

public class RoleTreeLabelProvider implements LabelProvider
{

    public String getImage(Object obj)
    {
        return "";
    }

    public String getLabel(Object obj)
    {
        if (obj != null)
        {
            return ((Role) obj).getRoleName();
        }
        return null;
    }

    public String getHoverImage(Object obj)
    {
        return null;
    }

}
