package com.uniwin.webkey.component.ui;

import org.zkoss.zk.ui.Sessions;

import com.uniwin.webkey.util.ui.FrameCommonDate;

public class Image extends org.zkoss.zul.Image
{
    private String type;

    private String context;

    private String imageSrc;

    private String imageType;

    private String visible;

    public String getVisible()
    {
        return visible;
    }

    public void setVisible(String visible)
    {
        super.setVisible(Boolean.parseBoolean(visible));
    }

    private boolean isExcute;

    private String  skinName = "";

    public Image()
    {
        isExcute = true;
        skinName = "/images/"
                + Sessions.getCurrent().getAttribute("themeName").toString();
        
//        System.out.println("--themeName---"+Sessions.getCurrent().getAttribute("themeName").toString());
    }

    /**
     * 根据type属性选择图片的路径
     */
    public void imageInit()
    {
        isExcute = false;
        if (type == null)
        {
            return;
        }
        context = FrameCommonDate.getWebContext();
        if ("auto".equals(type))
        {
            if (imageSrc != null)
            {
                String activeImageSrc = this.imageSrc + "a" + this.imageType;
                String inActiveImageSrc = this.imageSrc + this.imageType;
                this.setAction("onMouseOver:this.src='" + context + skinName
                        + activeImageSrc + "';onMouseOut:this.src='" + context
                        + skinName + inActiveImageSrc + "';");
            }
            return;
        }
        if ("search".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.searInactimage"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.searActimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.searInactimage") + "';");
            return;
        }
        if ("add".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.addInactimage"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.addActimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.addInactimage") + "';");
            return;
        }
        if ("del".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.delInactimage"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.delActimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.delInactimage") + "';");
            return;
        }
        if ("delList".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactDelimage"));
            this.setTooltiptext(org.zkoss.util.resource.Labels
                    .getLabel("uiutil.ui.delete"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.actDelimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactDelimage") + "';");
            return;
        }
        if ("save".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactSaveimage"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.actSaveimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactSaveimage") + "';");
            return;
        }
        if ("authorize".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.actauthorizeimage"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactauthorizeimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.actauthorizeimage") + "';");
            return;
        }
        // if("addNext".equals(type)){
        // this.setSrc(skinName+"/images/common/inactaddNext.gif");
        // this.setAction("onMouseOver:this.src='"+context+skinName+"/images/common/actaddNext.gif';onMouseOut:this.src='"+context+skinName+"/images/common/inactaddNext.gif'");
        // return;
        // }
        if ("edit".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactEditimage"));
            this.setTooltiptext(org.zkoss.util.resource.Labels
                    .getLabel("uiutil.ui.edit"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.actEditimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("uiutil.ui.inactEditimage") + "';");
            return;
        }
        if ("down".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowDownimage"));
            this.setTooltiptext(org.zkoss.util.resource.Labels
                    .getLabel("uiutil.ui.edit"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowDownhoverimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowDownimage") + "';");
            return;
        }
        if ("up".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowUpimage"));
            this.setTooltiptext(org.zkoss.util.resource.Labels
                    .getLabel("uiutil.ui.edit"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowUphoverimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("users.zul.arrowUpimage") + "';");
            return;
        }
        if ("quit".equals(type))
        {
            this.setSrc(skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("wkthelp.ui.actquitimage"));
            this.setTooltiptext(org.zkoss.util.resource.Labels
                    .getLabel("wkthelp.zul.quit"));
            this.setAction("onMouseOver:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("wkthelp.ui.inactquitimage")
                    + "';onMouseOut:this.src='"
                    + context
                    + skinName
                    + org.zkoss.util.resource.Labels
                            .getLabel("wkthelp.ui.actquitimage") + "';");
            return;
        }
    }

    public void setSrc(String src)
    {
        super.setSrc(src);
        if (src.length() >= 5)
        {
            imageSrc = src.substring(0, src.length() - 4);
            imageType = src.substring(src.length() - 4);
        }
        if (isExcute)
            imageInit();
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
        imageInit();
    }

}
