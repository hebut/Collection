<%@ page language="java" import="java.util.*" import="com.uniwin.webkey.core.model.*" pageEncoding="UTF-8"%>
<%
	response.setContentType("application/vnd.ms-word;charset=UTF-8");
%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	WkTInfo info = (WkTInfo)request.getAttribute("info");
	WkTDistribute dis= (WkTDistribute)request.getAttribute("dis");
	String title=info.getKiTitle().trim()+".doc";
	String cnt="";
	List infocnt = (List)request.getAttribute("infocnt");
	for(int i=0;i<infocnt.size();i++)
	{
		WkTInfocnt content = (WkTInfocnt)infocnt.get(i);
		cnt=cnt+content.getKiContent();
	}
%>
<%
	response.setCharacterEncoding("gb2312");
	String name = new String((info.getKiTitle().trim()+".doc").getBytes("gb2312"), "ISO8859-1");
	response.setHeader("Content-disposition", "inline; filename=" + name);
	//以上这行设定传送到前端浏览器时的文档名为信息名.doc
	//就是靠这一行，让前端浏览器以为接收到一个word文档
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;">
<title>导出word</title>
</head>
<body>
<!--资讯内容-->
		<div align="center" style="text-indent: 28pt; line-height: 24pt;font-size: 20px; color: black; font-family: 黑体">
	    <%=info.getKiTitle().trim() %>
		</div>
		<br/>
		<div height="5px"></div>
		<div align="center" style="font-size: 13px; color:red; font-family: 宋体">来源:<%=info.getKiSource().trim()%>&nbsp;&nbsp;&nbsp;作者:<%=info.getKuName().trim() %>&nbsp;&nbsp;&nbsp;发布时间:<%=dis.getKbDtime().substring(0,10) %>   </div>
		<br/>
		<div> <%=cnt %></div>
		</body>
		</html>