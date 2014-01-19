package com.uniwin.webkey.infoExtra.infosort;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;
import com.uniwin.webkey.infoExtra.task.ImportTypeWindow;

public class SortTreeComposer extends Window implements AfterCompose{

	
	Tree sortTree;
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	TreeData data;
	int i=0;
	WkTInfoSort infoSort1;
	Center sortCenter;
	List<Radio> radioList=new ArrayList<Radio>();
	DomainList domainList;
	DomainTreeComposer domainTreeCom;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();
		
		sortTree.addEventListener(Events.ON_SELECT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				if(sortTree.getSelectedItem()!=null){
					WkTInfoSort infoSort=(WkTInfoSort)sortTree.getSelectedItem().getValue();
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
				
				item.setOpen(true);
				i++;
				if(i==1){
					item.setSelected(true);
				}
				Menupopup menupopup=new Menupopup();
				menupopup.setId(item+"");

				Menuitem menuitem=new Menuitem();
				menuitem.setLabel("新建分类");
				menuitem.setImage("/images/content/issue_ico.gif");
				
				Menuitem menuitem2=new Menuitem();
				menuitem2.setLabel("编辑分类");
				
				Menuitem menuitem3=new Menuitem();
				menuitem3.setLabel("删除分类");
				menuitem3.setImage("/images/content/del.gif");
				

				menupopup.appendChild(menuitem);
				menupopup.appendChild(menuitem2);
				menupopup.appendChild(menuitem3);
				
				Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
				treecell.setContext(item+"");
				treecell.appendChild(menupopup);
				
				menuitem.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						NewSort newSort=(NewSort)Executions.createComponents("/apps/infoExtra/content/infosort/newsort.zul", null,null);
						newSort.initWindow(w);
						newSort.doHighlighted();
						
						
						newSort.addEventListener(Events.ON_CHANGE, new EventListener(){
							public void onEvent(Event arg0) throws Exception {
								initTree();
							}
						});
					}
					
				});
				
				menuitem2.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						SortEdit sort=(SortEdit)Executions.createComponents("/apps/infoExtra/content/infosort/sortedit.zul", null,null);
						sort.initWin(w);
						sort.doHighlighted();
						sort.addEventListener(Events.ON_CHANGE, new EventListener(){
							public void onEvent(Event arg0) throws Exception {
								initTree();
							}
							
						});
					}
					
				});
				
				menuitem3.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						if(w.getKsPid()==0){
							Messagebox.show("不能删除根文件夹！");
						}else{
							
						}
					}
					
				});
				
			}
			
		});
		
		if(sortTree.getSelectedItem()!=null){
			WkTInfoSort sort=(WkTInfoSort) sortTree.getSelectedItem().getValue();
			windowsLoad(sort);
		}
		
  }

	public void	windowsLoad(WkTInfoSort infoSort){
		if(infoSort!=null){
			this.infoSort1=infoSort;
			//sortCenter.setTitle(org.zkoss.util.resource.Labels.getLabel("task.ui.operationTaskName"));
			sortCenter.getChildren().clear();
			
			
			domainList= (DomainList)Executions.createComponents("/apps/infoExtra/content/infosort/domainList.zul",sortCenter, null);
			domainList.initWindow(infoSort);
			domainList.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					windowsLoad(infoSort1);
				}
				
			});
		}
	}
	
		
}
