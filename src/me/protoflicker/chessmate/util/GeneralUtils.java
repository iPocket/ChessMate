package me.protoflicker.chessmate.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class GeneralUtils {

	public static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String timestamp(long time){
		long hours = time / 3600;
		long minutes = (time - hours * 3600) / 60;
		long seconds = time - hours * 3600 - minutes * 60;

		return String.format("%02d" + ":" + "%02d" + ":" + "%02d", hours, minutes, seconds)
				.replace("-", "");
	}

	public static String timeHuman(int totalSecs){
		if(totalSecs < 0){
			return "(Expired)";
		}

		int days = totalSecs / 86400;
		int hours = (totalSecs % 86400) / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;

		String ans = (days != 0 ? " " + days + "d" : "") + (hours != 0 ? " " + hours + "h" : "")
				+ (minutes != 0 ? " " + minutes + "m" : "")
				+ ((seconds != 0 && (days != 0 || hours != 0 || minutes != 0)) ? " " + seconds + "s"
				: (seconds == 0 && (days != 0 || hours != 0 || minutes != 0) ? "" : " " + seconds + "s"));
		if(ans.startsWith(" ")){
			return ans.substring(1);
		} else {
			return ans;
		}
	}


	public static String getSecureRandomString(int length){
		try {
			SecureRandom secureRandom = SecureRandom.getInstanceStrong();
			return secureRandom.ints(length, 0, ALPHANUMERIC.length()).mapToObj(ALPHANUMERIC::charAt)
					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		} catch(NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
	}
}
