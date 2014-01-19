   package com.uniwin.webkey.infoExtra.newsaudit;
/**
 *显示信息详情界面
 * @author whm
 * 2010-3-20
 */

import java.io.IOException;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.WkTDistribute;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.core.model.WkTInfocnt;
import com.uniwin.webkey.infoExtra.itf.NewsServices;
import com.uniwin.webkey.cms.model.WkTFile;
import com.uniwin.webkey.infoExtra.util.EncodeUtil;



public class NewsShowDetailWindow extends Window implements AfterCompose {

	private static final long serialVersionUID = 1L;
	//信息数据访问接口
	private NewsServices newsService=(NewsServices)SpringUtil.getBean("info_newsService");
	Label title,source,author,time;
	Html content;
	Button print;
	WkTInfo info;
	Hbox pics;
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
	}

	
//判断信息类型，根据类型显示不一样的界面，并获得详细信息
	public void initWindow(WkTInfo info) throws IOException
	{
		this.info=info;
		this.setTitle(info.getKiTitle());
		WkTDistribute dis=newsService.getDistri(info.getKiId(),info.getKeId());
		title.setValue(info.getKiTitle());
		source.setValue(info.getKiSource());
		author.setValue(info.getKuName());
		time.setValue(dis.getKbDtime().substring(0, 10));
		List<WkTInfocnt> list=newsService.getInfocnt(info.getKiId());
		initInfocnt(list);
		List flist=newsService.getFile(info.getKiId());
		if(flist.size()!=0)
		{ for(int i=0;i<flist.size();i++)
		{
			WkTFile file=(WkTFile) flist.get(i);
			 Image img=new Image();
			 String pa = "/upload/info"+"/"+file.getId().getKfName().trim();
				img.setSrc(pa);
				img.setWidth("200px");
				   img.setHeight("200px");
				   img.setParent(pics);
		}
		}
		print.setHref("/newsprint.do?action=newsprint&infoid=" + EncodeUtil.encodeByDES(info.getKiId())+"&disid="+EncodeUtil.encodeByDES(dis.getKbId()));
		print.setTarget("_blank");
	}	

//获取信息内容
public void  initInfocnt(List<WkTInfocnt> rlist)
	{
		String con="";
		for(int i=0;i<rlist.size();i++)
		{
			WkTInfocnt inf=rlist.get(i);
			con+=inf.getKiContent();
		}
		content.setContent(con);
	}

public void onClick$print()
{
	Clients.print();
}

}
