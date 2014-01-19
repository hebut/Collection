package com.uniwin.webkey.infoExtra.infosortnew;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class SortEdit extends Window implements AfterCompose{

	
	Textbox name;
	InfoSortService infosortService;
	WkTInfoSort wkTInfoSort;
	SortListbox sortlist;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin(WkTInfoSort wkTInfoSort) {
		
		this.wkTInfoSort=wkTInfoSort;
		name.setValue(wkTInfoSort.getKsName());
		if(wkTInfoSort.getKsPid()==0){
			sortlist.setVisible(false);
		}
		else{
			WkTInfoSort e=infosortService.getTpyeById(wkTInfoSort.getKsPid());
			sortlist.initAllTaskSortSelect(e,wkTInfoSort);
		}		
	}

	
	public void onSelect$sortlist() throws InterruptedException
	{
		Listitem item=sortlist.getSelectedItem();
		WkTInfoSort task=(WkTInfoSort) item.getValue();
		if(task.getKsId().toString().trim().equals(wkTInfoSort.getKsId().toString().trim()))
		{
			WkTInfoSort e=infosortService.getTpyeById(wkTInfoSort.getKsPid());
			sortlist.initAllTaskSortSelect(e,wkTInfoSort);
		}
	}
	
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
	public void onClick$save(){
		
		if(name.getValue().equals("")){
			try {
				Messagebox.show("请输入分类名称！");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			
			wkTInfoSort.setKsName(name.getValue());
			
			Listitem item=sortlist.getSelectedItem();
			WkTInfoSort ta=(WkTInfoSort)item.getValue();
			wkTInfoSort.setKsPid(ta.getKsId());
			
			infosortService.update(wkTInfoSort);
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
	
}
