package com.uniwin.webkey.infoExtra.infosortnew;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
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
	Center treeCen;
	Tree domaintree;
	Toolbarbutton add,padd,edit,deleteDomain,del;
	WkTInfoSort sort;
	public void afterCompose() {	
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);

		
	}

	public void initWin(final WkTInfoSort sort) {
		    this.sort = sort;
		    treeCen.getChildren().clear();
			List<WkTInfoDomain> wList = infodomainService.findByksidANDkipid(sort.getKsId(),0);
			DomainTree domainTree = new DomainTree(wList,infodomainService);
			domaintree.setModel(domainTree);
			domaintree.setTreeitemRenderer(new TreeitemRenderer(){
				public void render(Treeitem item, Object obj) throws Exception {
					
					final WkTInfoDomain w=(WkTInfoDomain)obj;
					item.setValue(w);
					Treecell treecell1 = new Treecell(w.getKiName());
					Treecell treecell2 = new Treecell(w.getKiCode());
					Treecell treecell3 = new Treecell(w.getKiIssue());
	    			Menupopup pop=new Menupopup();
					pop.setId(item+"");
					Menu menu=new Menu();
					menu.setLabel("新建分类");
					menu.setImage("/images/content/19.gif");
					
					Menupopup pop1=new Menupopup();
					Menuitem cmenu1=new Menuitem();
					cmenu1.setLabel("单条新建");
					cmenu1.setImage("/images/content/issue_ico.gif");
					Menuitem cmenu2=new Menuitem();
					cmenu2.setLabel("批量新建");
					cmenu2.setVisible(true);
					cmenu2.setImage("/images/content/pl.gif");
					Menuitem menu2=new Menuitem();
					menu2.setLabel("编辑分类");
					menu2.setImage("/images/content/05.gif");
					Menuitem menu3=new Menuitem();
					menu3.setLabel("删除分类");
					menu3.setImage("/images/content/del.gif");
					Menuseparator sep=new  Menuseparator();
				    final Menuitem menu4=new Menuitem();
					menu4.setLabel("排序");
					menu4.setImage("/images/content/order.jpg");
					
					pop1.appendChild(cmenu1);
					pop1.appendChild(cmenu2);
					menu.appendChild(pop1);
					pop.appendChild(menu);
					pop.appendChild(menu2);
					pop.appendChild(menu3);
					pop.appendChild(sep);
					pop.appendChild(menu4);
					treecell1.setContext(item+"");
					treecell1.appendChild(pop);
					Treerow treerow = new Treerow();
					treerow.appendChild(treecell1);
					treerow.appendChild(treecell2);
					treerow.appendChild(treecell3);
					item.appendChild(treerow);
					item.setOpen(false);
					cmenu1.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							if(domaintree.getSelectedItem()!=null){
								WkTInfoDomain w1 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
								NewDomain wn = (NewDomain)Executions.createComponents("/apps/infoExtra/content/infosortnew/newdomain.zul",null,null);
								wn.initWin(sort, w1.getKiId());
								wn.doHighlighted();
								wn.addEventListener(Events.ON_CHANGE, new EventListener(){

									public void onEvent(Event arg0)
											throws Exception {
										initWin(sort);	
									}
								}); 
							} 
						}
						
					});
					cmenu2.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							if(domaintree.getSelectedItem()!=null){
								WkTInfoDomain w2 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
								NewMDomain wm = (NewMDomain)Executions.createComponents("/apps/infoExtra/content/infosortnew/newMdomain.zul",null,null);
								wm.initWin(sort, w2.getKiId());
								wm.doHighlighted();
								wm.addEventListener(Events.ON_CHANGE, new EventListener(){

									public void onEvent(Event arg0)
											throws Exception {
										initWin(sort);	
									}
								});  
							}

						}
						
					});
					menu3.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							
							if(domaintree.getSelectedItem()!=null){
								WkTInfoDomain w3 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
								List list = infodomainService.findBySortPid(w3.getKiId());
								if(list.size()>0){
									 Messagebox.show("该分类存在子类不能删除");
								}
								else{
									try{
										infodomainService.delete(w3);
										 Messagebox.show("该分类删除成功");
									}
									catch(InterruptedException e){
										e.printStackTrace();
									}
									initWin(sort);
								}
							}

						}
						
					});
					
					menu4.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							if(domaintree.getSelectedItem()!=null){
								WkTInfoDomain w5 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
								DomainTypeSort de5 = (DomainTypeSort)Executions.createComponents("/apps/infoExtra/content/infosortnew/domainTypeSort.zul",null,null);
								de5.initWindow(sort,w5);
								de5.doHighlighted();
								de5.addEventListener(Events.ON_CHANGE, new EventListener(){
									public void onEvent(Event arg0)
											throws Exception {
										initWin(sort);	
									}
								});
							}                          
 
						}
						
					});
					
					menu2.addEventListener(Events.ON_CLICK, new EventListener(){

						public void onEvent(Event arg0) throws Exception {
							if(domaintree.getSelectedItem()!=null){
								WkTInfoDomain w4 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
								DomainEdit de = (DomainEdit)Executions.createComponents("/apps/infoExtra/content/infosortnew/domainedit.zul",null,null);
								de.initWin(w4);
								de.doHighlighted();
								de.addEventListener(Events.ON_CHANGE, new EventListener(){

									public void onEvent(Event arg0)
											throws Exception {
										initWin(sort);	
									}
								});
							}
 
						}
						
					});
					
				}
			
			});
			treeCen.appendChild(domaintree);
	}
	
	public void onClick$add(){
		NewDomainOne wno = (NewDomainOne)Executions.createComponents("/apps/infoExtra/content/infosortnew/newdomainOne.zul",null,null);		
		if(domaintree.getSelectedItem()!=null){
			WkTInfoDomain w7 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
			wno.initWin(sort,w7);
		}
		else{
			wno.initWin(sort,null);
		}
		wno.doHighlighted();
		wno.addEventListener(Events.ON_CHANGE, new EventListener(){
			public void onEvent(Event arg0)
					throws Exception {
				initWin(sort);	
			}
		}); 
	}	
	
	public void onClick$padd(){

		
		NewMDomainOne wmo = (NewMDomainOne)Executions.createComponents("/apps/infoExtra/content/infosortnew/newMdomainOne.zul",null,null);
		if(domaintree.getSelectedItem()!=null){
			WkTInfoDomain w6 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
			wmo.initWin(sort,w6);
		}
		else{
			wmo.initWin(sort,null);
		}
		wmo.doHighlighted();
		wmo.addEventListener(Events.ON_CHANGE, new EventListener(){

			public void onEvent(Event arg0)
					throws Exception {
				initWin(sort);	
			}
		});  
	}
	
	public void onClick$del() throws InterruptedException{
		if(domaintree.getSelectedItem()==null)
		{
			Messagebox.show("选择要删除的分类！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		else{
			Set itemset = domaintree.getSelectedItems();
			Iterator diter = itemset.iterator();
			List<WkTInfoDomain> deldomainlist= new ArrayList<WkTInfoDomain>();
			boolean flag = true;
			while(diter.hasNext()){
				 Treeitem item=(Treeitem)diter.next();
				 WkTInfoDomain infoDomain = (WkTInfoDomain)item.getValue();
				 System.out.println(infoDomain.getKiName());
				 List list = infodomainService.findBySortPid(infoDomain.getKiId());
				 if(list.size()>0){
					 flag = false;
					 Messagebox.show("该分类存在子类不能删除");
					 break;
				 }
				 else{
					 deldomainlist.add(infoDomain);
				 }
			}
			if(flag){
				if(Messagebox.show("确定删除分类吗 ?","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
					for(int k=0;k<deldomainlist.size();k++){
						WkTInfoDomain dw = deldomainlist.get(k);
						infodomainService.delete(dw);
					}
					initWin(sort);					
				}
			}
		}
	}
	
//	public void onClick$add(){
//		if(domaintree.getSelectedItem()!=null){
//			WkTInfoDomain w1 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
//			NewDomain wn = (NewDomain)Executions.createComponents("/apps/infoExtra/content/infosortnew/newdomain.zul",null,null);
//			wn.initWin(sort, w1.getKiId());
//			wn.doHighlighted();
//			wn.addEventListener(Events.ON_CHANGE, new EventListener(){
//
//				public void onEvent(Event arg0)
//						throws Exception {
//					initWin(sort);	
//				}
//			}); 
//		} 
//	}
//	
//	public void onClick$padd(){
//		if(domaintree.getSelectedItem()!=null){
//			WkTInfoDomain w2 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
//			NewMDomain wm = (NewMDomain)Executions.createComponents("/apps/infoExtra/content/infosortnew/newMdomain.zul",null,null);
//			wm.initWin(sort, w2.getKiId());
//			wm.doHighlighted();
//			wm.addEventListener(Events.ON_CHANGE, new EventListener(){
//
//				public void onEvent(Event arg0)
//						throws Exception {
//					initWin(sort);	
//				}
//			});  
//		}
//		
//	}
//	
//	public void onClick$edit(){
//		if(domaintree.getSelectedItem()!=null){
//			WkTInfoDomain w4 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
//			DomainEdit de = (DomainEdit)Executions.createComponents("/apps/infoExtra/content/infosortnew/domainedit.zul",null,null);
//			de.initWin(w4);
//			de.doHighlighted();
//			de.addEventListener(Events.ON_CHANGE, new EventListener(){
//
//				public void onEvent(Event arg0)
//						throws Exception {
//					initWin(sort);	
//				}
//			});
//		}
//	}
//	
//	public void onClick$deleteDomain(){
//		if(domaintree.getSelectedItem()!=null){
//			WkTInfoDomain w3 = (WkTInfoDomain)domaintree.getSelectedItem().getValue();
//			List list = infodomainService.findBySortPid(w3.getKiId());
//			if(list.size()>0){
//				 try {
//					Messagebox.show("该分类存在子类不能删除");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			else{
//				try{
//					infodomainService.delete(w3);
//					 Messagebox.show("该专题删除成功");
//				}
//				catch(InterruptedException e){
//					e.printStackTrace();
//				}
//				initWin(sort);
//			}
//		}
//	}
	
	public void refreshTree(){
		Events.postEvent(Events.ON_CHANGE,this, null);
	}
}

