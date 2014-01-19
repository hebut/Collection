package com.uniwin.webkey.infoExtra.tasktemp;
/**
 * 编辑分类
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



public class SortEditWindow extends Window implements AfterCompose {


	 
	private static final long serialVersionUID = 1L;
	//使用自定义的listbox组件，用于选择父分类
	TaskListbox sortlist;
	Textbox name,temp,desc;
	Intbox orderno;
	Listitem yes,no;
	Users user;
	Window sortedit;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	
	WkTChanel t;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");
	}
	
	public void initWindow(WkTChanel t)
	{
		this.t=t;
		WkTChanel e=taskService.getTpyeById(t.getKcPid());
		sortlist.initAllTaskSortSelect(e,t);
		name.setValue(t.getKcName());
		temp.setValue(t.getKcTplist());
		orderno.setValue(Integer.parseInt(t.getKcOrdno().toString()));
		if(t.getKfId().toString().trim().equals("0"))
		{
			no.setSelected(false);
		}
		else if(t.getKfId().toString().trim().equals("1"))
		{
			no.setSelected(false);
		}
		desc.setValue(t.getKcDesc());
	}
	public void onSelect$sortlist() throws InterruptedException
	{
		Listitem item=sortlist.getSelectedItem();
		WkTChanel task=(WkTChanel) item.getValue();
		if(task.getKcId().toString().trim().equals(t.getKcId().toString().trim()))
		{
//			Messagebox.show("请重新选择！");
			WkTChanel e=taskService.getTpyeById(t.getKcPid());
			sortlist.initAllTaskSortSelect(e,t);
		}
	}
	
   //保存分类信息
	public void onClick$save() throws InterruptedException, IOException{
		if(name.getValue().equals(""))
		{
			Messagebox.show("请输入分类名称");
		}
		else
		{
		t.setKcName(name.getValue());
		t.setKcOrdno(Long.parseLong(orderno.getValue().toString()));
		if(yes.isSelected())
		{
			t.setKfId(Long.parseLong("1"));
		}
		else if(no.isSelected())
		{
			t.setKfId(Long.parseLong("0"));
		}
		else
		{
			t.setKfId(Long.parseLong("1"));
		}
		t.setKcDesc(desc.getValue());
		Listitem item=sortlist.getSelectedItem();
		WkTChanel ta=(WkTChanel) item.getValue();
		t.setKcPid(ta.getKcId());
		t.setKcTplist(temp.getValue());
		taskService.update(t);
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
		initWindow(t);
	}
	//关闭
	public void onClick$back()
	{
		sortedit.detach();
	}
	//刷新左侧站点树
	public void refreshTree()
	{
        Events.postEvent(Events.ON_CHANGE, this, null);
    }
}
