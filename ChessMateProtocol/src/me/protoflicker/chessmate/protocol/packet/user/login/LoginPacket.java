package me.protoflicker.chessmate.protocol.packet.user.login;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class LoginPacket extends Packet {

	@Getter
	private final String username;

	@Getter
	private final String password;

	public LoginPacket(String username, String password){
		this.username = username;
		this.password = password;
	}
}
