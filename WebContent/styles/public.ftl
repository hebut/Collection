<#------------------------------------------------------------
 #	Project:         Webkey /4
 #	File Name:       styles/public.ftl
 #	Title:           &#x5b8f;&#x5b9a;&#x4e49;&#xff0c;&#x4f9b;&#x524d;&#x53f0;&#x6a21;&#x677f;&#x8c03;&#x7528;
 #	Copyright:       Copyright (c) 2000-2005 Uniwin Co. Ltd.
 #	Company:         &#x4e2d;&#x4fe1;&#x8054;&#x4fe1;&#x606f;&#x6280;&#x672f;&#x6709;&#x9650;&#x516c;&#x53f8;
 #	Description:     &#x672c;&#x9875;&#x4e3a;&#x5b8f;&#x5b9a;&#x4e49;&#x9875;&#x9762;
 #	$Author: lcf $
 #	$Id: public.ftl,v 1.59 2007/09/30 04:59:41 lcf Exp $
 ------------------------------------------------------------->


<#------------------------------------------------------------
 # &#x7248;&#x6743;&#x4fe1;&#x606f;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;&#xff1a;
 #			data		&#x6570;&#x636e;
 #      style   &#x98ce;&#x683c;
 #			FIELDS	&#x663e;&#x793a;&#x5185;&#x5bb9;
 #			CLASS		&#x663e;&#x793a;&#x6837;&#x5f0f;
 ------------------------------------------------------------->


<#macro COPYRIGHT data style='default' FIELDS='CopyRight' CLASS='dftxt'>
	<div class='${CLASS}'>
	${FIELDS
		?replace("&lt;","<")
		?replace("&gt;",">")
		?replace("[KW_EPNAME]",data.kwEpname?default(''))
		?replace("[KW_ADDRESS]",data.kwAddress?default(''))
		?replace("[KW_FAX]",data.kwFax?default(''))
		?replace("[KW_POSTID]",data.kwPostid?default(''))
		?replace("[KW_EMAIL]","<a href='mailto:"+data.kwEmail?default('')+"'>"+data.kwEmail?default('')+"</a>")
		?replace("[KW_ID]",data.kwId?default(''))
		?replace("[KW_PID]",data.kwPid?default(''))
		?replace("[KW_VERSION]",data.kwVersion?default(''))
		?replace("[KW_LOCATION]",data.kwLocation?default(''))
		?replace("[KW_GENMGR]",data.kwGenmgr?default(''))
		?replace("[KW_PHONE]",data.kwPhone?default(''))
		?replace("[KW_STYLE]",data.kwStyle?default(''))
		?replace("[BR]","<br>")
	}
	</div>
</#macro>

<#------------------------------------------------------------
 # &#x6587;&#x6863;&#x5217;&#x8868;	
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #	data		&#x6570;&#x636e;&#xff0c;&#x683c;&#x5f0f;&#x4e3a;WKResultList&#x5bf9;&#x8c61;
 #	style		&#x754c;&#x9762;&#x98ce;&#x683c;
 #	CID			&#x680f;&#x76ee;id 
 #	FIELDS	&#x663e;&#x793a;&#x5185;&#x5bb9;
 #	ROWNUM	&#x663e;&#x793a;&#x884c;&#x6570;
 #	COLNUM	&#x663e;&#x793a;&#x5217;&#x6570;
 #	TEMPLET	&#x663e;&#x793a;&#x5185;&#x5bb9;&#x7684;&#x6a21;&#x677f;
 #	NEWINL	&#x662f;&#x5426;&#x5728;&#x65b0;&#x7a97;&#x53e3;&#x663e;&#x793a;&#x5185;&#x5bb9;
 #	JOPEN		&#x662f;&#x5426;&#x7528;&#x811a;&#x672c;&#x6253;&#x5f00;&#x65b0;&#x7a97;&#x53e3;
 #	NEWFLAG	&#x662f;&#x5426;&#x663e;&#x793a;&#x65b0;&#x53d1;&#x5e03;&#x72b6;&#x6001;&#x4fe1;&#x606f;
 #	WORDS		&#x663e;&#x793a;&#x7684;&#x5b57;&#x6570;
 #	EXTENDS	&#x6269;&#x5c55;&#x5c5e;&#x6027;&#xff08;td&#xff09;
 #	TEMPLETM&#x66f4;&#x591a;&#x6a21;&#x677f;
 #	MORETEXT&#x66f4;&#x591a;&#x6587;&#x5b57;
 #	MOREIMG	&#x66f4;&#x591a;&#x56fe;&#x7247;
 #	NEWINM	&#x662f;&#x5426;&#x5728;&#x65b0;&#x7a97;&#x53e3;&#x663e;&#x793a;&#x66f4;&#x591a;
 #	CLASS		td&#x6837;&#x5f0f;
 #	URL			&#x672c;&#x9875;&#x8fde;&#x63a5;
  ------------------------------------------------------------->
