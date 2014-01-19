package com.uniwin.webkey.infoExtra.tasktemp;

/**
 * 编辑信息界面
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;

import com.uniwin.webkey.infoExtra.core.LinkCollection;
import com.uniwin.webkey.infoExtra.core.MyCallable;
import com.uniwin.webkey.infoExtra.core.MyCallableSave;
import com.uniwin.webkey.infoExtra.core.ServerPush;
import com.uniwin.webkey.infoExtra.itf.GuideService;
import com.uniwin.webkey.infoExtra.itf.InfoService;
import com.uniwin.webkey.infoExtra.itf.LinkService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.PickService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;





public class TaskEditWindow extends Window implements AfterCompose {

	
	private WkTChanel tasktype;
	public WkTChanel getTasktype() {
		return tasktype;
	}
	public void setTasktype(WkTChanel tasktype) {
		this.tasktype = tasktype;
	}

	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	private GuideService guideService=(GuideService)SpringUtil.getBean("guideService");
	private PickService pickService=(PickService)SpringUtil.getBean("pickService");
	private LinkService linkService=(LinkService)SpringUtil.getBean("linkService");
	private InfoService infoService=(InfoService)SpringUtil.getBean("infoService");
	
	private OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	
	Listbox taskList;
	ListModelList modelList;
	Tabs tabs;Tabbox box;
	Toolbarbutton addtask,signSet;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this,this);
		addtask.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {
				Configure taskConfigure=(Configure)Executions.createComponents("/apps/infoExtra/content/tasktemp/configure.zul",null,null);
				taskConfigure.initWindow(tasktype);
				taskConfigure.doHighlighted();
				taskConfigure.addEventListener(Events.ON_CHANGE, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						loadTaskList();
					}
		    	});			
				}		
		});
	}

	public void initWindow(WkTChanel t) {
		this.tasktype=t;
		loadTaskList();
	}
	
	public void loadTaskList(){
	//	List<WkTExtractask> tList=taskService.findByFolderId(tasktype.getKcId());
		List<WkTExtractask> tList = new ArrayList<WkTExtractask>();
		if(tasktype.getKcId() == 1){
			tList = taskService.findAllTask();
		}else{
			List clist=taskService.getChildType(tasktype.getKcId());
			List cdlist=new ArrayList();
			int m=1;
			cdlist.add(0, tasktype.getKcId());
			for(int i=0;i<clist.size();i++)
			{
				WkTChanel ch=(WkTChanel) clist.get(i);
				cdlist.add(m, ch.getKcId());
				m++;
				List c1list=taskService.getChildType(ch.getKcId());
				for(int j=0;j<c1list.size();j++)
				{
					WkTChanel ch1=(WkTChanel) c1list.get(i);
					cdlist.add(m, ch1.getKcId());
					m++;
					List c2list=taskService.getChildType(ch1.getKcId());
					for(int k=0;k<c2list.size();k++)
					{
						WkTChanel ch2=(WkTChanel) c2list.get(i);
						cdlist.add(m, ch2.getKcId());
						m++;
						List c3list=taskService.getChildType(ch2.getKcId());
						for(int l=0;l<c3list.size();l++)
						{
							WkTChanel ch3=(WkTChanel) c3list.get(i);
							cdlist.add(m, ch3.getKcId());
							m++;
						}
					}
				}
				
			}
			tList=taskService.findByFolderId(cdlist);
		}  
		if(tList.size()!=0){
		modelList=new ListModelList(tList);
		taskList.setModel(modelList);
		taskList.setItemRenderer(new ListitemRenderer(){
			public void render(final Listitem item, Object arg1) throws Exception {
				final WkTExtractask task=(WkTExtractask)arg1;
				item.setValue(task);
				String taskName=task.getKeName();
				String taskRemark=task.getKeRemk();
				Listcell c1=new Listcell(item.getIndex()+1+"");
				if(taskName.length()>10){
					taskName=taskName.substring(0,10)+"...";
				}
				Listcell c2=new Listcell(taskName);
				Listcell c3=new Listcell(task.getKeBegincount());
				if(taskRemark!=null && taskRemark.length()>10){
					taskRemark=taskRemark.substring(0,10)+"...";
				}
				Listcell c4=new Listcell(taskRemark);
				final Listcell c5 = new Listcell();
				if(task.getKeStatus()==Long.parseLong("0")){
					c5.setStyle("color:red");
					c5.setLabel(task.END);
				}if(task.getKeStatus()==Long.parseLong("1")){
					c5.setStyle("color:blue");
					c5.setLabel(task.BEGIN);
				}if(task.getKeStatus()==Long.parseLong("2")){
					c5.setStyle("color:green");
					c5.setLabel(task.TIME_TASK);
				}
				Listcell c6=new Listcell(task.getKeTime());
				
				item.appendChild(c1);item.appendChild(c2);
				item.appendChild(c3);item.appendChild(c4);
				item.appendChild(c5);item.appendChild(c6);
				
				item.setContext(item+"");//注意
				Menupopup pop=new Menupopup();
				pop.setId(item+"");
				Menuitem menu=new Menuitem();
				menu.setLabel("开始");
				menu.setImage("/images/content/use.jpg");
				Menuitem menu2=new Menuitem();
				menu2.setLabel("停止");
				menu2.setImage("/images/content/stop.jpg");
				Menuitem menu4=new Menuitem();
				menu4.setLabel("编辑");
				menu4.setVisible(false);
				menu4.setImage("/images/content/edit.jpg");
				Menuitem menu3=new Menuitem();
				menu3.setLabel("删除");
				menu3.setImage("/images/content/del.gif");
				pop.appendChild(menu);pop.appendChild(menu2);
				pop.appendChild(menu4);pop.appendChild(menu3);
				c1.appendChild(pop);c2.appendChild(pop);
				
				menu.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						c5.setStyle("color:blue");
						c5.setLabel(task.BEGIN);
						c5.addEventListener(Events.ON_CHANGE, new EventListener(){
							public void onEvent(Event arg0) throws Exception {
								c5.setStyle("color:blue");
								c5.setLabel(task.BEGIN);
							}
							
						});
						
						beginOnRightClick(c5,task,tabs,box);
					}
				});//taskBegin
				
				menu2.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
//						ServerPush push=new ServerPush(box);
						Executions.getCurrent().getDesktop().enableServerPush(false);
//						push.setDone();
						LinkCollection.getUnVisitedUrl().deleteAll();
						LinkCollection.getVisitedUrl().clear();
						c5.setStyle("color:red");
						c5.setLabel(task.END);
					}
					
				});
				
				menu3.addEventListener(Events.ON_CLICK,new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						if(Messagebox.show("确定删除任务信息及其下的全部所属信息 ?","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
							
							deleteTaskAndInfo(task);
							deleteGuideAndReg(task);
							//loadChooseFolder(tasktype);
							initWindow(tasktype);
						}
					}
				});//Delete
				menu4.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						EditTask(task);
					}
					
				});
				//双击任务
				item.addEventListener(Events.ON_DOUBLE_CLICK,new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						EditTask(task);
					}
				});
			}
		});//Task Show
		}else{
			modelList=new ListModelList(tList);
			taskList.setModel(modelList);
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
	
	
	
	/*编辑任务*/
	public void EditTask(final WkTExtractask extractask){
		Configure configure=(Configure)Executions.createComponents("/apps/infoExtra/content/tasktemp/configure.zul",null,null);
		configure.doInit(extractask);
		configure.doHighlighted();
		configure.addEventListener(Events.ON_CHANGE, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				WkTChanel tEntity=taskService.findByFolderID(extractask.getKcId());
				//loadChooseFolder(tEntity);
				initWindow(tasktype);
			}
		});
	}
	
	public void deleteGuideAndReg(WkTExtractask extractTask){
		List<WkTGuidereg> gList=guideService.findGuideListById(extractTask.getKeId());
		List<WkTPickreg> pList=pickService.findpickReg(extractTask.getKeId());
		WkTGuidereg guideReg;
		for(int g=0;g<gList.size();g++){
			guideReg=gList.get(g);
			guideService.delete(guideReg);
		}
		WkTPickreg pReg;
		for(int p=0;p<pList.size();p++){
			pReg=pList.get(p);
			pickService.delete(pReg);
		}
		
	}
	
