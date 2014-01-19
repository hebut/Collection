package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.Date;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.infoExtra.model.WKTInfoEmail;

public class InfoEmailShow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -597640144339499936L;

	private Label mailSubject,mToInner,mailDate;
	private Html emailHTML;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);

	}
	
	public void initWindow(WKTInfoEmail infoEmail){
		mailSubject.setValue(infoEmail.getSubject());
		mToInner.setValue(infoEmail.getMailto());
		String time = DateUtil.dateToOriStr(new Date(infoEmail.getTime()));
		mailDate.setValue(time);
		emailHTML.setContent(infoEmail.getContent());
		
	}

}
