package com.uniwin.webkey.core.ui;

import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class UserInfoWin extends Window
{
    private User user = FrameCommonDate.getUser();

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

}
