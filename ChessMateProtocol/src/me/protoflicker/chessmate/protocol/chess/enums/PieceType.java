package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum PieceType {
	KING("King", "K", false),
	QUEEN("Queen", "Q", true),
	ROOK("Rook", "R", true),
	BISHOP("Bishop", "B", true),
	KNIGHT("Knight", "N", true),
	PAWN("Pawn", "P", false);

	@Getter
	private final String name;

	@Getter
	private final String code;

	@Getter
	private final boolean isPiece;

	PieceType(String name, String code, boolean isPiece){
		this.name = name;
		this.code = code;
		this.isPiece = isPiece;
	}


	private static final Map<String, PieceType> chessCodeMap = new HashMap<>();

	static {
		chessCodeMap.put(null, null);
		for (PieceType t : PieceType.values()) {
			chessCodeMap.put(t.getCode(), t);
		}
	}

	public static PieceType getByChessCode(String chessCode){
		if(chessCode == null){
			return null;
		}

		return chessCodeMap.get(chessCode.toUpperCase());
	}
}
