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
import org.zkoss.zul.Window;

import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.system.ui.WorkbenchWin;

public class OperationListWin extends Window implements AfterCompose
{
    private IOperationManager  operationManager = (IOperationManager) SpringUtil
                                                        .getBean("operationManager");

    private IPermissionManager permissionManager;

    private Paging             userPaging;

    private Listbox            listbox;

    private List<Operation>    operationList;

    private int                num              = 0;

    public OperationListWin()
    {
//        permissionManager = (IPermissionManager) TuscanySpringUtil.getBean(
//                "permissionManager", "t");
        permissionManager = (IPermissionManager)SpringUtil.getBean("permissionManager");
        // usersManager=(IUsersManager)scaDomain.getService(IUsersManager.class,
        // "User");
        try
        {
            operationList = operationManager.getOperationListOrderbyId();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Operation> getOperationList()
    {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList)
    {
        this.operationList = operationList;
    }

    /**
     * 修改操作信息
     * 
     * @param event事件源
     */
    public void updataOperation(Event event)
    {
        Map map = new HashMap();
        map.put("operation", (Operation) ((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
        Window win = (Window) Executions.createComponents(
                "/apps/core/operationUpdata.zul", this, map);
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
     * 添加操作信息
     */
    public void addOperation()
    {
        Window win = (Window) Executions.getCurrent().createComponents(
                "/apps/core/operationAdd.zul", this, null);
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

    /**
     * 删除操作信息
     * 
     * @param event事件源
     */
    public void deleteOperation(Event event)
    {
        Listitem item = (Listitem) event.getTarget().getParent().getParent().getParent();
        Operation operation = (Operation) item.getValue();
        try
        {
            boolean isDel = permissionManager.getOPNumUserdByOpid(operation.getOperationId());
//            boolean isDel = true;
            if (isDel)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("operation.ui.notdelete"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("operation.ui.isconfirmdelete"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"), Messagebox.OK
                            | Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            this.operationManager.remove(operation);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.deletesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
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
     * 批量删除操作信息
     */
    public void deleteOperations()
    {

        try
        {
            Set items = listbox.getSelectedItems();
            if (items.size() == 0)
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("operation.ui.selectoperations"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("operation.ui.isconfirmoperationsdelete"),
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
                Operation user = (Operation) item.getValue();
                if (user != null)
                    ids += user.getOperationId() + ",";
            }
            operationManager.deleteOperationByIds(ids.substring(0,
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

    public void afterCompose()
    {
        userPaging = (Paging) this.getFellow("userPaging");
        listbox = (Listbox) this.getFellow("listbox");
        userPaging.addEventListener("onPaging", new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                fullListbox(operationManager.getListByPage(userPaging.getActivePage(), userPaging.getPageSize()));
            }
        });
        try
        {
            fullListbox(operationManager.getListByPage(0, userPaging.getPageSize()));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 填充listbox
     * 
     * @param list
     *            要显示的集合
     */
    public void fullListbox(List<Operation> list)
    {
        this.listbox.getItems().clear();
        Listitem item = null;
        Listcell cellNum = null;
        Listcell check = null;
        Listcell cellName = null;
        Listcell cellCode = null;
        Listcell cellDesc = null;
        Listcell cellEdit = null;
//        Listcell cellDel = null;
        Image edit = null;
        Image delete = null;
        
        for (int i = 0; i < list.size(); i++)
        {
            Operation o = list.get(i);
            item = new Listitem();
            item.setValue(o);
            num = userPaging.getActivePage() * userPaging.getPageSize() + i + 1;
            check = new Listcell();
            check.setValue(o);
            cellNum = new Listcell();
            cellName = new Listcell();
            cellCode = new Listcell();
            cellDesc = new Listcell();
            cellEdit = new Listcell();

            item.appendChild(check);
            cellNum.setLabel(num + "");
            item.appendChild(cellNum);
            cellName.setLabel(o.getName());
            item.appendChild(cellName);
            cellCode.setLabel(o.getOpCode());
            item.appendChild(cellCode);
            cellDesc.setLabel(o.getDescription());
            item.appendChild(cellDesc);
            
            Hbox div1=new Hbox();
            div1.setClass("center");
            cellEdit.appendChild(div1); 
    		
            edit = new Image();
            edit.setType("edit");
            
            edit.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    updataOperation(arg0);
                }
            });
            
            delete = new Image();
            delete.setType("delList");
            delete.addEventListener("onClick", new EventListener()
            {

                public void onEvent(Event arg0) throws Exception
                {
                    deleteOperation(arg0);
                }
            });
            
            div1.appendChild(edit);
            div1.appendChild(delete);
            
//            cellEdit.appendChild(edit);
//            cellDel.appendChild(delete);
            item.appendChild(cellEdit);
//            item.appendChild(cellDel);
            if (Sessions.getCurrent().getAttribute("themeName").toString()
                    .equals("defult"))
            {
                check.setSclass("r-listitem-bor1");
                cellNum.setSclass("r-listitem-bor1");
                cellName.setSclass("r-listitem-bor1");
                cellCode.setSclass("r-listitem-bor1");
                cellDesc.setSclass("r-listitem-bor1");
                cellEdit.setSclass("r-listitem-bor1");
//                cellDel.setSclass("r-listitem-bor1");
            }
            this.listbox.appendChild(item);
        }
        try
        {
            int allSize = this.operationManager.getOperationListOrderbyId().size();
            this.userPaging.setTotalSize(allSize);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