//	public void loadChooseFolder(WkTChanel t){
//		List tList=taskService.findByFolderId(t.getKcId());
//		modelList=new ListModelList(tList);
//		taskList.setModel(modelList);
//	}
	
	// 右键抽取开始 
    public synchronized void beginOnRightClick(final Listcell cell,WkTExtractask eTask,Tabs tabs,final Tabbox box){
		
	Tab findTab=findTab(eTask,tabs);
	if(findTab!=null){
		findTab.setSelected(true);
	}else{

		Long taskId=eTask.getKeId();
		List<WkTGuidereg> guideList=guideService.findGuideListById(taskId);
		final List<WkTPickreg> pickRegList=pickService.findpickReg(taskId);
		Date date=new Date();
		String dateResult=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
		eTask.setKeTime(dateResult);
		taskService.update(eTask);
//		Callable myCallable = null;
		MyCallable myCallable;
		MyCallableSave myCallable2;
		if(eTask.getKePubtype()==Long.parseLong("0")){
			//动态添加
			Tabpanel tabpanel=(Tabpanel)Executions.createComponents("/apps/infoExtra/content/tasktemp/Tabpanel.zul", null, null);
			Tab t1=new Tab(eTask.getKeName());
			t1.setId(eTask.getKeId()+"");
			t1.setClosable(true);
			tabs.appendChild(t1);
			box.appendChild(tabs);
				
			
			Listbox listbox=(Listbox)Executions.createComponents("/apps/infoExtra/content/tasktemp/listbox.zul", null, null);
			Listhead head=new Listhead();
			head.setSizable(true);
			Listheader header = null;
			WkTPickreg pReg;
			double width;
			for(int h=0;h<pickRegList.size();h++){
				pReg=(WkTPickreg)pickRegList.get(h); 
				header=new Listheader(pReg.getKpRegname());
				width=1/(double)pickRegList.size();
				header.setWidth(100*width+"%");
				header.setAlign("left");
				head.appendChild(header);
			}
			listbox.appendChild(head);
			tabpanel.appendChild(listbox);
			box.getTabpanels().appendChild(tabpanel);
			
			
			myCallable=new MyCallable(cell,box,eTask,guideList,pickRegList);
			myCallable.call();
			
			box.getSelectedTab().addEventListener(Events.ON_CLOSE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					ServerPush push=new ServerPush(box);
					Executions.getCurrent().getDesktop().enableServerPush(false);
					push.setDone();
					LinkCollection.getUnVisitedUrl().deleteAll();
					LinkCollection.getVisitedUrl().clear();
					Tabpanel pTabpanel=box.getSelectedPanel();
					box.removeChild(pTabpanel);
				}
			});
			
		}else if(eTask.getKePubtype()==Long.parseLong("1")){
			String path=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/mht");
			String path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/html");
			myCallable2=new MyCallableSave(eTask,guideList,pickRegList,linkService,infoService,path,path1);
			myCallable2.call();
			cell.setStyle("color:red");
			cell.setLabel(eTask.END);
		}
		
	}//else
	
 }
	
	private Tab findTab(WkTExtractask task,Tabs tabs) {
		for(int t=0;t<tabs.getChildren().size();t++){
			Tab tab=(Tab)tabs.getChildren().get(t);
			if(tab.getId().equalsIgnoreCase(task.getKeId()+"")){
				return tab;
			}
		}
		return null;
		
	}
	/*html标记设置 */
	public void onClick$signSet(){
		
		HtmlSign sign=(HtmlSign)Executions.createComponents("/apps/infoExtra/content/tasktemp/htmlSign.zul", null, null);
		sign.Init();
		sign.doHighlighted();
	}
	

}
