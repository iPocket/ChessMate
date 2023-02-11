package me.protoflicker.chessmate.protocol.chess;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.io.Serializable;
import java.util.Objects;

public class ChessPiece implements Serializable, Cloneable {

	@Getter
	@Setter
	private PieceType type;

	@Getter
	private final GameSide gameSide;

	public ChessPiece(PieceType type, GameSide gameSide){
		this.type = type;
		this.gameSide = gameSide;
	}

	public ChessPiece getVersion(PieceType type){
		return new ChessPiece(type, this.gameSide);
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		var that = (ChessPiece) obj;
		return this.type == that.type &&
				this.gameSide == that.gameSide;
	}

	@Override
	public int hashCode(){
		return Objects.hash(type, gameSide);
	}

	@Override
	public ChessPiece clone(){
		try {
			return (ChessPiece) super.clone();
		} catch(CloneNotSupportedException e){
			throw new AssertionError();
		}
	}

	@Override
	public String toString(){
		return gameSide.getName() + " " + type.getName();
	}
}
