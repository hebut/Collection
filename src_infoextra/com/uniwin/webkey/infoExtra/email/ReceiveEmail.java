package com.uniwin.webkey.infoExtra.email;

import javax.mail.*;
import javax.mail.internet.*;



import com.sun.mail.pop3.POP3Folder;
import com.uniwin.webkey.core.model.Users;
import com.uniwin.webkey.infoExtra.model.ReceiveMail;


import java.util.*;
import java.io.*;

/*
 Title: ʹ��JavaMail�����ʼ�
 Description: ʵ��JavaMail������ʼ�����ʵ��û��ʵ�ֽ����ʼ��ĸ�����
 Copyright: Copyright (c) 2003
 Filename: POPMail.java
 @version 1.0
 */
public class ReceiveEmail {
//	/*
//	 * ����˵������������û����������������û�������� ������� �������ͣ�
//	 */
//	public static void main(String args[]) {
//		try {
//			String popServer = "pop.qq.com"; // �ʼ�������
//			String popUser = "596214789@qq.com"; // ������ѵ��û����޸�
//			String popPassword = "p@ssw0rd"; // ������ѵ������޸�
//			receive(popServer, popUser, popPassword);
//		} catch (Exception ex) {
//			System.out.println("Usage: java com.lotontech.mail.POPMail"
//					+ " popServer popUser popPassword");
//		}
//		System.exit(0);
//	}

	/*
	 * ����˵������ʼ���Ϣ ������� �������ͣ�
	 */
	public static List<ReceiveMail> receive(String popServer, String popUser,
			String popPassword,Set<String> hasReadSet,Users user) {
		Store store = null;
		Folder folder = null;
		List<ReceiveMail> list=new ArrayList<ReceiveMail>();
		try {
			// ��ȡĬ�ϻỰ
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props, null);
			// ʹ��POP3�Ự���ƣ�l�ӷ�����
			store = session.getStore("pop3");
			store.connect(popServer, popUser, popPassword);
			// ��ȡĬ���ļ���
			folder = store.getDefaultFolder();
			if (folder == null)
				throw new Exception("No default folder");
			// ������ռ���
			folder = folder.getFolder("INBOX");
			if (folder == null)
				throw new Exception("No POP3 INBOX");
			// ʹ��ֻ�w�ʽ���ռ���
			folder.open(Folder.READ_ONLY);
			// �õ��ļ�����Ϣ����ȡ�ʼ��б�
			Message[] msgs = folder.getMessages();
			POP3Folder inbox = (POP3Folder) folder;
			for (int msgNum = 0; msgNum < msgs.length; msgNum++) {
				String uid = inbox.getUID(msgs[msgNum]);
				if(!hasReadSet.contains(uid)){
					ReceiveMail e=new ReceiveMail();
					Message message=msgs[msgNum];
					String from = ((InternetAddress) message.getFrom()[0])
							.getPersonal();
					if (from == null)
						from = ((InternetAddress) message.getFrom()[0]).getAddress();
				
					if(from.startsWith("=?")){
						e.setMailfrom(new String(from.getBytes("ISO-8859-1"), "gb2312"));
					}else{
						e.setMailfrom(from);
					}
					
					if(message.getSubject().startsWith("=?")){
						e.setSubject(new String(message.getSubject().getBytes("ISO-8859-1"), "gb2312"));
					}else{
						e.setSubject(message.getSubject());
					}					
					e.setUid(uid);
					// ��ȡ��Ϣ����
					Part messagePart = message;
					Object content = messagePart.getContent();
					// ����
					if (content instanceof Multipart) {
						messagePart = ((Multipart) content).getBodyPart(0);
						System.out.println("[ Multipart Message ]");
					}
					// ��ȡcontent����
					String contentType = messagePart.getContentType();
					// ����ʼ������Ǵ��ı�������HTML����ô��ӡ����Ϣ
					//System.out.println("CONTENT:" + contentType);
					StringBuilder sb=new StringBuilder();
					if (contentType.startsWith("text/plain")
							|| contentType.startsWith("text/html")) {
						InputStream is = messagePart.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is));
						String thisLine = reader.readLine();
						while (thisLine != null) {
							//System.out.println(thisLine);
							sb.append(thisLine);
							thisLine = reader.readLine();
						}
					}
					e.setContent(sb.toString());
					e.setMaildate(ConvertUtil.convertDateAndTimeString(message.getSentDate()));
					e.setUser(user);
					list.add(e);
				}
//				System.out.println(uid);
				//printMessage(msgs[msgNum]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// �ͷ���Դ
			try {
				if (folder != null)
					folder.close(false);
				if (store != null)
					store.close();
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}
		return list;
	}

	/*
	 * ����˵���ӡ�ʼ���Ϣ �������Message message ��Ϣ���� �������ͣ�
	 */
	public static void printMessage(Message message) {
		try {
			// ��÷����ʼ���ַ
			String from = ((InternetAddress) message.getFrom()[0])
					.getPersonal();
			if (from == null)
				from = ((InternetAddress) message.getFrom()[0]).getAddress();
			System.out.println("FROM: " + from);
			// ��ȡ����
			String subject = message.getSubject();
			System.out.println("SUBJECT: " + subject);
			// ��ȡ��Ϣ����
			Part messagePart = message;
			Object content = messagePart.getContent();
			// ����
			if (content instanceof Multipart) {
				messagePart = ((Multipart) content).getBodyPart(0);
				System.out.println("[ Multipart Message ]");
			}
			// ��ȡcontent����
			String contentType = messagePart.getContentType();
			// ����ʼ������Ǵ��ı�������HTML����ô��ӡ����Ϣ
			System.out.println("CONTENT:" + contentType);
			if (contentType.startsWith("text/plain")
					|| contentType.startsWith("text/html")) {
				InputStream is = messagePart.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String thisLine = reader.readLine();
				while (thisLine != null) {
					System.out.println(thisLine);
					thisLine = reader.readLine();
				}
			}
			System.out.println("-------------- END ---------------");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static  String toChinese(String strvalue) throws UnsupportedEncodingException{   

		if(strvalue!=null){
			if(strvalue.startsWith("=?")){
				strvalue = MimeUtility.decodeText(strvalue);
			}else{
				strvalue=new String(strvalue.getBytes("ISO-8859-1"), "gb2312");
			}
			 return strvalue; 
    	}else{
    		strvalue="";
    		 return strvalue; 
    	}
	}   

}