package com.uniwin.webkey.infoExtra.newspub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.core.DataBaseSet;
import com.uniwin.webkey.infoExtra.infosort.DomainTreeComposer;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.OriNewsService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoIdAnddomainId;
import com.uniwin.webkey.infoExtra.model.WkTOrifile;
import com.uniwin.webkey.infoExtra.model.WkTOrinfo;
import com.uniwin.webkey.infoExtra.model.WkTOrinfocnt;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.infoExtra.subjectterm.SubjectTermTabComposer;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

public class OriNewsEditWindow extends Window implements AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map params;
	WkTOrinfo oinfo;
	Textbox kititle, kititle2, kikeys, kisource, ptime, taskname, sort;
	FCKeditor kicontent;
	OriNewsService orinewsService = (OriNewsService) SpringUtil
			.getBean("info_orinewsService");
	NewsServices newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");
	TaskService taskService = (TaskService) SpringUtil.getBean("taskService");
	Toolbarbutton reset, choose;
	Button photos, kuaizhao;
	Users user;
	Long userid;
	String username;

	private Textbox subjectTerm;
	private List<WkTSubjectTerm> selectedSts = null;

	private InfoIdAndDomainId infoIdAndDomainIdService = (InfoIdAndDomainId) SpringUtil
			.getBean("infoIdAndDomainIdService");
	List selectList = new ArrayList();
	List slist = new ArrayList();
	List flist = new ArrayList();
	Listbox upList;
	ListModelList modelListbox;
	Tab photo;
	Iframe pubPreView;
	Textbox url;

	public void afterCompose() {
		params = this.getAttributes();
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users) Sessions.getCurrent().getAttribute("users");
		// 选择发布频道
		choose.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				NewsTaskSelectWindow w = (NewsTaskSelectWindow) Executions
						.createComponents(
								"/apps/infoExtra/content/newspub/newtasksel.zul",
								null, null);
				Executions.getCurrent().setAttribute("mul", "0");
				w.initWindow();
				w.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						pubPreView.setVisible(true);
						NewsTaskSelectWindow ns = (NewsTaskSelectWindow) arg0
								.getTarget();
						slist = ns.getSlist();
						WkTExtractask et = (WkTExtractask) slist.get(0);
						taskname.setValue(et.getKeName());
					}
				});
				w.addEventListener(Events.ON_CHANGING, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						pubPreView.setVisible(true);
					}
				});
				pubPreView.setVisible(false);
				w.doModal();

			}
		});

		photos.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				// String
				// path1=Sessions.getCurrent().getWebApp().getRealPath("/mht");
				// String filename=oinfo.getKoiMht();
				// String pa = path1+"//"+filename.trim();
				String filename = oinfo.getKoiMht();
				// String
				// p="http://localhost:8080/InfoCollection/"+"html/"+filename.substring(0,
				// filename.indexOf("."))+".html";
				//String p1 = "http://127.0.0.1:8080/ROOT/" + "mht/" + filename;
				String path = oinfo.getKoiUrl();
				pubPreView.setSrc(path);
				// Executions.getCurrent().sendRedirect(path,"_black");
			}

		});

	}

	public void initWindow(WkTOrinfo oinfo) {
		this.oinfo = oinfo;
		WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
				oinfo.getKeId()).get(0);
		kititle.setValue(oinfo.getKoiTitle().trim());
		kititle2.setValue(oinfo.getKoiTitle2());
		kisource.setValue(oinfo.getKoiSource());
		kikeys.setValue(oinfo.getKoiKeys());
		taskname.setValue(e.getKeName());
		ptime.setValue(oinfo.getKoiPtime());
		pubPreView.setSrc("");
		initOinfocnt(orinewsService.getOriInfocnt(oinfo.getKoiId()));
		// 加载附件
		flist = newsService.getOrifile(oinfo.getKoiId());
		if (flist.size() != 0) {
			modelListbox.addAll(flist);
			upList.setModel(modelListbox);
		}
		if (selectList.size() != 0) {
			selectList.clear();
		}

		String path = oinfo.getKoiUrl();
		url.setValue(path);
		String path1 = Sessions.getCurrent().getWebApp().getRealPath("/mht");
		String filename = oinfo.getKoiMht();
		// String
		// p="http://localhost:8080/InfoCollection/"+"html/"+filename.substring(0,
		// filename.indexOf("."))+".html";
		String p1 = "http://127.0.0.1:8080/ROOT/" + "mht/" + filename;
		pubPreView.setSrc(path);
	}

	public void onClick$refresh() {

		String path = oinfo.getKoiUrl();
		url.setValue(path);
		pubPreView.setSrc(path);
	}

	public void initOinfocnt(List rlist) {
		String con = "";
		for (int i = 0; i < rlist.size(); i++) {
			WkTOrinfocnt infcnt = (WkTOrinfocnt) rlist.get(i);
			con += infcnt.getKoiContent();
		}
		kicontent.setValue(con);
	}

	// 保存信息
	public void onClick$save() throws InterruptedException {

		if (kititle.getValue().equals("")) {
			pubPreView.setVisible(false);
			Messagebox.show("标题不能为空！", "提示", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		if (taskname.getValue().equals("")) {
			pubPreView.setVisible(false);
			Messagebox.show("请选择所属任务！", "提示", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		if (sort.getValue().trim().equals("") || sort.getValue().trim() == null) {
			pubPreView.setVisible(false);
			Messagebox.show("请选择发布信息分类！", "提示", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}

		WkTInfo info = new WkTInfo();
		info.setKiTitle(kititle.getValue());
		info.setKiTitle2(kititle2.getValue());
		if (slist.size() != 0) {
			WkTExtractask et = (WkTExtractask) slist.get(0);
			info.setKeId(et.getKeId());
		} else {
			info.setKeId(oinfo.getKeId());
		}
		info.setKiAuthname(oinfo.getKoiAuthname());
		info.setKiCtime(oinfo.getKoiCtime());
		info.setKiHits(Integer.parseInt("0"));
		info.setKiKeys(kikeys.getValue());
		info.setKiOrdno(Integer.parseInt("10"));
		//info.setKiPtime(oinfo.getKoiPtime());
		info.setKiPtime(ptime.getValue());
		info.setKiAddress(oinfo.getKoiAddress());
		info.setKiImage(oinfo.getKoiImage());
		info.setKiValiddate("1900-01-01");
		info.setKiShow("1");
		info.setKiSource(kisource.getValue());
		info.setKiTop("0");
		info.setKiUrl(oinfo.getKoiUrl());
		info.setKiType("1");
		info.setKuId(Long.parseLong(user.getUserId() + ""));
		info.setKuName(user.getName());
		info.setKiMht(oinfo.getKoiMht());
		if(selectedSts != null && selectedSts.size() != 0){
			info.setKstids(selectedSts);
		}
		try {
			newsService.save(info);
		} catch (Exception e) {
			System.out.println("保存失败！");
			return;
		}

		Long len = DataBaseSet.data_Len;
		Long max = kicontent.getValue().length() / len + 1;
		WkTInfocnt infocnt;
		WkTInfocntId infocntid;
		for (Long i = 0L; i < max; i++) {
			infocnt = new WkTInfocnt();
			infocntid = new WkTInfocntId(info.getKiId(), i + 1);
			infocnt.setId(infocntid);
			Long be = i * len;
			if (i == (max - 1)) {
				infocnt.setKiContent(kicontent.getValue().substring(
						be.intValue()));
			} else {
				Long en = (i + 1) * len;
				infocnt.setKiContent(kicontent.getValue().substring(
						be.intValue(), en.intValue()));
			}

			try {
				newsService.save(infocnt);
			} catch (Exception e) {
				System.out.println("内容保存失败！");
				return;
			}
		}
		WkTExtractask extractask = taskService.findById(info.getKeId());
		WkTDistribute dis = new WkTDistribute();
		dis.setKiId(info.getKiId());
		dis.setKeId(info.getKeId());
		dis.setKbTitle(info.getKiTitle());
		dis.setKbRight("0");
		dis.setKbStatus("1");
		dis.setKbMail("0");
		dis.setKbFlag("0");
		dis.setKcId(extractask.getKcId());
		Date date = new Date();
		dis.setKbDtime(ConvertUtil.convertDateAndTimeString(date.getTime()));
		newsService.save(dis);

		saveInfoSort(info);

		// 删除原始信息表内容
		List ofilelist = orinewsService.getOrifile(oinfo.getKoiId());
		if (ofilelist.size() != 0) {
			for (int j = 0; j < ofilelist.size(); j++) {
				WkTOrifile file = (WkTOrifile) ofilelist.get(j);
				newsService.delete(file);
			}
		}
		List oinfolist = orinewsService.getOriInfocnt(oinfo.getKoiId());
		for (int i = 0; i < oinfolist.size(); i++) {
			WkTOrinfocnt oinfocnt = (WkTOrinfocnt) oinfolist.get(i);
			newsService.delete(oinfocnt);
		}
		newsService.delete(oinfo);
		pubPreView.setVisible(false);
		Messagebox.show("操作成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		this.detach();
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	// 发布
	public void onClick$saudit() throws InterruptedException {

		if (kititle.getValue().equals("")) {
			pubPreView.setVisible(false);
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		if (taskname.getValue().equals("")) {
			pubPreView.setVisible(false);
			Messagebox.show("请选择所属任务！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		if (sort.getValue().trim().equals("") || sort.getValue().trim() == null) {
			pubPreView.setVisible(false);
			Messagebox.show("请选择信息类别！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		if (!infoIdAndDomainIdService.findByTitle(kititle.getValue()).isEmpty()) {
			pubPreView.setVisible(false);
			Messagebox.show("信息已发布，请删除重复信息！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			pubPreView.setVisible(true);
			return;
		}
		WkTInfo info = new WkTInfo();
		info.setKiTitle(kititle.getValue());
		info.setKiTitle2(kititle2.getValue());
		if (slist.size() != 0) {
			WkTExtractask et = (WkTExtractask) slist.get(0);
			info.setKeId(et.getKeId());
		} else {
			info.setKeId(oinfo.getKeId());
		}
		info.setKiAuthname(oinfo.getKoiAuthname());
		info.setKiCtime(oinfo.getKoiCtime());
		info.setKiHits(Integer.parseInt("0"));
		info.setKiKeys(kikeys.getValue());
		info.setKiOrdno(Integer.parseInt("10"));
		//info.setKiPtime(oinfo.getKoiPtime());
		info.setKiPtime(ptime.getValue());
		info.setKiAddress(oinfo.getKoiAddress());
		info.setKiImage(oinfo.getKoiImage());
		info.setKiShow("1");
		info.setKiValiddate("1900-01-01");
		info.setKiSource(kisource.getValue());
		info.setKiTop("0");
		info.setKiUrl(oinfo.getKoiUrl());
		info.setKiType("1");
		info.setKuId(Long.parseLong(user.getUserId() + ""));
		info.setKuName(user.getName());
		info.setKiMht(oinfo.getKoiMht());
		if(selectedSts != null && selectedSts.size() != 0){
			info.setKstids(selectedSts);
		}
		newsService.save(info);
		Long len = DataBaseSet.data_Len;
		Long max = kicontent.getValue().length() / len + 1;
		for (Long i = 0L; i < max; i++) {
			WkTInfocnt infocnt = new WkTInfocnt();
			WkTInfocntId infocntid = new WkTInfocntId(info.getKiId(), i + 1);
			infocnt.setId(infocntid);
			Long be = i * len;
			if (i == (max - 1)) {
				infocnt.setKiContent(kicontent.getValue().substring(
						be.intValue()));
			} else {
				Long en = (i + 1) * len;
				infocnt.setKiContent(kicontent.getValue().substring(
						be.intValue(), en.intValue()));
			}
			newsService.save(infocnt);
		}
		WkTExtractask extractask = taskService.findById(info.getKeId());
		WkTDistribute dis = new WkTDistribute();
		dis.setKiId(info.getKiId());
		dis.setKeId(info.getKeId());

		dis.setKbTitle(info.getKiTitle());
		dis.setKbRight("0");
		dis.setKbFlag("0");
		dis.setKcId(extractask.getKcId());
		WkTExtractask wet = (WkTExtractask) taskService.getTaskBykeId(
				dis.getKeId()).get(0);
		WkTChanel tt = taskService.getTpyeById(wet.getKcId());
		if (tt.getKfId().toString().trim().equals("0")) {
			dis.setKbStatus("0");
		} else if (tt.getKfId().toString().trim().equals("1")) {
			dis.setKbStatus("2");
		}
		Date date = new Date();
		dis.setKbDtime(ConvertUtil.convertDateAndTimeString(date.getTime()));

		dis.setKbEm("0");
		newsService.save(dis);
		saveInfoSort(info);// 保存信息具体分类信息

		// 删除原始信息表内容
		List oinfolist = orinewsService.getOriInfocnt(oinfo.getKoiId());
		for (int i = 0; i < oinfolist.size(); i++) {
			WkTOrinfocnt oinfocnt = (WkTOrinfocnt) oinfolist.get(i);
			newsService.delete(oinfocnt);
		}
		List ofilelist = orinewsService.getOrifile(oinfo.getKoiId());
		if (ofilelist.size() != 0) {
			for (int j = 0; j < ofilelist.size(); j++) {
				WkTOrifile file = (WkTOrifile) ofilelist.get(j);
				newsService.delete(file);
			}
		}
		newsService.delete(oinfo);
		pubPreView.setVisible(false);
		Messagebox.show("操作成功！", "Information", Messagebox.OK,
				Messagebox.INFORMATION);
		this.detach();
		Events.postEvent(Events.ON_CHANGE, this, null);

	}

	private void saveInfoSort(WkTInfo info) {

		if (selectList != null && selectList.size() > 0) {
			WkTInfoIdAnddomainId idAnddomainId;
			WkTInfoDomain domain;
			for (int s = 0; s < selectList.size(); s++) {
				domain = (WkTInfoDomain) selectList.get(s);
				idAnddomainId = new WkTInfoIdAnddomainId();
				idAnddomainId.setDomainId(domain.getKiId());
				idAnddomainId.setInfoId(info.getKiId());
				infoIdAndDomainIdService.save(idAnddomainId);
			}

		}
	}

	public void onClick$stChoose() {
		final SubjectTermTabComposer w = (SubjectTermTabComposer) Executions
				.createComponents(
						"/apps/infoExtra/content/subjectterm/subjectTermTab.zul",
						null, null);
		w.doHighlighted();
		w.initWin(subjectTerm);
		w.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				selectedSts = w.getSelectedSubjectTerms();
				if(selectedSts != null && selectedSts.size() != 0){
					StringBuilder str = new StringBuilder("");
					for(int i = 0; i < selectedSts.size(); i++){
						WkTSubjectTerm st = selectedSts.get(i);
						str.append(st.getKiName());
						str.append(";");
//						if(i < selectedSts.size() - 1){
//							str.append(";");
//						}
					}
					subjectTerm.setValue(str.toString());
				}
			}
		});
	}

	public void onClick$chooseSort() {

		DomainTreeComposer s = (DomainTreeComposer) Executions
				.createComponents(
						"/apps/infoExtra/content/infosort/sortTree.zul", null,
						null);
		s.doHighlighted();
		pubPreView.setVisible(false);
		s.initWin(sort);
		s.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				pubPreView.setVisible(true);
				List s = (List) arg0.getData();
				selectList = s;
			}
		});
		s.addEventListener(Events.ON_CHANGING, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				pubPreView.setVisible(true);
			}
		});
	}

	// 删除单条信息
	public void onClick$delete() throws InterruptedException {
		pubPreView.setVisible(false);
		if (Messagebox.show("确定要删除吗？", "确认", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {
			// 删除信息内容
			List oinfolist = orinewsService.getOriInfocnt(oinfo.getKoiId());
			for (int i = 0; i < oinfolist.size(); i++) {
				WkTOrinfocnt oinfocnt = (WkTOrinfocnt) oinfolist.get(i);
				newsService.delete(oinfocnt);
			}
			newsService.delete(orinewsService.getOriInfo(oinfo.getKoiId()));
			Messagebox.show("操作成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			this.detach();
			Events.postEvent(Events.ON_CHANGE, this, null);
		}
		pubPreView.setVisible(true);
	}

	// 重置
	public void onClick$reset() {
		if (!sort.getValue().trim().equals("")) {
			sort.setValue("");
		}
		initWindow(oinfo);
	}

}