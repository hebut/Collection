package com.uniwin.webkey.infoExtra.tasktemp;
/**
 * <li>任务列表组件，根据页面使用参数配置，可以是列表或者下拉列表
 */
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.cms.model.WkTChanel;



public class TaskListbox extends Listbox implements AfterCompose {

	ListModelList wmodelList;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	public void afterCompose() {
		Components.wireVariables(this, this);
	}
	
	 public void addTaskListBoxItem(ListModelList cml,Long pid,int dep,WkTChanel tt){
		  	List wList;
		  	if(tt==null){
		  		wList=taskService.getChildType(pid);		
		  	}else{
		  		wList=taskService.getChildTasktype(pid,tt.getKcId());
		  	}    	
				for(int i=0;i<wList.size();i++){		
					WkTChanel w=(WkTChanel)wList.get(i);
					w.setDep(dep);
					cml.add(w);
					addTaskListBoxItem(cml,w.getKcId(),dep+1,tt);
				}
		  }
	  public void initAllTaskSortSelect(final WkTChanel arg,final WkTChanel a){
		  	wmodelList=new ListModelList();
		  	WkTChanel t=new WkTChanel();
		  	t.setDep(0);
			 final List wlist= taskService.findAll(WkTChanel.class);
			  if(wlist==null||wlist.size()==0){
				 addTaskListBoxItem(wmodelList,Long.parseLong("0"),0,arg);
			  }else {
				  addTaskListBoxItem(wmodelList,Long.parseLong("0"),0,null);
			  }
				this.setModel(wmodelList);
				this.setItemRenderer(new ListitemRenderer(){
			        public void render(Listitem item, Object data) throws Exception {
			        	WkTChanel w=(WkTChanel)data;		         
			        	   item.setValue(w);
				        	int dep=w.getDep();
							String bla="";
							while(dep>0){
							    bla+="　";
								dep--;
							}
							if(arg!=null&&w.getKcId().intValue()==arg.getKcId().intValue()){
								item.setSelected(true);
							}
							if(a!=null&&w.getKcId().intValue()==a.getKcId().intValue())
							{
								 item.setStyle("color:#e0e0e0");
							}
							item.setLabel(bla+w.getKcName());
			        }
					});
		  }

}