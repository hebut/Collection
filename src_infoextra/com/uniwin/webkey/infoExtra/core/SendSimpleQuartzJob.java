package com.uniwin.webkey.infoExtra.core;
/**
 * 定时采集
 */


import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Messagebox;

import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.email.Mail;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.UserIdAnddomainId;
import com.uniwin.webkey.infoExtra.itf.UserService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTUserIdAnddomainId;
import com.uniwin.webkey.infoExtra.util.BeanFactory;
public class SendSimpleQuartzJob implements Job{

	
	private InfoIdAndDomainId ss = (InfoIdAndDomainId) BeanFactory.getBean("infoIdAndDomainIdService");
	private UserIdAnddomainId us = (UserIdAnddomainId) BeanFactory.getBean("userIdAnddomainIdService");
	private UserService userService;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
				
				/*JobDataMap dataMap = context.getJobDetail().getJobDataMap();              
				String jobSays = dataMap.getString("extract");   
				System.out.println(jobSays);*/
		    JobDataMap dataMap = context.getJobDetail().getJobDataMap(); 	
		    Users user = (Users) dataMap.get("user");
		    userService = (UserService)dataMap.get("userService");
		    String euser1 = user.getKuEUserTo();
			String[] euser2 = euser1.split(",");
			for(String euser3:euser2){
				System.out.println(euser3);
				String HTML2="<title>新闻定制</title><style type=" + "text/css" + ">body {text-align: center;margin: 0px;padding: 0px;color:#5F7A77;}div, p{padding:0px; margin:0px;}h4{color:red;}";
				String HTML3=".top {background-color: #eaf7f7;border: 1px solid #cfedec;color: #5f7a77;width: 600px;font-size: 20px;margin-top: 0px;margin-right: auto;margin-bottom: 0px;margin-left: auto;position: relative;padding: 10px;height: 30px;clear: both;}";
				String HTML4=".txt {border: 1px solid #b1ded5;clear: both;width: 600px;margin-top: 10px;position: relative;padding: 10px;text-align: left;font-size: 15px;line-height: 20px;margin-right: auto;margin-left: auto;}";
				String HTMLMail =HTML2+HTML3+HTML4+"</style><br><div class=top><p align=center>" + "新闻定制" + "</p></div><div class=txt>";
				Long euser4 = Long.parseLong(euser3);
				Integer euser5 = Integer.parseInt(euser3);
				Users sendUser = userService.findMailById(euser5);
				List<WkTUserIdAnddomainId> udList1 = new ArrayList<WkTUserIdAnddomainId>();
				udList1 = us.findAllById(euser4);
				String domainName = null;
				boolean flag = true;
				for(int i=0;i<udList1.size();i++){
					WkTUserIdAnddomainId wid = (WkTUserIdAnddomainId)udList1.get(i);
					WkTInfoDomain infoDomain = us.findDomainNameById(wid.getKiId());
					domainName = infoDomain.getKiName();
					//System.out.println("第" + infoDomain.getKiId() +"分类" + domainName);
					List<WkTInfo> uiList = new ArrayList<WkTInfo>();
					uiList = ss.findWKTInfoByDomainId(wid.getKiId(),user.getKuETime());
					if(uiList.size()>0){
						HTMLMail = HTMLMail + domainName + "</p>";
						String hh = "<p><a ";
						for(int k=0;k<uiList.size();k++){
							WkTInfo inw = (WkTInfo)uiList.get(k);
							hh = hh + "href=" + inw.getKiUrl() +">"+inw.getKiTitle()+"("+inw.getKiCtime()+")" + "</a></p><br><p><a ";
							//System.out.println(inw.getKiId()+"第"+ k + "条信息："+inw.getKiTitle());
						}
						HTMLMail = HTMLMail + hh + "</a></p>";	
					}
					else{
						HTMLMail = HTMLMail + "<p>" + domainName + "</p><h4>"+"本分类无更新内容"+"</h4>";
					}

				 }
				HTMLMail = HTMLMail + "</div>";
				Mail sendmail=new Mail(sendUser.getKuEmail(),"iti_213@126.com","smtp.126.com","iti_213@126.com","iti213","新闻定制",HTMLMail);
				if(!sendmail.startSend()){
					flag=false;
				}
			    if(flag){
				    System.out.println("用户ID:"+ euser3 + "定制新闻邮件发送成功");
			    }
			    else{
			    	System.out.println("用户ID:"+ euser3 + "定制新闻邮件发送失败");
		        }
		     }
	   }	
}
         
	
	

