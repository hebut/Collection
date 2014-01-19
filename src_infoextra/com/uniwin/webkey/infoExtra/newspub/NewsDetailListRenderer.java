package com.uniwin.webkey.infoExtra.newspub;
/**
 * 初始化信息发布界面列表
 * 2010-3-18
 * @author whm
 */

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;

import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;

public class NewsDetailListRenderer implements ListitemRenderer {
	
	WkTDistribute dis;
	WkTInfo info;
	WkTInfocnt infocnt;
	Users user;
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("orinewsService");
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	ListModelList orinfoListModel,writerListModel,rebackListModel,auditListModel; 
	Listbox orinfoListbox,writerListbox,auditListbox,rebackListbox;
	Tab pubTab,auditTab,readTab;
	
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int count,start,pageSize,jishu;
	Long userid;
	private List applyList;
	
	public NewsDetailListRenderer(OriNewsService orinewsService,NewsServices newsService,ListModelList orinfoListModel,ListModelList writerListModel,
			ListModelList rebackListModel ,ListModelList auditListModel,Listbox orinfoListbox,Listbox writerListbox,Listbox auditListbox,Listbox rebackListboxt,List applyList/*,int count,int start, int pageSize*/)
	{
		this.orinewsService=orinewsService;
		this.newsService=newsService;
		this.orinfoListbox=orinfoListbox;
		this.orinfoListModel=orinfoListModel;
		this.writerListModel=writerListModel;
		this.writerListbox=writerListbox;
	    this.auditListbox=auditListbox;
	    this.auditListModel=auditListModel;
	    this.rebackListModel=rebackListModel;
	    this.rebackListbox=rebackListbox;
	    this.applyList=applyList;
//	    this.count=count;
//	    this.start=start;
//	    this.pageSize=pageSize;
	}
	public void render(Listitem item, Object data) throws Exception 
	{
		if(data instanceof WkTOrinfo)
		{
		 WkTOrinfo orinfo=(WkTOrinfo)data;
		 item.setValue(orinfo);
		 item.setHeight("30px");
		 Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
		 item.appendChild(c);
		 Listcell c0=new Listcell(item.getIndex()+1+"");
//		 Listcell c0=new Listcell(count+1+"");
//		 count++;
		 
		 item.appendChild(c0);
		 Listcell c1 = new Listcell("");
		 if(orinfo.getKoiTitle()!=""&&orinfo.getKoiTitle()!=null){
			 
			if(orinfo.getKoiTitle().trim().length()>25){
				String str1 = "";                                                      //消息内容长度小于20字符，全部显示，否则截取前20字符显示
				str1=orinfo.getKoiTitle().trim().substring(0,25);
					c1=new Listcell(str1+"......");
			}else{
				 c1=new Listcell(orinfo.getKoiTitle());					
			}
		 	c1.setTooltiptext("点击查看信息详情");
		}
		 
		  item.appendChild(c1);
		  c1.addEventListener(Events.ON_CLICK, new PnnerListener());
		  if(orinfo.getKoiPtime()!=""&&orinfo.getKoiPtime()!=null)
			{
			  String t=orinfo.getKoiPtime();
			  Listcell c2;
			  if(t.trim().length()>12){
				   c2=new Listcell(t.substring(0,12)+"...");
				   c2.setTooltiptext(t);
			  }else{
				   c2=new Listcell(orinfo.getKoiPtime());
			  }
			  item.appendChild(c2);
			}
		  else
		  {
			  Listcell c2=new Listcell("");
				 item.appendChild(c2);
		  }
		  if(orinfo.getKoiSource()!=""&&orinfo.getKoiSource()!=null)
			{
			  Listcell c3;
			  String s=orinfo.getKoiSource();
			  if(s.length()>10){
				  c3=new Listcell(s.substring(0,10)+"...");
				  c3.setTooltiptext(s);
			  }else{
				  c3=new Listcell(orinfo.getKoiSource());
			  }
			  item.appendChild(c3);
			}
		  else
		  {
			  Listcell c3=new Listcell("未知");
				 item.appendChild(c3);
		  }
		 Listcell c4=new Listcell(orinfo.getKoiCtime());
		 item.appendChild(c4);
		
		}
		else if(data instanceof WkTDistribute)
		{
			WkTDistribute dis=(WkTDistribute)data;
			WkTInfo info=newsService.getWkTInfo(dis.getKiId());
			 item.setValue(dis);
			 item.setHeight("30px");
			 Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
			 item.appendChild(c);
//			 Listcell c0=new Listcell(item.getIndex()+1+"");
			 Listcell c0=new Listcell(count+1+"");
			 count++;
			 
			 item.appendChild(c0);
			 Listcell c1=new Listcell();
			 if(info.getKiTitle().trim().length()>25){
					String str1 = "";                                                     
					str1=info.getKiTitle().trim().substring(0,25);
						c1=new Listcell(str1+"......");
				}else{
					 c1=new Listcell(info.getKiTitle());					
				}
			 c1.setTooltiptext("点击查看信息详情");
			 item.appendChild(c1);
			 if(dis.getKbStatus().toString().trim().equals("0")||dis.getKbStatus().toString().trim().equals("2")||dis.getKbStatus().toString().trim().equals("3"))
			 {
			  c1.addEventListener(Events.ON_CLICK, new LointListener1());
			 }
			 else
			 {
				 c1.addEventListener(Events.ON_CLICK, new LointListener());
			 }
			
			 Listcell c2=new Listcell(dis.getKbDtime());
			 item.appendChild(c2);
			
			 Listcell c3=new Listcell(info.getKiSource());
			 item.appendChild(c3);
			 Listcell c4=new Listcell(info.getKuName());
			 item.appendChild(c4);
			 Listcell c5=new Listcell(info.getKiHits().toString());
			 item.appendChild(c5);
			 if(info.getKiTop().trim().equals("0"))
			 {
			 Listcell c6=new Listcell("置顶");
			 c6.setTooltiptext("点击设置！");
			
			 c6.addEventListener(Events.ON_CLICK, new PointListener());
			 }
			 else if(info.getKiTop().trim().equals("1"))
			 {
				 Listcell c6=new Listcell("取消置顶");
				 c6.setTooltiptext("点击设置！");
				
				 c6.addEventListener(Events.ON_CLICK, new PointListener());
			 }
	    }

	}
	
