package me.protoflicker.chessmate.protocol.packet.user.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

import java.util.Set;

public class UsersInfoRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final Set<byte[]> userIds;

	public UsersInfoRequestPacket(Set<byte[]> userIds){
		this.userIds = userIds;
	}
}
