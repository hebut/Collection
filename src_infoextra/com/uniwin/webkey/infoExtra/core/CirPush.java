package com.uniwin.webkey.infoExtra.core;
/**
 * 循环采集
 */
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
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

import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.task.Reader;
import com.uniwin.webkey.infoExtra.util.HtmlTags;


public class CirPush extends Thread{

	private Tabbox _box;
	private final Desktop _desktop;
	private boolean _ceased;
	private Listbox _lisListbox;
	private List<WkTPickreg> _pList;
	private List<WkTGuidereg> _gList;
	String _encond;
	String _gCirculate;
	public CirPush(Tabbox box){
		this._box=box;
		_desktop=box.getDesktop();
	}
	
	public void run(){
		
//		CirPick cPick;
		String u;
		String[] extractResult;
		Parser parser;
		String source = null;
		 while ( !_ceased && !LinkCollection.unVisitedUrlsEmpty() && LinkCollection.getVisitedUrlNum()<=1000) {
			 
			 u=LinkCollection.unVisitedUrlDeQueue();
			 
			 try {
					Executions.activate(_desktop);
				} catch (DesktopUnavailableException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			    try {
//			    cPick=new CirPick();
//			    CirEntity cirEntity;
//				List<CirEntity> cirList=cPick.cirExtract(u,_gCirculate,_pList);
				LinkCollection.addVisitedUrl(u);
				
				try {
					parser=new Parser(u);
					NodeList list=parser.parse(null);
					source=list.toHtml();
				} catch (ParserException e) {
					System.out.println("循环解析失败！");
				}
				source=source.replaceAll("\r|\n","");
				String ss;
				Integer b=0,e=0;
				while((source.indexOf(_gCirculate)!=-1) && (source.indexOf(_gCirculate, source.indexOf(_gCirculate)+_gCirculate.length())!=-1)){
					b=source.indexOf(_gCirculate);
					e=source.indexOf(_gCirculate,b+_gCirculate.length());
					ss=source.substring(b,e);
					source=source.substring(e,source.length());
					extractResult=extractCir(u,source,_pList);
					DynameShow(extractResult);
				}
				String last = null;
				if(source.indexOf(_gCirculate)!=-1){
					last=source.substring(source.indexOf(_gCirculate),source.indexOf(_gCirculate)+(e-b));
					extractResult=extractCir(u,last,_pList);
					DynameShow(extractResult);
				}
				/*for(int cir=0;cir<cirList.size();cir++){
					cirEntity=cirList.get(cir);
					extractResult=cirEntity.getCEntity();
						if(extractResult!=null && !extractResult.equals("")){
							Listitem item=new Listitem();
						    Listcell cell;
						     for(int i=0;i<_pList.size();i++){
						    	 cell=new Listcell();
						    	 if(extractResult[i]!=null && extractResult[i].trim().length()>=40){
										String cc=extractResult[i].substring(0, 40)+"...";
										cell.setLabel(cc);
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
											Reader reader=(Reader)Executions.createComponents("/admin/content/task/reader.zul", null, null);
											reader.initWindow(cellValue);
											reader.doHighlighted();
										}
										
									});
						    	 
						     }
						     _lisListbox.appendChild(item);
						}
					
				}*/
				
			    } catch (RuntimeException ex) {
			     throw ex;
			    } catch (Error ex) {
			     throw ex;
			    } finally {
			     Executions.deactivate(_desktop);
			    }
			   }//while
		 
		 LinkCollection.getUnVisitedUrl().deleteAll();
		 LinkCollection.getVisitedUrl().clear();
		 System.out.println("循环采集结束！");
		
	}
	
	 public void setDone(){
		  _ceased = true;
	}
	 
	 
	 public void initData(String encond,List<WkTPickreg> pList,List<WkTGuidereg> gList,String gCirculate) {
			this._encond=encond;
			this._pList=pList;
			this._gList=gList;
			this._gCirculate=gCirculate;
			Executions.getCurrent().getDesktop().enableServerPush(true);
			Tabpanels  tabpanels=(Tabpanels)_box.getTabpanels();
			Tabpanel tabpanel=(Tabpanel) tabpanels.getFirstChild();
			Listbox lbox=(Listbox)tabpanel.getFirstChild();
			this._lisListbox=lbox;
		}
	 
	 
	 
	 //以下添加的
	 private void DynameShow(String[] extractResult){
		 
		 Listitem item=new Listitem();
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
	 
	 
	 private String[] extractCir(String path,String source,List<WkTPickreg> pList){
			
			WkTPickreg pReg;
			String regBegin,regEnd;
			String[] extract=new String[pList.size()];
			
			for(int t=0;t<pList.size();t++){
				
				pReg=(WkTPickreg)pList.get(t);
				regBegin=pReg.getKpRegbegin();
				regEnd=pReg.getKpRegend();
				
			
				Integer beg=source.indexOf(regBegin);
				Integer e=source.indexOf(regEnd,beg+regBegin.length());
				String result = null;
				if(beg!=-1 && e!=-1){
				 
				 beg=beg+regBegin.length();
				 result=source.substring(beg, e);
				
				}else{
				  System.out.println(path+pReg.getKpRegname()+"规则采集失败，查看采集规则设置！");
				}
			if(result==null || result.equals("")|| result.equals(null)){
					extract[t]="";
		
			}else{
			
				if(pReg.getKpRetainTags()!=null){
				
				List tagList=retainTags(result);
				String tagSource=pReg.getKpRetainTags().replace("false,", "").trim();
				HtmlTags htmlTags=new HtmlTags();
				List tList=htmlTags.analyTags(tagSource);
				
				String sourceTag,dataTag;
				for(int a=0;a<tList.size();a++){
					sourceTag=(String) tList.get(a);
					for(int b=0;b<tagList.size();b++){
						dataTag=(String) tagList.get(b);
						if(sourceTag.trim().equals(dataTag.trim())){
							tagList.remove(b);
						}
					}
				}
				
				extract[t]=removeTag(result, tagList);
				
			}else{
				
			try {
				
				final StringBuffer sb=new StringBuffer();
				
				Parser parser=new Parser(result);
				NodeVisitor visitor=new NodeVisitor(){
					public void visitStringNode(Text text){
						String t=text.getText();
						sb.append(t);
					}
				};
				parser.visitAllNodesWith(visitor);
				
				String rr=sb.toString();
				rr=rr.replace("\t", "");
				rr=rr.replace("\n", "");
				rr=rr.replace("\r", "");
				extract[t]=rr;
			
				source=source.substring(e+regEnd.length(),source.length());
				
			} catch (ParserException e1) {
				extract[t]=result;
				source=source.substring(e+regEnd.length(),source.length());
			}
		}
	  }
			

	}

			return extract;
		}
		
		
		/*提取符合标记的标签*/
		private List<String> retainTags(String source){
			
			final List<String> tagList=new ArrayList<String>();
			try {
				Parser parser=new Parser(source);
				NodeVisitor visitor=new NodeVisitor(){
					public void visitTag(Tag tag){
						if(!tagList.contains(tag.getTagName())){
							tagList.add(tag.getTagName());
						}
					}
				};
				parser.visitAllNodesWith(visitor);
				
			} catch (ParserException e) {
				e.printStackTrace();
			}
			return tagList;
		}
		
		private String removeTag(String source,List<String> tagList){
			String tag;
			for(int i=0;i<tagList.size();i++){
				tag=tagList.get(i);
				source=source.replaceAll("(<"+tag+"[^>]*>|</"+tag+">)","").trim();
				source=source.replaceAll("(<"+tag.toLowerCase()+"[^>]*>|</"+tag.toLowerCase()+">)","").trim();
			}
			return source;
		}
	 
	 
	 
	 
}
