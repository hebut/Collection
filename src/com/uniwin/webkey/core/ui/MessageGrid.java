package com.uniwin.webkey.core.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IUsersManager;
import com.uniwin.webkey.core.itf.IWktaccessoryManager;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.Wktaccessory;
import com.uniwin.webkey.core.model.Wktmessages;

public class MessageGrid extends Grid
{
    private IUsersManager        usersManager        = (IUsersManager) SpringUtil
                                                             .getBean(
                                                                     "usersManager");

    private IWktaccessoryManager wktaccessoryManager = (IWktaccessoryManager) SpringUtil
                                                             .getBean("wktaccessoryManager");

    private Wktmessages          wktmessages;

    public MessageGrid()
    {
    }

    public MessageGrid(Wktmessages wktmessages)
    {
        this.wktmessages = wktmessages;
        this.setWidth("100%");
        initRows();
    }

    /**
     * 初使化组件
     */
    public void initRows()
    {
        Rows rows = new Rows();

        //  用户信息列
        Row userInfoRow = new Row();
        Div userInfoDiv = new Div();
        userInfoDiv.setAlign("right");
        userInfoDiv.setWidth("100px");
        Label userInfoLabel = new Label();
        Users users = new Users();
        try
        {
            users = usersManager.get(wktmessages.getKU_SID());
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        } catch (ObjectNotExistException e)
        {
            e.printStackTrace();
        }
        userInfoLabel.setValue(users.getLoginName() + "说锟斤拷");
        userInfoDiv.appendChild(userInfoLabel);
        userInfoRow.appendChild(userInfoDiv);

        //  消息列
        Div messageDiv = new Div();
        messageDiv.setWidth("810px");
        messageDiv.setAlign("right");
        messageDiv.setStyle("text-align:center;");
        // 标题
        Div titleDiv = new Div();
        titleDiv
                .setStyle("text-align:center;padding-bottom:15px;padding-top:10px;border-bottom:1px dashed #999999;float:none;");
        Label titleLabel = new Label();
        titleLabel.setValue(wktmessages.getKM_TITLE());
        titleDiv.appendChild(titleLabel);

        // 内容
        Div contentDiv = new Div();
        contentDiv
                .setStyle("text-align:center;padding-bottom:15px;padding-top:10px;float:none;");
        Label contentLabel = new Label();
        contentLabel.setValue(wktmessages.getKM_CONTENT());
        contentDiv.appendChild(contentLabel);

        //  附件
        Div accessoryDiv = new Div();
        accessoryDiv
                .setStyle("text-align:center;padding-bottom:15px;padding-top:10px;float:none;");
        Wktaccessory wktaccessorySearch = new Wktaccessory();
        wktaccessorySearch.setKM_ID(wktmessages.getKM_ID());
        List<Wktaccessory> accessoryList = null;
        try
        {
            accessoryList = wktaccessoryManager.getList(wktaccessorySearch);
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }
        if (accessoryList != null && accessoryList.size() > 0)
        {
            for (Wktaccessory wktaccessory : accessoryList)
            {
                final Wktaccessory downWktaccessory = wktaccessory;
                Toolbarbutton toolbarbutton = new Toolbarbutton();
                toolbarbutton.setLabel(wktaccessory.getKA_NAME());
                toolbarbutton.setStyle("font-size:12px;color:#999999;");
                toolbarbutton.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event arg0) throws Exception
                    {
                        if (Messagebox.show("确定要下载该附件吗？", "提示信息", Messagebox.OK
                                | Messagebox.CANCEL, Messagebox.QUESTION) == 1)
                        {
                            downLoadAccessory(downWktaccessory);
                        }

                    }
                });
                accessoryDiv.appendChild(toolbarbutton);
                accessoryDiv.appendChild(new Space());
            }
        }
        // 日期
        Div dateDiv = new Div();
        dateDiv
                .setStyle("text-align:right;font-size:12px;color:#999999;width:100%;");
        Label dateLabel = new Label();
        dateLabel.setValue(wktmessages.getKM_TIME().toLocaleString());
        dateDiv.appendChild(dateLabel);

        messageDiv.appendChild(titleDiv);
        messageDiv.appendChild(contentDiv);
        messageDiv.appendChild(accessoryDiv);
        messageDiv.appendChild(dateDiv);
        userInfoRow
                .setStyle("text-align:center;border-bottom:1px solid #999999;");
        userInfoRow.appendChild(messageDiv);

        rows.appendChild(userInfoRow);
        rows.setStyle("border:0px;");
        this.appendChild(rows);

    }

    public void downLoadAccessory(Wktaccessory wktaccessory)
    {
        String filePath = Executions.getCurrent().getDesktop().getWebApp()
                .getRealPath("/images/messages/")
                + "/" + wktaccessory.getKA_NAME();
        File file = new File(filePath);

        try
        {
            Filedownload.save(file, "");
        } catch (FileNotFoundException e)
        {
            try
            {
                org.zkoss.zul.Messagebox.show("对不起，该文件已不存在！", "提示信息",
                        Messagebox.OK, Messagebox.INFORMATION);
            } catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
