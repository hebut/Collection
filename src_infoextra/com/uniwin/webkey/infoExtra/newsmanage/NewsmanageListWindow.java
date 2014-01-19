package com.uniwin.webkey.infoExtra.newsmanage;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;

public class NewsmanageListWindow extends Window implements AfterCompose
{
	private static final long serialVersionUID = 2223419390596150992L;
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
	private Center infmanageCen;
	private Tree tree;
	
	NewsManageTreeModel ctm;//树的模型组件
        
	public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        tree.addEventListener(Events.ON_SELECT, new TreeEventListener());
        try {
			inittree();
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (ObjectNotExistException e) {
			e.printStackTrace();
		}
		if(tree.getSelectedItem()!=null)
		{
			 if(tree.getSelectedItem().getValue() instanceof WkTExtractask)
			 {
			 WkTExtractask etask=(WkTExtractask) tree.getSelectedItem().getValue();
			 windowLoad(etask);
		     }
		}
		
        tree.getPagingChild().setMold("os");
    }
	
	/**
	 * 加载发布信息窗口
	 * @param chanel
	 */
	public void windowLoad(WkTExtractask etask)
	{
		if(etask==null)
		{
			return;
		}
		infmanageCen.setTitle(org.zkoss.util.resource.Labels
                .getLabel("chanel.ui.operationChanelName")+" : "+etask.getKeName());
		infmanageCen.getChildren().clear();
		NewsManageEditWindow c = (NewsManageEditWindow)Executions.createComponents("/apps/infoExtra/content/newsmanage/manage.zul",infmanageCen, null);
		c.initManage(etask);
	}
	
	/**
	 * 加载左侧栏目树，当前用户不具有发布权限的栏目不可用
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
	public void inittree() throws DataAccessException, ObjectNotExistException
    {
	   List clist=taskService.getChildType(Long.parseLong("0"));
		ctm=new NewsManageTreeModel(clist,taskService);
		tree.setModel(ctm);
		tree.setTreeitemRenderer(new TreeitemRenderer(){
			public void render(Treeitem item, Object data) throws Exception {
				if(data instanceof WkTExtractask)
				{
				WkTExtractask et=(WkTExtractask)data;
				item.setValue(et);
				if(et.getKeName().length()>5)
				{
					item.setLabel(et.getKeName().substring(0, 5)+"...");
					item.setTooltiptext(et.getKeName());
				}
				else
				{
				item.setLabel(et.getKeName());
				}
				item.setOpen(true);
				List elist=taskService.findAllTaskOrder();
				if(elist != null && elist.size() != 0)
				{
					WkTExtractask firsttask = (WkTExtractask)elist.get(0);
					if(et.getKeId().toString().trim().equals(firsttask.getKeId().toString().trim()))
						item.setSelected(true);
				}
				}
				else if(data instanceof WkTChanel)
				{
					WkTChanel tt=(WkTChanel)data;
					item.setValue(tt);
					if(tt.getKcName().length()>5)
					{
						item.setLabel(tt.getKcName().substring(0, 5)+"...");
						item.setTooltiptext(tt.getKcName());
					}
					else
					{
						item.setLabel(tt.getKcName());
					}
					
					item.setOpen(true);
				}				
				}
			});
		
	}
	/**
	 * 栏目树单击事件监听
	 */
	public class TreeEventListener implements EventListener {

		public void onEvent(Event event) throws Exception {
            Treeitem it = tree.getSelectedItem();
            if(it.getValue() instanceof WkTChanel)
            {
            	Messagebox.show("请选择任务名称");
            }
            else  if(it.getValue() instanceof WkTExtractask)
            {
            WkTExtractask n = (WkTExtractask) it.getValue();
                windowLoad(n);
            }
	}
	}
	
	/**
	 * 重新加载左侧栏目树
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
	public void updateTree() throws DataAccessException, ObjectNotExistException
    {
        inittree();
    }
}
