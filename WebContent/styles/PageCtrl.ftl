<#------------------------------------------------------------
 #	Project:         Webkey /4
 #	File Name:       common/pageContral.ftl
 #	Title:           &#x7ffb;&#x9875;&#x63a7;&#x5236;
 #	Copyright:       Copyright (c) 2000-2005 Uniwin Co. Ltd.
 #	Company:         &#x4e2d;&#x4fe1;&#x8054;&#x4fe1;&#x606f;&#x6280;&#x672f;&#x6709;&#x9650;&#x516c;&#x53f8;
 #	Description:     &#x672c;&#x9875;&#x4e3a;&#x5b9a;&#x4e49;&#x5404;&#x79cd;&#x7ffb;&#x9875;&#x663e;&#x793a;&#x63a7;&#x5236;&#x6761;
 #	Author:          &#x5f20;&#x82f1;
 #	Version:         1.0
 ------------------------------------------------------------->


<#------------------------------------------------------------
 #	&#x4f7f;&#x7528;&#x65b9;&#x6cd5;&#xff1a;
 #	Param: 
 #		1.data &#x4e3a;com.uniwin.webkey.itf.base.WKResultList&#x7c7b;&#x578b;
 #		2.url &#x4e3a;&#x8fde;&#x63a5;&#x5730;&#x5740;,&#x6700;&#x7ec8;&#x7ed3;&#x679c;&#x4f1a;&#x663e;&#x793a;&#xff1a;url&page=
 #	Type:
 #		1.type_a: &#x201c;&#x7b2c;n&#x9875;|&#x5171;n&#x9875;|&#x524d;&#x4e00;&#x9875;|&#x4e0b;&#x4e00;&#x9875;&#x201d;
 #		2.type_b: &#x201c;&#x9996;&#x9875; &#x524d;&#x9875; &#x540e;&#x9875; &#x672b;&#x9875;&#x201d;
 #		etc.
 #	Example:
 #		<#import "/common/pageContral.ftl" as com>
 #		<@com.type_a data=users url=link2dress></@com.type_a>
 ------------------------------------------------------------->


<#------------------------------------------------------------
 #						&#x6807;&#x7b7e;&#x5b9a;&#x4e49;&#x5f15;&#x7528;        
 ------------------------------------------------------------->
<#global s=JspTaglibs["/WEB-INF/taglib/struts-tags.tld"]>


<#------------------------------------------------------------
 #						Define macro type_a
 #					&#x201c;&#x7b2c;n&#x9875;|&#x5171;n&#x9875;|&#x524d;&#x4e00;&#x9875;|&#x4e0b;&#x4e00;&#x9875;&#x201d;
 ------------------------------------------------------------->
<#macro type_a data url>
	<#assign 
	          nextnb = data.pageNumber+1
	          prenb = data.pageNumber-1
	>
	
	<#if 1 &lt; data.pageNumber>
		<a href=${url}page=${prenb}>${prenb}</a>
	<#else>
		${prenb}
	</#if>
	<#if data.pageNumber &lt; data.rowCount>
		<a href=${url}page=${nextnb}>${nextnb}</a>
	<#else>
		${nextnb}
	</#if>
</#macro>
<#------------------------------------------------------------
 #					  Define macro type_b
 #		&#x201c;&#x5171; 300 &#x6761;, 1/2 &#x9875;     &#x9996;&#x9875; &#x524d;&#x9875; &#x540e;&#x9875; &#x672b;&#x9875;&#x201d;
 ------------------------------------------------------------->
