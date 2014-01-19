package com.uniwin.webkey.core.ui;

import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.util.ui.FrameCommonDate;

/**
 * File:    UsersWin.java
 * Author:  your_name
 * Company: uniWin
 * Created: Fri Apr 30 08:50:12 CST 2010 
 * NOTE:    本文件为自动生成,请根据实际情况进行删减,完善！
 */
public class UsersWin extends Window
{
    private User user;

    public UsersWin()
    {
        user = FrameCommonDate.getUser();
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
