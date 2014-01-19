package com.uniwin.webkey.infoExtra.task;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.model.Auth;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.zkoss.org.messageboxshow.MessageBox;


public class TasktypeTreeComposer extends Window implements AfterCompose {


	
	private static final long serialVersionUID = 5818071448713236671L;
	Tree taskTree;
	TasktypeTreeModel ctm;
	WkTChanel t,tp,type;
    
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	private IPermissionManager permissionManager = (IPermissionManager) SpringUtil.getBean("permissionManager");
	private IAuthManager  	iAuthManager =  (IAuthManager) SpringUtil.getBean("authManager"); 
	private OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	
    TaskEditWindow editWindow ;

	Users user;
	List userDeptList,clist;
	Listbox taskList;
	ListModelList modelList;
	
	Center taskCen;
	Tabs tabs;Tabbox box;
	
	public static final Short check = 1, uncheck = 0;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		loadTree();
        
		taskTree.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event event) throws Exception {
				 Treeitem it=taskTree.getSelectedItem();
				if(it.getValue() instanceof WkTChanel)
				{
					WkTChanel t=(WkTChanel)it.getValue();
					windowsLoad(t);
				}
			}
		});
		taskTree.getPagingChild().setMold("os");
		
	}
	
		//加载分类树
		private void loadTree() {
			clist=taskService.getChildType(Long.parseLong(0+""));
			ctm=new TasktypeTreeModel(clist,taskService);
			taskTree.setModel(ctm);
			List tList=taskService.findAllTaskOrder();
			
//			if(tList!=null && tList.size()>0){
//				WkTExtractask e=(WkTExtractask)tList.get(0);
//				type=taskService.findByFolderID(e.getKcId());
//			}
			
			taskTree.setTreeitemRenderer(new TaskItemRenderer()
		     {
		    		public void render(final Treeitem item, Object data) throws Exception {
		    			final WkTChanel task=(WkTChanel)data;
		    			item.setValue(task);
		    			item.setLabel(task.getKcName());
		    			item.setOpen(true);
		    			
		    			if(clist != null && clist.size()>= 0 )
						{
							if(task.getKcPid().toString().equals("0"))
							{
								item.setSelected(true);
								type=taskService.findByFolderID(task.getKcId());
							}
						}
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
						cmenu2.setVisible(false);
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
						Menuitem menu5=new Menuitem("添加采集任务");
						menu5.setImage("/images/content/addInact1.gif");
						
						pop1.appendChild(cmenu1);
						pop1.appendChild(cmenu2);
						menu.appendChild(pop1);
						pop.appendChild(menu);
						pop.appendChild(menu2);
						pop.appendChild(menu3);
						pop.appendChild(sep);
						pop.appendChild(menu4);
						pop.appendChild(menu5);
						Treecell treecell=(Treecell) item.getTreerow().getFirstChild();
						treecell.setContext(item+"");
						treecell.appendChild(pop);
					
						//抽取配置
						menu5.addEventListener(Events.ON_CLICK, new EventListener(){
							public void onEvent(Event arg0) throws Exception {
								createNewTask(item);
							}
						});
						
						//单条新建分类
						cmenu1.addEventListener(Events.ON_CLICK, new EventListener(){
							public void onEvent(Event event) throws Exception {
								NewSortWindow w=(NewSortWindow)Executions.createComponents("/apps/infoExtra/content/task/newsort.zul", null,null);
								w.doHighlighted();
								w.initWindow(task);
								w.addEventListener(Events.ON_CHANGE, new EventListener() {
									public void onEvent(Event arg0) throws Exception {
										loadTree();
									}
								});
							}
							
						});
						
						//批量新建分类
						cmenu2.addEventListener(Events.ON_CLICK, new EventListener(){
							public void onEvent(Event event) throws Exception {
								if(taskTree.getSelectedCount()==0)
								{
									Messagebox.show("请选择分类");
								}
								else
								{
									if(task.getKcPid().toString().trim().equals("0"))
									{
										Messagebox.show("请选择分类节点");
									}
									else
									{
									ImportTypeWindow w=(ImportTypeWindow) Executions.createComponents("/apps/infoExtra/content/task/imports.zul", null,null);
								    w.doHighlighted();
								    w.initWindow(task);
									w.addEventListener(Events.ON_CHANGE, new EventListener(){
										public void onEvent(Event arg0) throws Exception {
											loadTree();	
										} 
									 });
									}
								}
								
							}
							
						});
						
						//排序
						menu4.addEventListener(Events.ON_CLICK, new EventListener(){
							public void onEvent(Event event) throws Exception {
								     TasktypeSortWindow w=(TasktypeSortWindow)Executions.createComponents("/apps/infoExtra/content/task/typeSort.zul", null, null);
									w.doHighlighted();
									w.initWindow(task);
									w.addEventListener(Events.ON_CHANGE, new EventListener(){
										public void onEvent(Event arg0) throws Exception {
											loadTree();
										}			
									});
							}		
						});
						//编辑分类
						menu2.addEventListener(Events.ON_CLICK, new EventListener(){
							public void onEvent(Event event) throws Exception {
								SortEditWindow s=(SortEditWindow)Executions.createComponents("/apps/infoExtra/content/task/sortedit.zul", null,null);
								s.doHighlighted();
								s.initWindow(task);
								s.addEventListener(Events.ON_CHANGE, new EventListener() {
									public void onEvent(Event arg0) throws Exception {
										loadTree();
									}

								});
							}		
						});
						//删除分类
						menu3.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event event) throws Exception {
								if(task.getKcPid().toString().trim().equals("0")){
									Messagebox.show("根文件夹不允许删除");
								}else{
									
									List tlist=taskService.getChildType(task.getKcId());
									if(tlist.size()!=0){
										Messagebox.show("存在子类型不能删除");
									}else{
									
									/**删除流程：
									 * 1. 删除chanel下的权限授权，通过permission和auth表
									 * 2. 删除chanel下的extract列表，及其extract权限授予
									 * 3. 删除extract下的初始信息表及其正式信息表和发布表
									 * 4. 删除chanel.
									 */
									
										if (Messagebox.show("确实要删除该分类、分类下的任务及其全部所属信息 ?", "请确认", Messagebox.OK
												| Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
											
											//删除分类权限
											List alist=taskService.getTypeAuth(Integer.parseInt(task.getKcId()+""));
											if(alist!=null && alist.size()!=0){
												Permission permission;
												for(int i=0;i<alist.size();i++){
													
													permission=(Permission)alist.get(i);
													List authPerList=iAuthManager.getAuthByKpid(permission.getKpid());
													if(authPerList!=null && authPerList.size()>0){
														
														for(int p=0;p<authPerList.size();p++){
															Auth auth=(Auth)authPerList.get(p);
															iAuthManager.removeByKaid(auth.getKaId());
														}
													}
													permissionManager.delPermissionByPermission(permission);
												}
											}
											
											//删除分类下的任务
											List list=taskService.getTaskByKtaid(task.getKcId());
											if(list!=null)
											{
												for(int i=0;i<list.size();i++)
												{
													WkTExtractask e=(WkTExtractask) list.get(i);
													//删除任务下的信息
											    	deleteTaskAndInfo(e);
												}
											}
											
											taskService.delete(task);
											Messagebox.show("删除成功!");
											loadTree();
											
										}//删除框结束
									
								}					
							}//if结束
							}
						});
						
		    		}
		     });
			if(taskTree.getSelectedItem()!=null){
				windowsLoad(type);
			}
			
	}
		

	/**
	 * 	删除extract，初始信息表，信息表和发布表
	 * @param extractask
	 */
	private void deleteTaskAndInfo(WkTExtractask extractask){
		
		List orinfoList=orinewsService.getNewsOfOrinfo(extractask.getKeId()); 
		WkTOrinfo wkTOrinfo;
	try {
		
		if(orinfoList!=null){
			
		for(int i=0;i<orinfoList.size();i++){
			
			wkTOrinfo=(WkTOrinfo)orinfoList.get(i);
			List OrinfocntList=orinewsService.getOriInfocnt(wkTOrinfo.getKoiId());
			if(OrinfocntList!=null){
				WkTOrinfocnt wkTOrinfocnt;
				for(int j=0;j<OrinfocntList.size();j++){
					wkTOrinfocnt=(WkTOrinfocnt)OrinfocntList.get(j);
					orinewsService.delete(wkTOrinfocnt);
				}
			}
			
			orinewsService.delete(wkTOrinfo);
		}
	}
		List  infoList=newsService.findByTaskId(extractask.getKeId());
		if(infoList!=null){
			WkTInfo wkTInfo;
			for(int a=0;a<infoList.size();a++){
				wkTInfo=(WkTInfo)infoList.get(a);
				Long id=wkTInfo.getKiId();
				List infocntList=newsService.getInfocnt(id);
				List distubteList=newsService.findDistubteById(id);
				
				if(infocntList!=null){
					WkTInfocnt wkTInfocnt;
					for(int b=0;b<infocntList.size();b++){
						wkTInfocnt=(WkTInfocnt)infocntList.get(b);
						newsService.delete(wkTInfocnt);
					}
					
				}
				if(distubteList!=null){
					WkTDistribute wkTDistribute;
					for(int c=0;c<distubteList.size();c++){
						wkTDistribute=(WkTDistribute)distubteList.get(c);
						newsService.delete(wkTDistribute);
					}
				}
				newsService.delete(wkTInfo);
			}
			
		}
		
		taskService.delete(extractask);
		
	} catch (Exception e) {
		// TODO: handle exception
	}
		
}
		
		
		
	public void	windowsLoad(WkTChanel tasktype){
		if(tasktype!=null){
			taskCen.setTitle("当前操作信息源名称 : "+tasktype.getKcName());
			taskCen.getChildren().clear();
			editWindow= (TaskEditWindow)Executions.createComponents("/apps/infoExtra/content/task/tasklist.zul",taskCen, null);
			editWindow.initWindow(tasktype);
		}
	}
		
	/* 抽取采集任务*/
	private void createNewTask(Treeitem item){
			
			if(taskTree.getSelectedItem()==null){
				MessageBox.showInfo("请选择左侧树文件夹！");
			}else{
			final WkTChanel t=(WkTChanel) taskTree.getSelectedItem().getValue();
			Configure taskConfigure=(Configure)Executions.createComponents("/apps/infoExtra/content/task/configure.zul",null,null);
			taskConfigure.initWindow(t);
			taskConfigure.doHighlighted();
			taskConfigure.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					windowsLoad(t);
				}
	    	});
			}
		}
		
}
