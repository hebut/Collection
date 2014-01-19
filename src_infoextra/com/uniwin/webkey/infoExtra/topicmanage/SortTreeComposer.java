package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.itf.TopicIdAndDomainIdService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;
import com.uniwin.webkey.infoExtra.model.WkTTopicIdAndDomainId;

public class SortTreeComposer extends Window implements AfterCompose{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Tree sortTree,topicTree;
	InfoSortService infosortService;
	TopicIdAndDomainIdService topicIdAndDomainid;
	InfoDomainService infodomainService;     
	PersonTopicService persontopicService;
	TreeData data;
	TopicTreeData topicData;
	TopicInfoList topicInfoList;
	
	Center sortCenter;
	DomainTreeComposer domainList;
	Tabpanel tab1,tab2;
	Users user;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
		List<WKTPersonTopic> lp = persontopicService.findByPidAndUserId((long)0, user.getUserId());
		if(lp.size()==0){
			WKTPersonTopic perT = new WKTPersonTopic();
			perT.setKptName("个人专题");
			perT.setKptPid((long)0);
			perT.setKuId(user.getUserId());
			persontopicService.save(perT);
			
		}
		initTree(null);
		initPTree();
		topicTree.addEventListener(Events.ON_SELECT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				if(topicTree.getSelectedItem()!=null){
					Treeitem t = topicTree.getSelectedItem();
					if(t.getValue() instanceof WKTPersonTopic){
						WKTPersonTopic personTopic=(WKTPersonTopic)t.getValue();
						windowsLoadTopic(personTopic);
					}

				}
			}
			
		});
		
		sortTree.addEventListener(Events.ON_SELECT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				if(sortTree.getSelectedItem()!=null){
					Treeitem tt = sortTree.getSelectedItem();
					if(tt.getValue() instanceof WkTInfoSort){
						WkTInfoSort infoSort=(WkTInfoSort)tt.getValue();
						windowsLoadSort(infoSort);
					}
				}
			}
			
		});
		sortTree.getPagingChild().setMold("os");
	}

	private void initPTree(){
		final List<WKTPersonTopic> list = persontopicService.findByPid((long)0,user.getUserId());
		topicData = new TopicTreeData(list,persontopicService,user.getUserId());
		topicTree.setModel(topicData);
		loadTree();
//		topicTree.setTreeitemRenderer(new TreeitemRenderer(){
//
//			public void render(Treeitem item, Object obj) throws Exception {
//				
//				WKTPersonTopic w = (WKTPersonTopic)obj;
//				item.setValue(w);
//				item.setLabel(w.getKptName());
//				item.setOpen(true);
//				if(list!=null && list.size()>=0){
//					if(w.getKptPid().toString().equals("0")){
//						item.setSelected(true);
//					}
//				}
//				Menupopup pop = new Menupopup();
//				pop.setId(item+"");
//				
//				Menuitem menuitem0 = new Menuitem();
//				menuitem0.setLabel("新建专题");
//				menuitem0.setImage("/images/content/issue_ico.gif");
//				Menuitem menuitem1 = new Menuitem();
//				menuitem1.setLabel("编辑专题");
//				menuitem1.setImage("/images/content/05.gif");
//				Menuitem menuitem2 = new Menuitem();
//				menuitem2.setLabel("删除专题");
//				menuitem2.setImage("/images/content/del.gif");
//				
//				pop.appendChild(menuitem0);
//				pop.appendChild(menuitem1);
//				pop.appendChild(menuitem2);
//				
//				Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
//				treecell.setContext(item+"");
//				treecell.appendChild(pop);
//				
//				menuitem0.addEventListener(Events.ON_CLICK, new EventListener(){
//
//					public void onEvent(Event arg0) throws Exception {
//						if(topicTree.getSelectedItem()!=null){
//							WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue(); 
//								NewTopic newTopic = (NewTopic)Executions.createComponents("/apps/infoExtra/content/topicmanage/newTopic.zul", null, null);
//							    newTopic.initWindow(personTopic);
//							    newTopic.doHighlighted();
//							    newTopic.addEventListener(Events.ON_CHANGE, new EventListener(){
//									public void onEvent(Event arg0) throws Exception {
//										   initPTree();
//									}
//									
//								});
//							 
//							}
//							
//						}	
//				});
//				
//				menuitem1.addEventListener(Events.ON_CLICK, new EventListener(){
//
//					public void onEvent(Event arg0) throws Exception {
//						if(topicTree.getSelectedItem()!=null){
//							WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue();  
//							EditTopic editTopic = (EditTopic)Executions.createComponents("/apps/infoExtra/content/topicmanage/editTopic.zul", null, null);
//							editTopic.initWindow(personTopic);
//						    editTopic.doHighlighted();
//							editTopic.addEventListener(Events.ON_CHANGE, new EventListener(){
//									public void onEvent(Event arg0) throws Exception {
//										   initPTree();
//									}
//									
//							});
//							
//						}
//						
//					}
//					
//				});
//				
//				menuitem2.addEventListener(Events.ON_CLICK, new EventListener(){
//
//					public void onEvent(Event arg0) throws Exception {
//						if(topicTree.getSelectedItem()!=null){
//							WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue();  
//                            if(personTopic.getKptPid()==0){
//                            	try{
//                            		Messagebox.show("根专题不能删除");
//                            		return;
//                            	}
//                            	catch(InterruptedException e){
//                            		e.printStackTrace();
//                            	}
//                            	
//                            }
//                            else{
//                            	List<WKTPersonTopic> list = persontopicService.getChildType(personTopic.getKptId());
//                            	if(list.size()==0){
//                            		persontopicService.delete(personTopic);
//                                    Messagebox.show("该专题删除成功");
//                                    initPTree();
//                            	}
//                            	else{
//                                	try{
//                                		Messagebox.show("该专题存在子专题不能删除");
//                                		return;
//                                	}
//                                	catch(InterruptedException e){
//                                		e.printStackTrace();
//                                	}
//
//
//                            	}
//                            	
//                            }
//						}	
//					}	
//				});
//				
//				
//			}
//			
//		});
		if(topicTree.getSelectedItem()!=null){
			WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue();
			windowsLoadTopic(personTopic);
		}
	}
	
	private void initTree(WkTInfoSort infoSort) {
		
		final WkTInfoSort f = infoSort;
		List<WkTInfoSort> list=infosortService.findByPid(0);
		data=new TreeData(list,infosortService);
		sortTree.setModel(data);
		sortTree.setTreeitemRenderer(new TreeitemRenderer(){
			public void render(Treeitem item, Object obj) throws Exception {
				
				WkTInfoSort w=(WkTInfoSort)obj;
				item.setValue(w);
				item.setLabel(w.getKsName());
				if(f!=null){
					if(f.getKsId().toString().equals(w.getKsId().toString())){
						item.setSelected(true);
					}
					
				}
				item.setOpen(true);
				Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
				treecell.setContext(item+"");
			}
			
		});
		
		if(sortTree.getSelectedItem()!=null){
			WkTInfoSort sort=(WkTInfoSort)sortTree.getSelectedItem().getValue();
			windowsLoadSort(sort);
		}
		
  }

	
	public void	windowsLoadSort(final WkTInfoSort infoSort){
		if(infoSort!=null){
			sortCenter.setTitle("专题定制");
			sortCenter.getChildren().clear();
			domainList = (DomainTreeComposer)Executions.createComponents("/apps/infoExtra/content/topicmanage/domainList.zul",sortCenter, null);
			domainList.initWin(infoSort);
			domainList.addEventListener(Events.ON_CHANGE, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					loadTree();	
					initTree(infoSort);
				}
				
			});
		}
	}
	
	public void	windowsLoadTopic(WKTPersonTopic personTopic){
		if(personTopic!=null){
			sortCenter.setTitle("个人专题");
			sortCenter.getChildren().clear();
			topicInfoList = (TopicInfoList)Executions.createComponents("/apps/infoExtra/content/topicmanage/personTopicList.zul", sortCenter, null);
			topicInfoList.initWindows(personTopic);
		}
	}
	
	public void loadTree(){
		
				final List<WKTPersonTopic> list = persontopicService.findByPid((long)0,user.getUserId());
				topicData = new TopicTreeData(list,persontopicService,user.getUserId());
				topicTree.setModel(topicData);
				topicTree.setTreeitemRenderer(new TreeitemRenderer(){

					public void render(Treeitem item, Object obj) throws Exception {
						
						WKTPersonTopic w = (WKTPersonTopic)obj;
						item.setValue(w);
						item.setLabel(w.getKptName());
						item.setOpen(true);
						if(list!=null && list.size()>=0){
							if(w.getKptPid().toString().equals("0")){
								item.setSelected(true);
							}
						}
						Menupopup pop = new Menupopup();
						pop.setId(item+"");
						
						Menuitem menuitem0 = new Menuitem();
						menuitem0.setLabel("新建专题");
						menuitem0.setImage("/images/content/issue_ico.gif");
						Menuitem menuitem1 = new Menuitem();
						menuitem1.setLabel("编辑专题");
						menuitem1.setImage("/images/content/05.gif");
						Menuitem menuitem2 = new Menuitem();
						menuitem2.setLabel("删除专题");
						menuitem2.setImage("/images/content/del.gif");
						
						pop.appendChild(menuitem0);
						pop.appendChild(menuitem1);
						pop.appendChild(menuitem2);
						
						Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
						treecell.setContext(item+"");
						treecell.appendChild(pop);
						
						menuitem0.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								if(topicTree.getSelectedItem()!=null){
									WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue(); 
										NewTopic newTopic = (NewTopic)Executions.createComponents("/apps/infoExtra/content/topicmanage/newTopic.zul", null, null);
									    newTopic.initWindow(personTopic);
									    newTopic.doHighlighted();
									    newTopic.addEventListener(Events.ON_CHANGE, new EventListener(){
											public void onEvent(Event arg0) throws Exception {
												   initPTree();
											}
											
										});
									 
									}
									
								}	
						});
						
						menuitem1.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								if(topicTree.getSelectedItem()!=null){
									WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue();  
									EditTopic editTopic = (EditTopic)Executions.createComponents("/apps/infoExtra/content/topicmanage/editTopic.zul", null, null);
									editTopic.initWindow(personTopic);
								    editTopic.doHighlighted();
									editTopic.addEventListener(Events.ON_CHANGE, new EventListener(){
											public void onEvent(Event arg0) throws Exception {
												   initPTree();
											}
											
									});
									
								}
								
							}
							
						});
						
						menuitem2.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								if(topicTree.getSelectedItem()!=null){
									WKTPersonTopic personTopic=(WKTPersonTopic)topicTree.getSelectedItem().getValue();  
		                            if(personTopic.getKptPid()==0){
		                            	try{
		                            		Messagebox.show("根专题不能删除");
		                            		return;
		                            	}
		                            	catch(InterruptedException e){
		                            		e.printStackTrace();
		                            	}
		                            	
		                            }
		                            else{
		                            	List<WKTPersonTopic> list = persontopicService.getChildType(personTopic.getKptId(),user.getUserId());
		                            	if(list.size()==0){
		                            		persontopicService.delete(personTopic);
		                            		List<WkTTopicIdAndDomainId> lt = topicIdAndDomainid.findByTopic(personTopic.getKptId());
		                            		if(lt.size()>0){
		                            			for(int i=0;i<lt.size();i++){
		                            				topicIdAndDomainid.delete((WkTTopicIdAndDomainId)lt.get(i));
		                            			}	
		                            		}
		                                    Messagebox.show("该专题删除成功");
		                                    initPTree();
		                            	}
		                            	else{
		                                	try{
		                                		Messagebox.show("该专题存在子专题不能删除");
		                                		return;
		                                	}
		                                	catch(InterruptedException e){
		                                		e.printStackTrace();
		                                	}


		                            	}
		                            	
		                            }
								}	
							}	
						});
						
						
					}
					
				});
			}		
	
}
