package com.uniwin.webkey.infoExtra.test;

import org.zkoss.zhtml.Object;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import com.uniwin.webkey.common.exception.DataAccessException;

public class TestNum1 extends Window implements AfterCompose{

	
	private static final long serialVersionUID = 1L;
	Textbox key;
	Iframe iframe;
	Listitem baidu;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this,this);
		
//		totalList=orinewsService.getNewsOfOrinfo(76L);
//		pp.setTotalSize(totalList.size());
		
//		try {
//			redraw(76L,0,pp.getPageSize());
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//		}
		
//		pp.addEventListener("onPaging", new EventListener(){
//			public void onEvent(Event event) throws Exception {
//				PagingEvent pe = (PagingEvent) event;  
//				int pgno = pe.getActivePage();// 页数(从零计算)  
////				int start = pgno * pp.getPageSize();  
//		        redraw(76L,pgno, pp.getPageSize());  
//
//			}
//
//		});
	}

//	private void redraw(Long keid, int start, int pageSize) throws DataAccessException {
//		// TODO Auto-generated method stub
//		int count=start * pageSize;
//		this.s=count;
//		List sList=pageTemp.getListByPage(76L,start, pageSize);
//		listModelList=new ListModelList(sList);
//		searchList.setModel(listModelList);
//		
//		searchList.setItemRenderer(new ListitemRenderer(){
//
//			public void render(Listitem item, Object data) throws Exception {
//				WkTOrinfo orinfo=(WkTOrinfo)data;
//				item.setValue(orinfo);
//				Listcell c1=new Listcell(s+1+"");
//				Listcell c2=new Listcell(orinfo.getKoiTitle());
//				item.appendChild(c1);item.appendChild(c2);
//				s++;
//				System.out.println("!!! "+item.getIndex()+1);
//			}
//			
//		});
//	}
	
	public void onClick$searchnews() throws DataAccessException{
		iframe.setSrc("http://www.baidu.com/s?wd="+key.getValue());
	}
	
	public void onClick$resetnews() throws DataAccessException{
		
		key.setValue("");
	}
	
}
