package com.uniwin.webkey.infoExtra.statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.service.UsersManagerImpl;
import com.uniwin.webkey.core.ui.Arraylist;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTLinkurl;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

public class StatisticsWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;

	Users user;
	Listbox inforlistbox, taskstalist;
	ListModelList inforlistmodel, tasklistmodel;
	Datebox db, db1,db2,db3;
	Textbox name;
	List ulist, wktTaskList;
	Listfooter bjts, shts, ext,indatabase;
	String d, b;
	int audit = 0, edit = 0, extracts = 0,indata=0;
	String sex, role;
	NewsServices newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users) Sessions.getCurrent().getAttribute("users");
		Date date = new Date();
		db1.setValue(date);
		db3.setValue(date);
		Date date1 = new Date();
		date1.setDate(1);
		db.setValue(date1);
		Date date2 = new Date();
		date2.setDate(0);
		db2.setValue(date2);
		LoadList();
		inforlistbox.setItemRenderer(new PopleStsListRenderer());
		taskstalist.setItemRenderer(new TaskStaListRenderer());
	}

	private void clearfooter1() {
		audit = edit =  0;
	}
	
	private void clearfooter2() {
	   extracts = 0;
	}
	
	private void clearfooter3() {
		indata=0;
		}

	public void LoadList() {
		ulist = newsService.findByRoleName("审核", "编辑");
		inforlistmodel = new ListModelList(ulist);
		inforlistbox.setModel(inforlistmodel);

		wktTaskList = newsService.findExtractTask(name.getValue());
		tasklistmodel = new ListModelList(wktTaskList);
		taskstalist.setModel(tasklistmodel);

	}

	public void onClick$sts1() {
		clearfooter1();
		inforlistbox.setItemRenderer(new PopleStsListRenderer());
		
	}
	
	public void onClick$sts2(){
		
		clearfooter2();
		clearfooter3();
		wktTaskList = newsService.findExtractTask(name.getValue());
		tasklistmodel = new ListModelList(wktTaskList);
		taskstalist.setModel(tasklistmodel);
		taskstalist.setItemRenderer(new TaskStaListRenderer1());
	}

	private void initfooter() {

		shts.setLabel("  " + audit);
		bjts.setLabel("  " + edit);
		ext.setLabel("  "+extracts);
		indatabase.setLabel("  "+indata);

	}

	class PopleStsListRenderer implements ListitemRenderer {
		public void render(Listitem item, Object data) throws Exception {
			Users u = (Users) data;
			item.setValue(u);
			item.setHeight("25px");
			Listcell c0 = new Listcell(item.getIndex() + 1 + "");
			Listcell c1 = new Listcell(u.getName());
			Listcell c2 = new Listcell();
			if (u.getKuSex().equals("1")) {
				c2 = new Listcell("男");
			} else if (u.getKuSex().equals("2")) {
				c2 = new Listcell("女");
			}
			Listcell c3 = new Listcell();

			List dlist = newsService.findDeptById(u.getOrganizationId());
			Organization o = (Organization) dlist.get(0);
			Listcell c4 = new Listcell(o.getName());
			d = ConvertUtil.convertDateAndTimeString(db.getValue());
			b = ConvertUtil.convertDateAndTimeString(db1.getValue());
			List ilist = newsService.getCountInfor(
					Long.parseLong(u.getUserId().toString()), "0", d, b);
			List alist = newsService.getCountAudit(
					Long.parseLong(u.getUserId().toString()), "0", d, b);
			if (alist.size() != 0 && ilist.size() != 0) {
				c3 = new Listcell("编辑/审核角色");
			} else if (alist.size() != 0) {
				c3 = new Listcell("审核角色");
			} else if (ilist.size() != 0) {
				c3 = new Listcell("编辑角色");
			} else {
				c3 = new Listcell("编辑/审核角色");
			}
			Listcell c5 = new Listcell(ilist.size() + "");

			Listcell c6 = new Listcell(alist.size() + "");
			audit += alist.size();
			edit += ilist.size();
			shts.setLabel("  " + audit);
			bjts.setLabel("  " + edit);
			item.appendChild(c0);
			item.appendChild(c1);
			item.appendChild(c2);
			item.appendChild(c3);
			item.appendChild(c4);
			item.appendChild(c5);
			item.appendChild(c6);
		}
	}

	class TaskStaListRenderer implements ListitemRenderer {

		public void render(Listitem item, Object data) throws Exception {
			WkTExtractask e = (WkTExtractask)data;
			item.setValue(e);
			item.setHeight("25px");
			Listcell c0 = new Listcell(item.getIndex() + 1 + "");
			Listcell c1 = new Listcell(e.getKeName());
			Listcell c2 = new Listcell(e.getKcId().toString());
			WkTChanel c = (WkTChanel)newsService.getChanel(e.getKcId());
			c2.setLabel(c.getKcName());
			String sb = e.getKeBeginurl();
			String sbt = sb.substring(0, sb.length()-1);
			Listcell c3 = new Listcell(sbt);
			Listcell c4 = new Listcell(e.getKeTime());
			Listcell c5 = new Listcell();
			Listcell c6 = new Listcell();
			List <WkTLinkurl> l = (List<WkTLinkurl>)newsService.countLinkurl(e.getKeId());
			c5.setLabel(l.size()+"");
			extracts += l.size();
			ext.setLabel("  " + extracts);
			List <WkTDistribute> d=newsService.getStatisDistri(e.getKeId());
			c6.setLabel(d.size()+"");
			indata+=d.size();
			indatabase.setLabel(""+indata);
			item.appendChild(c0);
			item.appendChild(c1);
			item.appendChild(c2);
			item.appendChild(c3);
			item.appendChild(c4);
			item.appendChild(c5);
			item.appendChild(c6);
		}
	}
	
	class TaskStaListRenderer1 implements ListitemRenderer {

		public void render(Listitem item, Object data) throws Exception {
			WkTExtractask e = (WkTExtractask)data;
			item.setValue(e);
			item.setHeight("25px");
			Listcell c0 = new Listcell(item.getIndex() + 1 + "");
			Listcell c1 = new Listcell(e.getKeName());
			Listcell c2 = new Listcell(e.getKcId().toString());
			WkTChanel c = (WkTChanel)newsService.getChanel(e.getKcId());
			c2.setLabel(c.getKcName());
			String sb = e.getKeBeginurl();
			String sbt = sb.substring(0, sb.length()-1);
			Listcell c3 = new Listcell(sbt);
			Listcell c4 = new Listcell(e.getKeTime());
			Listcell c5 = new Listcell();
			Listcell c6 = new Listcell();
			String d1 = ConvertUtil.convertDateAndTimeString(db2.getValue());
			String b1 = ConvertUtil.convertDateAndTimeString(db3.getValue());
			List l1 = newsService.countLinkurl1(e.getKeId(),d1,b1);
			List l2 = newsService.countLinkurl2(e.getKeId(),d1,b1);
			c5.setLabel(l1.size()+l2.size()+"");
			extracts += l1.size()+l2.size();
			ext.setLabel("  " + extracts);
			List <WkTDistribute> d=newsService.getStatisDistri1(e.getKeId(),d1,b1);
			c6.setLabel(d.size()+"");
			indata+=d.size();
			indatabase.setLabel(""+indata);
			item.appendChild(c0);
			item.appendChild(c1);
			item.appendChild(c2);
			item.appendChild(c3);
			item.appendChild(c4);
			item.appendChild(c5);
			item.appendChild(c6);
		}
	}

	public void onClick$expert() throws RowsExceededException, WriteException,
			IOException {
		// 标题样式
		WritableCellFormat wcf = null;
		WritableFont wf = new WritableFont(WritableFont.TIMES, 8,
				WritableFont.BOLD, false);// 最后一个为是否italic,即正常
		wcf = new WritableCellFormat(wf);
		// 对齐方式
		wcf.setAlignment(Alignment.CENTRE);

		// {
		// String fileName = "tongji.xls";
		// WritableWorkbook workbook;
		// String[] title = {"用户名","性别","角色","所属部门","编辑条数","审核条数"};
		// OutputStream ops = new FileOutputStream(fileName);
		// workbook = Workbook.createWorkbook(ops);
		// WritableSheet sheet = workbook.createSheet("工作量统计", 0);
		// jxl.write.Label label;
		// for (int i = 0; i < title.length; i++)
		// {
		// label = new jxl.write.Label(i, 0, title[i]);
		// label.setCellFormat(wcf);
		// sheet.addCell(label);
		// }
		// for(int i=0;i<ulist.size();i++)
		// {
		// Users u = (Users) ulist.get(i);
		// jxl.write.Label l1 = new jxl.write.Label(0, i + 1, u.getName());
		// sheet.addCell(l1);
		//
		// if(u.getKuSex().equals("1")) sex="男";
		// else if (u.getKuSex().equals("2")) sex="女";
		// jxl.write.Label l2 = new jxl.write.Label(1, i + 1, sex);
		// sheet.addCell(l2);
		// if(u.getKuAuditnum()!=0)
		// {
		// role="审核角色";
		// }
		// else if(u.getKuEditnum()!=0)
		// {
		// role="编辑角色";
		// }
		// else
		// {
		// role="编辑/审核角色";
		// }
		// jxl.write.Label l3 = new jxl.write.Label(2, i + 1, role);
		// sheet.addCell(l3);
		// List dlist=newsService.findDeptById(u.getOrganizationId());
		// Organization o=(Organization) dlist.get(0);
		// jxl.write.Label l4 = new jxl.write.Label(3, i + 1,o.getName());
		// sheet.addCell(l4);
		// jxl.write.Label l5 = new jxl.write.Label(4, i + 1,
		// u.getKuEditnum()+"");
		// sheet.addCell(l5);
		// jxl.write.Label l6 = new jxl.write.Label(5, i + 1, u.getKuAuditnum()+
		// "");
		// sheet.addCell(l6);
		// }
		// workbook.write();
		// Filedownload.save(new File(fileName), null);
		// workbook.close();
		// System.out.println(org.zkoss.util.resource.Labels.getLabel("newssts.ui.exportStsInformation"));
		//
		// }

		d = ConvertUtil.convertDateAndTimeString(db.getValue());
		b = ConvertUtil.convertDateAndTimeString(db1.getValue());
		String fileName = d.substring(0, 10) + "_" + b.substring(0, 10)
				+ ".xls";
		WritableWorkbook workbook;
		String[] title = { "用户名", "性别", "角色", "所属部门", "编辑条数", "审核条数" };
		OutputStream ops = new FileOutputStream(fileName);
		workbook = Workbook.createWorkbook(ops);
		WritableSheet sheet = workbook.createSheet("工作量统计", 0);
		jxl.write.Label label;
		for (int i = 0; i < title.length; i++) {
			label = new jxl.write.Label(i, 0, title[i]);
			label.setCellFormat(wcf);
			sheet.addCell(label);
		}
		for (int i = 0; i < ulist.size(); i++) {
			Users u = (Users) ulist.get(i);
			jxl.write.Label l1 = new jxl.write.Label(0, i + 1, u.getName());
			sheet.addCell(l1);
			if (u.getKuSex().equals("1"))
				sex = "男";
			else if (u.getKuSex().equals("2"))
				sex = "女";
			jxl.write.Label l2 = new jxl.write.Label(1, i + 1, sex);
			sheet.addCell(l2);
			List dlist = newsService.findDeptById(u.getOrganizationId());
			Organization o = (Organization) dlist.get(0);
			jxl.write.Label l4 = new jxl.write.Label(3, i + 1, o.getName());
			sheet.addCell(l4);
			List ilist = newsService.getCountInfor(
					Long.parseLong(u.getUserId().toString()), "0", d, b);
			List alist = newsService.getCountAudit(
					Long.parseLong(u.getUserId().toString()), "0", d, b);
			if (alist.size() != 0 && ilist.size() != 0) {
				role = "编辑/审核角色";
			} else if (alist.size() != 0) {
				role = "审核角色";
			} else if (ilist.size() != 0) {
				role = "编辑角色";
			} else {
				role = "编辑/审核角色";
			}
			jxl.write.Label l3 = new jxl.write.Label(2, i + 1, role);
			sheet.addCell(l3);
			jxl.write.Label l5 = new jxl.write.Label(4, i + 1, ilist.size()
					+ "");
			sheet.addCell(l5);
			jxl.write.Label l6 = new jxl.write.Label(5, i + 1, alist.size()
					+ "");
			sheet.addCell(l6);
		}
		jxl.write.Label f0 = new jxl.write.Label(3, ulist.size() + 1, "合计：");
		sheet.addCell(f0);
		jxl.write.Label f1 = new jxl.write.Label(4, ulist.size() + 1, edit + "");
		sheet.addCell(f1);
		jxl.write.Label f2 = new jxl.write.Label(5, ulist.size() + 1, audit
				+ "");
		sheet.addCell(f2);
		workbook.write();
		Filedownload.save(new File(fileName), null);
		workbook.close();
		System.out.println(org.zkoss.util.resource.Labels
				.getLabel("newssts.ui.exportStsInformation"));
	}
	
	public void onClick$expert2() throws RowsExceededException, WriteException,
			IOException {
		// 标题样式
		WritableCellFormat wcf = null;
		WritableFont wf = new WritableFont(WritableFont.TIMES, 8,
				WritableFont.BOLD, false);// 最后一个为是否italic,即正常
		wcf = new WritableCellFormat(wf);
		// 对齐方式
		wcf.setAlignment(Alignment.CENTRE);
		Date now = new Date();
		DateFormat date = DateFormat.getDateInstance();
		String fileName = date.format(now)+ ".xls";
		WritableWorkbook workbook;
		String[] title = { "任务名", "所属分类", "来源", "任务配置时间", "采集条数","入库条数" };
		OutputStream ops = new FileOutputStream(fileName);
		workbook = Workbook.createWorkbook(ops);
		WritableSheet sheet = workbook.createSheet("采集量统计", 0);
		jxl.write.Label label;
		for (int i = 0; i < title.length; i++) {
			label = new jxl.write.Label(i, 0, title[i]);
			label.setCellFormat(wcf);
			sheet.addCell(label);
		}
		for (int i = 0; i < wktTaskList.size(); i++) {
			WkTExtractask e = (WkTExtractask) wktTaskList.get(i);
			jxl.write.Label l1 = new jxl.write.Label(0, i + 1, e.getKeName());
			sheet.addCell(l1);
			WkTChanel c = (WkTChanel)newsService.getChanel(e.getKcId());
			jxl.write.Label l2 = new jxl.write.Label(1, i + 1, c.getKcName());
			sheet.addCell(l2);
			jxl.write.Label l3 = new jxl.write.Label(2, i + 1, e.getKeBeginurl());
			sheet.addCell(l3);
			jxl.write.Label l4 = new jxl.write.Label(3, i + 1, e.getKeTime());
			sheet.addCell(l4);
			List <WkTLinkurl> l = (List<WkTLinkurl>)newsService. countLinkurl(e.getKeId());
			jxl.write.Label l5 = new jxl.write.Label(4, i + 1, l.size()+"");
			sheet.addCell(l5);
			List <WkTDistribute> d=newsService.getStatisDistri(e.getKeId());
			jxl.write.Label l6 = new jxl.write.Label(5, i + 1, d.size()+"");
			sheet.addCell(l6);
		}
		jxl.write.Label f0 = new jxl.write.Label(3, wktTaskList.size() + 1, "合计：");
		sheet.addCell(f0);
		jxl.write.Label f1 = new jxl.write.Label(4, wktTaskList.size() + 1, extracts + "");
		sheet.addCell(f1);
		jxl.write.Label f2 = new jxl.write.Label(5, wktTaskList.size() + 1, indata + "");
		sheet.addCell(f2);
		workbook.write();
		Filedownload.save(new File(fileName), null);
		workbook.close();
		System.out.println(org.zkoss.util.resource.Labels
				.getLabel("newssts.ui.exportStsInformation"));
	}
	
}
