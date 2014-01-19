package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;


public class TreeData extends AbstractTreeModel{

	private SubjectTermSortService subjectTermSortService;
	
	public TreeData(Object root,SubjectTermSortService subjectTermSortService) {
		super(root);
		this.subjectTermSortService = subjectTermSortService;
	}

	private static final long serialVersionUID = 1L;

	public Object getChild(Object parent, int index) {
		if(parent instanceof WkTSubjectTermSort){
			WkTSubjectTermSort w=(WkTSubjectTermSort)parent;
			List<WkTSubjectTermSort> clist=subjectTermSortService.findByPid(w.getKsId());
			return clist.get(index);
		}else if(parent instanceof List){
			@SuppressWarnings("unchecked")
			List<WkTSubjectTermSort> s = (List<WkTSubjectTermSort>)parent;
			return s.get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if(parent instanceof WkTSubjectTermSort){
			WkTSubjectTermSort w = (WkTSubjectTermSort)parent;
			List<WkTSubjectTermSort> clist = subjectTermSortService.findByPid(w.getKsId());
			return clist.size();
		}else if(parent instanceof List){
			@SuppressWarnings("unchecked")
			List<WkTSubjectTermSort> l=(List<WkTSubjectTermSort>)parent;
			return l.size();
		}
		return 1;
	}

	public boolean isLeaf(Object node) {
		if(node instanceof WkTSubjectTermSort){
			WkTSubjectTermSort w = (WkTSubjectTermSort)node;
			List<WkTSubjectTermSort> clist=subjectTermSortService.findByPid(w.getKsId());
			return clist.size()>0?false:true;
		}else if(node instanceof List){
			@SuppressWarnings("unchecked")
			List<WkTSubjectTermSort> clist=(List<WkTSubjectTermSort>)node;
			return clist.size()>0?false:true;
		}
		return true;
	}

}
