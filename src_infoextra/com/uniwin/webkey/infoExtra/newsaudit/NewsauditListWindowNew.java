package com.uniwin.webkey.infoExtra.newsaudit;
/**
 * <li>封装的基础左侧树组件，主要封装了对信息审核页面中标签页的操作,供继承之用
 * @2010-3-17
 * @author whm
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditEditWindow;
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditTreeModel;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.cms.model.WkTChanel;



public class NewsauditListWindowNew extends Window implements AfterCompose
{
	
	private static final long serialVersionUID = 1L;
	//栏目树组件
	Tree tree;	
	//栏目信息数据访问接口
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	IAuthManager authManager = (IAuthManager)SpringUtil.getBean("authManager");
	//树的模型组件
	NewsauditTreeModel natm;
	private Center infauditCen;
	private Session currSession = Sessions.getCurrent();
	//信息审核初始化窗口
	NewsauditEditWindowNew naWindow;
	Panel newsauditpanel;
	Users user;
	
	private WkTWebsite website;
	
	Listbox orinfolistbox,auditListbox,readListbox,pubListbox,writeListbox;
	ListModelList orinfoListModel,pubListModel,readListModel,auditListModel,writeListModel;
	
	private List<Long> taskList = new ArrayList<Long>();
	private List alist = new ArrayList();
	private List closeList = new ArrayList();
	
	public void afterCompose()
    {	
		  Components.wireVariables(this, this);
	      Components.addForwards(this, this);
	      user=(Users)Sessions.getCurrent().getAttribute("users");
		  website = (WkTWebsite)currSession.getAttribute("domain_defult");
	      
		//点击左侧树的响应事件
		tree.addEventListener(Events.ON_SELECT, new EventListener(){
			public void onEvent(Event event) throws Exception {
					 Treeitem it=tree.getSelectedItem();
					 if(it.getValue() instanceof WkTExtractask)
					 {
					 WkTExtractask etask=(WkTExtractask) it.getValue();
					 if(it.getValue() instanceof WkTExtractask)
					 {infauditCen.setTitle(org.zkoss.util.resource.Labels
				                .getLabel("chanel.ui.operationChanelName")+" : "+etask.getKeName());
					 	infauditCen.getChildren().clear();
						naWindow = (NewsauditEditWindowNew)Executions.createComponents("/apps/infoExtra/content/newsauditnew/audit.zul",infauditCen, null);
						naWindow.setApplyList(taskList);
						naWindow.initWindow(etask);}
					 }
					 else
					 {
//						 Messagebox.show("请选择底层任务节点！");
						 WkTChanel chanel = (WkTChanel)it.getValue();
						 infauditCen.setTitle(org.zkoss.util.resource.Labels.getLabel("chanel.ui.operationChanelName")+" : "+chanel.getKcName());
						 infauditCen.getChildren().clear();
						 naWindow = (NewsauditEditWindowNew)Executions.createComponents("/apps/infoExtra/content/newsauditnew/audit.zul",infauditCen, null);
						 naWindow.setApplyList(taskList);
						 naWindow.chanelInitWindow(chanel,alist);
					 }
				}			
				
		});
		tree.getPagingChild().setMold("os");
		inittree();
		//没用
		if (tree.getSelectedItem() != null) {
			if (tree.getSelectedItem().getValue() instanceof WkTExtractask) {
				WkTExtractask etask = (WkTExtractask) tree.getSelectedItem()
						.getValue();
				windowLoad(etask);
			}
		}
		//
		windowLoadAll(taskList);
	}
	
	/**
	 * 加载发布信息窗口
	 * @param chanel
	 */
	public void windowLoad(WkTExtractask task)
	{
		if(task==null)
		{
			return;
		}
		infauditCen.setTitle(org.zkoss.util.resource.Labels
                .getLabel("chanel.ui.operationChanelName")+" : "+task.getKeName());
		infauditCen.getChildren().clear();
		naWindow= (NewsauditEditWindowNew)Executions.createComponents("/apps/infoExtra/content/newsauditnew/audit.zul",infauditCen, null);
		naWindow.setTree(tree);
		naWindow.initWindow(task);
		}
	
	
	public void inittree()
	{
		List nlist=taskService.getChildType(Long.parseLong("0"));
		natm=new NewsauditTreeModel(nlist,taskService);
		tree.setModel(natm);
		tree.setTreeitemRenderer(new TreeitemRenderer()
		{public void render(Treeitem item, Object data) throws Exception {
			if (data instanceof WkTExtractask) {
				WkTExtractask et = (WkTExtractask) data;
				// 判断用户权限
				item.setValue(et);

				if (et.getKeName().length() > 5) {
					item.setLabel(et.getKeName().substring(0, 5) + "...");
					item.setTooltiptext(et.getKeName());
				} else {
					item.setLabel(et.getKeName());
				}

				alist = authManager.getWkTTaskByUWRandOperation(
						user.getUserId(), website.getKwId().intValue(),
						"SH");
				if (alist != null) {
					int count = 0;
					for (int i = 0; i < alist.size(); i++) {
						WkTExtractask etask = (WkTExtractask) alist.get(i);
						if (etask.getKeId().toString().trim()
								.equals(et.getKeId().toString().trim())) {
							count++;
						}
					}

					if (count == 0) {
						item.setDisabled(true);
					} else if (count != 0) {
						taskList.add(taskList.size(), et.getKeId());
						Executions.getCurrent().setAttribute("ppp",taskList);

					}
				}
			} else if (data instanceof WkTChanel) {
				WkTChanel tt = (WkTChanel) data;
				item.setValue(tt);
				item.setOpen(true);
				closeList.add(closeList.size(), item);
				if (tt.getKcName().length() > 5) {
					item.setLabel(tt.getKcName().substring(0, 5) + "...");
					item.setTooltiptext(tt.getKcName());
				} else {
					item.setLabel(tt.getKcName());
				}
				if (tt.getKcPid().toString().equals("0")) {
					item.setSelected(true);
				}
			}
		}
		});
		for(int i=0;i<closeList.size();i++)
		{
			Treeitem item=(Treeitem) closeList.get(i);
			item.setOpen(false);
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
	
	public void windowLoadAll(List<Long> taskList){
		if(taskList.size()==0)
		{
			return;
		}
//		infauditCen.setTitle("所有信息");
		infauditCen.getChildren().clear();
		naWindow= (NewsauditEditWindowNew)Executions.createComponents("/apps/infoExtra/content/newsauditnew/audit.zul",infauditCen, null);
		naWindow.setApplyList(taskList);
		naWindow.setTree(tree);
		naWindow.initAllWindow(taskList);
		
	}
	
}
