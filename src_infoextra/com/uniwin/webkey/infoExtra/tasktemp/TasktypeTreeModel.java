package com.uniwin.webkey.infoExtra.tasktemp;
/**
 * <li>站点树模型，
 */

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.cms.model.WkTChanel;



public class TasktypeTreeModel extends AbstractTreeModel {

	private static final long serialVersionUID = -9031880675642847049L;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");

	public TasktypeTreeModel(Object root, TaskService taskService) {
		super(root);
		this.taskService = taskService;
	}
	//由父栏目对象找孩子栏目对象
	public Object getChild(Object parent, int index) {
		if (parent instanceof WkTChanel) {
			WkTChanel w = (WkTChanel) parent;
			return taskService.getChildType(w.getKcId()).get(index);
		} 
		else if (parent instanceof List) {
			List clist = (List) parent;
			return clist.get(index);
		}
		return null;
	}
//获得孩子的数目
	public int getChildCount(Object parent) {
		if (parent instanceof WkTChanel) {
			WkTChanel w = (WkTChanel) parent;
			return taskService.getChildType(w.getKcId()).size();
		} else if (parent instanceof List) {
			List clist = (List) parent;
			return clist.size();
		}
		return 0;
	}
//判断当前节点是否为叶子节点
	public boolean isLeaf(Object node) {
		if (node instanceof WkTChanel) {
			WkTChanel w = (WkTChanel) node;
			return taskService.getChildType(w.getKcId()).size() > 0 ? false
					: true;
		} else if (node instanceof List) {
			List clist = (List) node;
			return clist.size() > 0 ? false : true;
		}
		return true;
	}
}
