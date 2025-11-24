package com.dsa360.api.dto;

public class DSAMetricsCombinedResponse {
    private String status;
    private DSAMetricsResponseDto data;

    public DSAMetricsCombinedResponse(String status, DSAMetricsResponseDto data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() { return status; }
    public DSAMetricsResponseDto getData() { return data; }
}
