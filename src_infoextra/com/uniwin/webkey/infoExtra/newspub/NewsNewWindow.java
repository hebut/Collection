package com.uniwin.webkey.infoExtra.newspub;
/**
 * 控制增加新信息界面
 * 2010-3-19
 * @author whm
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.io.Files;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Form;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.core.model.WkTInfocntId;
import com.uniwin.webkey.infoExtra.core.DataBaseSet;
import com.uniwin.webkey.infoExtra.core.Html2MHTCompiler;
import com.uniwin.webkey.infoExtra.core.JQueryCode;
import com.uniwin.webkey.infoExtra.infosort.DomainTreeComposer;
import com.uniwin.webkey.infoExtra.infosort.Sort2;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.listbox.SourceListbox;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoIdAnddomainId;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.cms.model.WkTFileId;
import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.infoExtra.util.ConvertUtil;


public class NewsNewWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	WkTInfo info,in;
	WkTExtractask task;
	WkTInfocnt infocnt;
	WkTInfocntId infocntid;
	WkTFile file;
	WkTFileId fileid;
	Users user;
	WkTDistribute dis,d;
	//信息数据访问接口
	NewsServices	 newsService = (NewsServices) SpringUtil.getBean("info_newsService");
	TaskService	 taskService = (TaskService) SpringUtil.getBean("taskService");
	//暂存栏目列表
	List slist,clist; 
	//信息界面所用的的各种组件
	Textbox kititle,kititle2,kiordno,kikeys,kisource,taskname,kiSort;
	Datebox kivaliddate;
	Toolbarbutton choice,choice1,save,saudit,reset,back,choose;
	Listbox fjlist,kitype;
    InfoIdAndDomainId infoIdAndDomainIdService=(InfoIdAndDomainId)SpringUtil.getBean("infoIdAndDomainIdService");
	Hbox fjnews,wdnews,tupian;
	Form kiimage;
	Label lfile;
	FCKeditor kicontent;
	SourceListbox kisource1;
	Listbox upList;
	List nameList=new ArrayList();
	List plist;
	ListModelList modelList;
	Long userid;
	String username;
	List selectList;
	public void afterCompose() {
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
//		kisource1.initSourceSelect();
	
		modelList=new ListModelList(nameList);	
	    user = (Users) Sessions.getCurrent().getAttribute("users");
		upList.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem arg0, Object arg1) throws Exception {
				Media name=(Media)arg1;
				arg0.setValue(arg1);
				arg0.setLabel(name.getName());
				String namefile=name.getName().toString();
				String savename=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info")+"//"+namefile;	//�ļ������� 
			}
		});
		upList.setModel(modelList);	
		//选择发布频道
		choose.addEventListener(Events.ON_CLICK,  new EventListener(){
			public void onEvent(Event event) throws Exception {
				Executions.getCurrent().setAttribute("mul","0");
				NewsTaskSelectWindow w=(NewsTaskSelectWindow)Executions.createComponents("/apps/infoExtra/content/newspub/newtasksel.zul", null,null);
				w.doHighlighted();
				w.initWindow();
				w.addEventListener(Events.ON_CHANGE, new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						NewsTaskSelectWindow w=(NewsTaskSelectWindow) arg0.getTarget();
						plist=w.getSlist();
						WkTExtractask et=(WkTExtractask) plist.get(0);
						taskname.setValue(et.getKeName());
						Sessions.getCurrent().setAttribute("sel","1");
					}
				});
			}	
		});
	}
private void saveInfoSort(WkTInfo info){
		
		if(selectList!=null && selectList.size()>0){
	    	 WkTInfoIdAnddomainId idAnddomainId;
	    	 WkTInfoDomain domain;
	    	 for(int s=0;s<selectList.size();s++){
	    		 domain=(WkTInfoDomain)selectList.get(s);
	    		 idAnddomainId=new WkTInfoIdAnddomainId();
	    		 idAnddomainId.setDomainId(domain.getKiId());
	    		 idAnddomainId.setInfoId(info.getKiId());
	    		 infoIdAndDomainIdService.save(idAnddomainId);
	    	 }
	    	 
	     }
		
	}
	
	public void initWindow(WkTExtractask task)
	{
		this.task= task;	
		taskname.setValue(task.getKeName());
		Sessions.getCurrent().setAttribute("sel","0");
	}
	public void onSelect$kisource1()
	{
		kisource.setValue(kisource1.getSelectedItem().getLabel());
	}
	//信息分类选择
		public void onClick$chooseSort(){
//			分类树修改前
//			Sort2 s=(Sort2)Executions.createComponents("/apps/infoExtra/content/infosort/sort.zul", null, null);
//			s.doHighlighted();
//			s.initWin(kiSort);
//			s.addEventListener(Events.ON_CHANGE, new EventListener(){
//				public void onEvent(Event arg0) throws Exception {
//					List s=(List) arg0.getData();
//					selectList=s;
//				}
//			});
            //分类树修改后			
			DomainTreeComposer s=(DomainTreeComposer)Executions.createComponents("/apps/infoExtra/content/infosort/sortTree.zul", null, null);
			s.doHighlighted();
			s.initWin(kiSort);
			s.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					List s=(List) arg0.getData();
					selectList=s;
				}
			});
		}
	
	//保存新信息
	public void onClick$save() throws InterruptedException, IOException
	{
		  if(kititle.getValue().equals("")){				
			  try {
					Messagebox.show("标题不能为空！", "Information", Messagebox.OK, Messagebox.INFORMATION);
					kititle.focus();
			  } catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
		  if(kiSort.getValue()==null || kiSort.getValue().equals("")){
			  Messagebox.show("分类不能为空！", "Information", Messagebox.OK, Messagebox.INFORMATION);
			  return;
		  }
		  
			//将新信息保存至WkTInfo表中
			WkTInfo info=new WkTInfo();
			 Date da=new Date();
			 String username = user.getName();
			 if(kivaliddate.getText().equals(""))
			 {
				 info.setKiValiddate("1900-1-1");
			 }
			 else
			 {
			 info.setKiValiddate(kivaliddate.getText());
			 }
			 info.setKiTitle(kititle.getText());
			 info.setKiTitle2(kititle2.getText());
			 info.setKiTop("0");
			 info.setKiHits(0);
			 info.setKiOrdno(10);
			 info.setKiKeys(kikeys.getText());
			 info.setKuId(Long.parseLong(user.getUserId() + ""));
	         info.setKuName(username);
		     info.setKiSource(kisource.getText());
		     info.setKiPtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
	    	 info.setKiType("1");
	    	 info.setKiShow("1");
	    	 info.setKiAuthname(username);
	    	 info.setKiAddress(null);
	    	 String sel=(String)Sessions.getCurrent().getAttribute("sel");
	    	 if(sel.equals("1"))
	    	 {
	    		 WkTExtractask et=(WkTExtractask) plist.get(0);
	    		 info.setKeId(et.getKeId());
	    	 }
	    	 else
	    	 {
	    		 info.setKeId(task.getKeId());
	    	 }
		     newsService.save(info);
					 
			//保存附件
					if(modelList.getInnerList().size()!=0&&modelList.getInnerList()!=null)
					{List flist=modelList.getInnerList();
					  for(int i=0;i<flist.size();i++){
						  Media media=(Media)flist.get(i);
						   saveToFile(media,info.getKiId(),info.getKuId());	 
							}
					  }
					
		   //保存信息内容至WkTInfocnt
					if(info.getKiType().toString().trim().equals("1"))
					{
				     Long len=DataBaseSet.data_Len;
				     Long max=kicontent.getValue().length()/len+1;
				     for(Long i=0L;i<max;i++){
				    	 WkTInfocnt infocnt=new WkTInfocnt();
					     WkTInfocntId infocntid=new WkTInfocntId(info.getKiId(),i+1);
					     infocnt.setId(infocntid);
					     Long be=i*len;
					     if(i==(max-1)){
					    	 infocnt.setKiContent(kicontent.getValue().substring(be.intValue())); 
					     }else{
					       Long en=(i+1)*len;
					       infocnt.setKiContent(kicontent.getValue().substring(be.intValue(),en.intValue()));
					     }
					     newsService.save(infocnt);
				     }
				     
					}
					    
					
		 //将新信息的发布情况保存到WkTDistribute表中
			WkTDistribute dis=new WkTDistribute();
			dis.setKbStatus("1");	
		    dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
		    dis.setKbTitle(kititle.getText());
		    dis.setKiId(info.getKiId());   
			dis.setKeId((info.getKeId()));
			dis.setKbFlag("0");
			newsService.save(dis);
			 saveInfoSort(info);//保存信息具体分类信息
			 
		     try {
					Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
				Events.postEvent(Events.ON_CHANGE, this, null);
				this.detach();
		}
	
	//保存上传的附件至附件列表
	public void saveToFile(Media media,Long infoid,Long kuid) throws IOException{
		if (media != null) { 
			//isBinary二进制文件情况，如下处理
			if(media.isBinary()){			
				InputStream objin=media.getStreamData(); 
				String fileName=DateUtil.getDateTimeString(new Date())+"_"+infoid.toString()+"_"+media.getName().toString(); //����������
				String pa=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
			    if(pa == null){
					    System.out.println("无法访问存储目录！");
					    return;
			    }		    
			    File fUploadDir = new File(pa);	
			    if(!fUploadDir.exists()){
					    if(!fUploadDir.mkdir()){
					    	   System.out.println("无法创建存储目录！");                             
						       return;
					    }			
			     }
				String path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
				 if(path1 == null){
				    	System.out.println("无法访问存储目录！");
					    return;
				 }	
				 File fUploadDir3 = new File(path1);	
				 if(!fUploadDir3.exists()){
						    if(!fUploadDir3.mkdir()){
						    	   System.out.println("无法创建存储目录！");                        		
							       return;
						    }			
				  }
				 File fUploadDir2 = new File(path1+"\\"+infoid);	                                   	
				 if(!fUploadDir2.exists()){
					    if(!fUploadDir2.mkdir()){
					    	 System.out.println("无法创建存储目录！");
						     return;
					     }			
				 }	
				String path=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info")+"//"+fileName.trim();
			    FileOutputStream out=null;
				out = new FileOutputStream(path);
			    DataOutputStream objout=new DataOutputStream(out); 		   
				Files.copy(objout,objin);
				
				if(out!=null){
					out.close();
				}	
				 WkTFile file=new WkTFile();
			       WkTFileId fileid=new WkTFileId(infoid,fileName,media.getName().toString().trim(),kuid,"1","0");
			        file.setId(fileid);
			        newsService.save(file);
				
		//否则做如下处理
		 }else{
			 
			 if(media.getName().endsWith(".txt")||media.getName().endsWith(".project")){
		            Reader r = media.getReaderData(); 
		            String fileName=DateUtil.getDateTimeString(new Date())+"_"+infoid.toString()+"_"+media.getName().toString(); //�������
				    String pa=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info");
				    if(pa == null){
						    System.out.println("无法访问存储目录！");
						    return;
				    }		    
				    File fUploadDir = new File(pa);	
				    if(!fUploadDir.exists()){
						    if(!fUploadDir.mkdir()){
						    	   System.out.println("无法创建存储目录！");                             
							       return;
						    }			
				     }
				    String path2=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/upload/info")+"//"+fileName.trim();
				    File f = new File(path2);
				    Files.copy(f,r,null);
				    Files.close(r);	
				    WkTFile file=new WkTFile();
				       WkTFileId fileid=new WkTFileId(infoid,fileName,media.getName().toString().trim(),kuid,"1","0");
				        file.setId(fileid);
				        newsService.save(file);
			 	}
			 
		 	}
		  }
		
	}
	//信息保存并送审
	public void onClick$saudit() throws InterruptedException, IOException
	{
		  if(kititle.getValue().equals("")){				
			  try {
					Messagebox.show("标题不能为空！", "Information", Messagebox.OK, Messagebox.INFORMATION);
					kititle.focus();
			  } catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if(!infoIdAndDomainIdService.findByTitle(kititle.getValue()).isEmpty()){
				Messagebox.show("信息已发布，请删除重复信息！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			//将新信息保存至WkTInfo表中
			WkTInfo info=new WkTInfo();
			 Date da=new Date();
			 if(kivaliddate.getText().equals(""))
			 {
				 info.setKiValiddate("1900-1-1");
			 }
			 else
			 {
			 info.setKiValiddate(kivaliddate.getText());
			 }
			 info.setKiTitle(kititle.getText());
			 info.setKiTitle2(kititle2.getText());
			 info.setKiTop("0");
			 info.setKiHits(0);
			 info.setKiOrdno(10);
			 info.setKiKeys(kikeys.getText());
			 info.setKuId(Long.parseLong(user.getUserId() + ""));
	         info.setKuName(user.getLoginName());
		     info.setKiSource(kisource.getText());
		     info.setKiPtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
	    	 info.setKiType("1");
	    	 info.setKiShow("1");
	    	 info.setKiAuthname(user.getName());
	    	 info.setKiAddress(null);
	    	 String sel=(String)Sessions.getCurrent().getAttribute("sel");
	    	 if(sel.equals("1"))
	    	 {
	    		 WkTExtractask et=(WkTExtractask) plist.get(0);
	    		 info.setKeId(et.getKeId());
	    	 }
	    	 else
	    	 {
	    		 info.setKeId(task.getKeId());
	    	 }
		     newsService.save(info);
			//保存附件
					if(modelList.getInnerList().size()!=0&&modelList.getInnerList()!=null)
					{List flist=modelList.getInnerList();
					  for(int i=0;i<flist.size();i++){
						  Media media=(Media)flist.get(i);
						   saveToFile(media,info.getKiId(),info.getKuId());	 
							}
					  }
					
		   //保存信息内容至WkTInfocnt
					if(info.getKiType().toString().trim().equals("1"))
					{
				     Long len=DataBaseSet.data_Len;
				     Long max=kicontent.getValue().length()/len+1;
				     for(Long i=0L;i<max;i++){
				    	 WkTInfocnt infocnt=new WkTInfocnt();
					     WkTInfocntId infocntid=new WkTInfocntId(info.getKiId(),i+1);
					     infocnt.setId(infocntid);
					     Long be=i*len;
					     if(i==(max-1)){
					    	 infocnt.setKiContent(kicontent.getValue().substring(be.intValue())); 
					     }else{
					       Long en=(i+1)*len;
					       infocnt.setKiContent(kicontent.getValue().substring(be.intValue(),en.intValue()));
					     }
					     newsService.save(infocnt);
				     }
				     
					}
					    
					
		 //将新信息的发布情况保存到WkTDistribute表中
			  WkTDistribute dis=new WkTDistribute();
			  WkTExtractask ee=(WkTExtractask) taskService.getTaskBykeId(info.getKeId()).get(0);
			  WkTChanel type=taskService.getTpyeById(ee.getKcId());
			  if(type.getKfId().toString().trim().equals("0"))
		  	dis.setKbStatus("0");	
			  else  dis.setKbStatus("2");
		    dis.setKbDtime(ConvertUtil.convertDateAndTimeString(da.getTime()));
		    dis.setKbTitle(kititle.getText());
		    dis.setKiId(info.getKiId());   
			dis.setKeId((info.getKeId()));
			dis.setKbFlag("0");
			newsService.save(dis);
			   saveInfoSort(info);//保存信息具体分类信息
		     try {
					Messagebox.show("操作成功！", "Information", Messagebox.OK, Messagebox.INFORMATION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
				Events.postEvent(Events.ON_CHANGE, this, null);
			this.detach();
					}
	//重置
	public void onClick$reset()
	{
		initWindow(task);kititle.setValue("");kititle2.setValue("");
		kivaliddate.setText("");kikeys.setValue("");
		kisource.setValue("");kisource1.setCheckmark(false);modelList.clear();
		kicontent.setValue("");
	}
	
	public ListModelList getModelList()
	{
		return modelList;
	}
}