<#macro DOCLIST data style='default' CID='' FIELDS='title' ROWNUM=5 COLNUM=1 TEMPLET='' NEWINL=0 JOPEN=0 NEWFLAG=0 WORDS=10 EXTENDS='' TEMPLETM='' MORETEXT='' MOREIMG='' NEWINM=0 CLASS='' URL=''>
	<#if MORETEXT?index_of("[PAGE]") gt -1 || FIELDS?index_of("[PAGE]") gt -1 >
		<#assign page=1>
	<#else>
		<#assign page=0>
	</#if>
	<#--&#x5b9a;&#x4e49;&#x66f4;&#x591a;-->
	<#assign more='&#26356;&#22810;...'>
	<#if MOREIMG !=''>
		<#assign more='<IMG SRC="${MOREIMG}" border=0 ALT="">'>
	<#elseif MORETEXT !=''>
		<#assign more=MORETEXT>
	</#if>
	<#assign tpm=TEMPLETM?replace("[CID]",CID)>
	<#if NEWINM=0>
		<#assign more='<a href="index.do?cid=${CID}&templet=${tpm}">${more}</a>'>
	<#else>
		<#assign more='<a href="index.do?cid=${CID}&templet=${tpm}" target="_blank">${more}</a>'>
	</#if>
	<#--&#x663e;&#x793a;&#x8868;&#x683c;-->
	<table width="100%">
		<#if COLNUM==0>
			<tr>
		</#if>
		<#list data.disList as x>
			<#if COLNUM!=0>
				<#if x_index%COLNUM==0>
					<tr>
				</#if>
			<#else>
				
			</#if>
			<td class='${CLASS}' ${EXTENDS}>				
				<#--&#x6807;&#x9898;&#x5185;&#x5bb9;-->
				<#if WORDS gt 0 && x.title?default('')?length gt WORDS>
					<#assign title=x.title[0..WORDS-1]+"...">
				<#else>
					<#assign title=x.title?default('')>
				</#if>

				<#if WORDS gt 0 && x.title2?default('')?length gt WORDS>
					<#assign title2=x.title2[0..WORDS-1]+"...">
				<#else>
					<#assign title2=x.title2?default('')>
				</#if>

				<#if WORDS gt 0 && x.distributeTitle?default('')?length gt WORDS>
					<#assign title3=x.distributeTitle[0..WORDS-1]+"...">
				<#else>
					<#assign title3=x.distributeTitle?default('')>
				</#if>
				
				<#if x.em?default('0') == '1'>
					<#assign title='<em>' + title + '</em>'>
					<#assign title2='<em>' + title2 + '</em>'>
					<#assign title3='<em>' + title3 + '</em>'>
				</#if>
				
				<#if x.strong?default('0') == '1'>
					<#assign title='<strong>' + title + '<strong>'>
					<#assign title2='<strong>' + title2 + '<strong>'>
					<#assign title3='<strong>' + title3 + '<strong>'>
				</#if>
				
				<#if x.color?default('') != ''>
					<#assign title='<font color="'+ x.color +'">' + title + '</font>'>
					<#assign title2='<font color="'+ x.color +'">' + title2 + '</font>'>
					<#assign title3='<font color="'+ x.color +'">' + title3 + '</font>'>
				</#if>


				<#if NEWFLAG gt 0 && beforeDay(x.DTime,NEWFLAG) lt 1>
					<#assign title=title + '<IMG SRC="./admin/styles/red/Image/new.gif" BORDER="0" ALT="">'>
					<#assign title2=title2 + '<IMG SRC="./admin/styles/${style}/Image/new.gif" BORDER="0" ALT="">'>
					<#assign title3=title3 + '<IMG SRC="./admin/styles/${style}/Image/new.gif" BORDER="0" ALT="">'>
				</#if>


				<#if WORDS gt 0 && x.memo?default('')?length gt WORDS>
					<#assign memo=x.memo[0..WORDS-1]+"...">
				<#else>
					<#assign memo=x.memo?default('')>
				</#if>

				<#if FIELDS?index_of("[TOP]") gt -1 && x.orderNumber?default(10)=0>
					<#assign top=1>
					<#assign st=
						FIELDS
							?replace("&lt;","<")
			    				?replace("&gt;",">")
							?replace("[BR]","<br>")
							?replace("[TR]","<tr>")
							?replace("[/TR]","</tr>")
							?replace("[TD]","<td>")
							?replace("[/TD]","</td>")
							?replace("[PAGE]","")
							?replace("[KI_TITLE]",title?default(''))
							?replace("[KI_TITLE2]",title2?default(''))
							?replace("[KI_SOURCE]",x.source?default(''))
							?replace("[KU_ID]",x.userId?default(''))
							?replace("[KU_NAME]",x.userName?default(''))
							?replace("[KI_HITS]",x.hit?default(0))
							?replace("[KB_DTIME]",x.DTime?default(''))
							?replace("[DATE]",x.DTime?default('2005-12-12 13:30')[0..10])
							?replace("[DATES]",x.DTime?default('2005-12-12 13:30')[5..10])
							?replace("[TIME]",x.DTime?default('2005-12-12 13:30')[11..15])
							?replace("[KI_MEMO]",memo?default(''))
							?replace("[KI_KEYS]",x.keys?default(''))
							?replace("[KC_NAME]",x.channelName?default(''))
							?replace("[KI_KEYS]",x.keys?default(''))
							?replace("[KB_TITLE]",title3?default(''))
					>
				<#else>
					<#assign top=0>
					<#assign st=
						FIELDS
							?replace("&lt;","<")
			    			?replace("&gt;",">")
							?replace("[BR]","<br>")
							?replace("[TR]","<tr>")
							?replace("[/TR]","</tr>")
							?replace("[TD]","<td>")
							?replace("[/TD]","</td>")
							?replace("[PAGE]","")
							?replace("[TOP]","")
							?replace("[KI_TITLE]",title?default(''))
							?replace("[KI_TITLE2]",title2?default(''))
							?replace("[KI_SOURCE]",x.source?default(''))
							?replace("[KU_ID]",x.userId?default(''))
							?replace("[KU_NAME]",x.userName?default(''))
							?replace("[KI_HITS]",x.hit?default(0))
							?replace("[KB_DTIME]",x.DTime?default(''))
							?replace("[DATE]",x.DTime?default('2005-12-12 13:30')[0..10])
							?replace("[DATES]",x.DTime?default('2005-12-12 13:30')[5..10])
							?replace("[TIME]",x.DTime?default('2005-12-12 13:30')[11..15])
							?replace("[KI_MEMO]",memo?default(''))
							?replace("[KI_KEYS]",x.keys?default(''))
							?replace("[KC_NAME]",x.channelName?default(''))
							?replace("[KI_KEYS]",x.keys?default(''))
							?replace("[KB_TITLE]",title3?default(''))
					>
				</#if>
				<#if x.image?exists && x.image!=''>
					<#assign st=st?replace("[KI_IMAGE]","'./upload/${x.userName}/${x.image}'")>
				<#else>
					<#assign st=st?replace("[KI_IMAGE]","'./images/defaultdocimg.gif' class='${CLASS}_docimg' ")>
				</#if>
				<#-- &#x751f;&#x6210;&#x94fe;&#x63a5; -->
				<#-- a href=<#if JOPEN==1>"javascript:popwin(</#if><#if x.type="1">'index.do?id=${x.distributeId}&templet=${TEMPLET}'<#elseif x.type="2" && x.address?exists>'apps/cms/upload/A${x.id}${x.address[x.address?index_of(".")..x.address?length-1]}'<#elseif x.type="3">'${x.address?default("")}'</#if><#if JOPEN==1>);"</#if--> 

				<#assign tp=TEMPLET
							?replace("[KU_ID]",x.userId?default(''))
							?replace("[KB_ID]",x.distributeId?default(''))
							?replace("[KI_ID]",x.id?default(''))
							?replace("[KC_ID]",x.channelId?default(''))
							?replace("[CID]",CID)
				>
				<#-- 1红关键字，新加&searchText=${searchText?default("")?url("utf-8")}-->
				<a href=<#if JOPEN==1>"javascript:popwin(</#if><#if x.type="1">'./SiteHtml/${data.kwId}/${x.channelId}/${x.dateTime[0..7]}/w${x.dateTime[8..13]}-${x.distributeId}doc.html'</#if><#if JOPEN==1>);"</#if> 
				  <#if NEWINL==1><#if JOPEN!=1 && x.type!="2"> target="_blank"</#if></#if> 
				  title='${x.title?default('')}'>
					<#if top?default(0)=1>						
						<#assign st=
								st?replace("[IMG]","<image border=0 src=")
								?replace("[/IMG]",">")
								?replace("[IMG ","<img ")>
						<#list st?split("[TOP]") as y>
							<#if st?split("[TOP]")?size=1>
								<@bean.message key='INFO.SHOWTOP'/>${y}
							<#else>
								<#if y_index=0>
									${y}<@bean.message key='INFO.SHOWTOP'/>
								<#else>
									${y}
								</#if>
							</#if>
						</#list>
					<#else>
						<#assign st=
								st?replace("[IMG]","<image border=0 src=")
								?replace("[/IMG]",">")
								?replace("[IMG ","<img ")>
						<#assign thislink="<a href=">
						<#if JOPEN==1>
							<#assign thislink=thislink+"javascript:popwin(">
						</#if>
						<#if x.type="1">
							<#assign thislink=thislink+"'index.do?id=${x.distributeId}&templet=${tp}&cid=${x.channelId}}'">
						<#elseif x.address?exists>
							<#assign thislink=thislink+"'apps/cms/docforward.do?id=${x.distributeId}'">
						</#if>
						<#if JOPEN==1>
							<#assign thislink=thislink+");">
						</#if>
						<#if NEWINL==1>
							<#if JOPEN!=1 && x.type!="2">
								<#assign thislink=thislink+" target='_blank'">
							</#if>
						</#if>
						<#assign thislink=thislink+" title='${x.title?default('')}'>">		
						<#assign st=st?replace("[LINK]",thislink)>	
						<#-- 2红关键字，修改267到272-->				
						<#if searchText?exists>
							<#if searchText=="">${st}<#else>${st?replace(searchText,'<span style="color:#FF0000">'+searchText+'</span>')}</#if>
						<#else>
							${st}
						</#if>
					</#if>									
				</a>
			</td>
			<#if COLNUM!=0>
				<#if (x_index+1)%COLNUM==0>
					</tr>
				</#if>
			<#else>
				
			</#if>
		</#list>
		<#if COLNUM==0>
			</tr>
		</#if>		

		<#--&#x5982;&#x679c;&#x6ca1;&#x6709;&#x6570;&#x636e;&#x663e;&#x793a;&#x63d0;&#x793a;&#xff1a;-->
		<#--if data.mydata?size==0>
			<tr><td align=center colspan=${COLNUM} class=${CLASS} ${EXTENDS}>&lt;&#27809;&#26377;&#31526;&#21512;&#26465;&#20214;&#30340;&#25968;&#25454;&gt;</td></tr>
		</#if-->
		
		<#--&#x8865;&#x7a7a;&#x884c;-->
		<#--if ROWNUM !=0 && data.mydata?size != ROWNUM*COLNUM>
			<#if data.mydata?size=0>
				<#list 2..ROWNUM as blr>
					<tr><td>&nbsp;</td></tr>
				</#list>
			<#else>
				<#assign blankrow=ROWNUM-data.mydata?size>
					<#if blankrow gt 1>
						<#list 1..blankrow as blr>
							<tr>
								<#list 1..COLNUM as blc>
									<td>&nbsp;</td>
								</#list>
							</tr>
						</#list>
					</#if>
			</#if>
		</#if-->
		
		<#--&#x663e;&#x793a;&#x66f4;&#x591a;-->
		<#if TEMPLETM!=''>
			<#if data.pageCount gt data.pageNumber>
				<tr><td align=right colspan=${COLNUM} class=${CLASS} ${EXTENDS}>${more}</td></tr>
			</#if>
		</#if>
	</table>
	<#--&#x663e;&#x793a;&#x5206;&#x9875;-->
	<#if page?default(0)=1>	
		<#if data.pageCount gt 1>		
			<table width="100%">
			<tr><@pc.type_b data=data url=URL?url("utf-8")?replace("%3F", "?")?replace("%3D", "=")?replace("%26", "&") inputclass='inputstyle' btnclass='btnstyle'/></tr>
			</table>
		</#if>
	</#if>
