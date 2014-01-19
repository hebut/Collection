package com.uniwin.webkey.infoExtra.infocustom;

import java.util.List;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class SortTreeComposer extends Window implements AfterCompose{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final AbstractComponent menuitem3 = null;
	Tree sortTree;
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	TreeData data;
	int i=0;
	
	Center sortCenter;

	DomainList domainList;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();
		
		sortTree.addEventListener(Events.ON_SELECT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				if(sortTree.getSelectedItem()!=null){
					WkTInfoSort infoSort=(WkTInfoSort)sortTree.getSelectedItem().getValue();
					if(domainList!=null)
						domainList.save();
					windowsLoad(infoSort);
				}
			}
			
		});
		sortTree.getPagingChild().setMold("os");
	}

	
	private void initTree() {
		
		List<WkTInfoSort> list=infosortService.findByPid(0);
		data=new TreeData(list,infosortService);
		
		sortTree.setModel(data);
		sortTree.setTreeitemRenderer(new TreeitemRenderer(){
			public void render(Treeitem item, Object obj) throws Exception {
				
				final WkTInfoSort w=(WkTInfoSort)obj;
				item.setValue(w);
				item.setLabel(w.getKsName());
				
				item.setOpen(false);
				i++;
				if(i==1){
					item.setSelected(true);
				}
				Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
				treecell.setContext(item+"");
			}
			
		});
		
		if(sortTree.getSelectedItem()!=null){
			WkTInfoSort sort=(WkTInfoSort) sortTree.getSelectedItem().getValue();
//			if(domainList!=null)
//			domainList.save();
			windowsLoad(sort);
		}
		
  }

	
	public void	windowsLoad(WkTInfoSort infoSort){
		if(infoSort!=null){
//			sortCenter.setTitle(org.zkoss.util.resource.Labels
//	                .getLabel("task.ui.operationTaskName"));
			sortCenter.setTitle("新闻定制");
			sortCenter.getChildren().clear();
			
			domainList= (DomainList)Executions.createComponents("/apps/infoExtra/content/infocustom/domainList.zul",sortCenter, null);
			domainList.initWindow(infoSort);
			
		}
	}
	
	
}
