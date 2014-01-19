package com.uniwin.webkey.infoExtra.subjectterm;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;

public class SubjectTermEdit extends Window implements AfterCompose{

	private static final long serialVersionUID = -5624361563834128525L;
	private Textbox name,code,issue;
	private WkTSubjectTerm subjectTerm = new WkTSubjectTerm();
	
	private SubjectTermService subjectTermService;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void onClick$save(){
		
		if(name.getValue().equals("")||code.getValue().equals("")){
			try {
				Messagebox.show("将信息填充完整！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			subjectTerm.setKiName(name.getValue());
			subjectTerm.setKiCode(code.getValue());
			subjectTerm.setKiIssue(issue.getValue());
			subjectTermService.update(subjectTerm);
			try {
				Messagebox.show("更新成功！");
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

	public void initWin(WkTSubjectTerm subjectTerm) {
		this.subjectTerm=subjectTerm;
		name.setValue(subjectTerm.getKiName());
		code.setValue(subjectTerm.getKiCode());
		issue.setValue(subjectTerm.getKiIssue());
	}
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
}
