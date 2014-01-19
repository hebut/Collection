package com.uniwin.webkey.infoExtra.infosort;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
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
	List<WkTInfoSort> list;
	Tabs tabs;
	Tabpanels panel;
	Textbox sort;
	Treeitem treeitem;
	List<Treeitem> itemList = new ArrayList<Treeitem>();
	List<WkTInfoDomain> list2;

	public void afterCompose() {	
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		
	}

	private void initWin() {
		
		list = infosortService.findAll();
		WkTInfoSort sort1,sort2;
		for(int i=0;i<list.size();i++){
			sort1=(WkTInfoSort)list.get(i);
			List<WkTInfoDomain> dList= infodomainService.findBySortId(sort1.getKsId());
			if(dList==null || dList.size()==0){
				list.remove(i);
			}
		}
		
		for(int j=0;j<list.size();j++){
			
			sort2=(WkTInfoSort)list.get(j);
			Tab tab=new Tab();
			tab.setLabel(sort2.getKsName());
			tab.setTooltiptext(sort2.getKsName());
			tabs.appendChild(tab);
			
			Tabpanel tabpanel=new Tabpanel();
			List<WkTInfoDomain> wList = infodomainService.findByksidANDkipid(sort2.getKsId(),0);
			DomainTree domainTree = new DomainTree(wList,infodomainService);
			
			Tree tree = new Tree();
			tree.setRows(13);
			tree.setHeight("300px");
			//tree.setMold("paging");
			Treecols treecols = new Treecols();
			Treecol treecol1 = new Treecol("分类名称");
			treecol1.setWidth("80%");
			Treecol treecol2 = new Treecol("分类代码");
			treecol2.setWidth("20%");
			treecols.appendChild(treecol1);
			treecols.appendChild(treecol2);
			tree.appendChild(treecols);
			tree.setCheckmark(true);
			tree.setMultiple(true);
			tree.setModel(domainTree);
			tree.setRows(wList.size()+1);
			tree.setTreeitemRenderer(new TreeitemRenderer(){
				public void render(Treeitem item, Object obj) throws Exception {
					
					final WkTInfoDomain w=(WkTInfoDomain)obj;
					item.setValue(w);
					item.setOpen(false);
					treeitem = new Treeitem();
					treeitem = item;
					treeitem.setAttribute("domain", w);
					itemList.add(treeitem);
					Treecell treecell1 = new Treecell(w.getKiName());
					Treecell treecell2 = new Treecell(w.getKiCode());
					Treerow treerow = new Treerow();
					treerow.appendChild(treecell1);
					treerow.appendChild(treecell2);
					item.appendChild(treerow);
				if(sort.getValue().trim().contains(w.getKiName().trim()+";"))
					item.setSelected(true);
					item.setOpen(false);
				}
			
			});
			tabpanel.appendChild(tree);
			panel.appendChild(tabpanel);		
		}
	}


	public void onClick$save(){
	    
		StringBuffer sb = new StringBuffer();
		list2=new ArrayList<WkTInfoDomain>();
		for(int i=0;i<itemList.size();i++)
		{
			Treeitem c=itemList.get(i);
			if(c.isSelected())
			{
				String	selectValue=c.getLabel();
				sb.append(selectValue+";");
				WkTInfoDomain domain=(WkTInfoDomain) c.getAttribute("domain");
				list2.add(domain);
			}
		}
		if(sb!=null && !sb.toString().equals("")){
			sort.setValue(sb.toString());
			Events.postEvent(Events.ON_CHANGE, this, list2);
		}
		this.detach();
	}
	
	public void initWin(Textbox sort) {
		this.sort=sort;
		initWin();
	}
	public void onClick$back()
	{
		Events.postEvent(Events.ON_CHANGING, this, null);
		   this.detach();	
	}

}
