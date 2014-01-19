package com.uniwin.webkey.infoExtra.email;


import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.model.ReceiveMail;


public class ReceiveMailViewWindow extends Window implements AfterCompose {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4073646420909058701L;

	// ��Ϣ�����
	private Label emailSubject, emailFrom, emailDate, emailReceive;
	// Html��ǩ��ʾ����
	private Html emailContent;
	// ����Ϣ�б���ݽӿ�
	private Users user;
	private ReceiveMail message;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        user = (Users)Sessions.getCurrent().getAttribute("users");
		
	}

	/**
	 * <Li>������Receiveҳ�������ݣ���ʼ������
	 * 
	 * @author bobo
	 * @throws Exception 
	 * 
	 */
	public void initWindow(ReceiveMail message) throws Exception {
		this.message = message;
		emailSubject.setValue(message.getSubject());
		emailFrom.setValue(message.getMailfrom());
		emailReceive.setValue(user.getKuEmail());
		emailDate.setValue(message.getMaildate());
		if(message.getContent()!=null&&!message.getContent().equals("")){
			emailContent.setContent(message.getContent());	
		}else{
			emailContent.setContent("");	
		}
			
	}
	
	public void onClick$concel() {
		this.detach();
	}

}
