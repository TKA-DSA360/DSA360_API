package com.dsa360.api.utility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.notification.NotificationService;

@Component
public class MailAsyncServices {

	@Autowired
	private NotificationService notificationService;

	@Async("asyncExecutor")
	//t2
	public void sendApplicationConfirmationEmail(DSAApplicationDTO dsaRegistrationDTO) {
		String to = dsaRegistrationDTO.getEmailAddress();
		String dsaName = dsaRegistrationDTO.getFirstName() + " " + dsaRegistrationDTO.getLastName();
		var dsaId = String.valueOf(dsaRegistrationDTO.getDsaApplicationId());
		String registeredName = dsaRegistrationDTO.getFirstName() + " " + dsaRegistrationDTO.getMiddleName() + " "
				+ dsaRegistrationDTO.getLastName();
		String contactInfo = dsaRegistrationDTO.getContactNumber();

		try {
			notificationService.dsaRegistrationConfirmationMail(to, dsaName, dsaId, registeredName, contactInfo);
		}

		catch (SomethingWentWrongException e) {
			Throwable cause = e.getCause();
			throw new SomethingWentWrongException(e.getMessage(), cause);
		}
	}

	@Async("asyncExecutor")
	public void sendKycSubmittedEmail(String to, String kycId, String dsaId, String dsaName, String contact,
			String address, List<String> docs) {

		try {
			notificationService.dsaKycConfirmationMail(to, kycId, dsaId, dsaName, contact, address, docs);
		}

		catch (SomethingWentWrongException e) {
			Throwable cause = e.getCause();
			throw new SomethingWentWrongException(e.getMessage(), cause);
		}
	}

	@Async("asyncExecutor")
	public void dsaReviewMail(String to, String dsaName, String reviewStatus, String type,String dsaId) {

		try {
			notificationService.dsaReviewMail(to, dsaName, reviewStatus, type,dsaId);

		} catch (SomethingWentWrongException e) {
			Throwable cause = e.getCause();
			throw new SomethingWentWrongException(e.getMessage(), cause);
		}

	}

	@Async("asyncExecutor")
	public void userProfileCreatedConfirmationMail(DSAApplicationDTO dsaById, String username, String password,
			List<RoleEntity> allRoleByIds, List<RegionsEntity> allRegionsByIds) {

		try {
			notificationService.userProfileCreatedConfirmationMail(dsaById, username, password, allRoleByIds,
					allRegionsByIds);
		}

		catch (SomethingWentWrongException e) {
			Throwable cause = e.getCause();
			throw new SomethingWentWrongException(e.getMessage(), cause);
		}
	}

	@Async("asyncExecutor")
	public void emailVerificationRequestMail(String dsaId,String dsaName,String emailTo, String token) {

		try {
			notificationService.emailVerificationRequestMail(dsaId, dsaName,emailTo,token);
		}

		catch (SomethingWentWrongException e) {
			Throwable cause = e.getCause();
			throw new SomethingWentWrongException(e.getMessage(), cause);
		}

	}

}
