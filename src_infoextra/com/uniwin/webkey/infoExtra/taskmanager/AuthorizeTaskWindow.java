package com.uniwin.webkey.infoExtra.taskmanager;

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
import org.zkoss.zul.Button;
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

import com.uniwin.webkey.cms.model.WkTChanel;
import com.uniwin.webkey.common.exception.DataAccessException;
import com.uniwin.webkey.common.exception.ObjectAlreadyExistException;
import com.uniwin.webkey.common.exception.ObjectNotExistException;
import com.uniwin.webkey.core.itf.IAuthManager;
import com.uniwin.webkey.core.itf.IOrganizationManager;
import com.uniwin.webkey.core.itf.IPermissionManager;
import com.uniwin.webkey.core.itf.IRoleManager;
import com.uniwin.webkey.core.model.Auth;
import com.uniwin.webkey.core.model.Organization;
import com.uniwin.webkey.core.model.Permission;
import com.uniwin.webkey.core.model.RoDepResChanelAuth;
import com.uniwin.webkey.core.model.Role;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.itf.TreeService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.tree.ui.OrganizationTree;
import com.uniwin.webkey.tree.ui.RoleTreeLabelProvider;
import com.uniwin.webkey.tree.ui.RolegroupTree;
import com.uniwin.webkey.tree.ui.RoletreeContentProvider;
import com.zkoss.org.messageboxshow.MessageBox;

public class AuthorizeTaskWindow extends Window implements AfterCompose{

	
	Users user = (Users) Sessions.getCurrent().getAttribute("users");
	//传递得“当前栏目名称”,"当前栏目ID"
	String tasklName = (String)Executions.getCurrent().getArg().get("taskName");
	int taskId = (Integer)Executions.getCurrent().getArg().get("taskId");
	//记录数据库数据
	private List<RoDepResChanelAuth> 	datebaseList = new ArrayList<RoDepResChanelAuth>();
	
	 //role角色接口
    private IRoleManager 			roleManager =  (IRoleManager) SpringUtil.getBean("roleManager");
    
    // organize组织接口
	private IOrganizationManager 	organizationManager = (IOrganizationManager) SpringUtil.getBean("organizationManager");
	
	 //auth授权接口
	private IAuthManager  			iAuthManager =  (IAuthManager) SpringUtil.getBean("authManager");
	
	 //permission许可接口
	private IPermissionManager 		iPermissionManager =  (IPermissionManager) SpringUtil.getBean("permissionManager");
	
	//控制栏目的checkbox的勾选
	private Map 	checkIdMap = new HashMap();
	
	//控制栏目的detail的开合
	private Map 	detailIdMap = new HashMap();
	 // 角色list
    private List<Role>         		rolesList;
    // 部门list
    private List organizationData; 
    //角色树
    private RolegroupTree   roletree;
    //部门树
    private OrganizationTree   organizeTree;
    
    private  Map 	checkPermissionMap = new HashMap();	
    private  Map<Integer,Map> 					allChanelListMap = new HashMap();
  //存放选择资源下许可
	private  List<Permission> 					permissionCheckList = new ArrayList<Permission>(); 
	
	//加载中间grid的数据
	private  List 					reGridlist = new ArrayList();
	//Modellist模型
    private ListModelList 			gridListModelList;  
  //存放中间grid选中的resource
	private  List<Object>		reList = new ArrayList<Object>();
	
    private Label 					role; 
    private Listbox 				taskListbox;
	private ListModelList 			chanelListModel;
	private Grid 					authorizeGrid; 
	private Button 				    addChanel,delChanel,save,reset,close;
	private Textbox searchTaskTextbox;
	
	private TaskService taskService=(TaskService)org.zkoss.spring.SpringUtil.getBean("taskService");
	private TreeService treeService=(TreeService)org.zkoss.spring.SpringUtil.getBean("treeService");
	//设置Grid的detail不同id
    int num = 0;
    String name = null;
  	Integer id = 0;
    
