package com.uniwin.webkey.content.website.win;
/**
 * <li>功能描述：实现网站信息修改后的保存和重置
 * 对应的页面为admin/system/parameters/websiteInfo/index.zul
 * @author fang
 * @2010.3
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.itf.WebsiteService;
import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.cms.util.FileUtil;

public class CurrentWebsiteWindow extends Window implements AfterCompose
{
	private static final long serialVersionUID = -2518102178114497440L;
	private Textbox enterpriseName;	//单位名称
    private Textbox legalPerson;	//法人代表
    private Textbox telephone;	//电话
    private Textbox fax;	//传真
    private Textbox email;	//E-MAIL地址ַ
    private Textbox address;	//地址
    private Textbox postCoder;	//邮编
    private Listbox style;	//默认界面风格
    private Textbox location;//页面保存路径名称
    Button sava,reset;	//保存和重置
    WkTWebsite website;	//网站实体
    WebsiteService websiteService = (WebsiteService) SpringUtil.getBean("websiteService");	//网站信息数据访问接口
    public void afterCompose() 
	{	
    	Components.wireVariables(this, this);
		Components.addForwards(this, this);
		this.initializeWindow();
	}
    /**
     * <li>功能描述：初始化网站信息界面，从数据库中读出存放信息
     */
    public void initializeWindow()
    {
		website=(WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");	//从Session中读出
		enterpriseName.setValue(website.getkwEpname());
		legalPerson.setValue(website.getkwGenmgr());
		telephone.setValue(website.getkwPhone());
		fax.setValue(website.getkwFax());
		email.setValue(website.getkwEmail());
		address.setValue(website.getkwAddress());
		postCoder.setValue(website.getkwPostid());

		
		//显示列表框中数据，读取/webkey/syles下面的风格文件	
		final String path0=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/styles");
		style.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem item, Object data) throws Exception {
				File file = (File)data;
				item.setValue(file);
				item.setHeight("25px");			
			    
				String path = path0+"\\"+file.getName().trim()+"\\"+"index.css";  //获取.css文件路径，得到其中的css文件名称
				String resu = FileUtil.getStyleName(path);
				Listcell c1 = new Listcell(resu);
				item.appendChild(c1);
			}
		});
		
		File cssFolder = new File(path0);
		final File[] folderList = cssFolder.listFiles();
		List fList = new ArrayList();
		for(int i=0;i<folderList.length;i++){
			if(folderList[i].isDirectory()){
				fList.add(folderList[i]);
			}				
		}	
		ListModelList fListModel = new ListModelList(fList);
		style.setModel(fListModel);
		
		//初始化用户当前所使用的css
		File cssFolder2 = new File(path0);
		final File[] folderList2 = cssFolder2.listFiles();
		List tempList = new ArrayList();
		if(folderList2.length>0){
			for(int j=0;j<folderList2.length;j++){
				if(folderList2[j].isDirectory()){
					tempList.add(folderList2[j].getName().trim());
				}
			}
			for(int k=0;k<tempList.size();k++){
				if(website.getkwStyle().trim().equals(tempList.get(k))){
					style.setSelectedIndex(k);
					break;
				}
			}
		}else{
			style.setSelectedIndex(0);
		}
		location.setValue(website.getKwLocation());
	}
   
    /**
     * <li>功能描述：实现网站信息修改后的保存功能
     * 将页面内获取的数据存放到数据库对应的位置
     * @throws InterruptedException 
     */
	public void onClick$save() throws InterruptedException
	{	
		website.setkwEpname(enterpriseName.getValue());
		website.setkwGenmgr(legalPerson.getValue());
		website.setkwPhone(telephone.getValue());
		website.setkwFax(fax.getValue());
		website.setkwEmail(email.getValue());
		if(postCoder.getValue().length()>6){
			Messagebox.show(Labels.getLabel("website.ui.postCoderValidate"),Labels.
					getLabel("website.ui.wrongInformation"), Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		website.setkwPostid(postCoder.getValue());
		website.setkwAddress(address.getValue());	
		
		String str="";
		if(style.getSelectedItem()==null){
			str = "default";
			website.setkwStyle(str);
		}else{
			str= style.getSelectedItem().getValue().toString();		//获取得到选择的css名称，比如：E:\EclipseWokeSpace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\webkey5\styles\default
			String path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/styles");	//获取当前路径	
			str = str.substring(path1.length()+1);  //将当前路径截取掉 ,得到css名称 		
			website.setkwStyle(str);
		}
		setProOfCookie("userStyle", str);
		

		//保存网站信息中页面保存的位置,先删除旧地址，然后判断location是否为空，空则默认为htmlFile.而后创建存放页面的文件夹
		String curPath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/");
		if(website.getKwLocation()!=""&&website.getKwLocation()!=null){			
			String oldPagePath = curPath +website.getKwLocation().trim();
			FileUtil.delFolder(oldPagePath);
		}
		String loc = "";
		if(location.getValue().equals("")||location.getValue()==null){
			loc = "html";
		}else{
			loc = location.getValue();
		}		
//		String newPagePath = curPath+website.getKwId()+"\\"+loc.trim();//项目名称/用户名/网站信息中路径
//		FileUtil.mkHtmlFolder(newPagePath);	
		
		String newPagePath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/");//当前项目根目录
		File siteDir = new File(newPagePath+"/"+"HtmlSite");
		if(!(siteDir.exists())){
			siteDir.mkdir();
		}
		
		File siteDir2 = new File(newPagePath+"/"+"HtmlSite"+"/"+website.getKwId().toString().trim());
		if(!(siteDir2.exists())){
			siteDir2.mkdir();
		}
		
		File siteDir3 = new File(newPagePath+"/"+"HtmlSite"+"/"+website.getKwId().toString().trim()+"/"+loc.trim());
		if(!(siteDir3.exists())){
			siteDir3.mkdir();
		}
		
		website.setKwLocation(loc.trim());

		websiteService.update(website);			
		Messagebox.show(Labels.getLabel("website.ui.saveSuccess"), Labels.getLabel("website.ui.information"),
				Messagebox.OK, Messagebox.INFORMATION);//如果操作成功，提示“保存成功”窗口 
		this.detach();
	}
	/**
	 *<li>功能描述：实现修改网站信息的重置功能
	 *点击重置后页面将显示原来存放在数据库中的内容
	 */
	public void onClick$reset()
	{
		enterpriseName.setValue(website.getkwEpname());
		legalPerson.setValue(website.getkwGenmgr());
		telephone.setValue(website.getkwPhone());
		fax.setValue(website.getkwFax());
		email.setValue(website.getkwEmail());
		address.setValue(website.getkwAddress());
		postCoder.setValue(website.getkwPostid());
	}
	/**
	 * <li>功能描述：向cookie中写入值。
	 * @param pname
	 * @param fs
	 * void 
	 * @author DaLei
	 * @2010-3-24
	 */
	public void setProOfCookie(String pname,String fs){
		Cookie cookie = new Cookie(pname,fs);
		cookie.setMaxAge(60*60*24*30);//store 30 days
		String cp = Executions.getCurrent().getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse)Executions.getCurrent().getNativeResponse()).addCookie(cookie);
	}
}
