package com.uniwin.webkey.component.ui;

import java.io.Serializable;

public class Menu implements Serializable
{
    private Integer id;

    private Integer weizhi;

    public Integer getWeizhi()
    {
        return weizhi;
    }

    public void setWeizhi(Integer weizhi)
    {
        this.weizhi = weizhi;
    }

    private String Name;

    private String pageUrl;

    private String inactiveImageurl;

    private String activeImageurl;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public String getInactiveImageurl()
    {
        return inactiveImageurl;
    }

    public void setInactiveImageurl(String inactiveImageurl)
    {
        this.inactiveImageurl = inactiveImageurl;
    }

    public String getActiveImageurl()
    {
        return activeImageurl;
    }

    public void setActiveImageurl(String activeImageurl)
    {
        this.activeImageurl = activeImageurl;
    }

}
