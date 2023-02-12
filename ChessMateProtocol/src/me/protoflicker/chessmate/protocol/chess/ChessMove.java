package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.MoveType;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.io.Serializable;
import java.util.Objects;

public class ChessMove implements Serializable, Cloneable {

	@Getter
	@Setter
	private MoveType moveType;

	@Getter
	private GameSide gameSide;

	@Getter
	@Setter
	private PieceType pieceMoved;

	@Getter
	private ChessPosition pieceFrom;

	@Getter
	private ChessPosition pieceTo;

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

	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		var that = (ChessMove) obj;
		return this.moveType == that.moveType && this.gameSide == that.gameSide && this.pieceMoved == that.pieceMoved
				&& this.pieceFrom.equals(that.pieceFrom) && this.pieceTo.equals(that.pieceTo)/* && this.promotionPiece == that.promotionPiece*/;
	}

	@Override
	public int hashCode(){
		return Objects.hash(moveType, gameSide, pieceMoved, pieceFrom, pieceTo, promotionPiece);
	}

	@Override
	public ChessMove clone(){
		try {
			ChessMove clone = (ChessMove) super.clone();
			clone.pieceFrom = pieceFrom.clone();
			clone.pieceTo = pieceTo.clone();
			return clone;
		} catch(CloneNotSupportedException e){
			throw new AssertionError();
		}
	}
}
