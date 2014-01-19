<%@page import="org.apache.struts2.ServletActionContext"%>
<%@ page language="java" 
import="java.util.*"
import="com.uniwin.webkey.infoExtra.model.WKTInfoView"
import="com.uniwin.framework.ui.system.CryptUtils"
import="com.uniwin.webkey.util.ui.IPUtil"
import="com.uniwin.webkey.cms.itf.NewsService"
import="com.uniwin.webkey.infoExtra.util.BeanFactory"
import="com.uniwin.webkey.infoExtra.service.InfoGenServiceImpl"
contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%
    String[] infoDetail = new String[6];
    NewsService newsService = (NewsService)BeanFactory.getBean("newsService");
    
    String userName = request.getParameter("userName");
    String infoId= request.getParameter("infoId");
    String template = request.getParameter("template");//模板文件名
    String style= request.getParameter("style");
    
    if(userName!=null&&!userName.trim().equals("")){
	    WKTInfoView infoView = new WKTInfoView();
	    String name = CryptUtils.decrypt(userName);
	    infoView.setLoginName(name);
	    String infoNo = CryptUtils.decrypt(infoId);
	    infoView.setInfoId(Long.valueOf(infoNo));
	    infoView.setTime(System.currentTimeMillis());
	    infoView.setTimeStamp(System.currentTimeMillis());
	    String ipAddr = IPUtil.getRemortIP(request);
	    infoView.setIpAddr(ipAddr);
	    newsService.save(infoView);
		if(infoNo!=null&&!infoNo.trim().equals("")){
			List<String[]>list = newsService.findWkTInfoByInfoId(Long.valueOf(infoNo.trim()));
			if(list!=null&&list.size()!=0){
				infoDetail = list.get(0);
				request.getSession().setAttribute("infoDetail", infoDetail);
				//response.sendRedirect(request.getContextPath()+"/info_detail.jsp");
				  response.sendRedirect(request.getContextPath()+template+"?style="+style);
				return;
			}
		}
		else{
			response.sendRedirect(request.getContextPath()+"/error.jsp");
			return;
		}
		//response.sendRedirect(request.getContextPath()+"/info_detail.jsp");
		response.sendRedirect(request.getContextPath()+template+"?style="+style);
		return;
	}
	else{
		response.sendRedirect(request.getContextPath()+"/error.jsp");
		return;
	}
%>