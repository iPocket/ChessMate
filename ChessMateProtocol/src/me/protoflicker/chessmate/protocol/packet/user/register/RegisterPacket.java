package me.protoflicker.chessmate.protocol.packet.user.register;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

import java.sql.Date;

public class RegisterPacket extends Packet {

	@Getter
	private final String username;

	@Getter
	private final String password;


	private final String birthday;

	public RegisterPacket(String username, String password, String birthday){
		this.username = username;
		this.password = password;
		this.birthday = birthday;
	}

	public Date getBirthday(){
		return Date.valueOf(birthday);
	}
}
