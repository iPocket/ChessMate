package me.protoflicker.chessmate.protocol.packet.user.register.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class RegisterSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final String username;

	public RegisterSuccessfulPacket(String username){
		this.username = username;
	}
}
