<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
  String[] infoDetail = (String[])request.getSession().getAttribute("infoDetail");
  String style = (String)request.getParameter("style");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>信息详情</title>
<style type="text/css">
<%=style%>

</style>
</head>
<%if(infoDetail !=null) { %>
<body style="margin: 50px 10%;text-align:center;">
  <table border="0" align="center" width="100%">
    <tr><h2><%=infoDetail[0]%></h2></tr>
    <tr>
      <td>
        <table width="100%" height="30px" cellpadding="0" cellspacing="0" border="0">
           <tr>
             <td><font color="gray" size="2">来源:<a href='<%=infoDetail[5]%>' style="cursor: pointer;text-decoration:none;color:gray;"><%=infoDetail[1]%></a></font></td>
             <td><font color="gray" size="2">发布时间:<%=infoDetail[2]%></font></td>
             <td><a href="<%=infoDetail[5]%>" target="_blank" style="cursor: pointer;text-decoration:none;color:gray;"><font color="gray" size="2">源地址:<%=infoDetail[5]%></font></a></td>
             <td><font color="gray" size="2">提供方:<a href="login.zul" style="cursor: pointer;text-decoration:none;color:gray;" target="_blank">Web信息采集与服务系统</a></font></td>
           </tr> 
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <hr />
      </td>
    </tr>
    <tr><td align="left"><p><%=infoDetail[4]%></p></td></tr>
    <tr>
      <td><hr /></td>
    </tr>    
  </table>
</body> 
<% } else{ %>
<body style="margin: 50px 10%;text-align:center;">
  <table border="0" align="center" width="100%">
    <tr ><td><span >标题</span></td></tr>
    <tr>
      <td>
        <table width="100%" height="30px" cellpadding="0" cellspacing="0" border="0">
           <tr>
             <td><font color="gray" size="2">来源:<a href='#' style="cursor: pointer;text-decoration:none;color:gray;">信息来源</a></font></td>
             <td><font color="gray" size="2">发布时间:发布时间</font></td>
             <td><font color="gray" size="2">源地址:源地址网址</font></td>
             <td><font color="gray" size="2">提供方:<a href="login.zul" style="cursor: pointer;text-decoration:none;color:gray;" target="_blank">Web信息采集与服务系统</a></font></td>
           </tr> 
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <hr />
      </td>
    </tr>
    <tr><td align="left"><p>资讯内容</p></td></tr>
    <tr>
      <td><hr /></td>
    </tr>    
  </table>
</body> 
<%} %>
</html>