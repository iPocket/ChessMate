package me.protoflicker.chessmate.protocol.packet.user.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

import java.util.List;

public class UsersInfoRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final List<byte[]> userIds;

	public UsersInfoRequestPacket(List<byte[]> userIds){
		this.userIds = userIds;
	}
}
