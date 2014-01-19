/*package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.AlarmclockEx;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.WorkbenchWin;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class TodayWin extends Window implements AfterCompose
{
    private List<AlarmclockEx> alarmclockExs;

    private IResourceManager   resourceManager;

    private Audio              alarmClockPlayer;

    public TodayWin()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean("resourceManager");
        alarmclockExs = FrameCommonDate.getThreeAlarmclockExs();

    }

    public List<AlarmclockEx> getAlarmclockExs()
    {
        return alarmclockExs;
    }

    public void setAlarmclockExs(List<AlarmclockEx> alarmclockExs)
    {
        this.alarmclockExs = alarmclockExs;
    }

    public void afterCompose()
    {
        alarmClockPlayer = (Audio) this.getFellow("alarmClockPlayer");
        this.getDesktop().setAttribute("TodayWin", this);
        alarmClockPlayer.setSrc(org.zkoss.util.resource.Labels
                .getLabel("welcome.ui.mp3"));
        // alarmClockPlayer.setAutostart(true);
        // startPalyer();

    }

    public void stopPalyer()
    {
        this.alarmClockPlayer.stop();
    }

    public void startPalyer()
    {
        alarmClockPlayer.setAutostart(true);
    }

    public void showAlarmclock()
    {
        TodayAlarmclockWin temp = (TodayAlarmclockWin) this.getDesktop()
                .getAttribute("TodayAlarmclockWin");
        if (temp != null)
        {
            temp.setVisible(true);
        }
    }

    public void openTab()
    {
        try
        {
            Resource res = FrameCommonDate
                    .getResourceByResourceName(org.zkoss.util.resource.Labels
                            .getLabel("welcome.ui.myclock"));
            if (res != null)
            {
                ((WorkbenchWin) this.getParent().getParent().getDesktop()
                        .getAttribute("WorkbenchWin")).addTab(res);
                // ((WorkbenchWin)Sessions.getCurrent().getAttribute("WorkbenchWin")).addTab(res);
                // ((WorkbenchWin)Executions.getCurrent().getDesktop().getAttribute("WorkbenchWin"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
*/