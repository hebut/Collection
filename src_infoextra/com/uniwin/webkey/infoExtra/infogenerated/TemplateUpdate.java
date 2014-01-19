package com.uniwin.webkey.infoExtra.infogenerated;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.TemplateFile;

/**
 * @author 
 *
 */
public class TemplateUpdate extends Window implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map params;
	OriNewsService orinewsService = (OriNewsService) SpringUtil
			.getBean("info_orinewsService");
	NewsServices newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");
	TaskService taskService = (TaskService) SpringUtil.getBean("taskService");
	
	private Users user;
	private TemplateFile template_update;
	private Textbox url,topbackground,titlesize,infosize,contentsize;
	private Iframe templatePreView;
	
	private Listbox titlecolor,infocolor,contentcolor;
	
	private String style ;

	public TemplateUpdate(){
		 @SuppressWarnings("rawtypes")
		 Map map = Executions.getCurrent().getArg();
		 template_update = (TemplateFile) map.get("template_update");//从父窗口获得传来的参数
	}
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users) Sessions.getCurrent().getAttribute("users");
		
        url = (Textbox) this.getFellow("url");
        url.setValue(template_update.getFileName());
        String p="http://localhost:8080/InfoCollection";
        templatePreView.setSrc(p+template_update.getFileName()+"?style="+template_update.getStyle());
        
	}
	
	public void onClick$refresh(){
		
		String p="http://localhost:8080/InfoCollection";
		/*String style = "?titlecolor="+titlecolor.getValue()+"&titlesize="+titlesize.getValue()+"&infocolor="+infocolor.getValue()+"&infosize="+infosize.getValue()
				+"&contentcolor="+contentcolor.getValue()+"&contentsize="+contentsize.getValue()+"&topbackground="+topbackground.getValue();
		*/
		
		String style = "tr.title{color:"+titlecolor.getSelectedItem().getValue().toString()+";font-size:"+titlesize.getValue()+"px}"+
				       "tr.info{color:"+infocolor.getSelectedItem().getValue().toString()+";font-size:"+infosize.getValue()+"px}"+
				       "tr.content{color:"+contentcolor.getSelectedItem().getValue().toString()+";font-size:"+contentsize.getValue()+"px}"+
				       "#top{background-color:"+topbackground.getValue()+";width:100%;height:120px;"+"}";
		
		templatePreView.setSrc(p+template_update.getFileName()+"?style="+style);
		
		this.style = style;
	}
	public void onClick$save(){
		System.out.println(style);
		template_update.setStyle(style);
		newsService.update(template_update);
		try {
			Messagebox.show("保存成功!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
