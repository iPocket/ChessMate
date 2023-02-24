package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

import java.sql.Date;

public class RegisterPacket extends Packet implements ClientPacket {

	@Getter
	private final String username;

	@Getter
	private final String password;

	@Getter
	private final Date birthday;

	public RegisterPacket(String username, String password, Date birthday){
		this.username = username;
		this.password = password;
		this.birthday = birthday;
	}
}
