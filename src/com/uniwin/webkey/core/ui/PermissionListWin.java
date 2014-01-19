package com.uniwin.webkey.core.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.PermissionAll;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.core.model.Resource;

public class PermissionListWin extends Window implements AfterCompose
{
    private IOperationManager           operationManager           = (IOperationManager) SpringUtil
                                                                           .getBean("operationManager");

    private IPermissionManager          permissionManager          = (IPermissionManager) SpringUtil
                                                                           .getBean("permissionManager");

    private IPermissionOperationManager permissionoperationManager = (IPermissionOperationManager) SpringUtil
                                                                           .getBean("permissionOperationManager");

    private Resource                    resource;

    private Map                         mPer;

    private Listbox                     permissionListbox;

    private List<Permission>            permissions;

    private Resource                    currResource;

    public Map getmPer()
    {
        return mPer;
    }

    public void setmPer(Map mPer)
    {
        this.mPer = mPer;
    }

    public PermissionListWin()
    {
        resource = (Resource) Sessions.getCurrent().getAttribute("resource");
        currResource = (Resource) Sessions.getCurrent().getAttribute(
                "currResource");
        permissions = (List<Permission>) ((HttpServletRequest) Executions
                .getCurrent().getNativeRequest()).getAttribute("permissions");
        mPer = (Map) ((Map) Sessions.getCurrent().getAttribute("rsopMap"))
                .get("rs" + currResource.getId());
        ((HttpServletRequest) Executions.getCurrent().getNativeRequest())
                .setAttribute("mPer", mPer);

        
    }

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        permissionListbox = (Listbox) this.getFellow("permissionListbox");
        permissionListbox.setItemRenderer(new ListitemRenderer()
        {
            public void render(Listitem item, Object data) throws Exception
            {
                PermissionAll pall = (PermissionAll) data;
                item.setValue(pall);
                item.setHeight("25px");
                Listcell checkBox = new Listcell();
                Listcell nameCell = new Listcell();
                Listcell operaCell = new Listcell();
                Listcell editCell = new Listcell();
                nameCell.setLabel(pall.getKpName());

                List lOp = pall.getlPermissionOperation();
                Iterator itOpera = lOp.iterator();
                String strOps = "";
                while (itOpera.hasNext())
                {
                    Permissionoperation permissionoperation = (Permissionoperation) itOpera
                            .next();
                    Operation operation = operationManager
                            .get(permissionoperation.getKoid());
                    strOps += operation.getName() + ";";
                }
                if (!strOps.equals(""))
                {
                    strOps = strOps.substring(0, strOps.length() - 1);
                }
                operaCell.setLabel(strOps);

                Image editImg = new Image();
                editImg.setType("edit");
                editImg.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        updatePermission(event);
                    }
                });

                Image delImg = new Image();
                delImg.setType("delList");
                delImg.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {

                        delPermission(event);
                    }
                });

                Hbox hb = new Hbox();
                hb.appendChild(editImg);
                hb.appendChild(delImg);
                editCell.appendChild(hb);

                item.appendChild(checkBox);
                item.appendChild(nameCell);
                item.appendChild(operaCell);
                item.appendChild(editCell);
            }
        });
        try
        {
            reloadListbox();
        } catch (DataAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ObjectAlreadyExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void reloadListbox() throws InterruptedException, IOException, DataAccessException, ObjectAlreadyExistException, IllegalArgumentException, ObjectNotExistException
    {
        List lPermission = permissionManager.getPermissionByKridAndKrtype(resource.getId()+"","1");
        List lPall = new ArrayList();
        for (int i = 0; i < lPermission.size(); i++)
        {
            PermissionAll pall = new PermissionAll();
            pall = permissionoperationManager.getPermissionOperationByPermissionID(((Permission)lPermission.get(i)).getKpid());
            lPall.add(pall);
        }
        ListModelList PrimissionAllListModel = new ListModelList(lPall);
        permissionListbox.setModel(PrimissionAllListModel);
    }

    public void delAllPermission()
    {
        try
        {
            int isOk = Messagebox.show("是否删除选择的许可记录？",
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

        String ids = "";
        for (Listitem item : (Set<Listitem>) permissionListbox.getSelectedItems())
        {
            PermissionAll pall = (PermissionAll)item.getValue();
            
            if (pall != null)
            {

                ids += pall.getKpid() + ",";
            }

        }
        if(!ids.equals(""))
        {
            ids = ids.substring(0, ids.length() - 1);
        }
          
        try
        {
            this.permissionManager.delPermissionBykpids(ids); 
            Messagebox.show("许可删除成功", org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);

            reloadListbox();

        } catch (DataAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ObjectAlreadyExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 删除许可信息
     * 
     * @param event
     */
    public void delPermission(Event event)
    {
        try
        {
            int isOk = Messagebox.show("是否删除该条许可记录？",
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

        PermissionAll pa = new PermissionAll();
        pa =(PermissionAll) ((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue();
        Permission p = new Permission();
        p.setKpid(pa.getKpid());       
        try
        {
            this.permissionManager.delPermissionByPermission(p);
            Messagebox.show("许可删除成功", org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);

            reloadListbox();

        } catch (DataAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ObjectAlreadyExistException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 修改许可信息
     * 
     * @param event
     */
    public void updatePermission(Event event)
    {
        Map map = new HashMap();
        map.put("permissionall", ((Listitem) (event.getTarget().getParent()
                .getParent().getParent())).getValue());
        Window permissionUpdata = (Window) Executions.getCurrent()
                .createComponents("/apps/core/permissionUpdata.zul", this, map);
        permissionUpdata.setClosable(true);
        permissionUpdata.addEventListener(Events.ON_CHANGE, new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                permissions = permissionManager.getPermissionByKridAndKrtype(
                        ((Resource) Sessions.getCurrent().getAttribute(
                                "resource")).getId()
                                + "", "1");
                reloadListbox();
            }
        });
        try
        {
            permissionUpdata.setPosition("center");
            permissionUpdata.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addPermission()
    {
       
        Window permissionAdd = (Window) Executions.getCurrent()
                .createComponents("/apps/core/permissionAdd.zul", this, null);
        permissionAdd.setClosable(true);
        permissionAdd.addEventListener(Events.ON_CHANGE, new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                permissions = permissionManager.getPermissionByKridAndKrtype(
                        ((Resource) Sessions.getCurrent().getAttribute(
                                "resource")).getId()
                                + "", "1");
                reloadListbox();
            }
        });
        try
        {
            permissionAdd.setPosition("center");
            permissionAdd.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
