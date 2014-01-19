package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.model.Users;

public class UsersQueryWin extends Window implements AfterCompose
{
    private Listbox       users;

    private IUsersManager usersManager;

    public List getUsersData()
    {
        return usersData;
    }

    public void setUsersData(List usersData)
    {
        this.usersData = usersData;
    }

    public List usersData;

    public UsersQueryWin()
    {
        usersManager = (IUsersManager) SpringUtil.getBean("usersManager");
        Listitem item = null;
        Listcell cell = null;
        try
        {
            usersData = usersManager.getAllUser();
            for (Object user : usersData)
            {
                item = new Listitem();
                cell = new Listcell(((Users) user).getName());
                cell.setLabel(((Users) user).getName());
                item.appendChild(cell);
            }

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

    }

    public void afterCompose()
    {
        users = (Listbox) this.getFellow("users");
        Listitem item = null;
        Listcell cell = null;
        try
        {
            usersData = usersManager.getAllUser();
            for (Object user : usersData)
            {
                item = new Listitem();
                cell = new Listcell(((Users) user).getName());
                cell.setLabel(((Users) user).getName());
                item.appendChild(cell);
                users.appendChild(item);
            }

        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

    }

    public void onPaging$usersPage(Event event)
    {

    }

}
