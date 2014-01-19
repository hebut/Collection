package com.uniwin.webkey.infoExtra.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.East;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.newspub.NewsTreeModel;
import com.uniwin.webkey.infoExtra.taskmanager.PermissionTaskWindow;
import com.uniwin.webkey.infoExtra.taskmanager.TaskManageAddWindow;
import com.uniwin.webkey.infoExtra.taskmanager.TaskManagerEditWindow;



public class TasktypeTreeManage extends Window implements AfterCompose{

	
	private static final long serialVersionUID = 1L;
	Center taskMCen;
	 East taskEast;
	 
	 TaskManageModel tManageModel;
	 
	 Tree managetree;
	 TaskService  taskService = (TaskService)SpringUtil.getBean("taskService");
	 TaskManageAddWindow taManageAddWindow;
	 
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		
		try {
			inittree();
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (ObjectNotExistException e) {
			e.printStackTrace();
		}
		
		managetree.getPagingChild().setMold("os");
		managetree.addEventListener(Events.ON_SELECT, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				windowsLoad(managetree.getSelectedItem().getValue());
			}
		});
		
	}
	
	
	 public void inittree() throws DataAccessException, ObjectNotExistException {
		 
		 List  nlist=taskService.getChildType(Long.parseLong("0"));
		 tManageModel=new TaskManageModel(nlist,taskService);
		 managetree.setModel(tManageModel);
		 managetree.setTreeitemRenderer(new TreeitemRenderer(){

			public void render(Treeitem item, Object data) throws Exception {
				
				if(data instanceof WkTChanel){
					WkTChanel tasktype=(WkTChanel)data;
					item.setValue(tasktype);
					if(tasktype.getKcName().trim().length()>5)
	    			{
	    			item.setLabel(tasktype.getKcName().substring(0, 5)+"...");
	    			item.setTooltiptext(tasktype.getKcName());
	    			}
	    			else 
	    			{
	    				item.setLabel(tasktype.getKcName());
	    			}
					item.setOpen(true);
				}else if(data instanceof WkTExtractask){
					WkTExtractask task = (WkTExtractask) data;
					item.setValue(task);
					if(task.getKeName().trim().length()>5)
	    			{
	    			item.setLabel(task.getKeName().substring(0, 5)+"...");
	    			item.setTooltiptext(task.getKeName());
	    			}
	    			else 
	    			{
	    				item.setLabel(task.getKeName());
	    			}
					item.setOpen(true);
				}
			
			}
			 
		 });
		 
		 
		 if(managetree.getSelectedItem()!=null)
			{
			 Object o=managetree.getSelectedItem().getValue();
			 	try {
					windowsLoad(o);
				} catch (ObjectAlreadyExistException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}else
			{
				if(nlist!=null && nlist.size()>0){
					try {
						windowsLoad(nlist.get(0));
					} catch (ObjectAlreadyExistException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}	
		 
	 }
	 
	 
	 public void windowsLoad(Object Object) throws DataAccessException, ObjectAlreadyExistException, IllegalArgumentException, ObjectNotExistException
		{
		 	taskMCen.getChildren().clear();
		 	taskEast.getChildren().clear();
			if(Object==null)
			{
				taskMCen.setTitle(org.zkoss.util.resource.Labels
						.getLabel("chanel.ui.newChanel"));
				taManageAddWindow = (TaskManageAddWindow) Executions.createComponents(
						"/apps/infoExtra/content/taskmanager/taskadd.zul", taskMCen, null);
//				taManageAddWindow.setEditChanel(null);
				taManageAddWindow.addEventListener(Events.ON_CHANGE, new EventListener()
				{
					public void onEvent(Event arg0) throws Exception 
					{
						inittree();
						openTree(managetree.getTreechildren());
					}
				});
				
			}else
			{
				String name;
				if(Object instanceof WkTExtractask){
					WkTExtractask t=(WkTExtractask)Object;
					name=t.getKeName();
				}else{
					WkTChanel t=(WkTChanel)Object;
					name=t.getKcName();
				}
				taskMCen.setTitle(org.zkoss.util.resource.Labels
		                .getLabel("chanel.ui.operationChanelName")+" : "+name);
				
				//此处不能随便删啊，
				Map map = new HashMap();
		        map.put("editchanel", Object);
				
				TaskManagerEditWindow tEditWindow  = (TaskManagerEditWindow)Executions.createComponents(
						"/apps/infoExtra/content/taskmanager/task.zul", taskMCen,map);
				tEditWindow.setEditTask(Object);
				tEditWindow.afterCompose();
			}
			PermissionTaskWindow pcWin  = (PermissionTaskWindow)Executions.createComponents(
					"/apps/infoExtra/content/taskmanager/permissionTaskList.zul", taskEast,null);
			pcWin.perListboxLoad(Object);
			pcWin.afterCompose();
			
		}
	 
	 
	 	/*
	     * <li>功能描述：将树节点展开。
	     */
	    private void openTree(Treechildren chi)
	    {
	        if (chi == null)
	            return;
	        List clist = chi.getChildren();
	        for (int i = 0; i < clist.size(); i++)
	        {
	            Treeitem item = (Treeitem) clist.get(i);
	            item.setOpen(true);
	            openTree(item.getTreechildren());
	        }
	    }
	
}

	
	
	