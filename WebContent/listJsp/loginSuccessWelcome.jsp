<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s"  uri="/struts-tags" %>   
<% String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 <%--Css样式    --%>
<link href="<s:url value="/css/main.css"/>" rel="stylesheet" type="text/css"/>  
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<base href="<%=basePath %>">
<%--去掉页面缓存    --%>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="0">
<title>Insert title here</title>
</head>
<body> 

<form action="loginWelcome.action"  method="post" target="_blank">	 
	 <div class="table" >   
        <div align="left">  
        	欢迎您！<s:property value="#request.uname"/> 
 		</div>
   </div>
	 
</form>
</body>
</html>