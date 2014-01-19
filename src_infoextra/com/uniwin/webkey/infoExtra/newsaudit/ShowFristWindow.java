package com.uniwin.webkey.infoExtra.newsaudit;


import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.cms.model.WkTChanel;


public class ShowFristWindow extends Window implements AfterCompose{

	private static final long serialVersionUID = 5576528748215362191L;
	
	
	//维护页面中的标签页组件
	Tabbox centerTabbox;  	
	//页面中左侧导航条部分对应对象
	//South south;	
	//ҳ���ж�Ӧ��һ���˵��Ͷ����˵�	
	Menubar onebar,twobar;	
	NewsServices newsService;	
	//页面中对应的一级菜单和二级菜单	
	Panelchildren leftPanel;	
	Listbox list;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	//用户数据访问接口
	ListModelList infoListModel;


	public void afterCompose() {	
		Components.wireVariables(this, this);
		Components.addForwards(this, this);	
	}
	
	
	public void initWindow()
	{
		//获得一级标题列表
		List olist=taskService.getChildType(Long.parseLong("1"));
		
		//初始化一级标题
		for(int i=0;i<olist.size();i++){
			WkTChanel title=(WkTChanel)olist.get(i);
    		//判断用户是否有此权限显示此标题		
    		  Menuitem it=new Menuitem();
    		  it.setLabel(title.getKcName());
    		  it.setAttribute("t_id", title);
    		  onebar.appendChild(it);
    		  //添加点击一级标题显示二级标题事件监听
    		  it.addEventListener(Events.ON_CLICK,new EventListener(){			
			   public void onEvent(Event arg0) throws Exception {
				  Menuitem it=(Menuitem)arg0.getTarget();	
				  WkTChanel tid=(WkTChanel)it.getAttribute("t_id");
				  reloadList(tid);
				  appendMenuItem(tid.getKcId());
				 
			    }			  
		      });		
    		}
		//默认初始化显示二级菜单
		if(olist.size()>0){
			WkTChanel title=(WkTChanel)olist.get(0);
			appendMenuItem(title.getKcId());						
		}   
		WkTChanel title=(WkTChanel)olist.get(0);
		List clist=taskService.getChildType(title.getKcId());
		WkTChanel ti=(WkTChanel)clist.get(0);
		reloadList(ti);
	}
	/**
	 * <li>功能描述：显示一级标题tid所属二级标题
	 * @param tid 父标题ID
	 * void 
	 * @author DaLei
	 */
	public void appendMenuItem(Long tid){
		List olist=taskService.getChildType(tid);
		twobar.getChildren().clear();
		boolean showleft=false;
		if(olist.size()!=0)
		{
			twobar.setVisible(true);
		for(int i=0;i<olist.size();i++){
			final WkTChanel title=(WkTChanel)olist.get(i);
    		 Menuitem tit=new Menuitem();
    		tit.setLabel(title.getKcName());
    		tit.setAttribute("t_id", title.getKcId());
    		twobar.appendChild(tit);
    		tit.addEventListener(Events.ON_CLICK,new EventListener(){			
			public void onEvent(Event arg0) throws Exception {
				Menuitem it=(Menuitem)arg0.getTarget();	
				reloadList(title);
			  }			  
		    });		
    		if(!showleft){
    			appendLeftTree(tit);
    			showleft=true;
    		}
    	  }
		}
		else
		{
			twobar.setVisible(false);
		}
    	}
	//加载信息列表
	public void reloadList(WkTChanel t)
	{ 
		List newsList=newsService.getNewsOfinfo(t.getKcId());
		infoListModel=new ListModelList();
		infoListModel.addAll(newsList);
		list.setModel(infoListModel);
		list.setItemRenderer(new NewsShowListRenderer(newsService,infoListModel,list));
	}	
	/**
	 * <li>功能描述：显示二级标题时候默认显示三级标题
	 * @param it 二级标题的菜单项
	 * void 
	 * @author DaLei
	 */
	public void appendLeftTree(Menuitem it){
		Long tid=(Long)it.getAttribute("t_id");	
		if(taskService.getChildType(tid).size()==0){
			//如果没有下级标题，则代表当前要在左侧打开某个操作菜单树，应为某个菜单zul页面
			WkTChanel title=(WkTChanel)taskService.get(WkTChanel.class, tid);
		}else{
		}
	}
}

