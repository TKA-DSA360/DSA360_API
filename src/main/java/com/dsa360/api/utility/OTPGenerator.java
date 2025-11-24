package com.dsa360.api.utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author RAM
 *
 */
public class OTPGenerator {

	public static int generateOtp() throws NoSuchAlgorithmException {
		Random random = SecureRandom.getInstanceStrong();
		var numbers = "123456789";
		var otp = new char[6];

		for (var i = 0; i < 6; i++) {

			otp[i] = numbers.charAt(random.nextInt(numbers.length()));
		}

		return Integer.parseInt(new String(otp));

	}

}
