package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * <li>初始化左侧的栏目树
 * @2010-3-17
 * @author whm
 */
import java.util.List;
import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;



public class NewsauditTreeModel  extends AbstractTreeModel{	
	
	TaskService taskService;
	public NewsauditTreeModel(Object root,TaskService taskService) {
		super(root);		
		this.taskService=taskService;	 	
	}
	private static final long serialVersionUID = 5665716328057383820L;
	//有了父节点后，获得孩子节点，index为第几个孩子
	public Object getChild(Object parent, int index) {
		if(parent instanceof WkTChanel)
		{
			WkTChanel n=(WkTChanel)parent;
			if(taskService.getChildType(n.getKcId()).size()==0)
			{
			return taskService.getChildTask(n.getKcId()).get(index);
			}
			else if (taskService.getChildType(n.getKcId()).size()!=0)
			{
				return taskService.getChildType(n.getKcId()).get(index);
			}
		}
		else if(parent instanceof WkTExtractask)
		{
			return null;
		}
		else if(parent instanceof List){
			List clist=(List)parent;
			return clist.get(index);
		}
		return null;
	}
	 //返回父节点的孩子节点数目
	public int getChildCount(Object parent) {
		if(parent instanceof WkTChanel)
		{
			WkTChanel n=(WkTChanel)parent;
			if(taskService.getChildType(n.getKcId()).size()==0)
			{
			return taskService.getChildTask(n.getKcId()).size();
			}
			else if(taskService.getChildType(n.getKcId()).size()!=0)
			{
				return taskService.getChildType(n.getKcId()).size();
			}
		}
		else if(parent instanceof WkTExtractask)
		{
			return 0;
		}
		else if(parent instanceof List){
			List clist=(List)parent;
			return clist.size();
		}
		return 0;	
	}
	//判断某节点是否为叶子节点
	public boolean isLeaf(Object node) {	
		
		 if(node instanceof WkTChanel){
			 WkTChanel n=(WkTChanel)node;
			 if(taskService.getChildType(n.getKcId()).size()==0)
			 {	 
				 return taskService.getChildTask(n.getKcId()).size()>0?false:true;
			 }
				
				else if(taskService.getChildType(n.getKcId()).size()!=0)
				{
					return taskService.getChildType(n.getKcId()).size()>0?false:true;
				}
		 }
		 else if(node instanceof WkTExtractask)
			{
				return true;
			}
		 else if(node instanceof List){
			List clist=(List)node;
			return clist.size()>0?false:true;
		}
		return true;
	}
}
