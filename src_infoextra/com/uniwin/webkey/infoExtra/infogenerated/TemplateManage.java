package com.uniwin.webkey.infoExtra.infogenerated;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.model.InfoUser;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.itf.InfoGeneratedService;
import com.uniwin.webkey.infoExtra.model.TemplateFile;
import com.uniwin.webkey.infoExtra.model.WKTInfoEmail;

public class TemplateManage extends Window implements AfterCompose {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1071271664593943549L;
	private Listbox templatelistbox;
	private ListModelList infoListModel;
	private Users user;
	private InfoGeneratedService infoGeneratedService;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		user=(Users)Sessions.getCurrent().getAttribute("users");		
	}
	
	public void initWindow(){
		List<TemplateFile> templateList = infoGeneratedService.findTemplateAllFile();
		infoListModel = new ListModelList();
		infoListModel.addAll(templateList);
		templatelistbox.setModel(infoListModel);
		templatelistbox.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem arg0, Object arg1) throws Exception {
				final TemplateFile template = (TemplateFile) arg1;
				arg0.setValue(template);
				Listcell c0 = new Listcell("");
				Listcell c1 = new Listcell(arg0.getIndex() + 1 + "");
				Listcell c2 = new Listcell(template.getName());
				/*c2.setStyle("color:blue");
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
				Listcell c3 = new Listcell(info.getKiShow());
				Listcell c4 = new Listcell(info.getKiAuthname());
				Listcell c5 = new Listcell(distribute.getDtime());*/
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Listcell c3 = new Listcell(sdf.format(new Date(template.getTime())));
				
				//操作列
				Listcell c4 = new Listcell();
                Hbox hb=new Hbox(); 
        		c4.appendChild(hb);  
        		c4.setTooltiptext("点击浏览模板");
                Image iEdit = new Image();
                Image iAuth = new Image();
                iEdit.setType("edit");
                iAuth.setType("authorize");
                
                iAuth.addEventListener("onClick", new EventListener(){
                    public void onEvent(Event event) throws Exception{
                    	updateTemplate(event);
                    }
                });
                 //iAuth.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.zul.authorize"));
                
                iAuth.setParent(hb);
                
                Listcell c5 = new Listcell(template.getId().toString());
				
				arg0.appendChild(c0);
				arg0.appendChild(c1);
				arg0.appendChild(c2);
				arg0.appendChild(c3);
				arg0.appendChild(c4);
				arg0.appendChild(c5);
			}
		});
	}
	
	 public void updateTemplate(Event event){

	        Map map = new HashMap();
	        map.put("template_update", (TemplateFile)((Listitem) (event.getTarget().getParent().getParent().getParent())).getValue());
	        templatelistbox.setSelectedItem(((Listitem) (event.getTarget().getParent().getParent().getParent())));
	        ((Listitem) (event.getTarget().getParent().getParent().getParent())).invalidate();
	        Window win_update = (Window) Executions.createComponents("/apps/infoExtra/content/infogenerated/template/updatetemplate.zul", this, map);
	        try
	        {       	
	        	win_update.setClosable(true);
	        	win_update.doModal();
	        } catch (Exception e)
	        {
	           /* log.error(org.zkoss.util.resource.Labels
	                    .getLabel("system.commom.ui.time")
	                    + new Date().toLocaleString()
	                    + " "
	                    + org.zkoss.util.resource.Labels
	                            .getLabel("users.ui.usermanageropen")
	                    + e.getMessage());*/
	            e.printStackTrace();
	        }
	 }
	
	public void onClick$addTemplate(){
		AddTemplate addTemplate=(AddTemplate)Executions.createComponents("/apps/infoExtra/content/infogenerated/template/addtemplate.zul", null, null);
		addTemplate.initWindow();
		addTemplate.setTemplatelistbox(this.templatelistbox);
		addTemplate.setInfoListModel(this.infoListModel);
		addTemplate.doHighlighted();
		
	}
	
	public void onClick$deleteInfo() throws InterruptedException{
		deleteinfo(templatelistbox,infoListModel);
	}
	
	//重载搜索到的信息列表
	public void searchinfo(List<?> slist,Listbox infolistbox,ListModelList infolistmodel)
	{
		infolistmodel.clear();
		infolistmodel.addAll(slist);
		infolistbox.setModel(infolistmodel);	
	}
	
	// 批量删除信息	 
	public void deleteinfo(Listbox listbox,ListModelList listModel) throws InterruptedException
	{
		if(listbox.getSelectedItem()==null)
		{
		Messagebox.show("请选择要删除的信息！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		else{	
			if(Messagebox.show("确定要删除吗？", "确认", 
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK)
			{
				Set<Listitem> isets = listbox.getSelectedItems();
				final Iterator<Listitem> ite = isets.iterator();
				final List<TemplateFile> hlist = new ArrayList<TemplateFile>();
				

				while (ite.hasNext()) {
					Listitem item = (Listitem) ite.next();
					hlist.add((TemplateFile) item.getValue());
				}
				for (TemplateFile mi : hlist) {			
					infoGeneratedService.delete(mi);
				}
			
		     Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
		   //刷新列表
				List<TemplateFile> templateList = infoGeneratedService.findTemplateAllFile();
				searchinfo(templateList,templatelistbox,infoListModel);
			}
		}
	}

}
