package com.uniwin.webkey.system.parameters.template.win;

public class TempTree
{

    // 模板树名称
    private String  _name;

    // 模板树是否打开
    private boolean _open;

    public TempTree(String name)
    {
        super();
        _name = name;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isOpen()
    {
        return _open;
    }

    public void setOpen(boolean open)
    {
        _open = open;
    }

}
