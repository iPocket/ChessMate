package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;

public class RegisterNameTooLong extends RegisterUnsuccessfulPacket {

	@Getter
	private final String username;

	public RegisterNameTooLong(String username){
		this.username = username;
	}
}
