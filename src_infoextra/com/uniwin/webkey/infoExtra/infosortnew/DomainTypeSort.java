package com.uniwin.webkey.infoExtra.infosortnew;

/**
 * <li>分类排序
 */

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.MLogService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;
import com.uniwin.webkey.infoExtra.model.WkTMlog;


public class DomainTypeSort extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Users user; 
	Listbox domainList;
	ListModelList domainModel;
	InfoDomainService infodomainService;
	private MLogService mlogService=(MLogService)SpringUtil.getBean("info_mlogService");
	WkTInfoDomain  domain;
	WkTInfoSort infoSort;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);	
		user=(Users)Sessions.getCurrent().getAttribute("users");	
		//domainList.setItemRenderer(new DomainListItemRenderer());
		this.domainList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, Object arg1) throws Exception {
				WkTInfoDomain domain=(WkTInfoDomain)arg1;
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
	public WkTInfoDomain getDomain() {
		return domain;
	}
	public void initWindow(WkTInfoSort infoSort,WkTInfoDomain domain) {		
		this.infoSort = infoSort;
		this.domain = domain;
		reloadList();
	}
	
	/**保存排序结果*/
	public void onClick$submit(){
		List itemList=this.domainList.getItems();
		StringBuffer sb=new StringBuffer("编辑分类顺序,ids:");
		List<WkTInfoDomain>	infolist = new ArrayList<WkTInfoDomain>();
		for (Object o : this.domainList.getItems()) {
			Listitem item = (Listitem)o;
			WkTInfoDomain c = (WkTInfoDomain)item.getValue();
			if (c == null)
				continue;
			infolist.add(c);
		}
		for(int i=0;i<infolist.size();i++){
			WkTInfoDomain c1 = infolist.get(i);
			int j=i+1;
			c1.setKiOrder(Long.parseLong(j+""));
			infodomainService.update(c1);
			sb.append(c1.getKiId()+",");
		}
		mlogService.saveMLog(WkTMlog.FUNC_CMS, sb.toString(),user);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}
	
	/**
	 * <li>功能描述：加载要排序的分类列表
	 */
	private void reloadList(){
		 domainModel = new ListModelList();
		 List<WkTInfoDomain> plist = infodomainService.findByksidANDkipid(infoSort.getKsId(), domain.getKiPid());
		 domainModel.addAll(plist);	
	     domainList.setModel(domainModel);
	}
	/**
	 * 重置
	 */
	public void onClick$reset(){
		initWindow(infoSort,getDomain());		
	}
	
	public void onClick$close(){
		this.detach();
	}
}
