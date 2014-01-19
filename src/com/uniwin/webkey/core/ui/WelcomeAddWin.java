package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.Date;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IWelcomeManager;
import com.uniwin.webkey.core.model.Welcome;
import com.uniwin.webkey.core.util.FileTool;
import com.uniwin.webkey.system.ui.WorkbenchWin;

public class WelcomeAddWin extends Window
{
    private IWelcomeManager welcomeManager = (IWelcomeManager) SpringUtil
                                                   .getBean("welcomeManager");

    public WelcomeAddWin()
    {
    }
    /**
	 * 上传图片
	 */
    public void addImage()
    {
        String fileName = "";
        try
        {
            Media media = Fileupload.get();
            fileName = media.getName();

            String fileT[] = fileName.split(".");
            fileName = fileName.substring(0, fileName.indexOf('.'))
                    + new Date().getTime()
                    + fileName.substring(fileName.indexOf('.'), fileName
                            .length());

            String filePath = Executions.getCurrent().getDesktop().getWebApp()
                    .getRealPath("images/system")
                    + "/" + fileName;
            File file = new File(filePath);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileTool.writeToFile(media.getStreamData(), file);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.uploadsuccess"));
        } catch (Exception e)
        {

        }
    }

    /**
	 * 添加欢迎内容
	 */
    public void addWelcome()
    {
        Textbox name = (Textbox) this.getFellow("name");
        Textbox url = (Textbox) this.getFellow("url");
        Checkbox move = (Checkbox) this.getFellow("move");
        try
        {
            if (welcomeManager.getWelcomeByName(name.getText()))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("welcome.ui.name")
                        + "’"
                        + name
                        + "‘"
                        + org.zkoss.util.resource.Labels
                                .getLabel("welcome.ui.tablPresence"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            if (url.getText().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("welcome.ui.urlvalidate"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        int visible = 0;
        Welcome welcome = new Welcome();
        welcome.setName(name.getText());
        welcome.setUrl(url.getText());
        welcome.setVisible(move.isChecked() ? 0 : 1);
        try
        {
            welcomeManager.add(welcome);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.savesuccess"),
                    org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.prompt"),
                    Messagebox.OK, Messagebox.INFORMATION);
            ((WorkbenchWin) this.getDesktop().getAttribute("WorkbenchWin"))
                    .reOpenTab();
            this.detach();
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("system.commom.ui.savefailed"),
                        org.zkoss.util.resource.Labels
                                .getLabel("system.commom.ui.prompt"),
                        Messagebox.OK, Messagebox.INFORMATION);
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }

    }

}
