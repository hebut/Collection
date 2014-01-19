package com.uniwin.webkey.infoExtra.newscheck;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.uniwin.webkey.cms.itf.IPageTemplateManager;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.ui.Arraylist;
import com.uniwin.webkey.infoExtra.infosort.DomainTreeComposer;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.model.WkTSubjectTerm;
import com.uniwin.webkey.infoExtra.newscenter.InfoShow;
import com.uniwin.webkey.infoExtra.newsmanage.NewsManageListRenderer;
import com.uniwin.webkey.infoExtra.subjectterm.SubjectTermTabComposer;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class InfoCheck extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7874180453632699536L;
	private Textbox key_word, main_word, from, title, kiSort;
	private List selectList,mainWordList;
	private Datebox begintime, endtime;
	private Listbox newsListbox;
	private ListModelList newsListModel;
	private User user;
	private NewsServices info_newsService = (NewsServices) SpringUtil
			.getBean("info_newsService");

	
	//分页加载
	Paging pageListbox;
	IPageTemplateManager pageTemp=(IPageTemplateManager)SpringUtil.getBean("pageTemp");
	int pageSize;
	int jishu;
	int total;
	int pgno=0;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initShow();
		initWindow();
	}

	public void initShow() {
		newsListbox.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem arg0, Object arg1) throws Exception {
				final WkTDistribute distribute = (WkTDistribute) arg1;
				WkTInfo info = info_newsService.getWkTInfo(distribute.getKiId());
				Listcell c=new Listcell(""); //用户Listbox的头一列设置为方框选择项，而加的空数据
				arg0.appendChild(c);
				Listcell c1 = new Listcell(arg0.getIndex() + 1 + "");
				Listcell c2 = new Listcell(info.getKiTitle());
				c2.setStyle("color:blue");
				c2.addEventListener(Events.ON_CLICK, new EventListener() {

					public void onEvent(Event arg0) throws Exception {

						WkTInfo wkTInfo = info_newsService
								.getWkTInfo(distribute.getKiId());
						InfoShow infoShow = (InfoShow) Executions
								.createComponents(
										"/apps/infoExtra/content/infocenter/showdetail.zul",
										null, null);
						infoShow.initWindow(wkTInfo);
						infoShow.doHighlighted();
					}

				});
				Listcell c3 = new Listcell(info.getKiSource());
				//Listcell c4 = new Listcell(info.getKiAuthname());
				Listcell c5 = new Listcell(distribute.getKbDtime());
				arg0.appendChild(c1);
				arg0.appendChild(c2);
				arg0.appendChild(c3);
				//arg0.appendChild(c4);
				arg0.appendChild(c5);
			}
		});
	}

	public void initWindow() {
		List<WkTDistribute> infoList = info_newsService.getPublishedNews(null,
				null, null, null, null, null, null, 2);
		newsListModel = new ListModelList();
		newsListModel.addAll(infoList);
		newsListbox.setModel(newsListModel);
	}
/*	public void initWindow() {
		pageSize=pageListbox.getPageSize();
		List<WkTDistribute> infoList = info_newsService.getPublishedNews(null,null, null, null, null, null, null, 15);
		pageListbox.setTotalSize(infoList.size());
		pageListbox.addEventListener("onPaging", new EventListener(){
			
			public void onEvent(Event event) throws Exception {
				PagingEvent pe = (PagingEvent) event;  
				 pgno = pe.getActivePage();
				redraw(pgno, pageSize); 
			}

		});
		reloadList();
	}*/

	//加载信息列表
	public void reloadList()
	{ 
		redraw( pgno, pageSize);
	}	
	private void redraw(int start, int pageSize) {
		int count=start * pageSize;
		this.jishu=count;
		List sList = null;
		try {
			sList=pageTemp.getListByPage(start, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		ListModelList infoList = new ListModelList();
		infoList.addAll(sList);
		newsListbox.setModel(infoList);
		//infomanagelist.setItemRenderer(new NewsManageListRenderer(orinewsService,infomanageListModel,infomanagelist,jishu,start,pageSize));
	}
	
	@SuppressWarnings("unchecked")
	public List<String> formatStringToList(String strings) {
		if (strings != null && (!strings.equals(""))) {
			List<String> stringList = new Arraylist<String>();
			String[] s = strings.split(";");
			for (int i = 0; i < s.length; i++) {
				stringList.add(s[i]);
			}
			return stringList;
		} else {
			return null;
		}
	}

	public void onClick$search() {
		List<String> KeyWordList = null;
		KeyWordList = formatStringToList(key_word.getValue());
		List<WkTDistribute> infoList = info_newsService.getPublishedNews(
				title.getValue(),
				from.getValue(),
				begintime.getValue() == null ? null : ConvertUtil
						.convertDateAndTimeString(begintime.getValue()),
				endtime.getValue() == null ? null : ConvertUtil
						.convertDateAndTimeString(endtime.getValue()),
					mainWordList, selectList, KeyWordList, null);
		//newsListbox.setModel(new ListModelList(infoList));
		searchinfo(infoList,newsListbox,newsListModel);
	}
	
	//重载搜索到的信息列表
	public void searchinfo(List slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	
	public void onClick$chooseMain() {
		final SubjectTermTabComposer s = (SubjectTermTabComposer) Executions
				.createComponents(
						"/apps/infoExtra/content/subjectterm/subjectTermTab.zul", null,
						null);
		s.doHighlighted();
		s.initWin(main_word);
		s.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				mainWordList = s.getSelectedSubjectTerms();
				String mainStrings = "";
				for(int i=0;i<mainWordList.size();i++){
					WkTSubjectTerm t = (WkTSubjectTerm)mainWordList.get(i);
					mainStrings += t.getKiName() + ";";
				}
				main_word.setValue(mainStrings);
			}
		});
	}

	public void onClick$chooseSort() {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
