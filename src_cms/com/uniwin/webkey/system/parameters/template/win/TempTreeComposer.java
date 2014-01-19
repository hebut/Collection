package com.uniwin.webkey.system.parameters.template.win;

/**
 * <li>功能描述：选择树节点（模板节点和分组节点），弹出相应页面
 * @author bobo
 * @serialData 2010-8-23
 */
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.cms.util.GroupUtil;
import com.uniwin.webkey.cms.util.WebkeyControlUtil;

public class TempTreeComposer extends GenericForwardComposer
{
	private static final long serialVersionUID = 3251548439522932122L;

	// 模板树 组件
    private Tree           tempTree;

    // 模板树Model
    private TempTreeModel  TemptreeModel;

    // 模板树节点
    private SimpleTreeNode tempnode;

    // wkt文件路径
    String                 path0 = WebkeyControlUtil.getPath("/admin/styles");

    // Center组件
    Center                 cssListCen;

    @Override
	public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);

        tempTree.setTreeitemRenderer(new TreeitemRenderer());

        tempTree.addEventListener(Events.ON_SELECT, new EventListener()
        {
            public void onEvent(Event event) throws Exception
            {
                Tree selected = (Tree) event.getTarget();
                Treeitem item = selected.getSelectedItem();
                String cssName = item.getLabel();
                cssName=URLEncoder.encode(cssName,"utf-8");
                String rootPath = Executions.getCurrent().getContextPath().substring(1);
                WebkeyControlUtil.setProOfCookie("webkeyRoot", rootPath);
                WebkeyControlUtil.setProOfCookie("webkeyCss", cssName);
                if (item.getLabel().equals("模板管理".trim()))
                {
                    cssListCen.getChildren().clear();
                    final IndexTempListWindow mainWin = (IndexTempListWindow) Executions
                            .createComponents("/apps/cms/template/indexTempList.zul",cssListCen, null);
                    mainWin.initWindow();
                    mainWin.addEventListener(Events.ON_CHANGE,
                            new EventListener()
                            {
                                public void onEvent(Event event)
                                        throws Exception
                                {
                                    loadTree();
                                }
                            });

                } else if (item != null)
                {
                    File cssFolder = new File(path0);
                    File[] folderList = cssFolder.listFiles();
                    new ArrayList();
                    String fn = "", groupname = "";
                    for (int i = 0; i < folderList.length; i++)
                    {
                        String path = path0 + "\\" + folderList[i].getName()
                                + "\\" + "index.css";
                        if (folderList[i].isDirectory() && path != null)
                        {

                            String resu = FileUtil.getStyleName(path);
                            // 当选择模板名称节点。。。。
                            if (item.getLabel().equals(resu))
                            {
                                fn = folderList[i].getName();
                                Map mapTList = new HashMap();
                                mapTList.put("foldername", fn);
                                cssListCen.getChildren().clear();

                                Sessions.getCurrent().setAttribute("filename",
                                        fn);

                                final TempListWindow tempWin = (TempListWindow) Executions
                                        .createComponents(
                                                "/apps/cms/template/tempList.zul",
                                                cssListCen, mapTList);
                                tempWin.initWindow();
                                tempWin.addEventListener(Events.ON_CHANGE,
                                        new EventListener()
                                        {
                                            public void onEvent(Event event)
                                                    throws Exception
                                            {
                                                loadTree();
                                            }
                                        });
                                // 当选择分组名称节点。。。。
                            } else
                            {
                                String pa = path0 + "\\"
                                        + folderList[i].getName() + "\\"
                                        + "group.properties";
                                String node = null;
                                List list = null;
                                if ((new File(pa)).exists())
                                {
                                    list = GroupUtil.getGroupList(pa);
                                    for (int k = 0; k < list.size(); k++)
                                    {
                                        node = list.get(k).toString();
                                        if (item.getLabel().equals(node))
                                        { // 循环遍历group.properties文件的每行的第一个逗号前，=号后的字符，然后与你选择的树节点比较
                                            groupname = node;
                                            Map map = new HashMap();
                                            map.put("gname", groupname);
                                            map.put("fname", folderList[i]
                                                    .getName());
                                            cssListCen.getChildren().clear();
                                            final TempGroupListWindow groupWin = (TempGroupListWindow) Executions
                                                    .createComponents(
                                                            "/apps/cms/template/tempGroupList.zul",
                                                            cssListCen, map);
                                            groupWin.initWindow();
                                            groupWin.addEventListener(
                                                    Events.ON_CHANGE,
                                                    new EventListener()
                                                    {
                                                        public void onEvent(
                                                                Event arg0)
                                                                throws Exception
                                                        {
                                                            loadTree();
                                                        }
                                                    });
                                            break; // 选择的树节点和文件中名称相等 ，结束循环
                                        } else
                                        {
                                            continue; // 不相等， 继续下次循环
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (item.getTreechildren() != null)
                    {
                        return;
                    }
                }
            }
        });
        loadTree();
    }

    public void loadTree() throws IOException
    {
        TemptreeModel = new TempTreeModel(createTreeData());
        tempTree.setModel(TemptreeModel);
        openTree(tempTree.getTreechildren());
    }

    /**
     * <li>功能描述：读取CSS文件夹名称，以此命名树节点
     * 
     * @author bobo
     * @serialData 2010-7-21
     * @param null
     * @return SimpleTreeNode("ROOT",al)
     * @throws IOException
     */
    public SimpleTreeNode createTreeData() throws IOException
    {
        // 树节点集合
        File cssFolder = new File(path0);
        File[] folderList = cssFolder.listFiles();
        List fList = new ArrayList();
        List proList = new ArrayList();
        for (int i = 0; i < folderList.length; i++)
        {
            if (folderList[i].isDirectory())
            {
                String path = path0 + "\\" + folderList[i].getName() + "\\"
                        + "index.css";
                String resu = null;
                if (path != null)
                {
                    resu = FileUtil.getStyleName(path);
                } else
                {
                    resu = "";
                }

                fList.add(resu);
                proList.add(path0 + "\\" + folderList[i].getName() + "\\"
                        + "group.properties");
            }
        }
        final int size = fList.size();
        String[] name = (String[]) fList.toArray(new String[size]);

        tempnode = new SimpleTreeNode(new TempTree("模板管理"), createTreeChildren(
                name, proList)); // 树根
        ArrayList<SimpleTreeNode> al = new ArrayList<SimpleTreeNode>(); // 创建树根，孩子添加在该根上
        al.add(tempnode);
        return new SimpleTreeNode("ROOT", al);
    }

    public List<SimpleTreeNode> createTreeChildren(String[] name, List pathList)
            throws IOException
    {
        List<SimpleTreeNode> alc = new ArrayList<SimpleTreeNode>();
        for (int i = 0; i < name.length; i++)
        {
            TempTree tem = new TempTree(name[i]);
            String propath = (String) pathList.get(i);
            new File(propath);
            List<SimpleTreeNode> clist = new ArrayList<SimpleTreeNode>();
            /**
             * 读取group.properties文件中的 分组名称
             */
            List list = null;
            String node = null;
            if ((new File(propath)).exists())
            {
                list = GroupUtil.getGroupList(propath);
                for (int k = 0; k < list.size(); k++)
                {
                    node = list.get(k).toString();
                    clist.add(new SimpleTreeNode(new TempTree(node),
                            new ArrayList()));
                }
            }
            SimpleTreeNode stn = new SimpleTreeNode(tem, clist);
            alc.add(stn);
        }
        return alc;
    }

    /**
     * <li>功能描述：将树节点展开。
     * 
     * @serialData 2010-7-21
     * @param chi
     * @return null
     */
    public void openTree(Treechildren chi)
    {
        if (chi == null)
            return;
        List clist = chi.getChildren();
        for (int i = 0; i < clist.size(); i++)
        {
            Treeitem item = (Treeitem) clist.get(i);
            item.setOpen(true);
            openTree(item.getTreechildren());
        }
    }
    /**
     * <li>功能描述：设置cookie参数。
     * 
     * @param pname
     * @param fs
     *            void
     * @author Administrator
     */
    public void setProOfCookie(String pname, String fs)
    {
        Cookie cookie = new Cookie(pname, fs);
        cookie.setMaxAge(60 * 60 * 24 * 30);// store 30 days
        String cp = Executions.getCurrent().getContextPath();
        cookie.setPath(cp);
        ((HttpServletResponse) Executions.getCurrent().getNativeResponse())
                .addCookie(cookie);
    }

}