</#macro>

<#------------------------------------------------------------
 # &#x6587;&#x7ae0;&#x5185;&#x5bb9;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro DOCDTL data style='default' sessionid='1' FIELDS='title' CLASS='' pages=1 >
	<div class='${CLASS}'>
	    <#assign rmk=0>
		<#if FIELDS?index_of("[rmk]") gt -1>
			<#assign rmk=1>
		</#if>
		<#if FIELDS?index_of("[RMK]") gt -1>
			<#assign rmk=2>
		</#if>
		<#assign dtl=FIELDS
		    ?replace("&lt;","<")
		    ?replace("&gt;",">")
			?replace("[KI_CONTENT]",data.docContent.kiContent?default(''))
			?replace("[KI_TITLE]",data.docInfo.kiTitle?default(''))
			?replace("[KI_TITLE2]",data.docInfo.kiTitle2?default(''))
			?replace("[KI_SOURCE]",data.docInfo.kiSource?default(''))
			?replace("[KU_ID]",data.docInfo.kuId?default(''))
			?replace("[KU_NAME]",data.docInfo.kuName?default(''))
			?replace("[KI_HITS]",data.docInfo.kiHits?default(0))
			?replace("[KB_DTIME]",data.docInfo.kbDtime?default(''))
			?replace("[KB_TITLE]",data.docInfo.kbTitle?default(''))
			?replace("[DATE]",data.docInfo.kiCtime?default('2005-12-12 13:30')[0..10])
			?replace("[DATES]",data.docInfo.kiCtime?default('2005-12-12 13:30')[5..10])
			?replace("[TIME]",data.docInfo.kiCtime?default('2005-12-12 13:30')[11..15])
			?replace("[KI_MEMO]",data.docInfo.kiMemo?default(''))
			?replace("[KI_KEYS]",data.docInfo.kiKeys?default(''))
			?replace("[BR]","<br>")
			?replace("[RMK]","")
			?replace("[rmk]","")
		>
		<#if data.docInfo.image?exists && data.docInfo.image!=''>
			<#assign dtl=dtl?replace("[KI_IMAGE]","'./apps/cms/upload/${data.docInfo.kiImage}'")>
		<#else>
			<#assign dtl=dtl?replace("[KI_IMAGE]",'aa style="display:none;"')>
		</#if>
		<#assign dtl=
				dtl?replace("[IMG]","<image border=0 src=")
				?replace("[/IMG]","/>")
				?replace("[IMG ","<img ")>
		${dtl}
		<#if FIELDS=='[KI_CONTENT]'>
		<#if pages != 1>
			<div align='center'>
				<table>
					<tr>
						<#list 1..pages as p>
							<#if p==data.curPage>
								<td>[${p}]</td>
							<#else>
								<#if p==1>
									<td><a href='./w${data.docCtime}-${data.kbId}doc.html'>[${p}]</a></td>
								<#else>
									<td><a href='./w${data.docCtime}-${data.kbId}-${p}doc.html'>[${p}]</a></td>
								</#if>
							</#if>
						</#list>
					</tr>
				</table>
			</div>
		</#if>
		</#if>
	</div>
	<#--RMK:&#x90fd;&#x53ef;&#x4ee5;&#x53d1;&#x8bc4;&#x8bba;&#xff1b;rmk:&#x53ea;&#x6709;&#x767b;&#x5f55;&#x7528;&#x6237;&#x624d;&#x53ef;&#x4ee5;&#x53d1;&#x8bc4;&#x8bba;-->
	<#if (rmk?default(0) = 2)||((rmk?default(0) = 1)&&(sessionid?default('1') != '1'))>
	<br>
	<#--&#x663e;&#x793a;&#x8bc4;&#x8bba;-->
	<table width='100%'>
		<tr><td>Comment:</td></tr>
	</table>
	<hr>
	<table width='100%'>
		<#list data.rmkList as x>
			<tr>
				<td>
					${x.koAuthor}&nbsp;&nbsp;
					Time:${x.koTime}&nbsp;&nbsp;
					Email:${x.koEmail?default('')}
				</td>
			</tr>
			<tr>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;${x.koContent}</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</#list>
	</table>
	
	</#if>
