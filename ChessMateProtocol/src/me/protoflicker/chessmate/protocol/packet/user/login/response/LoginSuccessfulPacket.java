package me.protoflicker.chessmate.protocol.packet.user.login.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.user.UserInfo;

public class LoginSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final UserInfo userInfo;

	@Getter
	private final String token;

	public LoginSuccessfulPacket(byte[] userId, UserInfo userInfo, String token){
		this.userId = userId;
		this.userInfo = userInfo;
		this.token = token;
	}

	public LoginSuccessfulPacket(byte[] userId, UserInfo userInfo){
		this.userId = userId;
		this.userInfo = userInfo;
		this.token = null;
	}
}
