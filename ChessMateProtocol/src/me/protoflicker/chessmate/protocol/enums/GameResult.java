package me.protoflicker.chessmate.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum GameResult {
	ONGOING("Ongoing", 0),
	WIN("Win", 1),
	Loss("Loss", 2),
	DRAW("Draw", 3);

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
