package com.uniwin.webkey.system.parameters.template.win;

/**
 * <li>模板编辑,对应页面admin/persongal/system/parameters/TemplateManage/templaeEdit.zul
 * @author bobo
 * 2010-06-29
 */
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkforge.fckez.FCKeditor;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.cms.util.CharSet;
import com.uniwin.webkey.cms.util.FileUtil;
import com.uniwin.webkey.cms.util.GroupUtil;
import com.uniwin.webkey.cms.util.HtmlParser;
import com.uniwin.webkey.cms.util.WebkeyControlUtil;
import com.uniwin.webkey.cms.util.XmlParser;
import com.uniwin.webkey.core.model.Users;

public class TempListEditWindow extends Window implements AfterCompose
{
	private static final long serialVersionUID = 7320032204960680939L;

	// FCK编辑器
    private FCKeditor mlContent;

    // 文本框 组件
    private Textbox   tempName, tempDesc, csscontent;

    // Listbox列表框组件
    private Listbox   groupListbox;

    //登录用户的Session
    Users             Users       = (Users) Sessions.getCurrent().getAttribute("users");

    // 由TempListWindow类传递得到.wkt文件的文件名称（不包含.wkt扩展名）
    String            wktFilename = (String) Executions.getCurrent().getArg()
                                          .get("TempListFn");

    // 由TempListWindow类传递得到模板文件夹session
    String            cf          = (String) Executions.getCurrent().getArg()
                                          .get("foldName");

    // 由TempListWindow类传递得到模板描述
    String            tDesc       = (String) Executions.getCurrent().getArg()
                                          .get("tempDesc");

    // wkt文件路径
    String            path0       = WebkeyControlUtil.getPath("/admin/styles");

    // Map集合
    Map               map         = new HashMap();
    
    //由templistwindow页面传递过来的tab参数
    Tab newTab = (Tab)Executions.getCurrent().getArg().get("newTab");

