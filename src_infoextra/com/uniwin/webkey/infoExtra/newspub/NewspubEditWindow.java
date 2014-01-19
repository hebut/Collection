package com.uniwin.webkey.infoExtra.newspub;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.uniwin.webkey.infoExtra.newspub.NewsDetailListRenderer;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;


public class NewspubEditWindow extends Window implements AfterCompose {
	
	private static final long serialVersionUID = -2451318625439388515L;
	//信息数据访问接口
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
	WkTExtractask etask;
	WkTInfocnt infocnt;
	Textbox searchtext;
	WkTInfo info,selected;
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
	ListModelList orinfoListModel1,writerListModel1,auditListModel1,rebackListModel1;
	List  slist=new ArrayList();
	//搜索、添加、删除按钮
	Toolbarbutton addnews,deletenews;
	Button searchnews;
	public List applyList; 

	public List getApplyList() {
		return applyList;
	}

	public void setApplyList(List applyList) {
		this.applyList = applyList;
	}

	//	Paging orinPage,writerPage,auditPage,rebackPage;
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int jishu,pageSize,start;
	Long userid;
	
	IAuthManager authManager = (IAuthManager)SpringUtil.getBean("authManager");
	private WkTWebsite website;
	
	public void afterCompose() {
		params=this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users)currSession.getAttribute("users");
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		
		addnews.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event event) throws Exception {
				NewsNewWindow w=(NewsNewWindow)Executions.createComponents("/apps/infoExtra/content/newspub/new.zul", null, null);
				WkTExtractask task = newsService.getWktExtByKeid(Long.parseLong(applyList.get(0).toString()));
				w.initWindow(task);
				w.doHighlighted();
				w.addEventListener(Events.ON_CHANGE, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						reloadList();
					}
				});
			}
		});
		orinfoTab.addEventListener(Events.ON_SELECT,new  DisDeleteEventLisenter() );
		auditTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		rebackTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		writerTab.addEventListener(Events.ON_SELECT,new  EnaDeleteEventLisenter() );
		
		orinfoTab.addEventListener(Events.ON_SELECT,new  EnaAuditEventLisenter() );
		auditTab.addEventListener(Events.ON_SELECT,new  DisAuditEventLisenter() );
		rebackTab.addEventListener(Events.ON_SELECT,new  DisAuditEventLisenter() );
		writerTab.addEventListener(Events.ON_SELECT,new  EnaAuditEventLisenter() );

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
		//分页
