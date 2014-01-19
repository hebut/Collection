package com.uniwin.webkey.core.ui;
/**
 * <li>用户注册信息编辑
 * @author bobo
 * @date 2010-3-12
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Treeitem;

import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.baseUtil.ConvertUtil;
import com.uniwin.webkey.cms.baseUtil.DateUtil;
import com.uniwin.webkey.cms.baseUtil.IPUtil;
import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class PersonalEditWin extends Window implements AfterCompose {

	private static final long serialVersionUID = -8080661133741540227L;
	//用户数据访问接口
	private IUsersManager usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
	 private IOrganizationManager organizationManager= (IOrganizationManager) SpringUtil
             .getBean("organizationManager");                                     // 资源管理服务接口

	//用户输入框组件
    private Textbox kuName,kuAnswer,kuQuestion,kuEmail,kuPhone,kuCompany,uBandIp,kulevel,kuduty,kumajor;
    
    private Label kuLid;
    private OrganizationTree    parentData;

	//用户选择框组件
    private Checkbox kuAutoenter;
    
    //用户日期输入框组件
	private Datebox kuBirthday;
	  
    //用户选择框组件
    private Radiogroup kuSex;
    
    private Organization        selectedOrganization;

    private Bandbox             organizationId;
    
    //用户下拉列表框组件
	private Listbox kuStyleListbox,uStatus,bangType;
	  
    //用户实体
	Users user;	
	
	//重置按钮
	private Button reset;
		
	
	public void afterCompose(){		
		
		Components.wireVariables(this, this);
		Components.addForwards(this, this);		
		user=(Users)Sessions.getCurrent().getAttribute("users");
		parentData.setLabelProvider(new OrganizationLabelProvider());
	    parentData.setContentProvider(new OrganizationThirdContentProvider());
	    parentData.rebuildTree();
	    try {
			List olist=organizationManager.getOrgById(user.getOrganizationId());
			 Organization org=(Organization) olist.get(0);
			 organizationId.setValue(org.getName());
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	    fullBandBox();
	   
	   
	   
		
		bangType.addEventListener(Events.ON_SELECT, new EventListener(){
			public void onEvent(Event arg0) throws Exception {
				bangTypeHandle();
			}			
		});
		
		initUser();	                                                       //调用初始化窗口函数
	}
    /**
     * 选中组织
     */
    public void fullBandBox()
    {
        Iterator children = parentData.getItems().iterator();
        Treeitem treeItem = null;
        Organization re = null;
        Object obj = null;
        while (children.hasNext())
        {
            obj = children.next();
            treeItem = (Treeitem) obj;
            treeItem.getTreerow().addEventListener("onClick",
                    new EventListener()
                    {
                        public void onEvent(Event arg0) throws Exception
                        {
                            setOrganizationId();
                        }
                    });
            re = (Organization) treeItem.getValue();
            if (re.getOrganizationId() == 0)
            {
                treeItem.setSelected(true);
            }
        }
    }

    /**
     * 将选择的组织添加到文本框中
     */
    public void setOrganizationId()
    {
        selectedOrganization = (Organization) parentData.getSelectedItem()
                .getValue();
        organizationId.setValue(selectedOrganization.getName());
        organizationId.close();

    }
	
	
	/**
	 * <li>功能描述：初始化 register页面数据。
	 * void 
	 * @author bobo
	 * @2010-3-13
	 */
	public void initUser(){		
	    kuLid.setValue(user.getLoginName());
	    kuName.setValue(user.getName());
	    kuAnswer.setValue(user.getKuPassAnswer());
	    kuQuestion.setValue(user.getKuPassProblem());
	    kuEmail.setValue(user.getKuEmail());
	    kuPhone.setValue(user.getKuPhone());
	    kumajor.setValue(user.getKuMajor());
	    kuduty.setValue(user.getKuDuty());
	    kulevel.setValue(user.getKuJobLevel());
	    if(user.getKuBirthday()!=null&&user.getKuBirthday().length()>0){
		    kuBirthday.setValue(ConvertUtil.convertDate(DateUtil.getDateString(user.getKuBirthday())));
		}
		
		if(user.getKuSex().trim().equalsIgnoreCase("2")){                                 //性别是字符，要去空格，否则该语句失灵
			kuSex.setSelectedIndex(1);
		}else{
			kuSex.setSelectedIndex(0);
		}
		
		kuCompany.setValue(user.getKuCompany());
		
		uBandIp.setValue(user.getKuBindAddr());
		bangType.setSelectedIndex(Integer.valueOf(user.getKuBindType().trim()));
		bangTypeHandle();                                                                 //调用函数
		
		
		if(user.getKuAutoEnter().trim().equalsIgnoreCase("1")){
			kuAutoenter.setChecked(true);
		}else{
			kuAutoenter.setChecked(false);
		}
		
		//显示列表框中数据，读取/webkey/syles下面的风格文件		
		final String path0=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/styles");
		kuStyleListbox.setItemRenderer(new ListitemRenderer(){
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
		kuStyleListbox.setModel(fListModel);
		
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
				if(user.getKustyle().trim().equals(tempList.get(k))){
					kuStyleListbox.setSelectedIndex(k);
					break;
				}
			}
		}else{
			kuStyleListbox.setSelectedIndex(0);
		}
	}
	
	
	/**
	 * <li>功能描述：用户信息更新功能。
	 * void 
	 * @author bobo
	 * @throws InterruptedException 
	 * @2010-3-1
	 */	
	public void onClick$save() throws InterruptedException{	
		
		 if(kuName.getValue().equals(""))
		 {	
			   Messagebox.show(Labels.getLabel("users.ui.namevalidate')"),
					   Labels.getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
			   kuName.focus();
			   return;
		  }
		 if(kuEmail.getValue().equals(""))
		 {
			   Messagebox.show(Labels.getLabel("users.ui.kuEmailvalidate"), Labels.
					   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
			   kuEmail.focus();
			   return;
		 }
	    if(kuPhone.getValue().equals(""))
	    {	
			   Messagebox.show(Labels.getLabel("users.ui.kuPhonevalidate"), Labels.
					   getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
			   kuPhone.focus();			  
			   return;
		 }
		
		user.setName(kuName.getText());
		user.setKuPassAnswer(kuAnswer.getText());
		user.setKuPassProblem(kuQuestion.getText());
		user.setKuEmail(kuEmail.getText());
		user.setKuPhone(kuPhone.getText());
		user.setKuMajor(kumajor.getValue());
		user.setKuDuty(kuduty.getValue());
		user.setKuJobLevel(kulevel.getValue());
		user.setOrganizationId(selectedOrganization.getOrganizationId());
		if(kuBirthday.getValue()!=null){
			   user.setKuBirthday(DateUtil.getDateString(kuBirthday.getValue()));
		}		
		user.setKuCompany(kuCompany.getText());		
		if(kuSex.getSelectedIndex()==0){
			user.setKuSex("1");                                                   //1代表“男”，2代表“女”
		}else{
			user.setKuSex("2");
		}		
		
				
		//如果用户选择不绑定，则设置其不能自动登陆，同时将绑定IP地址设置空.
		//如果用户选择IP绑定，首先设置绑定的IP地址，如果输入则设置为输入IP，否则设置该用户上传登陆IP地址。 
		//选择IP绑定并且设置绑定IP后，判断用户是否设置自动登陆
		
		if(bangType.getSelectedIndex()==0){//选择不绑定
		    user.setKuBindType("0");
		    user.setKuAutoEnter("0");
		    user.setKuBindAddr("");
		}else{                             //选择绑定
			user.setKuBindType("1");
			
			try{
				IPUtil.getIPLong(uBandIp.getValue()); 
				user.setKuBindAddr(uBandIp.getValue());
			}catch(Exception e){
				throw new WrongValueException(uBandIp, Labels.getLabel("sers.ui.uBandIpValidate")); 
			}
			
			if(kuAutoenter.isChecked()){	//自动登录	
				user.setKuAutoEnter("1");				
			}else{                          //不自动登录
				user.setKuAutoEnter("0");				
			}			
		}
		
		String str="";
		if(kuStyleListbox.getSelectedItem()==null){
			str = "default";
			user.setKustyle(str);
		}else{
			str= kuStyleListbox.getSelectedItem().getValue().toString();		//获取得到选择的css名称，比如：E:\EclipseWokeSpace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\webkey5\styles\default
			String path1=Executions.getCurrent().getDesktop().getWebApp().getRealPath("/styles");	//获取当前路径	
			str = str.substring(path1.length()+1);  //将当前路径截取掉 ,得到css名称 			
	        user.setKustyle(str);
		}
		setProOfCookie("userStyle", str);
		try
        {
            usersManager.update(user);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        }
		Sessions.getCurrent().setAttribute("users", user);		
		Messagebox.show(Labels.getLabel("users.ui.saveSuccess"), Labels.
				getLabel("users.ui.information"), Messagebox.OK, Messagebox.INFORMATION);
		//this.detach();	
		initUser();
	}
	
	/**
	 * <li>功能描述：用户信息重置功能。
	 * void 
	 * @author bobo
	 * @2010-4-13
	 */	
	public void onClick$reset(){
		initUser();	
	}
	
	private void bangTypeHandle(){
		if(bangType.getSelectedIndex()==0){//不绑定
			uBandIp.setRawValue(null);
			uBandIp.setReadonly(true);
			kuAutoenter.setChecked(false);
			kuAutoenter.setDisabled(true);
		}else{
			uBandIp.setReadonly(false);
			uBandIp.setValue(user.getKuBindAddr());
			kuAutoenter.setDisabled(false);
			if(user.getKuAutoEnter().trim().equalsIgnoreCase("1")){
				kuAutoenter.setChecked(true);
			}else{
				kuAutoenter.setChecked(false);
			}
		}		
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
