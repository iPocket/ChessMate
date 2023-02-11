package me.protoflicker.chessmate.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum AccountType {
	USER("User", 0),
	ADMIN("Admin", 1);

	@Getter
	private final String name;

	@Getter
	private final int code;

	AccountType(String name, int code){
		this.name = name;
		this.code = code;
	}


	private static final Map<Integer, AccountType> codeMap = new HashMap<>();

	static {
		codeMap.put(null, null);
		for (AccountType t : AccountType.values()) {
			codeMap.put(t.getCode(), t);
		}
	}

	public static AccountType getByCode(int code){
		return codeMap.get(code);
	}
}
