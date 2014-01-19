package com.uniwin.webkey.infoExtra.core;
/**
 * 定时采集
 */


import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.uniwin.contextloader.BeanFactory;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.itf.UserService;

public class StartupSendServlet extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public void init(ServletConfig cfg) throws javax.servlet.ServletException

	{

		try {
			initScheduler(cfg);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}


	protected void initScheduler(ServletConfig cfg)throws SchedulerException

	{
	    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler(); 
	    UserService userService = (UserService)BeanFactory.getBean("UserService");
	    List<Users> uList = userService.findAllUsers();
	    for(int i=0;i < uList.size(); i++){
	    	Users user = (Users)uList.get(i);
	    	Integer etime = user.getKuETime();
	    	if(user.getKuEUserTo()!=null && !user.getKuEUserTo().equals("")){
			    JobDetail jobDetail = new JobDetail("jobDetail11"+i, "jobDetailGroup11"+i, SendSimpleQuartzJob.class);
			    jobDetail.getJobDataMap().put("user", user);
			    jobDetail.getJobDataMap().put("userService", userService);
		    	long period=etime*24*3600*1000;
				long startTime = System.currentTimeMillis() + period; 
			    SimpleTrigger simpleTrigger=new SimpleTrigger("mySimTrigger1"+i,null,new Date(startTime),null,SimpleTrigger.REPEAT_INDEFINITELY,period);
			    scheduler.scheduleJob(jobDetail, simpleTrigger);
			    scheduler.start();
		
	    	}	    	
	    }		      
	}

	
}