	public void afterCompose() {
		// TODO Auto-generated method stub
		Components.wireVariables(this, this);
        Components.addForwards(this, this);   
        
        try {
        	rolesList = roleManager.getRoleListAllOrderbyRoleId();
		} catch (DataAccessException e1) {
			e1.printStackTrace();
		}
		
		try {
			organizationData = organizationManager.getOrganizationListAllOrderbyOrganizationId();
		} catch (DataAccessException e2) {
			e2.printStackTrace();
		}
        
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
		taskListbox.setItemRenderer(new ListitemRenderer(){
	        public void render(Listitem item, Object data) throws Exception{
        	    RoDepResChanelAuth redep = (RoDepResChanelAuth) data;	        		
                item.setValue(redep);
                item.setHeight("25px");
                Listcell c = new Listcell(redep.getChanelName());	                
                Listcell c1 = new Listcell(redep.getRoleName());
                Listcell c3 = new Listcell(redep.getPermissionName());
              
                item.appendChild(c);item.appendChild(c1);
                item.appendChild(c3);
	        }
	     });
        
	}

	
	/**
	 * <li>功能描述：查询，根据文本框输入的栏目名称，进行模糊查询事件处理函数。
	 * void 
	 * @author bobo
     * @throws DataAccessException 
     * @throws WrongValueException 
     * @throws ObjectNotExistException 
	 */
	public void onClick$search() throws WrongValueException, DataAccessException, ObjectNotExistException{
		checkPermissionMap.clear();
		allChanelListMap.clear();
		checkIdMap.clear();
		detailIdMap.clear();
		
        if(searchTaskTextbox.getValue().trim()==null||searchTaskTextbox.getValue().trim().equals("")){
        	bulidAuthorizeGrid();
        }else{
        	List resultlist = new ArrayList();
//        	reGridlist =  commentsService.getChildNews(Long.valueOf(0), curWS.getKwId());
        	Object obj;
        	List<Object> list;
        	for(int j=0;j<reGridlist.size();j++){
        		obj=(Object)reGridlist.get(j);
        		list = treeService.getTaskListByCName(obj,searchTaskTextbox.getValue().trim());
        		if(list.size()>0){
        			resultlist.add(list.get(0));
        		} 
        	  
        	}        	
            gridListModelList = new ListModelList(resultlist);
            authorizeGrid.setModel(gridListModelList);
        }         
        num=0;//点击查询后，grid中的checkboxb编号重新开始
	}
	
	
	
	/**
	 * <li>功能描述：栏目授权保存
	 * void 
	 * @throws IllegalArgumentException 
	 * @throws ObjectAlreadyExistException 
	 * @throws DataAccessException 
	 * @throws InterruptedException 
	 * @throws ObjectNotExistException 
	 */
	public void onClick$save() throws DataAccessException, ObjectAlreadyExistException, IllegalArgumentException, InterruptedException, ObjectNotExistException{				
		Map smallMap = new HashMap();
		Iterator iterator1 = allChanelListMap.keySet().iterator();
		while (iterator1.hasNext()) 
		{
			Integer key1 = (Integer)iterator1.next(); 
			smallMap = (Map)allChanelListMap.get(key1);
			List<RoDepResChanelAuth> newAddRDRAList = new ArrayList<RoDepResChanelAuth>(); 
			Iterator iterator = smallMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key2 = (String)iterator.next(); 
				RoDepResChanelAuth rdra = (RoDepResChanelAuth)smallMap.get(key2);			  
				newAddRDRAList.add(rdra); 
			}
			if(newAddRDRAList!=null){
				for(int k =0;k<newAddRDRAList.size();k++){
					RoDepResChanelAuth roDepResAuth = (RoDepResChanelAuth)newAddRDRAList.get(k);
					Auth au = new Auth();
					au.setKrId(roDepResAuth.getRoleId());
					au.setKdId(0);
					au.setKpId(roDepResAuth.getPermissionId());	
					au.setKaType(2);
					
					iAuthManager.removeByRDP(roDepResAuth.getRoleId(), roDepResAuth.getOrganizationId(), roDepResAuth.getPermissionId());
					iAuthManager.add(au);					
				}
				 
	        }
		}
		
