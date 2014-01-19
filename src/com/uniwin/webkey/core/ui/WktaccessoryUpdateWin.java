package com.uniwin.webkey.core.ui;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IWktaccessoryManager;
import com.uniwin.webkey.core.model.Wktaccessory;

/**
 * File:    WKTACCESSORY.java
 * Author:  your_name
 * Company: uniWin
 * Created: Tue Sep 07 10:23:41 CST 2010 
 * NOTE:    本文件为自动生成,请根据实际情况进行删减,完善！
 */
public class WktaccessoryUpdateWin extends Window implements AfterCompose
{
    private static final long    serialVersionUID    = 1L;

    private Logger               log                 = Logger
                                                             .getLogger(WktaccessoryUpdateWin.class);

    private IWktaccessoryManager wktaccessoryManager = (IWktaccessoryManager) SpringUtil
                                                             .getBean("wktaccessoryManager");

    private Textbox              KA_NAME;

    private Intbox               KM_ID;

    private Wktaccessory         wktaccessory;                                                       //要更新的对象

    public WktaccessoryUpdateWin()
    {
        Map map = Executions.getCurrent().getArg();
        wktaccessory = (Wktaccessory) map.get("wktaccessory");
    }

    public void afterCompose()
    {

        KA_NAME = (Textbox) this.getFellow("KA_NAME");

        KM_ID = (Intbox) this.getFellow("KM_ID");

    }

    public void updatewktaccessory(Event event)
    {
        Wktaccessory updateWktaccessory = wktaccessory;

        updateWktaccessory.setKA_NAME(KA_NAME.getValue());

        updateWktaccessory.setKM_ID(KM_ID.getValue());

        try
        {
            wktaccessoryManager.update(wktaccessory);
            ((WktaccessoryWin) this.getParent()).refresh();
            Messagebox.show("修改成功!");
            this.detach();
        } catch (Exception e)
        {
            log.error("WK_T_ACCESSORY  修改出错：" + e);
        }
    }

    public Wktaccessory getWktaccessory()
    {
        return wktaccessory;
    }

    public void setWktaccessory(Wktaccessory wktaccessory)
    {
        this.wktaccessory = wktaccessory;
    }
}
