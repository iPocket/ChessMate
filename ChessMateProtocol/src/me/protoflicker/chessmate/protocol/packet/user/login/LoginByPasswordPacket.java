package me.protoflicker.chessmate.protocol.packet.user.login;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class LoginByPasswordPacket extends Packet implements ClientPacket {

	@Getter
	private final String username;

	@Getter
	private final String password;

	public LoginByPasswordPacket(String username, String password){
		this.username = username;
		this.password = password;
	}
}
