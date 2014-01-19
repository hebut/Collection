package com.uniwin.webkey.infoExtra.subjectterm;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class NewSubjectTermOne extends Window implements AfterCompose {

	private static final long serialVersionUID = -6938120531743908265L;
	private Textbox name, code, issue;
	private SubjectTermService subjectTermService;
	private WkTSubjectTermSort subjectTermSort = null;
	private WkTSubjectTerm subjectTerm;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void onClick$save() {
		if (name.getValue().equals("")) {
			try {
				Messagebox.show("输入名称！");
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (code.getValue().equals("")) {
			try {
				Messagebox.show("输入代码！");
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		WkTSubjectTerm st = new WkTSubjectTerm();
		st.setKiCode(code.getValue());
		st.setKiIssue(issue.getValue());
		st.setKiName(name.getValue());
		st.setKsId(subjectTermSort.getKsId());
		if(subjectTerm == null){
			st.setKstPid(0);
		}else{
			st.setKstPid(subjectTerm.getKstId());
		}
		subjectTermService.save(st);
		try {
			Messagebox.show("保存成功！");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		refreshTree();
		this.detach();
	}

	public void onClick$back() {
		this.detach();
	}

	public void initWin(WkTSubjectTermSort subjectTermSort,
			WkTSubjectTerm subjectTerm) {
		this.subjectTermSort = subjectTermSort;
		this.subjectTerm = subjectTerm;
	}

	public void refreshTree() {
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

}
