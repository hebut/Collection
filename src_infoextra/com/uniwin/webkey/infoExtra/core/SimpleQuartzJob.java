package com.uniwin.webkey.infoExtra.core;
/**
 * 定时采集
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.uniwin.webkey.infoExtra.core.MyCallableSave;
import com.uniwin.webkey.infoExtra.itf.GuideService;
import com.uniwin.webkey.infoExtra.itf.InfoService;
import com.uniwin.webkey.infoExtra.itf.LinkService;
import com.uniwin.webkey.infoExtra.itf.PickService;
import com.uniwin.webkey.infoExtra.model.WkTExtractask;
import com.uniwin.webkey.infoExtra.model.WkTGuidereg;
import com.uniwin.webkey.infoExtra.model.WkTPickreg;
import com.uniwin.webkey.infoExtra.util.BeanFactory;

public class SimpleQuartzJob implements Job{

	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
				
				/*JobDataMap dataMap = context.getJobDetail().getJobDataMap();              
				String jobSays = dataMap.getString("extract");   
				System.out.println(jobSays);*/
			
			GuideService guideService=(GuideService) BeanFactory.getBean("guideService");
			PickService pickService=(PickService) BeanFactory.getBean("pickService");
			LinkService linkService=(LinkService) BeanFactory.getBean("linkService");
			InfoService infoService=(InfoService) BeanFactory.getBean("infoService");
	        String path=BeanFactory.getMhtPath();
	        String path1=BeanFactory.getHtmlPath();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap(); 	
			WkTExtractask extractask=(WkTExtractask) dataMap.get("extract");
			
			System.out.println("定时采集开始--任务--"+extractask.getKeName()+"开始采集");
			if(extractask.getKeStatus().toString().equals("2"))
			{
			String dateResult=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
			extractask.setKeTime(dateResult);
			infoService.update(extractask);
			}
			Long taskId=extractask.getKeId();
			List<WkTGuidereg> guideList=guideService.findGuideListById(taskId);
			List<WkTPickreg> pickRegList=pickService.findpickReg(taskId);
			MyCallableSave mSave=new MyCallableSave(extractask,guideList,pickRegList,linkService,infoService,path,path1);
			mSave.call();
		
	}
	
	
}
