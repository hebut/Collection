package com.uniwin.webkey.infoExtra.newspub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.lang.DRLParser.simple_operator_return;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditDetailListRendererNew;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

import edu.emory.mathcs.backport.java.util.Collections;

public class NewspubEditWindowNew extends Window implements AfterCompose {
	
	private static final long serialVersionUID = -2451318625439388515L;
	//信息数据访问接口
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
	WkTExtractask etask;
	WkTInfocnt infocnt;
	Textbox searchtext;
	WkTInfo info,selected;
	Log log=LogFactory.getLog(NewspubEditWindowNew.class);
	WkTDistribute dis;
	private Users user;
	private Session currSession = Sessions.getCurrent();
	Map params;	
	String bt,et,k,s,flag;
	Datebox begintime,endtime;
	Textbox keys,source;
	Listitem title,content,auth,memo;
	//界面上的各种组件
	Tab writerTab,auditTab,rebackTab,orinfoTab; 
	//信息列表
	Listbox orinfoListbox,writerListbox,auditListbox,rebackListbox;
	ListModelList orinfoListModel,writerListModel,auditListModel,rebackListModel;
	List  slist=new ArrayList();
	//搜索、添加、删除按钮
	Toolbarbutton addnews,deletenews,searchnews,resetnews,applyBT;

	//	Paging orinPage,writerPage,auditPage,rebackPage;
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int jishu,pageSize,start;
	Long userid;
	
	private List applyTaskList;
	
	Tree tree;
	public Tree getTree() {
		return tree;
	}
	public void setTree(Tree tree) {
		this.tree = tree;
	}
	public List getApplyTaskList() {
		return applyTaskList;
	}
	public void setApplyTaskList(List applyTaskList) {
		this.applyTaskList = applyTaskList;
	}

