package com.busi.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * 发送邮件
 *   
 * @author SunTianJie
 * 
 * @version create time：2017-5-22 下午5:30:47
 * 
 */
public class SendEmail {

	public String myEmailAccount = "lichengwang_vip@163.com";// 发件人邮箱
	public String myEmailPassword = "yx28000";// 发件人邮箱没密码
	public String myEmailSMTPHost = "smtp.163.com";// 邮箱服务器地址
	public String receiveMailAccount = "552440192@qq.com";// 收件人邮箱

	public void send(String receiveMailAccount, String subject,String mailMessge) {
		// 1. 创建参数配置, 用于连接邮件服务器的参数配置
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.smtp.host", myEmailSMTPHost); // 发件人的邮箱的 SMTP
		props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
		// 2. 根据配置创建会话对象, 用于和邮件服务器交互
		Transport transport = null;
		try {
			Session session = Session.getDefaultInstance(props);
//			session.setDebug(true);// 设置为debug模式, 可以查看详细的发送 log
			session.setDebug(false);// 设置为debug模式, 可以查看详细的发送 log
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myEmailAccount));
			message.setContent(mailMessge, "text/html;charset=utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMailAccount));
			message.setSentDate(new Date());
			message.setSubject(subject);
			message.saveChanges();
			// 4. 根据 Session 获取邮件传输对象
			transport = session.getTransport();
			// 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
			transport.connect(myEmailAccount, myEmailPassword);
			// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients()
			// 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
			transport.sendMessage(message, message.getAllRecipients());
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 7. 关闭连接
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
