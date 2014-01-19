package com.uniwin.webkey.core.ui;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.itf.IUsersfavorateManager;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.tree.ui.FavoriteTree;
import com.uniwin.webkey.util.ui.FavoriteContentProvider;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class FavoriteWin extends Window implements AfterCompose
{
    private Panelchildren         treeDiv;

    private FavoriteTree          tree;

    private List<Checkbox>        checkboxList;

    private IUsersfavorateManager usersfavorateManager = (IUsersfavorateManager) SpringUtil
                                                               .getBean("usersfavorateManager");
//    private IPermissionManager    permissionManager;

    public FavoriteWin()
    {
//        permissionManager = (IPermissionManager) TuscanySpringUtil.getBean(
//                "permissionManager", "t");
    }

    public void afterCompose()
    {
        treeDiv = (Panelchildren) this.getFellow("treeDiv");
        tree = new FavoriteTree();
        Resource root = new Resource();
        root.setId(0);
//        List<Resource> resourceList = FrameCommonDate.getResourceList();
//        
//        for (Resource resource : resourceList)
//        {
//            if (resource.getId().intValue() == 0)
//            {
//                root = resource;
//                break;
//            }
//        }
        FavoriteContentProvider contentProvider = new FavoriteContentProvider(
                root);
        checkboxList = contentProvider.getCheckList();
        tree.setTreeitemlContentProvider(contentProvider);
        tree.rebuildTree();
        treeDiv.appendChild(tree);
        selectedNavgation(); // 选中已加入导航的菜单
    }

    /**
     * 初始化用户选中的菜单
     */
    public void selectedNavgation()
    {
        List<Resource> faReses = FrameCommonDate.getFavorateResourceList();
        String selected = (String) Sessions.getCurrent().getAttribute(
                "rbac_selectedIds");
        String selectedIds = selected == null ? "" : selected;
        if (selected != null)
        {
            if (selectedIds.length() > 0)
            {
                String[] idsArray = selectedIds.split(",");
                for (Checkbox fav : checkboxList)
                {
                    for (String id : idsArray)
                    {
                        if (fav.getId().equals(id))
                        {
                            fav.setChecked(true);
                        }
                    }
                }
            }
        } else
        {
            for (Checkbox fav : checkboxList)
            {
                for (Resource resource : faReses)
                {
                    if (fav.getId().equals(resource.getId().intValue() + ""))
                    {
                        fav.setChecked(true);
                    }
                }
            }
        }
    }

    /**
     * 获得resources的id用逗号分割的字符串
     * 
     * @return组合的字符串
     */
    public String getResourceIds()
    {
        String ids = "";
        for (Checkbox box : checkboxList)
        {
            if (box.isChecked())
            {
                ids += box.getId() + ",";
            }
        }
        if (ids.length() > 0)
        {
            ids = ids.substring(0, ids.length() - 1);
        }
        Sessions.getCurrent().setAttribute("rbac_selectedIds", ids);
        return ids;
    }

    public void addNavigation()
    {
        String selected = (String) Sessions.getCurrent().getAttribute(
                "rbac_selectedIds");
        String selectedIds = selected == null ? "" : selected;
    }

    /**
     * 保存用户的设置的信息
     */
    public void saveOrUpdataFavorate()
    {
        String resourceIds = this.getResourceIds();
        if (resourceIds.length() > 0)
        {
            try
            {
                this.usersfavorateManager.addUsersfavorateList(resourceIds,
                        FrameCommonDate.getUser().getUserId().intValue());
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("favorite.ui.savechange"));
            } catch (Exception e)
            {
                e.printStackTrace();
                try
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.changefailed"));
                } catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        } else
        {
            try
            {
                this.usersfavorateManager
                        .removeUersfavoratListByUserid(FrameCommonDate
                                .getUser().getUserId().intValue());
                Messagebox.show(org.zkoss.util.resource.Labels
                        .getLabel("favorite.ui.savechange"));
            } catch (Exception e)
            {
                e.printStackTrace();
                try
                {
                    Messagebox.show(org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.changefailed"));
                } catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

    }

}
