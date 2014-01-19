package com.uniwin.webkey.infoExtra.newspub;

import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;

public class NewspubListWindowNew extends Window implements AfterCompose {
	private static final long serialVersionUID = 2223419390596150992L;
	IAuthManager authManager = (IAuthManager) SpringUtil.getBean("authManager");
	TaskService taskService = (TaskService) SpringUtil.getBean("taskService");
	NewspubEditWindowNew npWindow;// 发布信息初始化窗口
	private Center infPubCen;
	private Tree tree;
	private WkTWebsite website;
	private Users user;
	NewsTreeModel ntm;// 树的模型组件
	private Session currSession = Sessions.getCurrent();
	private List closeList = new ArrayList();
	private List taskList = new ArrayList();
	private List alist = new ArrayList();
	Session session;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users) currSession.getAttribute("users");
		website = (WkTWebsite) currSession.getAttribute("domain_defult");
		tree.getPagingChild().setMold("os");
		tree.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Treeitem it = tree.getSelectedItem();
				if (it.getValue() instanceof WkTExtractask) {//当点击一个任务节点时，初始该任务下各个tab的列表
					WkTExtractask etask = (WkTExtractask) it.getValue();
					if (it.getValue() instanceof WkTExtractask) {
						infPubCen.setTitle(org.zkoss.util.resource.Labels
								.getLabel("chanel.ui.operationChanelName")
								+ " : " + etask.getKeName());
						infPubCen.getChildren().clear();
						npWindow = (NewspubEditWindowNew) Executions
								.createComponents(
										"/apps/infoExtra/content/newspubnew/pub.zul",
										infPubCen, null);
						npWindow.setApplyTaskList(taskList);
						npWindow.setTree(tree);
						npWindow.initWindow(etask);
					}
				} else {//当点击根文件夹或其他频道（除了叶子节点是任务，其余都是频道），初始该频道下所有任务的各个tab下的列表
					WkTChanel chanel = (WkTChanel) it.getValue();
					infPubCen.setTitle(org.zkoss.util.resource.Labels
							.getLabel("chanel.ui.operationChanelName")
							+ " : "
							+ chanel.getKcName());
					infPubCen.getChildren().clear();
					npWindow = (NewspubEditWindowNew) Executions
							.createComponents(
									"/apps/infoExtra/content/newspubnew/pub.zul",
									infPubCen, null);
					npWindow.setApplyTaskList(taskList);
					npWindow.setTree(tree);
					npWindow.chanelInitWindow(chanel,alist);
				}
			}
		});
		try {
			inittree();
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (ObjectNotExistException e) {
			e.printStackTrace();
		}
		if (tree.getSelectedItem() != null) {
			if (tree.getSelectedItem().getValue() instanceof WkTExtractask) {
				WkTExtractask etask = (WkTExtractask) tree.getSelectedItem()
						.getValue();
				windowLoad(etask);
			}
		}
		//初始各个tab下属于所有任务栏目的当前用户名下的相应讯息
		windowLoadAll(taskList);
	}

	/**
	 * 加载发布信息窗口
	 * @param chanel
	 */
	public void windowLoad(WkTExtractask task) {
		if (task == null) {
			return;
		}
		infPubCen.setTitle(org.zkoss.util.resource.Labels
				.getLabel("chanel.ui.operationChanelName")
				+ " : "
				+ task.getKeName());
		infPubCen.getChildren().clear();
		npWindow = (NewspubEditWindowNew) Executions.createComponents(
				"/apps/infoExtra/content/newspubnew/pub.zul", infPubCen, null);
		npWindow.initWindow(task);
	}

	/**
	 * 加载左侧栏目树，当前用户不具有发布权限的栏目不可用
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
	public void inittree() throws DataAccessException, ObjectNotExistException {
		List nlist = taskService.getChildType(Long.parseLong("0"));
		ntm = new NewsTreeModel(nlist, taskService);
		tree.setModel(ntm);
		tree.setTreeitemRenderer(new TreeitemRenderer() {
			public void render(Treeitem item, Object data) throws Exception {
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
							"FB");
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
		for (int i = 0; i < closeList.size(); i++) {
			Treeitem item = (Treeitem) closeList.get(i);
			item.setOpen(false);
		}
	}

	private void windowLoadAll(List task) {
		if (task.size() == 0) {
			return;
		}
		// infPubCen.setTitle("所有信息");
		infPubCen.getChildren().clear();
		npWindow = (NewspubEditWindowNew) Executions.createComponents(
				"/apps/infoExtra/content/newspubnew/pub.zul", infPubCen, null);
		npWindow.setApplyTaskList(task);
		npWindow.initAllWindow(task);

	}

	/**
	 * 重新加载左侧栏目树
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
	public void updateTree() throws DataAccessException,
			ObjectNotExistException {
		inittree();
	}

}
