package me.protoflicker.chessmate.protocol.packet.user.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

import java.util.Set;

public class UsersOnlineResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final Set<byte[]> users;

	public UsersOnlineResponsePacket(Set<byte[]> users){
		this.users = users;
	}
}
