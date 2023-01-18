package me.protoflicker.chessmate.protocol.packet.user.login;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class LoginSuccessfulPacket extends Packet {

	@Getter
	private final String userId;

	@Getter
	private final String username;

	public LoginSuccessfulPacket(String userId, String username){
		this.userId = userId;
		this.username = username;
	}
}
