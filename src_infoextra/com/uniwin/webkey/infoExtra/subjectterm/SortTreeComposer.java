package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SortTreeComposer extends Window implements AfterCompose {

	private static final long serialVersionUID = 1013834011437592484L;
	private Tree sortTree;
	private Center sortCenter;
	private int i = 0;// 加载树时判断是不是树的第一项
	private Menupopup menupopupSort;
	private Menuitem menuitemAddSort, menuitemDelSort, menuitemEditSort;

	private SubjectTermList subjectTermList;
	private SubjectTermSortService subjectTermSortService;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();
		sortTree.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				if (sortTree.getSelectedItem() != null) {
					WkTSubjectTermSort infoSort = (WkTSubjectTermSort) sortTree
							.getSelectedItem().getValue();
					windowsLoad(infoSort);
				}
			}
		});
		sortTree.getPagingChild().setMold("os");

		sortTree.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				menupopupSort.close();
			}
		});

		menuitemAddSort.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				addSujectTerm();
			}
		});

		menuitemDelSort.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				deleteSubjectTerm();
			}
		});

		menuitemEditSort.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				editSujectTerm();
			}
		});

	}

	private void deleteSubjectTerm() throws InterruptedException {
		WkTSubjectTermSort w = (WkTSubjectTermSort) sortTree.getSelectedItem()
				.getValue();
		if (w.getKsPid() == 0) {
			Messagebox.show("不能删除根文件夹！");
		} else {
			List<WkTSubjectTermSort> deList = subjectTermSortService
					.findByPid(w.getKsId());
			if (deList.size() != 0) {
				Messagebox.show("该分类存在子分类,无法删除!");
			} else {
				subjectTermSortService.delete(w);
				Messagebox.show("删除成功！", "Information", Messagebox.OK,
						Messagebox.INFORMATION);
				initTree();
			}
		}
	}

	private void addSujectTerm() {
		NewSubjectTermSortWindow newSort = (NewSubjectTermSortWindow) Executions
				.createComponents(
						"/apps/infoExtra/content/subjectterm/newsort.zul",
						null, null);
		newSort.doHighlighted();
		if (sortTree.getSelectedItem() == null) {
			newSort.initWindow(null);
		} else {
			newSort.initWindow((WkTSubjectTermSort) sortTree.getSelectedItem()
					.getValue());
		}
		newSort.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				initTree();
			}
		});
	}

	private void editSujectTerm() {
		if(sortTree.getSelectedItem() == null || sortTree.getSelectedItem().getValue() == null){
			return;
		}
		SubjectSortEdit sort = (SubjectSortEdit) Executions.createComponents(
				"/apps/infoExtra/content/subjectterm/sortedit.zul", null, null);
		sort.initWin((WkTSubjectTermSort)sortTree.getSelectedItem().getValue());
		sort.doHighlighted();
		sort.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				initTree();
			}
		});
	}

	private void initTree() {
		sortTree.setTreeitemRenderer(new TreeitemRenderer() {
			public void render(Treeitem item, Object obj) throws Exception {
				final WkTSubjectTermSort w = (WkTSubjectTermSort) obj;
				item.setValue(w);
				item.setLabel(w.getKsName());
				item.setOpen(true);
				i++;
				if (i == 1) {
					item.setSelected(true);
				}
			}
		});

		List<WkTSubjectTermSort> list = subjectTermSortService.findByPid(0);
		sortTree.setModel(new TreeData(list, subjectTermSortService));
		if (sortTree.getSelectedItem() != null) {
			WkTSubjectTermSort sort = (WkTSubjectTermSort) sortTree
					.getSelectedItem().getValue();
			windowsLoad(sort);
		}
	}

	public void windowsLoad(WkTSubjectTermSort subjectTermSort) {
		if (subjectTermSort != null) {
			sortCenter.setTitle(subjectTermSort.getKsName());
			sortCenter.getChildren().clear();
			subjectTermList = (SubjectTermList) Executions.createComponents(
					"/apps/infoExtra/content/subjectterm/subjectTermList.zul",
					sortCenter, null);
			subjectTermList.initWin(subjectTermSort);
		}
	}
}
