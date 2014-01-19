package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;

public class SubjectTermTreeModel extends AbstractTreeModel {

	private static final long serialVersionUID = 7217784855235434754L;
	private SubjectTermService subjectTermService;
    
	public SubjectTermTreeModel(Object root,SubjectTermService subjectTermService) {
		super(root);
		this.subjectTermService = subjectTermService;
	}

	public Object getChild(Object parent, int index) {
		if(parent instanceof WkTSubjectTerm){
			WkTSubjectTerm w = (WkTSubjectTerm)parent;
			List<WkTSubjectTerm> clist = subjectTermService.findByPid(w.getKstId());
			return clist.get(index);
		}
		else{
			if(parent instanceof List){
				@SuppressWarnings("unchecked")
				List<WkTSubjectTerm> l = (List<WkTSubjectTerm>)parent;
				return l.get(index);
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		
		if(parent instanceof WkTSubjectTerm){
			WkTSubjectTerm w = (WkTSubjectTerm)parent;
			List<WkTSubjectTerm> clist = subjectTermService.findByPid(w.getKstId());
			return clist.size();
		}
		else{
			if(parent instanceof List){
				@SuppressWarnings("unchecked")
				List<WkTSubjectTerm> l = (List<WkTSubjectTerm>)parent;
				return l.size();
			}
		}
		return 1;
	}

	public boolean isLeaf(Object node) {
		
		if(node instanceof WkTSubjectTerm){
			WkTSubjectTerm w = (WkTSubjectTerm)node;
			List<WkTSubjectTerm> clist = subjectTermService.findByPid(w.getKstId());
			return clist.size()>0?false:true;
		}
		else{
			if(node instanceof List){
				@SuppressWarnings("unchecked")
				List<WkTSubjectTerm> l = (List<WkTSubjectTerm>)node;
				return l.size()>0?false:true;
			}
		}
		return true;
	}

}
