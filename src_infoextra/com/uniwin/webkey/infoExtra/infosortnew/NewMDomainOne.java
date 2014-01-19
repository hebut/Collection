package com.uniwin.webkey.infoExtra.infosortnew;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class NewMDomainOne extends Window implements AfterCompose{

	
	Textbox name,code,issue;
	InfoDomainService infodomainService;
	WkTInfoSort infoSort;
	WkTInfoDomain infoDomain;
	
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
	        String[] name1 = name.getValue().split(";");
	        String[] code1 = code.getValue().split(";");
	        String issu = issue.getValue();
	        
			if(name1.length != code1.length){
				try {
						Messagebox.show("输入的分类名称和分类代码个数不一致");
						return;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	        for(int i = 0; i<name1.length;i++){
	        	String name2 = name1[i];
	        	String code2 = code1[i];
				WkTInfoDomain domain=new WkTInfoDomain();
				domain.setKiName(name2);
				domain.setKiCode(code2);
				domain.setKiIssue(issu);
				domain.setKsId(infoSort.getKsId());
				if(infoDomain!=null){
					domain.setKiPid(infoDomain.getKiId());
				}
				else{
					domain.setKiPid(0);
				}
				infodomainService.save(domain);
	        	
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

	public void initWin(WkTInfoSort infoSort,WkTInfoDomain infoDomain) {
		this.infoSort=infoSort;
		this.infoDomain = infoDomain;
	}
	
	public void refreshTree(){
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
	
}
