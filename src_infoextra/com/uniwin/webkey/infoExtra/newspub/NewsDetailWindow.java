package com.uniwin.webkey.infoExtra.newspub;

/**
 * 显示信息详情界面
 * @author whm
 * 2010-3-20
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.zkoss.zul.Checkbox;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.core.DataBaseSet;
import com.uniwin.webkey.infoExtra.infosort.DomainTreeComposer;
import com.uniwin.webkey.infoExtra.infosort.Sort2;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoIdAnddomainId;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTFileId;
import com.uniwin.webkey.cms.model.WkTFlog;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.subjectterm.SubjectTermTabComposer;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;

public class NewsDetailWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	// 信息数据访问接口
	NewsServices newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");
	TaskService taskService = (TaskService) SpringUtil.getBean("taskService");
	InfoIdAndDomainId idAndDomainIdService = (InfoIdAndDomainId) SpringUtil
			.getBean("infoIdAndDomainIdService");
	WkTInfocnt Infocnt;
	WkTInfo info, in, i;
	WkTDistribute dis, d;
	WkTInfocntId infocntid;
	WkTFlog flog;
	WkTFile wfile;
	WkTFileId fileid;
	// 详细信息界面涉及的各种组件
	Textbox kititle, kititle2, kikeys, kiimage, kisource, kcid, advice,
			taskname, kiSort;
	Datebox kivaliddate;
	Toolbarbutton choice, up, down, choice1, download, upImage, upfile, choose;
	Checkbox kisite;
	FCKeditor kicontent;
	Hbox tupian, suggest;
	ListModelList writeListModel, rebackListModel, auditListModel,
			readListModel, pubListModel;
	Tab writerTab, readTab, auditTab, rebackTab, pubTab;
	Window win;
	Html add;
	Users user;
	Label file;
	Listbox upList;
	List nameList = new ArrayList();
	List userDeptList, flist, slist, silist, zlist, plist;
	ListModelList modelListbox;

	InfoIdAndDomainId infoIdAndDomainIdService = (InfoIdAndDomainId) SpringUtil
			.getBean("infoIdAndDomainIdService");
	List selectList;
	
	private List<WkTSubjectTerm> selectedSts = null;
	private Textbox subjectTerm;

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		modelListbox = new ListModelList(nameList);
		user = (Users) Sessions.getCurrent().getAttribute("users");
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
		// 选择共享栏目
		/*
		 * choice1.addEventListener(Events.ON_CLICK, new EventListener(){ public
		 * void onEvent(Event event) throws Exception {
		 * Executions.getCurrent().setAttribute("mul","1"); NewsTaskSelectWindow
		 * w=(NewsTaskSelectWindow)Executions.createComponents(
		 * "/apps/infoExtra/content/newspub/newtasksel.zul", null,null);
		 * w.doHighlighted(); w.initWindow();
		 * w.addEventListener(Events.ON_CHANGE, new EventListener(){ public void
		 * onEvent(Event arg0) throws Exception {
		 * Executions.getCurrent().setAttribute("state","1");
		 * NewsTaskSelectWindow addwin=(NewsTaskSelectWindow)arg0.getTarget();
		 * slist=addwin.getSlist(); StringBuffer sb=new StringBuffer(); for(int
		 * i=0;i<slist.size();i++){ WkTExtractask c=(WkTExtractask)slist.get(i);
		 * sb.append(c.getKeName().trim()+","); share.setValue(sb.toString());
		 * WkTDistribute dist=new WkTDistribute(); dist.setKeId(c.getKeId());
		 * dist.setKbFlag("1"); dist.setKiId(dis.getKiId());
		 * newsService.save(dist); } } }); } });
		 */
		// 选择发布频道
		choose.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				Executions.getCurrent().setAttribute("mul", "0");
				NewsTaskSelectWindow w = (NewsTaskSelectWindow) Executions
						.createComponents(
								"/apps/infoExtra/content/newspub/newtasksel.zul",
								null, null);
				w.doHighlighted();
				w.initWindow();
				w.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						NewsTaskSelectWindow w = (NewsTaskSelectWindow) arg0
								.getTarget();
						plist = w.getSlist();
						WkTExtractask et = (WkTExtractask) plist.get(0);
						taskname.setValue(et.getKeName());
					}
				});
			}
		});
	}

	private String initflog(WkTFlog flog) {
		this.flog = flog;
		return flog.getKflMemo();
	}

	// 信息分类选择
	public void onClick$chooseSort() {
		// 分类树修改之前
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

	// 判断信息类型，根据类型显示不一样的界面，并获得详细信息
	public void initWindow(WkTDistribute dis) throws IOException {
		this.dis = dis;
		WkTInfo info = newsService.getWkTInfo(dis.getKiId());
		WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
				dis.getKeId()).get(0);
		List sortList = idAndDomainIdService.findByInfoId(info.getKiId());

		String state = dis.getKbStatus().toString().trim();
		if (state.equals("1")) {
			suggest.setVisible(false);
		} else if (state.equals("4")) {
			List floglist = newsService.getflog(dis.getKbId());
			if (floglist.size() != 0) {
				WkTFlog flog = (WkTFlog) floglist.get(0);
				advice.setText(initflog(flog));
			}
		}

		initInfocnt(newsService.getChildNewsContent(info.getKiId()));
		kititle.setValue(info.getKiTitle());
		kititle2.setValue(info.getKiTitle2());
		kikeys.setValue(info.getKiKeys());
		kisource.setValue(info.getKiSource());
		taskname.setValue(e.getKeName());
		kivaliddate.setText(info.getKiValiddate().toString());

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
		modelListbox.addAll(flist);
		upList.setModel(modelListbox);

	}

	// 获取信息内容
	public void initInfocnt(List rlist) {

		String con = "";
		for (int i = 0; i < rlist.size(); i++) {
			WkTInfocnt inf = (WkTInfocnt) rlist.get(i);
			con += inf.getKiContent();
		}
		kicontent.setValue(con);
	}

	// 保存信息
	public void onClick$save() throws InterruptedException, IOException {

		if (kititle.getValue().equals("")) {
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		if (taskname.getValue().equals("")) {
			Messagebox.show("请选择发布栏目！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		WkTInfo info = newsService.getWkTInfo(dis.getKiId());
		WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
				dis.getKeId()).get(0);
		info.setKiTitle(kititle.getValue());
		info.setKiTitle2(kititle2.getValue());
		if (!taskname.getValue().trim().equals(e.getKeName().trim())) {
			WkTExtractask et = (WkTExtractask) plist.get(0);
			info.setKeId(et.getKeId());
		}
		info.setKiKeys(kikeys.getValue());
		info.setKiValiddate(kivaliddate.getText());
		info.setKiSource(kisource.getValue());
		if(selectedSts != null && selectedSts.size() != 0){
			info.setKstids(selectedSts);
		}
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
					String fileName = DateUtil.getDateTimeString(new Date())
							+ "_" + info.getKiId().toString() + "_"
							+ media.getName().toString();
					saveToFile(media, info.getKiId(), info.getKuId());
					WkTFile file = new WkTFile();
					WkTFileId fileid = new WkTFileId(info.getKiId(), fileName,
							media.getName().toString(), info.getKuId(), "1",
							"0");
					file.setId(fileid);
					newsService.save(file);
				}
			}
		}
		dis.setKeId(info.getKeId());
		dis.setKbTitle(kititle.getValue());
		dis.setKbStatus("1");
		Date date = new Date();
		dis.setKbDtime(ConvertUtil.convertDateAndTimeString(date.getTime()));

		newsService.update(dis);
		// 保存共享栏目记录
		/*
		 * if(share.getValue().equals(null)) { List zlist=
		 * newsService.getNewsOfShareChanel(dis.getKiId()); if(zlist.size()!=0)
		 * { for(int i=0;i<zlist.size();i++)
		 * newsService.delete((WkTDistribute)zlist.get(i)); } } else { List
		 * disnew= newsService.getNewsOfShareNew(dis.getKiId()); List zlist=
		 * newsService.getNewsOfShareChanel(dis.getKiId()); if(disnew.size()!=0)
		 * { if(zlist.size()!=0) { for(int i=0;i<zlist.size();i++)
		 * newsService.delete((WkTDistribute)zlist.get(i)); } for(int
		 * i=0;i<disnew.size();i++) { WkTDistribute
		 * distri=(WkTDistribute)disnew.get(i); Date d=new Date();
		 * distri.setKbDtime(ConvertUtil.convertDateAndTimeString(d.getTime()));
		 * distri.setKbStatus("1"); distri.setKbMail(dis.getKbMail());
		 * distri.setKbStrong(dis.getKbStrong());
		 * distri.setKbTitle(dis.getKbTitle()); newsService.update(distri); } }
		 * }
		 */
		saveInfoSort(info);// 保存信息具体分类信息

		Messagebox.show("操作成功！", "Information", Messagebox.OK,
				Messagebox.INFORMATION);
		this.detach();
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	// 发布信息
	public void onClick$saudit() throws InterruptedException, IOException {

		if (kititle.getValue().equals(null)) {
			Messagebox.show("标题不能为空！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		if (!infoIdAndDomainIdService.findByTitle(kititle.getValue()).isEmpty()) {
			Messagebox.show("信息已发布，请删除重复信息！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			return;
		}
		// 更新WkTInfo表
		Date da = new Date();
		WkTInfo info = newsService.getWkTInfo(dis.getKiId());
		info.setKiValiddate(kivaliddate.getText());
		info.setKiTitle(kititle.getText());
		info.setKiTitle2(kititle2.getText());
		info.setKiKeys(kikeys.getText());
		info.setKiSource(kisource.getText());
		if(selectedSts != null && selectedSts.size() != 0){
			info.setKstids(selectedSts);
		}
		info.setKiCtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
		info.setKiAddress(null);
		// 更新WkTInfocnt表
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
			infocnt.setKiFlag("0");
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
					String fileName = DateUtil.getDateTimeString(new Date())
							+ "_" + info.getKiId().toString() + "_"
							+ media.getName().toString();
					saveToFile(media, info.getKiId(), info.getKuId());
					WkTFile file = new WkTFile();
					WkTFileId fileid = new WkTFileId(info.getKiId(), fileName,
							media.getName().toString(), info.getKuId(), "1",
							"0");
					file.setId(fileid);
					newsService.save(file);
				}
			}
		}
		// 更新WkTDistribute表
		dis.setKeId(info.getKeId());
		// 如果所在频道需要审核则送审，否则直接发布
		dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
		dis.setKbTitle(kititle.getText());
		dis.setKbFlag("0");

		WkTExtractask e = (WkTExtractask) taskService.getTaskBykeId(
				info.getKeId()).get(0);
		WkTChanel t = (WkTChanel) taskService.getTpyeById(e.getKcId());
		if (t.getKfId().toString().trim().equals("0")) {
			dis.setKbStatus("0");

			// 发布成html页面
			// Map root = new HashMap();
			// Sessions.getCurrent().setAttribute("root", root);
			// WKT_DOCLIST dList = new WKT_DOCLIST();
			// dList.singleNewsPublic(info, t.getKcId());

		} else {
			dis.setKbStatus("2");
		}
		newsService.update(dis);
		// 共享栏目保存
		/*
		 * if(share.getValue().equals("")) { List zlist=
		 * newsService.getNewsOfShareChanel(dis.getKiId()); if(zlist.size()!=0)
		 * { for(int i=0;i<zlist.size();i++)
		 * newsService.delete((WkTDistribute)zlist.get(i));} } else {
		 * if(newsService.getNewsOfShareNew(dis.getKiId()).size()!=0) { List
		 * zlist= newsService.getNewsOfShareChanel(dis.getKiId());
		 * if(zlist.size()!=0) { for(int i=0;i<zlist.size();i++)
		 * newsService.delete((WkTDistribute)zlist.get(i)); } List disnew=
		 * newsService.getNewsOfShareNew(dis.getKiId()); for(int
		 * i=0;i<disnew.size();i++) { WkTDistribute
		 * distri=(WkTDistribute)disnew.get(i);
		 * distri.setKbColor(dis.getKbColor());
		 * distri.setKbDtime(ConvertUtil.convertDateAndTimeString
		 * (da.getTime())); distri.setKbEm(dis.getKbEm());
		 * distri.setKbRight(dis.getKbRight()); WkTExtractask et=(WkTExtractask)
		 * taskService.getTaskBykeId(distri.getKeId()).get(0); WkTChanel
		 * tt=(WkTChanel) taskService.getTpyeById(e.getKcId());
		 * if(tt.getKfId().toString().equals("0")) distri.setKbStatus("0"); else
		 * distri.setKbStatus("2"); distri.setKbStrong(dis.getKbStrong());
		 * distri.setKbTitle(dis.getKbTitle());
		 * distri.setKbMail(dis.getKbMail()); newsService.update(distri); } }
		 * else { List diso= newsService.getNewsOfShareChanel(dis.getKiId());
		 * for(int i=0;i<diso.size();i++) { WkTDistribute
		 * distri=(WkTDistribute)diso.get(i);
		 * distri.setKbColor(dis.getKbColor());
		 * distri.setKbDtime(ConvertUtil.convertDateAndTimeString
		 * (da.getTime())); distri.setKbEm(dis.getKbEm());
		 * distri.setKbRight(dis.getKbRight()); WkTExtractask et=(WkTExtractask)
		 * taskService.getTaskBykeId(distri.getKeId()).get(0); WkTChanel
		 * tt=(WkTChanel) taskService.getTpyeById(e.getKcId());
		 * if(tt.getKfId().toString().equals("0")) distri.setKbStatus("0"); else
		 * distri.setKbStatus("2"); distri.setKbStrong(dis.getKbStrong());
		 * distri.setKbTitle(dis.getKbTitle()); newsService.update(distri); } }
		 * }
		 */

		// 删除共享到主栏目的信息
		/*
		 * if(share.getText().toString()!="") { List
		 * di=newsService.getDistribute(info.getKiId(),dis.getKeId());
		 * if(di.size()!=0) newsService.delete((WkTDistribute)di.get(0)); }
		 */
		saveInfoSort(info);// 保存信息具体分类信息
		Messagebox.show("操作成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		this.detach();
		Events.postEvent(Events.ON_CHANGE, this, null);
	}

	// 保存上传的附件
	public void saveToFile(Media media, Long mlid, Long kuid)
			throws IOException {
		if (media != null) {
			InputStream objin = media.getStreamData();
			String fileName = DateUtil.getDateTimeString(new Date()) + "_"
					+ mlid.toString() + "_" + media.getName().toString();
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
			String uploadDir = Executions.getCurrent().getDesktop().getWebApp()
					.getRealPath("/upload/info");
			if (uploadDir == null) {
				System.out.println("无法创建存储目录！");
				return;
			}

			File fUploadDir2 = new File(uploadDir); // 在upload文件下创建登录用户文件夹
			String path2 = fUploadDir2.getCanonicalPath(); // 保存在path路径下
			if (!fUploadDir2.exists()) {
				if (!fUploadDir2.mkdir()) {
					System.out.println("无法创建存储目录！");
					return;
				}
			}
			String name = Executions
					.getCurrent()
					.getDesktop()
					.getWebApp()
					.getRealPath(
							"/upload/info" + "//" + fileName.toString().trim());
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			DataOutputStream objout = new DataOutputStream(out);
			try {
				Files.copy(objout, objin);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (out != null) {
				out.close();
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

	// 上传图片
	/*
	 * public void onClick$upImage() throws InterruptedException{ Media
	 * media=null; try { media = Fileupload.get(); } catch (InterruptedException
	 * e1) { e1.printStackTrace(); } if(media instanceof org.zkoss.image.Image){
	 * pics.getChildren().clear(); org.zkoss.zul.Image image = new
	 * org.zkoss.zul.Image(); image.setContent((org.zkoss.image.Image) media);
	 * image.setWidth("25px"); image.setHeight("25px"); image.setParent(pics);
	 * Button b=new Button(); b.setLabel("删除");
	 * b.addEventListener(Events.ON_CLICK,new
	 * org.zkoss.zk.ui.event.EventListener(){ public void onEvent(Event event)
	 * throws Exception { pics.getChildren().clear(); } }); b.setParent(pics);
	 * }else{ Messagebox.show("请选择图片上传！", "上传错误", Messagebox.OK,
	 * Messagebox.ERROR); }
	 * 
	 * }
	 */

	// 重置
	public void onClick$reset() throws IOException {
		initWindow(dis);
	}

	public void onClick$back() {
		win.detach();
	}

	// 删除单条信息
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
				// 删除信息内容
				WkTInfo info = newsService.getWkTInfo(dis.getKiId());
				if (info.getKiType().trim().equals("1")) {
					List cnt = newsService.getInfocnt(dis.getKiId());
					if (cnt.size() != 0) {
						for (int i = 0; i < cnt.size(); i++) {
							newsService.delete((WkTInfocnt) cnt.get(i));
						}
					}
				}
				List d = newsService.getDistributeList(dis.getKiId());
				for (int i = 0; i < d.size(); i++) {
					newsService.delete((WkTDistribute) d.get(i));
				}
				newsService.delete(newsService.getWkTInfo(dis.getKiId()));
			} else if (dis.getKbFlag().toString().trim().equals("1")) {
				newsService.delete(dis);
			}
			Messagebox.show("操作成功！", "Information", Messagebox.OK,
					Messagebox.INFORMATION);
			win.detach();
			Events.postEvent(Events.ON_CHANGE, this, null);
		}
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
