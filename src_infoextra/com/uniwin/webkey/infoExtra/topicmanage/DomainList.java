package com.uniwin.webkey.infoExtra.topicmanage;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.uniwin.webkey.core.model.User;
import com.uniwin.webkey.core.model.WkTInfo;
import com.uniwin.webkey.infoExtra.email.Mail;
import com.uniwin.webkey.infoExtra.email.UploadUtil;
import com.uniwin.webkey.infoExtra.itf.InfoDomainService;
import com.uniwin.webkey.infoExtra.itf.InfoIdAndDomainId;
import com.uniwin.webkey.infoExtra.itf.InfoSortService;
import com.uniwin.webkey.infoExtra.model.WkTInfoDomain;
import com.uniwin.webkey.infoExtra.model.WkTInfoSort;
import com.uniwin.webkey.infoExtra.model.WkTOrifile;
import com.uniwin.webkey.infoExtra.model.WkTUserIdAnddomainId;
import com.uniwin.webkey.util.ui.FrameCommonDate;

public class DomainList extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	InfoSortService infosortService;
	InfoDomainService infodomainService;
	InfoIdAndDomainId infoIdAndDomainId;

	Grid domainGrid;
	Rows domainRows;
	Checkbox radio;
	List<Checkbox> radioList = new ArrayList<Checkbox>();
	List<WkTInfoDomain> list2;
	List<WkTInfoDomain> dList;
	List<WkTInfoDomain> listed = new ArrayList<WkTInfoDomain>();
	WkTInfoSort wkTInfoSort;
	WkTUserIdAnddomainId userIdAnddomainId;
	User user;
	List<WkTUserIdAnddomainId> useless;
	
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);

	}

	private void loadGrid(List<WkTInfoDomain> dList) {

		if (dList != null && dList.size() > 0) {

			int count = (double) dList.size() / (double) 2 > (dList.size() / 2) ? dList
					.size() / 2 + 1 : dList.size() / 2;
			if (dList.size() % 2 == 0) {
				int jshu = 0;
				for (int r = 0; r < count; r++) {
					Row row = new Row();
					for (int c = 0; c < 2; c++) {
						radio = new Checkbox();
						radio.setAttribute("radioValue", dList.get(jshu));
						radioList.add(radio);
						radio.setLabel(dList.get(jshu).getKiName());
						row.appendChild(radio);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				domainGrid.appendChild(domainRows);
			} else {
				int jshu = 0;
				Row row;
				for (int r = 0; r < count - 1; r++) {
					row = new Row();
					for (int c = 0; c < 2; c++) {
						radio = new Checkbox();
						radioList.add(radio);
						radio.setAttribute("radioValue", dList.get(jshu));
						radio.setLabel(dList.get(jshu).getKiName());
						row.appendChild(radio);
						jshu++;
					}
					domainRows.appendChild(row);
				}
				Row row2 = new Row();
				for (int left = 0; left < dList.size() % 2; left++) {
					radio = new Checkbox();
					radio.setAttribute("radioValue", dList.get(jshu));
					radio.setLabel(dList.get(jshu).getKiName());
					radioList.add(radio);
					row2.appendChild(radio);
					domainRows.appendChild(row2);
					jshu++;
				}
				domainGrid.appendChild(domainRows);
			}
		}
		initGrid();
	}

	public void initGrid(){
		useless = infosortService.findByUserIdandSortId( user.getUserId(),wkTInfoSort.getKsId());
	
		for (int i = 0; i < radioList.size(); i++) {
			Checkbox radio1 = (Checkbox) radioList.get(i);
			WkTInfoDomain domain = (WkTInfoDomain) radio1
					.getAttribute("radioValue");
			if (useless.size() != 0) {
				for (int j = 0; j < useless.size(); j++) {
					WkTUserIdAnddomainId file = (WkTUserIdAnddomainId) useless.get(j);
					
					if(domain.getKiId().equals(file.getKiId())){
						radio1.setChecked(true);
					}
				}
			}
		}
	}
	
	public void initWindow(WkTInfoSort infoSort) {
		this.wkTInfoSort = infoSort;
		user = FrameCommonDate.getUser();
		dList = infodomainService.findBySortId(infoSort.getKsId());
		loadGrid(dList);
	}
	
	public void save(){
		if(wkTInfoSort == null){
			return;
		}
//		对比有无修改
		useless = infosortService.findByUserIdandSortId(user.getUserId(),wkTInfoSort.getKsId());
		List<Checkbox> list2=new ArrayList<Checkbox>();
		for (int i = 0; i < radioList.size(); i++) {
			Checkbox radio1 = (Checkbox) radioList.get(i);
			if (radio1.isChecked()) {
				 list2.add(radio1);}
		}
         if(list2.size()==useless.size()){
        		return;
         }
		
		// 删除原始信息表内容
		if (useless.size() != 0) {
			for (int j = 0; j < useless.size(); j++) {
				WkTUserIdAnddomainId file = (WkTUserIdAnddomainId) useless
						.get(j);
				infosortService.delete(file);
			}
		}
		// 保存新内容
				for (int i = 0; i < radioList.size(); i++) {
					Checkbox radio1 = (Checkbox) radioList.get(i);
					if (radio1.isChecked()) {
						WkTInfoDomain domain = (WkTInfoDomain) radio1
								.getAttribute("radioValue");
						userIdAnddomainId = new WkTUserIdAnddomainId();
						userIdAnddomainId.setKuId(user.getUserId());
						userIdAnddomainId.setKiId(domain.getKiId());
						userIdAnddomainId.setKsId(domain.getKsId());
						infosortService.save(userIdAnddomainId);
					}
				}
	}
	
	
	public void onClick$save() throws InterruptedException {
		// 删除原始信息表内容
		if (useless.size() != 0) {
			for (int j = 0; j < useless.size(); j++) {
				WkTUserIdAnddomainId file = (WkTUserIdAnddomainId) useless
						.get(j);
				infosortService.delete(file);
			}
		}
		
		// 保存新内容
		for (int i = 0; i < radioList.size(); i++) {
			Checkbox radio1 = (Checkbox) radioList.get(i);
			if (radio1.isChecked()) {
				WkTInfoDomain domain = (WkTInfoDomain) radio1
						.getAttribute("radioValue");
				userIdAnddomainId = new WkTUserIdAnddomainId();
				userIdAnddomainId.setKuId(user.getUserId());
				userIdAnddomainId.setKiId(domain.getKiId());
				userIdAnddomainId.setKsId(domain.getKsId());
				infosortService.save(userIdAnddomainId);
			}
		}

		Messagebox.show("保存成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
	}
		
}
