package com.uniwin.framework.ui.system;

/**
 * <li>用户登录组件,对应页面admin/alogin.zul
 * @author DaLei
 * @2010-3-15
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.uniwin.webkey.cms.itf.WebsiteService;
import com.uniwin.webkey.core.model.WkTSite;

public class UserLoginWindow extends Window implements AfterCompose {
	
	Textbox j_username, j_password;
	Datebox endtime;
	
	Button resetButton, resignButton;
	private WebsiteService websiteService=(WebsiteService)SpringUtil.getBean("websiteService");
	

	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		List list =websiteService.findAll();
		WkTSite sit=(WkTSite) list.get(0);
		if(sit.getKsEndtime()!=null&&sit.getKsEndtime().trim().length()!=0){
			endtime.setValue(new Date(EncodeUtil.decodeByDES(sit.getKsEndtime())));
		}
		
	}
	private static String getMacOnWindow() {
        String s = "";
        try {
            String s1 = "ipconfig /all";
            Process process = Runtime.getRuntime().exec(s1);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String nextLine;
            for (String line = bufferedreader.readLine(); line != null; line = nextLine) {
                nextLine = bufferedreader.readLine();
                if (line.indexOf("Physical Address") <= 0) {
                    continue;
                }
                int i = line.indexOf("Physical Address") + 36;
                s = line.substring(i);
                break;
            }
            bufferedreader.close();
            process.waitFor();
        } catch (Exception exception) {
            s = "";
        }
        return s.trim();
    }



	public void onClick$resignButton() throws Exception {
		if (j_username.getValue().length() == 0) {
			return;
		}
		String lid=EncodeUtil.encodeByDES(j_username.getValue().toString().trim());
		String password = EncodeUtil.encodeByDES(j_password.getValue().toString().trim());
		String et=EncodeUtil.encodeByDES(endtime.getValue().getTime()).trim();
		List ulist = websiteService.findByLidAndPassword(lid,password);
		if (ulist == null || ulist.size() == 0) {
				throw new WrongValueException(j_username, "用户名或密码错误");
		}else{
			String mac=EncodeUtil.encodeByDES(getMacOnWindow().trim());
			List slist=websiteService.findByLidAndPasswordAndMac(lid, password, mac);
			if(slist.size()!=0){
				WkTSite sit=(WkTSite) slist.get(0);
				sit.setKsEndtime(et);
				websiteService.update(sit);
				Messagebox.show("该用户已注册", "提示ʾ", Messagebox.OK,Messagebox.INFORMATION);
			}else{
				WkTSite site=(WkTSite) ulist.get(0);
				site.setKsMac(mac);
				site.setKsEndtime(et);
				websiteService.update(site);
				Messagebox.show("注册成功 ", "提示", Messagebox.OK,Messagebox.INFORMATION);
			}
		}
	
	}

	//重置事件
	public void onClick$resetButton() {
		j_username.setRawValue(null);
		j_password.setRawValue(null);
	}

}
