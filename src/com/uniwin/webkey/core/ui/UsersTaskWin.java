package com.uniwin.webkey.core.ui;

import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.UsersEx;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.newsaudit.NewsauditTreeModel;
import com.uniwin.webkey.infoExtra.newspub.NewsTreeModel;

public class UsersTaskWin extends Window implements AfterCompose
{
	
	Tree edittree,audittree;
	TaskService taskService = (TaskService)SpringUtil.getBean("taskService");
	NewsTreeModel ntm;// 树的模型组件
	//树的模型组件
	NewsauditTreeModel natm;
    UsersEx  user;     
    WkTWebsite website;
    Integer edit=0,audit=0;
    Window taskwind;
    IAuthManager authManager = (IAuthManager)SpringUtil.getBean("authManager");
	public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        Map map = Executions.getCurrent().getArg();
        user = (UsersEx) map.get("user");
        website = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");
        edittree.getPagingChild().setMold("os");
        audittree.getPagingChild().setMold("os");
		initedittree();
        initaudittree();
        taskwind.setTitle("用户【"+user.getName()+"】的任务分配情况---编辑任务"+edit+"项;审核任务"+audit+"项");

    }
	
	/**
	 * 加载左侧栏目树，当前用户不具有发布权限的栏目不可用
	 * @throws DataAccessException
	 * @throws ObjectNotExistException
	 */
	public void initedittree() 
    {
		List nlist=taskService.getChildType(Long.parseLong("0"));
		ntm=new NewsTreeModel(nlist,taskService);
		edittree.setModel(ntm);
		edittree.setTreeitemRenderer(new TreeitemRenderer(){
			
			public void render(Treeitem item, Object data) throws Exception {
			if(data instanceof WkTExtractask)
			{
			WkTExtractask et=(WkTExtractask)data;
			//判断用户权限
			item.setValue(et);
			item.setOpen(true);
			
			if(et.getKeName().length()>5)
			{
				item.setLabel(et.getKeName().substring(0, 5)+"...");
				item.setTooltiptext(et.getKeName());
			}
			else
			{
				item.setLabel(et.getKeName());
			}
			
			List alist = authManager.getWkTTaskByUWRandOperation(user.getUserId(), website.getKwId().intValue(), "FB");
			if(alist!=null){
				
			int count=0;
			for(int i=0;i<alist.size();i++)
			{
		     WkTExtractask etask=(WkTExtractask) alist.get(i);
		     	if(etask.getKeId().toString().trim().equals(et.getKeId().toString().trim())){
		     		count++;
		     	}
			}
			
			if(count==0)
			{
				item.setDisabled(true);
			}
			else
			{
				item.setCheckable(true);
				item.setSelected(true);
				edit+=1;
			}
		}
			}
			else if(data instanceof WkTChanel)
			{
				WkTChanel tt=(WkTChanel)data;
				item.setValue(tt);
				if(tt.getKcName().length()>5)
				{
					item.setLabel(tt.getKcName().substring(0, 5)+"...");
					item.setTooltiptext(tt.getKcName());
				}
				else
				{
					item.setLabel(tt.getKcName());
				}
				item.setCheckable(false);
				item.setOpen(true);
			}
			}
		});
	}
	
	public void initaudittree()
	{
		List nlist=taskService.getChildType(Long.parseLong("0"));
		natm=new NewsauditTreeModel(nlist,taskService);
		audittree.setModel(natm);
		audittree.setTreeitemRenderer(new TreeitemRenderer()
		{public void render(Treeitem item, Object data) throws Exception {
			if(data instanceof WkTExtractask)
			{
				WkTExtractask et=(WkTExtractask)data;
				item.setValue(et);
				
				if(et.getKeName().length()>5)
				{
					item.setLabel(et.getKeName().substring(0, 5)+"...");
					item.setTooltiptext(et.getKeName());
				}
				else
				{
					item.setLabel(et.getKeName());
				}
				
				List alist = authManager.getWkTTaskByUWRandOperation(user.getUserId(), website.getKwId().intValue(), "SH");
				int count=0;
				for(int i=0;i<alist.size();i++)
				{
			     WkTExtractask etask=(WkTExtractask) alist.get(i);
			     	if(etask.getKeId().toString().trim().equals(et.getKeId().toString().trim())){
			     		count++;
			     	}
				}
				if(count==0)
				{
					item.setDisabled(true);
				}
				else
				{
					item.setCheckable(true);
					item.setSelected(true);
					audit+=1;
				}
			}
			else if(data instanceof WkTChanel)
			{
				WkTChanel tt=(WkTChanel)data;
				item.setValue(tt);
				if(tt.getKcName().length()>5)
				{
					item.setLabel(tt.getKcName().substring(0, 5)+"...");
					item.setTooltiptext(tt.getKcName());
				}
				else
				{
					item.setLabel(tt.getKcName());
				}
				item.setCheckable(false);
				item.setOpen(true);
			}
			}
		});
	}
}