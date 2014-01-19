package com.uniwin.webkey.infoExtra.newsaudit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.newspub.NewsTreeModel;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;


public class NewsauditTaskSelectWindow extends Window implements AfterCompose {

	
	private static final long serialVersionUID = -2413959345167806957L;
	Tree tree,chaneltree;
	//站点数据访问接口
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	//WebsiteService websiteService;
	//RoleService roleService;
    //确定选择站点的按钮组件
	Toolbarbutton choosed,chooseall;
	Users user;
	List userDeptList,clist,wlist;
	List hasTlist,slist;
	List cList = new ArrayList();
	Radio part;
	Tab common,keys;
	String kind;
	NewsTreeModel wtm;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}
	
	public void initWindow(){
	
		user=(Users)Sessions.getCurrent().getAttribute("users");
		userDeptList=(List)Sessions.getCurrent().getAttribute("userDeptList");
		String mul=(String) Executions.getCurrent().getAttribute("mul");
		if(mul.equals("1"))
		{
		tree.setMultiple(true);
		}
		else tree.setMultiple(false);
		 List clist=taskService.getChildType(Long.parseLong("0"));
			wtm=new NewsTreeModel(clist,taskService);
			tree.setModel(wtm);
		     tree.setTreeitemRenderer(new TreeitemRenderer(){
			public void render(Treeitem item, Object data) throws Exception {
				if(data instanceof WkTExtractask)
				{
				WkTExtractask et=(WkTExtractask)data;
				item.setValue(et);
				item.setLabel(et.getKeName());
				item.setOpen(false);
				}
				else if(data instanceof WkTChanel)
				{
					WkTChanel tt=(WkTChanel)data;
					item.setValue(tt);
					item.setLabel(tt.getKcName());
					item.setCheckable(false);
					item.setOpen(true);
				}
				}			
		});
	}
public void onClick$choosed()
{
	Set set=tree.getSelectedItems();
	Iterator it=set.iterator();
	slist=new ArrayList();
	while(it.hasNext()){
		Treeitem titem=(Treeitem)it.next();
		Object o=titem.getValue();
		if(o instanceof WkTExtractask){
			slist.add(o);
		}
	}
    this.detach();
	Events.postEvent(Events.ON_CHANGE, this, null);
}
public List getSlist() {
	return slist;
}

public void setSlist(List slist) {
	this.slist = slist;
}
	//关闭当前窗口
	public void onClick$close()
	{
		   this.detach();	
	}
	
}
