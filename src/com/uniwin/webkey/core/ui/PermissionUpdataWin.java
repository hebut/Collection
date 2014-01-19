package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Executions;
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
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.PermissionAll;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.core.model.Resource;

public class PermissionUpdataWin extends Window implements AfterCompose
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

    private PermissionAll               pall                       = new PermissionAll();

    private Permission                  permission                 = new Permission();

    public List getOperationList()
    {
        return operationList;
    }

    public void setOperationList(List operationList)
    {
        this.operationList = operationList;
    }

    public PermissionUpdataWin()
    {
        try
        {
            operationList = operationManager
                    .getAllOperationListOrderByOperationName();
            Map map = Executions.getCurrent().getArg();
            pall = (PermissionAll) map.get("permissionall");
            permission.setKpid(pall.getKpid());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void afterCompose()
    {
        code = (Textbox) this.getFellow("code");
        code.setValue(pall.getKpCode());
        name = (Textbox) this.getFellow("name");
        name.setValue(pall.getKpName());
        rtype = (Combobox) this.getFellow("rtype");
        if (pall.getKrtype().equals("1"))
        {
            rtype.setSelectedIndex(0);
        } else
        {
            rtype.setSelectedIndex(1);
        }
        otype = (Combobox) this.getFellow("otype");
        if (pall.getlPermissionOperation().size() > 0)
        {
            if (((Permissionoperation) pall.getlPermissionOperation().get(0))
                    .getKptype().equals("0"))
            {
                otype.setSelectedIndex(0);
            } else
            {
                otype.setSelectedIndex(1);
            }
        } else
        {
            otype.setSelectedIndex(0);
        }
        resource = (Resource) Sessions.getCurrent().getAttribute("resource");
        opListbox = (Listbox) this.getFellow("opListbox");
        userPaging = (Paging) this.getFellow("userPaging");
        userPaging.addEventListener("onPaging", new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                fullListbox(operationManager.getListByPage(userPaging
                        .getActivePage(), userPaging.getPageSize()), pall
                        .getlPermissionOperation());
            }
        });
        try
        {
            fullListbox(operationManager.getListByPage(0, userPaging
                    .getPageSize()), pall.getlPermissionOperation());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 更新许可信息
     * 
     * @throws InterruptedException
     * @throws ObjectNotExistException
     */
    public void updataPermission() throws InterruptedException,
            ObjectNotExistException
    {
        permission.setName(name.getText());
        permission.setCode(code.getValue());
        if (name.getText().trim().equals(""))
        {
            Messagebox.show("许可名称不能为空", org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }
        if (code.getValue().equals(""))
        {
            Messagebox.show("许可代码不能为空", org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }
        permission.setKrid(resource.getId());
        permission
                .setKrtype(this.rtype.getSelectedItem().getValue().toString());
        try
        {
            permissionManager.update(permission);

            permissionoperationManager.removeByKpid(permission.getKpid()+"");
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

    public void fullListbox(List<Operation> list, List<Permissionoperation> lpo)
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
            boolean bSelect = false;
            for (int j = 0; j < lpo.size(); j++)
            {
                Permissionoperation p = (Permissionoperation) lpo.get(j);
                if (op.getOperationId() == p.getKoid())
                {
                    bSelect = true;
                    break;
                }
            }
            item.setSelected(bSelect);
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
