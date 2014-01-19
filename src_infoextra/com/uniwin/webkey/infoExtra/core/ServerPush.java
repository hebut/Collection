package com.uniwin.webkey.infoExtra.core;
/**
 * 结果暂存里的动态显示
 */
import java.util.List;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;

import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.task.Reader;


public class ServerPush extends Thread{

	private Tabbox _box;
	private Listcell cListcell;
	private final Desktop _desktop;
	private boolean _ceased;
	private Listbox _lisListbox;
	private WkTExtractask _extract;
	private List<WkTPickreg> _pList;
	private List<WkTGuidereg> _gList;
	String _encond;
	LinkCollection linkCollection;
	
	public ServerPush(Tabbox box){
		this._box=box;
		_desktop=box.getDesktop();
	}
	
	 public void run() {
		 
		 infoPick pick;
		 String[] extractResult;
		 String visitUrl;
		 int count=linkCollection.getunVisitedUrlNum();
		 while ( !_ceased && !linkCollection.unVisitedUrlsEmpty() && linkCollection.getVisitedUrlNum()<=1000) {
		   
			/*try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} */
		 try {
			 	
			 Executions.activate(_desktop);
			
		 } catch (DesktopUnavailableException e) {
				e.printStackTrace();
		 } catch (InterruptedException e) {
				System.out.println("中断");
		 }
		    try {
		    
		    visitUrl=linkCollection.unVisitedUrlDeQueue();
			extractResult=new String[_pList.size()];
		    pick=new infoPick();
			extractResult=pick.extractByTags(visitUrl,_extract,_encond,_pList,_gList.get(_gList.size()-1));
			linkCollection.addVisitedUrl(visitUrl);
		    
			if(extractResult!=null && !extractResult.equals("")){
				Listitem item=new Listitem();
				item.setHeight("20px");
			    Listcell cell;
			     for(int i=0;i<_pList.size();i++){
			    	 cell=new Listcell();
			    	 if(extractResult[i]!=null && extractResult[i].trim().length()>=40){
							String cc=extractResult[i].substring(0, 40)+"...";
							cell.setLabel(cc);
							cell.setTooltiptext(extractResult[i]);
							cell.setValue(extractResult[i]);
						}else{
							if(extractResult[i]!=null){
								cell.setLabel(extractResult[i]);
								cell.setValue(extractResult[i]);
							}
						}
			    	 item.appendChild(cell);
			    	 
			    	 final String cellValue;
						if(cell.getValue()==null){
							cellValue="";
						}else{
							cellValue=cell.getValue().toString();
						}
						
						cell.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener(){
							public void onEvent(Event arg0) throws Exception {
								Reader reader=(Reader)Executions.createComponents("/apps/infoExtra/content/task/reader.zul", null, null);
								reader.initWindow(cellValue);
								reader.doHighlighted();
							}
							
						});
			    	 
			     }
			     _lisListbox.appendChild(item);
			}
			
		     
		    } catch (RuntimeException ex) {
		     throw ex;
		    } catch (Error ex) {
		     throw ex;
		    } finally {
		     count--;	
		     if(count==0){
		    	 cListcell.setStyle("color:red");
		    	 cListcell.setLabel(_extract.END);
		     }
		     Executions.deactivate(_desktop);
		    }
		   }//while
		 
		 linkCollection.getUnVisitedUrl().deleteAll();
		 linkCollection.getVisitedUrl().clear();
		 System.out.println("采集结束！");
		
	}
		
	 
	 public void setDone(){
		  _ceased = true;
	}
	 
	public void initData(LinkCollection linkCollection,String encond,WkTExtractask extractask,List<WkTPickreg> pList,List<WkTGuidereg> gList,Listcell cell) {
		
		this.linkCollection=linkCollection;
		this._encond=encond;
		this._extract=extractask;
		this._pList=pList;
		this._gList=gList;
		this.cListcell=cell;
		Executions.getCurrent().getDesktop().enableServerPush(true);
		Tabpanels  tabpanels=(Tabpanels)_box.getTabpanels();
		Tabpanel tabpanel=(Tabpanel) tabpanels.getFirstChild();
		Listbox lbox=(Listbox)tabpanel.getFirstChild();
		this._lisListbox=lbox;
	}
		
}
