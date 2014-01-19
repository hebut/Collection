package com.uniwin.webkey.infoExtra.task;

/**
 * <li>分类排序
 */

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.MLogService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTMlog;
import com.uniwin.webkey.cms.model.WkTChanel;


public class TasktypeSortWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	WkTChanel chanel;	 
	WkTChanel task;
	Users user; 
	Listbox sortList;
	ListModelList sortModel;
	List userDeptList; 
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	private MLogService mlogService=(MLogService)SpringUtil.getBean("info_mlogService");
	 
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);	
		user=(Users)Sessions.getCurrent().getAttribute("users");
		userDeptList=(List)Sessions.getCurrent().getAttribute("userDeptList");
		sortList.setItemRenderer(new TaskListItemRenderer());
	}
	public WkTChanel getTask() {
		return task;
	}
	public void initWindow(WkTChanel task) {		
		this.task = task;
		reloadList();
	}
	/**保存排序结果*/
	public void onClick$submit(){
		List itemList=sortList.getItems();
		StringBuffer sb=new StringBuffer("编辑分类顺序,ids:");
		if(itemList.size()>0){
		  for(int i=0;i<itemList.size();i++){
			Listitem item=(Listitem)itemList.get(i);
			WkTChanel c=(WkTChanel)item.getValue();
			int j=i+1;
			c.setKcOrdno(Long.parseLong(j+""));
			taskService.update(c);
			sb.append(c.getKcId()+",");
		  }
		}
		mlogService.saveMLog(WkTMlog.FUNC_CMS, sb.toString(), user);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
	
	/**
	 * <li>功能描述：加载要排序的分类列表
	 */
	private void reloadList(){
		sortModel=new ListModelList();
		sortModel.clear();
		 List plist;
		 plist=taskService.getChildType(task.getKcPid());
		  sortModel.addAll(plist);	
	    sortList.setModel(sortModel);
	}
	
	/**
	 * 重置
	 */
	public void onClick$reset(){
		initWindow(getTask());		
	}
	
	public void onClick$close(){
		this.detach();
	}
}