</#macro>


<#------------------------------------------------------------
 # &#x5f53;&#x524d;&#x7528;&#x6237;&#x4fe1;&#x606f;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro CURUSER data style='default' FIELDS='guest' CLASS=''>
	<div class='${CLASS}'>
	${FIELDS
		?replace("&lt;","<")
		?replace("&gt;",">")
		?replace("[KU_LID]",data.kuLid?default(''))
		?replace("[KU_NAME]",data.kuName?default(''))
		?replace("[KU_EMAIL]","<a href='mailto:"+data.kuEmail?default('')+"'>"+data.kuEmail?default('')+"</a>")
		?replace("[KU_PHONE]",data.kuPhone?default(''))
		?replace("[KU_BIRTHDAY]",data.kuBirthday?default(''))
		?replace("[KU_SEX]",data.kuSex?default(''))
		?replace("[KU_STYLE]",data.kuStyle?default(''))
		?replace("[KU_COMPANY]",data.kuCompany?default(''))
		?replace("[KU_BINDADDR]",data.kuBindaddr?default(''))
		?replace("[KU_STATUS]",data.kuStatus?default(''))
		?replace("[KU_LIMIT]",data.kuLimit?default(''))
		?replace("[KU_RTIME]",data.kuRtime?default(''))
		?replace("[KU_LASTADDR]",data.kuLastaddr?default(''))
		?replace("[BR]","<br>")
	}	
	</div>
