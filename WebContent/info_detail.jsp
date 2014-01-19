<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
  String[] infoDetail = (String[])request.getSession().getAttribute("infoDetail");
String style = (String)request.getParameter("style");
/*   String titlecolor = (String)request.getParameter("titlecolor");
  String titlesize = (String)request.getParameter("titlesize");
  String infocolor = (String)request.getParameter("infocolor");
  String infosize = (String)request.getParameter("infosize");
  String contentcolor = (String)request.getParameter("contentcolor");
  String contentsize = (String)request.getParameter("contentsize"); */
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>信息详情</title>
<style type="text/css">
	<%-- #top{
		width:100%;
		height:130px;
		background-color:red;
	}
	tr.title{
		font-size:<%=titlesize%>;
		color:<%=titlecolor%>;
	}
	tr.info{
		font-size:<%=infosize%>;
		color:<%=infocolor%>;
	}
	tr.content{
		font-size:<%=contentsize%>;
		color:<%=contentcolor%>;
	} --%>
	tr.info,tr.info a{
		font-size:12px;
		color:gray;
	}
	<%=style%>
</style>

</head>
<%if(infoDetail !=null) { %>
<body style="margin: 50px 10%;text-align:center;">
<div id="top">

</div>

<div id="middle">
  <table border="0" align="center" width="100%">
    <tr class="title"><td><span><%=infoDetail[0]%></span></td></tr>
    <tr>
      <td>
        <table width="100%" height="30px" cellpadding="0" cellspacing="0" border="0">
           <tr class="info">
             <td><!-- <font color="gray" size="2"> --> 来源:<a href='<%=infoDetail[5]%>' style="cursor: pointer;text-decoration:none;color:gray;"><%=infoDetail[1]%></a> <!-- </font>  --></td>
             <td><!-- <font color="gray" size="2"> --> 发布时间:<%=infoDetail[2]%><!-- </font> --></td>
             <td><!-- <font color="gray" size="2"> --><a href="<%=infoDetail[5]%>" target="_blank" style="cursor: pointer;text-decoration:none;/* color:gray; */"><!-- <font color="gray" size="2"> -->源地址:<%=infoDetail[5]%><!-- </font> --></a><!-- </font> --></td>
             <td><!-- <font color="gray" size="2"> -->提供方:<a href="login.zul" style="cursor: pointer;text-decoration:none;/* color:gray; */" target="_blank">Web信息采集与服务系统</a><!-- </font> --></td>
           </tr> 
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <hr />
      </td>
    </tr>
    <tr class="content"><td align="left"><p><%=infoDetail[4]%></p></td></tr>
    <tr>
      <td><hr /></td>
    </tr>    
  </table>
</div>


</body> 
<% } else{ %>
<body style="margin: 10px 10%;text-align:center;">
<div id="top">

</div>

<div id="middle">
  <table border="0" align="center" width="100%">
    <tr class="title"><td><span>111</span></td></tr>
    <tr>
      <td>
        <table width="100%" height="30px" cellpadding="0" cellspacing="0" border="0">
           <tr class="info">
             <td><!-- <font color="gray" size="2"> -->来源:<a href='#' style="cursor: pointer;text-decoration:none;/* color:gray; */">新闻标题</a><!-- </font> --></td>
             <td><!-- <font color="gray" size="2"> -->发布时间:2013-11-11<!-- </font> --></td>
             <td><!-- <font color="gray" size="2"> --><!-- <font color="gray" size="2"> -->源地址:源地址网址<!-- </font> --><!-- </font> --></td>
             <td><!-- <font color="gray" size="2"> -->提供方:<a href="login.zul" style="cursor: pointer;text-decoration:none;/* color:gray; */" target="_blank">Web信息采集与服务系统</a><!-- </font> --></td>
           </tr> 
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <hr />
      </td>
    </tr>
    <tr class="content"><td align="left"><p>新闻内容</p></td></tr>
    <tr>
      <td><hr /></td>
    </tr>    
  </table>
</div>
<%} %>
</html>