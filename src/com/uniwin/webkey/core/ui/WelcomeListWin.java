package com.uniwin.webkey.core.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IWelcomeManager;
import com.uniwin.webkey.core.model.Welcome;
import com.uniwin.webkey.system.ui.WorkbenchWin;

public class WelcomeListWin extends Window implements AfterCompose
{
    private IWelcomeManager welcomeManager = (IWelcomeManager) SpringUtil
                                                   .getBean("welcomeManager");

    private List<Welcome>   welcomeList;

    private Paging          userPaging;

    private Listbox         listbox;

    public WelcomeListWin()
    {
        try
        {
            welcomeList = welcomeManager.getListWelcome();
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }
    }

    public List<Welcome> getWelcomeList()
    {
        return welcomeList;
    }

    public void setWelcomeList(List<Welcome> welcomeList)
    {
        this.welcomeList = welcomeList;
    }
	/**
	 * 弹出修改工作台页面
	 * @param event
	 */
    public void updataWelcome(Event event)
    {
        Map map = new HashMap();
        map.put("welcome", (Welcome) ((Listitem) (event.getTarget().getParent().getParent().getParent().getParent())).getValue());

        ((Listitem) (event.getTarget().getParent().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents(
                "/apps/core/welcomeUpdata.zul", this, map);
        try
        {
            win.setClosable(true);
            win.setPosition("center");
            win.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

	/**
	 * 删除工作台
	 * @param event
	 */
    public void deleteWelcome(Event event)
    {
        try
        {
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("welcome.ui.isconfirmdelete"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }
            Listitem item = (Listitem) event.getTarget().getParent()
                    .getParent().getParent().getParent();
            Welcome welcome = (Welcome) item.getValue();
            this.welcomeManager.remove(welcome);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }

        }
    }


	/**
	 * 删除选中的工作台
	 */
    public void deleteWelcomes()
    {
        try
        {
            Set items = listbox.getSelectedItems();
            if (items.size() == 0)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("welcome.ui.selectforums"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("welcome.ui.isconfirmdeleteforums"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }

            String ids = "";
            for (Listitem item : (Set<Listitem>) items)
            {
                Welcome user = (Welcome) item.getValue();
                if (user != null)
                    ids += user.getId() + ",";
            }
            welcomeManager.deleteWelcomesBuIds(ids.substring(0,
                    ids.length() - 1));
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.deletefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
            }
        }

    }

    public void addWelcome()
    {
        Window win = (Window) Executions.getCurrent().createComponents(
                "/apps/core/welcomeAdd.zul", this, null);
        try
        {
            win.setPosition("center");
            win.setClosable(true);
            win.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void afterCompose()
    {
        userPaging = (Paging) this.getFellow("userPaging");
        listbox = (Listbox) this.getFellow("listbox");
        userPaging.addEventListener("onPaging", new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                fullListbox(welcomeManager.getListByPage(userPaging
                        .getActivePage(), userPaging.getPageSize()));
            }
        });
        try
        {
            fullListbox(welcomeManager.getListByPage(0, userPaging
                    .getPageSize()));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fullListbox(List<Welcome> list)
    {
        this.listbox.getItems().clear();
        Listitem item = null;
        Listcell check = null;
        Listcell cellnum = null;
        Listcell cellUrl = null;
        Listcell cellName = null;
        Listcell cellDesc = null;
        Listcell cellEdit = null;
//        Listcell cellDel = null;
        Image edit = null;
        Image delete = null;
        
        for (int i = 0; i < list.size(); i++)
        {
            Welcome o = list.get(i);
            item = new Listitem();
            item.setValue(o);
            check = new Listcell();
            cellnum = new Listcell();
            cellUrl = new Listcell();
            cellName = new Listcell();
            cellDesc = new Listcell();
            cellEdit = new Listcell();
//            cellDel = new Listcell();
            cellUrl.setLabel(o.getUrl());
            item.appendChild(check);
            cellnum.setLabel((userPaging.getActivePage()
                    * userPaging.getPageSize() + i + 1)
                    + "");
            item.appendChild(cellnum);
            cellName.setLabel(o.getName());
            item.appendChild(cellName);
            item.appendChild(cellUrl);
            cellDesc.setLabel(o.getVisible() == 0 ? "是" : "否");
            item.appendChild(cellDesc);
            
            Vbox vb1=new Vbox();
            cellEdit.appendChild(vb1);    		
    		Hbox hb=new Hbox(); 
    		
            edit = new Image();
            edit.setType("edit");
            edit.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    updataWelcome(arg0);
                }
            });
            
            
            delete = new Image();
            delete.setType("delList");
            delete.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    deleteWelcome(arg0);
                }
            });
            
            hb.appendChild(edit);
            hb.appendChild(delete);
            vb1.appendChild(hb);
            
//            cellEdit.appendChild(edit);
//            cellDel.appendChild(delete);
            item.appendChild(cellEdit);
//            item.appendChild(cellDel);
            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
                check.setSclass("r-listitem-bor1");
                cellnum.setSclass("r-listitem-bor1");
                cellUrl.setSclass("r-listitem-bor1");
                cellName.setSclass("r-listitem-bor1");
                cellDesc.setSclass("r-listitem-bor1");
                cellEdit.setSclass("r-listitem-bor1");
//                cellDel.setSclass("r-listitem-bor1");
            }
            this.listbox.appendChild(item);
        }
        try
        {
            int allSize = this.welcomeManager.getAllWelcome().size();
            this.userPaging.setTotalSize(allSize);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
