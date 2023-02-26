package me.protoflicker.chessmate.protocol.packet.user.register.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class RegisterSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final String token;

	public RegisterSuccessfulPacket(byte[] userId, String token){
		this.userId = userId;
		this.token = token;
	}
}
