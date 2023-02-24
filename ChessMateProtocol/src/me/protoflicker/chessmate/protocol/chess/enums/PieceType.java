package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum PieceType {
	KING("King", "K"),
	QUEEN("Queen", "Q"),
	ROOK("Rook", "R"),
	BISHOP("Bishop", "B"),
	KNIGHT("Knight", "N"),
	PAWN("Pawn", "P");

	@Getter
	private final String name;

	@Getter
	private final String code;

	PieceType(String name, String code){
		this.name = name;
		this.code = code;
	}


	private static final Map<String, PieceType> chessCodeMap = new HashMap<>();

	static {
		chessCodeMap.put(null, null);
		for (PieceType t : PieceType.values()) {
			chessCodeMap.put(t.getCode(), t);
		}
	}

	public static PieceType getByChessCode(String chessCode){
		return chessCodeMap.get(chessCode.toUpperCase());
	}
}
