package com.uniwin.webkey.component.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Toolbarbutton;

import com.uniwin.webkey.common.DAOImpl;
import com.uniwin.webkey.common.IDAO;

public class Listbox extends org.zkoss.zul.Listbox
{
    /**
	 * 
	 */
    private static final long  serialVersionUID = 1L;

    private List               date;

    private boolean            editor           = false;

    private boolean            delete           = false;

    private Map                _orderMap        = new LinkedHashMap();

    private Map                _queryMap        = new HashMap();

    private List<ListMoldBean> _headerList      = new ArrayList<ListMoldBean>();

    private IDAO               daoImpl          = (IDAO) SpringUtil
                                                        .getBean("daoImpl");

    private Paging             paging           = new Paging();                  ;

    private int                pageSize;

    private String             pagingMold       = "default";

    private String             pagingAlign      = "left";

    private String             model;

    public String getPagingMold()
    {
        return pagingMold;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setPagingMold(String pagingMold)
    {
        this.pagingMold = pagingMold;
    }

    public String getPagingAlign()
    {
        return pagingAlign;
    }

    public void setPagingAlign(String pagingAlign)
    {
        this.pagingAlign = pagingAlign;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public Paging getPaging()
    {
        return paging;
    }

    public void setPaging(Paging paging)
    {
        this.paging = paging;
    }

    public void setDaoImpl(DAOImpl daoImpl)
    {
        this.daoImpl = daoImpl;
    }

    public Map getOrderMap()
    {
        return _orderMap;
    }

    public void setOrderMap(Map orderMap)
    {
        this._orderMap = orderMap;
    }

    public Map getQueryMap()
    {
        return _queryMap;
    }

    public void setQueryMap(Map queryMap)
    {
        this._queryMap = queryMap;
    }

    public List<ListMoldBean> getHeaderList()
    {
        return _headerList;
    }

    public List getDate()
    {
        return date;
    }

    public void setDate(List date)
    {
        this.date = date;
    }

    public void setHeaderList(List<ListMoldBean> headerList)
    {
        _headerList = headerList;
    }

    public boolean isEditor()
    {
        return editor;
    }

    public void setEditor(boolean editor)
    {
        this.editor = editor;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setDelete(boolean delete)
    {
        this.delete = delete;
    }

    public Listbox()
    {
        super();
    }

    /**
	 * 定义的listbox中的方法
	 */
    private void setListItemDeleteAndEdit()
    {
        if (editor)
        {
            for (Listitem item : (List<Listitem>) this.getItems())
            {
                Listcell editorCell = new Listcell();
                Toolbarbutton editorbtn = new Toolbarbutton();
                editorbtn.setLabel("编辑");
                editorbtn.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        Listbox listbox = (Listbox) event.getTarget()
                                .getParent().getParent().getParent();
                        listbox.setSelectedItem((Listitem) event.getTarget()
                                .getParent().getParent());
                        Events.postEvent("onEdit", listbox, null);
                    }
                });
                editorCell.appendChild(editorbtn);
                item.appendChild(editorCell);
            }
        }
        if (delete)
        {
            for (Listitem item : (List<Listitem>) this.getItems())
            {
                Listcell editorCell = new Listcell();
                Toolbarbutton editorbtn = new Toolbarbutton();
                editorbtn.setLabel("删除");
                editorbtn.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        Listbox listbox = (Listbox) event.getTarget()
                                .getParent().getParent().getParent();
                        listbox.setSelectedItem((Listitem) event.getTarget()
                                .getParent().getParent());
                        Events.postEvent("onDelete", listbox, null);
                    }
                });
                editorCell.appendChild(editorbtn);
                item.appendChild(editorCell);
            }
        }
    }

    /**
	 * 定义的listbox中的方法
	 */
    private void setEditAndDelete()
    {
        if (editor)
        {
            Listheader editeListheader = new Listheader();
            editeListheader.setLabel("编辑");
            editeListheader.setWidth("50px");
            editeListheader.setAlign("center");
            this.getListhead().appendChild(editeListheader);
            for (Listitem item : (List<Listitem>) this.getItems())
            {
                Listcell editorCell = new Listcell();
                Toolbarbutton editorbtn = new Toolbarbutton();
                editorbtn.setLabel("编辑");
                editorbtn.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        Listbox listbox = (Listbox) event.getTarget()
                                .getParent().getParent().getParent();
                        listbox.setSelectedItem((Listitem) event.getTarget()
                                .getParent().getParent());
                        Events.postEvent("onEdit", listbox, null);
                    }
                });
                editorCell.appendChild(editorbtn);
                item.appendChild(editorCell);
            }
        }
        if (delete)
        {
            Listheader deleteListheader = new Listheader();
            deleteListheader.setLabel("删除");
            deleteListheader.setWidth("50px");
            deleteListheader.setAlign("center");
            this.getListhead().appendChild(deleteListheader);
            for (Listitem item : (List<Listitem>) this.getItems())
            {
                Listcell editorCell = new Listcell();
                Toolbarbutton editorbtn = new Toolbarbutton();
                editorbtn.setLabel("删除");
                editorbtn.addEventListener("onClick", new EventListener()
                {
                    public void onEvent(Event event) throws Exception
                    {
                        Listbox listbox = (Listbox) event.getTarget()
                                .getParent().getParent().getParent();
                        listbox.setSelectedItem((Listitem) event.getTarget()
                                .getParent().getParent());
                        Events.postEvent("onDelete", listbox, null);
                    }
                });
                editorCell.appendChild(editorbtn);
                item.appendChild(editorCell);
            }
        }
    }

    public void createListbox() throws Exception
    {
        this.date = getList();
        this.createListHead();
        this.createListItem();
        setEditAndDelete();
        createListfoot();
    }

    private void clear()
    {
        List list = this.getChildren();
        for (int i = 0; i < list.size(); i++)
        {
            Object object = list.get(i);
            if ((object instanceof Listitem))
            {
                this.removeChild((Component) object);
                i--;
            }
        }
    }

    /**
	 * 创建表头
	 */
    private void createListHead()
    {
        Listhead listhead = new Listhead();
        for (ListMoldBean bean : _headerList)
        {
            Listheader header = new Listheader();
            header.setLabel(bean.getLabel());
            header.setValue(bean);
            for (Object obj : _orderMap.keySet())
            {
                if (obj.equals(bean.getProperty()))
                {
                    if (_orderMap.get(obj).equals("DESC"))
                    {
                        header.setSortDirection("descending");
                    } else
                    {
                        header.setSortDirection("ascending");
                    }
                }
            }
            if (bean.isSort())
            {
                header.setSort("auto");
            }
            listhead.appendChild(header);
        }
        this.appendChild(listhead);
    }

    /**
	 * 创建加载列表中的项目
	 */
    private void createListItem()
    {
        try
        {
            for (Object object : date)
            {
                Listitem item = new Listitem();
                item.setValue(object);
                for (ListMoldBean bean : _headerList)
                {
                    Listcell cell = new Listcell();
                    String label = "";
                    Object obj = object;
                    if (bean.getProperty().indexOf(".") != -1)
                    {
                        String temp = bean.getProperty().replace('.', '/');
                        String[] pro = (temp).split("/");
                        for (String str : pro)
                        {
                            String methodName = "get"
                                    + str.substring(0, 1).toUpperCase()
                                    + str.substring(1);
                            Method[] methods = obj.getClass().getMethods();
                            for (int i = 0; i < methods.length; i++)
                            {
                                Method method = methods[i];
                                if (methodName.equals(method.getName()))
                                {
                                    obj = method.invoke(obj, null);
                                }
                            }
                        }
                        label = obj.toString();
                    } else
                    {
                        String methodName = "get"
                                + bean.getProperty().substring(0, 1)
                                        .toUpperCase()
                                + bean.getProperty().substring(1);

                        Method[] methods = object.getClass().getMethods();
                        for (int i = 0; i < methods.length; i++)
                        {
                            Method method = methods[i];
                            if (methodName.equals(method.getName()))
                            {
                                label = method.invoke(object, null).toString();
                            }
                        }
                    }
                    cell.setLabel(label);
                    item.appendChild(cell);
                }
                this.appendChild(item);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createListfoot()
    {
        Listfoot listfoot = new Listfoot();
        Listfooter listfooter = new Listfooter();
        paging.setPageSize(this.getPageSize());
        addPagingListener();
        int span = this.getHeaderList().size();
        span = span + (this.delete ? 1 : 0);
        span = span + (this.editor ? 1 : 0);
        listfooter.setSpan(span);
        paging.setMold(pagingMold);
        Div div = new Div();
        div.setAlign(pagingAlign);
        div.appendChild(paging);
        listfooter.appendChild(div);
        listfoot.appendChild(listfooter);
        this.appendChild(listfoot);
    }

    public String getOrderString()
    {
        String orderString = "";
        for (Object obj : _orderMap.keySet())
        {
            orderString += obj.toString() + " " + _orderMap.get(obj).toString()
                    + ",";
        }
        if (_orderMap.size() == 0)
        {
            return "";
        }
        return orderString.substring(0, orderString.length() - 1);
    }
    /**
	 * 表头类，增加表头排序的方法
	 */
    public class Listheader extends org.zkoss.zul.Listheader
    {
        public void onSort(Event event)
        {
            boolean ishas = true;
            Listheader listheader = (Listheader) event.getTarget();
            ListMoldBean moldBean = (ListMoldBean) listheader.getValue();
            for (Object obj : _orderMap.keySet())
            {
                if (moldBean.getProperty().equals(obj))
                {
                    ishas = false;
                    if (_orderMap.get(obj).equals("DESC"))
                    {
                        _orderMap.put(obj, "");
                        listheader.setSortDirection("ascending");
                    } else
                    {
                        _orderMap.put(obj, "DESC");
                        listheader.setSortDirection("descending");
                    }
                }
            }
            if (ishas)
            {
                _orderMap.put(moldBean.getProperty(), "DESC");
                listheader.setSortDirection("descending");
            }
            Listbox listbox = (Listbox) listheader.getParent().getParent();
            try
            {
                setDate(getList());
            } catch (Exception e)
            {
            }
            clear();
            createListItem();
            setListItemDeleteAndEdit();
            Events.postEvent("onSort", listbox, null);

        }

    }

    private List getList() throws Exception
    {
        if (model == null || model.equals(""))
        {
            throw new Exception("对象不能为空");
        }
        String hql = "from";
        if (_orderMap.size() != 0)
        {
            hql += " " + model + " order by " + this.getOrderString();
        } else
        {
            hql += " " + model + " ";
        }
        String hqlsize = "select * from " + model;
        Session session = this.daoImpl.getDaoSession();
        Query q = session.createQuery(hql);
        q.setFirstResult(paging.getActivePage());
        q.setMaxResults(this.pageSize);
        List list = q.list();
        Query qsize = session.createQuery(hql);
        int countsize = qsize.list().size();
        paging.setTotalSize(countsize);
        session.close();
        return list;
    }

    public void changePaging()
    {

    }

    private void addPagingListener()
    {
        this.paging.addEventListener("onPaging", new EventListener()
        {

            public void onEvent(Event arg0) throws Exception
            {
                setDate(getList());
                clear();
                createListItem();
                setListItemDeleteAndEdit();
                Events.postEvent("onPaging", arg0.getTarget().getParent()
                        .getParent().getParent().getParent(), null);
            }
        });
    }

    public void reloadList()
    {
        this.getChildren().clear();
        try
        {
            createListbox();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
