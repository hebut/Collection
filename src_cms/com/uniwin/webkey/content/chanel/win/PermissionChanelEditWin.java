package com.uniwin.webkey.content.chanel.win;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
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
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IOperationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Operation;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.PermissionAll;
import com.uniwin.webkey.core.model.Permissionoperation;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;

public class PermissionChanelEditWin extends Window implements AfterCompose 
{
	private static final long serialVersionUID = 5719584951349425230L;
	private Textbox  name, code;
	private Combobox rtype, otype;
	private Listbox opListbox;
	private List operationList  = new ArrayList();
	private IOperationManager operationManager = (IOperationManager) SpringUtil.getBean("operationManager");
	private IPermissionManager permissionManager = (IPermissionManager) SpringUtil.getBean("permissionManager");
	private IPermissionOperationManager permissionoperationManager = 
		 (IPermissionOperationManager) SpringUtil.getBean("permissionOperationManager");
	
	PermissionAll pall = new PermissionAll();
	Permission permission = new Permission();
	WkTChanel chanel = new WkTChanel();
	WkTExtractask etask=new WkTExtractask();
	private Object object;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		opListbox.setItemRenderer(new OPListitemRenderer());
		pall=(PermissionAll)Executions.getCurrent().getArg().get("permissionall");
		object = (Object)Executions.getCurrent().getArg().get("editChanel");
		permission.setKpid(pall.getKpid());
		try {
			initWindow();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载许可编辑窗口
	 * @throws DataAccessException
	 */
	public void initWindow() throws DataAccessException
	{
		code.setValue(pall.getKpCode());
		name.setValue(pall.getKpName());
		if (pall.getKrtype().equals("1"))
        {
            rtype.setSelectedIndex(0);
        } else
        {
            rtype.setSelectedIndex(1);
        }
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
		operationList = operationManager.getAllOperationListOrderByOperationName();
		ListModelList opListModel = new ListModelList(operationList);
		opListbox.setModel(opListModel);
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
        if(object instanceof WkTChanel)
        {
        	chanel=(WkTChanel)object;
        permission.setKrid(chanel.getKcId().intValue());
        }
        else if(object  instanceof WkTExtractask)
        {
        	etask=(WkTExtractask)object;
        	 permission.setKrid(etask.getKeId().intValue());
        }
        permission.setKrtype(this.rtype.getSelectedItem().getValue().toString());
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
	
	public List getOperationList()
    {
        return operationList;
    }

    public void setOperationList(List operationList)
    {
        this.operationList = operationList;
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
    		boolean bSelect = false;
    		List lpo = pall.getlPermissionOperation();
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
    		item.appendChild(c0);
    		item.appendChild(c1);
    		item.appendChild(c2);
    		item.appendChild(c3);		
    	}
    }
}
