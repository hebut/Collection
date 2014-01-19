package com.uniwin.webkey.control;

import java.io.Serializable;


public class CacheManager implements Serializable
{
    /**
     * 
     */
    private static final long    serialVersionUID = 1L;

    private BaseCache            infoCache;

    private static CacheManager instance;
    private static Object        lock             = new Object();

    public CacheManager()
    {
        // 这个根据配置文件来，初始BaseCache而已;
        infoCache = new BaseCache("", 1800);
    }

    public static CacheManager getInstance()
    {
        if (instance == null)
        {
            synchronized (lock)
            {
                if (instance == null)
                {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    public void put(String key,Object obj)
    {
        infoCache.put(key,obj);
    }

    public void removeCache(String key)
    {
        infoCache.remove(key);
    }
    
    public void removeCacheGroup(String groupKey)
    {
        infoCache.removeGroup(groupKey);
    }

    public Object getCache(String key)
    {
        try
        {
            return infoCache.get(key);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("getCache>>key[" + key + "]>>" + e.getMessage());
            return null;
        }
    }

    public void removeAllNews()
    {
        infoCache.removeAll();
    }

}
