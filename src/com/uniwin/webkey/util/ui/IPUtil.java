package com.uniwin.webkey.util.ui;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
	public static String getRemortIP(HttpServletRequest request) {  
	    if (request.getHeader("x-forwarded-for") == null) {  
	        return request.getRemoteAddr();  
	    }  
	    return request.getHeader("x-forwarded-for");  
	} 
}
