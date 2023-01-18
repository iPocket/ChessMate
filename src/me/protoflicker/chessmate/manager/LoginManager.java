package me.protoflicker.chessmate.manager;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public abstract class LoginManager {

	public static boolean authenticate(String userId, String hashedPassword, String givenPassword){
		return hashPassword(givenPassword).equals(hashedPassword);
	}

	private static String hashPassword(String password){
		return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
	}
}
