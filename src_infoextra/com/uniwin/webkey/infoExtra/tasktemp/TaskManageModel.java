package com.uniwin.webkey.infoExtra.tasktemp;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;

public class TaskManageModel extends AbstractTreeModel{

	 TaskService  taskService;
	 
	public TaskManageModel(Object root,TaskService taskService) {
		super(root);
		this.taskService=taskService;
	}

	private static final long serialVersionUID = 1L;

	public Object getChild(Object parent, int index) {
		// TODO Auto-generated method stub
		if(parent instanceof WkTChanel){
			WkTChanel tasktype=(WkTChanel)parent;
			if(taskService.getChildType(tasktype.getKcId()).size()==0)
			{
			return taskService.getChildTask(tasktype.getKcId()).get(index);
			}
			else if (taskService.getChildType(tasktype.getKcId()).size()!=0)
			{
				return taskService.getChildType(tasktype.getKcId()).get(index);
			}
		}else if(parent instanceof WkTExtractask){
			return null;
		}else if(parent instanceof List){
			List t=(List)parent;
			return t.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub
		if(parent instanceof WkTChanel){
			WkTChanel tasktype=(WkTChanel)parent;
			if(taskService.getChildType(tasktype.getKcId()).size()==0)
			{
			return taskService.getChildTask(tasktype.getKcId()).size();
			}
			else if (taskService.getChildType(tasktype.getKcId()).size()!=0)
			{
				return taskService.getChildType(tasktype.getKcId()).size();
			}
		}else if(parent instanceof WkTExtractask){
			
			return 0;
			
		}else if(parent instanceof List){
			List t=(List)parent;
			return t.size();
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		if(node instanceof WkTChanel){
			WkTChanel tasktype=(WkTChanel)node;
			if(taskService.getChildType(tasktype.getKcId()).size()==0)
			{
			return taskService.getChildTask(tasktype.getKcId()).size()>0?false:true;
			}
			else if (taskService.getChildType(tasktype.getKcId()).size()!=0)
			{
				return taskService.getChildType(tasktype.getKcId()).size()>0?false:true;
			}
		}else if(node instanceof WkTExtractask){
			
			return true; 
			
		}else if(node instanceof List){
			List t=(List)node;
			return t.size()>0?false:true;
		}
		return true;
	}

}
