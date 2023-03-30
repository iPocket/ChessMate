package me.protoflicker.chessmate.protocol.packet;

import me.protoflicker.chessmate.protocol.Packet;

public class TestPacket extends Packet {

	public static String getUsernameRegex() {
		return "^[a-zA-Z0-9._-]{3,}$";
	}

	public static boolean isUsernameValid(String username){
		return username != null && username.length() <= 32 && username.length() >= 2 && username.matches(getUsernameRegex());
	}

	public static boolean isPasswordValid(String password){
		return password != null && password.length() >= 8;
	}
}
