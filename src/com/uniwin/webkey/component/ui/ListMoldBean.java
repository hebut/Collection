package com.uniwin.webkey.component.ui;

public class ListMoldBean
{
    private String  label;

    private String  property;

    private boolean isSort = false;

    public ListMoldBean()
    {
    }

    public ListMoldBean(String label, String property)
    {
        this.label = label;
        this.property = property;
    }

    public ListMoldBean(String label, String property, boolean isSort)
    {
        this.label = label;
        this.property = property;
        this.isSort = isSort;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public boolean isSort()
    {
        return isSort;
    }

    public void setSort(boolean isSort)
    {
        this.isSort = isSort;
    }
}
