package com.uniwin.webkey.infoExtra.infosort;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class NewDomain extends Window implements AfterCompose{

	
	Textbox name,code,issue;
	InfoDomainService infodomainService;
	WkTInfoSort infoSort;
	Integer pid;
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void onClick$save(){
		
		if(name.getValue().equals("")){
			try {
				Messagebox.show("输入名称！");
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 if(code.getValue().equals("")){
			try {
				Messagebox.show("输入代码！");
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
			WkTInfoDomain domain=new WkTInfoDomain();
			domain.setKiName(name.getValue());
			domain.setKiCode(code.getValue());
			domain.setKiIssue(issue.getValue());
			domain.setKsId(infoSort.getKsId());
			domain.setKiPid(pid);
			infodomainService.save(domain);
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

//	public void initWin(WkTInfoSort infoSort) {
//		this.infoSort=infoSort;
//	}
	
	public void initWin(WkTInfoSort infoSort,Integer pid){
		this.infoSort = infoSort;
		this.pid = pid;
	}
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
}
