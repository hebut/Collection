package com.uniwin.webkey.infoExtra.taskmanager;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;


public class TaskManageAddWindow extends Window implements AfterCompose{

	
	private Users user;
	Session currSession = Sessions.getCurrent();
	WkTWebsite website;
	
	private WkTExtractask extractask;
	private WkTChanel tasktype;
	TaskService  taskService = (TaskService)SpringUtil.getBean("taskService");
	
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
        Components.addForwards(this, this);
        user = (Users) currSession.getAttribute("users");
        website = (WkTWebsite)currSession.getAttribute("domain_defult");
	}

	
	
	/** 初始化增加新栏目的页面 */
    /*public void setEditChanel(Object obj)
    {
    	if(obj!=null)
    	{
    		this.c = obj;
    	}
        List cl = chanelService.findAll(WkTChanel.class);
        user = (Users) currSession.getAttribute("users");
        
        // 初始化栏目所属站点
        wname.initWebsiteSelect(user,website);
        
        // 初始化父栏目
        pselected.initSNewChanelSelect(website, c);
        if (wname.getSelectedItem() == null)
        {
            List list = (List)currSession.getAttribute("wsList");
            if (null!=list&&list.size()>0)
            {
                webs = (WkTWebsite) list.get(0);
            }
        } else
        {
            Listitem item = wname.getSelectedItem();
            webs = (WkTWebsite) item.getValue();
        }
        if (c == null && cl.size() != 0)
        {
            pselected.setSelectedIndex(0);
        }
        // 栏目所属站点
        wname.addEventListener(Events.ON_SELECT, new EventListener()
        {
            public void onEvent(Event event) throws Exception
            {
                Listitem item = wname.getSelectedItem();
                WkTWebsite website = (WkTWebsite) item.getValue();
                pselected.initSNewChanelSelect(website, null);
            }
        });
    }
    */
    
	
	
}
