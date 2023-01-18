package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;

public class RegisterNameTakenPacket extends RegisterUnsucessfulPacket {

	@Getter
	private final String username;

	public RegisterNameTakenPacket(String username){
		this.username = username;
	}
}
