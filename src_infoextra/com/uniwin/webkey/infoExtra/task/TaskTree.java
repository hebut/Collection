package com.uniwin.webkey.infoExtra.task;


import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.task.TaskItemRenderer;
import com.uniwin.webkey.infoExtra.task.TasktypeTreeModel;
import com.zkoss.org.messageboxshow.MessageBox;

public class TaskTree extends Window implements AfterCompose{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Tree subTree;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	List clist;
	WkTChanel type;
	TasktypeTreeModel ctm;
	WkTExtractask task;
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();	
	}

	private void initTree() {
		
		subTree.getPagingChild().setMold("os");
		clist=taskService.getChildType(Long.parseLong(0+""));
		ctm=new TasktypeTreeModel(clist,taskService);
		subTree.setModel(ctm);
		List tList=taskService.findAllTaskOrder();
		
		if(tList!=null && tList.size()>0){
			WkTExtractask e=(WkTExtractask)tList.get(0);
			type=taskService.findByFolderID(e.getKcId());
		}
		
		subTree.setTreeitemRenderer(new TaskItemRenderer()
	     {
	    		public void render(final Treeitem item, Object data) throws Exception {
	    			final WkTChanel task=(WkTChanel)data;
	    			item.setValue(task);
	    			item.setLabel(task.getKcName());
	    			item.setOpen(true);
					
	    		}
	     });
	}

	
	
	public void onClick$ok(){
			if(subTree.getSelectedItem()==null){
				MessageBox.showInfo("请选择采集方向！");
				return;
			}else{
				WkTChanel chanel=(WkTChanel) subTree.getSelectedItem().getValue();
				task.setKcId(chanel.getKcId());
				taskService.update(task);
				Events.postEvent(Events.ON_CHANGE, this, null);
				MessageBox.showInfo("保存成功！");
				this.detach();
			}		
		
	}
	
	
	public void onClick$canel(){
		this.detach();
	}

	public void initWin(WkTExtractask extractask) {
		this.task = extractask;
	}
	
	
}
