package com.dsa360.api.serviceimpl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.dao.DsaKycDao;
import com.dsa360.api.entity.DsaKycEntity;
import com.dsa360.api.service.DsaKycService;

/**
 * @author RAM
 *
 */
@Service
@Transactional(readOnly = true)
public class DsaKycServiceImpl implements DsaKycService {
	
	private static final Logger logger = LoggerFactory.getLogger(DsaKycServiceImpl.class);
	
	@Autowired
	private DsaKycDao kycDao;

	@Override
	public List<DsaKycEntity> getAllKycs() {
		logger.info("in get all kyc");
			List<DsaKycEntity> allKycs = kycDao.getAllKycs();
			if(!allKycs.isEmpty()) {
//				 return allKycs.stream()
//		                  .map(kyc -> mapper.map(kyc, DSA_KYC_DTO.class))
//		                  .collect(Collectors.toList());
			}
			
		return allKycs;
	}

}
