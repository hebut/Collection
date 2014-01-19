/*package com.uniwin.webkey.core.ui;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Alarmclock;

public class AlarmclockWin extends Window implements AfterCompose
{
    private Label      name, time, description;

    private Alarmclock ala;

    public void afterCompose()
    {
        name = (Label) this.getFellow("name");
        time = (Label) this.getFellow("time");
        description = (Label) this.getFellow("descrption");
        name.setValue(ala.getEventName());
        time.setValue(ala.getTotimeStr());
        description.setValue(ala.getEventDescription());
    }

    public AlarmclockWin()
    {
        Map map = Executions.getCurrent().getArg();
        ala = (Alarmclock) map.get("ala");

    }

}
*/