package com.uniwin.webkey.infoExtra.email;
import org.zkoss.zul.Toolbarbutton;

/**
 * <li>项目名称: xypt
 * <li>功能描述: 该文件的功能描述
 * @author DaLei
 * @version $Id: InnerButton.java,v 1.1 2012/08/01 04:11:33 whm Exp $
 */
public class InnerButton extends Toolbarbutton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8368978831153829951L;

	/**
	 * 
	 */
	public InnerButton() {
		super();
		 setSty();
	}

	/**
	 * @param label
	 * @param image
	 */
	public InnerButton(String label, String image) {
		super(label, image);
		 setSty();
	}

	/**
	 * @param label
	 */
	public InnerButton(String label) {
		super(label);
		setSty();
	}
	
	private void setSty(){
		this.setStyle("color:blue");
	}
  
}
