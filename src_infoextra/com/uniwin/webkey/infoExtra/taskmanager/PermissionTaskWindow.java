package com.uniwin.webkey.infoExtra.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.content.chanel.win.PermissionChanelEditWin;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.PermissionAll;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;

public class PermissionTaskWindow extends Window implements AfterCompose{

	private Listbox perTaskListbox;
	
	private IPermissionManager iPermissionManager = (IPermissionManager)SpringUtil.getBean("permissionManager");
	private IOperationManager operationManager = (IOperationManager) SpringUtil.getBean("operationManager");
	private IPermissionOperationManager permissionoperationManager = 
		(IPermissionOperationManager) SpringUtil.getBean("permissionOperationManager");
	
	private Object obj;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		perTaskListbox.setItemRenderer(new perItemRenderer());
	}

	
	
	/**
	 * 许可列表渲染器
	 */
	public class perItemRenderer implements ListitemRenderer 
	{
		public void render(Listitem item, Object data) throws Exception 
		{
            PermissionAll pall = (PermissionAll) data;
            item.setValue(pall);
            item.setHeight("25px");
            Listcell c = new Listcell("");
            Listcell nameCell = new Listcell();
            Listcell operaCell = new Listcell();
            Listcell editCell = new Listcell();
            nameCell.setLabel(pall.getKpName());

            List lOp = pall.getlPermissionOperation();
            Iterator itOpera = lOp.iterator();
            String strOps = "";
            while (itOpera.hasNext())
            {
                Permissionoperation permissionoperation = (Permissionoperation) itOpera.next();
                Operation operation = operationManager.get(permissionoperation.getKoid());
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

            item.appendChild(c);
            item.appendChild(nameCell);
            item.appendChild(operaCell);
            item.appendChild(editCell);
        }

	}
	
	
	/**
	 * 加载许可列表
	 * @param chanel
	 * 			许可对应的栏目
	 * @throws DataAccessException
	 * @throws ObjectAlreadyExistException
	 * @throws IllegalArgumentException
	 * @throws ObjectNotExistException
	 */
	public void perListboxLoad(Object obj) throws DataAccessException, ObjectAlreadyExistException, IllegalArgumentException, ObjectNotExistException
	{
		List perList = new ArrayList();
		if(obj!=null){
			this.obj=obj;
			String id = null;
			if(obj instanceof WkTExtractask){
				WkTExtractask e=(WkTExtractask)obj;
				id=e.getKeId()+"";
			}else if(obj instanceof WkTChanel){
				WkTChanel t=(WkTChanel)obj;
				id=t.getKcId()+"";
			}
			
			perList = iPermissionManager.getPermissionByKridAndKrtype(id, "2");
		}
		List pallList = new ArrayList();
        for (int i = 0; i < perList.size(); i++)
        {
            PermissionAll pall = new PermissionAll();
            pall = permissionoperationManager.getPermissionOperationByPermissionID(((Permission)perList.get(i)).getKpid());
            pallList.add(pall);
        }
		ListModelList perListModel = new ListModelList(pallList);
		perTaskListbox.setModel(perListModel);
	}
	
	
	/**
	 * 打开许可添加窗口
	 */
	public void addPermission(){
		PermissionTaskAddWin permissionAdd = (PermissionTaskAddWin) Executions.getCurrent().
			createComponents("/apps/infoExtra/content/taskmanager/permissionTaskAdd.zul", this, null);
		permissionAdd.setObj(obj);
		permissionAdd.setClosable(true);
		try {
			permissionAdd.doModal();
		} catch (SuspendNotAllowedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		permissionAdd.addEventListener(Events.ON_CHANGE, new EventListener()
		{
			public void onEvent(Event arg0) throws Exception 
			{
				perListboxLoad(obj);
				
			}
		});
	}
	
	/**
	 * 批量删除选定的许可
	 */
	public void delAllPermission()
	{
        try
        {
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.isDeleteSelectPermission"),org.zkoss.util.resource.Labels
                       	.getLabel("system.commom.ui.prompt"), Messagebox.OK| Messagebox.CANCEL, Messagebox.QUESTION);
            if (isOk == 2 || isOk == 0)
            {
                return;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        String ids = "";
        for (Listitem item : (Set<Listitem>) perTaskListbox.getSelectedItems())
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
            this.iPermissionManager.delPermissionBykpids(ids); 
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.DeletePermissionSuccess"), org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,Messagebox.INFORMATION);
            perListboxLoad(obj);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }catch (ObjectAlreadyExistException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
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
        map.put("editChanel", obj);
        PermissionChanelEditWin editWin = (PermissionChanelEditWin) Executions.getCurrent()
                .createComponents("/apps/cms/content/chanel/permissionChanelEdit.zul", this, map);
        editWin.setClosable(true);
        editWin.addEventListener(Events.ON_CHANGE, new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
            	perListboxLoad(obj);
            }
        });
        try
        {
        	editWin.setPosition("center");
        	editWin.doModal();
        } catch (Exception e)
        {
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
            int isOk = Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.isDeleteThisPermission"),
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
            this.iPermissionManager.delPermissionByPermission(p);
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.DeletePermissionSuccess"), org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,Messagebox.INFORMATION);

            perListboxLoad(obj);

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }catch (ObjectAlreadyExistException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }
	
	
}
