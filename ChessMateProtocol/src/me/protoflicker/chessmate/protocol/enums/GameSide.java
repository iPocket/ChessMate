package me.protoflicker.chessmate.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum GameSide {
	WHITE("White", 0, "W"),
	BLACK("Black", 1, "B");

	@Getter
	private final String name;

	@Getter
	private final int code;

	@Getter
	private final String chessCode;

	GameSide(String name, int code, String chessCode){
		this.name = name;
		this.code = code;
		this.chessCode = chessCode;
	}
	
	public GameSide getOpposite(){
		return this == GameSide.WHITE ? GameSide.BLACK : GameSide.WHITE;
	}

	private static final Map<Integer, GameSide> codeMap = new HashMap<>();
	private static final Map<String, GameSide> chessCodeMap = new HashMap<>();

	static {
		codeMap.put(null, null);
		chessCodeMap.put(null, null);
		for (GameSide t : GameSide.values()) {
			codeMap.put(t.getCode(), t);
			chessCodeMap.put(t.getChessCode(), t);
		}
	}

	public static GameSide getByCode(int code){
		return codeMap.get(code);
	}

	public static GameSide getByChessCode(String code){
		return chessCodeMap.get(code.toUpperCase());
	}
}
