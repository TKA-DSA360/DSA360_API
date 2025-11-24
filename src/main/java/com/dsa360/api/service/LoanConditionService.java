package com.dsa360.api.service;

import java.util.List;

import com.dsa360.api.dto.LoanConditionDto;

public interface LoanConditionService {

    LoanConditionDto createLoanCondition(LoanConditionDto loanConditionDto);

    LoanConditionDto getLoanConditionById(String id);
    
    LoanConditionDto getLoanConditionByBank_Loantype(String bankName,String loanType);

    List<LoanConditionDto> getAllLoanConditions();

    LoanConditionDto updateLoanCondition(LoanConditionDto loanConditionDto);

    void deleteLoanCondition(String id);
    
    List<String> getAllBanksNames();
    double getIntrestRate(String bankName);
}
