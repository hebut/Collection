<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s"  uri="/struts-tags"%>   
<%String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%--Css样式    --%>
<link href="<s:url value="/css/main.css"/>" rel="stylesheet" type="text/css"/>  
<%session.removeAttribute( "rand");  %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<base href="<%=basePath%>">
<%--去掉页面缓存    --%>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="0">
<title>Insert title here</title>

<script language="Javascript">
function changeImg(){
	   var fform = document.loginform;
	   var imgSrc = fform['UCheck'].value ;   
	   var src = imgSrc.attr("src"); 
	   imgSrc.attr("src",chgUrl(src));   
}
//时间戳   
//为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
function chgUrl(url){   
    var timestamp = (new Date()).valueOf();   
    url = url.substring(0,17);   
    if((url.indexOf("&")>=0)){   
        url = url + "×tamp=" + timestamp;   
    }else{   
        url = url + "?timestamp=" + timestamp;   
    }   
    return url;   
}   
</script>
 
 <script language="javascript"> 
  function xxg()
  {
      if (document.form.ULogin.value==""){
	      alert("请输入您的用户名！")
		  document.form.ULogin.focus();
		  return false
	  }
	 
	  if (document.form.UCheck.value==""){
	      alert("请输入系统产生的验证码！")
		  document.form.rand.focus();
		  return false
	   }
	   return true
  }
</script>

</head>
<body>  
<form name="form" method="POST" action="login.action" onSubmit="return xxg()">  	 
	<div class="table" >   
        <div align="center">   
            	用户名:<s:textfield  name="username"  id="ULogin" size="10" maxlength="10"  cssClass="text medium"/>   
              	口&nbsp;令：<s:textfield  name="password"  id="UPass" size="10" maxlength="10"  cssClass="text medium"/>
              	 验证码:<s:textfield  name="check" id="UCheck" size="10" maxlength="4"  cssClass="text medium"/>
              	 <div align="right">
              	 <img border="0" src="listJsp/conn.jsp"> 
              	 <a href="listJsp/login.jsp" onclick="changeImg()">换一张</a>  
              	 </div>      
  				<s:submit name="btnLogin" value="登录" method="log" theme="simple" cssClass="button" />
 				<s:submit name="btnReset" value="重填" method="rest" theme="simple" cssClass="button" />
 		</div>
   </div>
 </form>

</body>
</html>