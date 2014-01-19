package com.uniwin.webkey.infoExtra.newsmanage;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;


public class NewsManageEditWindow extends Window implements AfterCompose {
	
	private static final long serialVersionUID = -2451318625439388515L;

	Map params;	
	OriNewsService	 orinewsService = (OriNewsService) SpringUtil.getBean("info_orinewsService");
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	WkTExtractask etask;
	String bt,et,k,s,flag;
	Datebox begintime,endtime;
	Textbox keys,source;
	Listitem title,content,auth,memo;
	Listbox infomanagelist;
	ListModelList infomanageListModel;
	
	//下面分页加载
	/*Paging pageListbox;
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int pageSize;
	int jishu;
	int total;
	int pgno=0;*/
	public void afterCompose() 
	{
		params=this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
		
	}
	
	
/*	private void redraw(long keid, int start, int pageSize) {
		// TODO Auto-generated method stub
		int count=start * pageSize;
		this.jishu=count;
		List sList = null;
		try {
			sList=pageTemp.getListByPage(keid,start, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		infomanageListModel=new ListModelList();
		infomanageListModel.addAll(sList);
		infomanagelist.setModel(infomanageListModel);
		infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist,jishu,start,pageSize));
		
	}*/
	

	
	
	//页面初始化
	public void initManage(final WkTExtractask etask)
	{
		this.etask = etask;
		
		//pageSize=pageListbox.getPageSize();
		//List totallist=orinewsService.getNewsOfOrinfo(etask.getKeId());
/*		pageListbox.setTotalSize(totallist.size());
		pageListbox.addEventListener("onPaging", new EventListener(){
			
			public void onEvent(Event event) throws Exception {
				PagingEvent pe = (PagingEvent) event;  
				 pgno = pe.getActivePage();
				redraw(etask.getKeId(),pgno, pageSize); 
			}

		});*/
		reloadList();
	}
	//加载信息列表
	public void reloadList()
	{ 
		
		//redraw(etask.getKeId(), pgno, pageSize);
		
		List totallist=orinewsService.getNewsOfOrinfo(etask.getKeId());
		infomanageListModel=new ListModelList();
		infomanageListModel.addAll(totallist);
		infomanagelist.setModel(infomanageListModel);
		infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist,0,0,0));
		
		
		/*List manageList=orinewsService.getNewsOfOrinfo(etask.getKeId());
		infomanageListModel=new ListModelList();
		infomanageListModel.addAll(manageList);
		infomanagelist.setModel(infomanageListModel);
		infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist));*/
	}	
	
	
	
	//查询
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
 			List slist=newsService.OriNewsSearch(etask.getKeId(),bt,et,flag,k,s);
 			searchinfo(slist,infomanagelist,infomanageListModel);	
 		
	}
	//重载搜索到的信息列表
	public void searchinfo(List slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	//批量删除
	public void onClick$deletenews() throws InterruptedException
	{

		if(infomanagelist.getSelectedItem()==null)
		{
			Messagebox.show("选择要删除的信息！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		else{	
			if(Messagebox.show("确定要删除吗？", "确认", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
			{
			Set sel=infomanagelist.getSelectedItems();
				Iterator it=sel.iterator();
				List delinfoList=new ArrayList();
				     while(it.hasNext()){
				    	 Listitem item=(Listitem)it.next();
				    	 WkTOrinfo oin=(WkTOrinfo) item.getValue();
						 delinfoList.add(orinewsService.getOriInfo(oin.getKoiId()));
				     }
				     for(int j=0;j<delinfoList.size();j++)
				     {
				    	 WkTOrinfo oinfo=(WkTOrinfo) delinfoList.get(j);
				    	 List cntlist=orinewsService.getOriInfocnt(oinfo.getKoiId());
				    	 if(cntlist.size()!=0)
				    	 {
				    	 for(int i=0;i<cntlist.size();i++)
				    	 {
				    		 WkTOrinfocnt infocnt=(WkTOrinfocnt) cntlist.get(i);
				    		 orinewsService.delete(infocnt);
				    	 }
				    	 }
					    	orinewsService.delete(oinfo);
				     }
				     infomanageListModel.removeAll(delinfoList);
				     Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				     initManage(etask);
			}
		}
	
	}
	//导出数据
	public void onClick$expert() throws InterruptedException, IOException, RowsExceededException, WriteException{
	{

		   
		if(infomanagelist.getSelectedCount()==0)
		{
			 try {
				Messagebox.show("请选择要导出的数据", "错误", Messagebox.OK,Messagebox.INFORMATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			  return;
		}else{
			Date d=new Date();
			String date = ConvertUtil.convertDateAndTimeString(d).trim();
			String fileName=date.substring(0,10)+".xls";
			   WritableWorkbook workbook;   
			   String[] title = {"序号","标题","来源","发布时间","采集时间","摘要","关键字","内容"};
		    OutputStream ops=new FileOutputStream(fileName); 
		    workbook=Workbook.createWorkbook(ops); 
		    WritableSheet sheet = workbook.createSheet("采集信息", 0);
		    jxl.write.Label  label;
		    for(int i=0;i<title.length;i++){
		    	label=new jxl.write.Label (i,0,title[i]);
		    	sheet.addCell(label);	
		    }	    
	    Set sel=infomanagelist.getSelectedItems();
	    Iterator it=sel.iterator();
	    List ilist=new ArrayList();
			     while(it.hasNext()){
			    	 Listitem item=(Listitem)it.next();
			    	 WkTOrinfo oin=(WkTOrinfo) item.getValue();
					 ilist.add(oin);
			     }
			 for(int i=0;i<ilist.size();i++){
				 WkTOrinfo oinfo= (WkTOrinfo) ilist.get(i);	
				List cntlist=orinewsService.getOriInfocnt(oinfo.getKoiId());
				String con = null;
				if(cntlist.size()!=0)
				{
					for (int j=0;j<cntlist.size();j++)
					{
						WkTOrinfocnt cnt=(WkTOrinfocnt) cntlist.get(j);
						con+=cnt.getKoiContent();
					}
				}
				jxl.write.Label l1 = new jxl.write.Label(0,i+1,i+1+""); 	
			    sheet.addCell(l1);
				jxl.write.Label l2 = new jxl.write.Label(1,i+1,oinfo.getKoiTitle()); 
				sheet.addCell(l2);
				jxl.write.Label l3 = new jxl.write.Label(2,i+1,oinfo.getKoiSource()); 
				sheet.addCell(l3);
				jxl.write.Label l4 = new jxl.write.Label(3,i+1,oinfo.getKoiPtime()); 
				sheet.addCell(l4);
				jxl.write.Label l5 = new jxl.write.Label(4,i+1,oinfo.getKoiCtime()); 
				sheet.addCell(l5);
				jxl.write.Label l6 = new jxl.write.Label(5,i+1,oinfo.getKoiMemo()); 
				sheet.addCell(l6);
				jxl.write.Label l7 = new jxl.write.Label(6,i+1,oinfo.getKoiKeys()); 
				sheet.addCell(l7);
				jxl.write.Label l8 = new jxl.write.Label(7,i+1,con); 
				sheet.addCell(l8);
			 }
			 workbook.write();
		     Filedownload.save(new File(fileName), null);
		     workbook.close();
		     }
	 }
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
		/*pageListbox.setVisible(true);
		reloadList();*/
	}
}
	

