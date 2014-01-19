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

public class NewPSubjectTerm extends Window implements AfterCompose{

	private static final long serialVersionUID = 4316584878081309067L;
	private Textbox name,code,issue;
	private SubjectTermService subjectTermService;
	private WkTSubjectTermSort subjectTermSort;
	@SuppressWarnings("unused")
	private WkTSubjectTerm subjectTerm = null;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}
    
	public void onClick$save(){
		if(name.getValue().equals("")){
			try {
				Messagebox.show("输入分类名称！");
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		 if(code.getValue().equals("")){
			try {
				Messagebox.show("输入分类代码！");
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	        String[] name1 = name.getValue().split("；");
	        String[] code1 = code.getValue().split("；");
	        String issu = issue.getValue();
			if(name1.length != code1.length){
				try {
						Messagebox.show("输入的分类名称和分类代码个数不一致");
						return;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
	        for(int i = 0; i<name1.length;i++){
	        	String name2 = name1[i];
	        	String code2 = code1[i];
	        	WkTSubjectTerm st = new WkTSubjectTerm();
	        	st.setKiName(name2);
	        	st.setKiCode(code2);
	        	st.setKiIssue(issu);
	        	st.setKsId(subjectTermSort.getKsId());
	        	if(subjectTerm == null){
	    			st.setKstPid(0);
	    		}else{
	    			st.setKstPid(subjectTerm.getKstId());
	    		}
				subjectTermService.save(st);
	        }  
			try {
				Messagebox.show("保存成功！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			refreshTree();
			this.detach();
	}
	public void onClick$back(){
		this.detach();
	}

	public void initWin(WkTSubjectTermSort subjectTermSort, WkTSubjectTerm subjectTerm) {
		this.subjectTermSort = subjectTermSort;
		this.subjectTerm = subjectTerm;
	}
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
}
