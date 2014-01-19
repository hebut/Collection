package com.uniwin.webkey.core.ui;

import java.util.Map;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Html;

import org.zkoss.zul.Window;


import com.uniwin.webkey.core.model.InfoUser;


public class InfoUsersFocusWin extends Window implements AfterCompose
{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1927326170908605007L;
	InfoUser  infoUser;     
    private Html focus;
	public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        @SuppressWarnings("rawtypes")
		Map map = Executions.getCurrent().getArg();
        infoUser = (InfoUser) map.get("infoUser");
        focus = (Html)this.getFellow("focus");
        focus.setContent(infoUser.getFocusCon());
    }
	
}