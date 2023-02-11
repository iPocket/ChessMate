package me.protoflicker.chessmate.protocol.packet.user.login;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class LoginSuccessfulPacket extends Packet {

	@Getter
	private final byte[] userId;

	@Getter
	private final String username;

	public LoginSuccessfulPacket(byte[] userId, String username){
		this.userId = userId;
		this.username = username;
	}
}
