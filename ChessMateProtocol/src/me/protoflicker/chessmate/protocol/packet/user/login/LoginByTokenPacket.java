package me.protoflicker.chessmate.protocol.packet.user.login;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class LoginByTokenPacket extends Packet implements ClientPacket {

	@Getter
	private final String token;

	public LoginByTokenPacket(String token){
		this.token = token;
	}
}
