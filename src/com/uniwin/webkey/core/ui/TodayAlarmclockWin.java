/*package com.uniwin.webkey.core.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IAlarmclockManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.model.Alarmclock;
import com.uniwin.webkey.core.model.AlarmclockEx;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.system.ui.WorkbenchWin;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class TodayAlarmclockWin extends Window implements AfterCompose
{
    private IAlarmclockManager alarmclockManager = (IAlarmclockManager) SpringUtil
                                                         .getBean("alarmclockManager");

    private List<AlarmclockEx> alarmclockExs;

    private boolean            isVisabel;

    private IResourceManager   resourceManager;

    public TodayAlarmclockWin()
    {
        resourceManager = (IResourceManager) SpringUtil.getBean(
                "resourceManager");
        try
        {
            alarmclockExs = this.alarmclockManager
                    .getToTimeAlarmclocks(FrameCommonDate.getUser().getUserId()
                            .intValue());
            for (Alarmclock al : alarmclockExs)
            {
                al.setEventDescription(this.repleace(al.getEventDescription()));
            }
            if (alarmclockExs.size() > 0)
            {
                isVisabel = true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void afterCompose()
    {
        this.getDesktop().setAttribute("TodayAlarmclockWin", this);
        this.setVisible(isVisabel);
    }

    public void show()
    {
        this.setVisible(isVisabel);
    }

    public List<AlarmclockEx> getAlarmclockExs()
    {
        return alarmclockExs;
    }

    public void setAlarmclockExs(List<AlarmclockEx> alarmclockExs)
    {
        this.alarmclockExs = alarmclockExs;
    }


	*//**
	 * 停止闹钟
	 * @param event
	 *//*
    public void stopAlarmclock(Event event)
    {
        int alarmId = Integer.parseInt(event.getTarget().getId());
        for (AlarmclockEx alarmclockEx : alarmclockExs)
        {
            if (alarmclockEx.getId() == alarmId)
            {
                Alarmclock alarmclockUp = new Alarmclock();
                alarmclockUp.setId(alarmclockEx.getId());
                alarmclockUp.setEventName(alarmclockEx.getEventName());
                alarmclockUp.setState(1);
                alarmclockUp.setEventDescription(alarmclockEx
                        .getEventDescription());
                alarmclockUp.setTotime(alarmclockEx.getTotime());
                if (event.getTarget().getParent().getParent().getChildren()
                        .size() <= 2)
                {
                    this.setVisible(false);
                } else
                {
                    ((Hbox) (event.getTarget().getParent())).detach();
                }

                try
                {
                    alarmclockManager.update(alarmclockUp);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public String repleace(String content)
    {
        return content.replace('@', ',');
    }

    public IResourceManager getResourceManager()
    {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public void open()
    {
        try
        {
            Resource resource = resourceManager.get(20);
            WorkbenchWin win = ((WorkbenchWin) Sessions.getCurrent()
                    .getAttribute("WorkbenchWin"));
            win.afterCompose();
            win.addTab(resource);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showInfo(Event event)
    {
        try
        {
            int id = Integer.parseInt(event.getTarget().getId().substring(1));
            Alarmclock alarmclock = alarmclockManager.get(id);
            Map map = new HashMap();
            map.put("ala", alarmclock);
            Window win = (Window) Executions.createComponents(
                    "apps/core/alarmclockInfo.zul", this.getParent(),
                    map);
            win.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
*/