	public void afterCompose() {
	//	log.info("--data-test-- ");
		params=this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users)currSession.getAttribute("users");
		Date date = new Date();  
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		applyBT.setTooltiptext("点击按钮，获取待编辑的新任务");
		//顶部添加按钮的事件
		addnews.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {
				NewsNewWindow w=(NewsNewWindow)Executions.createComponents("/apps/infoExtra/content/newspubnew/new.zul", null, null);
				WkTExtractask task = newsService.getWktExtByKeid(Long.parseLong(applyTaskList.get(0).toString()));
				w.initWindow(task);
				w.doHighlighted();
				w.addEventListener(Events.ON_CHANGE, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						reloadList();
					}
				});
			}
		});
		//各个tab下顶部各按钮可用情况
		orinfoTab.addEventListener(Events.ON_SELECT,new  DisDeleteEventLisenter() );
		auditTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		rebackTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		writerTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		
		orinfoTab.addEventListener(Events.ON_SELECT,new  EnaAuditEventLisenter() );
		auditTab.addEventListener(Events.ON_SELECT,new  DisAuditEventLisenter() );
		rebackTab.addEventListener(Events.ON_SELECT,new  DisAuditEventLisenter() );
		writerTab.addEventListener(Events.ON_SELECT,new  EnaAuditEventLisenter() );
		//顶部删除按钮的事件
		deletenews.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {	
				if(writerTab.isSelected())
				  {
					deleteinfo(writerListbox,writerListModel);	
				  }
				else if(auditTab.isSelected())
				  {
					deleteinfo(auditListbox,auditListModel);	
				  }
				else if(rebackTab.isSelected())
				  {
					deleteinfo(rebackListbox,rebackListModel);	
				  }
			}
		});	
		
		
		orinfoTab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				Date date = new Date();
				endtime.setValue(date);
				Date date1 = new Date();
				date1.setDate(1);
				begintime.setValue(date1);
				keys.setValue("");
				source.setValue("");
				
				 Treeitem it=tree.getSelectedItem();
				 if(it!=null){
					 if(it.getValue() instanceof WkTExtractask)
					 {
						WkTExtractask etask=(WkTExtractask) it.getValue();
						initWindow(etask);
					 }
					 else
					 {
						 WkTChanel chanel = (WkTChanel)it.getValue();
						 chanelInitWindow1(chanel,applyTaskList);
					 }
				 }
			}
		});
		auditTab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				Date date = new Date();
				endtime.setValue(date);
				Date date1 = new Date();
				date1.setDate(1);
				begintime.setValue(date1);
				keys.setValue("");
				source.setValue("");
				
				 Treeitem it=tree.getSelectedItem();
				 if(it!=null){
					 if(it.getValue() instanceof WkTExtractask)
					 {
						WkTExtractask etask=(WkTExtractask) it.getValue();
						initWindow(etask);
					 }
					 else
					 {
						 WkTChanel chanel = (WkTChanel)it.getValue();
						 chanelInitWindow1(chanel,applyTaskList);
					 }
				 }
			}
		});
		writerTab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				Date date = new Date();
				endtime.setValue(date);
				Date date1 = new Date();
				date1.setDate(1);
				begintime.setValue(date1);
				keys.setValue("");
				source.setValue("");
				
				 Treeitem it=tree.getSelectedItem();
				 if(it!=null){
					 if(it.getValue() instanceof WkTExtractask)
					 {
						WkTExtractask etask=(WkTExtractask) it.getValue();
						initWindow(etask);
					 }
					 else
					 {
						 WkTChanel chanel = (WkTChanel)it.getValue();
						 chanelInitWindow1(chanel,applyTaskList);
					 }
				 }
			}
		});
		rebackTab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				Date date = new Date();
				endtime.setValue(date);
				Date date1 = new Date();
				date1.setDate(1);
				begintime.setValue(date1);
				keys.setValue("");
				source.setValue("");
				
				 Treeitem it=tree.getSelectedItem();
				 if(it!=null){
					 if(it.getValue() instanceof WkTExtractask)
					 {
						WkTExtractask etask=(WkTExtractask) it.getValue();
						initWindow(etask);
					 }
					 else
					 {
						 WkTChanel chanel = (WkTChanel)it.getValue();
						 chanelInitWindow1(chanel,applyTaskList);
					 }
				 }
			}
		});
		//分页
    	//loadPageing();
	}
	

	
	// 批量删除信息	 
	public void deleteinfo(Listbox listbox,ListModelList listModel) throws InterruptedException
	{
		if(listbox.getSelectedItem()==null)
		{
		Messagebox.show("请选择要删除的信息！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		else{	
			if(Messagebox.show("确定要删除吗？", "确认", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
			{
			Set sel=listbox.getSelectedItems();
				Iterator it=sel.iterator();
				List delinfoList=new ArrayList();
				List deldisList=new ArrayList();
				List delinfocntList=new ArrayList();
				     while(it.hasNext()){
				    	 Listitem item=(Listitem)it.next();
						 dis=(WkTDistribute)item.getValue();	
						 delinfoList.add(newsService.getWkTInfo(dis.getKiId()));
						 deldisList.add(dis);
						 delinfocntList.add(newsService.getInfocnt(dis.getKiId()));
				     }
				     for(int i=0;i<deldisList.size();i++)
				     {
				    	 if((((WkTDistribute)deldisList.get(i)).getKbFlag()).trim().equals("1"))
					     {
				    		 newsService.delete((WkTDistribute)deldisList.get(i));
					     }
				    	 else 
				    	 {   
				    		 List d=newsService.getDistributeList(((WkTDistribute)deldisList.get(i)).getKiId());
				    		 List f=newsService.getFile(((WkTDistribute)deldisList.get(i)).getKiId());
				    		 if(f.size()!=0)
				    		 {
				    		 for(int n=0;n<f.size();n++)
				    			 newsService.delete((WkTFile)f.get(n));
				    		 }
						    	for(int j=0;j<d.size();j++)
						    	newsService.delete((WkTDistribute)d.get(j));
						    	newsService.delete(delinfoList.get(i));
						    	List ic=newsService.getInfocnt(((WkTInfo)delinfoList.get(i)).getKiId());
						    	for(int j=0;j<ic.size();j++)
						    		 newsService.delete(ic.get(j));
				    	 }
				     }
				     listModel.removeAll(deldisList);
				     Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				reloadList();
			}
		}
	}
	public void initWindow(WkTExtractask etask)
	{
//		this.etask = etask;
		userid=Long.parseLong(user.getUserId().toString());
		
	/*	pageSize=orinPage.getPageSize();
		Long keid=etask.getKeId();
		int oriSize=orinewsService.findOriInfoCount(keid);
		orinPage.setTotalSize(oriSize);
		
		int infoSize;
		List infoList=newsService.getNewsOfChanelZG(keid,userid);
		if(infoList!=null && infoList.size()>0){
			infoSize=infoList.size();
		}else{
			infoSize=0;
		}
		
		writerPage.setTotalSize(infoSize);
		
		int auditSize;
		List auditList=newsService.getNewsOfChanelSS(keid,userid);
		if(auditList!=null && auditList.size()>0){
			auditSize=auditList.size();
		}else{
			auditSize=0;
		}
		auditPage.setTotalSize(auditSize);
		
		int rebackSize;
		List rebacklist=newsService.getNewsOfChanelTH(keid,userid);
		if(rebacklist!=null && rebacklist.size()>0){
			rebackSize=rebacklist.size();
		}else{
			rebackSize=0;
		}
		rebackPage.setTotalSize(rebackSize);*/
		loadList(etask);
	}
	//设置pub.zul界面删除按钮的响应状态״̬
    class DisDeleteEventLisenter implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 deletenews.setVisible(false);
			 /*searchnews.setDisabled(true);
			 resetnews.setDisabled(true);*/
			 searchnews.setDisabled(false);
			 resetnews.setDisabled(false);
		}		
	}
    class EnaDeleteEventLisenter  implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 deletenews.setVisible(true);
			 deletenews.setDisabled(false);
			 searchnews.setDisabled(false);
			 resetnews.setDisabled(false);
		}		
	}	
  
    //设置pub.zul界面送审按钮的响应状态״̬
    class DisAuditEventLisenter implements EventListener{
		public void onEvent(Event arg0) throws Exception {
		}		
	}
    class EnaAuditEventLisenter  implements EventListener{
		public void onEvent(Event arg0) throws Exception {
		}		
	}	
    
  	//搜索信息
  	public void onClick$searchnews() throws InterruptedException
  	{
  		 bt = ConvertUtil.convertDateAndTimeString(begintime.getValue());
  		 et = ConvertUtil.convertDateAndTimeString(endtime.getValue());
  		 if(bt.compareTo(et)>0)
  		 {
  			Messagebox.show("开始时间不能大于截止时间！", "Information", Messagebox.OK, Messagebox.INFORMATION);
  			 return;
  		 }
  		if(!keys.getValue().equals(""))  
  		{
  			k=keys.getValue();
  			if(content.isSelected()) flag="1";
  			else flag="2";
  		}
  		else k="";
  		if(!source.getValue().equals(""))
  		{s=source.getValue().trim();}
  		else s="";
  		///
  		List list = new ArrayList();
  		Treeitem it=tree.getSelectedItem();
  		if(it.getValue()instanceof WkTExtractask){
  			WkTExtractask etask=(WkTExtractask) it.getValue();
  			list.add(etask.getKeId());
  		}else{
  			 WkTChanel chanel = (WkTChanel)it.getValue();
  			list = chanelInitWindow2(chanel, applyTaskList);
  		}
  		////
  		userid=Long.parseLong(user.getUserId().toString());
  		if(writerTab.isSelected())
  		{
  			Long  status=1L;
  			//List slist=newsService.NewsSearch(applyTaskList,status,bt,et,flag,k,s);
  			//List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s);
  			List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s,userid);
  			searchinfo(slist,writerListbox,writerListModel);	
  		}
  		else if(auditTab.isSelected())
  		{
  			Long  status=2L;
  			//List slist=newsService.NewsSearch(applyTaskList,status,bt,et,flag,k,s);
  			//List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s);
  			List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s,userid);
  			searchinfo(slist,auditListbox,auditListModel);	
  		}
  		else if(rebackTab.isSelected())
  		{
  			Long  status=4L;
  			//List slist=newsService.NewsSearch(applyTaskList,status,bt,et,flag,k,s);
  			//List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s);
  			List slist=newsService.NewsSearch(list,status,bt,et,flag,k,s,userid);
  			searchinfo(slist,rebackListbox,rebackListModel);	
  		}
 		else //原始信息界面的查询按钮
 	  		{
 			List slist=newsService.OriNewsSearch(list,bt,et,flag,k,s,userid);
 			searchinfo(slist,orinfoListbox,orinfoListModel);	
  	  		}
  	}
	//重载搜索到的信息列表
	public void searchinfo(List slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	//加载信息列表
	public void reloadList()
	{ 	
		userid=Long.parseLong(user.getUserId().toString());
	    List infoList=newsService.getNewsOfChanelZGA(applyTaskList,userid);
	    List auditlist=newsService.getNewsOfChanelSSA(applyTaskList,userid);
	    List rebacklist=newsService.getNewsOfChanelTHA(applyTaskList,userid);
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
	
	}
	
	public void loadList(WkTExtractask etask)
	{ 	
		userid=Long.parseLong(user.getUserId().toString());
	    List infoList=newsService.getNewsOfChanelZG(etask.getKeId(),userid);
	    List auditlist=newsService.getNewsOfChanelSS(etask.getKeId(),userid);
	    List rebacklist=newsService.getNewsOfChanelTH(etask.getKeId(),userid);
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		///
		List e = new ArrayList<Long>();
		e.add(etask.getKeId());
		List<WkTOrinfo> orinfoList = orinewsService.findOrinfoByTaskList(e, user.getUserId().toString());
		orinfoListModel = new ListModelList();
		if(orinfoList!=null&&orinfoList.size()!=0){
			orinfoListModel.addAll(orinfoList);
		}
		
		if (orinfoListModel.size() != 0 && orinfoListModel != null) {
			orinfoListbox.setModel(orinfoListModel);
			orinfoListbox.setItemRenderer(new NewsDetailListRendererNew(
					orinewsService, newsService, orinfoListModel,
					writerListModel, auditListModel, rebackListModel,
					orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
		}
		///
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
	}
	
	//重置搜索条件
	public void onClick$resetnews()
	{
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		keys.setValue("");
		source.setValue("");
		//reloadList();
	}
	
	public void initAllWindow(List taskList){
        Long userid = Long.parseLong(user.getUserId().toString());
      //从原始信息中找出该用户名下所有任务栏目的讯息
        List<WkTOrinfo> orinfoList = orinewsService.findOrinfoByTaskList(taskList, user.getUserId().toString());
		List infoList = newsService.getNewsOfChanelZGA(taskList,userid);
		List auditlist = newsService.getNewsOfChanelSSA(taskList,userid);
		List rebacklist = newsService.getNewsOfChanelTHA(taskList,userid);
		
		orinfoListModel = new ListModelList();
		if(orinfoList!=null&&orinfoList.size()!=0){
			orinfoListModel.addAll(orinfoList);
		}	
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		if (orinfoListModel.size() != 0 && orinfoListModel != null) {
			orinfoListbox.setModel(orinfoListModel);
			orinfoListbox.setItemRenderer(new NewsDetailListRendererNew(
					orinewsService, newsService, orinfoListModel,
					writerListModel, auditListModel, rebackListModel,
					orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
		}
		
		if(writerListModel.size() != 0 && writerListModel != null){
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		}
		
		if(auditListModel.size() !=0 && auditListModel != null){
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		}
		if(rebackListModel.size() !=0 && rebackListModel != null){
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
		}
		
		Events.postEvent(Events.ON_SELECT, orinfoTab, null);
	}
	
	public void chanelInitWindow(WkTChanel chanel, List alist){
		List<WkTExtractask> tList = new ArrayList<WkTExtractask>();
		List<Long> idList = new ArrayList<Long>();
		List taskList = new ArrayList();
		if(chanel.getKcId() == 1){//根文件夹
			tList = taskService.findAllTask();
		}else{
			List clist=taskService.getChildType(chanel.getKcId());
			List cdlist=new ArrayList();
			int m=1;
			cdlist.add(0, chanel.getKcId());
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
			tList = taskService.findByFolderId(cdlist);
		}

		if (alist != null) {
			for (int i = 0; i < tList.size(); i++) {
				int count = 0;
				for (int j = 0; j < alist.size(); j++) {
					WkTExtractask etask = (WkTExtractask) alist.get(j);
					if (etask.getKeId().toString().trim()
							.equals(tList.get(i).getKeId().toString().trim())) {
						count++;
					}
				}
				if (count != 0) {
					taskList.add(taskList.size(), tList.get(i).getKeId());
				}
			}
			if(taskList.size() != 0){
				initAllWindow(taskList);
			}
		}
		    
	}
	
	public void chanelInitWindow1(WkTChanel chanel, List alist){
		List<WkTExtractask> tList = new ArrayList<WkTExtractask>();
		List<Long> idList = new ArrayList<Long>();
		List taskList = new ArrayList();
		if(chanel.getKcId() == 1){
			tList = taskService.findAllTask();
		}else{
			List clist=taskService.getChildType(chanel.getKcId());
			List cdlist=new ArrayList();
			int m=1;
			cdlist.add(0, chanel.getKcId());
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
			tList = taskService.findByFolderId(cdlist);
		}

		if (alist != null) {
			for (int i = 0; i < tList.size(); i++) {
				int count = 0;
				for (int j = 0; j < alist.size(); j++) {
					//WkTExtractask etask = (WkTExtractask) alist.get(j);
					if ( alist.get(j).toString().trim()
							.equals(tList.get(i).getKeId().toString().trim())) {
						count++;
					}
				}
				if (count != 0) {
					taskList.add(taskList.size(), tList.get(i).getKeId());
				}
			}
			if(taskList.size() != 0){
				 Long userid = Long.parseLong(user.getUserId().toString());
			      //从原始信息中找出该用户名下所有任务栏目的讯息
			        List<WkTOrinfo> orinfoList = orinewsService.findOrinfoByTaskList(taskList, user.getUserId().toString());
					List infoList = newsService.getNewsOfChanelZGA(taskList,userid);
					List auditlist = newsService.getNewsOfChanelSSA(taskList,userid);
					List rebacklist = newsService.getNewsOfChanelTHA(taskList,userid);
					
					orinfoListModel = new ListModelList();
					if(orinfoList!=null&&orinfoList.size()!=0){
						orinfoListModel.addAll(orinfoList);
					}	
					
					writerListModel=new ListModelList();
					writerListModel.addAll(infoList);
					
					auditListModel=new ListModelList();
					auditListModel.addAll(auditlist);
					
					rebackListModel=new ListModelList();
					rebackListModel.addAll(rebacklist);
					
					if (orinfoListModel.size() != 0 && orinfoListModel != null) {
						orinfoListbox.setModel(orinfoListModel);
						orinfoListbox.setItemRenderer(new NewsDetailListRendererNew(
								orinewsService, newsService, orinfoListModel,
								writerListModel, auditListModel, rebackListModel,
								orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
					}
					
					if(writerListModel.size() != 0 && writerListModel != null){
					writerListbox.setModel(writerListModel);
					writerListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
							orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
					}
					
					if(auditListModel.size() !=0 && auditListModel != null){
					auditListbox.setModel(auditListModel);
					auditListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
							orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
					}
					if(rebackListModel.size() !=0 && rebackListModel != null){
					rebackListbox.setModel(rebackListModel);
					rebackListbox.setItemRenderer(new NewsDetailListRendererNew(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
							orinfoListbox,writerListbox, auditListbox,rebackListbox,applyTaskList));
					}
			}
			
		}
	}
	
	public List chanelInitWindow2(WkTChanel chanel, List alist){
		List<WkTExtractask> tList = new ArrayList<WkTExtractask>();
		List<Long> idList = new ArrayList<Long>();
		List taskList = new ArrayList();
		if(chanel.getKcId() == 1){
			tList = taskService.findAllTask();
		}else{
			List clist=taskService.getChildType(chanel.getKcId());
			List cdlist=new ArrayList();
			int m=1;
			cdlist.add(0, chanel.getKcId());
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
			tList = taskService.findByFolderId(cdlist);
		}

		if (alist != null) {
			for (int i = 0; i < tList.size(); i++) {
				int count = 0;
				for (int j = 0; j < alist.size(); j++) {
					//WkTExtractask etask = (WkTExtractask) alist.get(j);
					if ( alist.get(j).toString().trim()
							.equals(tList.get(i).getKeId().toString().trim())) {
						count++;
					}
				}
				if (count != 0) {
					taskList.add(taskList.size(), tList.get(i).getKeId());
				}
			}
		}
		return taskList;
	}
	
	public void onClick$applyBT(){
		Long userid = Long.parseLong(user.getUserId().toString());
		//从原始信息表中取出未被申请的，时间最新的一条讯息。没有区分任务栏目，针对全部原始信息
		WkTOrinfo orinfo = orinewsService.findOrinfor(applyTaskList,user.getUserId().toString());
		//tree.setSelectedItem((Treeitem)tree.getFirstChild());//使树的根节点被选中
		///指向树的根节点
				while(tree.getSelectedItem()!=null&&tree.getSelectedItem().getParentItemApi()!=null) {
					 WkTChanel chanel = (WkTChanel)tree.getSelectedItem().getParentItemApi().getValue();
					 if(chanel.getKcId()!=1){
						 tree.setSelectedItemApi(tree.getSelectedItem().getParentItemApi());
					 }else{
						 tree.setSelectedItemApi(tree.getSelectedItem().getParentItemApi());
						 break;
					 }
				}
				
				///
		 
		if(orinfo!=null){
			List<WkTOrinfo> orinfoList = new ArrayList<WkTOrinfo>();
			//取出该用户已申请的原始信息
			List<WkTOrinfo> orl = orinewsService.findOrinfoByTaskList(applyTaskList, user.getUserId().toString());
			if(orl!=null){
				orinfoList = orl;
			}
			
			orinfo.setKoiApply(user.getUserId().toString());
			orinewsService.update(orinfo);
			
			orinfoList.add(orinfo);
			
			List infoList = newsService.getNewsOfChanelZGA(applyTaskList, userid);
			List auditlist = newsService.getNewsOfChanelSSA(applyTaskList, userid);
			List rebacklist = newsService.getNewsOfChanelTHA(applyTaskList, userid);
			
/*			List<Listitem> list = orinfoListbox.getItems();
			orinfoListModel = new ListModelList();
			if(list!=null&&list.size()!=0){
				for(Listitem o : list){
					WkTOrinfo of = (WkTOrinfo)o.getValue();
					orinfoList.add(of);
				}
			}*/
			//按时间降序排列
			Collections.sort(orinfoList, new Comparator<WkTOrinfo>() {

				public int compare(WkTOrinfo o1, WkTOrinfo o2) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					try {
						if(o1!=null&&o2!=null){
							int a = sdf.parse(o1.getKoiCtime()).compareTo(sdf.parse(o2.getKoiCtime()));
							if((sdf.parse(o1.getKoiCtime()).compareTo(sdf.parse(o2.getKoiCtime())))==-1)
								return 1;
							else if((sdf.parse(o1.getKoiCtime()).compareTo(sdf.parse(o2.getKoiCtime())))==1)
									return -1;
								//if((sdf.parse(o1.getKoiCtime()).compareTo(sdf.parse(o2.getKoiCtime())))==0)
								
							//else	return -1;
							else {
								if(o1.getKoiId().compareTo(o2.getKoiId())==-1){
									return 1;
								}else{
									return -1;
								}
								
							}
						}
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return 0;
				}
			});
			
			
	      //  orinfoList = orinewsService.findOrinfoByTaskList(applyTaskList, user.getUserId().toString());
	        orinfoListModel = new ListModelList();
			orinfoListModel.addAll(orinfoList);

			writerListModel = new ListModelList();
			writerListModel.addAll(infoList);

			auditListModel = new ListModelList();
			auditListModel.addAll(auditlist);

			rebackListModel = new ListModelList();
			rebackListModel.addAll(rebacklist);

			if (writerListModel.size() != 0 && writerListModel != null) {
				writerListbox.setModel(writerListModel);
				writerListbox.setItemRenderer(new NewsDetailListRendererNew(
						orinewsService, newsService, orinfoListModel,
						writerListModel, auditListModel, rebackListModel,
						orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
			}

			if (orinfoListModel.size() != 0 && orinfoListModel != null) {
				orinfoListbox.setModel(orinfoListModel);
				orinfoListbox.setItemRenderer(new NewsDetailListRendererNew(
						orinewsService, newsService, orinfoListModel,
						writerListModel, auditListModel, rebackListModel,
						orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
			}

			if (auditListModel.size() != 0 && auditListModel != null) {
				auditListbox.setModel(auditListModel);
				auditListbox.setItemRenderer(new NewsDetailListRendererNew(
						orinewsService, newsService, orinfoListModel,
						writerListModel, auditListModel, rebackListModel,
						orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
			}
			if (rebackListModel.size() != 0 && rebackListModel != null) {
				rebackListbox.setModel(rebackListModel);
				rebackListbox.setItemRenderer(new NewsDetailListRendererNew(
						orinewsService, newsService, orinfoListModel,
						writerListModel, auditListModel, rebackListModel,
						orinfoListbox, writerListbox, auditListbox, rebackListbox,applyTaskList));
			}			
		}
		else{
			try {
				Messagebox.show("没有可编辑的信息");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
}
	

