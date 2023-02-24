package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum GameStatus {
	ONGOING("Ongoing", null,0),
	WHITE_WIN_CHECKMATE("White win by checkmate", GameSide.WHITE, 1),
	WHITE_WIN_TIMEOUT("White win by timeout", GameSide.WHITE, 2),
	WHITE_WIN_RESIGNATION("White win by resignation", GameSide.WHITE, 3),
	BLACK_WIN_CHECKMATE("Black win by checkmate", GameSide.BLACK, 4),
	BLACK_WIN_TIMEOUT("Black win by timeout", GameSide.BLACK, 5),
	BLACK_WIN_RESIGNATION("Black win by resignation", GameSide.BLACK, 6),
	DRAW_BY_STALEMATE("Draw by stalemate", null, 7),
	DRAW_BY_INSUFFICIENT_MATERIAL("Draw by insufficient material", null, 8),
	DRAW_BY_AGREEMENT("Draw by agreement", null, 9);

	@Getter
	private final String name;

	@Getter
	private final GameSide winner;

	@Getter
	private final int code;

	GameStatus(String name, GameSide winner, int code){
		this.name = name;
		this.winner = winner;
		this.code = code;
	}

	private static final Map<Integer, GameStatus> codeMap = new HashMap<>();

	static {
		codeMap.put(null, null);
		for (GameStatus t : GameStatus.values()) {
			codeMap.put(t.getCode(), t);
		}
	}

	public static GameStatus getByCode(int code){
		return codeMap.get(code);
	}
}
