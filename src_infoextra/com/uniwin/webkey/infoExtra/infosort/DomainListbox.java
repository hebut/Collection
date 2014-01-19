package com.uniwin.webkey.infoExtra.infosort;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.PersonTopicService;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WKTPersonTopic;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;

public class DomainListbox extends Listbox implements AfterCompose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    ListModelList tmodelList;
    private InfoDomainService infodomainService;
	private TaskService taskService=(TaskService)SpringUtil.getBean("taskService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		
	}
	
	public void addTopicListboxItem(ListModelList cml,Integer pid,Integer dep,WkTInfoDomain tt,WkTInfoSort s){
	  	List wList;
	  	if(tt==null){
	  		wList=infodomainService.getChildType(pid, s.getKsId());		
	  	}else{
	  		wList=infodomainService.getChildDomainType(pid, tt.getKiId(), s.getKsId());
	  	}    	
			for(int i=0;i<wList.size();i++){		
				WkTInfoDomain w=(WkTInfoDomain)wList.get(i);
				w.setDep(dep);
				cml.add(w);
				addTopicListboxItem(cml,w.getKiId(),dep+1,tt,s);
		}
	}
    
	  public void initAllTopicSortSelect(final WkTInfoDomain arg,final WkTInfoDomain a,final WkTInfoSort s){
		  tmodelList=new ListModelList();
		  WkTInfoDomain t=new WkTInfoDomain();
		  	t.setDep(0);
			 final List wlist= infodomainService.findBySortId(s.getKsId());
			  if(wlist==null||wlist.size()==0){
				 addTopicListboxItem(tmodelList,0,0,arg,s);
			  }else {
				  addTopicListboxItem(tmodelList,0,0,null,s);
			  }
				this.setModel(tmodelList);
				this.setItemRenderer(new ListitemRenderer(){
			        public void render(Listitem item, Object data) throws Exception {
			        	WkTInfoDomain w=(WkTInfoDomain)data;		         
			        	   item.setValue(w);
				        	int dep= w.getDep();
							String bla="";
							while(dep>0){
							    bla+="　";
								dep--;
							}
						
							if(arg!=null){
								if(w.getKiId().intValue()==arg.getKiId().intValue())
								{
								item.setSelected(true);
								}
							}
							if(a!=null&&(w.getKiId().intValue()==a.getKiId().intValue()))
							{
								 item.setStyle("color:#e0e0e0");
							}
							item.setLabel(bla+w.getKiName());
			        }
				});
		  }
}
