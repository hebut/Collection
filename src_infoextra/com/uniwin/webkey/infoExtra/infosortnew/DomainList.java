package com.uniwin.webkey.infoExtra.infosortnew;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class DomainList extends Window implements AfterCompose{

	
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	Center domainCen;
	Grid domainGrid;
	Radio radio;
	Rows domainRows;
	Radiogroup domainGroup;
	List<Radio> radioList=new ArrayList<Radio>();
	
	WkTInfoSort wkTInfoSort;
	Toolbarbutton downLevel,upLevel;
	List<WkTInfoDomain> dList0=null;
	List<WkTInfoDomain> dList1=null;
	List<WkTInfoDomain> dList2=null;
	
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		downLevel.addEventListener(Events.ON_CLICK, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				
				Radio radio1=domainGroup.getSelectedItem();
				if(radio1!=null)
				{
				    radio1.setSelected(false);
				}
				if(radio1==null){
					try{
						Messagebox.show("请选择分类");
					}catch(InterruptedException e){
					    e.printStackTrace();
					}
				}
				else{
					WkTInfoDomain domain0 = (WkTInfoDomain)radio1.getAttribute("radioValue");
					dList1 = infodomainService.findBySortPid(domain0.getKiId());
					dList0 = infodomainService.findByksidANDkipid(domain0.getKsId(),domain0.getKiPid());
					//dList0 = infodomainService.findBySortPid(domain0.getKiPid());
					if(dList1.size() == 0){
						try{
							Messagebox.show("本分类无子类");
							//radio1.setSelected(false);
						}catch(InterruptedException e){
						    e.printStackTrace();
						}
					}
					else{
						domainRows.getChildren().clear();
						//System.out.println(dList0.size());
						domainCen.setTitle(domain0.getKiName());
						dList2 = dList1;
						loadGrid(dList1);	
					}
					
					//domainGroup.getSelectedItem().setSelected(false);
				}
				
			}
			
		});
		
		upLevel.addEventListener(Events.ON_CLICK, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				domainRows.getChildren().clear();
				domainCen.setTitle(domainName(dList0));
				dList2 = dList0;
				loadGrid(dList0);	
				upANDdown(dList0);
			}
			
		});
		
	}
	
	public void upANDdown(List<WkTInfoDomain> up){
		
	    if(up.get(0).getKiPid()!=0){
			List<WkTInfoDomain> domainUp = infodomainService.findById(up.get(0).getKiPid());
			if(domainUp.get(0).getKiPid()!=0){
				List<WkTInfoDomain> dListup = infodomainService.findBySortPid(domainUp.get(0).getKiPid());
				dList0 = dListup;
			}
			else{
				List<WkTInfoDomain> dListup = infodomainService.findByksidANDkipid(domainUp.get(0).getKsId(), domainUp.get(0).getKiPid());
				dList0 = dListup;
			}	
	    }
	    else{
	    	dList0 = up;
	    }
	}
	
    public String domainName(List<WkTInfoDomain> list){
    	String domainName;
    	if(list.get(0).getKiPid()==0){
    		WkTInfoSort infoSort = infosortService.getTpyeById(list.get(0).getKsId());
    		domainName = infoSort.getKsName();
    	}
    	else{
    		List<WkTInfoDomain> list0 = infodomainService.findById(list.get(0).getKiPid());
    		domainName = list0.get(0).getKiName();
    	}
    	
    	return domainName;
    }
	
	
	private void loadGrid(List<WkTInfoDomain> dList){
		
		if(dList!=null && dList.size()>0){
			
			int count=(double)dList.size()/(double)2>(dList.size()/2)?dList.size()/2+1:dList.size()/2;
			if(dList.size()%2==0){
				int jshu=0;
				for(int r=0;r<count;r++){
					Row row=new Row();
					for(int c=0;c<2;c++){
						radio=new Radio();
						radio.setAttribute("radioValue", dList.get(jshu));
						radioList.add(radio);
						radio.setLabel(dList.get(jshu).getKiName());
						row.appendChild(radio);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				domainGrid.appendChild(domainRows);
			}else{
				int jshu=0;
				Row row;
				for(int r=0;r<count-1;r++){
					row=new Row();
					for(int c=0;c<2;c++){
						radio=new Radio();
						radioList.add(radio);
						radio.setAttribute("radioValue", dList.get(jshu));
						radio.setLabel(dList.get(jshu).getKiName());
						row.appendChild(radio);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				Row row2=new Row();
				Radio cbox;
				for(int left=0;left<dList.size()%2;left++){
					radio=new Radio();
					radio.setAttribute("radioValue", dList.get(jshu));
					radio.setLabel(dList.get(jshu).getKiName());
					radioList.add(radio);
					row2.appendChild(radio);
					domainRows.appendChild(row2);
					//jshu++;
				}
				domainGrid.appendChild(domainRows);
				
			}
			
		}
		
	}
	
	public void onClick$padd(){
		
		
		
		if(wkTInfoSort==null){
			try {
				Messagebox.show("请选择分类！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			
		if(wkTInfoSort.getKsPid()==0){
			
			try {
				Messagebox.show("不能添加分类！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.detach();
		}else{
			
			NewMDomain domain=(NewMDomain)Executions.createComponents("/apps/infoExtra/content/infosort/newdomain.zul", null, null);
			domain.doHighlighted();
			//domain.initWin(infoSort, pid);
			
			domain.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					domainRows.getChildren().clear();
					radioList.clear();
					//List<WkTInfoDomain> dList=infodomainService.findBySortId(wkTInfoSort.getKsId());
					//loadGrid(dList);
					refreshTree();
				}
				
			});
		}
	}
		
 }
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
//	public void onClick$add(){
//		
//		if(wkTInfoSort==null){
//			try {
//				Messagebox.show("请选择分类！");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}else{
//			
//		if(wkTInfoSort.getKsPid()==0){
//			
//			try {
//				Messagebox.show("不能添加分类！");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			this.detach();
//		}else{
//			
//			NewDomain domain=(NewDomain)Executions.createComponents("/apps/infoExtra/content/infosort/newdomain.zul", null, null);
//			domain.doHighlighted();
//			domain.initWin(wkTInfoSort);
//			
//			domain.addEventListener(Events.ON_CHANGE, new EventListener(){
//				public void onEvent(Event arg0) throws Exception {
//					domainRows.getChildren().clear();
//					List<WkTInfoDomain> dList=infodomainService.findBySortId(wkTInfoSort.getKsId());
//					radioList.clear();
//					radioList=new ArrayList<Radio>(dList.size());
//					loadGrid(dList);
//				}
//				
//			});
//		}
//	}
//		
// }
	
	public void onClick$add(){
		
		if(wkTInfoSort == null){
			try{
				Messagebox.show("请选择分类");
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		else{
			if(wkTInfoSort.getKsPid()==0){
				try{
					Messagebox.show("请选择分类");
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				this.detach();
			}
			else{
				final Integer pid;
				if(dList2==null){
					pid=0;
				}
				else{
					pid= dList2.get(0).getKiPid();
				}
				
				if(pid == 0){
					NewDomain domain=(NewDomain)Executions.createComponents("/apps/infoExtra/content/infosort/newdomain.zul", null, null);
					domain.doHighlighted();
					domain.initWin(wkTInfoSort,pid);
					
					domain.addEventListener(Events.ON_CHANGE, new EventListener(){
						public void onEvent(Event arg0) throws Exception {
							domainRows.getChildren().clear();
							List<WkTInfoDomain> dList=infodomainService.findByksidANDkipid(wkTInfoSort.getKsId(),0);
							radioList.clear();
							radioList=new ArrayList<Radio>(dList.size());
							loadGrid(dList);
						}
						
					});
				}
				else{
					NewDomain domain=(NewDomain)Executions.createComponents("/apps/infoExtra/content/infosort/newdomain.zul", null, null);
					domain.doHighlighted();
					domain.initWin(wkTInfoSort,dList2.get(0).getKiPid());
					
					domain.addEventListener(Events.ON_CHANGE, new EventListener(){
						public void onEvent(Event arg0) throws Exception {
							domainRows.getChildren().clear();
							List<WkTInfoDomain> dList=infodomainService.findByksidANDkipid(wkTInfoSort.getKsId(),pid);
							radioList.clear();
							radioList=new ArrayList<Radio>(dList.size());
							loadGrid(dList);
						}
						
					});
				}
			}
			
		}
           
	}
	
	public void onClick$edit(){
		
		
		Radio radio0 = domainGroup.getSelectedItem();
		if(radio0==null){
			try {
				Messagebox.show("请选择编辑类！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			Radio radio=domainGroup.getSelectedItem();
			WkTInfoDomain wkTInfoDomain=(WkTInfoDomain) radio.getAttribute("radioValue");
			
			DomainEdit domainEdit=(DomainEdit)Executions.createComponents("/apps/infoExtra/content/infosort/domainedit.zul", null, null);
			domainEdit.initWin(wkTInfoDomain);
			domainEdit.doHighlighted();
			
			domainEdit.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					domainRows.getChildren().clear();
					List<WkTInfoDomain> dList=infodomainService.findBySortId(wkTInfoSort.getKsId());
					radioList.clear();
					radioList=new ArrayList<Radio>(dList.size());
					loadGrid(dList);
					//radioList.clear();
//					List<WkTInfoDomain> dList=infodomainService.findBySortId(wkTInfoSort.getKsId());
//					loadGrid(dList);
					//initWindow(wkTInfoSort);
				}
				
			});
			
		}
		
	}
	
	public void onClick$deleteDomain(){
		
		if(domainGroup.getSelectedItem()==null){
			try {
				Messagebox.show("请选择删除类！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			
			try {
				if(Messagebox.show("确实要删除该分类 ?", "请确认", Messagebox.OK
						| Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK){
					
					Radio radio=domainGroup.getSelectedItem();
					WkTInfoDomain domain=(WkTInfoDomain) radio.getAttribute("radioValue");
					infodomainService.delete(domain);
					domainRows.getChildren().clear();				
				    //radioList.clear();
				//	initWindow(wkTInfoSort);
					if(dList2.get(0).getKiPid()!=0){
						List<WkTInfoDomain> dList=infodomainService.findByksidANDkipid(dList2.get(0).getKsId(), dList2.get(0).getKiPid());
						radioList.clear();
						loadGrid(dList);
					}
					else{
						List<WkTInfoDomain> dList=infodomainService.findByksidANDkipid(dList2.get(0).getKsId(),0);
						radioList.clear();
						loadGrid(dList);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void initWindow(WkTInfoSort infoSort) {
		this.wkTInfoSort=infoSort;
		List<WkTInfoDomain> dList=infodomainService.findByksidANDkipid(infoSort.getKsId(), 0);
		dList2 = dList;
		radioList.clear();
		loadGrid(dList);
	}
	

	
	

}
