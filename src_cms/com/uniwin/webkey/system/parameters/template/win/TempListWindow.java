package com.uniwin.webkey.system.parameters.template.win;

/**
 * <li>功能描述：模板列表页面 对应页面admin/system/parameters/TemplateManage/tempList.zul
 * @author bobo
 * @serialData 2010-7-21
 */
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.cms.util.GroupUtil;
import com.uniwin.webkey.cms.util.WebkeyControlUtil;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.model.Users;

public class TempListWindow extends Window implements AfterCompose
{
    private static final long serialVersionUID = 5114667906692605861L;

    // Listbox列表框组件
    private Listbox           templeListbox;

    private Image             delTemple;

    // 文本框 组件
    private Textbox           groupName;

    // 由TempTreeComposer类传递得到模板文件夹session
    String fName = (String) Executions.getCurrent().getArg().get("foldername");

    // wkt文件路径
    String treepath = WebkeyControlUtil.getPath("/admin/styles");

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        delTemple.addEventListener(Events.ON_CLICK, new EventListener()
        {
            public void onEvent(Event event) throws Exception
            {
                if (templeListbox.getSelectedItem() == null)
                {
                    Messagebox.show("请您选择要删除的记录！", "提示", Messagebox.OK, Messagebox.INFORMATION);
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
                            String folder = (String) Sessions.getCurrent().getAttribute("filename");
                            Integer endIndex = wktFilename.lastIndexOf(".");
                            String filename = wktFilename.substring(0, endIndex); // 得到文件名称，不含后缀名
                            delFile("admin" + "\\" + "styles", folder, filename
                                    + ".wkt"); // 实现文件及下述文件夹的删除.查找对应路径下，是否存在同名fn的文件夹，有则删除
                            delFile("styles", folder, filename + ".ftl");
                            delFile("WEB-INF" + "\\" + "templet", folder,
                                    filename + ".xml");
                        }
                        initWindow();
                        Messagebox.show("删除成功！", "提示", Messagebox.OK,Messagebox.INFORMATION);
                        // Events.postEvent(Events.ON_CHANGE,this, null);
                    }
                }
            }
        });

    }

    /**
     * <li>功能描述：初始化窗口
     * 
     * @author bobo
     * @2010-8-27
     */
    public void initWindow() throws IOException
    {
        final DateFormat nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Users Users = (Users) Sessions.getCurrent().getAttribute("users");
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
                String path = WebkeyControlUtil.getPath("/admin/styles")
                        + fName + "\\" + file.getName(); // 获取.css文件路径，得到其中的css文件名称（目前，是写死的路径，以后需要该换掉）
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
        try
        {
            reloadListbox();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
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
        File ftlFolder = new File(filepath + fellowpath);
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

    /**
     * <li>功能描述：给Listbox加载数据，比较group.properties文件中每一分组中后模板名称 和
     * ”/admin/styles/fName“文件夹下面wkt文件，相同则删除，不相同添加到fList，加载数据
     * 
     * @author bobo
     * @throws InterruptedException
     * @throws IOException
     * @serialData 2010-7-23
     */
    public void reloadListbox() throws InterruptedException, IOException
    {
        String path0 = WebkeyControlUtil.getPath("/admin/styles") + "\\"
                + fName;
        String pa1 = WebkeyControlUtil.getPath("/admin/styles") + "\\" + fName
                + "\\" + "group.properties";
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
                    }
                }// if
            }// for
            gList = GroupUtil.getGroupName(pa1); // 读取group.properties下的分组模板名称
            for (int j = 0; j < gList.size(); j++)
            {
                for (int k = 0; k < fList.size(); k++)
                {
                    if ((gList.get(j) + ".wkt").equals(((File) fList.get(k)).getName()))
                        fList.remove(k); // 删除相同模板名称
                }
            }
            ListModelList fileNameListModel = new ListModelList(fList);
            templeListbox.setModel(fileNameListModel);
        } else
        {
            // return;
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
            Listitem c = (Listitem) event.getTarget().getParent();
            File file = (File) c.getValue();
            String path = WebkeyControlUtil.getPath("/admin/styles") + fName
                    + "\\" + file.getName(); // 获取.css文件路径，得到其中的css文件名称（目前，是写死的路径，以后需要该换掉）
            String resu = FileUtil.getWktDesName(path);
            Map mapList = new HashMap();
            mapList.put("TempListFn", file.getName().subSequence(0,file.getName().lastIndexOf('.')));
            mapList.put("tempDesc", resu); // 模板描述信息
            mapList.put("foldName", fName);
            final TempListEditWindow win = (TempListEditWindow) Executions
                    .createComponents("/apps/cms/template/tempListEdit.zul",null, mapList);
            win.doModal();
            win.initWindow();
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
     * <li>功能描述：新建模板，转移到模板编辑窗口
     * 
     * @author bobo
     * @throws InterruptedException 
     * @serialData 2010-7-23
     */
    public void onClick$newTemple() throws InterruptedException
    {
        Tabbox tabbox = (Tabbox) Sessions.getCurrent().getAttribute("centerTabbox");
		Tabs   tabs   =(Tabs)Sessions.getCurrent().getAttribute("tabs");		
		if(!tabbox.getTabs().hasFellow(String.valueOf("tempnewTab"))){
			final Tab newTab=new Tab();				
			newTab.setId(String.valueOf("tempnewTab"));
			newTab.setLabel("添加模板");				
			Tabpanel newtabpanel=new Tabpanel();
			newtabpanel.setId(String.valueOf("tempnewtabpanel"));
			tabbox.getTabs().getChildren().add(newTab);
			tabbox.getTabpanels().getChildren().add(newtabpanel);
			
			Hbox h=new Hbox();	
			h.setId(String.valueOf("temph"));				
			h.setAlign("left");
			h.setPack("center");
			h.setWidth("100%");
			newtabpanel.appendChild(h);
			newTab.setClosable(true);
			newTab.setSelected(true);
			
			String filename = (String)Sessions.getCurrent().getAttribute("filename");				
			Map map = new HashMap();
			map.put("foldName", filename);
			map.put("newTab", newTab);
			final TempListEditWindow  win = (TempListEditWindow)Executions.createComponents("/apps/cms/template/tempListEdit.zul",h,map);
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
     * <li>功能描述：增加分组，若模板树的子节点中存在该分组名称，提示更换名称
     * 
     * @author bobo
     * @serialData 2010-8-23
     */
    public void onClick$addGroup() throws IOException, InterruptedException
    {
        String cssPath = WebkeyControlUtil.getPath("/admin/styles");
        File cssFolder = new File(cssPath);
        File[] folderList = cssFolder.listFiles();
        List groupNameList = new ArrayList();
        List gList = new ArrayList();
        if (folderList != null && folderList.length > 0)
        {
            for (int i = 0; i < folderList.length; i++)
            {
                if (folderList[i].isDirectory())
                {
                    folderList[i].getName();
                    String tempPath = cssPath + "\\" + folderList[i].getName();
                    File tempFolder = new File(tempPath);
                    File[] tempList = tempFolder.listFiles();
                    if (tempList != null && tempList.length > 0)
                    {
                        for (int j = 0; j < tempList.length; j++)
                        {
                            if (tempList[j].getName().indexOf(".properties") > 0)
                            {
                                String pa2 = cssPath + "\\"
                                        + folderList[i].getName() + "\\"
                                        + tempList[j].getName();
                                File file = new File(pa2);
                                if (file.exists())
                                {
                                    gList = GroupUtil.getGroupList(pa2);
                                }
                                break;
                            }
                        }
                    }
                    if (gList.size() > 0)
                    {
                        for (int k = 0; k < gList.size(); k++)
                        {
                            groupNameList.add(gList.get(k));
                        }
                    }
                }
            }

            boolean flag = true;
            for (int l = 0; l < groupNameList.size(); l++)
            {
                if ((groupNameList.get(l)).equals(groupName.getValue().trim()))
                {
                    Messagebox.show("该分组已经存在，请您更换分组名称！", "提示", Messagebox.OK,
                            Messagebox.INFORMATION);
                    groupName.setValue("");
                    flag = false;
                }
            }
            if (flag)
            {
                String pa1 = cssPath + "\\" + fName + "\\" + "group.properties";
                if (groupName.getValue().trim() == ""
                        || groupName.getValue().trim().equals(null))
                {
                    Messagebox.show("分组名称不能为空！", "提示", Messagebox.OK,
                            Messagebox.INFORMATION);
                    return;
                } else
                {
                    GroupUtil.addGroup(pa1, groupName.getValue(), groupName
                            .getValue()); // 增加模板分组
                }

                Messagebox.show("添加分组成功！", "提示", Messagebox.OK,
                        Messagebox.INFORMATION);

            }
        }
        Events.postEvent(Events.ON_CHANGE, this, null);
    }

    /**
     * <li>功能描述：返回功能
     * 
     * @author bobo
     * @2010-7-27
     */
    public void onClick$reBack()
    {

    }
}
