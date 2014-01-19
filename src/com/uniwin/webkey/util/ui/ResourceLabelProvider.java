package com.uniwin.webkey.util.ui;

import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.LabelProvider;

public class ResourceLabelProvider implements LabelProvider
{

    public String getImage(Object obj)
    {
        return "";
    }


	/**
	 * 获得要显示的label信息
	 */
    public String getLabel(Object obj)
    {
        if (obj != null)
        {
            return ((Resource) obj).getName();
        }
        return null;
    }

	/**
	 * 获得hoverImage
	 */
    public String getHoverImage(Object obj)
    {
        return null;
    }

}
