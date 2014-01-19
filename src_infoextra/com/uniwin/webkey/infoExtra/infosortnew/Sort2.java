package com.uniwin.webkey.infoExtra.infosortnew;


import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class Sort2 extends Window implements AfterCompose{

	
	Hbox sortHbox;
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	List<WkTInfoSort> list;
	ListModelList listModelList;
	Tabs tabs;
	Tabpanels panel;
	Textbox sort;
	Checkbox checkbox;
	List<Checkbox> radioList=new ArrayList<Checkbox>();
	
	List<WkTInfoDomain> list2;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initWin();
	}

	
	private void initWin() {
		
		list=infosortService.findAll();
		WkTInfoSort sort,sort2;
		for(int i=0;i<list.size();i++){
			sort=(WkTInfoSort)list.get(i);
			List dList=infodomainService.findBySortId(sort.getKsId());
			if(dList==null || dList.size()==0){
				list.remove(i);
			}
		}

		
		for(int j=0;j<list.size();j++){
			
			sort2=(WkTInfoSort)list.get(j);
			
			Tab tab=new Tab();
//			tab.setAttribute("sort", sort2);
			tab.setLabel(sort2.getKsName());
			tab.setTooltiptext(sort2.getKsName());
			tabs.appendChild(tab);
			
			Tabpanel tabpanel=new Tabpanel();
			//Radiogroup radiogroup=new Radiogroup();
			Grid grid=new Grid();
			grid.setMold("paging");
			grid.setPageSize(8);
			grid.setFixedLayout(true);
			grid.setSclass("new_listbox");
			
			Rows rows=new Rows();
			grid.appendChild(rows);
			List dList=infodomainService.findBySortId(sort2.getKsId());
			
			loadGrid(dList,rows,grid);
			//radiogroup.appendChild(grid);
			tabpanel.appendChild(grid);
			panel.appendChild(tabpanel);
			
		}
	}

	
	private void loadGrid(List<WkTInfoDomain> dList,Rows domainRows,Grid domainGrid){
		int jshu=0;;
		if(dList!=null && dList.size()>0){
			
			int count=(double)dList.size()/(double)5>(dList.size()/5)?dList.size()/5+1:dList.size()/5;
			
			if(dList.size()%5==0){
				for(int r=0;r<count;r++){
					Row row=new Row();
					for(int c=0;c<5;c++){
						checkbox=new Checkbox();
						checkbox.setAttribute("domain", dList.get(jshu));
						checkbox.setLabel(dList.get(jshu).getKiName());
						radioList.add(checkbox);
						row.appendChild(checkbox);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				domainGrid.appendChild(domainRows);
			}else{
				
				Row row;
				for(int r=0;r<count-1;r++){
					row=new Row();
					for(int c=0;c<5;c++){
						checkbox=new Checkbox();
						checkbox.setAttribute("domain", dList.get(jshu));
						checkbox.setLabel(dList.get(jshu).getKiName());
						radioList.add(checkbox);
						row.appendChild(checkbox);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				Row row2=new Row();
				Checkbox cbox;
				for(int left=0;left<dList.size()%5;left++){
					cbox=new Checkbox(dList.get(jshu).getKiName());
					cbox.setAttribute("domain", dList.get(jshu));
					radioList.add(cbox);
					row2.appendChild(cbox);
					domainRows.appendChild(row2);
					jshu++;
				}
				domainGrid.appendChild(domainRows);
			}
			
		}
		
	}
	
	public void onClick$save(){
	
		StringBuffer sb = new StringBuffer();
		list2=new ArrayList<WkTInfoDomain>();
		for(int i=0;i<radioList.size();i++)
		{
			Checkbox c=radioList.get(i);
			if(c.isChecked())
			{
				String	selectValue=c.getLabel();
				sb.append(selectValue+";");
				WkTInfoDomain domain=(WkTInfoDomain) c.getAttribute("domain");
				list2.add(domain);
			}
		}

		if(sb!=null && !sb.toString().equals("")){
			sort.setValue(sb.toString());
			Events.postEvent(Events.ON_CHANGE, this, list2);
		}
		this.detach();
	}
	
	public void initWin(Textbox sort) {
		// TODO Auto-generated method stub
		this.sort=sort;
	}
	public void onClick$back()
	{
		Events.postEvent(Events.ON_CHANGING, this, null);
		   this.detach();	
	}
	
	
}
