package com.uniwin.webkey.infoExtra.email;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;

public abstract class MessageUserSelectWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4667774448241534076L;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initShow();
	}

	public abstract List<Users> getSelectUser();

	public abstract void initShow();
}
