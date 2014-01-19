<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s"  uri="/struts-tags"%>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<%
	String path = request.getContextPath();
	String rootPath = request.getScheme() + "://"+request.getServerName()+":"+request.getServerPort();
	String basePath = rootPath+path;
	String rownum = (String)request.getSession().getAttribute("rownum");
	String colnum = (String)request.getSession().getAttribute("colnum");
	String newinl = (String)request.getSession().getAttribute("newinl");
	String mark = "_top";
	if ("1".equalsIgnoreCase(newinl))
		mark = "_blank";
	
	String newinm = (String)request.getSession().getAttribute("newinm");
	if("1".equalsIgnoreCase(newinm)){
		newinm = "_blank";
	}else{
		newinm = "_top";
	}
	
	String cssClass = (String)request.getSession().getAttribute("cssClass");
	String style    = (String)request.getSession().getAttribute("style");
	String listurl    = (String)request.getSession().getAttribute("listurl");
	String moreurl    = (String)request.getSession().getAttribute("moreurl");
	String isMore   = (String)request.getSession().getAttribute("isMore");
	
	String moreimg = "";
	if((String)request.getSession().getAttribute("moreimg")!=null){
		moreimg = (String)request.getSession().getAttribute("moreimg");
	}
	String moretext1    = (String)request.getSession().getAttribute("moretext");
	String moretext = new String(moretext1.getBytes("ISO8859-1"),"UTF-8");
%>

<link href="../../../../admin/styles/<%=style%>/index.css" type="text/css" rel="stylesheet">
</head>
<body>

<s:if test="%{#request.commList.size==0}">
    <div align="center">
		没有符合条件的数据
	</div>
</s:if>
<s:else>
	<div class="<%=cssClass%>">
		<table>
			<%
			for(int i=0;i<Integer.valueOf(rownum);i++){
				String s = Integer.valueOf(colnum)*i+"";
				request.setAttribute("start",s);
			%>			
			<s:subset source="#request.commList" start="#request.start" count="#request.colnum">
				<tr>
					<s:iterator id="comm" status="st">
						<td>
						<a href="<s:url value="%{#request.listurl}"/>" target="<%=mark%>">
							<s:property  /></a>
						</td>
					</s:iterator>
				</tr>
			</s:subset>
			<%} %>
		</table> 
		
		 <div align="right">
				<%if("is".equalsIgnoreCase(isMore)){ %>
		   			<a href="<s:url value="%{#request.moreurl}"/>" target="<%=newinm %>">
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