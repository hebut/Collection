<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String style = request.getSession().getAttribute("style").toString();
	String count = request.getSession().getAttribute("count").toString();
	String cssClass = request.getParameter("cssClass");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>网站访问计数</title>
<link href="../../../../admin/styles/<%=style %>/index.css" type="text/css" rel="stylesheet">
</head>
<body>
	<div class="<%=cssClass %>" align="left" style="valign:top;">
		<span>您是第<%=count %>位访问者</span>
	</div>
</body>
</html>