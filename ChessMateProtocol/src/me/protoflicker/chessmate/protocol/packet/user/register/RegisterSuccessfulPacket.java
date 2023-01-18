package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class RegisterSuccessfulPacket extends Packet {

	@Getter
	private final String userId;

	@Getter
	private final String username;

	public RegisterSuccessfulPacket(String userId, String username){
		this.userId = userId;
		this.username = username;
	}
}
