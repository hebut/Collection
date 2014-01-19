package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * 信息审核时初始化信息列表
 * 2010-3-21
 * @author whm
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditDetailListRenderer;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;



public class NewsauditEditWindowNew extends Window implements AfterCompose {
	
	private static final long serialVersionUID = 1L;
	WkTExtractask etask;
	Users user;
	//管理日志数据访问接口
	//MLogService mlogService;
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	private Session currSession = Sessions.getCurrent();
	Toolbarbutton deletenews;
	Tab auditTab,pubTab,writeTab;
	String bt,et,k,s,flag;
	Datebox begintime,endtime;
	Textbox keys,source;
	Listitem title,content,auth,memo;
	//信息列表
	Listbox auditListbox,pubListbox,writeListbox;
	ListModelList pubListModel,auditListModel,readListModel;
	Toolbarbutton searchnews,resetnews,applyBT;

	//List<WkTDistribute> auditList_temp = new ArrayList<WkTDistribute>();
	Long userid;
	
	Tree tree;
	public Tree getTree() {
		return tree;
	}
	public void setTree(Tree tree) {
		this.tree = tree;
	}
	
	
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
    private List applyList;
	
	public List getApplyList() {
		return applyList;
	}
	public void setApplyList(List applyList) {
		this.applyList = applyList;
	}
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		user = (Users)currSession.getAttribute("users");
		applyBT.setTooltiptext("点击按钮，获取待审核的新任务");
		/*auditTab.addEventListener(Events.ON_SELECT,new  DisSeacherEventLisenter());
		writeTab.addEventListener(Events.ON_SELECT,new  EnSeacherEventLisenter());
		pubTab.addEventListener(Events.ON_SELECT,new  EnSeacherEventLisenter());*/
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
						 chanelInitWindow1(chanel,applyList);
					 }
				 }
			}
		});
		writeTab.addEventListener(Events.ON_SELECT, new EventListener() {
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
						 chanelInitWindow1(chanel,applyList);
					 }
				 }
			}
		});
		pubTab.addEventListener(Events.ON_SELECT, new EventListener() {
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
						 chanelInitWindow1(chanel,applyList);
					 }
				 }
			}
			
		});

		
		
		 //批量审核信息
		/*auditnews.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {	
			if(auditTab.isSelected())
			{
				if(auditListbox.getSelectedItem()==null){
					Messagebox.show("请选择您要审核的信息！");
					return;
				}else{	
					if(Messagebox.show("确定要通过审核吗？", "确认", 
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
					{
						
					Set sel=auditListbox.getSelectedItems();
						Iterator it=sel.iterator();			
						while(it.hasNext()){
							Listitem item=(Listitem)it.next();
							WkTDistribute dis=(WkTDistribute)item.getValue();
								dis.setKbStatus("0");
								newsService.update(dis);
								//发布html
								WkTInfo info=newsService.getInfobyBid(dis.getKbId());
							 	Map root = new HashMap();
							 	Sessions.getCurrent().setAttribute("root", root);
							 	WKT_DOCLIST dList = new WKT_DOCLIST();
							 	dList.singleNewsPublic(info, dis.getKcId());
							}			
					}
				}
			}
					else if(readTab.isSelected())
					{
						if(readListbox.getSelectedItem()==null){
							Messagebox.show("请选择您要审核的信息！");
							return;
						}else{	
							if(Messagebox.show("确定要通过审核吗？", "确认", 
									Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
							{
								
							Set sel=readListbox.getSelectedItems();
								Iterator it=sel.iterator();			
								while(it.hasNext()){
									Listitem item=(Listitem)it.next();
									WkTDistribute	 dis=(WkTDistribute)item.getValue();
										dis.setKbStatus("0");
										newsService.update(dis);
										WkTInfo info=newsService.getInfobyBid(dis.getKbId());
									 	Map root = new HashMap();
									 	Sessions.getCurrent().setAttribute("root", root);
									 	WKT_DOCLIST dList = new WKT_DOCLIST();
									 	dList.singleNewsPublic(info, dis.getKcId());
									 	
									}			
							}
						}
					}
//					Messagebox.show("完成审核！");
					reloadList();		
			}
		});
		//批量删除信息
		deletenews.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {	
				if(auditTab.isSelected())
				{
				deleteinfo(auditListbox,auditListModel);	
				}
		else if(readTab.isSelected())
		{
					deleteinfo(readListbox,readListModel);	
		}
		else if(pubTab.isSelected())
		{
			deleteinfo(pubListbox,pubListModel);	
		}
		else if(writeTab.isSelected())
		{
			deleteinfo(writeListbox,writeListModel);	
		}
	}
		});*/
	}
	//设置删除按钮的响应状态״̬
   /* class DisDeleteEventLisenter implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 auditnews.setDisabled(true);
		}		
	}
    class EnaDeleteEventLisenter  implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 auditnews.setDisabled(false);
		}		
	}*/
	public void initWindow(WkTExtractask etask) {
		this.etask = etask;
//		reloadList();
		loadList();
	}
	//ɾ删除信息
	public void deleteinfo(Listbox listbox,ListModelList listModel) throws InterruptedException
	{
		if(listbox.getSelectedItem()==null)
		{
			Messagebox.show("请选择您要删除的信息！");
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
                       WkTDistribute dis=(WkTDistribute)item.getValue();	
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
						    	for(int j=0;j<d.size();j++)
						    	newsService.delete((WkTDistribute)d.get(j));
						    	 List ic=newsService.getInfocnt(((WkTInfo)delinfoList.get(i)).getKiId());
						    	 for(int m=0;m<ic.size();m++)
						    	 {
						    		 newsService.delete((WkTInfocnt)ic.get(m));
						    	 }
						    		//mlogService.saveMLog(WkTMlog.FUNC_CMS, "ɾ����Ϣ��id:"+((WkTInfo)delinfoList.get(i)).getKiId(), user);
						    	 newsService.delete((WkTInfo)delinfoList.get(i));
						    	
				    	 }
				     }
				     listModel.removeAll(deldisList);
				Messagebox.show("删除成功");
				reloadList();
			}
		}
	}
	//搜索信息
  	public void onClick$searchnews()
  	{
  		 bt = ConvertUtil.convertDateAndTimeString(begintime.getValue());
  		 et = ConvertUtil.convertDateAndTimeString(endtime.getValue());
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
  		
//  		  if(auditTab.isSelected())
//  		{
//  			Long  status=2L;
//  			List slist=newsService.NewsSearch(etask.getKeId(),status,bt,et,flag,k,s);
//  			List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
//  			searchinfo(slist,auditListbox,auditListModel);	
//  		}
  		
  		List list = new ArrayList();
  		Treeitem it=tree.getSelectedItem();
  		if(it.getValue()instanceof WkTExtractask){
  			WkTExtractask etask=(WkTExtractask) it.getValue();
  			list.add(etask.getKeId());
  		}else{
  			 WkTChanel chanel = (WkTChanel)it.getValue();
  			list = chanelInitWindow2(chanel, applyList);
  		}
  		userid=Long.parseLong(user.getUserId().toString());
  		if(pubTab.isSelected())
  		{
  			Long  status=0L;
//  			List slist=newsService.NewsSearch(etask.getKeId(),status,bt,et,flag,k,s);
  			//List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
  			List slist=newsService.NewsSearch_audit(list,status,bt,et,flag,k,s,userid);
  			searchinfo(slist,pubListbox,pubListModel);	
  		}else if(writeTab.isSelected()){
  			Long  status=3L;
  			//List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
  			List slist=newsService.NewsSearch_audit(list,status,bt,et,flag,k,s,userid);
  			searchinfo(slist,writeListbox,readListModel);	
  		}
  		
  	}
	//重载搜索到的信息列表
	public void searchinfo(List slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	//列表重载
	/*public void reloadList()
	{
		 Long userid=Long.parseLong(user.getUserId().toString());
//		 List auditList=newsService.getNewsOfChanelSS(applyList);
		 List pubList=newsService.getNewsOfChanelFB(applyList);
		 List readList=newsService.getNewsOfChanelYY(applyList);
			
		    pubListModel=new ListModelList();
		    pubListModel.addAll(pubList);
			
//			auditListModel=new ListModelList();
//			auditListModel.addAll(auditList);
		    
			readListModel=new ListModelList();
			readListModel.addAll(readList);
			
//			auditListbox.setModel(auditListModel);
//			auditListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox));
			
			pubListbox.setModel(pubListModel);
			pubListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
			
			writeListbox.setModel(readListModel);
			writeListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
	}*/
	public void reloadList(){
		
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
				 chanelInitWindow1(chanel,applyList);
			 }
		 }
	}
	
	public void loadList(){
		 Long userid=Long.parseLong(user.getUserId().toString());
//		 List auditList=newsService.getNewsOfChanelSS(etask.getKeId());
		 List auditList=new ArrayList<WkTDistribute>();
		 auditList.add(newsService.getAuditNews_applyed(applyList, user.getUserId().toString()));
		 
		 List pubList=newsService.getNewsOfChanelFB1(etask.getKeId(),userid);
		 List readList=newsService.getNewsOfChanelYY1(etask.getKeId(),userid);
			
		    pubListModel=new ListModelList();
		    pubListModel.addAll(pubList);
			
			auditListModel=new ListModelList();
			auditListModel.addAll(auditList);
			
			readListModel=new ListModelList();
			readListModel.addAll(readList);
		
			auditListbox.setModel(auditListModel);
			auditListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
			pubListbox.setModel(pubListModel);
			pubListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
			writeListbox.setModel(readListModel);
			writeListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
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
	
	//没用
	public void onClick$show()
	{
		ShowFristWindow w=(ShowFristWindow) Executions.createComponents("/admin/content/newsauditnew/showfirst.zul", null,null);
						w.doHighlighted();
						w.initWindow();
	}
	
	public void initAllWindow(List<Long> taskList){
		
		Long userid=Long.parseLong(user.getUserId().toString());
//		List auditList=newsService.getNewsOfChanelSS(taskList);
		List auditList=new ArrayList<WkTDistribute>();
		auditList.add(newsService.getAuditNews_applyed(taskList, user.getUserId().toString()));
		
		List pubList=newsService.getNewsOfChanelFB1(taskList,userid);
		List readList=newsService.getNewsOfChanelYY1(taskList,userid);
		
		
		pubListModel=new ListModelList();
		pubListModel.addAll(pubList);
	    auditListModel=new ListModelList();
		auditListModel.addAll(auditList);
		readListModel = new ListModelList();
		readListModel.addAll(readList);
		
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsauditDetailListRendererNew(
				orinewsService, newsService, readListModel, auditListModel,
				pubListModel, auditListbox, writeListbox, pubListbox,applyList));
		pubListbox.setModel(pubListModel);
		pubListbox.setItemRenderer(new NewsauditDetailListRendererNew(
				orinewsService, newsService, readListModel, auditListModel,
				pubListModel, auditListbox, writeListbox, pubListbox,applyList));
		writeListbox.setModel(readListModel);
		writeListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
		
		//Events.postEvent(Events.ON_SELECT, auditTab, null);
	
	}
	
	public void chanelInitWindow(WkTChanel chanel, List alist){
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
				Long userid=Long.parseLong(user.getUserId().toString());
//				List auditList=newsService.getNewsOfChanelSS(taskList);
				List auditList=new ArrayList<WkTDistribute>();
				auditList.add(newsService.getAuditNews_applyed(taskList, user.getUserId().toString()));
				
				List pubList=newsService.getNewsOfChanelFB1(taskList,userid);
				List readList=newsService.getNewsOfChanelYY1(taskList,userid);
				
				
				pubListModel=new ListModelList();
				pubListModel.addAll(pubList);
			    auditListModel=new ListModelList();
				auditListModel.addAll(auditList);
				readListModel = new ListModelList();
				readListModel.addAll(readList);
				
				auditListbox.setModel(auditListModel);
				auditListbox.setItemRenderer(new NewsauditDetailListRendererNew(
						orinewsService, newsService, readListModel, auditListModel,
						pubListModel, auditListbox, writeListbox, pubListbox,applyList));
				pubListbox.setModel(pubListModel);
				pubListbox.setItemRenderer(new NewsauditDetailListRendererNew(
						orinewsService, newsService, readListModel, auditListModel,
						pubListModel, auditListbox, writeListbox, pubListbox,applyList));
				writeListbox.setModel(readListModel);
				writeListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));
				
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
		String userid = user.getUserId().toString();
		//从待审（kb_status=2）信息中(属于全部任务栏的)取出最旧的一条，若有已申请的处于待审的，则从中取;若没有，则从未被申请的处于待审的中取
		WkTDistribute distribute = newsService.getAuditNews(applyList,userid);
		
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
		if(distribute!=null){
			distribute.setKbApply(userid);
			newsService.update(distribute);
			
			//仅显示一条
			List<WkTDistribute> auditList = new ArrayList<WkTDistribute>();
			auditList.add(distribute);
			//auditList_temp = auditList;
			
//			List auditList=newsService.getNewsOfChanelSS(taskList);
			/*List pubList=newsService.getNewsOfChanelFB(applyList);
			List readList=newsService.getNewsOfChanelYY(applyList);
			
			pubListModel=new ListModelList();
			pubListModel.addAll(pubList);*/
			
		    auditListModel=new ListModelList();
			auditListModel.addAll(auditList);
			
			/*readListModel = new ListModelList();
			readListModel.addAll(readList);*/
			
			auditListbox.setModel(auditListModel);
			auditListbox.setItemRenderer(new NewsauditDetailListRendererNew(
					orinewsService, newsService, readListModel, auditListModel,
					pubListModel, auditListbox, writeListbox, pubListbox,applyList));
			reloadList();
			/*pubListbox.setModel(pubListModel);
			pubListbox.setItemRenderer(new NewsauditDetailListRendererNew(
					orinewsService, newsService, readListModel, auditListModel,
					pubListModel, auditListbox, writeListbox, pubListbox,applyList));
			
			writeListbox.setModel(readListModel);
			writeListbox.setItemRenderer(new NewsauditDetailListRendererNew(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,writeListbox,pubListbox,applyList));			*/
		}
		else{
			try {
				Messagebox.show("没有可审核的信息");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	 class DisSeacherEventLisenter implements EventListener{
			public void onEvent(Event arg0) throws Exception {
//				 searchnews.setDisabled(true);
//				 resetnews.setDisabled(true);
			}		
		}
	 class EnSeacherEventLisenter implements EventListener{
			public void onEvent(Event arg0) throws Exception {
//				 searchnews.setDisabled(false);
//				 resetnews.setDisabled(false);
			}		
		}
}