//		loadPageing();
	}
	
	
	/*private void loadPageing() {
		
		orinPage.addEventListener("onPaging",new EventListener(){
			public void onEvent(Event event) throws Exception {
				PagingEvent pe = (PagingEvent) event;  
				int pgno = pe.getActivePage(); 
//		        redraw(etask.getKeId(),pgno, orinPage.getPageSize());  
			}
		});
		
		writerPage.addEventListener("onPaging", new EventListener(){

			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				PagingEvent pe = (PagingEvent) event;  
				int pgno = pe.getActivePage(); 
//		        redraw(etask.getKeId(),pgno, writerPage.getPageSize());  
			}
			
		});
		
		auditPage.addEventListener("onPaging", new EventListener(){

			public void onEvent(Event event) throws Exception {
				PagingEvent pe = (PagingEvent) event;  
				int pgno = pe.getActivePage(); 
//		        redraw(etask.getKeId(),pgno, auditPage.getPageSize());  
			}
			
		});
		
		rebackPage.addEventListener("onPaging", new EventListener(){

			public void onEvent(Event event) throws Exception {
				PagingEvent pe = (PagingEvent) event;  
				int pgno = pe.getActivePage(); 
//		        redraw(etask.getKeId(),pgno, rebackPage.getPageSize());  
			}
			
		});
		
	}*/
	
	/*private void redraw(long keid, int start, int pageSize) {
		
		int count=start * pageSize;
		this.start=start;
		this.jishu=count;
		List<WkTOrinfo> sList = null;
		try {
			sList = pageTemp.getListByPage(keid,start, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		List<WkTDistribute> infoList=pageTemp.getListByPage(keid, userid, start,pageSize);
		List<WkTDistribute> auditlist=pageTemp.getSSListByPage(keid, userid, start, pageSize);
		List<WkTDistribute> rebacklist=pageTemp.getTHListByPage(keid, userid, start, pageSize);
		
		orinfoListModel=new ListModelList(sList);
		orinfoListbox.setModel(orinfoListModel);
		orinfoListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,count,start,pageSize));
	
		writerListModel=new ListModelList(infoList); 
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,jishu,start,pageSize));
		
		auditListModel=new ListModelList(auditlist);
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,jishu,start,pageSize));
		
		rebackListModel=new ListModelList(rebacklist);
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,jishu,start,pageSize));
		
	}*/
	
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
		this.etask = etask;
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
		
		loadList();
	}
	//设置pub.zul界面删除按钮的响应状态״̬
    class DisDeleteEventLisenter implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 deletenews.setVisible(false);
		}		
	}
    class EnaDeleteEventLisenter  implements EventListener{
		public void onEvent(Event arg0) throws Exception {
			 deletenews.setVisible(true);
			 deletenews.setDisabled(false);
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
  		if(writerTab.isSelected())
  		{
  			Long  status=1L;
  			List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
  			searchinfo(slist,writerListbox,writerListModel);	
  		}
  		else if(auditTab.isSelected())
  		{
  			Long  status=2L;
  			List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
  			searchinfo(slist,auditListbox,auditListModel);	
  		}
  		else if(rebackTab.isSelected())
  		{
  			Long  status=4L;
  			List slist=newsService.NewsSearch(applyList,status,bt,et,flag,k,s);
  			searchinfo(slist,rebackListbox,rebackListModel);	
  		}
  		else 
  	  		{
  			List slist=newsService.OriNewsSearch(applyList,bt,et,flag,k,s);
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
//		redraw(etask.getKeId(),0,pageSize);
		
//	    List orinfoList=orinewsService.getNewsOfOrinfo(etask.getKeId());
//	    List infoList=newsService.getNewsOfChanelZG(etask.getKeId(),userid);
//	    List auditlist=newsService.getNewsOfChanelSS(etask.getKeId(),userid);
//	    List rebacklist=newsService.getNewsOfChanelTH(etask.getKeId(),userid);
	    
	    List orinfoList=orinewsService.getNewsOfOrinfoA(applyList);
	    List infoList=newsService.getNewsOfChanelZGA(applyList,userid);
	    List auditlist=newsService.getNewsOfChanelSSA(applyList,userid);
	    List rebacklist=newsService.getNewsOfChanelTHA(applyList,userid);
	   
		orinfoListModel=new ListModelList();
		orinfoListModel.addAll(orinfoList);
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		orinfoListbox.setModel(orinfoListModel);
		orinfoListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
	
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
		reloadList();
	}
	
	public void initAllWindow(List taskList){
        Long userid = Long.parseLong(user.getUserId().toString());
		List orinfoList = orinewsService.getNewsOfOrinfoA(taskList);
		List infoList = newsService.getNewsOfChanelZGA(taskList,userid);
		List auditlist = newsService.getNewsOfChanelSSA(taskList,userid);
		List rebacklist = newsService.getNewsOfChanelTHA(taskList,userid);
		
		orinfoListModel=new ListModelList();
		orinfoListModel.addAll(orinfoList);
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		if(writerListModel.size() != 0 && writerListModel != null){
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		}
		
		if(orinfoListModel.size() !=0 && orinfoListModel != null){
		orinfoListbox.setModel(orinfoListModel);
		orinfoListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		}
		
		if(auditListModel.size() !=0 && auditListModel != null){
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		}
		if(rebackListModel.size() !=0 && rebackListModel != null){
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		}
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
	
	public void loadList()
	{ 	
		userid=Long.parseLong(user.getUserId().toString());
//		redraw(etask.getKeId(),0,pageSize);
		
	    List orinfoList=orinewsService.getNewsOfOrinfo(etask.getKeId());
	    List infoList=newsService.getNewsOfChanelZG(etask.getKeId(),userid);
	    List auditlist=newsService.getNewsOfChanelSS(etask.getKeId(),userid);
	    List rebacklist=newsService.getNewsOfChanelTH(etask.getKeId(),userid);
	    
		orinfoListModel=new ListModelList();
		orinfoListModel.addAll(orinfoList);
		
		writerListModel=new ListModelList();
		writerListModel.addAll(infoList);
		
		auditListModel=new ListModelList();
		auditListModel.addAll(auditlist);
		
		rebackListModel=new ListModelList();
		rebackListModel.addAll(rebacklist);
		
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		orinfoListbox.setModel(orinfoListModel);
		orinfoListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
	}
}
	

