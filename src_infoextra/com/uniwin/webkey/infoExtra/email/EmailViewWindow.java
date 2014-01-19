package com.uniwin.webkey.infoExtra.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;


import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.model.SendMail;

public class EmailViewWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5050161998188703730L;
	// ��Ϣ�����
	private Label xmSubject, xmSendTime, xmReceive;
	// Html��ǩ��ʾ����
	private Html xmContent;
	// ��Ϣʵ��
	// ��
	private Row rowFile;
	// listģ��
	private Listbox downList;
	private ListModelList modelListbox;
	// ����Ϣ�б���ݽӿ�
	private Users user;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        user = (Users)Sessions.getCurrent().getAttribute("users");
		modelListbox = new ListModelList(new ArrayList<File>());
		downList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, Object arg1) throws Exception {
				arg0.setValue(arg1);
				File f = (File) arg1;
				arg0.setValue(arg1);
				arg0.setLabel(f.getName() + "   " + "(" + f.length() / 1024 + "KB" + ")");
			}
		});
		downList.setModel(modelListbox);
	}

	/**
	 * <Li>������perReceiveҳ�������ݣ���ʼ������ 2010-4-8
	 * 
	 * @author bobo
	 * @throws Exception 
	 * 
	 */
	public void initWindow(SendMail notice) throws Exception {
		xmReceive.setValue(notice.getMailto());
		xmSubject.setValue(notice.getSubject());
		xmContent.setContent(notice.getContent());
		xmSendTime.setValue(notice.getMaildate());
			rowFile.setVisible(true);
			String basepath = UploadUtil.getRealPath("/email/"+user.getUserId()+"/"+notice.getId());
			File folder = new File(basepath);
			if(folder.exists()){
				String[] filelist = folder.list();
				if (filelist != null && filelist.length != 0) {
					for (int i = 0; i < filelist.length; i++) {
						File readfile = new File(folder.getAbsolutePath() + "\\" + filelist[i]);
						if (readfile.exists()) {
							modelListbox.add(readfile);
						}
					}
				}
			}
		}

	/**
	 * <Li>ZK������ظ��� 2010-4-11
	 * 
	 * @author bobo
	 * @throws FileNotFoundException
	 * 
	 */
	public void onClick$download() throws InterruptedException, FileNotFoundException {
		Listitem it = downList.getSelectedItem();
		if (it == null) {
			if (modelListbox.getSize() > 0) {
				it = downList.getItemAtIndex(0);
			} else {
				return;
			}
		}
		File f = (File) it.getValue();
		Filedownload.save(f, null);
	}

	public void onClick$concel() {
		this.detach();
	}

	public ListModelList getModelList() {
		return modelListbox;
	}
}
