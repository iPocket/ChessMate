package me.protoflicker.chessmate.protocol.packet.user.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

import java.util.List;

public class UsersOnlineResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final List<byte[]> users;

	public UsersOnlineResponsePacket(List<byte[]> users){
		this.users = users;
	}
}
