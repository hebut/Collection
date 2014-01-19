package com.uniwin.webkey.component.model;

import java.io.Serializable;

public class DBTree4ZKBean implements Serializable
{
    private String name;

    private String image;

    private String hoverImage;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getHoverImage()
    {
        return hoverImage;
    }

    public void setHoverImage(String hoverImage)
    {
        this.hoverImage = hoverImage;
    }

}
