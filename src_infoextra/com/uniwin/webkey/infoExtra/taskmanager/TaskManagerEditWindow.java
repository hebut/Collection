package com.uniwin.webkey.infoExtra.taskmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IPermissionOperationManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.itf.TreeService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.task.TaskManageModel;
import com.zkoss.org.messageboxshow.MessageBox;


public class TaskManagerEditWindow extends Window implements AfterCompose{

	private Users user;
	Session currSession = Sessions.getCurrent();
	WkTWebsite website;
	
	private Object  obj = (Object)Executions.getCurrent().getArg().get("editchanel");
	Textbox tname,desc;
	Listbox audit;
	Listitem ok,no;
	Label nothing;
	private Tree  managetree;
	List clist;
	TaskManageModel tManageModel;
	Center taskMCen;
	East taskEast;
	TaskManageAddWindow taManageAddWindow;
	
	Listitem isAudit;
    IPermissionOperationManager poManager = (IPermissionOperationManager)SpringUtil.getBean("permissionOperationManager");
	
	TaskService  taskService = (TaskService)SpringUtil.getBean("taskService");
	TreeService treeService=(TreeService)SpringUtil.getBean("treeService");
	
	public void afterCompose() {
		
		Components.wireVariables(this, this);
        Components.addForwards(this, this);
        user = (Users) currSession.getAttribute("users");
        website = (WkTWebsite)currSession.getAttribute("domain_defult");
	}

	public void setEditTask(Object object) {
		// TODO Auto-generated method stub
		if(object instanceof WkTExtractask){
			WkTExtractask t=(WkTExtractask)object;
			tname.setValue(t.getKeName());
			audit.setVisible(false);
			isAudit.setVisible(false);
			desc.setValue(t.getKeRemk());
//			nothing.setVisible(true);
		}else if(object instanceof WkTChanel){
			
			WkTChanel t=(WkTChanel)object;
			tname.setValue(t.getKcName());
			audit.setVisible(true);
			nothing.setVisible(false);
			desc.setValue(t.getKcDesc());
			if(t.getKfId().toString().trim().equals("0")){
				no.setSelected(true);
			}else if(t.getKfId().toString().trim().equals("1")){
				ok.setSelected(true);
			}
			
		}
	}
	
	 /**
     * <li>����������Դ��Ȩ
     * @serialData 2011-2-22
     */
    public void onClick$authTask()
    {
    	String name = null;
    	Integer id = null;
    	if(obj instanceof WkTExtractask){
    		WkTExtractask e=(WkTExtractask)obj;
    		name=e.getKeName();
    		id=Integer.parseInt(e.getKeId()+"");
    	}else if(obj instanceof WkTChanel){
    		WkTChanel t=(WkTChanel)obj;
    		name=t.getKcName();
    		id=Integer.parseInt(t.getKcId()+"");
    	}
    	Map map = new HashMap();
    	map.put("taskName", name);
    	map.put("taskId", id);
    	Component c = Executions.createComponents("/apps/infoExtra/content/taskmanager/authorizeTask.zul",null, map);
        if (c != null)
        {
        	AuthorizeTaskWindow win = (AuthorizeTaskWindow) c;
        	win.doHighlighted();
        }
  
    }
	
    
    public void onClick$save(){
    	
    	if(tname.getValue().equals("")){
    		MessageBox.showWarning("请输入分类名称!");
    	}else{
    	
    	if(obj instanceof WkTChanel){
    		WkTChanel chanel=(WkTChanel)obj;
    		if(chanel.getKcPid().equals(Long.parseLong("0"))){
    			MessageBox.showWarning("根文件夹不能修改！");
    		}else{
    			chanel.setKcName(tname.getValue());
    			if(ok.isSelected()){
    				chanel.setKfId(Long.parseLong("1"));
    			}else if(no.isSelected()){
    				chanel.setKfId(Long.parseLong("0"));
    			}
    			chanel.setKcDesc(desc.getValue());
    			treeService.update(chanel);
    			MessageBox.showInfo("保存成功！");
        		try {
    				inittree();
    			} catch (DataAccessException e) {
    				e.printStackTrace();
    			} catch (ObjectNotExistException e) {
    				e.printStackTrace();
    			} catch (ObjectAlreadyExistException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
    			
    			
    		}
    	}else if(obj instanceof WkTExtractask){
    		WkTExtractask extractask=(WkTExtractask)obj;
    		extractask.setKeName(tname.getValue());
    		extractask.setKeRemk(desc.getValue());
    		taskService.update(extractask);
    		MessageBox.showInfo("保存成功！");
    		try {
				inittree();
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (ObjectNotExistException e) {
				e.printStackTrace();
			} catch (ObjectAlreadyExistException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
    		
    	}
    		
      }
    	
   }

    public void inittree() throws DataAccessException, ObjectNotExistException, ObjectAlreadyExistException, IllegalArgumentException 
	 {
		 
		 clist=taskService.getChildType(Long.parseLong("0"));
		 tManageModel=new TaskManageModel(clist,taskService);
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
				}
				if(clist!=null && clist.size()!=0){
					if(data instanceof WkTChanel){
						
					}else if(data instanceof WkTExtractask){
						
					}
				}
			
			}
			 
		 });
		 if(managetree.getSelectedItem()!=null)
			{
			 Object o=managetree.getSelectedItem().getValue();
			 	windowsLoad(o);
			}else
			{
				if(clist!=null && clist.size()>0){
					windowsLoad(clist.get(0));
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
