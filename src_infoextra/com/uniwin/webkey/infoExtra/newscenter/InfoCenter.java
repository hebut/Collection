package com.uniwin.webkey.infoExtra.newscenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;
import com.uniwin.webkey.infoExtra.model.WkTUserIdAnddomainId;
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditTreeModel;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class InfoCenter extends Window implements AfterCompose{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ListModelList listModelList;
	Grid infoSortGrid;
	Auxhead sortHead;
	Rows domainRows;
	Label label;
	
//	List nlist;
	NewsauditTreeModel natm;
	
	Textbox keyword;
	Datebox begintime,endtime;
	Tab allTab,chanelTab,sortTab;
	Listbox choose;
	private Toolbarbutton search;
//	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	private InfoSortService infosortService=(InfoSortService) SpringUtil.getBean("infosortService");
	private InfoDomainService infodomainService=(InfoDomainService)SpringUtil.getBean("infodomainService");
	private NewsServices info_newsService=(NewsServices) SpringUtil.getBean("info_newsService");
	Listbox sortListbox;
	ListModelList modelList;
	private InfoIdAndDomainId infoIdAndDomainIdService=(InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainIdService");
	User user;
	String k,flag;
	Listitem title,content;
	int b1;
	int a;
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = FrameCommonDate.getUser();
		loadSortData();
		
		Date date = new Date();
		endtime.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		begintime.setValue(date1);
	}
	private void loadSortData() {
		List<WkTInfoSort> sortList=infosortService.findAll();
		Auxheader auxheader;
//		final WkTInfoSort infoSort;
		for(int i=0;i<sortList.size();i++){
			final WkTInfoSort infoSort=(WkTInfoSort)sortList.get(i);
			final List<WkTInfoDomain> domainList=infodomainService.findBySortId(user.getUserId(),infoSort.getKsId());
			auxheader=new Auxheader(infoSort.getKsName());
			auxheader.setStyle("color:blue;cursor:pointer");
			
			auxheader.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					
					domainRows.getChildren().clear();
					sortListbox.getItems().clear();
					if(domainList!=null && domainList.size()>0){
						int count=(double)domainList.size()/(double)10>(domainList.size()/10)?domainList.size()/10+1:domainList.size()/10;
						if(domainList.size()%10==0){
							int jshu=0;
							String name;
							for(int r=0;r<count;r++){
								Row row=new Row();
								for(int c=0;c<10;c++){
									name=domainList.get(jshu).getKiName();
									label=new Label();
									label.setTooltiptext(name);
									label.setAttribute("domain", domainList.get(jshu));
									
									if(name.length()>5){
										name=name.substring(0,3)+"...";
									}
									label.setValue(name);
//									
//									List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId((long) user.getUserId(), domainList.get(jshu).getKiId());
//									if(sortList1!=null&&sortList1.size()!=0){
										label.setStyle("cursor:pointer;text-decoration:underline;color:green");
										LabelClickListener(label,1);
//									} else{
//										label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
//										LabelClickListener(label,0);
//										label.setVisible(false);
//									}
//									LabelClickListener(label);
									row.appendChild(label);
									jshu++;
								}
								domainRows.appendChild(row);
								Row row1=new Row();
								label=new Label();
								label.setAttribute("sort", infoSort);
								label.setValue("点击更多");
								label.setStyle("cursor:pointer;text-decoration:underline;color:green");
								LabelClickListener(label);
								row1.appendChild(label);
								domainRows.appendChild(row1);
							}
							
							infoSortGrid.appendChild(domainRows);
						}else{
							int jshu=0;
							Row row;
							String name;
							for(int r=0;r<count-1;r++){
								row=new Row();
								for(int c=0;c<10;c++){
									name=domainList.get(jshu).getKiName();
									label=new Label();
									label.setAttribute("domain", domainList.get(jshu));
									label.setTooltiptext(name);
									if(name.length()>5){
										name=name.substring(0,3)+"...";
									}
									label.setValue(name);
//									
//									List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId((long) user.getUserId(), domainList.get(jshu).getKiId());
//									if(sortList1!=null&&sortList1.size()!=0){
										label.setStyle("cursor:pointer;text-decoration:underline;color:green");
										LabelClickListener(label,1);
//									} else{
//										label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
//										LabelClickListener(label,0);
//										label.setVisible(false);
//									}
									row.appendChild(label);
									jshu++;
								}
								domainRows.appendChild(row);
							}
							Row row2=new Row();
							for(int left=0;left<domainList.size()%10;left++){
								label=new Label();
								name=domainList.get(jshu).getKiName();
								label.setAttribute("domain", domainList.get(jshu));
								label.setTooltiptext(name);
								if(name.length()>5){
									name=name.substring(0,3)+"...";
								}
								label.setValue(name);
//								List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId((long) user.getUserId(), domainList.get(jshu).getKiId());
//								if(sortList1!=null&&sortList1.size()!=0){
									label.setStyle("cursor:pointer;text-decoration:underline;color:green");
									LabelClickListener(label,1);
//								} else{
//									label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
//									LabelClickListener(label,0);
//								}
								row2.appendChild(label);
								domainRows.appendChild(row2);
								jshu++;
							}
							label=new Label();
							label.setAttribute("sort", infoSort);
							label.setValue("点击更多");
							label.setStyle("cursor:pointer;text-decoration:underline;color:green");
							LabelClickListener(label);
							row2.appendChild(label);
							domainRows.appendChild(row2);
							infoSortGrid.appendChild(domainRows);
						}
						
					
					}else{
						Row row=new Row();
						label=new Label();
						label.setAttribute("sort", infoSort);
						label.setValue("点击更多");
						label.setStyle("cursor:pointer;text-decoration:underline;color:green");
						LabelClickListener(label);
						row.appendChild(label);
						domainRows.appendChild(row);
						infoSortGrid.appendChild(domainRows);
					}
				}
				
			});
			sortHead.appendChild(auxheader);
		}
		
	}

	private void LabelClickListener(final Label label,final int i){
		
		label.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				label.setStyle("cursor:pointer;text-decoration:underline;color:red");
				WkTInfoDomain domain=(WkTInfoDomain) label.getAttribute("domain");
				b1 = domain.getKiId();
				List<WkTDistribute> infoList=infoIdAndDomainIdService.findByDomainId(domain.getKiId());
				a = i;
				modelList=new ListModelList(infoList);
				sortListbox.setModel(modelList);
				sortListbox.setItemRenderer(new ListitemRenderer(){

					public void render(Listitem item, Object arg1)
							throws Exception {
						final WkTDistribute distribute=(WkTDistribute)arg1;
						item.setValue(distribute);
						Listcell c1=new Listcell(item.getIndex()+1+"");
						Listcell c2=new Listcell(distribute.getKbTitle());
						WkTInfo info=info_newsService.getWkTInfo(distribute.getKiId());
						Listcell c3=new Listcell(info.getKiSource());
						Listcell c4=new Listcell(info.getKuName());
						
						Listcell c5=new Listcell(distribute.getKbDtime());
						
						if(a==1){
						c2.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								
								WkTInfo wkTInfo=info_newsService.getWkTInfo(distribute.getKiId());
								InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/infocenter/showdetail.zul", null, null);
								infoShow.initWindow(wkTInfo);
								infoShow.doHighlighted();
								
							}
							
						});
						}
						item.appendChild(c1);item.appendChild(c2);
						item.appendChild(c3);item.appendChild(c4);item.appendChild(c5);
					}
					
				});
			}
			
		});
		
		search.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
			if(keyword.getValue()!=null && !keyword.getValue().equals("")){
				k=keyword.getValue();
				if(content.isSelected()) flag="1";
	  			else flag="2";
			}else{
				k="";
			}
			Date b=begintime.getValue();
			Date en=endtime.getValue();
			if(b==null || en==null){
				try {
					Messagebox.show("起止时间不允许为空！");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			String bt = ConvertUtil.convertDateAndTimeString(b);
			String et = ConvertUtil.convertDateAndTimeString(en);
			
			if(bt.compareTo(et)>0)
	  		 {
	  			try {
					Messagebox.show("开始时间不能大于截止时间！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	  			LabelClickListener(label,a);
	  		 }
			System.out.println(b1+"");
			List<WkTDistribute> infoList=infoIdAndDomainIdService.findByKeyword(b1,bt,et,flag,k);
			
			modelList=new ListModelList(infoList);
			sortListbox.setModel(modelList);
			sortListbox.setItemRenderer(new ListitemRenderer(){

				public void render(Listitem item, Object arg1)
						throws Exception {
					final WkTDistribute distribute=(WkTDistribute)arg1;
					item.setValue(distribute);
					Listcell c1=new Listcell(item.getIndex()+1+"");
					Listcell c2=new Listcell(distribute.getKbTitle());
					WkTInfo info=info_newsService.getWkTInfo(distribute.getKiId());
					Listcell c3=new Listcell(info.getKiSource());
					Listcell c4=new Listcell(info.getKiAuthname());
					
					Listcell c5=new Listcell(distribute.getKbDtime());
					
					if(a==1){
						c2.addEventListener(Events.ON_CLICK, new EventListener(){

							public void onEvent(Event arg0) throws Exception {
								
								WkTInfo wkTInfo=info_newsService.getWkTInfo(distribute.getKiId());
								InfoShow infoShow=(InfoShow)Executions.createComponents("/apps/infoExtra/content/infocenter/showdetail.zul", null, null);
								infoShow.initWindow(wkTInfo);
								infoShow.doHighlighted();
								
							}
							
						});
						}
					item.appendChild(c1);item.appendChild(c2);
					item.appendChild(c3);item.appendChild(c4);item.appendChild(c5);
				}
				
			});
			
			
			}});
	}
	private void LabelClickListener(final Label label){
		
		label.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				WkTInfoSort infoSort=(WkTInfoSort) label.getAttribute("sort");

				List<WkTInfoDomain> domainList1=infodomainService.findBySortId( user.getUserId(),infoSort.getKsId());
				List<WkTInfoDomain> domainList2=infodomainService.findBySortId1( user.getUserId(),infoSort.getKsId());
				final List<WkTInfoDomain> domainList = new ArrayList();
				domainList.addAll(domainList1);
				domainList.addAll(domainList2);
				
				domainRows.getChildren().clear();
				sortListbox.getItems().clear();
				if(domainList!=null && domainList.size()>0){
					int count=(double)domainList.size()/(double)10>(domainList.size()/10)?domainList.size()/10+1:domainList.size()/10;
					if(domainList.size()%10==0){
						int jshu=0;
						String name;
						for(int r=0;r<count;r++){
							Row row=new Row();
							for(int c=0;c<10;c++){
								name=domainList.get(jshu).getKiName();
								Label	label=new Label();
								label.setTooltiptext(name);
								label.setAttribute("domain", domainList.get(jshu));
								
								if(name.length()>5){
									name=name.substring(0,3)+"...";
								}
								label.setValue(name);
								
								List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId( user.getUserId(), domainList.get(jshu).getKiId());
								if(sortList1!=null&&sortList1.size()!=0){
									label.setStyle("cursor:pointer;text-decoration:underline;color:green");
									LabelClickListener(label,1);
								} else{
									label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
									LabelClickListener(label,0);
								}
								row.appendChild(label);
								jshu++;
							}
							domainRows.appendChild(row);
						}
						
						infoSortGrid.appendChild(domainRows);
					}else{
						int jshu=0;
						Row row;
						String name;
						for(int r=0;r<count-1;r++){
							row=new Row();
							for(int c=0;c<10;c++){
								name=domainList.get(jshu).getKiName();
								Label label=new Label();
								label.setAttribute("domain", domainList.get(jshu));
								label.setTooltiptext(name);
								if(name.length()>5){
									name=name.substring(0,3)+"...";
								}
								label.setValue(name);
								
								List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId( user.getUserId(), domainList.get(jshu).getKiId());
								if(sortList1!=null&&sortList1.size()!=0){
									label.setStyle("cursor:pointer;text-decoration:underline;color:green");
									LabelClickListener(label,1);
								} else{
									label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
									LabelClickListener(label,0);
								}
								row.appendChild(label);
								jshu++;
							}
							domainRows.appendChild(row);
						}
						Row row2=new Row();
						for(int left=0;left<domainList.size()%10;left++){
							Label label=new Label();
							name=domainList.get(jshu).getKiName();
							label.setAttribute("domain", domainList.get(jshu));
							label.setTooltiptext(name);
							if(name.length()>5){
								name=name.substring(0,3)+"...";
							}
							label.setValue(name);
							List<WkTUserIdAnddomainId> sortList1=infosortService.findByUserIdanddomainId(user.getUserId(), domainList.get(jshu).getKiId());
							if(sortList1!=null&&sortList1.size()!=0){
								label.setStyle("cursor:pointer;text-decoration:underline;color:green");
								LabelClickListener(label,1);
							} else{
								label.setStyle("cursor:pointer;text-decoration:underline;color:gray");
								LabelClickListener(label,0);
							}
							row2.appendChild(label);
							domainRows.appendChild(row2);
							jshu++;
						}
					
						infoSortGrid.appendChild(domainRows);
					}
					
				
				}
				
				
			}
			});
	}
}