<#macro type_b data url inputclass btnclass>
	<form name="pageForm" method="post" action="./${url[0..url?length-2]}" onsubmit="return checkpage()">
	<#assign 
	  		nextnb=data.pageNumber+1 
			prenb=data.pageNumber-1
			pcount=data.rowCount / data.pageSize
	>
	<#if pcount==0>
		<#assign pcount=1>
	</#if>
	<td height="25" align="center">
		共${data.rowCount}条，${data.pageCount}页
	</td>
	<td height="25" align="right" width=170>
	<#if data.pageNumber = 1>
		<@s.property value="%{getText('LABEL.TYPEB.FIRST')}"/>
	<#else>
		<a href=${url}page=1>首页</a>
	</#if>
	<#if 1 &lt; data.pageNumber>
		&nbsp;<a href=${url}page=${prenb}>前页</a>
	<#else>
		&nbsp;前页
	</#if>
	<#if data.pageNumber &lt; data.pageCount>
		&nbsp;<a href=${url}page=${nextnb}>后页</a>
	<#else>
		&nbsp;后页
	</#if>
	<#if data.pageNumber = data.pageCount | data.rowCount=0>
		&nbsp;末页
	<#else>
		&nbsp;<a href=${url}page=${data.pageCount}>末页</a>
	</#if>		
	</td>
	<td align="center" width="80">		
		<input name='page' style="width:25"  onkeyup="value=value.replace(/[^\d]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" value="${data.pageNumber}" size=1 class=${inputclass}> 
		<input type="button" class=${btnclass} name="btnGo" Value="GO" onclick="goPage()">&nbsp; 		
	</td>
	</form>
	<script language="javascript">
		function keyclick()
		{
			document.pageForm.page.value=document.pageForm.page.value.replace(/[^\d]/g,'');
			
		}
		function goPage()
		{
			//alert('请输入数字');
			//alert(document.pageForm.page.value);
			if ((document.pageForm.page.value > ${data.pageCount})||(document.pageForm.page.value <= 0))
			{
				alert('页号超出允许的范围');
			}else
			{
				//alert('./${url}page=' + document.pageForm.page.value);
				location='${url}page=' + document.pageForm.page.value;
			}
		}
		function checkpage()
		{
			if ((document.pageForm.page.value > ${data.pageCount})||(document.pageForm.page.value <= 0))
			{
				alert('页号超出允许的范围');
				return false;
			}
			return true;
		}
	</script>
</#macro>
<#------------------------------------------------------------
 #					  Define macro type_c
 #		用于视频查询分页
 ------------------------------------------------------------->
<#macro type_c data url inputclass btnclass searchFilmText type>
	<form name="pageForm" method="post" action="./${url[0..url?length-2]}" onsubmit="return checkpage()">
	<input name="searchFilmText" type="hidden" value="${searchFilmText}">
	<input name="type" type="hidden" value="${type}">
	<#assign 
	  		nextnb=data.pageNumber+1 
			prenb=data.pageNumber-1
			pcount=data.rowCount / data.pageSize
	>
	<#if pcount==0>
		<#assign pcount=1>
	</#if>
	<td height="25" align="center">
		<#if data.rowCount % data.pageSize = 0>
			<@s.bean.message key="LABEL.TYPEB.TATAL" arg0='${data.rowCount}' arg1='${data.pageNumber}'+'/'+'${pcount?int }'/>
		<#else>
			<@s.bean.message key="LABEL.TYPEB.TATAL" arg0='${data.rowCount}' arg1='${data.pageNumber}'+'/'+'${pcount?int +1}'/>
		</#if>	
	</td>
	<td height="25" align="right" width=150>
	<#if data.pageNumber = 1>
		<@s.bean.message key="LABEL.TYPEB.FIRST"/>
	<#else>
		<a href="#" onclick="goPage2(1);return;"><@s.bean.message key="LABEL.TYPEB.FIRST"/></a>
	</#if>
	<#if 1 &lt; data.pageNumber>
		&nbsp;<a href="#" onclick="goPage2(${prenb});return;"><@s.bean.message key="LABEL.TYPEB.PRE"/></a>
	<#else>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.PRE"/>
	</#if>
	<#if data.pageNumber &lt; data.rowCount>
		&nbsp;<a href="#" onclick="goPage2(${nextnb});return;"><@s.bean.message key="LABEL.TYPEB.NEXT"/></a>
	<#else>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.NEXT"/>
	</#if>
	<#if data.pageNumber = data.rowCount | data.rowCount=0>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.LAST"/>
	<#else>
		&nbsp;<a href="#" onclick="goPage2(${data.rowCount});return;"><@s.bean.message key="LABEL.TYPEB.LAST"/></a>
	</#if>		
	</td>
	<td align="center" width="80">		
		<input name='page' style="width:25"  onkeyup="value=value.replace(/[^\d]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" value="${data.pageNumber}" size=1 class=${inputclass}> 
		<input type="button" class=${btnclass} name="btnGo" Value="Go" onclick="goPage()">&nbsp; 		
	</td>
	</form>
	<script language="javascript">
		function keyclick()
		{
			document.pageForm.page.value=document.pageForm.page.value.replace(/[^\d]/g,'');
			
		}
		function goPage()
		{
			if ((document.pageForm.page.value > ${data.rowCount})||(document.pageForm.page.value <= 0))
			{
				alert('<@s.bean.message key="LABEL.TYPEB.INFO"/>');
			}else
			{
				document.pageForm.action='./FilmListAction.do?page=' + document.pageForm.page.value;
				document.pageForm.submit();
			}
		}
		function goPage2(page)
		{
			if ((document.pageForm.page.value > ${data.rowCount})||(document.pageForm.page.value <= 0))
			{
				alert('<@s.bean.message key="LABEL.TYPEB.INFO"/>');
			}else
			{
				document.pageForm.action='./FilmListAction.do?page=' + page;
				document.pageForm.submit();
			}
		}
		function checkpage()
		{
			if ((document.pageForm.page.value > ${data.rowCount})||(document.pageForm.page.value <= 0))
			{
				alert('<@s.bean.message key="LABEL.TYPEB.INFO"/>');
				return false;
			}
			return true;
		}
	</script>
