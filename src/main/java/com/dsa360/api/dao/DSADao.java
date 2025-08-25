package com.dsa360.api.dao;

import java.nio.file.Path;
import java.util.List;

import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.DsaKycEntity;

public interface DSADao {
	public abstract DsaApplicationEntity getDSAById(String dsaID);

	public abstract DsaApplicationEntity dsaApplication(DsaApplicationEntity dsaRegistrationEntity);

	public abstract List<DsaApplicationEntity> getAllDsaApplication();
	
	public abstract List<String> getAllApprovedDsa();

	public abstract DsaApplicationEntity notifyReview(String registrationId, String approvalStatus, String type);

	public abstract boolean systemUserKyc(DsaKycEntity dsa_KYC_Entity, List<Path> storedFilePaths);

	public abstract DsaKycEntity getDsaKycByDsaId(String dsaRegistrationId);
	
	public DsaApplicationEntity emailVerificationRequest(String dsaId, String token);

	public abstract void verifyEmail(String dsaId, String token);

	public abstract List<String> getAllApprovedDsaWithNoSystemUser();

}
