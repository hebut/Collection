package com.uniwin.webkey.infoExtra.newsmanage;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;




public class NewsManageListRenderer implements ListitemRenderer {
	
	
	
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("orinewsService");
	WkTExtractask etask;
	Listbox infomanagelist;
	ListModelList infomanageListModel;
	
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int count,start,pageSize,RowNum;
	
	public NewsManageListRenderer(OriNewsService orinewsService,ListModelList infomanageListModel,
			Listbox infomanagelist, int jishu, int start, int pageSize)
	{
		this.orinewsService=orinewsService;
		this.infomanageListModel=infomanageListModel;
		this.infomanagelist=infomanagelist;
		this.count=jishu;
		this.start=start;
		this.pageSize=pageSize;
	}
	
	public void render(Listitem item, Object data) throws Exception {
		
	 WkTOrinfo u=(WkTOrinfo)data;	
	 
	 if(etask==null){
		  this.etask=orinewsService.getTask(u.getKeId());
		}
	 
	 item.setValue(u);
	 item.setHeight("30px");
	 Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
	 item.appendChild(c);
	// RowNum = zxPaging.getActivePage() * zxPaging.getPageSize() + arg0.getIndex() + 1;
	 Listcell c0=new Listcell(count+item.getIndex()+1+"");
	// Listcell c0=new Listcell(count+1+"");
	// count++;
	 
	 item.appendChild(c0);
	 if(u.getKoiStatus().toString().trim().equals("0"))
	 {
		 Listcell c1=new Listcell();
		 c1.setImage("/images/content/ims.news.gif");		
	     item.appendChild(c1);
	 }
	 else
	 {
		 Listcell c1=new Listcell();
		 c1.setImage("/images/content/ims.readed.gif");
	     item.appendChild(c1);
	 }
	 Listcell c2=new Listcell();
	     if(u.getKoiTitle()!=""&&u.getKoiTitle()!=null)
			{
		 if(u.getKoiTitle().length()>20){
				String str1 = "";                                                      
				str1=u.getKoiTitle().substring(0,20);
					c2=new Listcell(str1+"......");
			}else{
				 c2=new Listcell(u.getKoiTitle());					
			}
			}
	     c2.setTooltiptext("点击查看信息详情");
	     item.appendChild(c2);
	 if(u.getKoiPtime()!=""&&u.getKoiPtime()!=null)
	 {
		 String t=u.getKoiPtime();
		 Listcell c3;
		 if(t.length()>12){
			  c3=new Listcell(t.substring(0,12)+"...");
		 }else{
			  c3=new Listcell(u.getKoiPtime());
		 }
		 c3.setTooltiptext(t);
		 item.appendChild(c3);
	 }
	 else
	 {
		 Listcell c3=new Listcell(""); 
		 item.appendChild(c3);
	 }
	 if(u.getKoiSource()!=""&&u.getKoiSource()!=null)
	 {
		 Listcell c4;
		 String s=u.getKoiSource();
		 if(s.length()>10){
			 c4=new Listcell(s.substring(0,10)+"...");
		 }else{
			 c4=new Listcell(s);
		 }
		 c4.setTooltiptext(s);
		 item.appendChild(c4);
	 }
	 else 
	 {Listcell c4=new Listcell("未知"); 
	 item.appendChild(c4);
	 }
	 Listcell c5=new Listcell(u.getKoiCtime());
	 item.appendChild(c5);	
	 c2.addEventListener(Events.ON_CLICK, new InnerListener());}
	
	
	//点击信息标题查看详情事件
	class InnerListener implements EventListener
	{
		
		public void onEvent(Event event) throws Exception 
		{
			Listitem c=(Listitem)event.getTarget().getParent();
			final WkTOrinfo d=(WkTOrinfo)c.getValue();
			d.setKoiStatus(Long.parseLong("1"));
			orinewsService.update(d);
			reloadList(d.getKeId());
			NewsConEditWindow w=(NewsConEditWindow)Executions.createComponents("/apps/infoExtra/content/newsmanage/newscon.zul",null, null);	
			w.initWindow(d);
			w.doHighlighted();
		    w.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					reloadList(d.getKeId());
				}
			});
		}
		}
	//加载数据
	public void reloadList(Long keid)
	{ 
//		List auditcommentsList=orinewsService.getNewsOfOrinfo(keid);
	//	count=count;
		/*List auditcommentsList = null;
		try {
			auditcommentsList = pageTemp.getListByPage(keid,start, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		};
		infomanageListModel=new ListModelList();
		infomanageListModel.addAll(auditcommentsList);
		infomanagelist.setModel(infomanageListModel);
		infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist,count,start,pageSize));*/
		
		List totallist=orinewsService.getNewsOfOrinfo(etask.getKeId());
		infomanageListModel=new ListModelList();
		infomanageListModel.addAll(totallist);
		infomanagelist.setModel(infomanageListModel);
		infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist,0,0,0));
	}	
}