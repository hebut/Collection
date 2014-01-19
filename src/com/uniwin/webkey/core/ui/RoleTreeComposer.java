package com.uniwin.webkey.core.ui;

/**
 * <li>功能描述：选择树节点（角色组节点和角色节点），弹出相应页面
 * @author bobo
 * @serialData 2011-2-22
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;

public class RoleTreeComposer extends GenericForwardComposer
{
	private static final long serialVersionUID = -8375448889814264947L;

	// 角色树 组件
    private Tree           roleTree;

    // 角色树Model
    private RoleTreeModel  roleTreeModel;

    // 角色树节点
    private SimpleTreeNode roleNode;

    // Center组件
    Center                 roleListCen;

    //接口
    private IRoleManager roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
    
    public void doAfterCompose(Component comp) throws Exception{
        super.doAfterCompose(comp);
        roleTree.setTreeitemRenderer(new RoleTreeitemRenderer());

        roleTree.addEventListener(Events.ON_SELECT, new EventListener(){
            public void onEvent(Event event) throws Exception{
                Tree selected = (Tree) event.getTarget();
                Treeitem item = selected.getSelectedItem();
                if (item.getLabel().equals(org.zkoss.util.resource.Labels.getLabel("role.zul.roleorganate"))){
                	roleListCen.getChildren().clear();
                    final RowIndexGroupListWin mainWin = (RowIndexGroupListWin) Executions
                            							.createComponents("/apps/core/roleIndexGroupList.zul",roleListCen, null);
                    mainWin.initWindow();
                    mainWin.addEventListener(Events.ON_CHANGE,new EventListener(){
                        public void onEvent(Event event)throws Exception{
                            loadTree();
                        }
                     });
                } else if (item != null){
                	List roleList = (List) roleManager.getParentRoleList(0);                	
                	for(int k=0;k<roleList.size();k++){
                		 Role ro = (Role)roleList.get(k);                		
                		 if (item.getLabel().equals(ro.getName().trim())){
                             String roleName = ro.getName().trim();
                             Map map = new HashMap();
                             map.put("roleTemp", ro);
                             roleListCen.getChildren().clear();
                             final RoleTempListWin tempWin = (RoleTempListWin) Executions
                                     							.createComponents("/apps/core/roleTempList.zul",roleListCen, map);
                             tempWin.initWindow();
                             tempWin.loadTree();
                             tempWin.addEventListener(Events.ON_CHANGE,new EventListener(){
                                 public void onEvent(Event event)throws Exception{
                                     loadTree();
                                 }
                             });
                         }
                	}                
                    if (item.getTreechildren() != null){
                        return;
                    }
                }
            }
        });
        loadTree();
    }

   /** <li>功能描述：加载树
    * @serialData 2011-2-22
    * @throws IOException
    * @throws DataAccessException 
    */
    public void loadTree() throws IOException, DataAccessException{
    	roleTreeModel = new RoleTreeModel(createTreeData());
    	roleTree.setModel(roleTreeModel);
        openTree(roleTree.getTreechildren());
    }

    /**
     * <li>功能描述：创建树节点
     * @author bobo
     * @serialData 2011-2-22
     * @param null
     * @return SimpleTreeNode("ROOT",al)
     * @throws IOException
     * @throws DataAccessException 
     */
    public SimpleTreeNode createTreeData() throws IOException, DataAccessException{
        List folderList = null;
        List fList = new ArrayList();
        List proList = new ArrayList();
		folderList = roleManager.getParentRoleList(0);		
        for (int i = 0; i < folderList.size(); i++){
            Role ro = (Role)folderList.get(i);
            fList.add(ro.getName());
            proList.add(ro);
        }
        final int size = fList.size();
        String[] name = (String[]) fList.toArray(new String[size]);

        roleNode = new SimpleTreeNode(new TempTree(org.zkoss.util.resource.Labels.getLabel("role.zul.roleorganate")), createTreeChildren(name, proList)); // 树根
        ArrayList<SimpleTreeNode> al = new ArrayList<SimpleTreeNode>(); // 创建树根，孩子添加在该根上
        al.add(roleNode);
        return new SimpleTreeNode("ROOT", al);
    }

    public List<SimpleTreeNode> createTreeChildren(String[] name, List pathList)throws IOException, DataAccessException{
        List<SimpleTreeNode> alc = new ArrayList<SimpleTreeNode>();
        for (int i = 0; i < name.length; i++){
            TempTree tem = new TempTree(name[i]);
            Role ro = (Role) pathList.get(i); 
            List<SimpleTreeNode> clist = new ArrayList<SimpleTreeNode>();
           
            List list = null;
            String node = null;
			list =  roleManager.getParentRoleList(ro.getRoleId());			
            for (int k = 0; k < list.size(); k++){
            	Role r = (Role)list.get(k);
                node = r.getName();
                clist.add(new SimpleTreeNode(new TempTree(node),new ArrayList()));
            }
            SimpleTreeNode stn = new SimpleTreeNode(tem, clist);
            alc.add(stn);
        }
        return alc;
    }
    /**
     * <li>功能描述：将树节点展开。
     * @serialData 2011-2-22
     * @param chi
     * @return null
     */
    public void openTree(Treechildren chi){
        if (chi == null)
            return;
        List clist = chi.getChildren();
        for (int i = 0; i < clist.size(); i++){
            Treeitem item = (Treeitem) clist.get(i);
            item.setOpen(true);
            openTree(item.getTreechildren());
        }
    }
}
