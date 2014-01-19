package com.uniwin.webkey.component.ui;

import java.util.Map;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Treerow;

public class FrameEvent
{
    private Map treeRow;

    public FrameEvent()
    {
        treeRow = (Map) Sessions.getCurrent().getAttribute("rbac_treeRow");
    }

    public void openTab(int resourceId)
    {
        Treerow row = (Treerow) treeRow.get("treeRow" + resourceId);
        Events.postEvent("onClick", row, null);
    }
}
