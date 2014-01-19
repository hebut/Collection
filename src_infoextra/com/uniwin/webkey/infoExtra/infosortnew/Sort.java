package com.uniwin.webkey.infoExtra.infosortnew;


import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class Sort extends Window implements AfterCompose{

	
	Tree sortTree;
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	TreeData data;
	int i=0;
	
	Grid domainGrid;
	Radio radio;
	Rows domainRows;
	Textbox sort;
	
	List<Radio> radioList=new ArrayList<Radio>();
	List<Radio> selectList=new ArrayList<Radio>();
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();
		sortTree.addEventListener(Events.ON_SELECT, new EventListener()
		{
			public void onEvent(Event arg0) throws Exception {
				
				if(sortTree.getSelectedItem()!=null){
					
					WkTInfoSort infoSort=(WkTInfoSort)sortTree.getSelectedItem().getValue();
					List<WkTInfoDomain> domList=infodomainService.findBySortId(infoSort.getKsId());
					radioList.clear();
					domainRows.getChildren().clear();
					loadGrid(domList);
				}
			}

			
		});
		
	}

	
	private void loadSelectRadio(List<Radio> list) {
		// TODO Auto-generated method stub
		Radio radio;
		for(int i=0;i<list.size();i++){
			radio=(Radio)list.get(i);
			radio.setSelected(true);
		}
		
	}
	
	private void initTree() {
		
		List list=infosortService.findByPid(0);
		data=new TreeData(list,infosortService);
		
		sortTree.setModel(data);
		sortTree.setTreeitemRenderer(new TreeitemRenderer(){
			public void render(Treeitem item, Object obj) throws Exception {
				
				final WkTInfoSort w=(WkTInfoSort)obj;
				item.setValue(w);
				item.setLabel(w.getKsName());
				
				item.setOpen(true);
				i++;
				if(i==1){
					item.setSelected(true);
				}

			}
			
		});
		
		WkTInfoSort sort=(WkTInfoSort) sortTree.getSelectedItem().getValue();
		List<WkTInfoDomain> dList=infodomainService.findBySortId(sort.getKsId());
		loadGrid(dList);
		
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
						radio.setLabel(dList.get(jshu).getKiName());
						row.appendChild(radio);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				Row row2=new Row();
				Radio cbox;
				for(int left=0;left<dList.size()%2;left++){
					cbox=new Radio(dList.get(jshu).getKiName());
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
		
		sort.setValue("sss");
		this.detach();
	}
	
	public void onClick$back(){
		this.detach();
	}


	public void initWin(Textbox sort) {
		// TODO Auto-generated method stub
		this.sort=sort;
	}
}
