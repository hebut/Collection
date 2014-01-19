package com.uniwin.webkey.infoExtra.tasktemp;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class Reader extends Window implements AfterCompose{


	Textbox text;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this,this);
	}
	public void initWindow(String txt) {
		text.setValue(txt);
	}

	
}
