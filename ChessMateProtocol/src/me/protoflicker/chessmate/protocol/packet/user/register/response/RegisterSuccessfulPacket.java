package me.protoflicker.chessmate.protocol.packet.user.register.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

public class RegisterSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final UserInfo userInfo;

	@Getter
	private final String token;

	public RegisterSuccessfulPacket(byte[] userId, UserInfo userInfo, String token){
		this.userId = userId;
		this.userInfo = userInfo;
		this.token = token;
	}
}
