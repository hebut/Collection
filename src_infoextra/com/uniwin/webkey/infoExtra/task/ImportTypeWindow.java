package com.uniwin.webkey.infoExtra.task;
/**
 * <li>功能描述:批量上传分类
 */


import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.util.DateUtil;

public class ImportTypeWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	WkTChanel chanel;
	WkTChanel type;
	//分类实例窗口
	Textbox clist;
	//分类树
	Tree tree;
	//树模型
	TasktypeTreeModel ctm;
	Users user;
	boolean signal=false;	
	Media media;
//	WkTUser wkTUser;
	List Medialist,zlist,alist,userDeptList;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);	
		user=(Users)Sessions.getCurrent().getAttribute("users");	
		StringBuffer buffer=new StringBuffer("博客");
		buffer.append("\r\n");		
		buffer.append("  新浪博客");
		buffer.append("\r\n");	
		buffer.append("  网易博客");
		//示例框初始化
		clist.setValue(buffer.toString());
		clist.addEventListener(Events.ON_CHANGE, new EventListener(){
           
			public void onEvent(Event arg0) throws Exception {
		     signal=true;	
		     }
			});
	}

	public void initWindow(WkTChanel type) {
		this.type = type;
	}

	public WkTChanel getEditType() {

		return type;
	}
	public void setMedia(Media media){
		this.media=media;
	}
	public Media getMedia(){
		
	  return media;
	}
	public void setSignal(boolean s){
		this.signal=s;
	}
	
	//点击下一步，浏览效果
	public void onClick$next() throws InterruptedException, IOException{

	Sessions.getCurrent().setAttribute("type",type);
		if(signal==false){
			Messagebox.show("请更改示例中的内容！", "上传失败", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		String path1=(String)Sessions.getCurrent().getAttribute("path");
		if(path1==null)
		{  
			String path2=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/task")+"//"+DateUtil.getDateTimeString(new Date())+"_"+"task.txt";
	        FileWriter fw=new FileWriter(path2);//文件保存名
	       for(int i=0;i<clist.getValue().length();i++){	 
            fw.write(clist.getValue().charAt(i));
	       }
           Sessions.getCurrent().setAttribute("path2",Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/task/"+"//"+DateUtil.getDateTimeString(new Date())+"_"+"task.txt"));
	       fw.close();
		 }
		Executions.getCurrent().setAttribute("type", type);
		this.detach();
		System.out.println("=");
		/*ViewTypeWindow vw=(ViewTypeWindow)Executions.createComponents("/admin/content/task/view.zul", null, null);
		vw.doHighlighted();	
		vw.addEventListener(Events.ON_CHANGE, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				refleshTree();			
			}
			
		});*/
	}
	
	//取消分类上传
	public void onClick$cancel(){
		this.detach();
		refleshTree();
		
	}
	//刷新树
	public void refleshTree(){
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
}