	//点击信息标题查看详情事件
	class PnnerListener implements EventListener
	{
		public void onEvent(Event event) throws Exception 
		{
			Listitem c=(Listitem)event.getTarget().getParent();
		    final WkTOrinfo d=(WkTOrinfo)c.getValue();
		    OriNewsEditWindow w=(OriNewsEditWindow)Executions.createComponents("/apps/infoExtra/content/newspub/orinewsdetail.zul",null, null);	
			w.initWindow(d);
			w.doHighlighted();
		    w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
//					reloadList(d.getKeId());
					initAllWindow(applyList);
				}
			});
		}
}
	
	//点击信息标题查看详情事件
	class LointListener implements EventListener
	{
		public void onEvent(Event event) throws Exception 
		{
			 Listitem c=(Listitem)event.getTarget().getParent();
		     final WkTDistribute d=(WkTDistribute)c.getValue();
		 	 Executions.getCurrent().setAttribute("kiTitle", dis);
		     NewsDetailWindow w=(NewsDetailWindow)Executions.createComponents("/apps/infoExtra/content/newspub/newsdetail.zul",null, null);	
		     w.doHighlighted();
		     w.initWindow(d);
		     w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
//					reloadList(d.getKeId());
					initAllWindow(applyList);
				}
			});
		}
}
	
	//点击信息标题查看详情事件
	class LointListener1 implements EventListener
	{
		public void onEvent(Event event) throws Exception 
		{
			Listitem c=(Listitem)event.getTarget().getParent();
		    final WkTDistribute d=(WkTDistribute)c.getValue();
		    NewsUnchangedDetailWindow w=(NewsUnchangedDetailWindow)Executions.createComponents("/apps/infoExtra/content/newspub/unchangednews.zul",null, null);	
			w.initWindow(d);
			w.doHighlighted();
		    w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
//					reloadList(d.getKeId());
					initAllWindow(applyList);
				}
			});
		}
}
	class PointListener implements EventListener
	{  
		public void onEvent(Event event) throws Exception 
		{
			
		 Listitem c=( Listitem )event.getTarget().getParent();
		 WkTDistribute dis=(WkTDistribute)c.getValue();
		 WkTInfo info=newsService.getWkTInfo(dis.getKiId());
		 if(info.getKiTop().trim().equals("0"))
		 {
			info.setKiTop("1");
			 newsService.update(info);
		 }
		 else if(info.getKiTop().trim().equals("1"))
		 {
			 info.setKiTop("0");
			 newsService.update(info);
		 }
//		 reloadList(dis.getKeId());
		 initAllWindow(applyList);
		}
	}
//重载列表

	public void reloadList(Long keid)
	{ 
	
	user = (Users) Sessions.getCurrent().getAttribute("users");
	Long  userid=Long.parseLong(user.getUserId() + "");
	this.userid=userid;
//	redraw(keid,start,pageSize);
	
    List orinfoList=orinewsService.getNewsOfOrinfo(keid);
    List infoList=newsService.getNewsOfChanelZG(keid,userid);
    List auditlist=newsService.getNewsOfChanelSS(keid,userid);
//    List publist=newsService.getNewsOfChanelFB(keid,userid);
    List rebacklist=newsService.getNewsOfChanelTH(keid,userid);
//    List readlist=newsService.getNewsOfChanelYY(keid,userid);
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
	

	private void redraw(Long keid, int i, int pageSize2) {
	// TODO Auto-generated method stub
		int count=start * pageSize;
		this.jishu=count;
		List<WkTOrinfo> sList = null;
		try {
			sList = pageTemp.getListByPage(keid,start, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
//		orinfoListModel.clear();
//		writerListModel.clear();
//		auditListModel.clear();
		List<WkTDistribute> infoList=pageTemp.getListByPage(keid, userid, start,pageSize);
		List<WkTDistribute> auditlist=pageTemp.getSSListByPage(keid, userid, start, pageSize);
		List<WkTDistribute> rebacklist=pageTemp.getTHListByPage(keid, userid, start, pageSize);
		
		orinfoListModel=new ListModelList(sList);
		orinfoListbox.setModel(orinfoListModel);
		orinfoListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
	
		writerListModel=new ListModelList(infoList); 
		writerListbox.setModel(writerListModel);
		writerListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		auditListModel=new ListModelList(auditlist);
		auditListbox.setModel(auditListModel);
		auditListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
		
		rebackListModel=new ListModelList(rebacklist);
		rebackListbox.setModel(rebackListModel);
		rebackListbox.setItemRenderer(new NewsDetailListRenderer(orinewsService,newsService,orinfoListModel,writerListModel,auditListModel,rebackListModel,
				orinfoListbox,writerListbox, auditListbox,rebackListbox,applyList));
	}

	public void initAllWindow(List taskList){
		user = (Users) Sessions.getCurrent().getAttribute("users");
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
	
}

