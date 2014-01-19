package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Group;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.DateUtil;
import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.common.util.IdcardValidator;
import com.uniwin.webkey.common.util.StringUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IUserroleManager;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Userrole;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.util.FileTool;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class UsersAddWin extends Window implements AfterCompose
{
    private IUsersManager       usersManager;

    private IUserroleManager    userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");                  ;

    private Logger              log             = Logger
                                                        .getLogger(UsersAddWin.class);

    private Textbox             name, loginName, password, configPassword,kuQuestion,kuAnswer;            // 用户名；登录别名；密码；确认密码,密码提示问题,密码提示答案

//    private Image               addUser;
    private Button               addUser;

    private OrganizationTree    parentData;

    private Organization        selectedOrganization;

    private Bandbox             organizationId;

    private Radio               isNotLock, nan, nv;

    private Radio               isLock,outdate;

    private List                userRoles;

    private List                allRoles;

    private Listbox             unauthorizedRoles, userRoleBox;

    private Image               toRight, toLeft;

    private Window              usersAddWin_add;

    private org.zkoss.zul.Image activeImage;

    private Datebox             kuBirthday,opentime,closetime;

    private Textbox             kuEmail;

    private Textbox             kuPhone;

    private Combobox            kuCertificatetype;

    private Textbox             kuCardNumber;

    private Textbox             kuPicPath;

    private Textbox             kuCompany;

    // private Combobox kuOnline;
    private Intbox              kuLimit;

    private Combobox            kuAutoShow;

    private Combobox            kuBindType;

    private Textbox             kuBindAddr;

    // private Combobox kuAutoEnter;
    private Combobox            kuKeyLogin;

    private Textbox             kuCertId;

    private Textbox             kuCertInfo,keyWord;
    
    private Textbox             kumajor;
    
    private Textbox             kuduty;
    
    private Textbox             kulevel;

    private Combobox            kuForm;
    
    private Rows rolerows;

    public UsersAddWin()
    {
        usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
    }

    public void afterCompose()
    {
    	Components.wireVariables(this, this);
        Components.addForwards(this, this);
        opentime.setValue(new Date());
        parentData.setLabelProvider(new OrganizationLabelProvider());
        parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        parentData.rebuildTree();
        fullBandBox();
        fullRoles();
        initComboxSelected();
    }

    public void initComboxSelected()
    {
        kuCertificatetype.setSelectedIndex(0);
        // kuOnline.setSelectedIndex(0);
        kuAutoShow.setSelectedIndex(0);
        kuBindType.setSelectedIndex(0);
        // kuAutoEnter.setSelectedIndex(0);
        kuKeyLogin.setSelectedIndex(0);
        kuForm.setSelectedIndex(0);
    }

    /**
     * 获取用户角色的信息，加载角色的信息
     */
    public void fullRoles()
    {
        try
        {
            allRoles = usersManager.getChildUsersRole(0);
            for (Role r : (List<Role>) allRoles)
            {
                Listitem item = new Listitem();
                item.setValue(r);         
                item.setHeight("25px");
                List clist=usersManager.getChildUsersRole(r.getRoleId());
                Listcell cellName = new Listcell();
                final Listcell cellDomain = new Listcell();
                Listcell cellDesc = new Listcell();         
                cellName.setLabel(r.getRoleName());
                cellDesc.setLabel(r.getDescription());             
                item.appendChild(cellName);
                item.appendChild(cellDesc);                   
                item.appendChild(cellDomain);
                unauthorizedRoles.appendChild(item);
           
                final Image down = new Image();
                down.setType("down");
                final Image up   = new Image();
                up.setType("up");
                
                down.addEventListener("onClick", new EventListener(){
                	public void onEvent(Event event) throws Exception{                		
                		((Listitem)down.getParent().getParent()).setParent(userRoleBox);
                        cellDomain.removeChild(down);
                    	cellDomain.appendChild(up);
                	}
                });                 
                up.addEventListener("onClick", new EventListener(){
                	public void onEvent(Event event) throws Exception{
                		((Listitem)up.getParent().getParent()).setParent(unauthorizedRoles);                       
                        cellDomain.removeChild(up);
                    	cellDomain.appendChild(down);
                	}
                });   
                
                item.addEventListener("onDoubleClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        if (((Listitem) event.getTarget()).getParent().getId().equals("unauthorizedRoles"))
                        {
                            ((Listitem) event.getTarget()).setParent(userRoleBox);
                            cellDomain.removeChild(down);
                        	cellDomain.appendChild(up);
                        } else
                        {
                            ((Listitem) event.getTarget()).setParent(unauthorizedRoles);
                            cellDomain.removeChild(up);
                        	cellDomain.appendChild(down);
                        }
                    }
                });
                
                for (Object obj : unauthorizedRoles.getItems())
                {
                    if (obj instanceof Listitem)
                    {
                        if (((Listitem) obj).getValue() instanceof Role)
                        {
                            Role roleTemp = (Role) ((Listitem) obj).getValue();
                            if (roleTemp.getIsDefult() == 1)
                            {
                                ((Listitem) obj).setParent(userRoleBox);
                            }

                        }
                    }
                }
                if(item.getParent().getId().equals("unauthorizedRoles")){
                	 if(clist.size()==0)
                     { 
                	cellDomain.appendChild(down);
                     }
                	 else
                	 {
                		 String bl=" ";
                		 Label l=new Label(bl);
                         cellDomain.appendChild(l);   
                	 }
                }else{
                	 if(clist.size()==0)
                     { 
                	cellDomain.appendChild(up);
                     }
                	 else
                	 {
                		 String bl="  ";
                		 Label l=new Label(bl);
                         cellDomain.appendChild(l);   
                	 }
                }
                for(int i=0;i<clist.size();i++)
                {
                	Role role=(Role) clist.get(i);
                	Listitem item1 = new Listitem();
                    item1.setValue(role);  
                    item1.setHeight("25px");
                    Listcell cellName1 = new Listcell();
                    final Listcell cellDomain1 = new Listcell();
                    Listcell cellDesc1 = new Listcell();          
                    String bla="　    　    ";
                    cellName1.setLabel(bla+role.getRoleName());
                    cellDesc1.setLabel(role.getDescription());             
                    item1.appendChild(cellName1);
                    item1.appendChild(cellDesc1);
                    item1.appendChild(cellDomain1);
                    unauthorizedRoles.appendChild(item1);
                    
                    final Image down1 = new Image();
                    down1.setType("down");
                    final Image up1   = new Image();
                    up1.setType("up");
                    
                    down1.addEventListener("onClick", new EventListener(){
                    	public void onEvent(Event event) throws Exception{                		
                    		((Listitem)down1.getParent().getParent()).setParent(userRoleBox);
                            cellDomain1.removeChild(down1);
                        	cellDomain1.appendChild(up1);
                    	}
                    });                 
                    up1.addEventListener("onClick", new EventListener(){
                    	public void onEvent(Event event) throws Exception{
                    		((Listitem)up1.getParent().getParent()).setParent(unauthorizedRoles);                       
                            cellDomain1.removeChild(up1);
                        	cellDomain1.appendChild(down1);
                    	}
                    });  
                    
                    item1.addEventListener("onDoubleClick", new EventListener()
                    {
                        public void onEvent(Event event) throws Exception
                        {
                            if (((Listitem) event.getTarget()).getParent().getId().equals("unauthorizedRoles"))
                            {
                                ((Listitem) event.getTarget()).setParent(userRoleBox);
                                cellDomain1.removeChild(down1);
                            	cellDomain1.appendChild(up1);
                            } else
                            {
                                ((Listitem) event.getTarget()).setParent(unauthorizedRoles);
                                cellDomain1.removeChild(up1);
                            	cellDomain1.appendChild(down1);
                            }
                        }
                    });
                    
                    for (Object obj : unauthorizedRoles.getItems())
                    {
                        if (obj instanceof Listitem)
                        {
                            if (((Listitem) obj).getValue() instanceof Role)
                            {
                                Role roleTemp = (Role) ((Listitem) obj).getValue();
                                if (roleTemp.getIsDefult() == 1)
                                {
                                    ((Listitem) obj).setParent(userRoleBox);
                                }

                            }
                        }
                    }
                    if(item1.getParent().getId().equals("unauthorizedRoles")){
                    	cellDomain1.appendChild(down1);
                    }else{
                    	cellDomain1.appendChild(up1);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
//    public void fullRoles()
//  {
//    try
//    {
//        allRoles = usersManager.getChildUsersRole(0);
//        for (Role r : (List<Role>) allRoles)
//        {
//            Group group=new Group();
//            group.setValue(r);
//            group.setLabel(r.getRoleName()+"---"+r.getDescription());
//            group.setParent(rolerows);
//            List clist=usersManager.getChildUsersRole(r.getRoleId());
//            for(int i=0;i<clist.size();i++)
//            	{
//            	Role role=(Role) clist.get(i);
//            	Row row=new Row();
//            	row.setValue(role);
//            	Label l1=new Label();
//            	l1.setValue(role.getName());
//            	Label l2=new Label();
//            	l2.setValue(role.getDescription());
//            	final Label cellDomain=new Label();
//                l1.setParent(row);l2.setParent(row);
//                cellDomain.setParent(row);
//                row.setParent(rolerows);
//            	  final Image down = new Image();
//                  down.setType("down");
//                  final Image up   = new Image();
//                  up.setType("up");
//                  down.addEventListener("onClick", new EventListener(){
//                  	public void onEvent(Event event) throws Exception{                		
//                  		((Row)down.getParent().getParent()).setParent(userRoleBox);
//                          cellDomain.removeChild(down);
//                      	cellDomain.appendChild(up);
//                  	}
//                  });                 
//                  up.addEventListener("onClick", new EventListener(){
//                  	public void onEvent(Event event) throws Exception{
//                  		((Listitem)up.getParent().getParent()).setParent(unauthorizedRoles);                       
//                          cellDomain.removeChild(up);
//                      	cellDomain.appendChild(down);
//                  	}
//                  });   
//                  
//                  for (Object obj : unauthorizedRoles.getItems())
//                  {
//                      if (obj instanceof Listitem)
//                      {
//                          if (((Listitem) obj).getValue() instanceof Role)
//                          {
//                              Role roleTemp = (Role) ((Listitem) obj).getValue();
//                              if (roleTemp.getIsDefult() == 1)
//                              {
//                                  ((Listitem) obj).setParent(userRoleBox);
//                              }
//
//                          }
//                      }
//                  }
//                  
//                  if(row.getParent().getId().equals("unauthorizedRoles")){
//                  	cellDomain.appendChild(down);
//                  }else{
//                  	cellDomain.appendChild(up);
//                  }
//            	}        
          //  unauthorizedRoles.appendChild(item);
            
          
           
//            item.addEventListener("onDoubleClick", new EventListener()
//            {
//                public void onEvent(Event event) throws Exception
//                {
//                    if (((Listitem) event.getTarget()).getParent().getId().equals("unauthorizedRoles"))
//                    {
//                        ((Listitem) event.getTarget()).setParent(userRoleBox);
//                        cellDomain.removeChild(down);
//                    	cellDomain.appendChild(up);
//                    } else
//                    {
//                        ((Listitem) event.getTarget()).setParent(unauthorizedRoles);
//                        cellDomain.removeChild(up);
//                    	cellDomain.appendChild(down);
//                    }
//                }
//            });
//            
           
           
            
//        }
//    } catch (Exception e)
//    {
//        e.printStackTrace();
//    }
//  }

    /**
     * 添加用户信息
     * 
     * @param event
     */
    public void addUser(Event event)
    {
        try
        {
            if (name.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.namevalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            else
            {
                if(!StringUtil.checkValue(name.getText().trim(), "UserName"))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("users.ui.namevalidatetype"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            if (loginName.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            else
            {
                if(!StringUtil.checkValue(loginName.getText().trim(), "loginName"))
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("users.ui.loginnamevalidatetype"),
                            org.zkoss.util.resource.Labels
                                    .getLabel("system.commom.ui.prompt"),
                            Messagebox.OK, Messagebox.EXCLAMATION);
                    return;
                }
            }
            // if(loginName.getText().trim().matches("[\u4e00-\u9fa5]")){
            // Messagebox.show("��¼������ʹ������",
            // org.zkoss.util.resource.Labels.getLabel("system.commom.ui.prompt"),
            // Messagebox.OK,
            // Messagebox.EXCLAMATION);
            // return;
            // }
            if (password.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.password"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
//            if (loginName.getText().trim().length() < 6)
//            {
//                Messagebox.show(org.zkoss.util.resource.Labels
//                        .getLabel("users.ui.loginnamevalidateLength"),
//                        org.zkoss.util.resource.Labels
//                                .getLabel("system.commom.ui.prompt"),
//                        Messagebox.OK, Messagebox.EXCLAMATION);
//                return;
//            }
            if (!usersManager.isExistLoginName(loginName.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate1"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (organizationId.getText().trim().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.organizationvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (!password.getText().equals(configPassword.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.passwordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if(kuEmail.getValue().equals(""))
            {
            	 Messagebox.show("请输入电子邮箱！",
                         org.zkoss.util.resource.Labels
                                 .getLabel("system.commom.ui.prompt"),
                         Messagebox.OK, Messagebox.EXCLAMATION);
            	 kuEmail.focus();
                 return;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Users user = new Users();
        user.setName(name.getText());
        user.setLoginName(loginName.getText());
        if(!closetime.getText().equals("")&&!closetime.getText().equals(null))
        {
        user.setAccountEndTime(DateUtil.strToDate(closetime.getText(),"yyyy-MM-dd"));
        }
        else
        {
        	user.setAccountEndTime(DateUtil.strToDate("2050-12-30"));
        }
        user.setOrganizationId(selectedOrganization.getOrganizationId());
        user.setPasswordEndTime(new Date());
        user.setKuPassProblem(kuQuestion.getText());
        user.setKuPassAnswer(kuAnswer.getText());
        user.setEnable("1");
        user.setLockTime(new Date());
        user.setFocusContent(keyWord.getText());
        user.setPassword(Encryption.encryption(password.getText()));
        if(outdate.isChecked())
        {
        	  user.setIsLocked("2");
        }
        else
        {
        user.setIsLocked(isNotLock.isChecked() ? "0" : "1");
        }
        user.setKuRegDate(DateUtil.dateToStr(new Date(),"yyyy-MM-dd"));

        /*** 扩展属性 */
        // user.setKuAutoEnter((String)kuAutoEnter.getSelectedItem().getValue());
        user.setKuAutoShow((String) kuAutoShow.getSelectedItem().getValue());
        user.setKuBindAddr(kuBindAddr.getValue());
        user.setKuMajor(kumajor.getValue());
        user.setKuDuty(kuduty.getValue());
        user.setKuJobLevel(kulevel.getValue());
        user.setKuBindType((String) kuBindType.getSelectedItem().getValue());
        user.setKuBirthday(DateUtil.dateToStr(kuBirthday.getValue(),
                "yyyy-MM-dd"));
        /**
         *验证身份证号，如果选择身份证切身份证号有输入则进行验证
         */
        if (kuCertificatetype.getSelectedItem().getValue().equals("0"))
        {
            if (kuCardNumber.getValue() != null && !kuCardNumber.getValue().equals(""))
            {
                IdcardValidator IdCheck = new IdcardValidator();
                if (!IdCheck.isValidatedAllIdcard(kuCardNumber.getValue()))
                {
                    try
                    {
                        Messagebox.show(org.zkoss.util.resource.Labels
                                .getLabel("users.ui.kuCardNumberValidate"),
                                org.zkoss.util.resource.Labels
                                        .getLabel("system.commom.ui.prompt"),
                                Messagebox.OK, Messagebox.EXCLAMATION);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    return;
                }
            }
        }

        user.setKuCardNumber(kuCardNumber.getValue());
        user.setKuCertId(kuCertId.getValue());
        user.setKuCertificatetype((String) kuCertificatetype.getSelectedItem()
                .getValue());
       // user.setKuCertInfo(kuCertInfo.getValue());
        user.setKuCompany(kuCompany.getValue());
        user.setKuEmail(kuEmail.getValue());
        user.setKuForm((String) kuForm.getSelectedItem().getValue());
        user.setKuKeyLogin((String) kuKeyLogin.getSelectedItem().getValue());
        user.setKuSkinname("default");
        /**
         * 验证允许登录次数为不能小于0
         */
        if(kuLimit.getValue()!=null && !kuLimit.getValue().equals(""))
        {
           if(kuLimit.getValue()<0)
           {
               try
               {
                   Messagebox.show(org.zkoss.util.resource.Labels
                           .getLabel("users.zul.kuLimitValidate"),
                           org.zkoss.util.resource.Labels
                                   .getLabel("system.commom.ui.prompt"),
                           Messagebox.OK, Messagebox.EXCLAMATION);
               } catch (InterruptedException e)
               {
                   e.printStackTrace();
                   return;
               }
               return;
           }
           else
           {
        	   user.setKuLimit(kuLimit.getValue());
           }
        }
        else
        {
        user.setKuLimit(0);
        }
        // user.setKuOnline((String)kuOnline.getSelectedItem().getValue());
        user.setKuPhone(kuPhone.getValue());
        user.setKuPicPath(kuPicPath.getValue());
        user.setKuSex(nan.isChecked() ? "1" : "2");
        user.setKuAutoEnter("0");
        user.setKustyle("default");
        try
        {
            usersManager.add(user);
            userRoles = new ArrayList();
            for (Object obj : userRoleBox.getChildren())
            {
                if (obj instanceof Listitem)
                {
                    if (((Listitem) obj).getValue() instanceof Role)
                    {
                        userRoles.add(((Listitem) obj).getValue());
                    }
                }
            }
            for (Role r : (List<Role>) userRoles)
            {
                Userrole userrole = new Userrole();
                userrole.setRoleId(r.getRoleId());
                userrole.setUserId(user.getUserId());
                userroleManager.add(userrole);
            }
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            UsersListWin usesListWin = (UsersListWin) Executions.getCurrent()
                    .getDesktop().getAttribute("UsersListWin");
            usesListWin.selectId = user.getUserId();
            usesListWin.searchUsers();
            this.detach();
        } catch (Exception e)
        {
            log
                    .error(org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.time")
                            + new Date().toLocaleString()
                            + " class:"
                            + UsersAddWin.class.toString()
                            + org.zkoss.util.resource.Labels
                                    .getLabel("users.ui.addusererror")
                            + e.getMessage());
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.savefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }

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
     * 移动选中项
     * 
     * @param event
     */
    public void onClick$toRight(Event event)
    {

        if (unauthorizedRoles.getSelectedItem() == null)
            return;
        unauthorizedRoles.getSelectedItem().setParent(userRoleBox);

    }

    /**
     * 移动选中项
     * 
     * @param event
     */
    public void onClick$toLeft(Event event)
    {

        if (userRoleBox.getSelectedItem() == null)
            return;
        userRoleBox.getSelectedItem().setParent(unauthorizedRoles);

    }

    /**
     * 打开资源树
     * 
     * @param roleId
     */
    public void showResourceTree(int roleId)
    {
        Role roleS = new Role();
        roleS.setRoleId(roleId);
        Map map = new HashMap();
        map.put("role", roleS);
        Window resourceT = (Window) Executions.createComponents(
                "/apps/core/roleTree.zul", usersAddWin_add, map);
        try
        {
            resourceT.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 上传用户的图片
     */
    public void addImage()
    {
        String fileName = "";
        try
        {
            Media media = Fileupload.get();
            fileName = media.getName();

            String fileT[] = fileName.split(".");
            // fileName = fileName.substring(0, fileName.indexOf('.'))
            // + new Date().getTime()
            // + fileName.substring(fileName.indexOf('.'), fileName
            // .length());
            fileName = new Date().getTime()
                    + fileName.substring(fileName.indexOf('.'), fileName
                            .length());

            String filePath = Executions.getCurrent().getDesktop().getWebApp()
                    .getRealPath("images/usersphoto")
                    + "/" + fileName;
            String fileLastName = fileName.substring(fileName.lastIndexOf("."))
                    .toLowerCase();
            String imageType = ".jpg.png.gif.bmp.jpeg";
            if (imageType.indexOf(fileLastName) == -1)
            {
                Messagebox.show("请指定正确的文件类型！");
                return;
            }
            File file = new File(filePath);
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileTool.writeToFile(media.getStreamData(), file);
            kuPicPath.setValue("/images/usersphoto/" + fileName);
            if (fileName != null || !fileName.equals(""))
            {
                activeImage.setSrc("/images/usersphoto/" + fileName);
            }
        } catch (Exception e)
        {
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + org.zkoss.util.resource.Labels
                            .getLabel("resource.ui.rootmenumanager")
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}
