package com.uniwin.webkey.system.parameters.template.win;

/**
 * <li>模板编辑,对应页面admin/persongal/system/parameters/TemplateManage/newCSS.zul
 * @author bobo
 * 2010-06-29
 */

import java.io.IOException;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.util.FileUtil;

public class NewCssWindow extends Window implements AfterCompose
{

    private static final long serialVersionUID = -945374772848264517L;

    // 文本框 组件
    private Textbox           cssId, cssContent, cssName;

    // Button按钮组件
    private Toolbarbutton     save;

    // 字符变量
    public String             cssFilename;

    public String getCssFilename()
    {
        return cssFilename;
    }

    public void setCssFilename(String cssFilename)
    {
        this.cssFilename = cssFilename;
    }

    // wkt文件路径
    String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/admin/styles");

    public void afterCompose()
    {

        Components.wireVariables(this, this);
        Components.addForwards(this, this);

        String cssString = "";
        if (cssFilename == null)
        {
            String defaultPath = path + "\\default.css";
            cssString = FileUtil.read(defaultPath);
            cssContent.setValue(cssString);
        } else
        {
            String cssPath = path + "\\" + cssFilename + "\\index.css";
            cssId.setValue(cssFilename);
            cssId.setDisabled(true);

            if (cssPath != null)
            {
                cssName.setValue(FileUtil.getStyleName(cssPath));
                cssString = FileUtil.read(cssPath);
                Integer beginIndex = cssString.indexOf("==*/") + 4;
                if (cssString.length() > beginIndex)
                {
                    cssContent.setValue(cssString.substring(beginIndex));
                } else
                {
                    cssContent.setValue("");
                }
            } else
            {
                cssName.setValue("");
                cssContent.setValue("");
            }
        }
        this.addForward(Events.ON_OK, save, Events.ON_CLICK);

    }

    /**
     * <li>功能描述：保存CSS样式文件 void
     * 
     * @author bobo
     * @throws IOException
     * @2010-06-29
     */
    public void onClick$save() throws InterruptedException, IOException
    {
        String cssFolder = cssId.getValue();
        String cssStyle = "/*==" + cssName.getValue() + "==*/" + "\r\n"+ cssContent.getValue();
        String path0 = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/admin/styles");
        FileUtil.mkDir(path0, cssFolder);
        FileUtil.writeFile(path0 + "\\" + cssFolder, cssStyle, "index.css");

        // 创建image文件夹和图片文件
        if (cssFilename == null)
        {
            FileUtil.mkDir(path0 + "\\" + cssFolder, "Image");
            byte[] gifFile = FileUtil.readBinaryFile(path0 + "\\new.gif");
            FileUtil.writeBinaryFile(gifFile, path0 + "\\" + cssFolder + "\\Image\\new.gif");
        }
        Messagebox.show("保存成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
        Events.postEvent(Events.ON_CHANGE, this, null);
        this.detach();
    }

    public void onClick$renturn()
    {
        this.detach();
    }
}
