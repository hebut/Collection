package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.core.model.Resource;

public class PermissionAddWin extends Window implements AfterCompose
{
    private Textbox                     name, code;

    private Combobox                    rtype, otype;

    private List                        operationList              = new ArrayList();

    private IOperationManager           operationManager           = (IOperationManager) SpringUtil
                                                                           .getBean("operationManager");

    private IPermissionManager          permissionManager          = (IPermissionManager) SpringUtil
                                                                           .getBean("permissionManager");

    private IPermissionOperationManager permissionoperationManager = (IPermissionOperationManager) SpringUtil
                                                                           .getBean("permissionOperationManager");

    private Resource                    resource;

    private Listbox                     opListbox;

    private Paging                      userPaging;

    public PermissionAddWin()
    {
        try
        {
            operationList = operationManager
                    .getAllOperationListOrderByOperationName();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void afterCompose()
    {
        code = (Textbox) this.getFellow("code");
        name = (Textbox) this.getFellow("name");
        rtype = (Combobox) this.getFellow("rtype");
        rtype.setSelectedIndex(0);
        otype = (Combobox) this.getFellow("otype");
        otype.setSelectedIndex(0);
        resource = (Resource) Sessions.getCurrent().getAttribute("resource");
        opListbox = (Listbox) this.getFellow("opListbox");
        userPaging = (Paging) this.getFellow("userPaging");
        userPaging.addEventListener("onPaging", new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                fullListbox(operationManager.getListByPage(userPaging
                        .getActivePage(), userPaging.getPageSize()));
            }
        });
        try
        {
            fullListbox(operationManager.getListByPage(0, userPaging
                    .getPageSize()));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 添加许可信息
     * 
     * @throws InterruptedException
     */
    public void addPermission() throws InterruptedException
    {
        Permission permission = new Permission();
        permission.setName(name.getText());
        permission.setCode(code.getValue());
        if (name.getText().trim().equals(""))
        {
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("permission.zul.licensenamevalidate"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            return;
        }
        if (code.getValue().equals(""))
        {
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("permission.zul.codevalidate"), org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }
        permission.setKrid(resource.getId());
        permission
                .setKrtype(this.rtype.getSelectedItem().getValue().toString());
        try
        {
            permissionManager.add(permission);
            Set set = this.opListbox.getSelectedItems();
            Iterator it = set.iterator();
            int i = 0;
            while (it.hasNext())
            {
                Listitem listitem = (Listitem) it.next();
                Operation oPeration = (Operation) listitem.getValue();
                Permissionoperation po = new Permissionoperation();
                po.setKpid(permission.getKpid());
                po
                        .setKptype(this.otype.getSelectedItem().getValue()
                                .toString());
                po.setKporder(oPeration.getOperationId() + "");
                po.setKoid(oPeration.getOperationId());
                permissionoperationManager.add(po);
            }
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            Events.postEvent(Events.ON_CHANGE, this, null);
            this.detach();

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectAlreadyExistException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public void fullListbox(List<Operation> list)
    {
        this.opListbox.getItems().clear();
        Listitem item = null;
        Listcell check = null;
        Listcell cellnum = null;
        Listcell cellopName = null;
        Listcell cellopCode = null;

        for (int i = 0; i < list.size(); i++)
        {
            Operation op = list.get(i);
            item = new Listitem();
            item.setValue(op);
            check = new Listcell();
            cellnum = new Listcell();
            cellopName = new Listcell();
            cellopCode = new Listcell();

            item.appendChild(check);
            cellnum.setLabel((userPaging.getActivePage()
                    * userPaging.getPageSize() + i + 1)
                    + "");
            item.appendChild(cellnum);
            cellopName.setLabel(op.getName());
            item.appendChild(cellopName);
            cellopCode.setLabel(op.getOpCode());
            item.appendChild(cellopCode);

            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
                check.setSclass("r-listitem-bor1");
                cellnum.setSclass("r-listitem-bor1");
                cellopName.setSclass("r-listitem-bor1");
                cellopCode.setSclass("r-listitem-bor1");
            }
            this.opListbox.appendChild(item);
        }
        try
        {
            int allSize = this.operationManager.getAllOperation().size();
            this.userPaging.setTotalSize(allSize);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
