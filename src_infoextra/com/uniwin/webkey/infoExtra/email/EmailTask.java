package com.uniwin.webkey.infoExtra.email;

import java.util.List;
import java.util.TimerTask;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.UserIdAnddomainId;
import com.uniwin.webkey.infoExtra.model.WkTUserIdAnddomainId;

public class EmailTask extends TimerTask {
    
	//private Users user;
	@Override
	public void run() {
//		user=(Users)Sessions.getCurrent().getAttribute("users");
//		InfoIdAndDomainId ss = (InfoIdAndDomainId) SpringUtil.getBean("InfoIdAndDomainId");
//		UserIdAnddomainId us = (UserIdAnddomainId) SpringUtil.getBean("UserIdAnddomainId");
//		String euser1 = user.getKuEUserTo();
//		System.out.println(euser1);
//		String[] euser2 = euser1.split(",");
//		for(String euser3:euser2){
//			int euser4 = Integer.parseInt(euser3);
//			List<WkTUserIdAnddomainId> udList = us.findAllById(euser4);
//			for(int i=0;i<udList.size();i++){
//				int du = udList.get(i).getKsId();
//				List<WkTInfo> uiList = ss.findAllByDomainId(du);
//				for(int k=0;k<uiList.size();k++){
//					System.out.println(uiList.get(i).getKiTitle());
//				}		
//			}
//		}
		//System.out.println("邮件又发了");
		

	}

}
