package com.uniwin.webkey.infoExtra.email;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <li>��������:����ת������Ҫwebkey��ݿ��������ַ�java�������ڻ���ת��
 * 
 * @author DaLei
 * @version 1.0
 */
public class DateUtil {
	/**
	 * <li>�����������ݿ��д洢�����������ַ�ת��Ϊ���ڸ�ʽ�ַ�
	 * 
	 * @param ����ȫ�����ַ�
	 * @return String
	 * @author DaLei
	 * @2010-3-15
	 */
	static DateFormat albumDate = new SimpleDateFormat("yyyyMMdd");
	static DateFormat albumDate2 = new SimpleDateFormat("yyyy-MM-dd");
	static DateFormat albumTime = new SimpleDateFormat("yyyyMMddHHmmss");
	static DateFormat albumTime2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	public static String getDateString(String arg) {
		StringBuffer sb = new StringBuffer(arg.substring(0, 4));
		sb.append("-" + arg.substring(4, 6));
		sb.append("-" + arg.substring(6, 8));
		return sb.toString();
	}

	/**
	 * <li>�����������ݿ��д洢��ʱ���������ַ�ת��Ϊʱ���ʽ�ַ�
	 * 
	 * @param ʱ����ȫ�����ַ�
	 * @return String
	 * @author DaLei
	 * @2010-3-15
	 */
	public static String getHourString(String arg) {
		if (arg.length() < 6)
			arg = "0" + arg;
		StringBuffer sb = new StringBuffer(arg.substring(0, 2));
		sb.append(":" + arg.substring(2, 4));
		sb.append(":" + arg.subSequence(4, 6));
		return sb.toString();
	}

	/**
	 * <li>�����������ݿ��д洢������ʱ�������ַ�ת��Ϊ���ڸ�ʽ�ַ�
	 * 
	 * @param ���ڼ�ʱ��ȫ�����ַ�
	 * @return String
	 * @author DaLei
	 * @2010-3-15
	 */
	public static String getTimeString(String arg) {
		if (arg == null || arg.trim().length() < 12) {
			return "δ��¼";
		}
		StringBuffer sb = new StringBuffer(arg.substring(0, 4));
		sb.append("-" + arg.substring(4, 6));
		sb.append("-" + arg.substring(6, 8));
		sb.append(" " + arg.substring(8, 10));
		sb.append(":" + arg.substring(10, 12));
		return sb.toString();
	}

	/**
	 * <li>���������java��������ת��Ϊȫ���ֵ��ַ�,��ȡ������
	 * 
	 * @param d
	 * @return String
	 * @author DaLei
	 * @2010-3-20
	 */
	public static String getDateString(Date d) {
		return albumDate.format(d);
	}

	/**
	 * <li>���������java��������ת��Ϊȫ���ֵ��ַ�,��ȡ����
	 * 
	 * @param d
	 * @return String
	 * @author DaLei
	 * @2010-3-20
	 */
	public static String getDateTimeString(Date d) {
		return albumTime.format(d);
	}

	/**
	 * <li>�����������ַ�date���������Ͷ���
	 * 
	 * @param date
	 * @return Date
	 * @author DaLei
	 */
	public static Date getDate(String date) {
		try {
			return albumDate.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date getDate2(String date) {
		try {
			return albumDate2.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <li>�����������ַ����ں�ʱ���������ڡ�
	 * 
	 * @param date
	 * @return Date
	 * @author DaLei
	 */
	public static Date getDateAndTime(String date) {
		try {
			return albumTime.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <li>�����������ڼ�����õ��µ�����
	 * 
	 * @return Date
	 * @2010-7-22
	 */
	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);
	}

	public static String getDate(Date date){
		return albumTime2.format(date);
	}
	public static void main(String[] args) {
		System.out.println(DateUtil.getDate(new Date()));
	}
}
