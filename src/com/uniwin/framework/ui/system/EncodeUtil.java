package com.uniwin.framework.ui.system;

/**
 * <li>MD5加密组件,用来对用户密码进行加密存储
 * @author DaLei
 * @2010-3-15
 */
import java.util.Date;

public class EncodeUtil {
	/**
	 * <li>功能描述：调用JDK自带MD5加密对字符此进行加密
	 * 
	 * @param arg要加密的字符串
	 * @return String 加密后字符串
	 * @author DaLei
	 */
	public static String encodeByMD5(String arg) {
		return arg;
	}

	/**
	 * <li>功能描述：将JDK自带加密后的二进制流转换为字符串。
	 * 
	 * @param b
	 * @return String
	 * @author DaLei
	 * @2010-3-15
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static void main(String args[]) {
		String e1 = EncodeUtil.encodeByMD5("");
		System.out.println(e1);
		String e2 = EncodeUtil.encodeByMD5(e1);
		System.out.println(EncodeUtil.encodeByMD5(e2));
	}

	public static String encodeByDES(String buid) {
		DESPlus des;
		try {
			des = new DESPlus("2011-11-11");
			return des.encrypt(buid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buid;
	}

	public static String encodeByDES(Long buid) {
		DESPlus des;
		try {
			des = new DESPlus("2011-11-11");
			return des.encrypt(buid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buid + "";
	}

	public static Long decodeByDES(String buid) {
		DESPlus des;
		try {
			des = new DESPlus("2011-11-11");
			return Long.parseLong(des.decrypt(buid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}
	public static String decodeByDESStr(String buid) {
		DESPlus des;
		try {
			des = new DESPlus("2011-11-11");
			return String.valueOf(des.decrypt(buid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