    public void afterCompose()
    {
        Components.wireVariables(this, this);
        Components.addForwards(this, this);
        try
        {
            initWindow();

            String nowEditWkt = WebkeyControlUtil.getPath("/admin/styles") + cf
                    + "\\" + tempName.getValue().trim(); // 用于fck各个标签中js读取Cookie，区别当前的编辑wkt文件cookie有效
            setProOfCookie("nowWkt", nowEditWkt + ";");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * <li>功能描述：初始化窗口
     * 
     * @author bobo
     * @2010-7-27
     */
    public void initWindow() throws IOException
    {
        if (wktFilename == null)
        {
            tempName.focus();
            String cssString = "<head>\n"
                    + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\"/>\n"
                    + "<title>WebKey快速开始</title>\n"
                    + "<link rel=\"stylesheet\" href=\"../../../../styles/"
                    + cf + "/index.css\" type=\"text/css\"/>\n"
                    + "<script language=\"javascript\">\n" + "</script>\n"
                    + "<style type=\"text/css\">" + "</style>\n" + "</head>\n";
            csscontent.setValue(cssString);
        } else
        {
            tempName.setValue(wktFilename);
            tempDesc.setValue(tDesc);
            String path = WebkeyControlUtil.getPath("/admin/styles") + cf + "/"
                    + wktFilename + ".wkt";
            String content = FileUtil.read(path);
            Integer beginIndex = content.indexOf("<head>");
            Integer endIndex = content.indexOf("</head>") + 7;
            csscontent.setValue(content.substring(beginIndex, endIndex));
            mlContent.setValue(content.substring(endIndex));
        }
        // 首先判断group.properties文件存在否？存在 读取文件夹名称，不存在显示默认
        File file = new File(path0 + "\\" + cf + "\\" + "group.properties");
        if (!file.exists())
        {
        } else
        {
            groupListbox.setItemRenderer(new ListitemRenderer()
            {
                public void render(Listitem item, Object data) throws Exception
                {
                    String f = (String) data;
                    item.setValue(f);
                    item.setHeight("25px");
                    item.setValue(f);
                    Listcell c1 = new Listcell(f);
                    item.appendChild(c1);
                }
            });
            String path2 = path0 + "\\" + cf + "\\" + "group.properties";
            List fList = new ArrayList();
            List gList = new ArrayList();
            gList = GroupUtil.getGroupList(path2);
            fList.add("默认");
            for (int j = 0; j < gList.size(); j++)
            {
                fList.add(gList.get(j));
            }
            ListModelList fListModel = new ListModelList(fList);
            groupListbox.setModel(fListModel);
        }
        groupListbox.setSelectedIndex(0);
    }

    /**
     * <li>功能描述：风格编辑
     * 
     * @2010-7-27
     */
    public void onClick$cssEdit()
    {
        final NewCssWindow win = (NewCssWindow) Executions.createComponents(
                "/apps/cms/template/newCSS.zul", null,
                null);
        win.doHighlighted();
        win.setClosable(true);
        String cssFilename = (String) Sessions.getCurrent().getAttribute(
                "filename");
        win.setCssFilename(cssFilename);
        win.afterCompose();

    }

    /**
     * <li>功能描述：保存模板信息,生成.wkt文件。保存在CssID相应的文件夹下面
     * 
     * @throws InterruptedException
     * @author bobo
     * @throws UnsupportedEncodingException
     * @2010-06-29
     */
    public void onClick$save() throws InterruptedException,
            UnsupportedEncodingException
    {
        String path0 = WebkeyControlUtil.getPath("/admin/styles");
        FileUtil.mkDir(path0, cf);
        String filePath = WebkeyControlUtil.getPath("/admin/styles") + "\\"
                + cf; // 判断该文件夹下面是否有.wkt文件

        WkTWebsite website = (WkTWebsite) Sessions.getCurrent().getAttribute( "domain_defult");
        String htmlFilePath = WebkeyControlUtil.getPath("/") + cf; // 生成的html文件存放位置，比如:/.../HtmlFile/
        String curCon = mlContent.getValue().trim();

        if (tempName.getValue().equals(""))
        {
            Messagebox.show("请您检查输入框，模板名称必须填写，并且只能输入英文字母或数字！", "提示",
                    Messagebox.OK, Messagebox.INFORMATION);
            tempName.focus();
            return;
        } else if (tempDesc.getValue().equals(""))
        {
            Messagebox.show("请您检查输入框，模板描述必须填写！", "提示", Messagebox.OK,
                    Messagebox.INFORMATION);
            tempDesc.focus();
            return;
        } else
        {
            // ---------------------------------------先保存.wkt文件--------------------------------//
            String con = "";
            Long len = 1000000L;
            Long be = null, en = null;
            Long max = mlContent.getValue().length() / len + 1;
            for (Long i = 0L; i < max; i++)
            {
                be = i * len;
                if (i == (max - 1))
                {
                    curCon = curCon.substring(be.intValue()).trim();
                    con = "<!--" + tempDesc.getValue().trim() + "-->" + "\r\n"
                            + "<!--" + Users.getLoginName().trim() + "-->"
                            + "\r\n" + "<!--" + "admin" + "--><html>"
                            + csscontent.getValue().trim() + "<body>" + curCon
                            + "</body></html>";
                } else
                {
                    en = (i + 1) * len;
                    curCon = curCon.substring(be.intValue(), en.intValue()).trim();
                    con = "<!--" + tempDesc.getValue().trim() + "-->" + "\r\n"
                            + "<!--" + Users.getLoginName().trim() + "-->"
                            + "\r\n" + "<!--" + "admin" + "--><html>"
                            + csscontent.getValue().trim() + "<body>" + curCon
                            + "</body></html>";
                }
            }

            File dir = new File(filePath);
            File[] listOfFiles = dir.listFiles();
            for (int i = 0; i < listOfFiles.length; i++)
            {
                if (listOfFiles[i].isFile())
                {
                    FileUtil.writeFile(filePath, con, tempName.getValue() + ".wkt");
                }
            }

            File file = new File(path0 + "\\" + cf + "\\" + "group.properties");
            if (!file.exists())
            {
                GroupUtil.creatNullGroup(path0 + "\\" + cf + "\\"+ "group.properties");
            } else
            {
                if (groupListbox.getSelectedIndex() == 0)
                {
                } else
                {
                    String grou = groupListbox.getSelectedItem().getValue().toString(); // 按行追加分组的名称
                    if (grou == null || wktFilename == null || grou.equals("")|| wktFilename.equals(""))
                    {
                        // 空操作吧~~~~~
                    } else
                    {
                        try
                        {
                            wktFilename = CharSet.iso2utf8(wktFilename);
                        } catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        GroupUtil.editGroup(filePath + "\\"
                                + "group.properties", grou, wktFilename); // 先删除原有分组后的模板名称,在编辑保存到新分组后面
                    }
                }
            }

            Messagebox.show("保存成功！", "提示", Messagebox.OK,
                    Messagebox.INFORMATION);
            this.detach();
			if(newTab!=null){
				newTab.onClose();
			}	
            Events.postEvent(Events.ON_CHANGE, this, null);
        }
    }

    /**
     * <li>功能描述：先保存，再发布。发布的时候 首先建立目录:一个是根下的styles,一个是WEB-INF/templet下的目录
     * styles下面是CSSID文件名命名的子文件夹，里面是.wkt文件对应的.ftl文件和.css文件
     * 
     * @author bobo
     * @throws Exception
     * @2010-07-20
     */
    public void onClick$saveAndRelease() throws Exception
    {
        String path0 = WebkeyControlUtil.getPath("/admin/styles");
        FileUtil.mkDir(path0, cf);
        String filePath = WebkeyControlUtil.getPath("/admin/styles") + "\\"+ cf; // 判断该文件夹下面是否有.wkt文件

        WkTWebsite site = (WkTWebsite) Sessions.getCurrent().getAttribute(
                "website");
        WkTWebsite w = new WkTWebsite();
        w.setKwId(0L);
        w.setkwName("testWebSite");
        w.setDep(0);
        String curPath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/"); // 当前项目根目录
        String htmlFilePath = curPath + "SiteHtml";
        htmlFilePath = createSiteDir(w, htmlFilePath);

        String curCon = mlContent.getValue().trim();
        if (tempName.getValue().equals(""))
        {
            Messagebox.show("请您检查输入框，模板名称必须填写，并且只能输入英文字母或数字！", "提示",
                    Messagebox.OK, Messagebox.INFORMATION);
            tempName.focus();
            return;
        } else if (tempDesc.getValue().equals(""))
        {
            Messagebox.show("请您检查输入框，模板描述必须填写！", "提示", Messagebox.OK,
                    Messagebox.INFORMATION);
            tempDesc.focus();
            return;
        } else
        {
            // ---------------------------------------先保存.wkt文件--------------------------------//
            String con = "";
            Long len = 1000000L;
            Long be = null, en = null;
            Long max = mlContent.getValue().length() / len + 1;
            for (Long i = 0L; i < max; i++)
            {
                be = i * len;
                if (i == (max - 1))
                {
                    curCon = curCon.substring(be.intValue()).trim();
                    con = "<!--" + tempDesc.getValue().trim() + "-->" + "\r\n"
                            + "<!--" + Users.getLoginName().trim() + "-->"
                            + "\r\n" + "<!--" + "admin" + "--><html>"
                            + csscontent.getValue().trim() + "<body>" + curCon
                            + "</body></html>";
                } else
                {
                    en = (i + 1) * len;
                    curCon = curCon.substring(be.intValue(), en.intValue()).trim();
                    con = "<!--" + tempDesc.getValue().trim() + "-->" + "\r\n"
                            + "<!--" + Users.getLoginName().trim() + "-->"
                            + "\r\n" + "<!--" + "admin" + "--><html>"
                            + csscontent.getValue().trim() + "<body>" + curCon
                            + "</body></html>";
                }
            }
            boolean isHave = false;
            boolean isheader = false;
            boolean isfooter =false;
            if(con.contains("HEADER")&&con.contains("FOOTER")){
            	isheader = checkHeader(con,cf);
            	isfooter  = checkFooter(con,cf);
            	if(isheader&&isfooter){
            		isHave = true;
            	}else{
            		isHave = false;
            	}
            }else if(con.contains("HEADER")){
            	isheader = checkHeader(con,cf);
            	isHave =  isheader;
            }else if(con.contains("FOOTER")){
            	isfooter = checkFooter(con,cf);
            	isHave =  isfooter;
            }
            
        	if(con.contains("HEADER")||con.contains("FOOTER")){   
            	if(isHave){            		
            		File dir = new File(filePath);
                    File[] listOfFiles = dir.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++)
                    {
                        if (listOfFiles[i].isFile())
                        {
                            FileUtil.writeFile(filePath, con, tempName.getValue() + ".wkt");
                        }
                    }
                    
            		boolean bOK = wktRelease(filePath, con, htmlFilePath);
            		if(bOK){
            			Messagebox.show("保存并发布成功！", "提示", Messagebox.OK,Messagebox.INFORMATION);
                        this.detach();
        				if(newTab!=null){
        					newTab.onClose();
        				}	
            		}else{
            			Messagebox.show("html生成失败！", "错误", Messagebox.OK, Messagebox.ERROR);
            		}            		
            	}else{
            		return;
            	}
        	}else{        		
        		File dir = new File(filePath);
                File[] listOfFiles = dir.listFiles();
                for (int i = 0; i < listOfFiles.length; i++)
                {
                    if (listOfFiles[i].isFile())
                    {
                        FileUtil.writeFile(filePath, con, tempName.getValue() + ".wkt");
                    }
                }
                
                boolean bOK = wktRelease(filePath, con, htmlFilePath);
        		if(bOK){
        			Messagebox.show("保存并发布成功！", "提示", Messagebox.OK,Messagebox.INFORMATION);
                    this.detach();
    				if(newTab!=null){
    					newTab.onClose();
    				}	
        		}else{
        			Messagebox.show("html生成失败！", "错误", Messagebox.OK, Messagebox.ERROR);
        		}
            }
            this.detach();
            Events.postEvent(Events.ON_CHANGE, this, null);
        }
    }

    /**
     * <li>功能描述：发布功能，即.wkt文件生成.html
     * 
     * @param filePath
     *            .wkt文件存放路径
     * @param con
     *            .wkt文件内容
     * @param htmlFilePath
     *            .html文件存放路径
     * @throws WrongValueException
     *             ,Exception
     * @author bobo
     * @2010-09-27
     */
    public boolean wktRelease(String filePath, String con, String htmlFilePath)
            throws WrongValueException, Exception
    {
        String baseDir0 = WebkeyControlUtil.getPath("/styles");
        FileUtil.mkDir(baseDir0, cf);
        String baseDir1 = WebkeyControlUtil.getPath("/WEB-INF/templet"); // 在templet下建立文件
        FileUtil.mkDir(baseDir1, cf);

        if ((filePath + "\\" + "index.css") != null)
        {
            String cssContent = FileUtil.read(filePath + "\\" + "index.css"); // 先读取/admin/styles/index.css，然后写入到/styles/index.css中
            FileUtil.writerText(baseDir0 + "\\" + cf + "\\" + "index.css",
                    cssContent);

        }

        // ---------------------------------------------------------------------------------情况1：
        // 第一个模板保存；再次保存 *** group.properties文件不存在： 则创建该文件 ,group.properties文件存在：则读取
        File file = new File(path0 + "\\" + cf + "\\" + "group.properties");
        if (!file.exists())
        {
            GroupUtil.creatNullGroup(path0 + "\\" + cf + "\\"
                    + "group.properties");
        } else
        {
            if (groupListbox.getSelectedIndex() == 0)
            {
            } else
            {
                String grou = groupListbox.getSelectedItem().getValue()
                        .toString(); // 按行追加分组的名称
                if (grou == null || wktFilename == null || grou.equals("")
                        || wktFilename.equals(""))
                {
                    // 空操作吧~~~~~
                } else
                {
                    try
                    {
                        wktFilename = CharSet.iso2utf8(wktFilename);
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                    GroupUtil.editGroup(filePath + "\\" + "group.properties",
                            grou, wktFilename); // 先删除原有分组后的模板名称,在编辑保存到新分组后面
                }
            }
        }

        boolean bOK = false;
        if (con.contains("wktid=\"wkt_docdtl\""))
        {
            bOK = true;
        } else
        {
            HtmlParser hp = new HtmlParser(filePath + "\\"
                    + tempName.getValue() + ".wkt", baseDir0 + cf + "\\"
                    + tempName.getValue() + ".ftl", path0 + "\\" + "head.txt",
                    tempName.getValue(),cf); // 解析.wkt文件
            XmlParser xp = new XmlParser(baseDir1  + cf + "/" + tempName.getValue() + ".xml", hp.getData());
            bOK = true;
//            Map root = (Map) Sessions.getCurrent().getAttribute("root");
//            String templatePath = WebkeyControlUtil.getPath("/styles"); // ftl存放位置，比如:/.../styles/，其下包含.ftl文件夹和要引入的.ftl
//            String templateFileName = cf + "\\" + tempName.getValue() + ".ftl"; // ftl文件名称
//            String htmlFileName = tempName.getValue() + ".html"; // 生成的htm文件名称
//            FreeMarkerUtil freetest = new FreeMarkerUtil();
//            bOK = freetest.geneHtmlFile(templatePath, templateFileName, root, htmlFilePath, htmlFileName);
        }
        return bOK;
    }
    
    /**
     * <li>功能描述：检查是否包含“页头”标签
     * @param con
     *            编辑器中的内容
     * @param cf
     *            风格文件夹，即存放wkt文件的文件夹
     * @author bobo
     * @2011-02-21
     */
    public boolean checkHeader(String con, String cf) throws InterruptedException{
    	String filePath = WebkeyControlUtil.getPath("/styles") + "\\"+ cf; 
    	String str = con;
    	boolean check = false;
		if(str.contains("HEADER")){
			Boolean flaghead = false;
			File ftlFile = new File(filePath);
			File[] ftlFileList = ftlFile.listFiles();
			if(ftlFileList!=null&&ftlFileList.length>0){
				for(int j=0;j<ftlFileList.length;j++){
					if(ftlFileList[j].exists()&&ftlFileList[j].isFile()){	
						String ftl = ftlFileList[j].getName();	
						if(ftl.trim().equals("header.ftl")){
							flaghead = true;
						}
					}
				}				
			}
			if(flaghead){
				check = true;
			}else{
				Messagebox.show("页头模板FTL文件不存在，请您检查是否已经发布页头模板！", "提示", Messagebox.OK,Messagebox.INFORMATION);
			    check = false;
			}			
		}
		
		return check;
    }
    
    /**
     * <li>功能描述：检查是否包含“页尾”标签
     * @param con
     *            编辑器中的内容
     * @param cf
     *            风格文件夹，即存放wkt文件的文件夹
     * @author bobo
     * @2011-02-21
     */
    public boolean checkFooter(String con, String cf) throws InterruptedException{
    	String filePath = WebkeyControlUtil.getPath("/styles") + "\\"+ cf; 
    	String str = con;
    	boolean check = false;
		if(str.contains("FOOTER")){
			Boolean flaghead = false;
			File ftlFile = new File(filePath);
			File[] ftlFileList = ftlFile.listFiles();
			if(ftlFileList!=null&&ftlFileList.length>0){
				for(int j=0;j<ftlFileList.length;j++){
					if(ftlFileList[j].exists()&&ftlFileList[j].isFile()){	
						String ftl = ftlFileList[j].getName();	
						if(ftl.trim().equals("footer.ftl")){
							flaghead = true;
						}
					}
				}				
			}
			if(flaghead){
				check = true;
			}else{
				Messagebox.show("页尾模板FTL文件不存在，请您检查是否已经发布页尾模板！", "提示", Messagebox.OK,Messagebox.INFORMATION);
			    check = false;
			}			
		}
		
		return check;
    }
    
    /**
     * <li>功能描述：重置模板编辑中的文本输入框
     * 
     * @author bobo
     * @2010-07-20
     */
    public void onClick$reset()
    {
        tempName.setValue("");
        tempDesc.setValue("");
    }

    public void onClick$renturn(){
		this.detach();
		if(newTab!=null){
			newTab.onClose();
		}
		
	}
    /**
     * <li>功能描述：由当前站点创建文件夹，并返回最终存放位置。
     * 
     * @param site
     * @param htmlFilePath
     * @author bobo
     */
    public String createSiteDir(WkTWebsite site, String htmlFilePath)
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
