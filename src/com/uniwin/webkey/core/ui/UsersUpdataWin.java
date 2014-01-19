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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
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
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.UsersEx;
import com.uniwin.webkey.core.util.FileTool;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.util.ui.CheckLoginFilter;
import com.uniwin.webkey.util.ui.FrameCommonDate;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

/**
 * 
 * @author gbDong
 * 
 */
public class UsersUpdataWin extends Window implements AfterCompose
{
    private IUsersManager    usersManager;

    private IUserroleManager userroleManager = (IUserroleManager) SpringUtil.getBean("userroleManager");

    private Logger           log             = Logger
                                                     .getLogger(UsersUpdataWin.class);

    private Textbox          name, loginName, password, configPassword,kuAnswer,kuQuestion;               // 用户名；登录别名；密码；确认密码,密码提示问题,密码提示答案

    private Button           updataUser;                                              // 更新用户按钮

    private UsersEx          user;                                                    // 要更新的用户对象

    private OrganizationTree parentData;

    private Organization     selectedOrganization;

    private Bandbox          organizationId;

    private Radio            isNotLock, nan, nv;

    private Radio            isLock,outdate;

    private List             userRoles;

    private List             allRoles;

    private Listbox          unauthorizedRoles, userRoleBox;

    private Image            activeImage;

    private Date             birthday;

    private Datebox          kuBirthday,opentime,closetime;

    private Textbox          kuEmail;

    private Textbox          kuPhone;

    private Combobox         kuCertificatetype;

    private Textbox          kuCardNumber;

    private Textbox          kuPicPath;

    private Textbox          kuCompany;

    // private Combobox kuOnline;
    private Intbox           kuLimit;

    private Combobox         kuAutoShow;

    private Combobox         kuBindType;

    private Textbox          kuBindAddr;

    // private Combobox kuAutoEnter;
    private Combobox         kuKeyLogin;

    private Textbox          kuCertId;

    private Textbox          kuCertInfo;
    
    private Textbox             kumajor;
    
    private Textbox             kuduty;
    
    private Textbox             kulevel,keyWord;


    private Combobox         kuForm;

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Bandbox getOrganizationId()
    {
        return organizationId;
    }

    public void setOrganizationId(Bandbox organizationId)
    {
        this.organizationId = organizationId;
    }

    public UsersUpdataWin()
    {
        usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
        Map map = Executions.getCurrent().getArg();
        user = (UsersEx) map.get("user");
        if (user.getKuBirthday() != null && !"".equals(user.getKuBirthday()))
        {
            birthday = DateUtil.strToDate(user.getKuBirthday());
        }

    }

