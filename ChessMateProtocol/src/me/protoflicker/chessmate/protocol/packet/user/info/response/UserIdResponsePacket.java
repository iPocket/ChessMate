package me.protoflicker.chessmate.protocol.packet.user.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class UserIdResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final String username;

	@Getter
	private final byte[] userId;

	public UserIdResponsePacket(String username, byte[] userId){
		this.username = username;
		this.userId = userId;
	}
}
