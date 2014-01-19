package com.uniwin.webkey.infoExtra.infosort;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class NewSort extends Window implements AfterCompose{

	Textbox sortname;
	InfoSortService infosortService;
	SortListbox sortlist;
	
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	protected void initWindow(WkTInfoSort t){
		
		sortlist.initAllTaskSortSelect(t,null);
	}

	public void onClick$save(){
		
		if(sortname.getValue().equals("")){
			try {
				Messagebox.show("请输入分类名称！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			
			WkTInfoSort wkTInfoSort=new WkTInfoSort();
			wkTInfoSort.setKsName(sortname.getValue());
			Listitem item=sortlist.getSelectedItem();
			if(item!=null)
			{
				WkTInfoSort t=(WkTInfoSort) item.getValue();
				wkTInfoSort.setKsPid(t.getKsId());
			}
			else 
				wkTInfoSort.setKsPid(1);
			
			    infosortService.save(wkTInfoSort);
			try {
				Messagebox.show("保存成功！", "Information", Messagebox.OK,
						Messagebox.INFORMATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			refreshTree();
			this.detach();
		}
		
	}
	
	public void onClick$back(){
		this.detach();
	}
	
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
	
}
