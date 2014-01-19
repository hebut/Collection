/*package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IAlarmclockManager;
import com.uniwin.webkey.core.model.Alarmclock;
import com.uniwin.webkey.core.model.AlarmclockEx;
import com.uniwin.webkey.system.ui.WorkbenchWin;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class AlarmclockAddWin extends Window implements AfterCompose
{
    private Datebox            toTime;

    private Textbox            eventName;

    private Radio              start, stop;

    private Listbox            events;

    private IAlarmclockManager alarmclockManager = (IAlarmclockManager) SpringUtil
                                                         .getBean("alarmclockManager");

    private int                rowIndex          = 1;

    private List<Textbox>      textboxs          = new ArrayList<Textbox>();

    public void afterCompose()
    {
        toTime = (Datebox) this.getFellow("toTime");
        eventName = (Textbox) this.getFellow("eventName");
        events = (Listbox) this.getFellow("events");
        start = (Radio) this.getFellow("start");
        stop = (Radio) this.getFellow("stop");
        textboxs.add((Textbox) this.getFellow("item1"));
    }

    *//**
     * 添加listbox的Row
     *//*
    public void addRow()
    {

        Listitem item = new Listitem();
        Listcell cell1 = new Listcell();
        Listcell cell2 = new Listcell();
        Listcell cell3 = new Listcell();
        Textbox textbox = new Textbox();
        Image delIm = new Image();
        rowIndex++;
        cell1.setLabel(rowIndex + "");
        textbox.setRows(2);
        textbox.setCols(50);
        textbox.setId("item" + (rowIndex + 1));
        textbox.setWidth("100%");
        textboxs.add(textbox);
        delIm.setType("delList");
        delIm.addEventListener("onClick", new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                delRow(arg0);
            }
        });
        cell3.appendChild(delIm);
        cell2.appendChild(textbox);
        item.appendChild(cell1);
        item.appendChild(cell2);
        item.appendChild(cell3);
        events.appendChild(item);
    }

    *//**
     * 删除ROW
     * 
     * @param event
     *            事件源
     *//*
    public void delRow(Event event)
    {
        Listitem listitem = (Listitem) event.getTarget().getParent()
                .getParent();

        if (textboxs.size() == 1)
        {
            try
            {
                Textbox text = (Textbox) this
                        .getFellow(textboxs.get(0).getId());
                text.setText("");
                return;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        events.removeChild(listitem);
        textboxs.remove((Textbox) ((Listcell) listitem.getChildren().get(1))
                .getFirstChild());
    }

    *//**
     * 添加闹钟提醒
     *//*
    public void addAlarmclock()
    {
        Alarmclock alarmc = new Alarmclock();
        alarmc.setUserId(FrameCommonDate.getUser().getUserId().intValue());
      //  alarmc.setTotime(toTime.getValue().toString());
        alarmc.setTotime(DateUtil.dateToStr(toTime.getValue(),
		"yyyy-MM-dd HH:mm:ss"));
        alarmc.setEventName(eventName.getText());
        alarmc.setEventDescription(this.getDescirption());
        if (start.isChecked())
        {
            alarmc.setState(Integer.parseInt(start.getValue()));
        }
        if (stop.isChecked())
        {
            alarmc.setState(Integer.parseInt(stop.getValue()));
        }
        try
        {

            alarmclockManager.add(alarmc);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            AlarmclockEx alarmclockUpdate = new AlarmclockEx();
            alarmclockUpdate.setId(alarmc.getId());
            alarmclockUpdate.setState(alarmc.getState());
            alarmclockUpdate.setEventName(alarmc.getEventName());
            alarmclockUpdate.setEventDescription(alarmc.getEventDescription());
            alarmclockUpdate.setTotime(alarmc.getTotime());
            Sessions.getCurrent()
                    .setAttribute("alarmclockEx", alarmclockUpdate);
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
            this.detach();

        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.savefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
    }

    *//**
     * 获得闹钟说明信息
     * 
     * @return 闹钟说明
     *//*
    public String getDescirption()
    {
        String descri = "";
        for (Textbox textbox : textboxs)
        {
            if (!textbox.getText().trim().equals(""))
            {
                descri += textbox.getText() + "@@";
            }
        }

        if (descri.length() > 0)
        {
            descri = descri.substring(0, descri.length() - 2);
        }
        return descri;
    }

}
*/