package com.uniwin.webkey.infoExtra.core;
/**
 * 定时采集
 */
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.uniwin.contextloader.BeanFactory;
import com.uniwin.webkey.infoExtra.itf.TaskService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.util.TimerFactory;

public class StartupServlet extends HttpServlet  {

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

		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(cfg.getServletContext());
		if(webApplicationContext == null){
			throw new RuntimeException("应用环境初始化错误");
		}

		TimerFactory.setWebApplicationContext(webApplicationContext);

	    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    TimerFactory.setSchedulerFactory(schedulerFactory);
		Scheduler scheduler = schedulerFactory.getScheduler(); 

		TaskService taskService=(TaskService) BeanFactory.getBean("taskService");

		List<WkTExtractask> tList=taskService.findAllTask();
			for(int t=0;t<tList.size();t++){
				WkTExtractask extractask=tList.get(t);
				if(extractask.getKeDefinite()!=null && !extractask.getKeDefinite().equals("") && extractask.getKePubtype()==Long.parseLong("1")
						&& extractask.getKeDefinite()>0){
					 
				    JobDetail jobDetail = new JobDetail("jobDetail"+extractask.getKeId(), "jobDetailGroup"+extractask.getKeId(), SimpleQuartzJob.class);
				    jobDetail.getJobDataMap().put("extract", extractask);
				
				    if(extractask.getKeDefinitetype().equals("0")){
				    	long period=extractask.getKeDefinite()*1000;
						long startTime = System.currentTimeMillis() + period; 
					    SimpleTrigger simpleTrigger=new SimpleTrigger("mySimTrigger"+t,null,new Date(startTime),null,SimpleTrigger.REPEAT_INDEFINITELY,period);
					    scheduler.scheduleJob(jobDetail, simpleTrigger);
					    
				    }else{
				    	
				    	Date d=new Date(extractask.getKeDefinite());
				    	int h=d.getHours();
				    	int m=d.getMinutes();
				    	
				    	try {
				    		
				    		 CronExpression expression=new CronExpression("0 "+m+" "+h+" * * ?");
							 CronTrigger cronTrigger=new CronTrigger("myCronTrigger"+t,null,expression.getCronExpression());
							 scheduler.scheduleJob(jobDetail, cronTrigger);
						
				    	} catch (ParseException e) {
							e.printStackTrace();
						}
				    	
				    }
				    scheduler.start();
				    
				}
				
			}
       
       
}

}

