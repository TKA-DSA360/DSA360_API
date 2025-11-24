package com.dsa360.api.dto;

import java.util.List;

import com.dsa360.api.dto.report.YearlyDisbursementSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoanDisbursementSummaryResponseDto {
	private List<YearlyDisbursementSummaryDto> summaries;
}
