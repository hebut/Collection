package com.uniwin.webkey.infoExtra.infosort;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;


/**
 * 
 */
public class DomainTree extends AbstractTreeModel {

    private InfoDomainService infodomainService;
    
	public DomainTree(Object root,InfoDomainService infodomainService) {
		super(root);
		this.infodomainService = infodomainService;
		
	}
	private static final long serialVersionUID = 1L;

	public Object getChild(Object parent, int index) {
		
		if(parent instanceof WkTInfoDomain){
			WkTInfoDomain w = (WkTInfoDomain)parent;
			List clist = infodomainService.findBySortPid(w.getKiId());
			return clist.get(index);
		}
		else{
			if(parent instanceof List){
				List l = (List)parent;
				return l.get(index);
			}
		}
		return null;
	}

	public int getChildCount(Object parent) {
		
		if(parent instanceof WkTInfoDomain){
			WkTInfoDomain w = (WkTInfoDomain)parent;
			List clist = infodomainService.findBySortPid(w.getKiId());
			return clist.size();
		}
		else{
			if(parent instanceof List){
				List l = (List)parent;
				return l.size();
			}
		}
		return 1;
	}

	public boolean isLeaf(Object node) {
		
		if(node instanceof WkTInfoDomain){
			WkTInfoDomain w = (WkTInfoDomain)node;
			List clist = infodomainService.findBySortPid(w.getKiId());
			return clist.size()>0?false:true;
		}
		else{
			if(node instanceof List){
				List l = (List)node;
				return l.size()>0?false:true;
			}
		}
		return true;
	}

}
