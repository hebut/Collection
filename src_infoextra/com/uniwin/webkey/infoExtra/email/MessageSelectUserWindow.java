package com.uniwin.webkey.infoExtra.email;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.infogenerated.InfoAllUserWindow;

public class MessageSelectUserWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6651232284569280206L;
	private InfoAllUserWindow allUserSelect;
	private List<Users> selectUsers = new ArrayList<Users>();
	private Tab allUserSelectTab;

	//private Tab myGroupSelectTab, bysjUserSelectTab;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		allUserSelect = (InfoAllUserWindow) Executions.createComponents("/apps/infoExtra/content/infogenerated/new_info/allUserSelect.zul", allUserSelectTab.getLinkedPanel(), null);
		allUserSelect.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				selectUsers = allUserSelect.getSelectUsers();
				sendEvents();
			}
		});
	}

	public void initWindow(List<Users> recelist) throws Exception {
		if (recelist != null) {
			allUserSelect.initWindow(recelist);
		}
	}

	private void sendEvents() {
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	public List<Users> getSelectUsers() {
		return selectUsers;
	}
}
