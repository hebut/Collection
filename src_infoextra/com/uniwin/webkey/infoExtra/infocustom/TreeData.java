package com.uniwin.webkey.infoExtra.infocustom;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;


public class TreeData extends AbstractTreeModel{

	InfoSortService infosortService;
	
	public TreeData(Object root,InfoSortService infosortService) {
		super(root);
		this.infosortService=infosortService;
	}

	private static final long serialVersionUID = 1L;

	public Object getChild(Object parent, int index) {
		if(parent instanceof WkTInfoSort){
			WkTInfoSort w=(WkTInfoSort)parent;
			List clist=infosortService.findByPid(w.getKsId());
			return clist.get(index);
		}else if(parent instanceof List){
			List s=(List)parent;
			return s.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if(parent instanceof WkTInfoSort){
			WkTInfoSort w=(WkTInfoSort)parent;
			List clist=infosortService.findByPid(w.getKsId());
			return clist.size();
		}else if(parent instanceof List){
			List l=(List)parent;
			return l.size();
		}
		return 1;
	}

	public boolean isLeaf(Object node) {
		if(node instanceof WkTInfoSort){
			WkTInfoSort w=(WkTInfoSort)node;
			List clist=infosortService.findByPid(w.getKsId());
			return clist.size()>0?false:true;
		}else if(node instanceof List){
			List clist=(List)node;
			return clist.size()>0?false:true;
		}
		
		return true;
	}

	

}
