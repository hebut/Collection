package com.uniwin.webkey.infoExtra.newsaudit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkforge.fckez.FCKeditor;
import org.zkoss.io.Files;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.core.DataBaseSet;
import com.uniwin.webkey.infoExtra.infosort.DomainTreeComposer;
import com.uniwin.webkey.infoExtra.infosort.Sort2;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.MLogService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoIdAnddomainId;
import com.uniwin.webkey.infoExtra.model.WkTMlog;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTFileId;
import com.uniwin.webkey.cms.model.WkTFlog;
import com.uniwin.webkey.infoExtra.subjectterm.SubjectTermTabComposer;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

/**
 * 管理信息审核中的查看详细信息界面 2010-3-19
 * 
 * @author whm
 * 
 */
public class NewsauditDetailWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 4915312144706234770L;
	// 数据访问接口
	IUsersManager userService;

	private NewsServices newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");
	private TaskService taskService = (TaskService) SpringUtil
			.getBean("taskService");
	// 管理日志数据访问接口
	// MLogService mlogService;
	WkTInfocnt Infocnt;
	Users user;
	WkTInfo info;
	WkTDistribute dis, d;
	WkTInfocntId infocntid;
	WkTFlog flog;
	List slist;
	Html content;
	// 详细信息界面涉及的各种组件
	Textbox kititle, kititle2, kikeys,kiorigtime, kiimage, kisource, kitype, kflmemo,
			taskname, kiSort;
	Datebox kivaliddate;
	Toolbarbutton del, down, upfile, download, choose, save, reset;
	Toolbarbutton reback, deUpload, up, choice1;
	Listbox upList;
	FCKeditor kicontent;
	Separator sep1, sep2;
	Hbox wdnews, tupian, suggest, adv, wd, sharehbox;
	Window win;
	Label fileup;
	List nameList = new ArrayList();
	List userDeptList, flist, plist;
	ListModelList modelListbox;

	InfoIdAndDomainId infoIdAndDomainIdService = (InfoIdAndDomainId) SpringUtil
			.getBean("infoIdAndDomainIdService");
	List selectList;

	private List<WkTSubjectTerm> selectedSts = null;
	private Textbox subjectTerm;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user = (Users) Sessions.getCurrent().getAttribute("users");
		userDeptList = (List) Sessions.getCurrent()
				.getAttribute("userDeptList");
		final WkTDistribute di = (WkTDistribute) Executions.getCurrent()
				.getAttribute("d");
		modelListbox = new ListModelList(nameList);
		// 上传附件
		upList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem arg0, Object arg1) throws Exception {
				if (arg1 instanceof Media) {
					Media media = (Media) arg1;
					arg0.setValue(arg1);
					arg0.setLabel(media.getName());
				} else {
					WkTFile f = (WkTFile) arg1;
					arg0.setValue(arg1);
					arg0.setLabel(f.getId().getKfShowname());
				}
			}
		});

		choose.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Executions.getCurrent().setAttribute("mul", "0");
				NewsauditTaskSelectWindow w = (NewsauditTaskSelectWindow) Executions
						.createComponents(
								"/apps/infoExtra/content/newsaudit/taskselect.zul",
								null, null);
				w.doHighlighted();
				w.initWindow();
				w.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						NewsauditTaskSelectWindow ns = (NewsauditTaskSelectWindow) arg0
								.getTarget();
						plist = ns.getSlist();
						WkTExtractask et = (WkTExtractask) plist.get(0);
						taskname.setValue(et.getKeName());
					}
				});
			}
		});
	}

	// 判断信息类型，根据类型显示不一样的界面，并获得详细信息
	public void initWindow(WkTDistribute dist) {
		this.dis = dist;
		WkTInfo info = newsService.getWkTInfo(dis.getKiId());
		WkTExtractask et = (WkTExtractask) taskService.getTaskBykeId(
				dis.getKeId()).get(0);
		List sortList = infoIdAndDomainIdService.findByInfoId(info.getKiId());
		if (dis.getKbStatus().toString().trim().equals("2")) {
			adv.setVisible(true);
		}
		if (dis.getKbFlag().toString().trim().equals("1")) {
			kititle.setReadonly(true);
			kititle2.setReadonly(true);
			content.setVisible(true);
			kivaliddate.setDisabled(true);
			kikeys.setReadonly(true);
			kisource.setReadonly(true);
			up.setDisabled(true);
			deUpload.setDisabled(true);
			adv.setVisible(false);
			kicontent.setVisible(false);
			reback.setVisible(false);
			choose.setDisabled(true);
			taskname.setReadonly(true);
			sep1.setVisible(true);
			sep2.setVisible(true);
			save.setVisible(false);
			reset.setVisible(false);
		}
		initInfocnt(newsService.getChildNewsContent(info.getKiId()));

		taskname.setValue(et.getKeName());
		kititle.setValue(info.getKiTitle());
		kititle2.setValue(info.getKiTitle2());
		kisource.setValue(info.getKiSource());
		kikeys.setValue(info.getKiKeys());
		kiorigtime.setValue(info.getKiPtime());

		if (sortList != null && sortList.size() > 0) {
			WkTInfoDomain domain;
			StringBuffer sb = new StringBuffer("");
			for (int d = 0; d < sortList.size(); d++) {
				domain = (WkTInfoDomain) sortList.get(d);
				sb.append(domain.getKiName() + ";");
			}
			kiSort.setValue(sb.toString());
		}
		subjectTerm.setValue(info.getWkTSubjectTermNames());
		// 加载附件
		flist = newsService.getFile(dis.getKiId());
		if (flist.size() != 0) {
			modelListbox.addAll(flist);
			upList.setModel(modelListbox);
		}

	}

	public WkTInfocnt getInfocnt() {

		return Infocnt;
	}

	// 初始化信息内容
	public void initInfocnt(List rlist) {
		String con = "";
		for (int i = 0; i < rlist.size(); i++) {
			WkTInfocnt inf = (WkTInfocnt) rlist.get(i);
			con += inf.getKiContent();
		}
		if (dis.getKbFlag().toString().trim().equals("0"))
			kicontent.setValue(con);
		else
			content.setContent(con);
	}

	public void onClick$chooseSort() {
		// 修改分类树前
		// Sort2
		// s=(Sort2)Executions.createComponents("/apps/infoExtra/content/infosort/sort.zul",
		// null, null);
		// s.doHighlighted();
		// s.initWin(kiSort);
		// s.addEventListener(Events.ON_CHANGE, new EventListener(){
		// public void onEvent(Event arg0) throws Exception {
		// List s=(List) arg0.getData();
		// selectList=s;
		// }
		//
		// });

		// 分类树修改之后
		DomainTreeComposer s = (DomainTreeComposer) Executions
				.createComponents(
						"/apps/infoExtra/content/infosort/sortTree.zul", null,
						null);
		s.doHighlighted();
		s.initWin(kiSort);
		s.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				List s = (List) arg0.getData();
				selectList = s;
			}
		});

	}

	// 信息类型保存
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

	// 信息发布
	public void onClick$pass() throws InterruptedException, IOException {

		if (kititle.getValue().equals("")) {
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		if (kiSort.getValue().equals("") || kiSort.getValue().trim() == null) {
			Messagebox.show("信息类别不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		if (!infoIdAndDomainIdService.findByTitle(kititle.getValue()).isEmpty()) {
			Messagebox.show("信息已发布,请删除重复信息！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		if (Messagebox.show("确定要通过吗？", "确认", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {
			Date da = new Date();
			WkTInfo info = newsService.getWkTInfo(dis.getKiId());
			WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
					dis.getKeId()).get(0);
			if (kivaliddate.getText().trim().equals("")) {
				info.setKiValiddate("1900-1-1");
			} else {
				info.setKiValiddate(kivaliddate.getText());
			}
			if (dis.getKbStatus().toString().trim().equals("1")) {

				info.setKuId(Long.parseLong(user.getUserId().toString()));
				info.setKuName(user.getLoginName());
			}
			info.setKiTitle(kititle.getText());
			info.setKiTitle2(kititle2.getText());
			info.setKiSource(kisource.getText());
			if (dis.getKbFlag().toString().trim().equals("0")) {

				if (!taskname.getValue().trim().equals(e.getKeName().trim())) {
					WkTExtractask et = (WkTExtractask) plist.get(0);
					info.setKeId(et.getKeId());
				}
			}
			if (newsService.getWkTInfo(dis.getKiId()).getKiType().toString()
					.trim().equals("1")) {
				info.setKiAddress("");
			}
			info.setKiKeys(kikeys.getText());
			info.setKiCtime(ConvertUtil.convertDateAndTimeString(da.getTime()));

			// 保存信息内容至WkTInfocnt
			if (dis.getKbFlag().toString().trim().equals("0")) {
				List cntlist = newsService.getInfocnt(dis.getKiId());
				for (int i = 0; i < cntlist.size(); i++) {
					WkTInfocnt infocnt = (WkTInfocnt) cntlist.get(i);
					newsService.delete(infocnt);
				}
				Long len = DataBaseSet.data_Len;
				Long max = kicontent.getValue().length() / len + 1;
				for (Long i = 0L; i < max; i++) {
					WkTInfocnt infocnt = new WkTInfocnt();
					WkTInfocntId infocntid = new WkTInfocntId(info.getKiId(),
							i + 1);
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
			}
			newsService.update(info);
			// 将信息的情况保存到WkTDistribute表中
			if (dis.getKbFlag().toString().trim().equals("0")) {
				dis.setKeId(info.getKeId());
			}
			dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
			dis.setKbTitle(kititle.getText());

			if (dis.getKbStatus().toString().trim().equals("1")) {

				List zlist = newsService.getNewsOfShareChanel(dis.getKiId());
				if (zlist.size() != 0) {
					for (int i = 0; i < zlist.size(); i++)
						newsService.delete((WkTDistribute) zlist.get(i));
				}

			}
			dis.setKbStatus("0");
			newsService.update(dis);

			// 保存处理信息
			WkTFlog flog = new WkTFlog();
			flog.setKflMemo("审核通过");
			flog.setKflTime(ConvertUtil.convertDateAndTimeString(da.getTime()));
			flog.setKuId(Long.parseLong(user.getUserId().toString()));
			flog.setKuName(user.getLoginName());
			flog.setKbId(dis.getKbId());
			flog.setKfId(null);
			newsService.save(flog);

			// 保存附件
			if (modelListbox.getInnerList().size() != 0
					&& modelListbox.getInnerList() != null) {
				List flist = modelListbox.getInnerList();
				for (int i = 0; i < flist.size(); i++) {
					if (flist.get(i) instanceof Media) {
						Media media = (Media) flist.get(i);
						String fileName = DateUtil
								.getDateTimeString(new Date())
								+ "_"
								+ info.getKiId().toString()
								+ "_"
								+ media.getName().toString();
						saveToFile(media, dis.getKiId(), info.getKuId());
					}
				}
			}

			// 发布成html页面
			// Map root = new HashMap();
			// Sessions.getCurrent().setAttribute("root", root);
			// WKT_DOCLIST dList = new WKT_DOCLIST();
			// dList.singleNewsPublic(info, dis.getKcId());

			saveInfoSort(info);
			Users user = (Users) Sessions.getCurrent().getAttribute("user");
			// mlogService.saveMLog(WkTMlog.FUNC_CMS, "发布信息，id:"+dis.getKiId(),
			// user);
			Messagebox.show("操作成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			win.detach();
			Events.postEvent(Events.ON_CHANGE, this, null);
		}
	}

	// 将信息退回给信息编写者
	public void onClick$reback() throws InterruptedException, IOException {

		if (kititle.getValue().equals("")) {
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}

		if (Messagebox.show("确定要退回吗？", "确认", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {
			Date da = new Date();
			WkTInfo info = newsService.getWkTInfo(dis.getKiId());
			WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
					dis.getKeId()).get(0);
			info.setKiValiddate(kivaliddate.getText());
			info.setKiTitle(kititle.getText());
			info.setKiSource(kisource.getText());
			if(selectedSts != null && selectedSts.size() != 0){
				info.setKstids(selectedSts);
			}
			info.setKiTitle2(kititle2.getText());
			if (newsService.getWkTInfo(dis.getKiId()).getKiType().toString()
					.trim().equals("1")) {
				info.setKiAddress(null);
			}
			if (!taskname.getValue().trim().equals(e.getKeName().trim())) {
				WkTExtractask et = (WkTExtractask) plist.get(0);
				info.setKeId(et.getKeId());
			}
			info.setKiKeys(kikeys.getText());
			info.setKiCtime(ConvertUtil.convertDateAndTimeString(da.getTime()));

			// 保存信息内容至WkTInfocnt
			List cntlist = newsService.getInfocnt(dis.getKiId());
			for (int i = 0; i < cntlist.size(); i++) {
				WkTInfocnt infocnt = (WkTInfocnt) cntlist.get(i);
				newsService.delete(infocnt);
			}
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
			newsService.update(info);
			// 保存附件
			if (modelListbox.getInnerList().size() != 0
					&& modelListbox.getInnerList() != null) {
				List flist = modelListbox.getInnerList();
				for (int i = 0; i < flist.size(); i++) {
					if (flist.get(i) instanceof Media) {
						Media media = (Media) flist.get(i);
						String fileName = DateUtil
								.getDateTimeString(new Date())
								+ "_"
								+ info.getKiId().toString()
								+ "_"
								+ media.getName().toString();
						saveToFile(media, dis.getKiId(), info.getKuId());
					}
				}
			}
			// 将新信息的发布情况保存到WkTDistribute表中
			dis.setKeId(info.getKeId());
			dis.setKbStatus("4");
			dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
			dis.setKbTitle(kititle.getText());
			newsService.update(dis);
			if (dis.getKbFlag().trim().equals("0")) {
				List blist = newsService.getDistributeList(dis.getKiId());
				if (blist.size() > 1) {
					for (int i = 0; i < blist.size(); i++) {
						WkTDistribute dis = (WkTDistribute) blist.get(i);
						dis.setKbStatus("4");
						newsService.update(dis);
					}
				}
			}
			if (newsService.getDistributeShare(info.getKiId()).size() != 0) {
				List list = newsService.getDistributeShare(info.getKiId());
				for (int i = 0; i < list.size(); i++) {
					WkTDistribute d = (WkTDistribute) newsService
							.getDistributeShare(info.getKiId()).get(i);
					d.setKbStatus("4");
					newsService.update(d);
				}
			}
			// 保存处理信息
			WkTFlog flog = new WkTFlog();
			flog.setKflMemo(kflmemo.getText());
			flog.setKflTime(ConvertUtil.convertDateAndTimeString(da.getTime()));
			flog.setKuId(Long.parseLong(user.getUserId().toString()));
			flog.setKuName(user.getLoginName());
			flog.setKbId(dis.getKbId());
			flog.setKfId(null);
			newsService.save(flog);
			Messagebox.show("操作成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			Users user = (Users) Sessions.getCurrent().getAttribute("users");
			// mlogService.saveMLog(WkTMlog.FUNC_CMS, "退回信息，id:"+dis.getKiId(),
			// user);
			win.detach();
			Events.postEvent(Events.ON_CHANGE, this, null);
		}
	}

	// 保存信息至已阅信息列表
	public void onClick$save() throws InterruptedException, IOException {

		if (kititle.getValue().equals("")) {
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		Date da = new Date();
		WkTInfo info = newsService.getWkTInfo(dis.getKiId());
		WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
				dis.getKeId()).get(0);
		info.setKiValiddate(kivaliddate.getText());
		info.setKiTitle(kititle.getText());
		info.setKiTitle2(kititle2.getText());
		info.setKiTop("0");
		info.setKiHits(0);
		info.setKiSource(kisource.getText());
		if(selectedSts != null && selectedSts.size() != 0){
			info.setKstids(selectedSts);
		}
		if (!taskname.getValue().trim().equals(e.getKeName().trim())) {
			WkTExtractask et = (WkTExtractask) plist.get(0);
			info.setKeId(et.getKeId());
		}
		if (newsService.getWkTInfo(dis.getKiId()).getKiType().toString().trim()
				.equals("1")) {
			info.setKiAddress(null);
		}
		info.setKiKeys(kikeys.getText());
		info.setKiCtime(ConvertUtil.convertDateAndTimeString(da.getTime()));

		// 保存信息内容至WkTInfocnt
		List cntlist = newsService.getInfocnt(dis.getKiId());
		for (int i = 0; i < cntlist.size(); i++) {
			WkTInfocnt infocnt = (WkTInfocnt) cntlist.get(i);
			newsService.delete(infocnt);
		}
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

		newsService.update(info);
		// 将新信息的发布情况保存到WkTDistribute表中
		dis.setKeId(info.getKeId());

		dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
		dis.setKbTitle(kititle.getText());

		if (dis.getKbStatus().toString().trim().equals("1")) {
			dis.setKbStatus("1");
		} else if (dis.getKbStatus().toString().trim().equals("2")) {
			dis.setKbStatus("3");
		}
		newsService.update(dis);

		// 保存附件
		if (modelListbox.getInnerList().size() != 0
				&& modelListbox.getInnerList() != null) {
			List flist = modelListbox.getInnerList();
			for (int i = 0; i < flist.size(); i++) {
				if (flist.get(i) instanceof Media) {
					Media media = (Media) flist.get(i);
					String fileName = DateUtil.getDateTimeString(new Date())
							+ "_" + info.getKiId().toString() + "_"
							+ media.getName().toString();
					saveToFile(media, dis.getKiId(), info.getKuId());
				}
			}
		}
		Users user = (Users) Sessions.getCurrent().getAttribute("users");
		// mlogService.saveMLog(WkTMlog.FUNC_CMS, "已阅信息，id:"+dis.getKiId(),
		// user);
		saveInfoSort(info);
		Messagebox.show("操作成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		win.detach();
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	// 保存上传的附件至附件列表
	public void saveToFile(Media media, Long infoid, Long kuid)
			throws IOException {
		if (media != null) {
			// isBinary二进制文件情况，如下处理
			if (media.isBinary()) {
				InputStream objin = media.getStreamData();
				String fileName = DateUtil.getDateTimeString(new Date()) + "_"
						+ infoid.toString() + "_" + media.getName().toString();
				String Name = media.getName();
				String pa = Executions.getCurrent().getDesktop().getWebApp()
						.getRealPath("/upload/info");
				if (pa == null) {
					System.out.println("无法访问存储目录！");
					return;
				}
				File fUploadDir = new File(pa);
				if (!fUploadDir.exists()) {
					if (!fUploadDir.mkdir()) {
						System.out.println("无法创建存储目录！");
						return;
					}
				}
				String path1 = Executions.getCurrent().getDesktop().getWebApp()
						.getRealPath("/upload/info");
				if (path1 == null) {
					System.out.println("无法访问存储目录！");
					return;
				}
				File fUploadDir2 = new File(path1);
				if (!fUploadDir2.exists()) {
					if (!fUploadDir2.mkdir()) {
						System.out.println("无法创建存储目录！");
						return;
					}
				}
				String path = Executions.getCurrent().getDesktop().getWebApp()
						.getRealPath("/upload/info")
						+ "//" + fileName.trim();
				FileOutputStream out = null;
				out = new FileOutputStream(path);
				DataOutputStream objout = new DataOutputStream(out);
				Files.copy(objout, objin);

				if (out != null) {
					out.close();
				}
				WkTFile file = new WkTFile();
				WkTFileId fileid = new WkTFileId(infoid, fileName, media
						.getName().toString(), kuid, "1", "0");
				file.setId(fileid);
				newsService.save(file);
			}
			// 否则做如下处理
			else {

				if (media.getName().endsWith(".txt")
						|| media.getName().endsWith(".project")) {
					Reader r = media.getReaderData();
					String fileName = DateUtil.getDateTimeString(new Date())
							+ "_" + infoid.toString() + "_"
							+ media.getName().toString();
					String Name = media.getName();
					String pa = Executions.getCurrent().getDesktop()
							.getWebApp().getRealPath("/upload/info");
					if (pa == null) {
						System.out.println("无法访问存储目录！");
						return;
					}
					File fUploadDir = new File(pa);
					if (!fUploadDir.exists()) {
						if (!fUploadDir.mkdir()) {
							System.out.println("无法创建存储目录！");
							return;
						}
					}

					String path1 = Executions.getCurrent().getDesktop()
							.getWebApp().getRealPath("/upload/info");
					if (path1 == null) {
						System.out.println("无法访问存储目录！");
						return;
					}
					File fUploadDir2 = new File(path1);
					if (!fUploadDir2.exists()) {
						if (!fUploadDir2.mkdir()) {
							System.out.println("无法创建存储目录！");
							return;
						}
					}
					String path2 = Executions
							.getCurrent()
							.getDesktop()
							.getWebApp()
							.getRealPath(
									"/upload/info" + "//" + fileName.trim());
					File f = new File(path2);
					Files.copy(f, r, null);
					Files.close(r);
					WkTFile file = new WkTFile();
					WkTFileId fileid = new WkTFileId(infoid, fileName, media
							.getName().toString(), kuid, "1", "0");
					file.setId(fileid);
					newsService.save(file);
				}

			}
		}

	}

	// 下载或查看图片
	public void onClick$pics() throws FileNotFoundException {
		String path = Executions.getCurrent().getDesktop().getWebApp()
				.getRealPath("/upload/info");
		File f = new File(path.trim()
				+ "\\".trim()
				+ newsService.getWkTInfo(dis.getKiId()).getKiImage().toString()
						.trim());
		Filedownload.save(f, null);
	}

	// 下载附件文件
	public void onClick$down() throws InterruptedException,
			FileNotFoundException {

		if (modelListbox.getSize() == 0)
			return;

		Listitem it = upList.getSelectedItem();
		if (it == null) {
			if (modelListbox.getSize() > 0) {
				it = upList.getItemAtIndex(0);
			}
		}
		if (it.getValue() instanceof Media) {
			Filedownload.save((Media) it.getValue());
		} else {
			WkTFile f = (WkTFile) it.getValue();
			String path = Executions.getCurrent().getDesktop().getWebApp()
					.getRealPath("/upload/info");
			Filedownload.save(new File(path + "\\" + f.getId().getKfName()),
					null);
		}
	}

	// 删除上传的文件
	public void onClick$deUpload() {

		if (modelListbox.getSize() == 0)
			return;
		Listitem it = upList.getSelectedItem();
		if (it == null) {
			if (modelListbox.getSize() > 0) {
				it = upList.getItemAtIndex(0);
			}
		}

		if (it.getValue() instanceof WkTFile) {
			WkTFile f = (WkTFile) it.getValue();
			String path = Executions.getCurrent().getDesktop().getWebApp()
					.getRealPath("/upload/info");
			File file = new File(path + "\\" + f.getId().getKfName());
			if (file.exists()) {
				file.delete();
			}
			newsService.delete(it.getValue());
		}
		modelListbox.remove(it.getIndex());
	}

	// 删除信息
	public void onClick$delete() throws InterruptedException {
		if (Messagebox.show("确定要删除吗？", "确认", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {

			if (dis.getKbFlag().toString().trim().equals("0")) {
				if (newsService.getFile(dis.getKiId()).size() != 0) {
					List annex = new ArrayList();
					annex = newsService.getFile(dis.getKiId());
					for (int i = 0; i < annex.size(); i++) {
						newsService.delete((WkTFile) annex.get(i));
					}
				}
				// 删除引用此信息的信息
				List slist = newsService.getInfoSiteTime(dis.getKbId()
						.toString());
				if (slist.size() != 0) {
					for (int i = 0; i < slist.size(); i++) {
						WkTInfo in = (WkTInfo) slist.get(i);
						List dlist = newsService
								.getDistributeList(in.getKiId());
						if (dlist.size() != 0) {
							for (int j = 0; j < dlist.size(); j++) {
								WkTDistribute d = (WkTDistribute) dlist.get(j);
								newsService.delete(d);
							}
						}
						newsService.delete(in);
					}
				}
				List d = newsService.getDistributeList(dis.getKiId());
				for (int i = 0; i < d.size(); i++) {
					newsService.delete((WkTDistribute) d.get(i));
				}
				newsService.delete(newsService.getWkTInfo(dis.getKiId()));
				List cnt = newsService.getInfocnt(dis.getKiId());
				for (int j = 0; j < cnt.size(); j++)
					newsService.delete((WkTInfocnt) cnt.get(j));
				Users user = (Users) Sessions.getCurrent().getAttribute("user");
				// mlogService.saveMLog(WkTMlog.FUNC_CMS,
				// "删除信息，id:"+dis.getKiId(), user);
			} else {
				newsService.delete(dis);
				Users user = (Users) Sessions.getCurrent()
						.getAttribute("users");
				// mlogService.saveMLog(WkTMlog.FUNC_CMS,
				// "删除信息，id:"+dis.getKiId(), user);
			}
			Messagebox.show("操作成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			this.detach();
			Events.postEvent(Events.ON_CHANGE, this, null);
		}
	}

	// 重置
	public void onClick$reset() {
		kflmemo.setValue("");
		initWindow(dis);
	}

	public ListModelList getModelList() {
		return modelListbox;
	}
	
	public void onClick$chooseSubjectTerm(){
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
}