package com.uniwin.webkey.infoExtra.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;

public class PermissionTaskAddWin extends Window implements AfterCompose{

	private Textbox  name, code;
	private Combobox rtype, otype;
	private Listbox opListbox;
	private List operationList  = new ArrayList();
	private IOperationManager operationManager = (IOperationManager) SpringUtil.getBean("operationManager");
	private IPermissionManager permissionManager = (IPermissionManager) SpringUtil.getBean("permissionManager");
	private IPermissionOperationManager permissionoperationManager = 
		 (IPermissionOperationManager) SpringUtil.getBean("permissionOperationManager");
	Object obj;
	
		
	public Object getObj() {
		return obj;
	}


	public void setObj(Object obj) {
		this.obj = obj;
	}


	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		rtype.setSelectedIndex(1);
		otype.setSelectedIndex(1);
		this.opListbox.setItemRenderer(new OPListitemRenderer());
		try {
			loadListbox();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadListbox() throws DataAccessException
	{
		operationList = operationManager.getAllOperationListOrderByOperationName();
		ListModelList opListModel = new ListModelList(operationList);
		this.opListbox.setModel(opListModel);
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
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.permissionNameDontNull"), org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }
        if (code.getValue().equals(""))
        {
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("chanel.ui.permissionCodeDontNull"), org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.prompt"), Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }
        Long tid = null;
        if(obj instanceof WkTExtractask){
        	WkTExtractask e=(WkTExtractask)obj;
        	tid=e.getKeId();
        }else if(obj instanceof WkTChanel){
        	WkTChanel t=(WkTChanel)obj;
        	tid=t.getKcId();
        }
        permission.setKrid(tid.intValue());
        permission.setKrtype(this.rtype.getSelectedItem().getValue().toString());
        try
        {
            permissionManager.add(permission);
            Set set = this.opListbox.getSelectedItems();
            Iterator it = set.iterator();
            while (it.hasNext())
            {
                Listitem listitem = (Listitem) it.next();
                Operation oPeration = (Operation) listitem.getValue();
                Permissionoperation po = new Permissionoperation();
                po.setKpid(permission.getKpid());
                po.setKptype(this.otype.getSelectedItem().getValue().toString());
                po.setKporder(oPeration.getOperationId() + "");
                po.setKoid(oPeration.getOperationId());
                permissionoperationManager.add(po);
            }
            Messagebox.show(org.zkoss.util.resource.Labels.getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels.getLabel("system.commom.ui.prompt"),
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
    
	
	/**
	 * 操作列表渲染器
	 */
	public class OPListitemRenderer implements ListitemRenderer {

		public void render(Listitem item, Object data) throws Exception {
			Operation op = (Operation)data;
			item.setValue(op);
			Listcell c0 = new Listcell();
			Listcell c1 = new Listcell(item.getIndex()+1+"");
			Listcell c2 = new Listcell(op.getName());
			Listcell c3 = new Listcell(op.getOpCode());
			item.appendChild(c0);
			item.appendChild(c1);
			item.appendChild(c2);
			item.appendChild(c3);		
		}
	}

}
