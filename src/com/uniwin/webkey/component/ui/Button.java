package com.uniwin.webkey.component.ui;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;

public class Button extends org.zkoss.zul.Button implements AfterCompose
{
    private String type = "";

    public Button()
    {

    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void afterCompose()
    {
        if (null == type)
        {
            return;
        }
        if (type.equals("search"))
        {
            this
                    .setStyle("background-image:url('/images/uiutil/button_inActvie.gif')");
        }
        if (type.equals("delete"))
        {
            this
                    .setStyle("background-image:url(/images/uiutil/button_dele_inactive.gif)");
        }
        if (type.equals("update"))
        {
            this
                    .setStyle("background-image:url(/images/uiutil/button_update_inactive.gif)");
        }
        
        this.removeEventListener("onMouseOver", new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                Button temp = (Button) arg0.getTarget();
                temp.setStyle("color:red");
            }
        });
        
    }

}
