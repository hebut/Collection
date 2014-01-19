package com.uniwin.webkey.core.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.model.WkTWebsite;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IResourceManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Auth;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.Resource;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.RoleDepResouAuth;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.tree.ui.RoleTreeLabelProvider;
import com.uniwin.webkey.tree.ui.RolegroupTree;
import com.uniwin.webkey.tree.ui.RoletreeContentProvider;
import com.uniwin.webkey.util.ui.OrganizationLabelProvider;
import com.uniwin.webkey.util.ui.OrganizationThirdContentProvider;

public class AuthorizeResourceWindow extends Window implements AfterCompose{   
	
	//传递的user 站点
    Users user = (Users) Sessions.getCurrent().getAttribute("users");
    WkTWebsite curWS = (WkTWebsite)Sessions.getCurrent().getAttribute("domain_defult");    

  //由ResourceUpdataWin类传递得“当前资源名称”,"当前资源ID"
	String sourceName = (String)Executions.getCurrent().getArg().get("sourceName");
	int sourceId = (Integer)Executions.getCurrent().getArg().get("sourceId");
       
	 //resource资源接口
    private IResourceManager 		resourceManager = (IResourceManager)SpringUtil.getBean("resourceManager");
    
    //role角色接口
    private IRoleManager 			roleManager =  (IRoleManager) SpringUtil.getBean("roleManager");
    
    // organize组织接口
	private IOrganizationManager 	organizationManager = (IOrganizationManager) SpringUtil.getBean("organizationManager");
	
	 //auth授权接口
	private IAuthManager  			iAuthManager =  (IAuthManager) SpringUtil.getBean("authManager");
	
	 //permission许可接口
	private IPermissionManager 		iPermissionManager =  (IPermissionManager) SpringUtil.getBean("permissionManager");

    //角色树
    private RolegroupTree      		roletree;
    
    //部门树
    private OrganizationTree   		organizeTree;
    
    //zk组件
    private Grid 					authorizeGrid;    
    private Label 					role,depart;
    private Textbox 				searchResouceTextbox;
    
    //下端的listbox
	private Listbox 				resourcesListbox;
	private ListModelList 			resourceListModel;
    
    //Modellist模型
    private ListModelList 			gridListModelList; 
	
    //设置Grid的detail不同id
    int num = 0;
    	
	//加载中间grid的数据
	private  List<Resource> 					reGridlist = new ArrayList<Resource>();	
	
	//存放中间grid选中的resource
	private  List<Resource> 					reList = new ArrayList<Resource>();	
	
	///存放选择资源下许可
	private  List<Permission> 					permissionCheckList = new ArrayList<Permission>(); 
 		
	//记录选中资源下，许可是否选中
	private  Map 								checkPermissionMap = new HashMap();	
	
	//记录所有选中的资源，key=资源ID,value=map
	private  Map<Integer,Map> 					allResourceListMap = new HashMap();
	
	//记录数据库数据
	private List<RoleDepResouAuth> 				datebaseList = new ArrayList<RoleDepResouAuth>();
	
	//控制资源的checkbox的勾选
	private Map 								checkIdMap = new HashMap();
	
	//控制资源的detail的开合
	private Map 								detailIdMap = new HashMap();
			
