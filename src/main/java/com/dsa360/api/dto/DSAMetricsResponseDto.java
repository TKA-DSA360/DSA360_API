package com.dsa360.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DSAMetricsResponseDto {
	private long totalSyatemUsers;
	private long activeSystemUsers;
	private NewRegistrations newRegistrations;
	private long pendingDsaRegistration;
	private long deactivatedSystemUsers;

	// Inner DTO for registration breakdown
	public static class NewRegistrations {
		private long daily;
		private long weekly;
		private long monthly;

		public NewRegistrations(long daily, long weekly, long monthly) {
			this.daily = daily;
			this.weekly = weekly;
			this.monthly = monthly;
		}

		public long getDaily() {
			return daily;
		}

		public long getWeekly() {
			return weekly;
		}

		public long getMonthly() {
			return monthly;
		}
	}

}