</#macro>
<#------------------------------------------------------------
 #					  Define macro type_d
 #		&#x201c;&#x5171; 300 &#x6761;, 1/2 &#x9875;     &#x9996;&#x9875; &#x524d;&#x9875; &#x540e;&#x9875; &#x672b;&#x9875;&#x201d;
 ------------------------------------------------------------->
<#macro type_d data url inputclass btnclass searchText>
	<form name="pageForm" method="post" action="">
	<input name="searchText" type="hidden" value="${searchText}">
	<#assign 
	  		nextnb=data.pageNumber+1 
			prenb=data.pageNumber-1
			pcount=data.rowCount / data.pageSize
	>
	<#if pcount==0>
		<#assign pcount=1>
	</#if>
	<td height="25" align="center">
		<#if data.rowCount % data.pageSize = 0>
			<@s.bean.message key="LABEL.TYPEB.TATAL" arg0='${data.rowCount}' arg1='${data.pageNumber}'+'/'+'${pcount?int }'/>
		<#else>
			<@s.bean.message key="LABEL.TYPEB.TATAL" arg0='${data.rowCount}' arg1='${data.pageNumber}'+'/'+'${pcount?int +1}'/>
		</#if>	
	</td>
	<td height="25" align="right" width=150>
	<#if data.pageNumber = 1>
		<@s.bean.message key="LABEL.TYPEB.FIRST"/>
	<#else>
		<a href="#" onclick="formSub('1');return false;"><@s.bean.message key="LABEL.TYPEB.FIRST"/></a>
	</#if>
	<#if 1 &lt; data.pageNumber>
		&nbsp;<a href="#" onclick="formSub('${prenb}');return false;"><@s.bean.message key="LABEL.TYPEB.PRE"/></a>
	<#else>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.PRE"/>
	</#if>
	<#if data.pageNumber &lt; data.rowCount>
		&nbsp;<a href="#" onclick="formSub('${nextnb}');return false;"><@s.bean.message key="LABEL.TYPEB.NEXT"/></a>
	<#else>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.NEXT"/>
	</#if>
	<#if data.pageNumber = data.rowCount | data.rowCount=0>
		&nbsp;<@s.bean.message key="LABEL.TYPEB.LAST"/>
	<#else>
		&nbsp;<a href="#" onclick="formSub('${data.rowCount}');return false;"><@s.bean.message key="LABEL.TYPEB.LAST"/></a>
	</#if>		
	</td>
	<td align="center" width="80">		
		<input name='page' style="width:25"  onkeyup="value=value.replace(/[^\d]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" value="${data.pageNumber}" size=1 class=${inputclass}> 
		<input type="button" class=${btnclass} name="btnGo" Value="GO" onclick="goPage()">&nbsp; 		
	</td>
	</form>
	<script language="javascript">
		function formSub(page)
		{
			document.pageForm.action='${url}page=' + page;
			document.pageForm.submit();
		}
		function goPage()
		{
			if ((document.pageForm.page.value > ${data.rowCount})||(document.pageForm.page.value <= 0))
			{
				alert('<@s.bean.message key="LABEL.TYPEB.INFO"/>');
				document.pageForm.page.focus();
				return;
			}else
			{
				document.pageForm.action='${url}page=' + document.pageForm.page.value;
				document.pageForm.submit();
			}
		}
		
	</script>
</#macro>