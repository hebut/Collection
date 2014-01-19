package com.uniwin.webkey.infoExtra.core;

public class DataBaseSet {

	public DataBaseSet() {
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * 1、oracle 数据库wktoricnt 数据存储字段长度， 不同编码存储长度不同,varchar（4000），在utf-8的编码下，
	 * 最多可以存储666个汉字，660L;
	 * 2、mysql 数据库内容长度，设置为text，3000L
	 * 3、sql 数据库内容长度，设置为text，3000L
	 * 
	 * */

	public static Long data_Len=3000l;
	public static Long getData_Len() {
		return data_Len;
	}

	public static void setData_Len(Long data_Len) {
		DataBaseSet.data_Len = data_Len;
	}
	
}
