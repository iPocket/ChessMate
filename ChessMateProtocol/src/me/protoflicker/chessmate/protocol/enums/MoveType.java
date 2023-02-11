package me.protoflicker.chessmate.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MoveType {
	MOVE("Move", false, 0),
	TAKE("Take", true, 1),
	CASTLE("Castle", false, 2),
	EN_PASSANT("En Passant", false, 3),
	EN_PASSANT_TAKE("En Passant Take", true, 4),
	RESIGNATION("Resignation", false, 5);

	@Getter
	private final String name;

	@Getter
	private final boolean canTake;

	@Getter
	private final int code;

	MoveType(String name, boolean canTake, int code){
		this.name = name;
		this.canTake = canTake;
		this.code = code;
	}

	public MoveType getTakeVersion(){
		return this == MOVE ? TAKE : EN_PASSANT_TAKE;
	}

	private static final Map<Integer, MoveType> codeMap = new HashMap<>();

	static {
		codeMap.put(null, null);
		for (MoveType t : MoveType.values()) {
			codeMap.put(t.getCode(), t);
		}
	}

	public static MoveType getByCode(int code){
		return codeMap.get(code);
	}
}
