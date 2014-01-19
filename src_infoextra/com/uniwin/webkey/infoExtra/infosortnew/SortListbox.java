package com.uniwin.webkey.infoExtra.infosortnew;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class SortListbox extends Listbox implements AfterCompose{
	
	InfoSortService infosortService;
	ListModelList wmodelList;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
	}
	
	
	public void addTaskListBoxItem(ListModelList cml,Integer pid,int dep,WkTInfoSort tt){
	  	List wList;
	  	if(tt==null){
	  		wList=infosortService.findByPid(pid);		
	  	}else{
	  		wList=infosortService.getChildSorttype(pid,tt.getKsId());
	  	}    	
			for(int i=0;i<wList.size();i++){		
				WkTInfoSort w=(WkTInfoSort)wList.get(i);
				w.setDep(dep);
				cml.add(w);
				addTaskListBoxItem(cml,w.getKsId(),dep+1,tt);
			}
	  }
  public void initAllTaskSortSelect(final WkTInfoSort arg,final WkTInfoSort a){
	  	wmodelList=new ListModelList();
	  	WkTInfoSort t=new WkTInfoSort();
	  	t.setDep(0);
		 final List wlist= infosortService.findAllS();
		  if(wlist==null||wlist.size()==0){
			 addTaskListBoxItem(wmodelList,0,0,arg);
		  }else {
			  addTaskListBoxItem(wmodelList,0,0,null);
		  }
			this.setModel(wmodelList);
			this.setItemRenderer(new ListitemRenderer(){
		        public void render(Listitem item, Object data) throws Exception {
		        	WkTInfoSort w=(WkTInfoSort)data;		         
		        	   item.setValue(w);
			        	int dep=w.getDep();
						String bla="";
						while(dep>0){
						    bla+="　";
							dep--;
						}
						if(arg!=null&&w.getKsId().intValue()==arg.getKsId().intValue()){
							item.setSelected(true);
						}
						if(a!=null&&w.getKsId().intValue()==a.getKsId().intValue())
						{
							 item.setStyle("color:#e0e0e0");
						}
						item.setLabel(bla+w.getKsName());
		        }
				});
	  }
	
}
