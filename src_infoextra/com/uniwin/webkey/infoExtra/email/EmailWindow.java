package com.uniwin.webkey.infoExtra.email;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class EmailWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Treeitem emailset,emails,emailsed,emailr;
	private Center emailCen;
	
	public void afterCompose() {
		Components.wireController(this, this);
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	
  }
	public void onClick$emailset()
	{
		emailCen.getChildren().clear();
		AllInfoWindow afw = (AllInfoWindow)Executions.createComponents("/apps/infoExtra/content/email/conf/index.zul", null, null);
		emailCen.appendChild(afw);
	}
	
	public void onClick$emails()
	{
		
		emailCen.getChildren().clear();
		EmailNewWindow enw = (EmailNewWindow)Executions.createComponents("/apps/infoExtra/content/email/new/index.zul", null, null);
		emailCen.appendChild(enw);
	}
	
	public void onClick$emailsed()
	{
		
		emailCen.getChildren().clear();
		EmailSendWindow esw = (EmailSendWindow)Executions.createComponents("/apps/infoExtra/content/email/send/index.zul", null, null);
		emailCen.appendChild(esw);
	}
	
	public void onClick$emailr()
	{
		
		emailCen.getChildren().clear();
		EmailReceiveWindow erw = (EmailReceiveWindow)Executions.createComponents("/apps/infoExtra/content/email/Receive/index.zul", null, null);
		emailCen.appendChild(erw);
	}
	
}
