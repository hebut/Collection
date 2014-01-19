package com.uniwin.webkey.system.parameters.template.win;

/**
 * <li>功能描述：点击“模板管理”树节点，对应的页面
 * 
 * @author bobo
 * @serialData 2010-7-21
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.cms.util.GroupUtil;
import com.uniwin.webkey.cms.util.HtmlParser;
import com.uniwin.webkey.cms.util.WebkeyControlUtil;
import com.uniwin.webkey.cms.util.XmlParser;
import com.uniwin.webkey.cms.util.ZipUtils;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.model.Users;

public class IndexTempListWindow extends Window implements AfterCompose
{

    private static final long serialVersionUID = 5114667906692605861L;

    // Listbox列表框组件
    private Listbox           templeListbox, cssListbox;

    // Checkbox选择组件
    private Checkbox          isCover;

    //Button按钮组件	
    private Toolbarbutton     importZip,addGroup;
    
    private Image             newTemple,delTemple,newCss, delCss;

    // 文本框 组件
    private Textbox           groupName;

    // 模板树 组件
    private Tree              tempTree;

    // 模板树Model
    private TempTreeModel     TemptreeModel;

    // 模板树节点
    private SimpleTreeNode    tempnode;

    // Panel组件
    public Panel              cssListPanel, tempListPanel;

    // 网站信息实体
    private WkTWebsite        website;

    //wkt文件路径
    String           path0   = WebkeyControlUtil.getPath("/admin/styles");

    // 登录用户的Session
    final Users      Users   = (Users) Sessions.getCurrent().getAttribute("users");

    // 最后修改日期显示样式
    final DateFormat nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Map              map     = new HashMap();

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        try
        {
            initWindow();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        delCss.addEventListener(Events.ON_CLICK, new EventListener()
        {
            public void onEvent(Event event) throws Exception
            {
                if (cssListbox.getSelectedItem() == null)
                {
                    Messagebox.show("请您选择要删除的记录！", "提示", Messagebox.OK,
                            Messagebox.INFORMATION);
                    return;
                } else
                {
                    Set sel = cssListbox.getSelectedItems();
                    Iterator it = sel.iterator(); // 用于多选删除提示
                    if (Messagebox.show("确定要删除此记录吗？", "提示", Messagebox.YES
                            | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES)
                    {
                        while (it.hasNext())
                        {
                            Listitem item = (Listitem) it.next();
                            File file = (File) item.getValue();
                            String fn = file.getName();
                            delFolderAndFile(fn, "admin" + "\\" + "styles"); // 实现文件及下述文件夹的删除.查找对应路径下，是否存在同名fn的文件夹，有则删除
                            delFolderAndFile(fn, "styles");
                            delFolderAndFile(fn, "WEB-INF" + "\\" + "templet");
                            delFolderAndFile(fn, "htmlFile"); // 删除htmlFiles下的相应文件夹的Html页面

                            String pa = WebkeyControlUtil.getPath("/");
                            File zipFile = new File(pa + "\\" + "admin" + "\\"
                                    + "styles"); // 同时删除对应的.zip
                            File[] zipFileList = zipFile.listFiles();
                            if (zipFileList != null && zipFileList.length > 0)
                            {
                                for (int i = 0; i < zipFileList.length; i++)
                                {
                                    if (zipFileList[i].exists()
                                            && zipFileList[i].isFile())
                                    {
                                        String zf = zipFileList[i].getName();
                                        if (zf.equals(fn + ".zip"))
                                        {
                                            zipFileList[i].delete();
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                        initWindow();
                        loadTree();
                        Messagebox.show("删除成功！", "提示", Messagebox.OK,
                                Messagebox.INFORMATION);
                    }
                }

            }
        });

        delTemple.addEventListener(Events.ON_CLICK, new EventListener()
        {
            public void onEvent(Event event) throws Exception
            {
                if (templeListbox.getSelectedItem() == null)
                {
                    Messagebox.show("请您选择要删除的记录！", "提示", Messagebox.OK,
                            Messagebox.INFORMATION);
                    return;
                } else
                {
                    Set sel = templeListbox.getSelectedItems();
                    Iterator it = sel.iterator(); // 用于多选删除提示
                    if (Messagebox.show("确定要删除此记录吗？", "提示", Messagebox.YES
                            | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES)
                    {
                        while (it.hasNext())
                        {
                            Listitem item = (Listitem) it.next();
                            File file = (File) item.getValue();
                            String wktFilename = file.getName();

                            String folder = (String) Sessions.getCurrent()
                                    .getAttribute("filename");
                            Integer endIndex = wktFilename.lastIndexOf(".");
                            String filename = wktFilename
                                    .substring(0, endIndex); // 得到文件名称，不含后缀名

                            delFile("admin" + "\\" + "styles", folder, filename
                                    + ".wkt"); // 实现文件及下述文件夹的删除.查找对应路径下，是否存在同名fn的文件夹，有则删除
                            delFile("styles", folder, filename + ".ftl");
                            delFile("WEB-INF" + "\\" + "templet", folder,
                                    filename + ".xml");

                            initWindow();
                            loadTree();
                            Messagebox.show("删除成功！", "提示", Messagebox.OK,
                                    Messagebox.INFORMATION);

                        }
                    }
                }
            }
        });

    }

    /**
     * <li>功能描述：查找对应路径下，是否存在发布后的同名fn的文件夹及其下面的文件，存在则删除
     * 
     * @author bobo
     * @param fileName
     *            文件名
     * @param path
     *            路径
     * @2010-7-27
     */
    public void delFolderAndFile(String fileName, String path)
    {
        String cssPath = WebkeyControlUtil.getPath("/") + "\\" + path;
        File cssFolder = new File(cssPath);
        File[] folderList = cssFolder.listFiles();
        if (folderList != null && folderList.length > 0)
        {
            for (int i = 0; i < folderList.length; i++)
            {
                if (folderList[i].exists() && folderList[i].isDirectory())
                {
                    String fn = folderList[i].getName();
                    if (fn.equals(fileName))
                    {
                        String cssPath1 = WebkeyControlUtil.getPath(path)
                                + "\\" + fileName;
                        FileUtil.delAllFile(cssPath1);
                        folderList[i].delete();
                        break;
                    }
                }
            }
        } else
        {
            return;
        }
    }

    /**
     * <li>功能描述：查找对应路径下，是否存在发布后的同名fileName的文件，存在则删除
     * 
     * @author bobo
     * @param fellowpath
     *            追加的路径
     * @param folderName
     *            文件夹名称
     * @param filename
     * @2010-7-27
     */
    public void delFile(String fellowpath, String folderName, String filename)
    {
        String filepath = WebkeyControlUtil.getPath("/");
        File ftlFolder = new File(filepath + "\\" + fellowpath);
        File[] ftlFolderList = ftlFolder.listFiles();
        if (ftlFolderList != null && ftlFolderList.length > 0)
        {
            for (int i = 0; i < ftlFolderList.length; i++)
            {
                if (ftlFolderList[i].exists() && ftlFolderList[i].isDirectory())
                {
                    String ftlname = ftlFolderList[i].getName();
                    if (ftlname.equals(folderName))
                    {
                        File ftlFile = new File(filepath + fellowpath + "\\"
                                + folderName);
                        File[] ftlFileList = ftlFile.listFiles();
                        if (ftlFileList != null && ftlFileList.length > 0)
                        {
                            for (int j = 0; j < ftlFileList.length; j++)
                            {
                                if (ftlFileList[j].exists()
                                        && ftlFileList[j].isFile())
                                {
                                    String ftl = ftlFileList[j].getName();
                                    if (ftl.equals(filename))
                                    {
                                        ftlFileList[j].delete();
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else
        {
            return;
        }
    }

    public void initWindow() throws InterruptedException, IOException
    {
        final DateFormat albumTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cssListbox.setItemRenderer(new ListitemRenderer()
        {
            public void render(Listitem item, Object data) throws Exception
            {
                File file = (File) data;
                item.setValue(file);
                item.setHeight("25px");
                Listcell c = new Listcell("");
                Listcell c0 = new Listcell(item.getIndex() + 1 + "");
                Listcell c1 = new Listcell(file.getName());
                c1.setTooltiptext(file.getName());

                String path = path0 + "\\" + file.getName() + "\\"
                        + "index.css"; // 获取.css文件路径，得到其中的css文件名称
                Listcell c2;
                if (path != null)
                {
                    String resu = FileUtil.getStyleName(path);
                    c2 = new Listcell(resu);
                    c2.setTooltiptext(resu);
                } else
                {
                    c2 = new Listcell("");
                    c2.setTooltiptext("");
                }

                Listcell c3 = new Listcell(albumTime
                        .format(file.lastModified()));
                c3.setTooltiptext(albumTime.format(file.lastModified()));
                Listcell c4 = new Listcell("编辑CSS");
                c4.setTooltiptext("编辑CSS");
                Listcell c5 = new Listcell("导出");
                c5.setTooltiptext("导出");
                Listcell c6 = new Listcell("发布");
                c6.setTooltiptext("发布");
                c4.setStyle("color:#484891");
                c5.setStyle("color:#484891");
                c6.setStyle("color:#484891");

                c1.addEventListener(Events.ON_CLICK, new InnerListener());
                c2.addEventListener(Events.ON_CLICK, new InnerListener());
                c4.addEventListener(Events.ON_CLICK, new EditListener());
                c5.addEventListener(Events.ON_CLICK, new ExportListener());
                c6.addEventListener(Events.ON_CLICK, new ReleaseListener());

                item.appendChild(c);
                item.appendChild(c0);
                item.appendChild(c1);
                item.appendChild(c2);
                item.appendChild(c3);
                item.appendChild(c4);
                item.appendChild(c5);
                item.appendChild(c6);
            }
        });

        File cssFolder = new File(path0);
        File[] folderList = cssFolder.listFiles();
        List fList = new ArrayList();
        for (int i = 0; i < folderList.length; i++)
        {
            if (folderList[i].isDirectory())
            {
                fList.add(folderList[i]);
            }
        }
        ListModelList fListModel = new ListModelList(fList);
        cssListbox.setModel(fListModel);
        reloadListbox();
    }

    /**
     * <li>功能描述：点击列，转移查看页面
     * 
     * @author bobo
     * @2010-4-1
     */
    public class InnerListener implements EventListener
    {
        public void onEvent(Event event) throws Exception
        {
            Listitem c = (Listitem) event.getTarget().getParent();
            File file = (File) c.getValue();
            Sessions.getCurrent().setAttribute("filename", file.getName());

            String path = Executions.getCurrent().getContextPath().substring(1);
            WebkeyControlUtil.setProOfCookie("webkeyRoot", path);
            WebkeyControlUtil.setProOfCookie("webkeyCss", file.getName());

            cssListPanel.setVisible(false);
            tempListPanel.setVisible(true);
            intTempListWindow();
        }
    }

    /**
     * <li>功能描述：点击列，转移css编辑页面
     * 
     * @author bobo
     * @2010-7-27
     */
    public class EditListener implements EventListener
    {
        public void onEvent(Event event) throws Exception
        {
            Listitem item = (Listitem) event.getTarget().getParent();
            File cssFile = (File) item.getValue();
            String cssFilename = cssFile.getName();
            final NewCssWindow win = (NewCssWindow) Executions
                    .createComponents(
                            "/apps/cms/template/newCSS.zul",
                            null, null);
            win.doModal();
            win.setClosable(true);
            win.setCssFilename(cssFilename);
            win.afterCompose();
            win.addEventListener(Events.ON_CHANGE, new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    initWindow();
                    win.detach();
                }
            });
        }
    }

    /**
     * <li>功能描述：点击列，实现导出并下载的功能
     * 
     * @author bobo
     * @2010-7-27
     */
    public class ExportListener implements EventListener
    {
        public void onEvent(Event event) throws Exception
        {
            Listitem item = (Listitem) event.getTarget().getParent();
            File cssFile = (File) item.getValue();
            String cssFilename = cssFile.getName();
            String path = path0 + "\\" + cssFilename;
            ZipUtils.zip(path);
            File zipFile = new File(path0 + "\\" + cssFilename + ".zip");
            Filedownload.save(zipFile, null);
        }
    }

    /**
     * <li>功能描述：点击列，实现模板批量发布功能
     * 
     * @author bobo
     * @2010-7-28
     */
    public class ReleaseListener implements EventListener
    {
        public void onEvent(Event event) throws Exception
        {
            Listitem item = (Listitem) event.getTarget().getParent();
            File cssFile = (File) item.getValue();
            Sessions.getCurrent().setAttribute("filename", cssFile.getName());
            String cssFilename = cssFile.getName();
            String baseDir0 = WebkeyControlUtil.getPath("/styles");
            FileUtil.mkDir(baseDir0, cssFilename);
            String baseDir1 = WebkeyControlUtil.getPath("/WEB-INF/templet"); // 在templet下建立文件
            FileUtil.mkDir(baseDir1, cssFilename);

            String path1 = path0 + "\\" + cssFilename; // 添加image文件夹及其内容的创建
            if (path1 + "\\" + "index.css" != null)
            {
                String cssContent = FileUtil.read(path1 + "\\" + "index.css"); // 先读取/admin/styles/index.css，然后写入到/styles/index.css中
                FileUtil.writerText(baseDir0 + "\\" + cssFilename + "\\" + "index.css", cssContent);
            }

            File cssFolder = new File(path1);
            File[] folderList = cssFolder.listFiles();
            List fList = new ArrayList();
            if (folderList.length > 0)
            {
                for (int i = 0; i < folderList.length; i++)
                {
                    if (folderList[i].isFile())
                    {
                        String fileName = folderList[i].getName();
                        if (fileName.indexOf(".wkt") > 0)
                        {
                            fList.add(folderList[i]);
                        }
                    }
                }
            }
            if (fList.size() != 0)
            {
                boolean bOK = true;
                for (int i = 0; i < fList.size(); i++)
                {
                    File wktFile = (File) fList.get(i);
                    String content = FileUtil.read(wktFile.getAbsolutePath());
                    Integer endIndex = wktFile.getName().indexOf(".wkt");
                    String wktFilename = wktFile.getName().substring(0,endIndex);
                    if (content.contains("wktid=\"wkt_docdtl\""))
                    {
                        bOK = true;
                    } else
                    {
                        HtmlParser hp = new HtmlParser(wktFile.getPath(), baseDir0 + "\\" + cssFilename + "\\"
                                        + wktFilename + ".ftl", path0 + "\\" + "head.txt", wktFilename,cssFilename);
                        XmlParser xp = new XmlParser(baseDir1  + cssFilename + "/" + wktFilename + ".xml", hp.getData());
                        bOK = true;

                        // **************************下面用于生成对应的html页面*************************
//                        WKT_User user = new WKT_User();
//                        user.loadData();
//
//                        Map root = (Map) Sessions.getCurrent().getAttribute("root");
//                        root.put("user", user);
//                        root.put("site", website);
//                        // ftl存放位置，比如:/.../styles/，其下包含.ftl文件夹和要引入的.ftl
//                        String templatePath = WebkeyControlUtil.getPath("/styles"); 
//                        String templateFileName = cssFilename + "\\" + wktFilename + ".ftl"; // ftl文件名称
//                        String htmlFileName = wktFilename + ".html"; // 生成的htm文件名称
//                        WkTWebsite website = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");
//                        String curPath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/"); // 当前项目根目录
//                        String htmlFilePath = curPath + "SiteHtml";
//                        htmlFilePath = createSiteDir(website, htmlFilePath,cssFilename);
//
//                        FreeMarkerUtil freetest = new FreeMarkerUtil();
//                        bOK = freetest.geneHtmlFile(templatePath,templateFileName, root, htmlFilePath,htmlFileName);
//                        root.clear();
                    }
                    if (!bOK)
                    {
                        bOK = false;
                        Messagebox.show("html生成失败！", "错误", Messagebox.OK,Messagebox.ERROR);
                        break;
                    }
                }
                if (bOK)
                {
                    Messagebox.show("保存并发布成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
                } else
                {
                    Messagebox.show("html生成失败！", "错误", Messagebox.OK, Messagebox.ERROR);
                }
            }

            FileUtil.mkDir(baseDir0 + "\\" + cssFilename, "Image"); // 发布模板images下的所有文件
            FileUtil.pasteFile(path1 + "\\" + "Image", baseDir0 + "\\" + cssFilename + "\\" + "Image");
        }
    }

    /**
     * <li>功能描述：由当前站点创建文件夹，并返回最终存放位置。
     * 
     * @param site
     * @param htmlFilePath
     * @author bobo
     */
    public String createSiteDir(WkTWebsite site, String htmlFilePath, String cf)
    {
        htmlFilePath = htmlFilePath.trim() + "\\" + site.getKwId();
        File siteDir2 = new File(htmlFilePath.trim());
        if (!(siteDir2.exists()))
        {
            siteDir2.mkdir();
        }
//        htmlFilePath = htmlFilePath + "\\" + site.getKwLocation().trim();
//        File siteDir3 = new File(htmlFilePath.trim());
//        if (!(siteDir3.exists()))
//        {
//            siteDir3.mkdir();
//        }
//        htmlFilePath = htmlFilePath + "\\" + cf;
//        File siteDir4 = new File(htmlFilePath.trim());
//        if (!(siteDir4.exists()))
//        {
//            siteDir4.mkdir();
//        }

        return htmlFilePath;
    }

    /**
     * <li>功能描述：初始化TempListWindow窗口
     * 
     * @throws InterruptedException
     * @throws IOException
     * @2010-7-8
     */
    public void intTempListWindow() throws InterruptedException, IOException
    {
        final String cf = (String) Sessions.getCurrent().getAttribute("filename");
        templeListbox.setItemRenderer(new ListitemRenderer()
        {
            public void render(Listitem item, Object data) throws Exception
            {
                File file = (File) data;
                item.setValue(file);
                item.setHeight("25px");
                Listcell c = new Listcell("");
                Listcell c0 = new Listcell(item.getIndex() + 1 + "");
                int dotPos = file.getName().lastIndexOf('.');
                Listcell c1 = new Listcell(file.getName().substring(0, dotPos));
                c1.setTooltiptext(file.getName().substring(0, dotPos));

                String path = WebkeyControlUtil.getPath("/admin/styles") + "\\"
                        + cf + "\\" + file.getName(); // 获取.css文件路径，得到其中的css文件名称（目前，是写死的路径，以后需要该换掉）
                String resu = FileUtil.getWktDesName(path);
                Listcell c2 = new Listcell(resu);
                c2.setTooltiptext(resu);
                Listcell c3 = new Listcell(Users.getLoginName());
                c3.setTooltiptext(Users.getLoginName());
                Listcell c4 = new Listcell(nowTime.format(file.lastModified()));
                c4.setTooltiptext(nowTime.format(file.lastModified()));

                c1.addEventListener(Events.ON_CLICK, new pointListener());
                c2.addEventListener(Events.ON_CLICK, new pointListener());

                item.appendChild(c);
                item.appendChild(c0);
                item.appendChild(c1);
                item.appendChild(c2);
                item.appendChild(c3);
                item.appendChild(c4);
            }

        });
        reloadListbox();
    }

    /**
     * <li>功能描述：给TempListWindow中的Listbox加载数据
     * 
     * @author bobo
     * @throws InterruptedException
     * @throws IOException
     * @serialData 2010-7-23
     */
    public void reloadListbox() throws InterruptedException, IOException
    {
        final String fN = (String) Sessions.getCurrent().getAttribute("filename");
        String path0 = WebkeyControlUtil.getPath("/admin/styles") + "\\" + fN;
        String pa1 = WebkeyControlUtil.getPath("/admin/styles") + "\\" + fN+ "\\" + "group.properties";
        File cssFolder = new File(path0);
        File[] folderList = cssFolder.listFiles();
        List fList = new ArrayList();
        List gList = new ArrayList();
        File groupf = new File(pa1);
        if (groupf.exists())
        {
            if (folderList != null && folderList.length > 0)
            {
                for (int i = 0; i < folderList.length; i++)
                { // 读取/admin/styles/fName下的wkt文件
                    String fileName = folderList[i].getName();
                    if (fileName.indexOf(".wkt") > 0)
                    {
                        fList.add(folderList[i]);
                    }// if
                }// for
            }
            gList = GroupUtil.getGroupName(pa1); // 读取group.properties下的分组模板名称
            if (gList.size() > 0)
            {
                for (int j = 0; j < gList.size(); j++)
                {
                    for (int k = 0; k < fList.size(); k++)
                    {
                        if ((gList.get(j) + ".wkt")
                                .equals(((File) fList.get(k)).getName()))
                            fList.remove(k); // 删除相同模板名称
                    }
                }
            }
            ListModelList fileNameListModel = new ListModelList(fList);
            templeListbox.setModel(fileNameListModel);
        } else
        {
            ListModelList fileNameListModel = new ListModelList(fList);
            templeListbox.setModel(fileNameListModel);
        }
    }

    /**
     * <li>功能描述：监听事件，转移到模板编辑窗口
     * 
     * @author bobo
     * @serialData 2010-7-23
     */
    class pointListener implements EventListener
    {
        public void onEvent(Event event) throws Exception
        {
            final String cf = (String) Sessions.getCurrent().getAttribute("filename");
            Listitem c = (Listitem) event.getTarget().getParent();
            File file = (File) c.getValue();
            Executions.getCurrent().setAttribute("TempleListfileName",
                    file.getName().subSequence(0, file.getName().lastIndexOf('.'))); // 得到.wkt文件文件名（不包含扩展名）

            String path = WebkeyControlUtil.getPath("/admin/styles") + cf
                    + "\\" + file.getName(); // 获取.css文件路径，得到其中的css文件名称（目前，是写死的路径，以后需要该换掉）
            String resu = FileUtil.getWktDesName(path);
            Executions.getCurrent().setAttribute("tempDesc", resu); // 模板描述信 息

            final IndexTempEditWindow win = (IndexTempEditWindow) Executions
                    .createComponents("/apps/cms/template/indexTempEdit.zul", null, null);
            win.doModal();
            win.addEventListener(Events.ON_CHANGE, new EventListener()
            {
                public void onEvent(Event arg0) throws Exception
                {
                    reloadListbox();
                    win.detach();
                }
            });
        }
    }

    /**
     * <li>功能描述：导入风格的压缩文件，并解压到固定的路径
     * 
     * @author bobo
     * @throws InterruptedException
     * @throws IOException
     * @2010-7-30
     */
    public void onClick$importZip() throws InterruptedException, IOException
    {
        Media media = Fileupload.get();
        Desktop desktop = this.getDesktop();
        Configuration conf = desktop.getWebApp().getConfiguration();
        conf.setMaxUploadSize(1024 * 1024 * 100); // 最大100M
        conf.setUploadCharset("UTF-8");
        if (media == null)
        {
            Messagebox.show("您未指定导入的文件！", "提示", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }
        if (!media.getFormat().equals(("zip")))
        {
            Messagebox.show("只能导入ZIP格式的风格压缩文件！", "错误", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        String fileName = media.getName();
        InputStream mediaInput = media.getStreamData();
        if (path0 == null)
        {
            Messagebox.show("存储路径不存在！", "错误", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        FileOutputStream out = new FileOutputStream(path0 + "\\" + fileName);
        DataOutputStream mediaOutput = new DataOutputStream(out);
        Files.copy(mediaOutput, mediaInput);
        if (out != null)
        {
            out.close();
        }
        if (!isCover.isChecked()
                && ZipUtils.folderIsExists(path0 + "\\" + fileName))
        {
            Messagebox.show("导入的样式文件已存在！", "错误", Messagebox.OK,
                    Messagebox.ERROR);
            return;
        }
        ZipUtils.unZip(path0 + "\\" + fileName);
        initWindow();
    }

    /**
     * <li>功能描述：添加风格文件，显示风格列表
     * 
     * @author bobo
     * @serialData 2010-7-21
     * @param null
     * @return null
     * @throws IOException
     * @throws InterruptedException 
     * @throws SuspendNotAllowedException 
     */
    public void onClick$newCss() throws IOException, SuspendNotAllowedException, InterruptedException
    {
        final NewCssWindow win = (NewCssWindow) Executions.createComponents(
                "/apps/cms/template/newCSS.zul", null, null);
        win.doModal();
        win.addEventListener(Events.ON_CHANGE, new EventListener()
        {
            public void onEvent(Event arg0) throws Exception
            {
                initWindow();
                loadTree();
                win.detach();
            }
        });
        Events.postEvent(Events.ON_CHANGE, this, null);
    }

    /**
     * <li>功能描述：新建模板，转移到模板编辑窗口
     * 
     * @author bobo
     * @throws InterruptedException 
     * @serialData 2010-7-23
     */
    public void onClick$newTemple() throws InterruptedException
    {
        Tabbox tabbox = (Tabbox) Sessions.getCurrent().getAttribute("centerTabbox");
		
		if(!tabbox.getTabs().hasFellow(String.valueOf("indexTempnewTab"))){
			final Tab newTab=new Tab();		
			newTab.setId(String.valueOf("indexTempnewTab"));
			newTab.setLabel("添加模板");	
			Tabpanel newtabpanel=new Tabpanel();
			newtabpanel.setId(String.valueOf("indexTempnewtabpanel"));
			tabbox.getTabs().getChildren().add(newTab);
			tabbox.getTabpanels().getChildren().add(newtabpanel);
			
			Hbox h=new Hbox();	
			h.setId(String.valueOf("indexTemph"));				
			h.setAlign("left");
			h.setPack("center");
			h.setWidth("100%");
			newtabpanel.appendChild(h);
			newTab.setClosable(true);
			newTab.setSelected(true);
			
			String filename = (String)Sessions.getCurrent().getAttribute("filename");				
		    Sessions.getCurrent().setAttribute("filename", filename);			    
		    Map map = new HashMap();
			map.put("indexTempNewTab", newTab);			    
			final IndexTempEditWindow  win = (IndexTempEditWindow)Executions.createComponents("/apps/cms/template/indexTempEdit.zul",null,map);
			win.setParent(newtabpanel);
			win.setPosition("center,center");
			win.addEventListener(Events.ON_CHANGE, new EventListener(){
				public void onEvent(Event arg0) throws Exception {
					initWindow();
					newTab.onClose();
					win.detach();
				 }    		 
    	    }); 
		    }else{
				Messagebox.show("该添加模板标签已经打开！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		}
		
        Events.postEvent(Events.ON_CHANGE, this, null);
    }

    /**
     * <li>功能描述：增加分组，若存在该分组提示更换名称
     * 
     * @author bobo
     * @serialData 2010-8-23
     */
    public void onClick$addGroup() throws IOException, InterruptedException
    {
        final String fName = (String) Sessions.getCurrent().getAttribute( "filename");
        String pa1 = WebkeyControlUtil.getPath("/admin/styles") + "\\" + fName + "\\" + "group.properties";
        List gList = new ArrayList();
        File file = new File(pa1);
        if (file.exists())
        {
            gList = GroupUtil.getGroupList(pa1); // 读取group.properties下的分组模板名称
            boolean flag = true;
            for (int j = 0; j < gList.size(); j++)
            {
                if ((gList.get(j)).equals(groupName.getValue()))
                {
                    Messagebox.show("该分组已经存在，请您更换分组名称！", "提示", Messagebox.OK,Messagebox.INFORMATION);
                    flag = false;
                }
            }
            if (flag)
            {
                if (groupName.getValue().trim() == "" || groupName.getValue().trim().equals(null))
                {
                    Messagebox.show("分组名称不能为空！", "提示", Messagebox.OK,Messagebox.INFORMATION);
                    return;
                } else
                {
                    GroupUtil.addGroup(pa1, groupName.getValue(), groupName.getValue()); // 增加模板分组
                }

                loadTree(); // 实现树的同步加载
                Messagebox.show("添加分组成功！", "提示", Messagebox.OK,
                        Messagebox.INFORMATION);
            }
        } else
        {
            if (groupName.getValue().trim() == ""
                    || groupName.getValue().trim().equals(null))
            {
                Messagebox.show("分组名称不能为空！", "提示", Messagebox.OK,
                        Messagebox.INFORMATION);
                return;
            } else
            {
                GroupUtil.addGroup(pa1, groupName.getValue(), groupName.getValue()); // 增加模板分组
            }

            loadTree(); // 实现树的同步加载
            Messagebox.show("添加分组成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
        }
    }

    /**
     * <li>功能描述：加载模板树
     * 
     * @author bobo
     * @serialData 2010-7-21
     * @param null
     * @throws IOException
     */
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
                String path = path0 + "\\" + folderList[i].getName() + "\\"+ "index.css";
                String resu = null;
                if (path != null)
                {
                    resu = FileUtil.getStyleName(path);
                } else
                {
                    resu = "";
                }
                fList.add(resu);
                proList.add(path0 + "\\" + folderList[i].getName() + "\\" + "group.properties");
            }
        }
        final int size = fList.size();
        String[] name = (String[]) fList.toArray(new String[size]);

        tempnode = new SimpleTreeNode(new TempTree("模板管理"), createTreeChildren( name, proList)); // 树根
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
            File pfile = new File(propath);
            List<SimpleTreeNode> clist = new ArrayList<SimpleTreeNode>();

            List list = null; // 读取group.properties文件中的 分组名称
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
     * <li>功能描述：获取cookie参数。
     * 
     * @param pname
     * @return String
     * @author Administrator
     */
    public String getProOfCookie(String pname)
    {
        Cookie[] cookies = ((HttpServletRequest) Executions.getCurrent()
                .getNativeRequest()).getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (pname.equals(cookies[i].getName()))
                {
                    String fs = cookies[i].getValue();
                    return fs;
                }
            }
        }
        return "";
    }
}
