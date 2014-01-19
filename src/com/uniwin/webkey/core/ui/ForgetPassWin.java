package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;

public class ForgetPassWin extends Window implements AfterCompose
{
    private IUsersManager usersManager = (IUsersManager) SpringUtil
                                               .getBean("usersManager");

    private Textbox       passQuestion;

    private Textbox       passAnswer;

    private Textbox       loginname;

    private Textbox       password;

    private LoginWin      loginWin;

    public void afterCompose()
    {
        passQuestion = (Textbox) this.getFellow("passQuestion");
        passAnswer = (Textbox) this.getFellow("passAnswer");
        loginWin = (LoginWin) Executions.getCurrent().getArg().get("loginWin");
        password = loginWin.getPassword();
        loginname = loginWin.getUserName();
    }

    /**
     * 登录
     */
    public void login()
    {

        try
        {
            if (loginname.getValue() == null
                    || loginname.getValue().trim().equals(""))
            {
                Messagebox.show("对不起，登录名 不能为空！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }
            if (passQuestion.getValue() == null
                    || passQuestion.getValue().trim().equals(""))
            {
                Messagebox.show("对不起，密码问题 不能为空！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }
            if (passAnswer.getValue() == null
                    || passAnswer.getValue().trim().equals(""))
            {
                Messagebox.show("对不起，密码问题 答案不能为空！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }

            Users usersSearch = new Users();
            usersSearch.setLoginName(loginname.getValue());
            List<Users> usersList = usersManager.getUserByLoginName(loginname
                    .getValue());
            Users users = new Users();
            if (usersList != null && usersList.size() > 0)
            {
                users = usersList.get(0);
            } else
            {
                Messagebox.show("对不起，该用户不存在！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }

            if (!passQuestion.getValue().equals(users.getKuPassProblem()))
            {
                Messagebox.show("对不起，密码提示问题不正确！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }

            if (!passAnswer.getValue().equals(users.getKuPassAnswer()))
            {
                Messagebox.show("对不起，密码问题答案不正确！", "提示信息", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            }
            loginWin.loginToSysByPassQuestion(users);
            this.detach();
        } catch (WrongValueException e)
        {
            e.printStackTrace();
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

}
