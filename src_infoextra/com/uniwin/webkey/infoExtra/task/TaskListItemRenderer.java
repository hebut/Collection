package com.uniwin.webkey.infoExtra.task;

/**
 * <li>分类的列表显呈器
 */

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.cms.model.WkTChanel;

public class TaskListItemRenderer implements ListitemRenderer {

	public void render(Listitem item, Object data) throws Exception {
		WkTChanel task=(WkTChanel)data;
	  item.setValue(task);
	  item.setLabel(task.getKcName());
	  item.setHeight("25px");
	  item.setDraggable("true");
	  item.setDroppable("true");
	  item.addEventListener(Events.ON_DROP, new EventListener(){
			public void onEvent(Event event) throws Exception {
				DropEvent de=(DropEvent)event;
				Component self=(Component)event.getTarget();	
				Listitem dragged=(Listitem)de.getDragged();						 
				if (self instanceof Listitem) {								
					self.getParent().insertBefore(dragged, self.getNextSibling());								
				} else {
					self.appendChild(dragged);
				}		
			}			
		});
	}

}
