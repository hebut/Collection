package com.uniwin.webkey.infoExtra.newspub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;
import com.uniwin.webkey.infoExtra.newspub.NewsTreeModel;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;



public class NewsTaskSelectWindow extends Window implements AfterCompose {

	
	private static final long serialVersionUID = -2413959345167806957L;
	Tree tree;
	TaskService taskService;
    //确定选择站点的按钮组件
	List hasTlist,slist;
	Radio part;
	Tab common,keys;
	String kind;
	NewsTreeModel wtm;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}
	
	public void initWindow(){
	String mul=	(String) Executions.getCurrent().getAttribute("mul");
	if(mul.equals("1"))
	{
		tree.setMultiple(true);
	}
	else if(mul.equals("0"))
	{
		tree.setMultiple(false);
	}
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
		Events.postEvent(Events.ON_CHANGING, this, null);
		   this.detach();	
	}
	
}
