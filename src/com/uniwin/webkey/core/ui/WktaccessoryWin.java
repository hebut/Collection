package com.uniwin.webkey.core.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IWktaccessoryManager;
import com.uniwin.webkey.core.model.Wktaccessory;
/**
 * File: WKTACCESSORY.java Author: your_name Company: uniWin Created: Tue Sep 07
 * 10:23:41 CST 2010 NOTE: 本文件为自动生成,请根据实际情况进行删减,完善！
 */
public class WktaccessoryWin extends Window implements AfterCompose
{
    private static final long    serialVersionUID    = 1L;

    private Logger               log                 = Logger
                                                             .getLogger(WktaccessoryWin.class);

    private IWktaccessoryManager wktaccessoryManager = (IWktaccessoryManager) SpringUtil
                                                             .getBean("wktaccessoryManager");

    private Listhead             listhead;

    private Listbox              listbox;

    private int                  kmId;

    public void afterCompose()
    {
        kmId = (Integer) Executions.getCurrent().getArg().get("kmId");
        listhead = (Listhead) this.getFellow("listhead");
        bindingListHead();
        listbox = (Listbox) this.getFellow("listbox");
        try
        {
            bindingListItem();
        } catch (DataAccessException e)
        {
            log.info("绑定数据出错：" + e);
        }
    }

    private void bindingListHead()
    {

        String[] head = { "编号 ","附件名称"  };
        for (int i = 0; i < head.length; i++)
        {
            String headLabel = head[i];
            Listheader lhead = new Listheader();
            lhead.setLabel(headLabel);
            lhead.setSort("auto");
            listhead.appendChild(lhead);
        }
        Listheader lhead = new Listheader();
        lhead.setLabel("操作");
        lhead.setWidth("200px");
        listhead.appendChild(lhead);
    }

	/**
	 * 加载信息列表
	 * @throws DataAccessException
	 */
    private void bindingListItem() throws DataAccessException
    {
        Wktaccessory wktaccessorySearch = new Wktaccessory();
        wktaccessorySearch.setKM_ID(kmId);
        List<Wktaccessory> list = wktaccessoryManager
                .getList(wktaccessorySearch);
        Listitem lt;
        for (int i = 0; i < list.size(); i++)
        {
            lt = new Listitem();
            final Wktaccessory wktaccessory = list.get(i);
            Listcell lc = null;

            lc = new Listcell();
            lc.setLabel(wktaccessory.getKA_ID() + "");
            lc.setStyle("text-align:center;");
            lt.appendChild(lc);

            lc = new Listcell();
            lc.setLabel(wktaccessory.getKA_NAME() + "");
            lc.setStyle("text-align:center;");
            lt.appendChild(lc);

            Listcell update = new Listcell();
            update.setStyle("text-align:center;");
            Toolbarbutton modfy = new Toolbarbutton();
            modfy.setLabel("下载");
            modfy.setStyle("color:blue");
            modfy.addEventListener(Events.ON_CLICK, new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    if (Messagebox.show("确定要下载该附件吗？", "提示信息",
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION) == 1)
                    {
                        downLoadAccessory(wktaccessory);
                    }

                }
            });

            update.appendChild(new Space());
            update.appendChild(modfy);
            update.appendChild(new Space());
            lt.appendChild(update);
            listbox.appendChild(lt);
        }
    }

    /**
     * 锟斤拷锟截革拷锟斤拷
     * 
     * @param wktaccessory
     */
    public void downLoadAccessory(Wktaccessory wktaccessory)
    {
        String filePath = Executions.getCurrent().getDesktop().getWebApp()
                .getRealPath("/upload/messages/")
                + "/" + wktaccessory.getKA_NAME();
        File file = new File(filePath);

        try
        {
            Filedownload.save(file, "");
        } catch (FileNotFoundException e)
        {
            try
            {
                org.zkoss.zul.Messagebox.show("对不起，该文件已不存在！", "提示信息",
                        Messagebox.OK, Messagebox.INFORMATION);
            } catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    public void refresh()
    {
        List listitem = (List) listbox.getItems();
        listitem.clear();
        try
        {
            bindingListItem();
        } catch (DataAccessException e)
        {
            log.info("绑定数据出错：" + e);
        }
    }
}
