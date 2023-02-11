package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class RegisterSuccessfulPacket extends Packet {

	@Getter
	private final byte[] userId;

	@Getter
	private final String username;

	public RegisterSuccessfulPacket(byte[] userId, String username){
		this.userId = userId;
		this.username = username;
	}
}