    public void afterCompose()
    {
        activeImage = (Image) this.getFellow("activeImage");
        isNotLock = (Radio) this.getFellow("isNotLock");
        nan = (Radio) this.getFellow("nan");
        nv = (Radio) this.getFellow("nv");
        isLock = (Radio) this.getFellow("isLock");
        outdate=(Radio) this.getFellow("outdate");
        opentime=(Datebox) this.getFellow("opentime");
        closetime=(Datebox) this.getFellow("closetime");
        organizationId = (Bandbox) this.getFellow("organizationId");
        name = (Textbox) this.getFellow("name");
        loginName = (Textbox) this.getFellow("loginName");
        password = (Textbox) this.getFellow("password");
        configPassword = (Textbox) this.getFellow("configPassword");
        parentData = (OrganizationTree) this.getFellow("parentData");
        kuQuestion = (Textbox) this.getFellow("kuQuestion");
        kuAnswer = (Textbox) this.getFellow("kuAnswer");
        kumajor=(Textbox) this.getFellow("kumajor");
        kuduty=(Textbox) this.getFellow("kuduty");
        kulevel=(Textbox) this.getFellow("kulevel");
        keyWord = (Textbox) this.getFellow("keyWord");
        
        this.parentData.setLabelProvider(new OrganizationLabelProvider());
        this.parentData
                .setContentProvider(new OrganizationThirdContentProvider());
        this.parentData.rebuildTree();
        fullBandBox();
        organizationId.setValue(user.getOrganizationName());
//        isNotLock.setChecked(this.user.getIsLocked().trim().equals("0")
//                || this.user.getIsLocked().trim().equals("2"));
//        isLock.setChecked(this.user.getIsLocked().trim().equals("1"));
       Users u= usersManager.getUserByuid(Long.parseLong(user.getUserId()+""));
        opentime.setText(u.getKuRegDate());
        closetime.setValue(u.getAccountEndTime());
        kumajor.setValue(u.getKuMajor());
        kuduty.setValue(u.getKuDuty());
        kulevel.setValue(u.getKuJobLevel());
        	 isNotLock.setChecked(this.user.getIsLocked().trim().equals("0"));
             isLock.setChecked(this.user.getIsLocked().trim().equals("1"));
        	 outdate.setChecked(this.user.getIsLocked().trim().equals("2"));
//        if (user.getIsLocked().trim().equals("2"))
//        {
//            isLock.setVisible(false);
//        }
        selectedOrganization = new Organization();
        selectedOrganization.setOrganizationId(user.getOrganizationId());
        unauthorizedRoles = (Listbox) this.getFellow("unauthorizedRoles");
        userRoleBox = (Listbox) this.getFellow("userRoleBox");
        try
        {
            userRoles = userroleManager.getRoleByUser(user.getUserId());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        fullRoles();

        kuBirthday = (Datebox) this.getFellow("kuBirthday");
        kuEmail = (Textbox) this.getFellow("kuEmail");
        kuPhone = (Textbox) this.getFellow("kuPhone");
        kuCertificatetype = (Combobox) this.getFellow("kuCertificatetype");
        kuCardNumber = (Textbox) this.getFellow("kuCardNumber");
        kuPicPath = (Textbox) this.getFellow("kuPicPath");
        kuCompany = (Textbox) this.getFellow("kuCompany");
        // kuOnline = (Combobox) this.getFellow("kuOnline");
        kuLimit = (Intbox) this.getFellow("kuLimit");
        kuAutoShow = (Combobox) this.getFellow("kuAutoShow");
        kuBindType = (Combobox) this.getFellow("kuBindType");
        kuBindAddr = (Textbox) this.getFellow("kuBindAddr");
        // kuAutoEnter = (Combobox) this.getFellow("kuAutoEnter");
        kuKeyLogin = (Combobox) this.getFellow("kuKeyLogin");
        kuCertId = (Textbox) this.getFellow("kuCertId");
        kuCertInfo = (Textbox) this.getFellow("kuCertInfo");
        kuForm = (Combobox) this.getFellow("kuForm");

        initCombox();

    }

    /**
     * 初使化绑定下拉列表值
     */
    public void initCombox()
    {

        /**
         * 绑定性别
         */
        if (user.getKuSex() != null && !user.getKuSex().equals(""))
        {
            if (user.getKuSex().equals("1"))
            {
                nan.setChecked(true);
            } else
            {
                nv.setChecked(true);
            }
        }

        /**
         * 绑定证件类型
         */
        if (user.getKuCertificatetype() != null
                && !user.getKuCertificatetype().equals(""))
        {
            for (int i = 0; i < kuCertificatetype.getItemCount(); i++)
            {
                Comboitem comboitem = (Comboitem) kuCertificatetype.getItems()
                        .get(i);
                if (comboitem.getValue().equals(user.getKuCertificatetype()))
                {
                    kuCertificatetype.setSelectedItem(comboitem);
                    break;
                }
            }
        } else
        {
            kuCertificatetype.setSelectedIndex(0);
        }

        // /**
        // * ���Ƿ�����
        // */
        // if (user.getKuOnline() != null && !user.getKuOnline().equals("")) {
        // for (int i = 0; i < kuOnline.getItemCount(); i++) {
        // Comboitem comboitem = (Comboitem)kuOnline.getItems().get(i);
        // if (comboitem.getValue().equals(user.getKuOnline())) {
        // kuOnline.setSelectedItem(comboitem);
        // break;
        // }
        // }
        // }else{
        // kuOnline.setSelectedIndex(0);
        // }

        /**
         * 绑定智能化页面
         */
        if (user.getKuAutoShow() != null && !user.getKuAutoShow().equals(""))
        {
            for (int i = 0; i < kuAutoShow.getItemCount(); i++)
            {
                Comboitem comboitem = (Comboitem) kuAutoShow.getItems().get(i);
                if (comboitem.getValue().equals(user.getKuAutoShow()))
                {
                    kuAutoShow.setSelectedItem(comboitem);
                    break;
                }
            }
        } else
        {
            kuAutoShow.setSelectedIndex(0);
        }

        /**
         * 绑定绑定类型
         */
        if (user.getKuBindType() != null && !user.getKuBindType().equals(""))
        {
            for (int i = 0; i < kuBindType.getItemCount(); i++)
            {
                Comboitem comboitem = (Comboitem) kuBindType.getItems().get(i);
                if (comboitem.getValue().equals(user.getKuBindType()))
                {
                    kuBindType.setSelectedItem(comboitem);
                    break;
                }
            }
        } else
        {
            kuBindType.setSelectedIndex(0);
        }

        // /**
        // * ���Զ���¼
        // */
        // if (user.getKuAutoEnter() != null &&
        // !user.getKuAutoEnter().equals("")) {
        // for (int i = 0; i < kuAutoEnter.getItemCount(); i++) {
        // Comboitem comboitem = (Comboitem)kuAutoEnter.getItems().get(i);
        // if (comboitem.getValue().equals(user.getKuAutoEnter())) {
        // kuAutoEnter.setSelectedItem(comboitem);
        // break;
        // }
        // }
        // }else{
        // kuAutoEnter.setSelectedIndex(0);
        // }

        /**
         * 绑定是否用key
         */
        if (user.getKuKeyLogin() != null && !user.getKuKeyLogin().equals(""))
        {
            for (int i = 0; i < kuKeyLogin.getItemCount(); i++)
            {
                Comboitem comboitem = (Comboitem) kuKeyLogin.getItems().get(i);
                if (comboitem.getValue().equals(user.getKuKeyLogin()))
                {
                    kuKeyLogin.setSelectedItem(comboitem);
                    break;
                }
            }
        } else
        {
            kuKeyLogin.setSelectedIndex(0);
        }

        /**
         * 绑定来源
         */
        if (user.getKuForm() != null && !user.getKuForm().equals(""))
        {
            for (int i = 0; i < kuForm.getItemCount(); i++)
            {
                Comboitem comboitem = (Comboitem) kuForm.getItems().get(i);
                if (comboitem.getValue().equals(user.getKuForm()))
                {
                    kuForm.setSelectedItem(comboitem);
                    break;
                }
            }
        } else
        {
            kuForm.setSelectedIndex(0);
        }

    }

    /**
     * 修改用户信息
     * 
     * @param event
     */
    public void updataUserRole(Event event)
    {

        try
        {

            userRoles = new ArrayList();
            for (Object obj : userRoleBox.getItems())
            {
                if (obj instanceof Listitem)
                {
                    if (((Listitem) obj).getValue() instanceof Role)
                    {
                        userRoles.add(((Listitem) obj).getValue());
                    }
                }
            }
            userroleManager.updateUserRole(userRoles, user.getUserId());
            ((UsersListWin) this.getDesktop().getAttribute("UsersListWin"))
                    .searchUsers();
            CheckLoginFilter.reloadSessionsData();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *修改用户信息
     * 
     * @param event
     */
    public void updataUser(Event event)
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
            if (!checkLoginName(loginName.getText(), user.getUserId()))
                return;
//            if (loginName.getText().trim().length() < 6)
//            {
//                Messagebox.show(org.zkoss.util.resource.Labels
//                        .getLabel("users.ui.loginnamevalidateLength"),
//                        org.zkoss.util.resource.Labels
//                                .getLabel("system.commom.ui.prompt"),
//                        Messagebox.OK, Messagebox.EXCLAMATION);
//                return;
//            }
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
            
            if (!password.getText().equals(configPassword.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.passwordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (password.getText().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.password"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            if (kuEmail.getValue().equals(""))
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
        try
        {
            Users updateUser = new Users();
            updateUser.setUserId(user.getUserId());
            updateUser = usersManager.get(user.getUserId());
            if(outdate.isChecked())
            {
            	updateUser.setIsLocked("2");
            }
            else
            {
            updateUser.setIsLocked(isNotLock.isChecked() ? "0" : "1");
            }
            updateUser.setEnable("1");
//            if (user.getIsLocked().trim().equals("2"))
//            {
//                updateUser.setIsLocked("2");
//            }
            updateUser.setOrganizationId(this.selectedOrganization
                    .getOrganizationId());
            updateUser.setName(name.getText());
            updateUser.setLoginName(loginName.getText());
            User user = FrameCommonDate.getUser();
            if (password.getText().equals(this.user.getPassword()))
            {
                updateUser.setPassword(password.getText());
            } else
            {
                updateUser.setPassword(Encryption
                        .encryption(password.getText()));
            }
            // 临时时间
            Date dt = new Date();
            updateUser.setLockTime(dt);
            updateUser.setAccountEndTime(DateUtil.strToDate(closetime.getText()));
            updateUser.setPasswordEndTime(dt);
            
            

            /***  扩展属性 */
            // updateUser.setKuAutoEnter((String)kuAutoEnter.getSelectedItem().getValue());
            updateUser.setKuAutoShow((String) kuAutoShow.getSelectedItem()
                    .getValue());
            updateUser.setKuBindAddr(kuBindAddr.getValue());
            updateUser.setKuMajor(kumajor.getValue());
            updateUser.setKuDuty(kuduty.getValue());
            updateUser.setKuJobLevel(kulevel.getValue());
            updateUser.setFocusContent(keyWord.getValue());
            updateUser.setKuBindType((String) kuBindType.getSelectedItem()
                    .getValue());
            updateUser.setKuBirthday(DateUtil.dateToStr(kuBirthday.getValue(),
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
            updateUser.setKuCardNumber(kuCardNumber.getValue());
            updateUser.setKuCertId(kuCertId.getValue());
            updateUser.setKuCertificatetype((String) kuCertificatetype
                    .getSelectedItem().getValue());
            updateUser.setKuCertInfo(kuCertInfo.getValue());
            updateUser.setKuCompany(kuCompany.getValue());
            updateUser.setKuEmail(kuEmail.getValue());
            updateUser.setKuForm((String) kuForm.getSelectedItem().getValue());
            updateUser.setKuKeyLogin((String) kuKeyLogin.getSelectedItem()
                    .getValue());
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
            }
            updateUser.setKuLimit(kuLimit.getValue());
            // updateUser.setKuOnline((String)kuOnline.getSelectedItem().getValue());
            updateUser.setKuPhone(kuPhone.getValue());
            updateUser.setKuPicPath(kuPicPath.getValue());
            updateUser.setKuSex(nan.isChecked() ? "1" : "2");
            updateUser.setKuPassProblem(kuQuestion.getText());
            updateUser.setKuPassAnswer(kuAnswer.getText());

            usersManager.update(updateUser);
            updataUserRole(event);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((UsersListWin) this.getDesktop().getAttribute("UsersListWin")).selectId = updateUser
                    .getUserId();
            ((UsersListWin) this.getDesktop().getAttribute("UsersListWin"))
                    .searchUsers();
            this.detach();
        } catch (Exception e)
        {
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.changefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
            log.error(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.time")
                    + new Date().toLocaleString()
                    + " "
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.ui.userInfochange")
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public UsersEx getUser()
    {
        return user;
    }

    public void setUser(UsersEx user)
    {
        this.user = user;
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
     * 填充所有的角色
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
                Listcell cellName = new Listcell();
                final Listcell cellDomain = new Listcell();
                Listcell cellDesc = new Listcell();
                List clist=usersManager.getChildUsersRole(r.getRoleId());               
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
                            for(int i=0;i<userRoles.size();i++)
                            {
                            	Role userole=(Role) userRoles.get(i);
                            	int  ur= userole.getRoleId();	
                            if (roleTemp.getRoleId()==ur)
                            {
                                ((Listitem) obj).setParent(userRoleBox);
                            }
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
        	cellDomain.appendChild(up);        
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
                                for(int j=0;j<userRoles.size();j++)
                                {
                                	Role userole=(Role) userRoles.get(j);
                                	int  ur= userole.getRoleId();	
                                if (roleTemp.getRoleId()==ur)
                                {
                                    ((Listitem) obj).setParent(userRoleBox);
                                }
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

    /**
     * 移动选中项
     * 
     * @param event
     */
    public void moveListItem(Event event)
    {
        Image button = ((Image) event.getTarget());
        if (button.getId().trim().equals("toRight"))
        {
            if (unauthorizedRoles.getSelectedItem() == null)
                return;
            unauthorizedRoles.getSelectedItem().setParent(userRoleBox);
        } else
        {
            if (userRoleBox.getSelectedItem() == null)
                return;
            userRoleBox.getSelectedItem().setParent(unauthorizedRoles);
        }
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
                "/apps/core/roleTree.zul", this, map);
        try
        {
            resourceT.doModal();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 检查用户名
     * 
     * @param loginName
     * @param userId
     * @return
     */
    public boolean checkLoginName(String loginName, int userId)
    {
        try
        {
            List userList = usersManager.getUserByLoginName(loginName);
            if (userList.size() == 0)
                return true;
            else if (userList.size() == 1
                    && ((Users) userList.get(0)).getUserId() == userId)
            {
                return true;
            } else
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.loginnamevalidate1"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.EXCLAMATION);
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加上传图片
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
