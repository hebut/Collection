package com.uniwin.webkey.infoExtra.task;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Html;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class Reader extends Window implements AfterCompose{


	Html text;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this,this);
	}
	public void initWindow(String txt) {
		text.setContent(txt);
		//.setValue(txt);
	}

	
}
