package me.protoflicker.chessmate.protocol.packet.user.register.response;

import lombok.Getter;

public class RegisterNameTakenPacket extends RegisterUnsuccessfulPacket {

	@Getter
	private final String username;

	public RegisterNameTakenPacket(String username){
		this.username = username;
	}
}
