package com.uniwin.webkey.core.ui;

/**
 * <li>功能描述：模板列表页面 对应页面admin/system/parameters/TemplateManage/tempList.zul
 * @author bobo
 * @serialData 2010-7-21
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.component.ui.Image;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Role;

public class RoleTempListWin extends Window implements AfterCompose
{
    private static final long serialVersionUID = 5114667906692605861L;

    Center roleListCen;
    
    // Listbox列表框组件
    private Listbox           roleListbox;

    //Button按钮组件	
	private Toolbarbutton 	  newRole,delRole;

    // Panel组件
    public Panel              roleGroupListPanel, roleListPanel;
    
    // 角色树 组件
    private Tree           roleTree;

    // 角色树Model
    private RoleTreeModel  roleTreeModel;

    // 角色树节点
    private SimpleTreeNode roleNode;
    
    //接口
    private IRoleManager roleManager = (IRoleManager) SpringUtil.getBean("roleManager");
    
    //传递角色对象参数
    Role roleTemp = (Role)Executions.getCurrent().getArg().get("roleTemp");

    public void afterCompose(){
        Components.wireVariables(this, this);
        Components.addForwards(this, this);        
		try {
			initWindow();
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
		
        delRole.addEventListener(Events.ON_CLICK, new EventListener(){
            public void onEvent(Event event) throws Exception{
                if (roleListbox.getSelectedItem() == null){
                	Messagebox.show(org.zkoss.util.resource.Labels.getLabel("role.ui.selectdelrecord"),
							org.zkoss.util.resource.Labels.getLabel("role.ui.messagepoint"),
					         Messagebox.OK, Messagebox.INFORMATION);
                    return;
                } else{
                    Set sel = roleListbox.getSelectedItems();
                    Iterator it = sel.iterator(); 
                    if (Messagebox.show(org.zkoss.util.resource.Labels.getLabel("role.ui.reallydelrecord"), 
					          org.zkoss.util.resource.Labels.getLabel("role.ui.messagepoint"),
					          Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES){
                        while (it.hasNext()){
                        	Listitem item=(Listitem)it.next();
							Role role = (Role)item.getValue();								
							roleManager.remove(role);
							Messagebox.show(org.zkoss.util.resource.Labels.getLabel("role.ui.messagedelsuccess"),
	    							org.zkoss.util.resource.Labels.getLabel("role.ui.messagepoint")
	    							, Messagebox.OK,Messagebox.INFORMATION);
                        }
                        initWindow();
                        loadTree();
                        
                    }
                }
            }
        });
		Events.postEvent(Events.ON_CHANGE, this, null);
    }

    /**
     * <li>功能描述：初始化窗口
     * @author bobo
     * @throws InterruptedException 
     * @throws DataAccessException 
     * @2010-8-27
     */
    public void initWindow() throws IOException, DataAccessException, InterruptedException{
    	roleListbox.setItemRenderer(new ListitemRenderer(){
        	public void render(Listitem item, Object data) throws Exception{
                final Role role = (Role) data;
                item.setValue(role);
                item.setHeight("25px");
                Listcell c = new Listcell("");
                Listcell c0 = new Listcell(item.getIndex() + 1 + "");
                Listcell c1 = new Listcell(role.getRoleName());
                c1.setTooltiptext(role.getRoleName());
                
                Listcell c2 ;
                if(role.getState() ==0){
                	c2 = new Listcell(org.zkoss.util.resource.Labels.getLabel("role.zul.normal"));
                	c2.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.zul.normal"));
                }else{
                	c2 = new Listcell(org.zkoss.util.resource.Labels.getLabel("role.zul.disable"));
                	c2.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.zul.disable"));
                }        
                
                Listcell c3;                
                if (role.getIsDefult() == 1){
                	c3 = new Listcell(org.zkoss.util.resource.Labels.getLabel("role.ui.yes"));
                	c2.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.ui.yes"));
                } else{
                	c3 = new Listcell(org.zkoss.util.resource.Labels.getLabel("role.ui.no"));
                	c2.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.ui.no"));
                }
                
                Listcell c4 = new Listcell();
                Hbox hb=new Hbox(); 
        		c4.appendChild(hb);  
                Image iEdit = new Image();
                Image iAuth = new Image();
                iEdit.setType("edit");
                iAuth.setType("authorize");
                
                iEdit.addEventListener("onClick", new EventListener(){
                    public void onEvent(Event event) throws Exception{
                        editRole(role);
                    }
                });
                iAuth.addEventListener("onClick", new EventListener(){
                    public void onEvent(Event event) throws Exception{
                        authRole(role);
                    }
                });
                iAuth.setTooltiptext(org.zkoss.util.resource.Labels.getLabel("role.zul.authorize"));
                
                iEdit.setParent(hb);
                iAuth.setParent(hb);

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
     * @author bobo
     * @throws InterruptedException
     * @throws IOException
     * @throws DataAccessException 
     * @serialData 2011-2-22
     */
    public void reloadListbox() throws InterruptedException, IOException, DataAccessException{  
		List list =  roleManager.getParentRoleList(roleTemp.getRoleId());
        ListModelList fileNameListModel = new ListModelList(list);
        roleListbox.setModel(fileNameListModel);
    }

    /**
     * <li>功能描述：点击“操作”中编辑按钮
     * @serialData 2011-2-23
     */
    public void editRole(Role role) throws SuspendNotAllowedException, InterruptedException, DataAccessException, IOException{
    	try{   
            Map map = new HashMap();
            map.put("editRole", role);
            RoleUpdataWin roleEditWin = (RoleUpdataWin) Executions.createComponents("/apps/core/roleUpdata.zul",null,map);
            roleEditWin.setClosable(true);
            roleEditWin.doModal();            
            initWindow();
            Events.postEvent(Events.ON_CHANGE, this, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   
    /**
     * <li>功能描述：点击“操作”中角色授权按钮，进入授权页面
     * @serialData 2011-2-23
     */
    public void authRole(Role role){
        try{
            Map map = new HashMap();
            map.put("role", role);
            UserroleWin window = (UserroleWin) Executions.createComponents("/apps/core/userrole.zul", null, map);
            window.doModal();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * <li>功能描述：添加角色
     * @author bobo
     * @throws InterruptedException 
     * @serialData 2011-2-22
     */
    public void onClick$newRole() throws InterruptedException{		
    	Map map = new HashMap();
        map.put("roleGroup", roleTemp);
    	try{   
    		final RoleAddWin win = (RoleAddWin) Executions.createComponents("/apps/core/roleAdd.zul", null, map);
            win.setClosable(true);
            win.doModal();
            initWindow(); 
        } catch (Exception e){
            e.printStackTrace();
        }
        Events.postEvent(Events.ON_CHANGE, this, null);
    } 

    /**
     * <li>功能描述：加载树
     * @throws DataAccessException 
     * @serialData 2011-2-23
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

        roleNode = new SimpleTreeNode(new TempTree(org.zkoss.util.resource.Labels.getLabel("role.zul.roleorganate")), createTreeChildren(name, proList)); // ���
        ArrayList<SimpleTreeNode> al = new ArrayList<SimpleTreeNode>(); 
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
     * 
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
