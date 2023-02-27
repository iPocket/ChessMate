package me.protoflicker.chessmate.protocol.packet.user.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class UserIdRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final String username;

	public UserIdRequestPacket(String username){
		this.username = username;
	}
}
