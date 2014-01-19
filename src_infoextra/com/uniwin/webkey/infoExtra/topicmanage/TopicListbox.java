package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;

public class TopicListbox extends Listbox implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    ListModelList tmodelList;
    private PersonTopicService personTopicS;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	Integer kuId;
	public void afterCompose() {
		Components.wireVariables(this, this);
		
	}
	
	public void addTopicListboxItem(ListModelList cml,Long pid,Integer dep,WKTPersonTopic tt){
	  	List wList;
	  	if(tt==null){
	  		wList=personTopicS.getChildType(pid,kuId);		
	  	}else{
	  		wList=personTopicS.getChildTopictype(pid,tt.getKptId(),kuId);
	  	}    	
			for(int i=0;i<wList.size();i++){		
				WKTPersonTopic w=(WKTPersonTopic)wList.get(i);
				w.setDep(dep);
				cml.add(w);
				addTopicListboxItem(cml,w.getKptId(),dep+1,tt);
		}
	}
    
	  public void initAllTopicSortSelect(final WKTPersonTopic arg,final WKTPersonTopic a,Integer kuId){
		  this.kuId = kuId;
		  tmodelList=new ListModelList();
		  WKTPersonTopic t=new WKTPersonTopic();
		  	t.setDep(0);
			 final List wlist= personTopicS.findByKuId(kuId);
			  if(wlist==null||wlist.size()==0){
				 addTopicListboxItem(tmodelList,Long.parseLong("0"),0,arg);
			  }else {
				  addTopicListboxItem(tmodelList,Long.parseLong("0"),0,null);
			  }
				this.setModel(tmodelList);
				this.setItemRenderer(new ListitemRenderer(){
			        public void render(Listitem item, Object data) throws Exception {
			        	WKTPersonTopic w=(WKTPersonTopic)data;		         
			        	   item.setValue(w);
				        	int dep= w.getDep();
							String bla="";
							while(dep>0){
							    bla+="　";
								dep--;
							}
						
							if(arg!=null){
								if(w.getKptId().intValue()==arg.getKptId().intValue())
								{
								item.setSelected(true);
								}
							}
							if(a!=null&&(w.getKptId().intValue()==a.getKptId().intValue()))
							{
								 item.setStyle("color:#e0e0e0");
							}
							item.setLabel(bla+w.getKptName());
			        }
				});
		  }
}
