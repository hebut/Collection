package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;



public class DomainTreeComposer extends Window implements AfterCompose {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	Center topicCen;
	Treeitem treeitem;
	List<Treeitem> itemList = new ArrayList<Treeitem>();
	Toolbarbutton save;
	List<WkTInfoDomain> list2;

	public void afterCompose() {	
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
//		Events.postEvent(Events.ON_CHANGE, this, itemList);
		
	}

	public void initWin(WkTInfoSort sort) {
		 
		    topicCen.getChildren().clear();
			List<WkTInfoDomain> wList = infodomainService.findByksidANDkipid(sort.getKsId(),0);
			DomainTree domainTree = new DomainTree(wList,infodomainService);
			Tree tree = new Tree();
			tree.setRows(15);
			tree.setPageSize(15);
			Treecols treecols = new Treecols();
			Treecol treecol1 = new Treecol("分类名称");
			treecol1.setStyle("text-align:center");
			treecol1.setWidth("60%");
			Treecol treecol2 = new Treecol("分类代码");
			treecol1.setStyle("text-align:center");
			treecol2.setWidth("20%");
			Treecol treecol3 = new Treecol("分类描述");
			treecol3.setWidth("20%");
			treecol1.setStyle("text-align:center");
			treecol3.setWidth("20%");
			treecols.appendChild(treecol1);
			treecols.appendChild(treecol2);
			treecols.appendChild(treecol3);
			tree.appendChild(treecols);
			tree.setCheckmark(true);
			tree.setMultiple(true);
			tree.setModel(domainTree);
			tree.setRows(wList.size()+1);
			tree.setTreeitemRenderer(new TreeitemRenderer(){
				public void render(Treeitem item, Object obj) throws Exception {
					
					final WkTInfoDomain w=(WkTInfoDomain)obj;
					item.setValue(w);
					treeitem = item;
					treeitem.setAttribute("domain", w);
					itemList.add(treeitem);
					Treecell treecell1 = new Treecell(w.getKiName());
					Treecell treecell2 = new Treecell(w.getKiCode());
					Treecell treecell3 = new Treecell(w.getKiIssue());
					Treerow treerow = new Treerow();
					treerow.appendChild(treecell1);
					treerow.appendChild(treecell2);
					treerow.appendChild(treecell3);
					item.appendChild(treerow);
					item.setOpen(true);
				}
			
			});	
			topicCen.appendChild(tree);
	}	
	
	public void onClick$save(){
		
		list2 = new ArrayList<WkTInfoDomain>();
		for(int i=0;i<itemList.size();i++){
			Treeitem item = itemList.get(i);
			if(item.isSelected()){
				WkTInfoDomain domain = (WkTInfoDomain)item.getValue();
				list2.add(domain);
			}
		}
		if(list2.size()==0){
			try {
				Messagebox.show("没有更新");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
            SortEdit sortEdit = (SortEdit)Executions.createComponents("/apps/infoExtra/content/topicmanage/sort_topic.zul", null, null);
			sortEdit.doHighlighted();
			sortEdit.initWindow(list2);
			sortEdit.addEventListener(Events.ON_CHANGE, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					refreshTree();
				}
				
			});
		}
		
	}
	
	public void refreshTree(){
		Events.postEvent(Events.ON_CHANGE,this, null);
	}
}
