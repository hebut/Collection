package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * 初始化信息审核界面列表
 * @author whm
 */
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.ui.Arraylist;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;



public class NewsauditDetailListRenderer implements ListitemRenderer {

	private OriNewsService orinewsService=(OriNewsService)SpringUtil.getBean("info_orinewsService");
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	Listbox auditListbox,pubListbox,readListbox;
	ListModelList pubListModel,readListModel,auditListModel;
	WkTExtractask etask;
	NewsauditEditWindow auditWin;
	private List applyList;
	
	public NewsauditDetailListRenderer(OriNewsService orinewsService,NewsServices newsService,
			ListModelList readListModel,ListModelList auditListModel,ListModelList pubListModel,
			Listbox auditListbox,Listbox readListbox,Listbox pubListbox,List applyList)
	{
		this.orinewsService=orinewsService;
		this.newsService=newsService;
		this.readListbox=readListbox;
		this.readListModel=readListModel;
		this.auditListbox=auditListbox;
		this.auditListModel=auditListModel;
		this.pubListbox=pubListbox;
		this.pubListModel=pubListModel;
		this.applyList = applyList;
		
	}
	
	public void render(Listitem item, Object data) throws Exception {
		if(data instanceof WkTOrinfo)
		{
			
		WkTOrinfo orinfo=(WkTOrinfo)data;
		if(getTask()==null){
			  this.etask=orinewsService.getTask(orinfo.getKeId());
			}
		 item.setValue(orinfo);
		 item.setHeight("30px");
		 Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
		 item.appendChild(c);
		 Listcell c0=new Listcell(item.getIndex()+1+"");
		 item.appendChild(c0);
		 Listcell c1=new Listcell();
		 if(orinfo.getKoiTitle().trim().length()>30){
				String str1 = "";                                                      
				str1=orinfo.getKoiTitle().trim().substring(0,30);
					c1=new Listcell(str1+"......");
			}else{
				 c1=new Listcell(orinfo.getKoiTitle());					
			}
		 c1.setTooltiptext("点击查看信息详情");
		 item.appendChild(c1);
		  c1.addEventListener(Events.ON_CLICK, new PnnerListener());
		 Listcell c2=new Listcell(orinfo.getKoiPtime());
		 item.appendChild(c2);
		 Listcell c3=new Listcell(orinfo.getKoiSource());
		 item.appendChild(c3);
		 Listcell c4=new Listcell(orinfo.getKoiCtime());
		 item.appendChild(c4);
		
		}
		else if(data instanceof WkTDistribute)
		{
			WkTDistribute dis=(WkTDistribute)data;
			WkTInfo info=newsService.getWkTInfo(dis.getKiId());
			if(getTask()==null){
				  this.etask=orinewsService.getTask(dis.getKeId());
				}
			 item.setValue(dis);
			 item.setHeight("30px");
			 Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
			 item.appendChild(c);
			 Listcell c0=new Listcell(item.getIndex()+1+"");
			 item.appendChild(c0);
			 Listcell c1=new Listcell();
			 if(info.getKiTitle().trim().length()>30){
					String str1 = "";                                                     
					str1=info.getKiTitle().trim().substring(0,30);
						c1=new Listcell(str1+"......");
				}else{
					 c1=new Listcell(info.getKiTitle());					
				}
			 c1.setTooltiptext("点击查看信息详情");
			 item.appendChild(c1);
			 if(dis.getKbStatus().toString().trim().equals("0"))
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
		     OriauditNewsEditWindow w=(OriauditNewsEditWindow)Executions.createComponents("/apps/infoExtra/content/newsaudit/orinewsdetail.zul",null, null);	
			 w.initWindow(d);
			 w.doHighlighted();
		     w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					reloadList();
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
		    NewsauditDetailWindow w=(NewsauditDetailWindow)Executions.createComponents("/apps/infoExtra/content/newsaudit/shenhenews.zul",null, null);	
			w.initWindow(d);
			w.doHighlighted();
		    w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					reloadList();
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
		     NewsUnchangeauditDetailWindow w=(NewsUnchangeauditDetailWindow)Executions.createComponents("/apps/infoExtra/content/newsaudit/unchangeauditnews.zul",null, null);	
			w.initWindow(d);
			w.doHighlighted();
		    w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					reloadList();
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
		reloadList();
		}
	}
	public WkTExtractask getTask() {
		return etask;
	}

	//重载列表
	public void reloadList() 
	
	{	 Users user=(Users)Sessions.getCurrent().getAttribute("users");
		 List auditList=newsService.getNewsOfChanelSS(applyList);
		 List readList=newsService.getNewsOfChanelYY(applyList);
		 List pubList=newsService.getNewsOfChanelFB(applyList);

		 readListModel.clear();
			auditListModel.clear();
			pubListModel.clear();
			
			auditListModel=new ListModelList();
			auditListModel.addAll(auditList);
			pubListModel=new ListModelList();
			pubListModel.addAll(pubList);
			readListModel=new ListModelList();
			readListModel.addAll(readList);
			
			readListbox.setModel(readListModel);
			readListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,readListbox,pubListbox,applyList));
			auditListbox.setModel(auditListModel);
			auditListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,readListbox,pubListbox,applyList));
			pubListbox.setModel(pubListModel);
			pubListbox.setItemRenderer(new NewsauditDetailListRenderer(orinewsService,newsService,readListModel,auditListModel,pubListModel,auditListbox,readListbox,pubListbox,applyList));		
	}
}


