package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.infoExtra.itf.SubjectTermSortService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SubjectTermSortListbox extends Listbox implements AfterCompose {

	private static final long serialVersionUID = 34290849246690502L;
	private SubjectTermSortService subjectTermSortService;
	private ListModelList wmodelList;

	public void afterCompose() {
		Components.wireVariables(this, this);
	}

	public void addTaskListBoxItem(ListModelList cml, Integer pid, int dep,
			WkTSubjectTermSort tt) {
		List<WkTSubjectTermSort> wList = null;
		if (tt == null) {
			wList = subjectTermSortService.findByPid(pid);
		} else {
			wList = subjectTermSortService.getChildSorttype(pid, tt.getKsId());
		}
		for (int i = 0; i < wList.size(); i++) {
			WkTSubjectTermSort w = wList.get(i);
			w.setDep(dep);
			cml.add(w);
			addTaskListBoxItem(cml, w.getKsId(), dep + 1, tt);
		}
	}

	public void initAllTaskSortSelect(final WkTSubjectTermSort arg,
			final WkTSubjectTermSort a) {
		wmodelList = new ListModelList();
		WkTSubjectTermSort t = new WkTSubjectTermSort();
		t.setDep(0);
		@SuppressWarnings("unchecked")
		final List<WkTSubjectTermSort> wlist = subjectTermSortService
				.findAll(WkTSubjectTermSort.class);
		if (wlist == null || wlist.size() == 0) {
			addTaskListBoxItem(wmodelList, 0, 0, arg);
		} else {
			addTaskListBoxItem(wmodelList, 0, 0, null);
		}
		this.setModel(wmodelList);
		this.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				WkTSubjectTermSort w = (WkTSubjectTermSort) data;
				item.setValue(w);
				int dep = w.getDep();
				String bla = "";
				while (dep > 0) {
					bla += "　";
					dep--;
				}
				if (arg != null
						&& w.getKsId().intValue() == arg.getKsId().intValue()) {
					item.setSelected(true);
				}
				if (a != null
						&& w.getKsId().intValue() == a.getKsId().intValue()) {
					item.setStyle("color:#e0e0e0");
				}
				item.setLabel(bla + w.getKsName());
			}
		});
	}

}
