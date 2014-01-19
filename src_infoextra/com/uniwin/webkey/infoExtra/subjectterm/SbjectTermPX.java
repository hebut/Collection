package com.uniwin.webkey.infoExtra.subjectterm;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SubjectTermService;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTermSort;

public class SbjectTermPX extends Window implements AfterCompose {

	private static final long serialVersionUID = 6889673810843313475L;
	private Listbox subjecttermList;
	private WkTSubjectTermSort  sort;
	private WkTSubjectTerm subjectTerm;
	private SubjectTermService subjectTermService;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);	
		this.subjecttermList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, Object arg1) throws Exception {
				WkTSubjectTerm domain=(WkTSubjectTerm)arg1;
				arg0.setValue(arg1);
				arg0.setLabel(domain.getKiName());
				arg0.setDraggable(Boolean.TRUE.toString());
				arg0.setDroppable(Boolean.TRUE.toString());
				arg0.setHeight("30px");
				arg0.addEventListener(Events.ON_DROP, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						DropEvent event = (DropEvent) arg0;
						Component self = event.getTarget();
						Listitem dragged = (Listitem) event.getDragged();
						if (self instanceof Listitem) {
							self.getParent().insertBefore(dragged,
									self.getNextSibling());
						} else {
							self.appendChild(dragged);
						}
					}
				});
			}
		});
	}
	public WkTSubjectTerm getSubjectTerm() {
		return subjectTerm;
	}
	public void initWindow(WkTSubjectTermSort sort, WkTSubjectTerm subjectTerm) {		
		this.subjectTerm = subjectTerm;
		this.sort = sort;
		reloadList();
	}
	
	/**保存排序结果*/
	public void onClick$submit(){
		List itemList=this.subjecttermList.getItems();
		StringBuffer sb=new StringBuffer("编辑分类顺序,ids:");
		List<WkTSubjectTerm>	infolist = new ArrayList<WkTSubjectTerm>();
		for (Object o : this.subjecttermList.getItems()) {
			Listitem item = (Listitem)o;
			WkTSubjectTerm c = (WkTSubjectTerm)item.getValue();
			if (c == null)
				continue;
			infolist.add(c);
		}
		for(int i=0;i<infolist.size();i++){
			WkTSubjectTerm c1 = infolist.get(i);
			int j=i+1;
			c1.setKiOrder(Long.parseLong(j+""));
			subjectTermService.update(c1);
			sb.append(c1.getKstId()+",");
		}
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
	
	/**
	 * <li>功能描述：加载要排序的分类列表
	 */
	private void reloadList(){
		List<WkTSubjectTerm> plist = subjectTermService.findByksidANDkstPid(sort.getKsId(), subjectTerm.getKstPid());
		 subjecttermList.setModel(new ListModelList(plist));
	}
	/**
	 * 重置
	 */
	public void onClick$reset(){
		initWindow(sort, getSubjectTerm());		
	}
	
	public void onClick$close(){
		this.detach();
	}
}
