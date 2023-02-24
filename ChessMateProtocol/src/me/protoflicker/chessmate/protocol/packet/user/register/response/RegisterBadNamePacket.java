package me.protoflicker.chessmate.protocol.packet.user.register.response;

import lombok.Getter;

public class RegisterBadNamePacket extends RegisterUnsuccessfulPacket {

	@Getter
	private final String username;

	public RegisterBadNamePacket(String username){
		this.username = username;
	}
}
