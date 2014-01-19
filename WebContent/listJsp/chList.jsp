<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String rootPath = request.getScheme() + "://"+request.getServerName()+":"+request.getServerPort();
	String basePath = rootPath+path;
	String newinl = request.getParameter("newinl").toString();
	String mark = "_top";
	if ("1".equalsIgnoreCase(newinl))
		mark = "_blank";
	String style = request.getSession().getAttribute("style").toString();
	String cssClass = request.getParameter("cssClass").toString();
	String rownum = request.getParameter("rownum").toString();
	String colnum = request.getParameter("colnum").toString();
	String moretext = new String(request.getParameter("moretext").getBytes("ISO8859-1"),"UTF-8");
	String templetm = request.getParameter("templetm");
	String newinm = request.getParameter("newinm");
	if("1".equalsIgnoreCase(newinm)){
		newinm = "_blank";
	}else{
		newinm = "_top";
	}
	String moreimg = "";
	if(request.getParameter("moreimg")!=null){
		moreimg = request.getParameter("moreimg");
	}
	String extend = request.getParameter("extend");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<link href="../../../../admin/styles/<%=style%>/index.css" type="text/css"
	rel="stylesheet">
</head>
<body>
<s:if test="%{#request.chList.size==0}">
<div align="center">
	没有符合条件的数据</div>
</s:if>
<s:else>
<div style="<%=extend %>;overflow:hidden;align:center" class="<%=cssClass%>">
<table>
<%
for(int i=0;i<Integer.valueOf(rownum);i++){
	String s = Integer.valueOf(colnum)*i+"";
	request.setAttribute("start",s);
%>
<s:subset source="#request.chList" start="#request.start" count="#request.colnum">
	<tr>
		<s:iterator id="chanel" status="st">
			<td>
				<a href="<s:url value="%{basePath}/SiteHtml/%{#request.websiteID}/%{#request.chUrl}/%{#request.templet}/%{#chanel.kcId}chl.html"/>" target="<%=mark%>">
				<s:property value="fields" /></a>
			</td>
		</s:iterator>
	</tr>
</s:subset>
<%} %>
</table> 
	<div align="right">
		<%if("is".equalsIgnoreCase(request.getParameter("ismore"))){ %>
   			<a href="<s:url value="%{basePath}/SiteHtml/%{#request.websiteID}/%{#request.docUrl}/%{#request.templetm}.html"/>" target="<%=newinm %>">
          	<%if("".equalsIgnoreCase(moreimg)||moreimg==null){ %>
          		<%=moretext %>
          	<%}else{ %>
          		<img alt="图片载入中..." src="<%=rootPath+moreimg %>" border="0">
          	<%} %>
   			</a>
		<%} %>
	</div>
</div>
</s:else>
</body>
</html>