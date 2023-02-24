package me.protoflicker.chessmate.protocol.packet.user.setting;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class UserPasswordChangePacket extends Packet implements ClientPacket {

	@Getter
	private final String oldPassword;

	@Getter
	private final String newPassword;

	public UserPasswordChangePacket(String oldPassword, String newPassword){
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
}
