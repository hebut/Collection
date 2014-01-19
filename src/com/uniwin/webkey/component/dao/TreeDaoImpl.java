package com.uniwin.webkey.component.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.uniwin.webkey.common.DAOImpl;
import com.uniwin.webkey.core.itf.ITreeDao;

public class TreeDaoImpl extends DAOImpl implements ITreeDao
{
    public List executeSQL(final String sql)
    {
        return (List) super.getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    public Object doInHibernate(Session arg0)
                            throws HibernateException, SQLException
                    {
                        return arg0.createSQLQuery(sql).setResultTransformer(
                                Transformers.ALIAS_TO_ENTITY_MAP).list();
                    }
                });
    }

}
