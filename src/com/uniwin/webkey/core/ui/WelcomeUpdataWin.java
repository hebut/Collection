package com.uniwin.webkey.core.ui;

import java.io.File;
import java.util.Date;
import java.util.Map;

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

public class WelcomeUpdataWin extends Window
{
    private Welcome         welcome;

    private IWelcomeManager welcomeManager = (IWelcomeManager) SpringUtil
                                                   .getBean("welcomeManager");

    // private String imageUrl = "";
    // private Image browImage;
    public WelcomeUpdataWin()
    {
        Map map = Executions.getCurrent().getArg();
        welcome = (Welcome) map.get("welcome");
        // imageUrl = welcome.getImageUrl();
    }

    public Welcome getWelcome()
    {
        return welcome;
    }

    public void setWelcome(Welcome welcome)
    {
        this.welcome = welcome;
    }

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
            // browImage = (Image)this.getFellow("browImage");
            // browImage.setContent((AImage)media);
            // imageUrl = "/images/system/" + fileName;
        } catch (Exception e)
        {

        }
    }

    public void updataWelcome()
    {
        Textbox name = (Textbox) this.getFellow("name");
        Textbox url = (Textbox) this.getFellow("url");
        Checkbox move = (Checkbox) this.getFellow("move");
        try
        {
            if (url.getText().equals(""))
            {
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("welcome.ui.urlvalidate"),
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
        Welcome welcomeup = new Welcome();
        welcomeup.setId(welcome.getId());
        welcomeup.setName(name.getText());
        welcomeup.setUrl(url.getText());
        // welcomeup.setImageUrl(imageUrl);
        welcomeup.setVisible(move.isChecked() ? 0 : 1);
        try
        {
            welcomeManager.update(welcomeup);
            Messagebox.show(org.zkoss.util.resource.Labels
                    .getLabel("system.commom.ui.changesuccess"),
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
                        .getLabel("system.commom.ui.changefailedtoadmin"),
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