</#macro>


<#------------------------------------------------------------
 # &#x6807;&#x9898;&#x5185;&#x5bb9;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro TITLEDTL data style='default' FIELDS='title' CLASS=''>
	<div class='${CLASS}'>
	${FIELDS
		?replace("&lt;","<")
		?replace("&gt;",">")
		?replace("[KT_NAME]",data.name?default(''))
		?replace("[KT_HITS]",data.hits?default(''))
		?replace("[BR]","<br>")
	}	
	</div>
</#macro>



 <#------------------------------------------------------------
 # &#x9891;&#x9053;&#x5185;&#x5bb9;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro CHLDTL data style='default' FIELDS='guest' CLASS=''>
	<div class='${CLASS}'>
		${FIELDS
		    ?replace("&lt;","<")
		    ?replace("&gt;",">")
			?replace("[KC_NAME]",data.kcName?default(''))
			?replace("[KC_STATUS]",'<#if channel.status?exists && channel.status="1"><@bean.message key="OPTION.USERSTATUS.NORMAL"/></#if><#if channel.status?exists && channel.status="0"><@bean.message key="OPTION.USERSTATUS.CLOSEED"/></#if>')
			?replace("[KC_DESC]",data.desc?default(''))
			?replace("[BR]","<br>")
		}
	</div>
</#macro>


 <#------------------------------------------------------------
 # &#x9891;&#x9053;&#x5217;&#x8868;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro CHLIST data style='default' CID='' FIELDS='title' ROWNUM=5 COLNUM=1 TEMPLET='' NEWINL=0 JOPEN=0 NEWFLAG=0 WORDS=10 EXTENDS='' TEMPLETM='' MORETEXT='' MOREIMG='' NEWINM=0 CHILD=0 CLASS=''>
<#if CHILD lt 2>
	<#--&#x5b9a;&#x4e49;&#x66f4;&#x591a;-->
	<#assign more='&#26356;&#22810;...'>
	<#if MOREIMG !=''>
		<#assign more='<IMG SRC="${MOREIMG}" ALT="">'>
	<#elseif MORETEXT !=''>
		<#assign more=MORETEXT>
	</#if>
	<#assign tpm=TEMPLETM?replace("[CID]",CID)>
	<#if NEWINM=0>
		<#assign more='<a href="./${tpm}.html">${more}</a>'>
	<#else>
		<#assign more='<a href="./{tpm}.html" target="_blank">${more}</a>'>
	</#if>
	<#--&#x663e;&#x793a;&#x8868;&#x683c;-->
	<table width="100%">
		<#if COLNUM==0>
			<tr>
		</#if>
		<#list data.channelList as x>
			<#if COLNUM!=0>
				<#if x_index%COLNUM==0>
					<tr>
				</#if>
			<#else>
				
			</#if>
			<td class='${CLASS}' ${EXTENDS}>
				<#--&#x6807;&#x9898;&#x5185;&#x5bb9;-->
				<#if WORDS gt 0 && x.kuName?default('')?length gt WORDS>
						<#assign title=x.kuName[0..WORDS-1]+"...">
				<#else>
						<#assign title=x.kuName?default('')>
				</#if>
				<#assign st=
					FIELDS
						?replace("&lt;","<")
			    		?replace("&gt;",">")
						?replace("[KC_NAME]",title)
						?replace("[BR]","<br>")
				>

				<#if x.kcImage?exists && x.kcImage!=''>
					<#assign st=st?replace("[KC_IMAGE]","'./apps/cms/upload/${x.kcImage}'")>
				<#else>
					<#assign st=st?replace("[KC_IMAGE]",'')>
				</#if>

				<a href=
					<#if TEMPLET != ''>
						<#assign tp=TEMPLET
							?replace("[KC_ID]",x.kcId?default(''))
							?replace("[CID]",CID)
						>
						'./${tp}/${x.kcId}chl.html' 
					<#else>
						'./${x.tpList?default("")}/${x.kcId}chl.html'
					</#if>
					<#if NEWINL=1> target="_blank"</#if> 
					<#if JOPEN=1> onclick="window.open(this)"</#if>
					title='${x.kcName}'
				>					
					 ${FIELDS
						?replace("&lt;","<")
						?replace("&gt;",">")
						?replace("[KC_NAME]",x.kcName?default(''))
						?replace("[BR]","<br>")
					}		
				</a>
			</td>
			<#if COLNUM!=0>
				<#if (x_index+1)%COLNUM==0>
					</tr>
				</#if>
			<#else>
				
			</#if>
		</#list>
		<#if COLNUM==0>
			</tr>
		</#if>
		<#--&#x663e;&#x793a;&#x66f4;&#x591a;-->
		<#if TEMPLETM!=''>
			<#if data.pageCount gt data.pageNumber>
				<tr><td align=right colspan=${COLNUM} class=${CLASS} ${EXTENDS}>${more}</td></tr>
			</#if>
		</#if>
	</table>
