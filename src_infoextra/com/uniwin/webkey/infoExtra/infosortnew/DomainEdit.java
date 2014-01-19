package com.uniwin.webkey.infoExtra.infosortnew;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;

public class DomainEdit extends Window implements AfterCompose{


	Textbox name,code,issue;
	InfoDomainService infodomainService;
	WkTInfoDomain wkTInfoDomain;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
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
			
			wkTInfoDomain.setKiName(name.getValue());
			wkTInfoDomain.setKiCode(code.getValue());
			wkTInfoDomain.setKiIssue(issue.getValue());
			infodomainService.update(wkTInfoDomain);
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

	public void initWin(WkTInfoDomain domain) {
		// TODO Auto-generated method stub
		this.wkTInfoDomain=domain;
		name.setValue(domain.getKiName());
		code.setValue(domain.getKiCode());
		issue.setValue(domain.getKiIssue());
	}
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
}
