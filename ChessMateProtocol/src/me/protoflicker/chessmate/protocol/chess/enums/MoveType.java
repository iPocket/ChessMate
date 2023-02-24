package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum MoveType {
	MOVE("Move", true, true, 0),
	TAKE("Take", true, true, 1),
	CASTLE("Castle", true, false, 2),
	EN_PASSANT("En Passant", true, true, 3),
	RESIGNATION("Resignation", false, false, 4),
	DRAW_AGREEMENT("Draw Agreement", false, false, 5);
//	RESIGNATION("Resignation", false, 9);

	@Getter
	private final String name;

	@Getter
	private final boolean isPieceMove;

	@Getter
	private final boolean canTake;

	@Getter
	private final int code;

	MoveType(String name, boolean isPieceMove, boolean canTake, int code){
		this.name = name;
		this.isPieceMove = isPieceMove;
		this.canTake = canTake;
		this.code = code;
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