<#else>

</#if></#macro>



<#------------------------------------------------------------
 # &#x5f53;&#x524d;&#x4f4d;&#x7f6e;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro CURPOS data CID=1 style='default' FIELDS='pos' SEPCHAR='-' CLASS='' LEVEL=0 BEGIN=0>
    <table class="data" border="0" id="ItemList" cellpadding="0" cellspacing="0" class='${CLASS}' >
                <thead>
                   <tr>
                        <th align="left" ></th>
                    </tr>
                </thead>
                <tbody class='${CLASS}'>
                    <tr>
                       <td></td>
                   	   <#list data as x>                         
                              <td align="left" >
                              
                               <#if x_index+1 gt BEGIN>		  
									<#if LEVEL gt 0 && x_index=LEVEL>
									<#break>
									</#if>
                                   <#assign st=
											FIELDS
											 ?replace("&lt;","<")
											 ?replace("&gt;",">")
											 ?replace("[KC_NAME]",x.kcName?default(''))
											 ?replace("[BR]","<br>")
									>
												
									<#if x.kcImage?exists && x.kcImage!=''>
						                  <#assign st=st?replace("[KC_IMAGE]","'./upload/chanel/${x.kcImage}'")>
					                <#else>
						                   <#assign st=st?replace("[KC_IMAGE]",'')>
					                </#if>
									
									<#assign st=
										  st?replace("[IMG]","<image border=0 src=")
											?replace("[/IMG]",">")
					    					?replace("[IMG ","<img ")
					    			>
									
									<#if x_index = BEGIN>
									<#else>
										${SEPCHAR}
									</#if>
										 										 
									<#if x.kcId = CID>
										${st}
									<#else>
										<a href="#?cid=${x.kcId}">
										${st}</a> 
									</#if>
								 </#if>	
					           </td>                              
                     	   </#list>   
                     </tr>                     
                </tbody>
    </table>	
</#macro>

<#------------------------------------------------------------
 # &#x5f53;&#x524d;&#x65f6;&#x95f4;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;,&#x5982;&#xff1a;yyyy-MM-dd HH:mm:ss
 ------------------------------------------------------------->
<#macro CURTIME data style='default' FIELDS='time' CLASS=''>
	<div class='${CLASS}'>
		${FIELDS?replace("[T]",data)}
	</div>
</#macro>

<#------------------------------------------------------------
 # &#x8ba1;&#x6570;&#x5668;
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro COUNTER data style='default' FIELDS='counter' CLASS=''>
	<div class='${CLASS}'>
		<#if FIELDS?index_of("[n]") gt -1>
			${FIELDS?replace("[n]",data)}
		<#else>
			<#assign pic0=FIELDS?replace("n","0")?replace("[","")?replace("]","")
					 pic1=FIELDS?replace("n","1")?replace("[","")?replace("]","")
					 pic2=FIELDS?replace("n","2")?replace("[","")?replace("]","")
					 pic3=FIELDS?replace("n","3")?replace("[","")?replace("]","")
					 pic4=FIELDS?replace("n","4")?replace("[","")?replace("]","")
					 pic5=FIELDS?replace("n","5")?replace("[","")?replace("]","")
					 pic6=FIELDS?replace("n","6")?replace("[","")?replace("]","")
					 pic7=FIELDS?replace("n","7")?replace("[","")?replace("]","")
					 pic8=FIELDS?replace("n","8")?replace("[","")?replace("]","")
					 pic9=FIELDS?replace("n","9")?replace("[","")?replace("]","")
					 cnt=data?replace("0","<img border=0 src='styles/${style}/images/${pic0}'>")
							 ?replace("1","<img border=0 src='styles/${style}/images/${pic1}'>")
							 ?replace("2","<img border=0 src='styles/${style}/images/${pic2}'>")
							 ?replace("3","<img border=0 src='styles/${style}/images/${pic3}'>")
							 ?replace("4","<img border=0 src='styles/${style}/images/${pic4}'>")
							 ?replace("5","<img border=0 src='styles/${style}/images/${pic5}'>")
							 ?replace("6","<img border=0 src='styles/${style}/images/${pic6}'>")
							 ?replace("7","<img border=0 src='styles/${style}/images/${pic7}'>")
							 ?replace("8","<img border=0 src='styles/${style}/images/${pic8}'>")
							 ?replace("9","<img border=0 src='styles/${style}/images/${pic9}'>")
			>
			${cnt}
		</#if>
	</div>
</#macro>

<#------------------------------------------------------------
 # &#x901a;&#x7528;&#x5217;&#x8868;&#x5bf9;&#x8c61;	
 #
 # &#x53c2;&#x6570;&#x5217;&#x8868;
 #			data		&#x6570;&#x636e;
 #			FIELDS		&#x663e;&#x793a;&#x5185;&#x5bb9;
 ------------------------------------------------------------->
