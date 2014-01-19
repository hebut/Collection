package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SubjectTermList extends Window implements AfterCompose {

	private static final long serialVersionUID = -7046764364684950338L;

	private WkTSubjectTermSort sort;
	// private Listbox subjectTermList;
	private Center treeCen;
	private Tree subjectTermTree;

	private SubjectTermService subjectTermService;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	public void initWin(final WkTSubjectTermSort sort) {
		this.sort = sort;
		List<WkTSubjectTerm> wList = subjectTermService.findBySortId(
				sort.getKsId(), 0);
		treeCen.getChildren().clear();
		SubjectTermTreeModel treeModel = new SubjectTermTreeModel(wList,
				subjectTermService);
		subjectTermTree.setModel(treeModel);
		subjectTermTree.setTreeitemRenderer(new TreeitemRenderer() {
			public void render(Treeitem item, Object obj) throws Exception {
				final WkTSubjectTerm w = (WkTSubjectTerm) obj;
				item.setValue(w);
				Treecell treecell1 = new Treecell(w.getKiName());
				Treecell treecell2 = new Treecell(w.getKiCode());
				Treecell treecell3 = new Treecell(w.getKiIssue());
				Menupopup pop = new Menupopup();
				pop.setId(item + "");
				Menu menu = new Menu();
				menu.setLabel("新建主题词");
				menu.setImage("/images/content/19.gif");
				Menupopup pop1 = new Menupopup();
				Menuitem cmenu1 = new Menuitem();
				cmenu1.setLabel("单条新建");
				cmenu1.setImage("/images/content/issue_ico.gif");
				Menuitem cmenu2 = new Menuitem();
				cmenu2.setLabel("批量新建");
				cmenu2.setVisible(true);
				cmenu2.setImage("/images/content/pl.gif");
				Menuitem menu2 = new Menuitem();
				menu2.setLabel("编辑主题词");
				menu2.setImage("/images/content/05.gif");
				Menuitem menu3 = new Menuitem();
				menu3.setLabel("删除主题词");
				menu3.setImage("/images/content/del.gif");
				Menuseparator sep = new Menuseparator();
				final Menuitem menu4 = new Menuitem();
				menu4.setLabel("排序");
				menu4.setImage("/images/content/order.jpg");
				pop1.appendChild(cmenu1);
				pop1.appendChild(cmenu2);
				menu.appendChild(pop1);
				pop.appendChild(menu);
				pop.appendChild(menu2);
				pop.appendChild(menu3);
				pop.appendChild(sep);
				pop.appendChild(menu4);
				treecell1.setContext(item + "");
				treecell1.appendChild(pop);
				Treerow treerow = new Treerow();
				treerow.appendChild(treecell1);
				treerow.appendChild(treecell2);
				treerow.appendChild(treecell3);
				item.appendChild(treerow);
				item.setOpen(false);
				cmenu1.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						if (subjectTermTree.getSelectedItem() != null) {
							WkTSubjectTerm w1 = (WkTSubjectTerm) subjectTermTree
									.getSelectedItem().getValue();
							NewSubjectTermOne wn = (NewSubjectTermOne) Executions
									.createComponents(
											"/apps/infoExtra/content/subjectterm/newSubjectTermOne.zul",
											null, null);
							wn.initWin(sort, w1);
							wn.doHighlighted();
							wn.addEventListener(Events.ON_CHANGE,
									new EventListener() {
										public void onEvent(Event arg0)
												throws Exception {
											initWin(sort);
										}
									});
						}
					}
				});

				cmenu2.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						if (subjectTermTree.getSelectedItem() != null) {
							NewPSubjectTerm wm = (NewPSubjectTerm) Executions
									.createComponents(
											"/apps/infoExtra/content/subjectterm/newPSubjectTerm.zul",
											null, null);
							wm.initWin(
									sort,
									subjectTermTree.getSelectedItem() == null ? null
											: (WkTSubjectTerm) subjectTermTree
													.getSelectedItem()
													.getValue());
							wm.doHighlighted();
							wm.addEventListener(Events.ON_CHANGE,
									new EventListener() {
										public void onEvent(Event arg0)
												throws Exception {
											initWin(sort);
										}
									});
						}
					}
				});

				menu2.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						if (subjectTermTree.getSelectedItem() != null) {
							WkTSubjectTerm w4 = (WkTSubjectTerm) subjectTermTree
									.getSelectedItem().getValue();
							SubjectTermEdit de = (SubjectTermEdit) Executions
									.createComponents(
											"/apps/infoExtra/content/subjectterm/subjecttermedit.zul",
											null, null);
							de.initWin(w4);
							de.doHighlighted();
							de.addEventListener(Events.ON_CHANGE,
									new EventListener() {
										public void onEvent(Event arg0)
												throws Exception {
											initWin(sort);
										}
									});
						}
					}
				});

				menu3.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						if (subjectTermTree.getSelectedItem() != null) {
							WkTSubjectTerm w3 = (WkTSubjectTerm) subjectTermTree
									.getSelectedItem().getValue();
							List<WkTSubjectTerm> list = subjectTermService
									.findByPid(w3.getKstId());
							if (list.size() > 0) {
								Messagebox.show("该分类存在子类不能删除");
							} else {
								try {
									if (Messagebox.show("确定删除选中的主题词？", "确认",
											Messagebox.OK | Messagebox.CANCEL,
											Messagebox.INFORMATION) == Messagebox.OK) {
										subjectTermService.delete(w3);
										Messagebox.show("该分类删除成功");
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								initWin(sort);
							}
						}
					}
				});

				menu4.addEventListener(Events.ON_CLICK, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						if (subjectTermTree.getSelectedItem() != null) {
							WkTSubjectTerm w5 = (WkTSubjectTerm) subjectTermTree
									.getSelectedItem().getValue();
							SbjectTermPX de5 = (SbjectTermPX) Executions
									.createComponents(
											"/apps/infoExtra/content/subjectterm/subjecttermPX.zul",
											null, null);
							de5.initWindow(sort, w5);
							de5.doHighlighted();
							de5.addEventListener(Events.ON_CHANGE,
									new EventListener() {
										public void onEvent(Event arg0)
												throws Exception {
											initWin(sort);
										}
									});
						}

					}
				});

			}

		});
		treeCen.appendChild(subjectTermTree);

		// subjectTermList.setModel(new ListModelList(wList));
		// subjectTermList.setItemRenderer(new ListitemRenderer() {
		// public void render(Listitem item, Object data) throws Exception {
		// WkTSubjectTerm subjectTerm = (WkTSubjectTerm) data;
		// item.setValue(subjectTerm);
		// Listcell c0 = new Listcell();
		// Listcell c1 = new Listcell(subjectTerm.getKiName());
		// Listcell c2 = new Listcell(subjectTerm.getKiCode());
		// Listcell c3 = new Listcell(subjectTerm.getKiIssue());
		// item.appendChild(c0);
		// item.appendChild(c1);
		// item.appendChild(c2);
		// item.appendChild(c3);
		// }
		// });
	}

	public void onClick$add() throws InterruptedException {
		if (sort == null) {
			Messagebox.show("请从菜单中选择分类！");
			return;
		}
		NewSubjectTermOne w = (NewSubjectTermOne) Executions.createComponents(
				"/apps/infoExtra/content/subjectterm/newSubjectTermOne.zul",
				null, null);
		w.doHighlighted();
		w.initWin(sort, subjectTermTree.getSelectedItem() == null ? null
				: (WkTSubjectTerm) subjectTermTree.getSelectedItem().getValue());// 如果第二个参数为null
		w.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				initWin(sort);
			}
		});
	}

	public void onClick$del() throws InterruptedException {
		if (subjectTermTree.getSelectedItems() == null) {
			Messagebox.show("请选择要删除的主题词！");
			return;
		} else {
			if (Messagebox.show("确定删除选中的主题词？", "确认", Messagebox.OK
					| Messagebox.CANCEL, Messagebox.INFORMATION) == Messagebox.OK) {
				for (Object o : subjectTermTree.getSelectedItems()) {
					WkTSubjectTerm st = (WkTSubjectTerm) ((Treeitem) o)
							.getValue();
					List<WkTSubjectTerm> wlist = subjectTermService
							.findByPid(st.getKstId());
					if (wlist != null && wlist.size() != 0) {
						continue;
					}
					subjectTermService.delete(st);
				}
				initWin(sort);
			}
		}
	}

	public void onClick$padd() {
		if (sort == null) {
			return;
		}
		NewPSubjectTerm w = (NewPSubjectTerm) Executions.createComponents(
				"/apps/infoExtra/content/subjectterm/newPSubjectTerm.zul",
				null, null);
		w.doHighlighted();
		w.initWin(sort, subjectTermTree.getSelectedItem() == null ? null
				: (WkTSubjectTerm) subjectTermTree.getSelectedItem().getValue());
		w.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				initWin(sort);
			}
		});
	}

	public void refreshList() {
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
}
