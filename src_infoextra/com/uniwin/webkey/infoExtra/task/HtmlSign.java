package com.uniwin.webkey.infoExtra.task;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.infoExtra.itf.SignService;
import com.uniwin.webkey.infoExtra.model.WKTHtmlSign;
import com.zkoss.org.messageboxshow.MessageBox;

public class HtmlSign extends Window implements AfterCompose{

	Listbox signList;
	Textbox signValue,signName;
	ListModelList listModelList;
	Window signWin;
	Button moveUp,moveDown;
	List<WKTHtmlSign> sList=new ArrayList<WKTHtmlSign>();
	private SignService signService=(SignService)SpringUtil.getBean("signService");
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		loadSignList();
		if(signList.getSelectedItem()==null){
			moveDown.setDisabled(true);
			moveUp.setDisabled(true);
		}
	}

	public void Init() {
		sList=signService.findAll();
		loadSignList();
	}
	
	public void loadSignList(){
		
		listModelList=new ListModelList(sList);
		signList.setModel(listModelList);
		signList.setItemRenderer(new ListitemRenderer(){

			public void render(final Listitem item, Object arg1) throws Exception {
				final WKTHtmlSign htmlSign=(WKTHtmlSign)arg1;
				item.setValue(htmlSign);
				Listcell c1=new Listcell(htmlSign.getKsName());
				Listcell c2=new Listcell(htmlSign.getKsValue());
				item.appendChild(c1);item.appendChild(c2);
				
				item.addEventListener(Events.ON_CLICK, new EventListener(){
					public void onEvent(Event arg0) throws Exception {
						signName.setValue(htmlSign.getKsName());
						signValue.setValue(htmlSign.getKsValue());
						if(sList!=null && sList.size()>1){
							
							if((item.getIndex()+1)==1){
								moveUp.setDisabled(true);
								moveDown.setDisabled(false);
							}else if((item.getIndex()+1)==sList.size()){
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
	
	/* 暂存列表 */
	public void onClick$saveSign(){
		
		List<String> nameList=new ArrayList<String>();
		List<String> valueList=new ArrayList<String>();
		for(int a=0;a<sList.size();a++){
			nameList.add(sList.get(a).getKsName());
			valueList.add(sList.get(a).getKsValue());
		}
		String sName=signName.getValue().trim();
		String sValue=signValue.getValue().trim();
	if(!sName.equals("") && !sValue.equals("")){	
		if(signList.getSelectedItem()==null){
			
			WKTHtmlSign sign=new WKTHtmlSign();
			sign.setKsName(sName);
			sign.setKsValue(sValue);
			if(!nameList.contains(sign.getKsName())&& !valueList.contains(sign.getKsValue())){
				sList.add(sign);
			}else{
				MessageBox.showWarning("名称或值重复！");
			}
			
			loadSignList();
		
	}else{
		WKTHtmlSign sign=(WKTHtmlSign) signList.getSelectedItem().getValue();
		WKTHtmlSign sign2;
		for(int i=0;i<sList.size();i++){
			sign2=sList.get(i);
			if(sign2.getKsName().equals(sign.getKsName())){
				sign.setKsName(signName.getValue());
				sign.setKsValue(signValue.getValue());
				sign2=sign;
			}
		}
		loadSignList();
	}
	
	}else{
		MessageBox.showWarning("请将信息填充完整！");
	}
	
}
	
	public void onClick$delete(){
		
		if(signList.getSelectedItem()==null){
			MessageBox.showWarning("请选择删除项！");
		}else{
			WKTHtmlSign sign=(WKTHtmlSign)signList.getSelectedItem().getValue();
			WKTHtmlSign wHtmlSign=signService.findBySignValue(sign.getKsValue().trim());
			if(wHtmlSign.equals("")){
				WKTHtmlSign sign2;
				for(int i=0;i<sList.size();i++){
					sign2=sList.get(i);
					if(sign.getKsName().equals(sign2.getKsName())){
						try {
							if(Messagebox.show("确定删除标记信息？","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
								sList.remove(i);
								signValue.setValue("");
								signName.setValue("");
								loadSignList();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				try {
					if(Messagebox.show("确定删除标记信息？","提示信息",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION)==Messagebox.YES){
						signService.delete(sign);
						signValue.setValue("");
						signName.setValue("");
						Init();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
		}//else
			
		}
	}
	
	public void onClick$moveUp(){
		if(signList.getSelectedItem()==null){
			MessageBox.showWarning("请选择移动对象！");
		}else{
			WKTHtmlSign htmlSign=(WKTHtmlSign) signList.getSelectedItem().getValue();
			Integer cId=signList.getSelectedItem().getIndex();
			WKTHtmlSign sign=sList.get(cId-1);
			sList.set(cId-1,htmlSign);
			sList.set(cId, sign);
			loadSignList();
		}
	}
	
	public void onClick$moveDown(){
		if(signList.getSelectedItem()==null){
			MessageBox.showWarning("请选择移动对象！");
		}else{
			WKTHtmlSign htmlSign=(WKTHtmlSign) signList.getSelectedItem().getValue();
			Integer cId=signList.getSelectedItem().getIndex();
			WKTHtmlSign hmSign=sList.get(cId+1);
			sList.set(cId+1,htmlSign);
			sList.set(cId, hmSign);
			loadSignList();
		}
	}
	
	public void onClick$newSign(){
		signName.setValue("");
		signValue.setValue("");
		signList.setSelectedItem(null);
	}
	
	public void onClick$OK(){
		if(sList.size()!=0){
			WKTHtmlSign sign;
			for(int j=0;j<sList.size();j++){
				sign=sList.get(j);
				sign.setKsOrderid(Long.parseLong(j+""));
				if(sign.getKsId()!=null){
					signService.update(sign);
				}else{
					signService.save(sign);
				}
				
			}
		}
		signWin.detach();
	}
	
	public void onClick$cancel(){
		sList.clear();
		signWin.detach();
	}

	

	
}
