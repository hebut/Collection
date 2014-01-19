/**
 * 
 */
package com.uniwin.webkey.core.ui;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.util.Encryption;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class UsersUpdatePasswordWin extends Window implements AfterCompose
{
    private Textbox       oldPassword, newPassword, confirmNewPassword,
            passQuestion, passAnswer, forgetNewPass, forgetConfirmPass;

    private IUsersManager userManager;

    private User          user = FrameCommonDate.getUser();
    
    private Div method1,method2;

    public UsersUpdatePasswordWin()
    {
        userManager = (IUsersManager) SpringUtil.getBean("usersManager");
    }

    public void afterCompose()
    {
        oldPassword = (Textbox) this.getFellow("oldPassword");
        newPassword = (Textbox) this.getFellow("newPassword");
        confirmNewPassword = (Textbox) this.getFellow("confirmNewPassword");

        passQuestion = (Textbox) this.getFellow("passQuestion");
        passAnswer = (Textbox) this.getFellow("passAnswer");
        forgetNewPass = (Textbox) this.getFellow("forgetNewPass");
        forgetConfirmPass = (Textbox) this.getFellow("forgetConfirmPass");
        passQuestion.setValue(user.getKuPassProblem());
        Executions.getCurrent().setAttribute("method", 0);
    }

    /**
     * 修改密码
     */
    public void updateUserPassword()
    {
        try
        {
            if (!Encryption.encryption(oldPassword.getText()).equals(
                    user.getPassword()))
            {

                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.oldpasswordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            if (!newPassword.getText().equals(confirmNewPassword.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.newpasswordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            user.setPassword(Encryption.encryption(newPassword.getText()));
            Users userEntity = userManager.get(user.getUserId());
            userEntity
                    .setPassword(Encryption.encryption(newPassword.getText()));
            userManager.update(userEntity);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.savehailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改密码
     */
    public void updateUserPassword2()
    {
        try
        {
        	

//            if (passQuestion.getValue() == null
//                    || passQuestion.getValue().trim().equals(""))
//            {
//                Messagebox.show("对不起，密码问题 不能为空！", "提示信息", Messagebox.OK,
//                        Messagebox.INFORMATION);
//                return;
//            }
        		if (!passAnswer.getValue().equals(user.getKuPassAnswer()))
                {
                    Messagebox.show("对不起，密码问题答案不正确！", "提示信息", Messagebox.OK,
                            Messagebox.INFORMATION);
                    return;
                }
            if (forgetNewPass.getValue() == null
                    || forgetNewPass.getValue().trim().equals(""))
            {
                Messagebox.show("对不起，新密码不能为空！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }

//            if (!passQuestion.getValue().equals(user.getKuPassProblem()))
//            {
//                Messagebox.show("对不起，密码提示问题不正确！", "提示信息", Messagebox.OK,
//                        Messagebox.INFORMATION);
//                return;
//            }

            

            if (!forgetNewPass.getText().equals(forgetConfirmPass.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.newpasswordvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            user.setPassword(Encryption.encryption(forgetNewPass.getText()));
            Users userEntity = userManager.get(user.getUserId());
            userEntity.setPassword(Encryption.encryption(forgetNewPass
                    .getText()));
            userManager.update(userEntity);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("users.ui.savehailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.ERROR);
            } catch (Exception e2)
            {
                e.printStackTrace();
            }
        }
    }

    public void showMethod(int num)
    {
        for (int i = 1; i <= 2; i++)
        {
            if (num == i)
            {
                ((Div) this.getFellow("method" + i)).setVisible(true);
                Executions.getCurrent().setAttribute("method", num);
            } else
            {
                ((Div) this.getFellow("method" + i)).setVisible(false);
            }
        }
    }
    
    
    public void onClick$reset(){
    	passQuestion.setValue("");
    	passAnswer.setValue("");
    	forgetNewPass.setValue("");
    	forgetConfirmPass.setValue("");
    }
    

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

}
