package com.uniwin.webkey.infoExtra.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.itf.SendMailService;
import com.uniwin.webkey.infoExtra.itf.UserService;
import com.uniwin.webkey.infoExtra.model.SendMail;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.io.Files;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;


public class EmailNewWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6583512750938805591L;
	// 消息输入框组件
	private Textbox mlSubject, user_to;
	private FCKeditor mlContent;
	// 添加收件人按钮
	private Button addUser;
	// 暂存收件人列表
	private List<Users> recelist = new ArrayList<Users>();
	private Row rowFile;
	private Listbox upList;
	private ListModelList fileModel;
	private Users user;
	private IUsersManager usersManager;
	private boolean sendflag=false;
	private SendMailService sendMailService = (SendMailService)SpringUtil.getBean("SendMailService");
	private UserService userService = (UserService)SpringUtil.getBean("UserService");
	

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
        user = (Users)Sessions.getCurrent().getAttribute("users");
		upList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, Object arg1) throws Exception {
				Media name = (Media) arg1;
				arg0.setValue(arg1);
				arg0.setLabel(name.getName());
			}
		});
		// 添加收件人
		addUser.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				MessageSelectUserWindow addwin = (MessageSelectUserWindow) Executions.createComponents("/apps/infoExtra/content/email/new/selectUser.zul", null, null);
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
		fileModel = new ListModelList(new ArrayList<Object>());
		upList.setModel(fileModel);
		this.mlContent.addEventListener(Events.ON_CHANGE, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				if(sendflag)
					onChange();
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
		user_to.setValue(sb.toString());
	}

	public void onClick$upFile() throws Exception {
		Object media = Fileupload.get();
		if (media == null) {
			return;
		}
		rowFile.setVisible(true);
		fileModel.add(media);
	}

	/**
	 * <li>新消息的 “发送” 功能
	 * 
	 * @author bobo 2010-03-01
	 * @throws Exception 
	 */
	public void onClick$save() throws Exception {
		sendflag=true;
		Events.postEvent(Events.ON_CHANGE, mlContent, null);
	}
	private void onChange() throws InterruptedException, Exception{
		user = usersManager.get(user.getUserId());
		user=userService.loadById(Users.class, user.getUserId());
		if(user.getKuSendSmtp()==null){
			EmailInfoWindow addwin = (EmailInfoWindow) Executions.createComponents("/apps/infoExtra/content/email/new/getInfo.zul", null, null);
			addwin.doHighlighted();
			addwin.setUser(user);
			addwin.addEventListener(Events.ON_CHANGE, new EventListener() {
				public void onEvent(Event arg0) throws Exception {
					user=(Users) arg0.getData();
					sendEmail();
				}
			});
		}else{
			sendEmail();
		}
	}

	private void sendEmail() throws Exception {
		if (mlSubject.getValue().equals("") || mlSubject.getValue().getBytes().length > 40) {
			Messagebox.show("请您检查标题，标题必须填写，并且不能超过50个字符(25个汉字)！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			mlSubject.focus();
			return;
		} else if (user_to.getValue().equals("") && user_to.getValue() != null) {
			Messagebox.show("请您选择收件人，收件人不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
			user_to.focus();
			return;
		} else {
			SendMail sm=new SendMail();
			System.out.println(mlContent.getValue());
			sm.setContent(mlContent.getValue());
			sm.setMaildate(DateUtil.getDateString(new Date()));
			sm.setMailto(user_to.getValue());
			sm.setSubject(mlSubject.getText());
			sm.setUser(user);
			sendMailService.save(sm);
			//System.out.println(sm);
			List<?> flist = fileModel.getInnerList();
			List<String> fileNamelist=new ArrayList<String>();
			for (int i = 0; i < flist.size(); i++) {
				Media media = (Media) flist.get(i);
				saveToFile(media,sm.getId());
				//System.out.println(media);
				fileNamelist.add(media.getName());
			}
			
			boolean flag=true;
			StringBuilder sb=new StringBuilder();
			//System.out.println(recelist);
			for (int i = 0; i < recelist.size(); i++) {
				Users u = (Users) recelist.get(i);
				//u=userService.loadById(Users.class, u.getUserId());
				Mail sendmail=new Mail(u.getKuEmail(),user.getKuSendUsername(),user.getKuSendSmtp(),user.getKuSendUsername(),user.getKuSendPassword(),mlSubject.getText(),mlContent.getValue());
				for (String s:fileNamelist) {
					sendmail.attachfile(UploadUtil.getRealPath("/email/"+user.getUserId())+"/"+sm.getId()+"/"+s);
				}
				if(!sendmail.startSend()){
					flag=false;
					sb.append(user.getLoginName()+",");
				}
			}
			if(flag){
				Messagebox.show("发送成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
				onClick$reSend();
			}
			else
				Messagebox.show("发送出现问题！"+sb.toString().substring(0,sb.length()-1)+"发送失败！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		    }
	}

	/**
	 * <li>使用ZK上传组件，将上传的文件保存到硬盘,获取消息ID号创建文件夹
	 * 
	 * @param Media
	 *            media 附件
	 * @param String
	 *            mlid 消息的Id号
	 * @author bobo 2010-4-11
	 * @throws Exception 
	 */
	public void saveToFile(Media media,Long id) throws Exception {
		if (media != null) {
			String fileName = media.getName();
			//System.out.println(fileName);
			UploadUtil up=new UploadUtil();
			String basePath =UploadUtil.getRealPath("/email/");
			
			//System.out.println(basePath);
			
			File folder = new File(basePath + "\\" + user.getUserId()+"\\"+id+"\\");
			// 如果Mlid文件夹不存在，用于在upload文件下创建Mlid号的文件夹
			if (!folder.exists()) {
				folder.mkdirs();
			}
			String path = folder.getAbsolutePath() + "\\" + fileName;
			System.out.println(path);
			File newFile = new File(path);
			if (newFile.exists()) {
				newFile.delete();
			} else {
				newFile.createNewFile();
			}
			if (fileName.endsWith(".txt") || fileName.endsWith(".project")) {
				Reader r = media.getReaderData();
				File f = new File(path);
				Files.copy(f, r, null);
				Files.close(r);
			} else {
				OutputStream out = new FileOutputStream(newFile);
				InputStream objin = media.getStreamData();
				byte[] buf = new byte[1024];
				int len;
				long finallen = 0L;
				while ((len = objin.read(buf)) > 0) {
					out.write(buf, 0, len);
					finallen = finallen + len;
				}
				out.close();
				objin.close();
			}
		}
	}

	/**
	 * <li>删除上传的文件，重新选择
	 * 
	 * @author bobo 2010-4-11
	 */
	public void onClick$deUpload() {
		Listitem it = upList.getSelectedItem();
		if (it == null) {
			if (fileModel.getSize() > 0) {
				it = upList.getItemAtIndex(0);
			}
		}
		if (fileModel.getSize() == 1) {
			fileModel.remove(it.getValue());
			rowFile.setVisible(false);
		} else if (fileModel.getSize() > 1) {
			fileModel.remove(it.getValue());
		}
	}

	/**
	 * <li>页面输入框进行重置
	 * 
	 * @author bobo 2010-4-11
	 */
	public void onClick$reSend() {
		rowFile.setVisible(false);
		mlSubject.setValue("");
		user_to.setValue("");
		mlContent.setValue("");
		fileModel.clear();
		sendflag=false;
		recelist = new ArrayList<Users>();
	}
}
