package com.uniwin.webkey.core.ui;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IWktaccessoryManager;
import com.uniwin.webkey.core.model.Wktaccessory;

/**
 * File: WKTACCESSORY.java Author: your_name Company: uniWin Created: Tue Sep 07
 * 10:23:41 CST 2010 NOTE: 本文件为自动生成,请根据实际情况进行删减,完善！
 */
public class WktaccessoryAddWin extends GenericForwardComposer
{
    private static final long    serialVersionUID    = 1L;

    private Logger               log                 = Logger
                                                             .getLogger(WktaccessoryAddWin.class);

    private IWktaccessoryManager wktaccessoryManager = (IWktaccessoryManager) SpringUtil
                                                             .getBean("wktaccessoryManager");

    private Window               wktaccessoryAddWin;

    private Intbox               KA_ID;

    private Textbox              KA_NAME;

    private Intbox               KM_ID;

    public void afterCompose()
    {

    }

    public void onClick$addwktaccessory()
    {
        Wktaccessory wktaccessory = new Wktaccessory();

        wktaccessory.setKA_ID(KA_ID.getValue());

        wktaccessory.setKA_NAME(KA_NAME.getValue());

        wktaccessory.setKM_ID(KM_ID.getValue());

        try
        {
            wktaccessoryManager.add(wktaccessory);
            Messagebox.show("添加成功!");
            WktaccessoryWin wktaccessoryWin = (WktaccessoryWin) Executions
                    .getCurrent().getDesktop().getPage("page").getFellow(
                            "wktaccessoryWin");
            wktaccessoryWin.refresh();
            wktaccessoryAddWin.detach();
        } catch (Exception e)
        {
            log.error("WK_T_ACCESSORY 添加出错 ：" + e);
        }
    }

    public void onClick$colse()
    {
        // this.detach();
    }
}
