package com.uniwin.webkey.infoExtra.subjectterm;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class NewSubjectTermSortWindow extends Window implements AfterCompose{

	private static final long serialVersionUID = 2992000267148682421L;
	private Textbox sortname;
	private SubjectTermSortListbox sortlist;
	private Row parentRow;
	private SubjectTermSortService subjectTermSortService;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWindow(WkTSubjectTermSort t){
		if(t == null){
			parentRow.setVisible(false);
		}
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
			WkTSubjectTermSort subterm = new WkTSubjectTermSort();
			subterm.setKsName(sortname.getValue());
			Listitem item=sortlist.getSelectedItem();
			if(item!=null){
				WkTSubjectTermSort t = (WkTSubjectTermSort)item.getValue();
				subterm.setKsPid(t.getKsId());
			}else{
				subterm.setKsPid(0);
			}	
			subjectTermSortService.save(subterm);
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