		MessageBox.showInfo("授权保存成功！");
		
		this.detach();		
		Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
		Sessions.getCurrent().removeAttribute("roleID");
		Sessions.getCurrent().removeAttribute("orgaID");
		checkPermissionMap.clear();
		allChanelListMap.clear();
		checkIdMap.clear();
		detailIdMap.clear();
	}
	
	
	
	/**
	 *<li>功能描述：关闭窗口
	 * void 
	 */	
	 public void onClick$close(){
	    	this.detach();
	    	Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
			Sessions.getCurrent().removeAttribute("roleID");
			Sessions.getCurrent().removeAttribute("orgaID");
			checkPermissionMap.clear();
			allChanelListMap.clear();
			checkIdMap.clear();
			detailIdMap.clear();
	    }
	
	
	 /**
	  * <li>功能描述：重置
	  * void 
	  */	
	 public void onClick$reset() throws DataAccessException, ObjectNotExistException{		 
		role.setValue(null);
//		depart.setValue(null);
		searchTaskTextbox.setValue(null);
		 
		Sessions.getCurrent().removeAttribute("permissionCheckList");//清空该session
		Sessions.getCurrent().removeAttribute("roleID");
		Sessions.getCurrent().removeAttribute("orgaID");		
		
		//加载Grid
		bulidAuthorizeGrid();			
		chanelListModel = new ListModelList();
		taskListbox.setModel(chanelListModel);
		
		num=0;//点击重置后，grid中的checkboxb编号重新开始			
		checkPermissionMap.clear();
		allChanelListMap.clear();
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
       this.rolesList = treeContent.getResources();
       roletree.rebuildTree();      
       initTree();
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
   
   /**
    * <li>功能描述：初始化栏目授权窗体的中间和右侧部分
    * @throws DataAccessException
    * @throws ObjectNotExistException
    */
   public void iniWinCenterAndLeft() throws DataAccessException, ObjectNotExistException{
		//根据栏目id,查找数据库auth表，加载右侧的listbox
		 Map inibaseMap = new HashMap();
		 String key="";        					
		 List authlist = new ArrayList();
		 authlist = iAuthManager.getAuthByRID(String.valueOf(taskId), "2");
		 
		 //防止第一次不选栏目的勾，就许可
		 WkTChanel chan = (WkTChanel)treeService.get(WkTChanel.class, Long.valueOf(taskId+""));
		 WkTExtractask extractask=(WkTExtractask)taskService.get(WkTExtractask.class, Long.valueOf(taskId+""));
		 if(chan!=null){
			 reList.add(chan);
		 }else if(extractask!=null){
			 reList.add(extractask);
		 }
		 
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
			
			RoDepResChanelAuth rda = new RoDepResChanelAuth();        			
			rda.setRoleId(roleId);
			rda.setRoleName(roleAu.getName());        			
			rda.setOrganizationId(organId);
			rda.setOrganizationname(orgaAu.getName());        			
			rda.setChanelId(taskId);
			rda.setChanelName(tasklName);        			
			rda.setPermissionId(permiId);
			rda.setPermissionName(permiAu.getName());
			             	        		
			key = taskId+","+roleId+"," + organId +"," +permiId;   
			inibaseMap.put(key, rda);
		}
		if(!inibaseMap.isEmpty()){
			Iterator tp = inibaseMap.keySet().iterator();
			while (tp.hasNext()) { 
					String ke = (String) tp.next(); 
					RoDepResChanelAuth rdra = (RoDepResChanelAuth)inibaseMap.get(ke);			  
					datebaseList.add(rdra); 
			}
		}
		chanelListModel = new ListModelList(datebaseList);
		taskListbox.setModel(chanelListModel);
   }
   
   
   /**
	 * <li>功能描述：Grid的detail进行润色处理
	 * @param null
    * @throws DataAccessException 
    * @throws ObjectNotExistException 
	 */
   public void bulidAuthorizeGrid() throws DataAccessException, ObjectNotExistException{
   	authorizeGrid.setRowRenderer(new RowRenderer(){
           public void render(final Row row, final Object data) throws Exception{
           	if(data == null) return;
           	
           	if(data instanceof WkTExtractask){
           		WkTExtractask e=(WkTExtractask)data;
           		name=e.getKeName();
           		id=e.getKeId().intValue();
           	}else if(data instanceof WkTChanel){
           		WkTChanel t=(WkTChanel)data;
           		name=t.getKcName();
           		id=t.getKcId().intValue();
           	}
           	final Detail detail = new Detail(); 
               num++;
               detail.setId("chanelDdetail"+id+num); //--------------2011-5-11
               detail.setOpen(false);
               detail.setParent(row);
               detailWindow(row,data, detail);//调用新页面生成窗口函数
        		
           	final Checkbox check = new Checkbox();
           	check.setId("chanelCheck"+id+num);//-------------------2011-5-11
//           	check.setLabel(num+"");
               check.setParent(row);  
               check.setChecked(false);
               
               Label label = new Label();
               label.setValue(name);
               label.setParent(row);
               row.setId("row"+id+num);//----------------------------2011-5-11
               
               checkIdMap.put(check.getId(), false);
               detailIdMap.put(detail.getId(), false);
               
               DetailOpenEventListener(check,row,detail);                
               RowOnClickEventListener(check,row,detail); 
                               
               //Checkbox组件事件，
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
       					 reList.add(data);  
       					 
       					//根据资源id,查找该资源对应的数据库数据
       					 String key="";        					
            				 List authlist = iAuthManager.getAuthByRID(String.valueOf(id), "2");
            				 for(int k=0;k<authlist.size();k++){
            					Auth auth = (Auth)authlist.get(k);
            					int roleId = auth.getKrId();
            					int organId = auth.getKdId();
            					int permiId = auth.getKpId();             					
            					Role roleAu = roleManager.get(roleId);
            					Organization orgaAu = organizationManager.get(organId);
            					Permission  permiAu = iPermissionManager.get(permiId);        					
            					
            					RoDepResChanelAuth rdc = new RoDepResChanelAuth();        			
            					rdc.setRoleId(roleId);
            					rdc.setRoleName(roleAu.getName());        			
            					rdc.setOrganizationId(organId);
            					rdc.setOrganizationname(orgaAu.getName());        			
            					rdc.setChanelId(id);
            					rdc.setChanelName(name);        			
            					rdc.setPermissionId(permiId);
            					rdc.setPermissionName(permiAu.getName());
                    			             	        		
                    			key = id+","+roleId+"," + organId +"," +permiId;   
                    			base.put(key, rdc);
            				}
            				if(!base.isEmpty()){
            					Iterator tp = base.keySet().iterator();
            					while (tp.hasNext()) { 
         							String ke = (String) tp.next(); 
         							RoDepResChanelAuth rdra = (RoDepResChanelAuth)base.get(ke);			  
         							datebaseList.add(rdra); 
            					}
            				}
            				
            				Map smallMap = new HashMap();
            				Object o=(Object)reList.get(0);
            				Integer id2=0;
            				if(o instanceof WkTExtractask){
            					WkTExtractask e=(WkTExtractask)o;
            					id2=e.getKeId().intValue();
            				}else if(o instanceof WkTChanel){
            					WkTChanel t=(WkTChanel)o;
            					id2=t.getKcId().intValue();
            				}
            				if((Map)allChanelListMap.get(id2)!=null)
            					smallMap = (Map)allChanelListMap.get(id2);             				
            				List<RoDepResChanelAuth> newAddRDRAList = new ArrayList<RoDepResChanelAuth>(); 
            				Iterator iterator = smallMap.keySet().iterator();
            				while (iterator.hasNext()) {
            						String key1 = (String)iterator.next(); 
            						RoDepResChanelAuth rdrc = (RoDepResChanelAuth)smallMap.get(key1);			  
            						newAddRDRAList.add(rdrc); 
            				} 
            				
            				//新添加的数据   +数据库
            				newAddRDRAList.addAll(datebaseList);
            				List lastList = removeSameObject(newAddRDRAList);//过滤函数             				
            				chanelListModel = new ListModelList(lastList);
            				taskListbox.setModel(chanelListModel);
           			    
           			    
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
       					chanelListModel = new ListModelList();
       					taskListbox.setModel(chanelListModel);             			    
       					reList.remove(data); 						
       				}
       			}
       		});
           }            
       });
       
       loadGrid();   
       Events.postEvent(Events.ON_CHANGE, this, null);
   }  
   
   /**
	 * <li>功能描述：grid下detail新页面生成窗口函数
	 * void 
	 * @author bobo
	 * @param row grid的行,ch 栏目实体,detail grid的detail
    * @throws DataAccessException 
	 */
   public void detailWindow(Row row,Object ch, Detail detail) throws DataAccessException{
	   		int chanelId = 0;
	   		if(ch instanceof WkTExtractask){
	   		    WkTExtractask e=(WkTExtractask)ch;
	   		    chanelId=e.getKeId().intValue();
	   		}else if(ch instanceof WkTChanel){
	   			WkTChanel t=(WkTChanel)ch;
	   			chanelId=t.getKcId().intValue();
	   		}
	       
	        final List permissionList = iPermissionManager.getPermissionByKridAndKrtype(String.valueOf(chanelId),"2");                              
	    	final org.zkoss.zul.Window w = (org.zkoss.zul.Window)Executions.createComponents("/apps/core/authorizeResourceDetail.zul",null,null);   //向a_authorizeDetail.zul页面，传递当前栏目所拥有的许可
	    			    	
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
       w.setId("AuthChanelDetailWin"+num);
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
	 * <li>功能描述：过滤重复的对象。
	 * return:relist
	 */	
	public List removeSameObject(List relist){
		 Map countMap = new HashMap();
		 for(int i=0;i<relist.size();i++){
			 int count = 0;
			 RoDepResChanelAuth authi = (RoDepResChanelAuth)relist.get(i);
			 String tr = authi.getChanelId()+","+ authi.getRoleId()+","+authi.getOrganizationId()+","+authi.getPermissionId();
			 for(int j=0;j<relist.size();j++){				 				 
				 RoDepResChanelAuth authj = (RoDepResChanelAuth)relist.get(j);
				 String temp = authj.getChanelId()+","+ authj.getRoleId()+","+authj.getOrganizationId()+","+authj.getPermissionId();
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
	 * <li>功能描述：查询数据库，获取当前站点下的所有栏目，加载Grid数据
	 * void 
	 * @author bobo
	 * @param null
     * @throws DataAccessException 
     * @throws ObjectNotExistException 
	 */
    public void loadGrid() throws DataAccessException, ObjectNotExistException{ 
//    	reGridlist = chanelService.getChanelByKwid(curWS.getKwId());
    	List list1=taskService.findAllTask();
    	List list2=treeService.findAll();
    	reGridlist.addAll(list1);
    	reGridlist.addAll(list2);
    	gridListModelList = new ListModelList(reGridlist);
        authorizeGrid.setModel(gridListModelList);
    }
	
    /**
	 * <li>功能描述：添加，左侧选中，右侧的list添加选中的记录；删除resourceList中重复元素	。
	 * void 
	 * @throws InterruptedException 
	 */
	public void addTask() throws InterruptedException{
	    //获取角色id,部门id,栏目id	 
		String key="";
		Map addMap = new HashMap();
		Role role = (Role)Sessions.getCurrent().getAttribute("roleID");
		Organization organiza = (Organization)Sessions.getCurrent().getAttribute("orgaID");
		if(role==null){
			Messagebox.show(org.zkoss.util.resource.Labels
                    	.getLabel("chanel.ui.authChanelAuthselectRole"), 
                    org.zkoss.util.resource.Labels
                    	.getLabel("chanel.ui.messagepoint"), Messagebox.OK,Messagebox.INFORMATION);
			return;
		}
		Integer id3=0;String name3=null;
		for(int i=0;i<reList.size();i++){
			Object ch = (Object)reList.get(i);
			if(ch instanceof WkTExtractask){
				WkTExtractask e=(WkTExtractask)ch;
				id3=e.getKeId().intValue();
				name3=e.getKeName();
			}else if(ch instanceof WkTChanel){
				WkTChanel t=(WkTChanel)ch;
				id3=t.getKcId().intValue();
				name3=t.getKcName();
			}
            StringBuffer permissiones = new StringBuffer();
            if(permissionCheckList!=null){
        		for(int j=0;j<permissionCheckList.size();j++){
        			Permission permi = (Permission)permissionCheckList.get(j); 
        			
        			RoDepResChanelAuth rda = new RoDepResChanelAuth();        			
        			rda.setRoleId(role.getRoleId());
        			rda.setRoleName(role.getName()); 
        			if(organiza==null){
        				rda.setOrganizationId(0);
            			rda.setOrganizationname("");
        			}else{
        				rda.setOrganizationId(organiza.getOrganizationId());
            			rda.setOrganizationname(organiza.getName());
        			}        			        			
        			rda.setChanelId(id3);
        			rda.setChanelName(name3);        			
        			rda.setPermissionId(permi.getKpid());
        			rda.setPermissionName(permi.getName());   
        			if(organiza==null){
        				key = id3+","+role.getRoleId()+"," + 0 +"," +permi.getKpid();
        			}else{
        				key = id3+","+role.getRoleId()+"," + organiza.getOrganizationId() +"," +permi.getKpid();
        			}        			
        			addMap.put(key, rda);
        			
        			//2011-2-16
        			Iterator iterat = checkPermissionMap.keySet().iterator();
      				while (iterat.hasNext()) { 
      				   Integer key0 = (Integer) iterat.next(); 
      				   Checkbox che = (Checkbox)checkPermissionMap.get(key0);       				   
      				   if(che.isChecked())
         	    			che.setChecked(false); 
      				} 
        		}
        		
        		Map tempMap = new HashMap();        		 
        		if(allChanelListMap.get(id3)!=null){
        			 tempMap = allChanelListMap.get(id3);
        		}
        		addMap.putAll(tempMap);
        		allChanelListMap.put(id3, addMap);//每一个资源下的记录(Map)
            } 
            
            permissionCheckList.clear();//每次清空该栏目下的所选的许可
		}	
		Map smallMap = new HashMap();
		if((Map)allChanelListMap.get(id3)!=null)
			smallMap = (Map)allChanelListMap.get(id3);
		List<RoDepResChanelAuth> newAddRDRAList = new ArrayList<RoDepResChanelAuth>(); 
		Iterator iterator = smallMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key1 = (String)iterator.next(); 
			RoDepResChanelAuth rdra = (RoDepResChanelAuth)smallMap.get(key1);
			newAddRDRAList.add(rdra); 
		} 

		newAddRDRAList.addAll(datebaseList);			 
		List lastList = removeSameObject(newAddRDRAList);//过滤函数	
		
		chanelListModel = new ListModelList(lastList);
		taskListbox.setModel(chanelListModel);
		Events.postEvent(Events.ON_CHANGE, this, null);
	}
    
	/**
	 * <li>功能描述：删除右侧选中的记录	。
	 * @throws InterruptedException 
	 * @throws ObjectNotExistException 
	 * @throws DataAccessException 
	 */
	public void delTask() throws InterruptedException, DataAccessException, ObjectNotExistException{	
        if (taskListbox.getSelectedItem() == null){
                Messagebox.show(org.zkoss.util.resource.Labels
                    	.getLabel("chanel.ui.selectDeleteChanelRecord"), 
                    	org.zkoss.util.resource.Labels
                    	.getLabel("chanel.ui.messagepoint"), Messagebox.OK,Messagebox.INFORMATION);
                return;
 		} else{ 
 			RoDepResChanelAuth rdra = (RoDepResChanelAuth) taskListbox.getSelectedItem().getValue();
	        String zuhe =rdra.getChanelId()+","+ rdra.getRoleId()+"," + rdra.getOrganizationId()+"," +rdra.getPermissionId();
	        
	       //如果是新添加的记录，删除新添加的列表记录				        
			Map smallMap = new HashMap();
			List tmplist = new ArrayList();
			Integer taskId=0;
			if(reList.get(0) instanceof WkTChanel){
				WkTChanel chanel=(WkTChanel)reList.get(0);
				taskId=chanel.getKcId().intValue();
			}else if(reList.get(0) instanceof WkTExtractask){
				WkTExtractask extractask=(WkTExtractask)reList.get(0);
				taskId=extractask.getKeId().intValue();
			}
			
//			if((Map)allChanelListMap.get(((WkTChanel)reList.get(0)).getKcId().intValue())!=null)
			if((Map)allChanelListMap.get(taskId)!=null)
				smallMap = (Map)allChanelListMap.get(((WkTExtractask)reList.get(0)).getKeId().intValue());             				
			List<RoDepResChanelAuth> newAddRDRAList = new ArrayList<RoDepResChanelAuth>(); 
			Iterator iterator = smallMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key1 = (String)iterator.next(); 
				RoDepResChanelAuth roleDepRe = (RoDepResChanelAuth)smallMap.get(key1);			  
				newAddRDRAList.add(roleDepRe); 
			} 
			
	        for(int j=0;j<newAddRDRAList.size();j++){                            	                        	
	        	 RoDepResChanelAuth temp = (RoDepResChanelAuth)newAddRDRAList.get(j);
		         String zhtemp = temp.getChanelId()+","+ temp.getRoleId()+"," + temp.getOrganizationId()+"," +temp.getPermissionId();						         	
		         if(zuhe.trim().equals(zhtemp.trim())){
		        	 newAddRDRAList.remove(temp);
		        	 
		        	 Map newSmallMap = new HashMap();
		        	 if((Map)allChanelListMap.get(temp.getChanelId())!=null)
		        		 newSmallMap = (Map)allChanelListMap.get(temp.getChanelId()); 
		        	 newSmallMap.remove(zhtemp);
		        	 allChanelListMap.put(temp.getChanelId(), newSmallMap);
	            }
	        }
	        	
	        //如果选中的是数据库记录，从数据库中删除列表记录				        
			if(datebaseList!=null){	
				for(int k=0;k<datebaseList.size();k++){                            	                        	
					 RoDepResChanelAuth temp = (RoDepResChanelAuth)datebaseList.get(k);
			         String zhtemp = temp.getChanelId()+","+ temp.getRoleId()+"," + temp.getOrganizationId()+"," +temp.getPermissionId();
			         if(zuhe.trim().equals(zhtemp.trim())){					        	    
			        	    if (Messagebox.show(org.zkoss.util.resource.Labels
			                        .getLabel("chanel.ui.reallyDeleteChanelFromDabase"),org.zkoss.util.resource.Labels
			                        .getLabel("chanel.ui.messagepoint"), Messagebox.YES| Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES){
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
	        chanelListModel = new ListModelList(laList);
	        taskListbox.setModel(chanelListModel);
 		}
        Events.postEvent(Events.ON_CHANGE, this, null);
	}
	
    
   
}
