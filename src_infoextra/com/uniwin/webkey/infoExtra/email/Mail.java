package com.uniwin.webkey.infoExtra.email;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mail {
	private static String messageContentMimeType = "text/html; charset=gb2312";
	// ���巢���ˡ��ռ��ˡ������
	private String to = "";
	private String from = "";
	private String host = "";
	private String username="";
	private String password="";
	private String filename = "";
	private String subject = "";
	private String msgContent="";
	// ���ڱ��淢�͸������ļ���ļ���
	Vector file = new Vector();

	// ��һ����Դ������˵Ȳ���Ĺ���
	public Mail(String to, String from, String smtpServer,String username,String password, String subject,String msgContent) {
		// ��ʼ�������ˡ��ռ��ˡ������
		this.to = to;
		this.from = from;
		this.host = smtpServer;
		this.username=username;
		this.password=password;
		this.subject = subject;
		this.msgContent=msgContent;
	}

	// �÷��������ռ�������
	public void attachfile(String fname) {
		file.addElement(fname);
	}

	// ��ʼ�����ż��ķ���
	public boolean startSend() {
		// ����Properties����
		Properties props = System.getProperties();
		// �����ż�������
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		// �õ�Ĭ�ϵĶԻ�����
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication()
		    {
		        return new PasswordAuthentication(username, password);
		    }
		});
		try {
			// ����һ����Ϣ������ʼ������Ϣ�ĸ���Ԫ��
			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			// �����BodyPart�����뵽�˴�������Multipart��
			Multipart mp = new MimeMultipart();
			MimeBodyPart mBodyContent = new MimeBodyPart();
			mBodyContent.setContent(msgContent, messageContentMimeType);
			mp.addBodyPart(mBodyContent);
			// ����ö�����ı����
			Enumeration efile = file.elements();
			// ����������Ƿ��и��Ķ���
			while (efile.hasMoreElements()) {
				MimeBodyPart mbp = new MimeBodyPart();
				
				
				// ѡ���ÿһ�����
				filename = efile.nextElement().toString();
				System.out.println(filename);
				System.out.println("\n");
				// �õ����Դ
				FileDataSource fds = new FileDataSource(filename);
			
				System.out.println(fds.getName());
				// �õ��������?����BodyPart
				mbp.setDataHandler(new DataHandler(fds));
				// �õ��ļ���ͬ������BodyPart
	             sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();

	             String fileName = "=?GBK?B?" + enc.encode(fds.getName().getBytes()) + "?=";
				mbp.setFileName(fileName);
				mp.addBodyPart(mbp);
			}
			// ���߼����е�����Ԫ��
			file.removeAllElements();
			// Multipart���뵽�ż�
			msg.setContent(mp);
			// �����ż�ͷ�ķ�������
			msg.setSentDate(new Date());
			Transport trans = session.getTransport("smtp");
			try {
				trans.connect(host,username,password);
			} catch (AuthenticationFailedException e) {
				e.printStackTrace();
				System.out.println("l���ʼ����������");
				return false;
			} catch (MessagingException e) {
				System.out.println("l���ʼ����������");
				return false;
			}

			trans.send(msg);
			trans.close();
		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
			return false;
		}
		return true;
	}
	public static void main(String[] args) {
		Mail sendmail=new Mail("596214789@qq.com","yuanchao_000@126.com","smtp.126.com","yuanchao_000@126.com","p@ssw0rd","test","abcd");
		if(sendmail.startSend()){
			System.out.println("�ɹ�");
		}
	}
}
