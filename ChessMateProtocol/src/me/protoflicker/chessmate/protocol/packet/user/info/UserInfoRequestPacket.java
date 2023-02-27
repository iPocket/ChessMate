package me.protoflicker.chessmate.protocol.packet.user.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class UserInfoRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final byte[] userId;

	public UserInfoRequestPacket(byte[] userId){
		this.userId = userId;
	}
}
