package com.qingwenwei.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service("emailService")
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	// asynchronous function
	// requires EnableAsync annotation in application class - main method
	@Async
	public void sendEmail(MimeMessage email) {
		try {
			mailSender.send(email);
			System.out.println("邮件发送成功！");
		} catch (Exception e) {
			System.out.println("发送邮件时发生异常！"+e);
		}

	}
}