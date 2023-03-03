package me.protoflicker.chessmate.protocol.packet.user.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

import java.util.Map;

public class UsersInfoResponsePacket extends Packet implements ServerPacket {
	@Getter
	private final Map<byte[], UserInfo> info;

	public UsersInfoResponsePacket(Map<byte[], UserInfo> info){
		this.info = info;
	}
}
