package com.uniwin.webkey.infoExtra.infogenerated;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.BaseService;
import com.uniwin.webkey.infoExtra.itf.InfoGeneratedService;
import com.uniwin.webkey.infoExtra.model.TemplateFile;
import com.uniwin.webkey.infoExtra.util.BeanFactory;

public class AddTemplate extends Window implements AfterCompose {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1913489209867127034L;
	public static final String BASE_PATH = "/template/";
	private Users user;
	private Textbox template_name;
	private String filePathAndName;
	private InfoGeneratedService infoGeneratedService;
///	
	private Listbox templatelistbox;
	private ListModelList infoListModel;
	
	public void setTemplatelistbox(Listbox templatelistbox) {
		this.templatelistbox = templatelistbox;
	}

	public void setInfoListModel(ListModelList infoListModel) {
		this.infoListModel = infoListModel;
	}
	///	
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}
	
	public void initWindow(){
	}
	
	public void onClick$save(){
		if(template_name.getValue()==null||template_name.getValue()==""){
			try {
				Messagebox.show("请填写模板名称！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		if(filePathAndName==null||filePathAndName==""){
			try {
				Messagebox.show("请上传模板文件！");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		TemplateFile templateFile = new TemplateFile();
		templateFile.setName(template_name.getValue());
		templateFile.setFileName(filePathAndName);
		templateFile.setTime(new Date().getTime());
		infoGeneratedService.save(templateFile);
		try {
			Messagebox.show("保存成功！");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//刷新列表
		List<TemplateFile> templateList = infoGeneratedService.findTemplateAllFile();
		searchinfo(templateList,templatelistbox,infoListModel);
		this.detach();
	}
	
	//重载搜索到的信息列表
	public void searchinfo(List<?> slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	
	public void onClick$close(){
		if(filePathAndName!=null){
			File tempFile = new File(Executions.getCurrent().getDesktop().getWebApp().getRealPath(BASE_PATH)+"\\"+filePathAndName.substring(filePathAndName.lastIndexOf("/"), filePathAndName.length()));
			if (tempFile.exists()) {
				tempFile.delete();
			}
		}
		this.detach();
	}
	
	public void onClick$up_load() throws WrongValueException, InterruptedException, IOException{
		if(template_name.getValue()==null||template_name.getValue().equals("")){
			Messagebox.show("请填写模板名称！");
			template_name.focus();
			return;
		}
		filePathAndName = uploadFile(BASE_PATH, new Date().getTime()+"_"+template_name.getValue(), "jsp,html", 1);
	}
	
	public String uploadFile(String relativePath, String fileName, String fileType, Integer maxSize) throws InterruptedException, IOException {
		if (maxSize == null) {
			Messagebox.show("请设置上传文件最大值！", "注意", Messagebox.OK, Messagebox.ERROR);
			return "";
		}
		Media[] media = Fileupload.get("上传文件类型必须为:" + fileType + ",文件最大" + maxSize + "M", "请选择上传文件", 1);
		if (media != null) {
			String filename = media[0].getName();
			String ftype = "";
			if ((filename != null) && (filename.length() > 0)) {
				int i = filename.lastIndexOf('.');
				if ((i > 0) && (i < (filename.length() - 1))) {
					ftype = filename.substring(i + 1);
				}
			}
			if (!rightType(fileType, ftype)) {
				Messagebox.show("上传文件类型错误！只能为" + fileType.toString(), "注意", Messagebox.OK, Messagebox.ERROR);
				return "";
			}
			//String ins = media[0].getStringData();
			byte[] ins = media[0].getByteData();
			String filePath = this.getDesktop().getWebApp().getRealPath(relativePath);//服务器根目录下的template文件夹
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File newFile = new File(filePath + "\\" + fileName + "." + ftype);
			if (newFile.exists()) {
				newFile.delete();
			}
			OutputStream out = new FileOutputStream(newFile);
			//byte[] buf = new byte[1024];
			//int len;
			//long finallen = 0L;
			//out.write(ins.getBytes(), 0, ins.length());
			out.write(ins, 0, ins.length);
//			while ((len = ins.read(buf)) > 0) {
//				out.write(buf, 0, len);
//				finallen = finallen + len;
//			}
			out.close();
			//ins.close();
			if (ins.length > maxSize * 1024 * 1024) {

				Messagebox.show("上传文件超过最大限制！" + ",文件最大" + maxSize + "M", "注意", Messagebox.OK, Messagebox.ERROR);
				File tempFile = new File(filePath + "\\" + fileName + "." + ftype);
				if (tempFile.exists()) {
					tempFile.delete();
				}
				return "";
			}
			return relativePath + fileName + "." + ftype;
		}
		return "";
	}
	
	private boolean rightType(String fileType, String ftype) {
		if (fileType == null || fileType.length() == 0) {
			return true;
		}
		String[] fileTypes = fileType.split(",");
		for (int i = 0; i < fileTypes.length; i++) {
			if (fileTypes[i].equalsIgnoreCase(ftype)) {
				return true;
			}
		}
		return false;
	}
}
