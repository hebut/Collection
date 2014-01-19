package com.uniwin.webkey.infoExtra.core;

/**
 * 采集规则出替换规则
 */

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.ReplaceService;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.model.WkTReplace;
import com.zkoss.org.messageboxshow.MessageBox;

public class ResultReplace extends Window implements AfterCompose{

	
	Textbox oldValue,newValue;
	Checkbox reg,replaceOld,replaceAll;
	Listbox replaceList;
	Listhead repleHead;
	Button moveUp,moveDown;
	
	ListModelList modelList;
	List<WkTReplace> rList=new ArrayList<WkTReplace>();
	private ReplaceService replaceService=(ReplaceService)SpringUtil.getBean("replaceService");
	
	WkTPickreg pickreg;
	public WkTPickreg getPickreg() {
		return pickreg;
	}

	public void setPickreg(WkTPickreg pickreg) {
		this.pickreg = pickreg;
	}

	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		if(replaceList.getSelectedItem()==null){
			moveDown.setDisabled(true);
			moveUp.setDisabled(true);
		}
		
	}

	public void loadReplaceListbox(){
		
		modelList=new ListModelList(rList);
		
		repleHead.setVisible(true);
		replaceList.setModel(modelList);
		replaceList.setItemRenderer(new ListitemRenderer(){
			public void render(final Listitem item, Object obj) throws Exception {
				final WkTReplace replace=(WkTReplace)obj;
				item.setValue(replace);
				Listcell cell1=new Listcell(replace.getOldValue());
				Listcell cell2=new Listcell(replace.getNewValue());
				Listcell cell3=new Listcell(replace.getIsReg());
				Listcell cell4=new Listcell(replace.getIsReplaceOld());
				Listcell cell5=new Listcell(replace.getIsReplaceAll());
				
				item.appendChild(cell1);item.appendChild(cell2);
				item.appendChild(cell3);item.appendChild(cell4);
				item.appendChild(cell5);
				
				item.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						oldValue.setValue(replace.getOldValue());
						newValue.setValue(replace.getNewValue());
						reg.setChecked(Boolean.parseBoolean(replace.getIsReg()));
						replaceOld.setChecked(Boolean.parseBoolean(replace.getIsReplaceOld()));
						replaceAll.setChecked(Boolean.parseBoolean(replace.getIsReplaceAll()));
						
						if(rList!=null && rList.size()>1){
							
							if((item.getIndex()+1)==1){
								moveUp.setDisabled(true);
								moveDown.setDisabled(false);
							}else if((item.getIndex()+1)==rList.size()){
								moveDown.setDisabled(true);
								moveUp.setDisabled(false);
							}else{
								moveDown.setDisabled(false);
								moveUp.setDisabled(false);
							}
							
						}else{
							moveUp.setDisabled(true);
							moveDown.setDisabled(true);
						}
						
					}
					
				});
				
			}
		});
		
	}
	
	public void onClick$save(){
		
		
		if(replaceList.getSelectedItem()==null){
			
			WkTReplace wkTReplace=new WkTReplace();
			wkTReplace.setOldValue(oldValue.getValue());
			wkTReplace.setNewValue(newValue.getValue());
			if(pickreg!=null){
				wkTReplace.setPickId(pickreg.getKpId());
			}
			wkTReplace.setIsReg(reg.isChecked()+"");
			wkTReplace.setIsReplaceOld(replaceOld.isChecked()+"");
			wkTReplace.setIsReplaceAll(replaceAll.isChecked()+"");
			
			rList.add(wkTReplace);
			MessageBox.showInfo("替换规则保存成功！");
			clear();
			
			loadReplaceListbox();
			
		}else{
			
			WkTReplace w=(WkTReplace)replaceList.getSelectedItem().getValue();
			WkTReplace w2;
			for(int i=0;i<rList.size();i++){
				w2=rList.get(i);
				if(w.equals(w2)){
					
					w.setOldValue(oldValue.getValue());
					w.setNewValue(newValue.getValue());
					w.setIsReg(reg.isChecked()+"");
					w.setIsReplaceOld(replaceOld.isChecked()+"");
					w.setIsReplaceAll(replaceAll.isChecked()+"");
					w2=w;
				}
			}
			
			MessageBox.showInfo("替换规则修改成功！");
			clear();
			loadReplaceListbox();
		}
		
	}
	
	
	public void onClick$delete(){
		
		if(replaceList.getSelectedItem()==null){
			MessageBox.showError("请选择删除项！");
		}else{
			
			WkTReplace replace=(WkTReplace)replaceList.getSelectedItem().getValue();
			WkTReplace wkTReplace=replaceService.findByReplace(replace);
			if(wkTReplace!=null){
				
				try {
					if(Messagebox.show("确定删除替换信息？","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
						replaceService.delete(wkTReplace);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				loadReplace();
			}else{
				
				WkTReplace replace2;
				for(int j=0;j<rList.size();j++){
					replace2=rList.get(j);
					if(replace.equals(replace2)){
						try {
							if(Messagebox.show("确定删除替换信息？","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
									rList.remove(j);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			clear();
			loadReplaceListbox();
		}
	}

	public void onClick$moveUp(){
		
		if(replaceList.getSelectedItem()==null){
			MessageBox.showInfo("请选择移动对象！");
		}else{
			
			WkTReplace wkTReplace=(WkTReplace)replaceList.getSelectedItem().getValue();
			Integer id=replaceList.getSelectedItem().getIndex();
			WkTReplace wkTReplace2=rList.get(id-1);
			rList.set(id-1, wkTReplace);
			rList.set(id, wkTReplace2);
			loadReplaceListbox();
			clear();
		}
		
	}
	
	public void onClick$moveDown(){
		
		if(replaceList.getSelectedItem()==null){
			MessageBox.showInfo("请选择移动对象！");
		}else{
			
			WkTReplace wkTReplace=(WkTReplace)replaceList.getSelectedItem().getValue();
			Integer id=replaceList.getSelectedItem().getIndex();
			WkTReplace wkTReplace2=rList.get(id+1);
			rList.set(id+1, wkTReplace);
			rList.set(id, wkTReplace2);
			loadReplaceListbox();
			clear();
		}
		
	}
	
	private void loadReplace(){
		rList=replaceService.findByPid(pickreg.getKpId());
	}
	
	public void init(Listitem Item) {
		
		if(Item!=null){
			WkTPickreg pickreg=(WkTPickreg)Item.getValue();
			this.pickreg=pickreg;
			rList=replaceService.findByPid(pickreg.getKpId());
			loadReplaceListbox();
			
		}else{
			pickreg=null;
		}
		
	}
	
	private void clear(){
		
		oldValue.setValue("");
		newValue.setValue("");
		reg.setChecked(false);
		replaceOld.setChecked(false);
		replaceAll.setChecked(false);
	}
	
	
	public void onClick$newReplace(){
		
		clear();
		replaceList.setSelectedItem(null);
	}
	
	public void onClick$OK(){
		
		/*if(pickreg!=null){
			
			WkTReplace wkTReplace;
			for(int r=0;r<rList.size();r++){
				wkTReplace=rList.get(r);
			}
		}else{
//			Sessions.getCurrent().setAttribute("replaceList", rList);
		}*/
		
		Events.postEvent(Events.ON_CHANGE, this, rList);
		this.detach();
	}
	
	
	public void onClick$cancel(){
		rList.clear();
		this.detach();
	}
	
}
