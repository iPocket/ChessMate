package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.MoveType;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.io.Serializable;

public class ChessMove implements Serializable {

	@Getter
	@Setter
	private MoveType moveType;

	@Getter
	private GameSide gameSide;

	@Getter
	private PieceType pieceMoved;

	@Getter
	private final ChessPosition pieceFrom;

	@Getter
	private final ChessPosition pieceTo;

	@Getter
	@Setter
	private PieceType promotionPiece;

	public ChessMove(MoveType moveType, GameSide gameSide, PieceType pieceMoved, ChessPosition pieceFrom, ChessPosition pieceTo){
		this(moveType, gameSide, pieceMoved, pieceFrom, pieceTo, null);
	}

	public ChessMove(MoveType moveType, GameSide gameSide, PieceType pieceMoved, ChessPosition pieceFrom, ChessPosition pieceTo, PieceType promotionPiece){
		this.moveType = moveType;
		this.gameSide = gameSide;
		this.pieceMoved = pieceMoved;
		this.pieceFrom = pieceFrom;
		this.pieceTo = pieceTo;
		this.promotionPiece = promotionPiece;
	}
}
