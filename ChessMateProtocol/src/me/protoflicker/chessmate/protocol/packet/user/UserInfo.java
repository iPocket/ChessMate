package me.protoflicker.chessmate.protocol.packet.user;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.AccountType;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class UserInfo implements Serializable {

	@Getter
	private final byte[] userId;

	@Getter
	private final String username;

	@Getter
	private final Date birthday;

	@Getter
	private final AccountType accountType;

	@Getter
	private final Timestamp lastLogin;

	public UserInfo(byte[] userId, String username, Date birthday, AccountType accountType, Timestamp lastLogin){
		this.userId = userId;
		this.username = username;
		this.birthday = birthday;
		this.accountType = accountType;
		this.lastLogin = lastLogin;
	}
}
