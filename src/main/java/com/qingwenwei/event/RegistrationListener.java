package com.qingwenwei.event;

import java.util.UUID;

import com.qingwenwei.util.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.qingwenwei.persistence.dao.UserMapper;
import com.qingwenwei.persistence.dao.VerificationTokenMapper;
import com.qingwenwei.persistence.model.User;
import com.qingwenwei.persistence.model.VerificationToken;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private static final String VERIFICATION_EMAIL_FROM_ADDR = "zzh390921606@163.com";

	private static final String EMAIL_NAME = "工单系统";

	private static final String VERIFICATION_EMAIL_SUBJECT = "用户激活";

	private static final String CONFIRM_ENDPOINT = "registration-confirm";

	private static final Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

	@Autowired
	private VerificationTokenMapper verificationTokenMapper;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JavaMailSender jms;

	@Autowired
	private UserMapper userMapper;

	// root URL of service
	@Value("${service.url}")
	private String serviceUrl;

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
	}

	private void confirmRegistration(final OnRegistrationCompleteEvent event) {
		logger.info("confirmRegistration() >> " + event);
		String username = event.getUsername();
		this.createUserVerificationToken(username);
	}

	private void createUserVerificationToken(String username) {
		String token = UUID.randomUUID().toString(); // token string
		User user = this.userMapper.findByUsername(username);
		VerificationToken verificationToken = new VerificationToken(user, token);
		this.verificationTokenMapper.save(verificationToken);

		// construct verification email
		//SimpleMailMessage email = new SimpleMailMessage();

		// confirmation link in email
		String confirmationLink = serviceUrl + "/user/" + CONFIRM_ENDPOINT + "?token=" + token;
		//System.out.println("confirmation link >> " + confirmationLink);
		//email.setFrom(VERIFICATION_EMAIL_FROM_ADDR);
		//email.setSubject(VERIFICATION_EMAIL_SUBJECT);

		String emailMsg = "感谢您注册，点击"
				 + "<a href='"+ confirmationLink + "'>&nbsp;激活&nbsp;</a>"+"后使用。"
				+ "为保障您的账户安全，请在24小时内完成激活操作";
		//email.setText(emailMsg);
		//email.setTo(user.getEmail());


		MimeMessage mimeMessage = jms.createMimeMessage();
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(new InternetAddress(VERIFICATION_EMAIL_FROM_ADDR, "Company XYZ"));
			messageHelper.setTo(user.getEmail());
			messageHelper.setSubject(VERIFICATION_EMAIL_SUBJECT);
			String html = emailMsg;
			messageHelper.setText(html, true);
			this.emailService.sendEmail(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		 send email asynchronously

	}


}