	/**
	 *<li>功能描述：窗体的afterCompose()
	 */
    public void afterCompose(){    	
        Components.wireVariables(this, this);
        Components.addForwards(this, this);        
        
		//创建角色树
        bulidRoleTree();        
        roletree.addEventListener(Events.ON_SELECT, new EventListener(){
       		public void onEvent(Event event) throws Exception {                 
                    Treeitem it=roletree.getSelectedItem();
                    Role ro=(Role)it.getValue();                                      
                    role.setValue(ro.getName());                    
                    Sessions.getCurrent().setAttribute("roleID", ro);
       		}
        });
           
        //创建部门树
        bulidOrganizeTree();
        organizeTree.addEventListener(Events.ON_SELECT, new EventListener(){
       		public void onEvent(Event event) throws Exception {                 
                    Treeitem it = organizeTree.getSelectedItem();
                    Organization orga = (Organization)it.getValue();                                       
                    depart.setValue(orga.getName());                    
                    Sessions.getCurrent().setAttribute("orgaID", orga);
       		}
        });
        
        //加载Grid
        try {
			try {
				bulidAuthorizeGrid();
			} catch (ObjectNotExistException e) {
				e.printStackTrace();
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		//初始窗体右侧
		try {
			iniWinCenterAndLeft();
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (ObjectNotExistException e) {
			e.printStackTrace();
		}		
    }
    /**
     * <li>功能描述：初始化资源授权窗体的中间和右侧部分
     * @throws DataAccessException
     * @throws ObjectNotExistException
     */
    public void iniWinCenterAndLeft() throws DataAccessException, ObjectNotExistException{
    	//右侧的listbox
		resourcesListbox.setItemRenderer(new ListitemRenderer(){
	        public void render(Listitem item, Object data) throws Exception{
	        		RoleDepResouAuth redep = (RoleDepResouAuth) data;	        		
	                item.setValue(redep);
	                item.setHeight("25px");
	                Listcell c = new Listcell(redep.getReourceName());	                
	                Listcell c1 = new Listcell(redep.getRoleName());
	                Listcell c2 = new Listcell(redep.getOrganizationname());
	                Listcell c3 = new Listcell(redep.getPermissionName());
	              
	                item.appendChild(c);
	                item.appendChild(c1);
	                item.appendChild(c2);
	                item.appendChild(c3);
	        }
	     });	
		
		//根据资源id,查找数据库auth表，加载右侧的listbox
		 Map inibaseMap = new HashMap();
		 String key="";        					
		 List authlist = new ArrayList();
		 authlist = iAuthManager.getAuthByRID(String.valueOf(sourceId), "1");
		 
		 //防止第一次不选资源的勾，就许可
		 Resource resou = (Resource)resourceManager.get(sourceId);
		 reList.add(resou);
		 
		 for(int k=0;k<authlist.size();k++){
			Auth auth = (Auth)authlist.get(k);
			int roleId = auth.getKrId();
			int organId = auth.getKdId();
			int permiId = auth.getKpId();             					
			Role roleAu = null;
			roleAu = roleManager.get(roleId);			
			Organization orgaAu = null;
		    orgaAu = organizationManager.get(organId);
			
			Permission permiAu = null;
			permiAu = iPermissionManager.get(permiId);
			RoleDepResouAuth rda = new RoleDepResouAuth();        			
			rda.setRoleId(roleId);
			rda.setRoleName(roleAu.getName());        			
			rda.setOrganizationId(organId);
			rda.setOrganizationname(orgaAu.getName());        			
			rda.setReourceId(sourceId);
			rda.setReourceName(sourceName);        			
			rda.setPermissionId(permiId);
			rda.setPermissionName(permiAu.getName());
			             	        		
			key = sourceId+","+roleId+"," + organId +"," +permiId;   
			inibaseMap.put(key, rda);
		}
		if(!inibaseMap.isEmpty()){
			Iterator tp = inibaseMap.keySet().iterator();
			while (tp.hasNext()) { 
					String ke = (String) tp.next(); 
					RoleDepResouAuth rdra = (RoleDepResouAuth)inibaseMap.get(ke);			  
					datebaseList.add(rdra); 
			}
		}
		resourceListModel = new ListModelList(datebaseList);
	    resourcesListbox.setModel(resourceListModel);
    }
    /**
	 * <li>功能描述：Grid的detail进行润色处理
	 * @param null
     * @throws DataAccessException 
     * @throws ObjectNotExistException 
	 */
    public void bulidAuthorizeGrid() throws DataAccessException, ObjectNotExistException{
        authorizeGrid.setRowRenderer(new RowRenderer(){
            public void render(final Row row, Object data) throws Exception{
            	if(data == null) return;            	
            	final Resource resource = (Resource)data; 
            	
                final Detail detail = new Detail(); 
                num++;
                detail.setId("detail"+resource.getId()); 
                detail.setOpen(false);
                detail.setParent(row);
                detailWindow(row,resource, detail);//调用新页面生成窗口函数
         		
            	final Checkbox check = new Checkbox();
            	check.setId("check"+resource.getId());
//            	check.setLabel(num+"");
                check.setParent(row);  
                check.setChecked(false);
                Label label = new Label();
                label.setValue(resource.getName());
                label.setParent(row); 
                row.setId("row"+resource.getId());
                
                if(sourceId==resource.getId()){
            		detail.setOpen(true);
            		check.setChecked(true);
            	}
                
                checkIdMap.put(check.getId(), false);
                detailIdMap.put(detail.getId(), false);
                
                DetailOpenEventListener(check,row,detail);                
                RowOnClickEventListener(check,row,detail);                
                
                //Checkbox����¼���
                check.addEventListener("onCheck", new EventListener() {        			
                	public void onEvent(Event event) throws Exception { 
                		 Map base = new HashMap();
                		 datebaseList.clear();
                		 
                		 Iterator ite = checkIdMap.keySet().iterator();
	           			 while (ite.hasNext()) { 
	           				String key1 = (String) ite.next(); 
	           				checkIdMap.put(key1, false);	           					
	           			 }
	           			 checkIdMap.put(check.getId(),true);
	           			 
	           			 Iterator item = checkIdMap.keySet().iterator();
	           			 while (item.hasNext()) { 
	           				String key1 = (String)item.next(); 
	           				((Checkbox)row.getFellow(key1)).setChecked((Boolean)checkIdMap.get(key1));
	           			 }
	           			 
	           			 Iterator itera0 = detailIdMap.keySet().iterator();
	           			 while (itera0.hasNext()) { 
	           				String key2 = (String)itera0.next(); 
	           				detailIdMap.put(key2, false);	           					
	           			 }
	           			 detailIdMap.put(detail.getId(),true);
	           			 Iterator itera1 = detailIdMap.keySet().iterator();
	           			 while (itera1.hasNext()) { 
	           				String key1 = (String)itera1.next(); 
	           				((Detail)row.getFellow(key1)).setOpen((Boolean)detailIdMap.get(key1));
	           			 }	           			
	           			
        				 if(check.isChecked()){
        					 reList.clear(); 
        					 reList.add(resource);  
        					 
        					//根据资源id,查找数据库auth表，加载右侧的listbox
        					 String key="";        					
             				 List authlist = iAuthManager.getAuthByRID(String.valueOf(resource.getId()), "1");
             				 for(int k=0;k<authlist.size();k++){
             					Auth auth = (Auth)authlist.get(k);
             					int roleId = auth.getKrId();
             					int organId = auth.getKdId();
             					int permiId = auth.getKpId();             					
             					Role roleAu = roleManager.get(roleId);
             					Organization orgaAu = organizationManager.get(organId);
             					Permission  permiAu = iPermissionManager.get(permiId);        					
             					
             					RoleDepResouAuth rda = new RoleDepResouAuth();        			
                     			rda.setRoleId(roleId);
                     			rda.setRoleName(roleAu.getName());        			
                     			rda.setOrganizationId(organId);
                     			rda.setOrganizationname(orgaAu.getName());        			
                     			rda.setReourceId(resource.getId());
                     			rda.setReourceName(resource.getName());        			
                     			rda.setPermissionId(permiId);
                     			rda.setPermissionName(permiAu.getName());
                     			             	        		
                     			key = resource.getId()+","+roleId+"," + organId +"," +permiId;   
                     			base.put(key, rda);
             				}
             				if(!base.isEmpty()){
             					Iterator tp = base.keySet().iterator();
             					while (tp.hasNext()) { 
          							String ke = (String) tp.next(); 
          							RoleDepResouAuth rdra = (RoleDepResouAuth)base.get(ke);			  
          							datebaseList.add(rdra); 
             					}
             				}
             				
             				Map smallMap = new HashMap();
             				if((Map)allResourceListMap.get(((Resource)reList.get(0)).getId())!=null)
             					smallMap = (Map)allResourceListMap.get(((Resource)reList.get(0)).getId());             				
             				List<RoleDepResouAuth> newAddRDRAList = new ArrayList<RoleDepResouAuth>(); 
             				Iterator iterator = smallMap.keySet().iterator();
             				while (iterator.hasNext()) {
             						String key1 = (String)iterator.next(); 
             						RoleDepResouAuth rdra = (RoleDepResouAuth)smallMap.get(key1);			  
             						newAddRDRAList.add(rdra); 
             				} 
             				
             				//新添加的数据   +数据库
             				newAddRDRAList.addAll(datebaseList);
             				List lastList = removeSameObject(newAddRDRAList);//过滤函数               				
             				resourceListModel = new ListModelList(lastList);
             				resourcesListbox.setModel(resourceListModel);             				
            			    
        					Iterator iterat = checkPermissionMap.keySet().iterator();
             				while (iterat.hasNext()) { 
             				   Integer key0 = (Integer) iterat.next(); 
             				   Checkbox ch = (Checkbox)checkPermissionMap.get(key0); 
             				   if(ch.isChecked()){
             					   ch.setChecked(false);
             					   checkPermissionMap.put(key0,null);
             					   iterat.remove();
             				   }	
             				} 
        				}else{
        					resourceListModel = new ListModelList();
             			    resourcesListbox.setModel(resourceListModel);             			    
        					reList.remove(resource); 						
        				}
        			}
        		});
            }            
        });
        
        loadGrid();   
        Events.postEvent(Events.ON_CHANGE, this, null);
    }  
    /**
	 * <li>功能描述：grid下detail的打开事件
	 * void 
	 * @author bobo
	 * @param check grid的资源选择框
	 * @param row，资源行
	 * @param detail ,grid的detail
     * @throws DataAccessException 
	 */
    public void DetailOpenEventListener(final Checkbox check,final Row row,final Detail detail) 
	{
    	detail.addEventListener("onOpen", new EventListener() {        			
        	public void onEvent(Event event) throws Exception {  
       		     Iterator ite = checkIdMap.keySet().iterator();
       			 while (ite.hasNext()) { 
       				String key1 = (String) ite.next(); 
       				checkIdMap.put(key1, false);	           					
       			 }
       			 checkIdMap.put(check.getId(),true);
       			 Iterator item = checkIdMap.keySet().iterator();
       			 while (item.hasNext()) { 
       				String key1 = (String)item.next(); 
       				((Checkbox)row.getFellow(key1)).setChecked((Boolean)checkIdMap.get(key1));       				
       			 }
       			 
       			 Iterator iterator = detailIdMap.keySet().iterator();
       			 while (iterator.hasNext()) { 
       				String key0 = (String)iterator.next(); 
       				detailIdMap.put(key0, false);	           					
       			 }
       			 detailIdMap.put(detail.getId(),true);
       			 Iterator iterator1 = detailIdMap.keySet().iterator();
       			 while (iterator1.hasNext()) { 
       				String key1 = (String)iterator1.next(); 
       				((Detail)row.getFellow(key1)).setOpen((Boolean)detailIdMap.get(key1));
       			 }	
       			 
       			 if(check.isChecked()){
       				Events.postEvent(Events.ON_CHECK, check,null);//被选中，触发下面的check事件
					Iterator iterat = checkPermissionMap.keySet().iterator();
     				while (iterat.hasNext()) {
     				   Integer key0 = (Integer) iterat.next();
     				   Checkbox ch = (Checkbox)checkPermissionMap.get(key0); 
     				   if(ch.isChecked()){
     					   ch.setChecked(false); 
     					   checkPermissionMap.put(key0,null);
                           iterat.remove();
     				   }     				   
     				}
       			 }
       			 
        	  }            
        });
	}
    
    /**
	 * <li>功能描述：row的打开detail事件
	 * void 
	 * @author bobo
	 * @param check grid的资源选择框
	 * @param row，资源行
	 * @param detail ,grid的detail
     * @throws DataAccessException 
	 */
    public void RowOnClickEventListener(final Checkbox check,final Row row,final Detail detail){
    	row.addEventListener("onClick", new EventListener() {        			
        	public void onEvent(Event event) throws Exception {  
       		     Iterator ite = checkIdMap.keySet().iterator();
       			 while (ite.hasNext()) { 
       				String key1 = (String) ite.next(); 
       				checkIdMap.put(key1, false);	           					
       			 }
       			 checkIdMap.put(check.getId(),true);
       			 Iterator item = checkIdMap.keySet().iterator();
       			 while (item.hasNext()) { 
       				String key1 = (String)item.next(); 
       				((Checkbox)row.getFellow(key1)).setChecked((Boolean)checkIdMap.get(key1));
       			 }
       			 
        		
       			 Iterator iterator = detailIdMap.keySet().iterator();
       			 while (iterator.hasNext()) { 
       				String key0 = (String)iterator.next(); 
       				detailIdMap.put(key0, false);	           					
       			 }
       			 detailIdMap.put(detail.getId(),true);
       			 Iterator iterator1 = detailIdMap.keySet().iterator();
       			 while (iterator1.hasNext()) { 
       				String key1 = (String)iterator1.next(); 
       				((Detail)row.getFellow(key1)).setOpen((Boolean)detailIdMap.get(key1));
       			 }	
       			 	           			 	           			 
       			 if(check.isChecked()){
       				Events.postEvent(Events.ON_CHECK, check,null);//被选中，触发下面的check事件
					Iterator iterat = checkPermissionMap.keySet().iterator();
     				while (iterat.hasNext()) { 
     				   Integer key0 = (Integer) iterat.next(); 
     				   Checkbox ch = (Checkbox)checkPermissionMap.get(key0); 
     				  if(ch.isChecked()){
     					   ch.setChecked(false);
     					   checkPermissionMap.put(key0,null);
                           iterat.remove();
     				  } 
     				} 
            	} 
        	  }            
        });
    }
    /**
	 * <li>功能描述：grid下detail新页面生成窗口函数
	 * void 
	 * @author bobo
	 * @param row grid的行,resource资源实体,detail grid的detail
     * @throws DataAccessException 
	 */
    public void detailWindow(Row row,final Resource resource, Detail detail) throws DataAccessException{
    	int resourceId = resource.getId().intValue();
        final List permissionList = iPermissionManager.getPermissionByKridAndKrtype(String.valueOf(resourceId),"1");                             
	    	final org.zkoss.zul.Window w = (org.zkoss.zul.Window)Executions.createComponents("/apps/core/authorizeResourceDetail.zul",null,null);   //��a_authorizeDetail.zulҳ�棬���ݵ�ǰ��Դ��ӵ�е����
		    for(int i=0;i<permissionList.size();i++){
	     	     final Permission permission = (Permission)permissionList.get(i);
		     	 final Checkbox che = new Checkbox();	 		     	 
		     	 che.setLabel(permission.getName());
		     	 che.setId(""+i);
		     	 che.setParent(w);
		     	 che.setAttribute("per", permission);	
		     	 
		     	 che.addEventListener("onCheck", new EventListener() {        			
            	 public void onEvent(Event event) throws Exception {            		
            		List childList = w.getChildren();	                		
            		for (int j = 0; j < childList.size(); j++) {
            			if (childList.get(j) instanceof Checkbox) {
            				if(((Checkbox)childList.get(j)).isChecked()){
            					if (null!=((Checkbox)childList.get(j)).getAttribute("per")) {
            						if(permissionCheckList.size()>0){
            							boolean isSame = false;
            							for(int k=0;k<permissionCheckList.size();k++){
            								if(((Permission)((Checkbox)childList.get(j)).getAttribute("per")).equals((Permission)permissionCheckList.get(k))){
            									isSame = true;
            								}
            							}
            							if (!isSame) {
            								if(che.isChecked()){
            									permissionCheckList.add((Permission) ((Checkbox)childList.get(j)).getAttribute("per"));
            								}else{
            									permissionCheckList.remove((Permission) ((Checkbox)childList.get(j)).getAttribute("per"));
            								}
										}
            						}else{
            							if(che.isChecked()){
            								permissionCheckList.add((Permission) ((Checkbox)childList.get(j)).getAttribute("per"));
            							}else{
        									permissionCheckList.remove((Permission) ((Checkbox)childList.get(j)).getAttribute("per"));
        								}
            						}
								}
            				}else{
            					permissionCheckList.remove((Permission) ((Checkbox)childList.get(j)).getAttribute("per"));
            				}
						}
					}
	            	checkPermissionMap.put(permission.getKpid(), che); 	            	
            	}
    		});		     	 
	    }
	    w.setParent(detail);
        detail.setParent(row);
        w.setId("AuthGridDetailWin"+num);
    }
    
    /**
	 * <li>功能描述：查询，根据文本框输入的资源名称，进行模糊查询事件处理函数。
	 * void 
	 * @author bobo
     * @throws DataAccessException 
     * @throws WrongValueException 
     * @throws ObjectNotExistException 
	 */
	public void onClick$search() throws WrongValueException, DataAccessException, ObjectNotExistException{	
		checkPermissionMap.clear();
		allResourceListMap.clear();
		checkIdMap.clear();
		detailIdMap.clear();
		
        if(searchResouceTextbox.getValue().trim()==null||searchResouceTextbox.getValue().trim().equals("")){
        	bulidAuthorizeGrid();
        }else{
        	List resultlist = new ArrayList();
        	reGridlist = iAuthManager.getResourceByUserIdAndWebsite(user.getUserId(),curWS.getKwId().intValue());
        	for(int j=0;j<reGridlist.size();j++){
        		Resource resou = (Resource)reGridlist.get(j);        		
        		List list = resourceManager.getResourceListByResourceIdsAndRName(resou.getId().toString(),searchResouceTextbox.getValue().trim());
        		if(list.size()>0){
        			resultlist.add((Resource)list.get(0));
        		}        			
        	}        	
            gridListModelList = new ListModelList(resultlist);
            authorizeGrid.setModel(gridListModelList);
        }         
        num=0;//点击查询后，grid中的checkboxb编号重新开始
	}
	
	 /**
	 * <li>功能描述：添加，左侧选中，右侧的list添加选中的记录；删除resourceList中重复元素	。
	 * void 
	 * @throws InterruptedException 
	 */
	public void addResouce() throws InterruptedException{
		//获取角色id,部门id,资源id	
		String key="";
		Map addMap = new HashMap();
		Role role = (Role)Sessions.getCurrent().getAttribute("roleID");
		Organization organiza = (Organization)Sessions.getCurrent().getAttribute("orgaID");
		if(role==null){
			Messagebox.show("请您先选择角色！", "提示", Messagebox.OK,Messagebox.INFORMATION);
			return;
		}
		for(int i=0;i<reList.size();i++){
			Resource re = (Resource)reList.get(i);			
            StringBuffer permissiones = new StringBuffer();
            if(permissionCheckList!=null){
        		for(int j=0;j<permissionCheckList.size();j++){
        			Permission permi = (Permission)permissionCheckList.get(j);        			
        			RoleDepResouAuth rda = new RoleDepResouAuth();        			
        			rda.setRoleId(role.getRoleId());
        			rda.setRoleName(role.getName()); 
        			if(organiza==null){
        				rda.setOrganizationId(0);
            			rda.setOrganizationname("");
        			}else{
        				rda.setOrganizationId(organiza.getOrganizationId());
            			rda.setOrganizationname(organiza.getName());
        			}        			        			
        			rda.setReourceId(re.getId());
        			rda.setReourceName(re.getName());        			
        			rda.setPermissionId(permi.getKpid());
        			rda.setPermissionName(permi.getName()); 
        			if(organiza==null){
        				key = re.getId()+","+role.getRoleId()+"," + 0 +"," +permi.getKpid();
        			}else{
        				key = re.getId()+","+role.getRoleId()+"," + organiza.getOrganizationId() +"," +permi.getKpid();
        			}        			
        			addMap.put(key, rda);
        			
        			//2011-2-16
        			Iterator iterat = checkPermissionMap.keySet().iterator();
      				while (iterat.hasNext()) { 
      				   Integer key0 = (Integer) iterat.next(); 
      				   Checkbox ch = (Checkbox)checkPermissionMap.get(key0);       				   
      				   if(ch.isChecked())      					   
         	    		  ch.setChecked(false); 
      				} 
        		}
        		
        		Map tempMap = new HashMap();        		 
        		if(allResourceListMap.get(re.getId())!=null){
        			 tempMap = allResourceListMap.get(re.getId());
        		}
        		addMap.putAll(tempMap);
        		allResourceListMap.put(re.getId(), addMap);//每一个资源下的记录(Map)
            } 
            
            permissionCheckList.clear();//每次清空该资源下的所选的许可
		}	
		
		Map smallMap = new HashMap();
		if((Map)allResourceListMap.get(((Resource)reList.get(0)).getId())!=null)
			smallMap = (Map)allResourceListMap.get(((Resource)reList.get(0)).getId());             				
		List<RoleDepResouAuth> newAddRDRAList = new ArrayList<RoleDepResouAuth>(); 
		Iterator iterator = smallMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key1 = (String)iterator.next(); 
			RoleDepResouAuth rdra = (RoleDepResouAuth)smallMap.get(key1);			  
			newAddRDRAList.add(rdra); 
		} 

		newAddRDRAList.addAll(datebaseList);			 
		List lastList = removeSameObject(newAddRDRAList);//过滤函数		
		resourceListModel = new ListModelList(lastList);		
		resourcesListbox.setModel(resourceListModel);		
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
	/**
	 * <li>功能描述：过滤重复的对象。
	 * return:relist
	 */	
	public List removeSameObject(List relist){
		 Map countMap = new HashMap();
		 for(int i=0;i<relist.size();i++){
			 int count = 0;
			 RoleDepResouAuth authi = (RoleDepResouAuth)relist.get(i);
			 String tr = authi.getReourceId()+","+ authi.getRoleId()+","+authi.getOrganizationId()+","+authi.getPermissionId();

			 for(int j=0;j<relist.size();j++){				 				 
				 RoleDepResouAuth authj = (RoleDepResouAuth)relist.get(j);
				 String temp = authj.getReourceId()+","+ authj.getRoleId()+","+authj.getOrganizationId()+","+authj.getPermissionId();

				 if(tr.trim().equals(temp.trim())){
					 count++;
				 }
				 if(count>1){
					 countMap.put(j, "au");
					 count --;
				 }
			 }
		 }
		
		 Map lmap = new HashMap();
		for(int k=0;k<relist.size();k++){
			lmap.put(k, relist.get(k));
		}
		
		Iterator iterator = countMap.keySet().iterator();
		while (iterator.hasNext()) { 
		   Integer key0 = (Integer) iterator.next(); 
		   lmap.remove(key0);	   
		    
		}
		
		Iterator iterator1 = lmap.keySet().iterator();
		relist.clear();
		while (iterator1.hasNext()) { 
		   Integer key0 = (Integer) iterator1.next();    
		   relist.add(lmap.get(key0)) ;
			}
		return relist;			
	}
	/**
	 * <li>功能描述：查询数据库，加载Grid数据
	 * void 
	 * @author bobo
	 * @param null
     * @throws DataAccessException 
     * @throws ObjectNotExistException 
	 */
    public void loadGrid() throws DataAccessException, ObjectNotExistException{ 
    	reGridlist = resourceManager.getResourceAll();
    	gridListModelList = new ListModelList(reGridlist);
        authorizeGrid.setModel(gridListModelList);
    }
	
    /**
	 * <li>功能描述：删除，删除右侧选中的记录	。
	 * @throws InterruptedException 
	 * @throws ObjectNotExistException 
	 * @throws DataAccessException 
	 */
	public void delResouce() throws InterruptedException, DataAccessException, ObjectNotExistException{	 
		if (resourcesListbox.getSelectedItem() == null){
               Messagebox.show("请您选择要删除的记录！", "提示", Messagebox.OK,Messagebox.INFORMATION);
               return;
		} else{ 
			    RoleDepResouAuth rdra = (RoleDepResouAuth) resourcesListbox.getSelectedItem().getValue();
		        String zuhe =rdra.getReourceId()+","+ rdra.getRoleId()+"," + rdra.getOrganizationId()+"," +rdra.getPermissionId();
		          
		       //如果是新添加的记录，删除新添加的列表记录			        
				Map smallMap = new HashMap();
				List tmplist = new ArrayList();
				if((Map)allResourceListMap.get(((Resource)reList.get(0)).getId())!=null)
					smallMap = (Map)allResourceListMap.get(((Resource)reList.get(0)).getId());             				
				List<RoleDepResouAuth> newAddRDRAList = new ArrayList<RoleDepResouAuth>(); 
				Iterator iterator = smallMap.keySet().iterator();
				while (iterator.hasNext()) {
					String key1 = (String)iterator.next(); 
					RoleDepResouAuth roleDepRe = (RoleDepResouAuth)smallMap.get(key1);			  
					newAddRDRAList.add(roleDepRe); 
				} 
				
		        for(int j=0;j<newAddRDRAList.size();j++){                            	                        	
			         RoleDepResouAuth temp = (RoleDepResouAuth)newAddRDRAList.get(j);
			         String zhtemp = temp.getReourceId()+","+ temp.getRoleId()+"," + temp.getOrganizationId()+"," +temp.getPermissionId();						         	
			         if(zuhe.trim().equals(zhtemp.trim())){
			        	 newAddRDRAList.remove(temp);
			        	 
			        	 Map newSmallMap = new HashMap();
			        	 if((Map)allResourceListMap.get(temp.getReourceId())!=null)
			        		 newSmallMap = (Map)allResourceListMap.get(temp.getReourceId()); 
			        	 newSmallMap.remove(zhtemp);
			        	 allResourceListMap.put(temp.getReourceId(), newSmallMap);
		            }
		        }
		        	
		        //如果选中的是数据库记录，从数据库中删除列表记录			        
				if(datebaseList!=null){	
					for(int k=0;k<datebaseList.size();k++){                            	                        	
				         RoleDepResouAuth temp = (RoleDepResouAuth)datebaseList.get(k);
				         String zhtemp = temp.getReourceId()+","+ temp.getRoleId()+"," + temp.getOrganizationId()+"," +temp.getPermissionId();
				         if(zuhe.trim().equals(zhtemp.trim())){					        	    
				        	    if (Messagebox.show("您确定要从数据库中彻底删除此条记录吗？", "提示", Messagebox.YES| Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES){
				        	    	datebaseList.remove(temp);
				        	    	iAuthManager.removeByRDP(temp.getRoleId(), temp.getOrganizationId(), temp.getPermissionId());
				        	    }
			                    break;
			            }
			        }
				}	
		        
				List laList = new ArrayList();
		        if(newAddRDRAList==null){
		        	laList = removeSameObject(datebaseList);//过滤函数
		        }else{
		        	newAddRDRAList.addAll(datebaseList);
		        	laList = removeSameObject(newAddRDRAList);//过滤函数
		        }				        
		        resourceListModel = new ListModelList(laList);
		        resourcesListbox.setModel(resourceListModel);
		}
        Events.postEvent(Events.ON_CHANGE, this, null);
	}
		
	/**
	 * <li>功能描述：资源授权保存
	 * void 
	 * @throws IllegalArgumentException 
	 * @throws ObjectAlreadyExistException 
	 * @throws DataAccessException 
	 * @throws InterruptedException 
	 * @throws ObjectNotExistException 
	 */
	public void onClick$save() throws DataAccessException, ObjectAlreadyExistException, IllegalArgumentException, InterruptedException, ObjectNotExistException{
		Map smallMap = new HashMap();
		Iterator iterator1 = allResourceListMap.keySet().iterator();
		while (iterator1.hasNext()) 
		{
			Integer key1 = (Integer)iterator1.next(); 
			smallMap = (Map)allResourceListMap.get(key1);
			List<RoleDepResouAuth> newAddRDRAList = new ArrayList<RoleDepResouAuth>(); 
			Iterator iterator = smallMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key2 = (String)iterator.next(); 
				RoleDepResouAuth rdra = (RoleDepResouAuth)smallMap.get(key2);			  
				newAddRDRAList.add(rdra); 
			}
			if(newAddRDRAList!=null){
				for(int k =0;k<newAddRDRAList.size();k++){
					RoleDepResouAuth roDepResAuth = (RoleDepResouAuth)newAddRDRAList.get(k);
					Auth au = new Auth();
					au.setKrId(roDepResAuth.getRoleId());
					au.setKdId(roDepResAuth.getOrganizationId());
					au.setKpId(roDepResAuth.getPermissionId());	
					au.setKaType(1);
					
					iAuthManager.removeByRDP(roDepResAuth.getRoleId(), roDepResAuth.getOrganizationId(), roDepResAuth.getPermissionId());
					iAuthManager.add(au);				
				}
				 
	        }
		}
		
		Messagebox.show("保存成功！", "提示", Messagebox.OK,Messagebox.INFORMATION);		 
		this.detach();		
		Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
		Sessions.getCurrent().removeAttribute("roleID");
		Sessions.getCurrent().removeAttribute("orgaID");
		checkPermissionMap.clear();
		allResourceListMap.clear();
		checkIdMap.clear();
		detailIdMap.clear();
	}
	
	/**
	 * <li>功能描述：关闭窗口
	 * void 
	 */
	 public void onClick$close(){
	    	this.detach();	    	
	    	Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
			Sessions.getCurrent().removeAttribute("roleID");
			Sessions.getCurrent().removeAttribute("orgaID");
			checkPermissionMap.clear();
			allResourceListMap.clear();
			checkIdMap.clear();
			detailIdMap.clear();
	    }
	 /**
	  * <li>功能描述：重置
	  * void 
	  */	
	 public void onClick$reset() throws DataAccessException, ObjectNotExistException{
		role.setValue(null);
	    depart.setValue(null);
	    searchResouceTextbox.setValue(null);
	    Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
		Sessions.getCurrent().removeAttribute("roleID");
		Sessions.getCurrent().removeAttribute("orgaID");
		
		//加载Grid
		bulidAuthorizeGrid();
		resourceListModel = new ListModelList();
        resourcesListbox.setModel(resourceListModel);
        
		num=0;//点击重置后，grid中的checkboxb编号重新开始
		
		checkPermissionMap.clear();
		allResourceListMap.clear();
		checkIdMap.clear();
		detailIdMap.clear();
	 }
	 /**
		 * <li>功能描述：加载左侧角色树
		 * void 
		 * @author bobo
		 * @param null
		 */
   public void bulidRoleTree(){
	   roletree = (RolegroupTree) this.getFellow("roleData");
       roletree.setLabelProvider(new RoleTreeLabelProvider());
       RoletreeContentProvider treeContent = new RoletreeContentProvider();
       roletree.setContentProvider(treeContent);
       roletree.rebuildTree();      
       initTree();
   }
   
   /**
	 * <li>功能描述：加载左侧部门树
	 * void 
	 * @author bobo
	 * @param null
	 */
   public void bulidOrganizeTree(){
	   organizeTree = (OrganizationTree) this.getFellow("organizeData");
	   organizeTree.setLabelProvider(new OrganizationLabelProvider());
	   organizeTree.setContentProvider(new OrganizationThirdContentProvider());
	   organizeTree.rebuildTree();
   }
   /**
	 * <li>功能描述：角色树展开
	 * void 
	 * @author bobo
	 * @param null
	 */
    public void initTree(){
        Collection<Treeitem> items = (Collection<Treeitem>) roletree.getItems();
        for (Treeitem item : items){
            item.setOpen(false);
            if (((Role) item.getValue()).getpId() == 0){
                item.setOpen(true);
            } else{
                item.setOpen(false);
            }
        }
    }
    
   
}