<#macro COMMONLIST data style='default' FIELDS='' SQL='' ROWNUM=5 COLNUM=1 LISTURL='' NEWINL=0 JOPEN=0 WORDS=10 EXTEND='' MOREURL='' MORETEXT='' MOREIMG='' NEWINM=0 CLASS='' URL=''>
    <#if MORETEXT?index_of("[PAGE]") gt -1>
		<#assign page=1>
	</#if>
	<#--&#x5b9a;&#x4e49;&#x66f4;&#x591a;-->
	<#assign more='&#26356;&#22810;...'>
	<#if MOREIMG !=''>
		<#assign more='<IMG SRC="${MOREIMG}" border=0 ALT="">'>
	<#elseif MORETEXT !=''>
		<#assign more=MORETEXT>
	</#if>
	<#if NEWINM=0>
		<#assign more='<a href="${MOREURL}">${more}</a>'>
	<#else>
		<#assign more='<a href="${MOREURL}" target="_blank">${more}</a>'>
	</#if>
	
	
	<#--&#x663e;&#x793a;&#x8868;&#x683c;-->
    <table >
		    <#if COLNUM==0>
					<tr>
			</#if>	
			<#list data as y>  
			       	
			       	<#if COLNUM!=0>
						<#if y_index%COLNUM==0>
							<tr>
						</#if>
					<#else>
					</#if>
			       	
			       	<#-- &#x5904;&#x7406;&#x81ea;&#x5b9a;&#x4e49;&#x94fe;&#x63a5; -->
					<#if NEWINL==1>
					    <#assign newin="target=_blank">
					<#else>
					    <#assign newin="">
					</#if>
					<#-- &#x5904;&#x7406;&#x81ea;&#x5b9a;&#x4e49;&#x94fe;&#x63a5; -->
					<#if LISTURL?default('')?length gt 0>
							    <#assign linkaddr=LISTURL
							        ?replace("&lt;","<")
									?replace("&gt;",">")
									?replace("[FLD1]",y.FLD1?default(''))
									?replace("[FLD2]",y.FLD2?default(''))
									?replace("[FLD3]",y.FLD3?default(''))
									?replace("[FLD4]",y.FLD4?default(''))
									?replace("[FLD5]",y.FLD5?default(''))
									?replace("[FLD6]",y.FLD6?default(''))
									?replace("[FLD7]",y.FLD7?default(''))
									?replace("[FLD8]",y.FLD8?default(''))
									?replace("[FLD9]",y.FLD9?default(''))
									?replace("[BR]","<br>")
							    >
							    <#assign title="<a href='"+linkaddr+"' "+ newin +">"+"</a>">   
					   </#if>	
					   <td class='${CLASS}' ${EXTEND}>
							    <#assign st=
							    	FIELDS
							    		?replace("[title]",title?default(''))
							    		?replace("[TITLE]",y.TITLE?default(''))
							    		?replace("[FLD1]",y.FLD1?default(''))
							    		?replace("[FLD2]",y.FLD2?default(''))
							    		?replace("[FLD3]",y.FLD3?default(''))
							    		?replace("[FLD4]",y.FLD4?default(''))
							    		?replace("[FLD5]",y.FLD5?default(''))
							    		?replace("[FLD6]",y.FLD6?default(''))
							    		?replace("[FLD7]",y.FLD7?default(''))
							    		?replace("[FLD8]",y.FLD8?default(''))
							    		?replace("[FLD9]",y.FLD9?default(''))
							    		?replace("&lt;","<")
							    		?replace("&gt;",">")
							    		?replace("[BR]","<br>")
							    		?replace("[TR]","<tr>")
							    		?replace("[/TR]","</tr>")
							    		?replace("[TD]","<td>")
							    		?replace("[/TD]","</td>")
							    		?replace("[IMG]","<img border=0 src=")
										?replace("[/IMG]",">")
							            ?replace("[IMG ","<img ")
							            >
							        ${st}
						</td>
						
						<#if COLNUM!=0>
							<#if (y_index+1)%COLNUM==0>
								</tr>
							</#if>
						<#else>				
						</#if>	
						
						<#--&#x5982;&#x679c;&#x6ca1;&#x6709;&#x6570;&#x636e;&#x663e;&#x793a;&#x63d0;&#x793a;&#xff1a;-->
									
				</#list>			
				<#if COLNUM==0>
					    </tr>
				</#if>		    
		        
		        
	</table>
	
</#macro>
<#------------------------------------------------------------
 # &#x63d2;&#x5165;&#x6862;	
 ------------------------------------------------------------->
<#macro WKTIFRAME data  WIDTH='100%' HEIGHT='100%' MARGINWIDTH='0' MARGINHEIGHT='0' FRAMEBORDER='0' VSPACE='0' HSPACE='0' SCROLLING='' NAME='' ID=''>
    <iframe src='${data}'        
	        width='${WIDTH}'
	        height='${HEIGHT}'
	        marginwidth=${MARGINWIDTH}
	        marginheight=${MARGINHEIGHT}
	        frameborder=${FRAMEBORDER}
	        hspace=${HSPACE}
	        vspace=${VSPACE}
	        <#if NAME?default('')?length gt 0>
	            name='${NAME}'
	        </#if>
	        <#if ID?default('')?length gt 0>
	            id='${ID}'
	        </#if>
	        scrolling='${SCROLLING}'>
	 </iframe>
</#macro>

<#------------------------------------------------------------
 # &#x641c;&#x7d22;&#x7ed3;&#x679c;&#x663e;&#x793a;
 # data &#x6570;&#x636e;
 # style &#x6837;&#x5f0f;
 # cid &#x680f;&#x76ee;id
 # templet &#x663e;&#x793a;&#x5185;&#x5bb9;&#x7684;&#x6a21;&#x677f;
 # newwin &#x662f;&#x5426;&#x65b0;&#x7a97;&#x53e3;&#x663e;&#x793a;&#x5185;&#x5bb9; 0 &#x5426;/1 &#x662f;
 # classtitle &#x6807;&#x9898;class
 # classmemo &#x6458;&#x8981;class
 # classpage &#x7ffb;&#x9875;class
 # url &#x8fde;&#x63a5;
 # errormsg &#x9519;&#x8bef;&#x4fe1;&#x606f; 
 ------------------------------------------------------------->
