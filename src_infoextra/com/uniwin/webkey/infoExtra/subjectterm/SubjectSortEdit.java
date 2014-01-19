package com.uniwin.webkey.infoExtra.subjectterm;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SubjectSortEdit extends Window implements AfterCompose{

	private static final long serialVersionUID = -4167039936949095470L;
	private Textbox name;
	private SubjectTermSortService subjectTermSortService;
	private WkTSubjectTermSort subjectTermSort;
	private SubjectTermSortListbox sortlist;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin(WkTSubjectTermSort subjectTermSort) {
		this.subjectTermSort=subjectTermSort;
		name.setValue(subjectTermSort.getKsName());
		if(subjectTermSort.getKsPid()==0){
			sortlist.setVisible(false);
		}
		else{
			WkTSubjectTermSort e = subjectTermSortService.getTpyeById(subjectTermSort.getKsPid());
			sortlist.initAllTaskSortSelect(e, subjectTermSort);
		}		
	}
	
	public void onSelect$sortlist() throws InterruptedException {
		Listitem item=sortlist.getSelectedItem();
		WkTSubjectTermSort task=(WkTSubjectTermSort) item.getValue();
		if(task.getKsId().toString().trim().equals(subjectTermSort.getKsId().toString().trim()))
		{
			WkTSubjectTermSort e = subjectTermSortService.getTpyeById(subjectTermSort.getKsPid());
			sortlist.initAllTaskSortSelect(e, subjectTermSort);
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
				e.printStackTrace();
			}
		}else{
			subjectTermSort.setKsName(name.getValue());
			Listitem item=sortlist.getSelectedItem();
			WkTSubjectTermSort ta = (WkTSubjectTermSort)item.getValue();
			subjectTermSort.setKsPid(ta.getKsId());
			subjectTermSortService.update(subjectTermSort);
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
