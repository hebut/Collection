package com.uniwin.webkey.infoExtra.infosubject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.itf.InfoSubjectService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTSubject;
import com.uniwin.webkey.infoExtra.task.TaskItemRenderer;
import com.uniwin.webkey.infoExtra.task.TasktypeTreeModel;
import com.zkoss.org.messageboxshow.MessageBox;

public class SubjectTree extends Window implements AfterCompose{

	
	Tree subTree;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	private InfoSubjectService infosubjectService=(InfoSubjectService)SpringUtil.getBean("infosubjectService");
	
	ListModelList modelList;
	List clist;
	WkTChanel type;
	TasktypeTreeModel ctm;
	Tab choose,build;
	
	WkTSubject subject;
	Textbox site1,site2,task1,extract,task2;
	
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		initTree();
		
		/*choose.addEventListener(Events.ON_SELECT, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				extract.setValue(null);
				task2.setValue(null);
			}
		});
		build.addEventListener(Events.ON_SELECT, new EventListener(){

			public void onEvent(Event arg0) throws Exception {
				task1.setValue(null);
			}
		});*/
		
	}

	private void initTree() {
		
		subTree.getPagingChild().setMold("os");
		clist=taskService.getChildType(Long.parseLong(0+""));
		ctm=new TasktypeTreeModel(clist,taskService);
		subTree.setModel(ctm);
		List tList=taskService.findAllTaskOrder();
		
		if(tList!=null && tList.size()>0){
			WkTExtractask e=(WkTExtractask)tList.get(0);
			type=taskService.findByFolderID(e.getKcId());
		}
		
		subTree.setTreeitemRenderer(new TaskItemRenderer()
	     {
	    		public void render(final Treeitem item, Object data) throws Exception {
	    			final WkTChanel task=(WkTChanel)data;
	    			item.setValue(task);
	    			item.setLabel(task.getKcName());
	    			item.setOpen(true);
					
	    		}
	     });
	}

	
	
	public void onClick$ok(){
		
		if(choose.isSelected()==true){
			if(subTree.getSelectedItem()==null){
				MessageBox.showInfo("请选择采集方向！");
				return;
			}/*else if(site1.getValue().equals("")|| task1.getValue()==null){
				MessageBox.showInfo("请输入网站名称！");
				return;
			}*/else if(task1.getValue().equals("")|| task1.getValue()==null ){
				MessageBox.showInfo("请输入任务名称！");
				return;
			}else{
				String taskName=task1.getValue().trim();
				WkTChanel chanel=(WkTChanel) subTree.getSelectedItem().getValue();
				WkTExtractask extractask=new WkTExtractask();
				extractask.setKeName(taskName);
				extractask.setKcId(chanel.getKcId());
				extractask.setKeRepeat("true");
				extractask.setKeStatus(0L);
				taskService.save(extractask);
				Date date = new Date();
				String subAtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				subject.setSubATime(subAtime);
				subject.setSubStatus(1);
				infosubjectService.update(subject);
				
				Events.postEvent(Events.ON_CHANGE, this, null);
				MessageBox.showInfo("保存成功！");
				this.detach();
			}
			
		}else if(build.isSelected()==true){
			
			if(extract.getValue().equals("") || extract.getValue()==null){
				MessageBox.showInfo("请输入采集方向！");
				return;
			}else if(task2.getValue().equals("")|| task2.getValue()==null ){
				MessageBox.showInfo("请输入任务名称！");
				return;
			}else{
				String chanelName=extract.getValue();
				String taskName=task2.getValue();
				WkTChanel wkTChanel=new WkTChanel();
				wkTChanel.setKcName(chanelName);
				wkTChanel.setKcPid(1L);//此处存在漏洞！
				wkTChanel.setKwId(1L);
				wkTChanel.setKcOrdno(10L);
				wkTChanel.setKcKind(0L);
				wkTChanel.setKfId(1L);
				taskService.save(wkTChanel);
				
				WkTExtractask wkTExtractask=new WkTExtractask();
				wkTExtractask.setKeName(taskName);
				wkTExtractask.setKcId(wkTChanel.getKcId());
				wkTExtractask.setKeRepeat("true");
				wkTExtractask.setKeStatus(0L);
				taskService.save(wkTExtractask);
				
				subject.setSubStatus(1);
				infosubjectService.update(subject);
				
				Events.postEvent(Events.ON_CHANGE, this, null);
				MessageBox.showInfo("保存成功！");
				this.detach();
			}
			
		}
		
	}
	
	
	public void onClick$canel(){
		this.detach();
	}

	public void initWin(WkTSubject wkTSubject) {
		this.subject=wkTSubject;
	}
	
	
}
