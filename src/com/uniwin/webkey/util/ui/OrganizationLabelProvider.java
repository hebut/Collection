package com.uniwin.webkey.util.ui;

import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.system.ui.LabelProvider;

public class OrganizationLabelProvider implements LabelProvider
{

    public String getImage(Object obj)
    {
        return "";
    }

    public String getLabel(Object obj)
    {
        if (obj != null)
        {
            return ((Organization) obj).getName();
        }
        return null;
    }

    public String getHoverImage(Object obj)
    {
        return null;
    }

}
