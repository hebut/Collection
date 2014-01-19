package com.uniwin.webkey.infoExtra.email;


import java.util.ArrayList;

import java.util.List;


import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import org.zkoss.zul.Window;


import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;


public class AllInfoWindow extends Window  implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
    private Users user;
    private IUsersManager usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
    private Textbox sendusername,smtp,sendpassword,pop,receiveusername,receivepassword,eUser;
    private Button addUser;
    private Intbox todTime;
    private List<Users> recelist = new ArrayList<Users>();
	public void afterCompose() {
//		Components.wireController(this, this);
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
		sendusername.setValue(user.getKuSendUsername());
		smtp.setValue(user.getKuSendSmtp());
		sendpassword.setValue(user.getKuSendPassword());
		receiveusername.setValue(user.getKuReceiveMail());
		receivepassword.setValue(user.getKuReceivePassword());
		pop.setValue(user.getKuReceivePop());
		String gg = user.getKuEUserTo();
		if(!gg.trim().equals("")&&!gg.equals(null))
		{
        String[] gg1 = gg.split(",");
        StringBuffer su = new StringBuffer();
        for(int i= 0; i<gg1.length;i++){
        	Integer u = Integer.parseInt(gg1[i]);
        	if(usersManager.getUserUid(Long.parseLong(gg1[i])).size()!=0)
        	{
        	try {
				Users euser = (Users)usersManager.get(u);
				su.append(euser.getLoginName());
				if ( i<(gg1.length-1)) {
					su.append(",");
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (ObjectNotExistException e) {
				e.printStackTrace();
			}
        	}
        }
		
        eUser.setValue(su.toString());
		}
		todTime.setValue(user.getKuETime());
	
		
		addUser.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				MessageSelectUserWindow addwin = (MessageSelectUserWindow) Executions.createComponents("/apps/infoExtra/content/email/conf/selectUser.zul", null, null);
				addwin.doHighlighted();
				addwin.setTitle("选择收件人");
				addwin.initWindow(recelist);
				addwin.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						MessageSelectUserWindow addwin = (MessageSelectUserWindow) arg0.getTarget();
						addReceiver(addwin.getSelectUsers());
						addwin.detach();
					}
				});
			}
		});       
}
	public void addReceiver(List<Users> userList) {
		recelist = userList;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < recelist.size(); i++) {
			Users u = (Users) recelist.get(i);
			sb.append(u.getLoginName());
			if (i < (recelist.size() - 1)) {
				sb.append(",");
			}
		}
		eUser.setValue(sb.toString());
	}
	
	
	
	public void onClick$submit() throws DataAccessException, InterruptedException, ObjectNotExistException {
			
/*		    
		InfoIdAndDomainId ss = (InfoIdAndDomainId) SpringUtil.getBean("InfoIdAndDomainId");
		UserIdAnddomainId us = (UserIdAnddomainId) SpringUtil.getBean("UserIdAnddomainId");
		
		String hh = user.getKuEUserTo();
		String[] types = hh.split(",");
		for(String type: types){
			Integer id = Integer.parseInt(type);
			List<WkTUserIdAnddomainId> kk = new ArrayList<WkTUserIdAnddomainId>();
			kk = us.findAllById(id);
			for(int i = 0; i < kk.size(); i++){
				System.out.println(kk.get(i).getKiId());
				List<WkTInfo> tt = new ArrayList<WkTInfo>();
				tt = ss.findAllByDomainId(kk.get(i).getKiId());
				for(int k = 0; k< tt.size();k++){
					System.out.println(tt.get(i).getKiTitle());
				}
			}
			
		}*/
		
		if(recelist.size()!=0)
		{
		    StringBuffer sf = new StringBuffer();
		    for (int i = 0; i < recelist.size(); i++) {
			    Users u = (Users) recelist.get(i);
			    sf.append(u.getUserId());
			    if (i < (recelist.size() - 1)) {
				sf.append(",");
			    }
		     }
		  
		    //Long etime1 = etime*24*3600;
		    user.setKuEUserTo(sf.toString());
		}
		  Integer etime = todTime.getValue();
		    user.setKuETime(etime);
		    user.setKuSendUsername(sendusername.getValue());
			user.setKuSendSmtp(smtp.getValue());
			user.setKuSendPassword(sendpassword.getValue());
			user.setKuReceiveMail(receiveusername.getValue());
			user.setKuReceivePassword(receivepassword.getValue());
			user.setKuReceivePop(pop.getValue());
			usersManager.update(user);

			Messagebox.show("保存成功","邮箱配置", Messagebox.OK, Messagebox.INFORMATION);
			
	}
	

	

}
