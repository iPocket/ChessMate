package me.protoflicker.chessmate.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum GameResult {
	LOSS("Loss", 0),
	WIN("Win", 1),
	ONGOING("Ongoing", 2);

	@Getter
	private final String name;

	@Getter
	private final int code;

	GameResult(String name, int code){
		this.name = name;
		this.code = code;
	}

	private static final Map<Integer, GameResult> codeMap = new HashMap<>();

	static {
		codeMap.put(null, null);
		for (GameResult t : GameResult.values()) {
			codeMap.put(t.getCode(), t);
		}
	}

	public static GameResult getByCode(int code){
		return codeMap.get(code);
	}
}
