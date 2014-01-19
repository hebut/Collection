package com.uniwin.webkey.component.service;

import java.util.List;

import com.uniwin.webkey.common.exception.DataAccessException;

public interface TreeManager
{
    public List<Object> getChildren(String url) throws DataAccessException;
}
