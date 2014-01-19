/*package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IAlarmclockManager;
import com.uniwin.webkey.core.model.Alarmclock;
import com.uniwin.webkey.core.model.AlarmclockEx;
import com.uniwin.webkey.core.model.AlarmclockM;
import com.uniwin.webkey.system.ui.WorkbenchWin;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class AlarmclockUpdateWin extends Window implements AfterCompose {
	private AlarmclockEx alarmclockEx;

	private String description[];

	private List<AlarmclockM> alarmclockMs = new ArrayList<AlarmclockM>();

	int rowIndex = 0;

	private Listbox events;

	private Datebox toTime;

	private Textbox eventName;

	private Radio start, stop;

	private IAlarmclockManager alarmclockManager = (IAlarmclockManager) SpringUtil
			.getBean("alarmclockManager");

	private List<Textbox> textboxs = new ArrayList<Textbox>();

	// private Panel panel;

	public void afterCompose() {
		alarmclockEx = (AlarmclockEx) Sessions.getCurrent().getAttribute(
				"alarmclockEx");
		toTime = (Datebox) this.getFellow("toTime");
		//在后台为修改页面赋时间值（alarmclockEx对象中toTime是字符串类型，需要手动赋值）
		toTime.setValue(DateUtil.strToDate(alarmclockEx.getTotime(),
				"yyyy-MM-dd HH:mm:ss"));
		eventName = (Textbox) this.getFellow("eventName");
		events = (Listbox) this.getFellow("events");
		start = (Radio) this.getFellow("start");
		stop = (Radio) this.getFellow("stop");
		getAlltextbox();
	}

	*//**
	 * 获取所有的事件项及多个TextBox
	 *//*
	public void getAlltextbox() {
		for (int i = 1; i <= this.rowIndex; i++) {
			Textbox box = (Textbox) this.getFellow("item" + i);
			textboxs.add(box);
		}
	}

	*//**
	 *split截取事件项为数组显示到多个TextBox中
	 *//*
	public AlarmclockUpdateWin() {
		alarmclockEx = (AlarmclockEx) Sessions.getCurrent().getAttribute(
				"alarmclockEx");
		description = alarmclockEx.getEventDescription().split("@@");
		int i = 0;
		AlarmclockM alTemp = null;
		for (String des : description) {
			++i;
			rowIndex++;
			alTemp = new AlarmclockM();
			alTemp.setId(i + "");
			alTemp.setValue(des);
			alarmclockMs.add(alTemp);
		}
		if (alarmclockMs.size() == 0) {
			this.addDeafaultRow();
		}
	}

	*//**
	 * 修改信息
	 *//*
	public void updateAlarmclock() {

		Alarmclock alarmc = new Alarmclock();
		alarmc.setId(alarmclockEx.getId());

		alarmc.setTotime(DateUtil.dateToStr(toTime.getValue(),
				"yyyy-MM-dd HH:mm:ss"));
		alarmc.setEventName(eventName.getText());
		alarmc.setEventDescription(this.getDescirption());
		alarmc.setUserId(FrameCommonDate.getUser().getUserId());
		if (start.isChecked()) {
			alarmc.setState(Integer.parseInt(start.getValue()));
		}
		if (stop.isChecked()) {
			alarmc.setState(Integer.parseInt(stop.getValue()));
		}
		try {

			alarmclockManager.update(alarmc);
			AlarmclockEx alarmclockEx = null;
			alarmclockEx = new AlarmclockEx();
			alarmclockEx.setId(alarmc.getId());
			alarmclockEx.setState(alarmc.getState());
			alarmclockEx.setEventName(alarmc.getEventName());
			alarmclockEx.setEventDescription(alarmc.getEventDescription());
			alarmclockEx.setTotime(alarmc.getTotime());
			Sessions.getCurrent().setAttribute("alarmclockEx", alarmclockEx);
			Sessions.getCurrent().setAttribute("settime", toTime.getValue());
	
			((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
					.reOpenTab();
			this.detach();
			Messagebox.show(org.zkoss.util.resource.Labels
					.getLabel("system.commom.ui.changesuccess"),
					org.zkoss.util.resource.Labels
							.getLabel("system.commom.ui.prompt"),
					Messagebox.OK, Messagebox.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Messagebox.show(org.zkoss.util.resource.Labels
						.getLabel("system.commom.ui.changefailed"),
						org.zkoss.util.resource.Labels
								.getLabel("system.commom.ui.prompt"),
						Messagebox.OK, Messagebox.INFORMATION);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	*//**
	 * 刪除textBox
	 * 
	 * @param event
	 *           
	 *//*
	public void delRow(Event event) {
		Listitem listitem = (Listitem) event.getTarget().getParent()
				.getParent();

		if (textboxs.size() == 1) {
			try {
				Textbox text = (Textbox) this
						.getFellow(textboxs.get(0).getId());
				text.setText("");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		events.removeChild(listitem);
		textboxs.remove((Textbox) ((Listcell) listitem.getChildren().get(1))
				.getFirstChild());
	}

	*//**
	 * 刪除textBox的数据
	 *//*
	public void removeTextboxs() {
		List<Textbox> removeIds = new Arraylist<Textbox>();
		for (Textbox box : textboxs) {
			try {
				this.getFellow(box.getId());
			} catch (Exception e) {
				removeIds.add(box);
			}

		}
		for (Textbox textbox : removeIds) {
			textboxs.remove(textbox);
		}
	}

	*//**
	 * 添加默认行
	 *//*
	public void addDeafaultRow() {

		Listitem item = new Listitem();
		Listcell cell1 = new Listcell();
		Listcell cell2 = new Listcell();
		Listcell cell3 = new Listcell();
		Textbox textbox = new Textbox();
		Image delIm = new Image();
		rowIndex++;
		cell1.setLabel(rowIndex + "");
		textbox.setRows(2);
		textbox.setCols(50);
		textbox.setId("item" + rowIndex);
		textbox.setWidth("100%");
		textboxs.add(textbox);
		delIm.setType("delList");
		delIm.addEventListener("onClick", new EventListener() {

			public void onEvent(Event arg0) throws Exception {
				delRow(arg0);
			}
		});
		cell3.appendChild(delIm);
		cell2.appendChild(textbox);
		item.appendChild(cell1);
		item.appendChild(cell2);
		item.appendChild(cell3);
		events.appendChild(item);
	}

	*//**
	 * 增加多行
	 *//*
	public void addRow() {

		Listitem item = new Listitem();
		Listcell cell1 = new Listcell();
		Listcell cell2 = new Listcell();
		Listcell cell3 = new Listcell();
		Textbox textbox = new Textbox();
		Image delIm = new Image();
		++rowIndex;
		cell1.setLabel(rowIndex + "");
		textbox.setRows(2);
		textbox.setCols(50);
		textbox.setId("item" + (rowIndex + 1));
		textbox.setWidth("100%");
		textboxs.add(textbox);
		delIm.setType("delList");
		delIm.addEventListener("onClick", new EventListener() {

			public void onEvent(Event arg0) throws Exception {
				delRow(arg0);
			}
		});
		cell3.appendChild(delIm);
		cell2.appendChild(textbox);
		item.appendChild(cell1);
		item.appendChild(cell2);
		item.appendChild(cell3);
		events.appendChild(item);
	}

	*//**
	 * 得到事件项的描述
	 * 
	 * @return
	 *//*
	public String getDescirption() {
		String descri = "";
		for (Textbox textbox : textboxs) {
			if (textbox != null && !textbox.getText().trim().equals("")) {
				descri += textbox.getText() + "@@";
			}
		}

		if (descri.length() > 0) {
			descri = descri.substring(0, descri.length() - 2);
		}
		return descri;
	}

	public AlarmclockEx getAlarmclockEx() {
		return alarmclockEx;
	}

	public void setAlarmclockEx(AlarmclockEx alarmclockEx) {
		this.alarmclockEx = alarmclockEx;
	}

	public String[] getDescription() {
		return description;
	}

	public void setDescription(String[] description) {
		this.description = description;
	}

	public List<AlarmclockM> getAlarmclockMs() {
		return alarmclockMs;
	}

	public void setAlarmclockMs(List<AlarmclockM> alarmclockMs) {
		this.alarmclockMs = alarmclockMs;
	}

}
*/