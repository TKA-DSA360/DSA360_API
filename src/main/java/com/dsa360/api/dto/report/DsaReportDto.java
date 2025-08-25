package com.dsa360.api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DsaReportDto {
    private int totalDsas;
    private int activeDsas;
    private int suspendedDsas;
    private int deactivatedDsas;
}
