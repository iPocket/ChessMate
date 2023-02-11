package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;

public class RegisterNameTakenPacket extends RegisterUnsuccessfulPacket {

	@Getter
	private final String username;

	public RegisterNameTakenPacket(String username){
		this.username = username;
	}
}
