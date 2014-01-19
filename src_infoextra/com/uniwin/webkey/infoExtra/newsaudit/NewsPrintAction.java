//package com.uniwin.webkey.infoExtra.newsaudit;
//
//import java.util.List;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.zkoss.spring.SpringUtil;
//
//import com.uniwin.webkey.core.model.WkTDistribute;
//import com.uniwin.webkey.core.model.WkTInfo;
//import com.uniwin.webkey.infoExtra.itf.NewsServices;
//import com.uniwin.webkey.infoExtra.itf.TaskService;
//
//
//
//public class NewsPrintAction extends Action {
//
//	private NewsServices newsService=(NewsServices)SpringUtil.getBean("newsService");
//	
//	
//	public NewsServices getNewsService() {
//		return newsService;
//	}
//
//	public void setNewsService(NewsServices newsService) {
//		this.newsService = newsService;
//	}
//
//	@Override
//	public ActionForward execute(ActionMapping mapping, ActionForm form,
//			HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//		String action=(String) request.getParameter("action");
//		if(action.equalsIgnoreCase("newsprint")){			
//			return executeNewsPrint(mapping,form,request,response);
//		}
//
//		return null;
//	}
//
//	private ActionForward executeNewsPrint(ActionMapping mapping, ActionForm form,
//			HttpServletRequest request, HttpServletResponse response) {
//		Long  infoid=EncodeUtil.decodeByDES(request.getParameter("infoid"));
//		Long disid=EncodeUtil.decodeByDES(request.getParameter("disid"));
//		WkTInfo info=(WkTInfo)newsService.getWkTInfo(infoid);
//		WkTDistribute dis=(WkTDistribute)newsService.getWktDistribute(disid);
//		List infocnt=(List)newsService.getInfocnt(infoid);
//	
//		request.setAttribute("info", info);
//		request.setAttribute("infocnt", infocnt);
//		request.setAttribute("dis", dis);
//		return mapping.findForward("newsprint");
//	}
//	
//}