<#macro WKINDEXSEARCH data='' style='default' cid='' templet='' newwin=0 classtitle='' classmemo='' classpage='' url='' errormsg=''>
	<#if errormsg != ''>
		${errormsg}
	<#else>
		<table width="100%">
			<#list data.mydata as x>
				<tr>
					<td class='${classtitle}'>
						<#assign thisurl="">
						<#if x.type="1">
							<#assign thisurl=thisurl + 'index.do?id=${x.distributeId}&templet=${templet}&cid=${x.channelId}'>
						<#else>
							<#assign thisurl=thisurl + 'apps/cms/docforward.do?id=${x.distributeId}'>
						</#if>
						<a href=${thisurl}<#if newwin==1><#if x.type!="2"> target="_blank"</#if></#if> 
					  title="${x.title?default('')}">
						${x.title?default('')}</a>
					</td>
				</tr>
				<tr>
					<td class='${classmemo}'>
					${x.summary?default('')}
					</td>
				</tr>
				<#--
				<tr>
					<td class='${classpage}'>
						<a href=${thisurl}<#if newwin==1><#if x.type!="2"> target="_blank"</#if></#if> 
					  title="${x.title?default('')}">
						${thisurl}
						</a>
					</td>
				</tr>
				-->
				<tr>
					<td><br></td>
				</tr>
			</#list>
			<#if data.mydata?size==0>
				<tr><td align=center class=${classtitle} >&lt;&#27809;&#26377;&#31526;&#21512;&#26465;&#20214;&#30340;&#25968;&#25454;&gt;</td></tr>
			</#if>
		</table>
		<#if data.pageCount gt 1>		
			<table width="100%">
			<tr><@pc.type_b data=data url=url inputclass='inputstyle' btnclass='btnstyle'/></tr>
			</table>
		</#if>
	</#if>
</#macro>

<#------------------------------------------------------------
 # &#x6ee1;&#x8db3;&#x5728;&#x6807;&#x9898;&#x680f;&#x4e2d;&#x663e;&#x793a;&#x6587;&#x7ae0;&#x6807;&#x9898;&#xff0c;&#x5173;&#x952e;&#x5b57;&#x7b49;&#x6587;&#x7ae0;&#x7c7b;&#x4fe1;&#x606f;&#x7684;&#x5b57;&#x6bb5;
 # &#x7136;&#x540e;&#x5728;&#x4fe1;&#x606f;&#x663e;&#x793a;&#x6a21;&#x677f;&#x4e2d;&#xff0c;&#x6807;&#x9898;&#x4e2d;&#x95f4;&#x52a0;&#x5165;&#x5982;:<@pub.DOCINFO FIELDS='[KI_TITLE]'/>
 # &#x5c40;&#x9650;&#x4e8e;&#x6240;&#x5199;&#x7684;ftl&#x5b8f;&#xff0c;&#x53ea;&#x652f;&#x6301;&#x6a21;&#x677f;&#x4e2d;&#x4e0d;&#x8d85;&#x8fc7;10&#x4e2a;&#x81ea;&#x5b9a;&#x4e49;&#x5bf9;&#x8c61;&#x7684;&#x60c5;&#x51b5;
 ------------------------------------------------------------->
<#macro DOCINFO FIELDS>
	<#if data0?exists && data0.docInfo?exists>
	  <#assign mydoc=data0>
	<#elseif data1?exists && data1.docInfo?exists>
	  <#assign mydoc=data1>
	<#elseif data2?exists && data2.docInfo?exists>
	  <#assign mydoc=data2>
	<#elseif data3?exists && data3.docInfo?exists>
	  <#assign mydoc=data3>
	<#elseif data4?exists && data4.docInfo?exists>
	  <#assign mydoc=data4>
	<#elseif data5?exists && data5.docInfo?exists>
	  <#assign mydoc=data5>
	<#elseif data6?exists && data6.docInfo?exists>
	  <#assign mydoc=data6>
	<#elseif data7?exists && data7.docInfo?exists>
	  <#assign mydoc=data7>
	<#elseif data8?exists && data8.docInfo?exists>
	  <#assign mydoc=data8>
	<#elseif data9?exists && data9.docInfo?exists>
	  <#assign mydoc=data9>
	<#elseif data10?exists && data10.docInfo?exists>
	  <#assign mydoc=data10>
	</#if>
	<#if mydoc?exists && mydoc.docInfo?exists>
	        <#assign dtl=FIELDS
	            ?replace("&lt;","<")
	            ?replace("&gt;",">")
	            ?replace("[KI_TITLE]",mydoc.docInfo.title?default(''))
	            ?replace("[KI_TITLE2]",mydoc.docInfo.title2?default(''))
	            ?replace("[KU_ID]",mydoc.docInfo.userId?default(''))
	            ?replace("[KU_NAME]",mydoc.docInfo.userName?default(''))
	            ?replace("[KI_HITS]",mydoc.docInfo.hit?default(0))
	            ?replace("[KB_DTIME]",mydoc.docInfo.DTime?default(''))
	            ?replace("[KB_TITLE]",mydoc.docInfo.distributeTitle?default(''))
	            ?replace("[DATE]",mydoc.docInfo.DTime?default('2005-12-12 13:30')[0..10])
	            ?replace("[DATES]",mydoc.docInfo.DTime?default('2005-12-12 13:30')[5..10])
	            ?replace("[TIME]",mydoc.docInfo.DTime?default('2005-12-12 13:30')[11..15])
	            ?replace("[KI_KEYS]",mydoc.docInfo.keys?default(''))
	        >${dtl}
	</#if>
</#macro>

