package com.uniwin.webkey.core.ui;

import java.util.Iterator;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.model.Organization;


public class OrgnazationSortWindow extends Window implements AfterCompose
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Listbox sortList;
	ListModelList sortModel;
	 List orgList; 
	 Organization org,porg;
	 private IOrganizationManager organizationManager;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		organizationManager = (IOrganizationManager) SpringUtil.getBean("organizationManager");
		sortList.setItemRenderer(new ListitemRenderer()
		{
			public void render(Listitem item, Object data) throws Exception {
				Organization org=(Organization)data;
				item.setValue(org);
				 Listcell c0=new Listcell(item.getIndex()+1+"");
				 item.appendChild(c0);
				if(org.getParentId()!=-1)
				{
					List olist= organizationManager.getOrgById(org.getParentId());
				    porg= (Organization) olist.get(0);
				    Listcell c1=new Listcell(porg.getName()+"---"+org.getName());
				    item.appendChild(c1);
				}
				else
				{
					Listcell c1=new Listcell(org.getName());
					 item.appendChild(c1);
				}
				 item.setHeight("20px");
				 item.setDraggable("true");
				 item.setDroppable("true");
				 item.addEventListener(Events.ON_DROP, new EventListener() {
						public void onEvent(Event arg0) throws Exception {
							DropEvent event = (DropEvent) arg0;
							Component self = (Component) arg0.getTarget();
							Listitem dragged = (Listitem) event.getDragged();
							if (self instanceof Listitem) {
								self.getParent().insertBefore(dragged,
										self.getNextSibling());
								initListbox();
							} else {
								self.appendChild(dragged);
							}
						}
					});
				
			}
		});
		sortModel=new ListModelList();
	}
	
	public void initListbox() {
		Iterator<Listitem> items = sortList.getItems().iterator();
		orgList.clear();
		while (items.hasNext()) {
			orgList.add((Organization) items.next().getValue());
		}
		sortList.setModel(new ListModelList(orgList));
	}

	public void initWindow(Organization org) throws DataAccessException {		
		this.org = org;
		reloadList();
	}
	
	public void onClick$submit() throws DataAccessException, ObjectNotExistException{
		List itemList=sortList.getItems();
		if(itemList.size()>0){
		  for(int i=0;i<itemList.size();i++){
			Listitem item=(Listitem)itemList.get(i);
			Organization c=(Organization)item.getValue();
			int j=i+1;
			c.setOrder(j);
			organizationManager.update(c);
		  }
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
	
	/**
	 * <li>功能描述：加载要排序的栏目列表
	 * void 
	 * @author whm
	 * @throws DataAccessException 
	 * @2010-7-22
	 */
	private void reloadList() throws DataAccessException{
		sortModel.clear();
		orgList = organizationManager
                 .getChildrenByParentId(((Organization) org).getParentId());
		sortModel.addAll(orgList);	
	    sortList.setModel(sortModel);
	}
	
	public void onClick$reset() throws DataAccessException{
		initWindow(org);		
	}
	
	public void onClick$close(){
		this.detach();
	}
}