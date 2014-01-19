package com.uniwin.webkey.infoExtra.task;
/**
 * 添加新分类
 */
import java.io.IOException;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.cms.model.WkTChanel;




public class NewSortWindow extends Window implements AfterCompose {


	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//使用自定义的listbox组件，用于选择父分类
	TaskListbox sortlist;
	Textbox sortname,temp,desc;
	Intbox orderno;
	Listitem yes,no;
    Users user;
	Window newsort;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
		
	}
	
	protected void initWindow(WkTChanel t)
	{
		sortlist.initAllTaskSortSelect(t,null);
	}
	
   //保存新增加的分类信息
	public void onClick$save() throws InterruptedException, IOException{
		if(sortname.getValue().equals(""))
		{
			Messagebox.show("请输入分类名称");
		}
		else
		{
			WkTChanel tt=new WkTChanel();
		tt.setKcName(sortname.getValue());
		tt.setKcOrdno(Long.parseLong(orderno.getValue().toString()));
		if(yes.isSelected())
		{
			tt.setKfId(Long.parseLong("1"));
		}
		else if(no.isSelected())
		{
			tt.setKfId(Long.parseLong("0"));
		}
		else
		{
			tt.setKfId(Long.parseLong("1"));
		}
		tt.setKcDesc(desc.getValue());
		Listitem item=sortlist.getSelectedItem();
		if(item!=null)
		{
			WkTChanel t=(WkTChanel) item.getValue();
		tt.setKcPid(t.getKcId());
		}
		else tt.setKcPid(Long.parseLong("1"));
		tt.setKcTplist(temp.getValue());
		tt.setKwId(1L);
		tt.setKcKind(0L);
		taskService.save(tt);
	//	mlogService.saveMLog(WkTMlog.FUNC_CMS, "新建分类，id:"+tt.getKcId(), user);
		try 
		{
			Messagebox.show("保存成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		refreshTree();
		this.detach();
		}
	}
    //点击reset按钮触发的方法
	public void onClick$reset()
	{
		sortname.setRawValue(null);
		temp.setRawValue(null);
		desc.setRawValue(null);
		orderno.setValue(Integer.parseInt("10"));
		sortlist.initAllTaskSortSelect(null,null);
			
	}
	//关闭
	public void onClick$back()
	{
		newsort.detach();
	}
	//刷新左侧站点树
	public void refreshTree()
	{
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
}
