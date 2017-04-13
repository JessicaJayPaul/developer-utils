import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailerUtil {

	public static final String SERVER = "smtp.163.com";
	public static final String FROM = "13437104137@163.com";
	public static final String FROM_ALIAS = "TAO";
	public static final String PWD = "cjt009764";
	public static final String DEFAULT_ENCODE = "UTF-8";

	private static MailerUtil util = new MailerUtil();

	private Session session;
	
	private Transport transport;

	private MailerUtil() {
		// 1.初始化邮件服务器连接配置
		Properties props = initProperties();
		// 2.获取Session
		session = Session.getDefaultInstance(props);
		session.setDebug(true);
		// 3.获取邮件传输对象
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	public static MailerUtil getInstance() {
		return util;
	}

	public Properties initProperties() {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", SERVER);
		props.setProperty("mail.smtp.auth", "true");
		return props;
	}
	
	public void sendMail(String subject, String to, String content){
		sendMail(subject, to, to, content);
	}
	
	public void sendMail(String subject, String to, String toAlias, String content){
		sendMail(subject, FROM, FROM_ALIAS, to, toAlias, content);
	}
	
	/**
	 * 发送邮件
	 */
	public void sendMail(String subject, String from, String fromAlias, String to, String toAlias, String content){
		MimeMessage mail = createMail(subject, from, fromAlias, to, toAlias, content);
		try {
			transport.connect(FROM, PWD);
			transport.sendMessage(mail, mail.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一封邮件
	 */
	public MimeMessage createMail(String subject, String from, String fromAlias, String to, String toAlias, String content) {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);
		try {
			// 2. Subject: 邮件主题
			message.setSubject(subject, "UTF-8");
			// 3. From: 发件人
			message.setFrom(new InternetAddress(from, fromAlias, DEFAULT_ENCODE));
			// 4. To: 收件人（可以增加多个收件人、抄送、密送）
			message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to, toAlias, DEFAULT_ENCODE));
			// 5. Content: 邮件正文（可以使用html标签）
			message.setContent(content, "text/html;charset=UTF-8");
			// 6. 设置发件时间
			message.setSentDate(new Date());
			// 7. 保存设置
			message.saveChanges();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return message;
	}
}