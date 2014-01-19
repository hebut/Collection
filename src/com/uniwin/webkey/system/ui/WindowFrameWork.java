package com.uniwin.webkey.system.ui;

import org.zkoss.zul.Tree;

import com.uniwin.webkey.component.event.FrameworkEvent;

public class WindowFrameWork extends WindowEx
{

    public void sendToFrameWorkEvent(String eventPage, Integer resourceId)
    {
        if (eventPage == null)
        {
            // 自定义异常
            return;
        }
        if (FrameworkEvent.PAGETOP.equals(eventPage))
        {
            this.pageTopEventExcution(resourceId);
        }
        if (FrameworkEvent.PAGELEFT.equals(eventPage))
        {
            this.pageLeftEventExcution(resourceId);
        }
        if (FrameworkEvent.PAGERIGHT.equals(eventPage))
        {
            this.pageRightEventExcution(resourceId);
        }

    }

    public void pageTopEventExcution(Integer resourceId)
    {
        Tree leftTree = (Tree) this.getDesktop().getAttribute(
                "framework_leftTree");
        // leftTree=this.getTree(resourceId);
    }

    public void pageLeftEventExcution(Integer resourceId)
    {

    }

    public void pageRightEventExcution(Integer resourceId)
    {

    }

}
