package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.io.Serializable;
import java.util.Objects;

public class LocatableChessPiece implements Serializable {

	@Getter
	private final ChessPiece piece;

	@Getter
	private final ChessPosition position;

	public LocatableChessPiece(ChessPiece piece, ChessPosition position){
		this.piece = piece;
		this.position = position;
	}

	public PieceType getType(){
		return piece.getType();
	}

	public GameSide getGameSide(){
		return piece.getGameSide();
	}

	public LocatableChessPiece getVersion(PieceType type){
		return new LocatableChessPiece(piece.getVersion(type), position);
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		var that = (LocatableChessPiece) obj;
		return this.piece.getType() == that.piece.getType() && this.position.equals(that.position) &&
				this.piece.getGameSide() == that.piece.getGameSide();
	}

	@Override
	public int hashCode(){
		return Objects.hash(piece, position);
	}
}
