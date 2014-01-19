package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.email.MessageUserSelectWindow;

public class InfoAllUserWindow extends MessageUserSelectWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1986260017400167299L;
	private Listbox userList, userListSelected;
	private ListModelList userListModel, userListSelectedModel;
	private Textbox trueName;
	private IUsersManager usersManager;
	private List<Users> selectUsers;
	private List<Integer> selectUserId = new ArrayList<Integer>();
	private Checkbox teacherCheck, studentCheck, graduateCheck;

	@SuppressWarnings("unchecked")
	public void onClick$choose() {
		Set<Listitem> isets = userList.getSelectedItems();
		Iterator<Listitem> ite = isets.iterator();
		List<Users> hlist = new ArrayList<Users>();
		while (ite.hasNext()) {
			Listitem item = (Listitem) ite.next();
			Users u = (Users) item.getValue();
			if (!selectUserId.contains(u.getUserId())) {
				hlist.add((Users) item.getValue());
			}
		}
		userListModel.removeAll(hlist);
		userListSelectedModel.addAll(hlist);
	}

	@SuppressWarnings("unchecked")
	public void onClick$remove() {
		Set<Listitem> isets = userListSelected.getSelectedItems();
		Iterator<Listitem> ite = isets.iterator();
		List<Users> hlist = new ArrayList<Users>();
		while (ite.hasNext()) {
			Listitem item = (Listitem) ite.next();
			Users u = (Users) item.getValue();
			selectUserId.remove(u.getUserId());
			hlist.add((Users) item.getValue());
		}
		userListSelectedModel.removeAll(hlist);
		userListModel.addAll(hlist);
	}

	public void onClick$reset() throws DataAccessException {
		initWindow(getSelectUsers());
	}


	/**
	 * ��ʼ�����ڣ���Ҫ�Ѿ�ѡ����û��б�
	 * 
	 * @param receList
	 * @throws DataAccessException 
	 */
	public void initWindow(List<Users> receList) throws DataAccessException {
		setSelectUsers(receList);
		List uList = usersManager.findMessageInfoUser();
		userListModel = new ListModelList(uList);
		userListSelectedModel = new ListModelList(receList);
		for (int i = 0; i < receList.size(); i++) {
			Users u = (Users) receList.get(i);
			selectUserId.add(u.getUserId());
		}
		userList.setModel(userListModel);
		userListSelected.setModel(userListSelectedModel);
	}

//	public void onClick$search() {
//		userListModel.clear();
//		WkTDept dept = (WkTDept) deptSelect.getSelectedItem().getValue();
//		List<WkTUser> userlist = userService.findUserForGroupAndEmailByKdIdAndName(dept.getKdId(), trueName.getValue(), teacherCheck.isChecked(), studentCheck.isChecked(), graduateCheck.isChecked());
//		userListModel.addAll(userlist);
//	}

	@SuppressWarnings("unchecked")
	public void onClick$submit() throws InterruptedException {
		selectUsers = userListSelectedModel.getInnerList();
		if(selectUsers.size()>20){
			Messagebox.show("ÿ�����ѡ��20������ߣ�", "��ʾ", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	public List<Users> getSelectUsers() {
		return selectUsers;
	}

	public void setSelectUsers(List<Users> selectUsers) {
		this.selectUsers = selectUsers;
	}

	@SuppressWarnings("unchecked")
	public List<Users> getSelectUser() {
		return userListSelectedModel.getInnerList();
	}

	public void initShow() {
		userList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, final Object arg1) throws Exception {
				arg0.setValue(arg1);
				Users u = (Users) arg1;
				Listcell c0 = new Listcell(arg0.getIndex() + 1 + "");
				Listcell c1 = new Listcell(u.getLoginName());
				Listcell c2 = new Listcell(u.getKuEmail());
			
				arg0.appendChild(c0);
				arg0.appendChild(c1);
				arg0.appendChild(c2);
				//arg0.appendChild(c3);// arg0.appendChild(c4);
			}
		});
		userListSelected.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, final Object arg1) throws Exception {
				arg0.setValue(arg1);
				Users u = (Users) arg1;
				Listcell c0 = new Listcell(arg0.getIndex() + 1 + "");
				Listcell c1 = new Listcell(u.getLoginName());
				Listcell c2 = new Listcell(u.getKuEmail());
				//Listcell c3 = new Listcell(u.getKuSex());
				
				arg0.appendChild(c0);
				arg0.appendChild(c1);
				arg0.appendChild(c2);
				//arg0.appendChild(c3);
			}
		});
//		String kuLid = (String) Sessions.getCurrent().getAttribute("kuLid");
//		UserInfo wkTUser = (UserInfo) EhcacheUtil.loadObjectCacheManual(EntityUtil.buildEntityGlobeId(UserInfo.class, kuLid.toLowerCase().trim()));
//		WkTUser user = wkTUser.getWkTUserForLog();
//		deptSelect.setRootDept(null);
//		deptSelect.setRootID(0L);
//		deptSelect.initNewDeptSelect(user.getWktDept());
	}
}
