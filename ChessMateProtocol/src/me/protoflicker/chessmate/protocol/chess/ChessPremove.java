package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.PieceType;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ChessPremove implements Serializable, Cloneable {

	@Getter
	private final GameSide gameSide;

	@Getter
	private ChessPosition pieceFrom;

	@Getter
	private ChessPosition pieceTo;

	@Getter
	@Setter
	private PieceType promotionPiece;

	public ChessPremove(GameSide gameSide, ChessPosition pieceFrom, ChessPosition pieceTo, PieceType promotionPiece){
		this.gameSide = gameSide;
		this.pieceFrom = pieceFrom;
		this.pieceTo = pieceTo;
		this.promotionPiece = promotionPiece;
	}

	public List<ChessPosition> getAffectedSquares(){
		return List.of(pieceFrom, pieceTo);
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		var that = (ChessPremove) obj;
		return this.gameSide == that.gameSide && this.pieceFrom.equals(that.pieceFrom)
				&& this.pieceTo.equals(that.pieceTo) && this.promotionPiece == that.promotionPiece;
	}

	@Override
	public int hashCode(){
		return Objects.hash(gameSide, pieceFrom, pieceTo, promotionPiece);
	}

	@Override
	public ChessPremove clone(){
		try {
			ChessPremove clone = (ChessPremove) super.clone();
			clone.pieceFrom = pieceFrom.clone();
			clone.pieceTo = pieceTo.clone();
			return clone;
		} catch(CloneNotSupportedException e){
			throw new AssertionError();
		}
	}
}
