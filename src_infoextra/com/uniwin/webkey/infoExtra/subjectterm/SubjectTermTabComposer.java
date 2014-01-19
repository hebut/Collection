package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
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

import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SubjectTermTabComposer extends Window implements AfterCompose {

	private static final long serialVersionUID = 9190535420896072694L;
	
	private SubjectTermSortService subjectTermSortService;
	private SubjectTermService subjectTermService;
	private List<WkTSubjectTermSort> list;
	private Tabs tabs;
	private Textbox sort;
	private Tabpanels panel;
	private List<WkTSubjectTerm> selectedSubjectTerms = new ArrayList<WkTSubjectTerm>();
	private List<Tree> treeList = new ArrayList<Tree>();
	private Treeitem treeitem;
	private List<Treeitem> itemList = new ArrayList<Treeitem>();

	public void afterCompose() {	
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin() {
		list = subjectTermSortService.findAll();
		WkTSubjectTermSort sort1,sort2;
		for(int i=0;i<list.size();i++){
			sort1=(WkTSubjectTermSort)list.get(i);
			List<WkTSubjectTerm> dList= subjectTermService.findBySortId(sort1.getKsId(), null);
			if(dList==null || dList.size()==0){
				list.remove(i);
			}
		}
		
		for(int j=0;j<list.size();j++){
			
			sort2=(WkTSubjectTermSort)list.get(j);
			Tab tab=new Tab();
			tab.setLabel(sort2.getKsName());
			tab.setTooltiptext(sort2.getKsName());
			tabs.appendChild(tab);
			
			Tabpanel tabpanel=new Tabpanel();
			List<WkTSubjectTerm> wList = subjectTermService.findByksidANDkstPid(sort2.getKsId(),0);
			SubjectTermTreeModel subjectTermTreeModel = new SubjectTermTreeModel(wList, subjectTermService);
			
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
			tree.setModel(subjectTermTreeModel);
			tree.setRows(wList.size()+1);
			tree.setTreeitemRenderer(new TreeitemRenderer(){
				public void render(Treeitem item, Object obj) throws Exception {
					final WkTSubjectTerm w=(WkTSubjectTerm)obj;
					item.setValue(w);
					item.setOpen(false);
					treeitem = new Treeitem();
					treeitem = item;
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
			treeList.add(tree);
		}
	}

	public void initWin(Textbox sort) {
		this.sort = sort;
		initWin();
	}

	public void onClick$save() {
		for (Tree sorttree : treeList) {
			for (Object o : sorttree.getSelectedItems()) {
				WkTSubjectTerm st = (WkTSubjectTerm) ((Treeitem) o).getValue();
				selectedSubjectTerms.add(st);
			}
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
	
	public void onClick$back()
	{
		Events.postEvent(Events.ON_CHANGING, this, null);
		   this.detach();	
	}

	public List<WkTSubjectTerm> getSelectedSubjectTerms() {
		return selectedSubjectTerms;
	}

}
