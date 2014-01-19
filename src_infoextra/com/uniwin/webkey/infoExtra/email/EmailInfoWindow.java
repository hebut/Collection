package com.uniwin.webkey.infoExtra.email;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;

public class EmailInfoWindow extends Window  implements AfterCompose{

	/**
	 * 
	 */
	private Users user;

	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	private Textbox username,smtp,password;
	private IUsersManager usersManager;
	private static final long serialVersionUID = -3197537088050190179L;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}
	public void onClick$cancel() {
		this.detach();
	}
	public void onClick$submit() throws Exception, ObjectNotExistException {
		user.setKuSendMail(username.getValue());
		user.setKuSendUsername(username.getValue());
		user.setKuSendSmtp(smtp.getValue());
		user.setKuSendPassword(password.getValue());
		usersManager.update(user);
		Events.postEvent(Events.ON_CHANGE, this, user);
		this.detach();
	}
}
