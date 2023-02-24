package me.protoflicker.chessmate.protocol.packet.user.login.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class LoginSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final String token;

	public LoginSuccessfulPacket(byte[] userId, String token){
		this.userId = userId;
		this.token = token;
	}

	public LoginSuccessfulPacket(byte[] userId){
		this.userId = userId;
		this.token = null;
	}
}
