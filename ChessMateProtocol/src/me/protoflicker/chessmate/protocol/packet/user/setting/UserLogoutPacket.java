package me.protoflicker.chessmate.protocol.packet.user.setting;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class UserLogoutPacket extends Packet implements ClientPacket {

	@Getter
	private final String token;

	public UserLogoutPacket(String token){
		this.token = token;
	}
}
