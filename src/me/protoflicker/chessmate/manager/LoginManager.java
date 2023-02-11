package me.protoflicker.chessmate.manager;

import com.google.common.hash.Hashing;
import me.protoflicker.chessmate.data.table.UserTable;

import java.nio.charset.StandardCharsets;

public abstract class LoginManager {

	private static final String SALT = "medicbag";

	public static boolean isPasswordCorrect(byte[] userId, String givenPassword){
		return hashPassword(givenPassword).equals(UserTable.getHashedPassword(userId));
	}

	private static String hashPassword(String password){
		return Hashing.sha256().hashString(password + SALT, StandardCharsets.UTF_8).toString();
	}
}
