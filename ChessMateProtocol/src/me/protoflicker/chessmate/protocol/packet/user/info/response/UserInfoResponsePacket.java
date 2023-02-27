package me.protoflicker.chessmate.protocol.packet.user.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

public class UserInfoResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final UserInfo info;

	public UserInfoResponsePacket(byte[] userId, UserInfo info){
		this.userId = userId;
		this.info = info;
	}
}
