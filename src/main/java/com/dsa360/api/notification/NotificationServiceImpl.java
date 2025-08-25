package com.dsa360.api.notification;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.dsa360.api.constants.General;
import com.dsa360.api.constants.ReviewType;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.utility.FileStorageUtility;
import org.springframework.core.io.FileSystemResource;

/**
 * @author RAM
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	private static final String PRODNAME = "DSA 360 Solution";
	private static final String EMAILFAIL = "Failed to send the email";
	private static final String DSAID = "dsaId";
	private static final String DSANAME = "dsaName";
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String sender;

	@Value("${server.port}")
	private String port;

	@Value("${server.hostname}")
	private String host;
	
//	@Value("${ng.server}")
//	private String ngServer;

	@Override
	public void dsaRegistrationConfirmationMail(String to, String dsaName, String dsaId, String registeredName,
			String contactInfo) {
		try {
			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, true);

			var context = new Context();
			context.setVariable(General.DSA_NAME.getValue(), dsaName);
			context.setVariable(General.DSA_ID.getValue(), dsaId);
			context.setVariable(General.REGISTERED_NAME.getValue(), registeredName);
			context.setVariable(General.CONTACT_INFO.getValue(), contactInfo);

			String text = templateEngine.process("dsa-registration", context);
			
			System.out.println("HTML TEXT : "+text);
			
			helper.setFrom(sender, PRODNAME);
			helper.setTo(to);
			helper.setSubject("Welcome to DSA 360 Solution: DSA Registration Confirmed!");
			helper.setText(text,true);

			mailSender.send(message);
		} catch (MailSendException | MailAuthenticationException | MessagingException
				| UnsupportedEncodingException e) {

			throw new SomethingWentWrongException(EMAILFAIL, e);

		}

	}
	
	@Autowired
	private FileStorageUtility fileStorageUtility;

	@Override
	public void dsaKycConfirmationMail(String to, String kycId, String dsaId, String dsaName, String contact,
	        String address, List<String> docs) {
	    try {
	    	var message = mailSender.createMimeMessage();
	    	var helper = new MimeMessageHelper(message, true);

	        // Context setup for Thymeleaf template
	    	var context = new Context();
	        context.setVariable(General.DSA_NAME.getValue(), dsaName);
	        context.setVariable(General.DSA_ID.getValue(), dsaId);
	        context.setVariable("kycId", kycId);
	        context.setVariable("registeredName", dsaName);
	        context.setVariable("email", to);
	        context.setVariable("contactNumber", contact);
	        context.setVariable("address", address);

	        // Prepare document image CIDs and add them inline
//	        List<String> docImageCids = new ArrayList<>();
//	        for (int i = 0; i < docs.size(); i++) {
//	            String cid = "docImg" + i;
//	            docImageCids.add(cid);
//
//	            Path filePath = fileStorageUtility.getKycRootLocation().resolve(dsaId).resolve(docs.get(i));
//	            File file = filePath.toFile();
//
//	            if (file.exists()) {
//	                //Highlighted: Inline image added (CID-based)
//	                helper.addInline(cid, new FileSystemResource(file)); 
//	            } else {
//	                System.err.println("File not found: " + file.getAbsolutePath());
//	            }
//	        }
	        
	        for (String doc : docs) {
	        	var filePath = fileStorageUtility.getKycRootLocation().resolve(dsaId).resolve(doc);
	        	var file = filePath.toFile();
	            if (file.exists()) {
	                helper.addAttachment(file.getName(), new FileSystemResource(file));
	            } else {
	                System.err.println("File not found: " + file.getAbsolutePath());
	            }
	        }

	        // Add CID list to context for embedding in HTML
	        //context.setVariable("docImageCids", docImageCids);

	        // Process the Thymeleaf HTML template
	        String html = templateEngine.process("kyc_submission", context);

	        // Send as HTML
	        helper.setFrom(sender, PRODNAME);
	        helper.setTo(to);
	        helper.setSubject("KYC Submission Confirmed - " + dsaId);
	        helper.setText(html, true); // must be true for HTML
        	        

	        

	        mailSender.send(message);
	    } catch (MailSendException | MailAuthenticationException | MessagingException
	            | UnsupportedEncodingException e) {
	        throw new SomethingWentWrongException(EMAILFAIL, e);
	    }
	}

	@Override
	public void dsaReviewMail(String to, String dsaName, String reviewStatus, String type,String dsaId) {
		String html = null;
		try {
			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, true);

			var context = new Context();
			context.setVariable(DSANAME, dsaName);
			context.setVariable("reviewStatus", reviewStatus);
			context.setVariable(DSAID, dsaId);
			if (ReviewType.APPLICATION.getValue().equals(type)) {
				
				html = templateEngine.process("applicatiion_review", context);
				helper.setSubject("DSA Application : " + reviewStatus);

			} else if (ReviewType.KYC.getValue().equals(type)) {
				
				html = templateEngine.process("kyc_review", context);
				helper.setSubject("DSA KYC : " + reviewStatus);

			}else if (ReviewType.LOAN.getValue().equals(type)) {
	            html = templateEngine.process("loan_review", context);
	            helper.setSubject("DSA Loan : " + reviewStatus);
	        }

			helper.setFrom(sender, PRODNAME);
			helper.setTo(to);
			helper.setText(html, true);

			mailSender.send(message);
		} catch (MailSendException | MailAuthenticationException | MessagingException
				| UnsupportedEncodingException e) {
			throw new SomethingWentWrongException(EMAILFAIL, e);

		}
	}
	
	@Override
	public void userProfileCreatedConfirmationMail(DSAApplicationDTO dsaById, String username, String password,
			List<RoleEntity> allRoleByIds, List<RegionsEntity> allRegionsByIds) {

		try {
			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, true);

			var context = new Context();

			String fullName = dsaById.getFirstName() + " " + dsaById.getLastName();
			List<String> roles = allRoleByIds.stream().map(role -> role.getName().replace("ROLE_", ""))
					.collect(Collectors.toList());

			List<String> regions = allRegionsByIds.stream().map(RegionsEntity::getRegionName)
					.collect(Collectors.toList());

			context.setVariable(DSANAME, fullName);
			context.setVariable(DSAID, dsaById.getDsaApplicationId());
			context.setVariable("username", username);
			context.setVariable("password", password);
			context.setVariable("roles", roles);
			context.setVariable("regions", regions);

			String html = templateEngine.process("user-profile", context);

			helper.setFrom(sender, PRODNAME);
			helper.setTo(dsaById.getEmailAddress());
			helper.setSubject("Welcome to DSA 360 Solution: DSA Profile Created !");
			helper.setText(html, true);

			mailSender.send(message);
		} catch (MailSendException | MailAuthenticationException | MessagingException
				| UnsupportedEncodingException e) {

			throw new SomethingWentWrongException(EMAILFAIL, e);

		}

	}

	@Override
	public void emailVerificationRequestMail(String dsaId, String dsaName, String emailTo, String token) {

		try {
			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, true);

			var context = new Context();
			context.setVariable(DSANAME, dsaName);
			context.setVariable(DSAID, dsaId);
			context.setVariable("email", emailTo);
            String url = "http://" + host + ":" + port + "/public/verify-email/" + dsaId + "?token=" + token;
			context.setVariable("url", url);

			String html = templateEngine.process("email-verification", context);

			helper.setFrom(sender, PRODNAME);
			helper.setTo(emailTo);
			helper.setSubject("DSA 360: Email Verification Request - " + dsaId);
			helper.setText(html, true);

			mailSender.send(message);
		} catch (MailSendException | MailAuthenticationException | MessagingException
				| UnsupportedEncodingException e) {

			throw new SomethingWentWrongException(EMAILFAIL, e);

		}
	}

}
