package me.protoflicker.chessmate.protocol.chess;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class PerformedChessMove implements Serializable, Cloneable {

	@Getter
	private Timestamp timePlayed;

	@Getter
	private ChessMove move;

	public PerformedChessMove(Timestamp timePlayed, ChessMove move){
		this.timePlayed = timePlayed;
		this.move = move;
	}

	@Override
	public PerformedChessMove clone(){
		try {
			PerformedChessMove clone = (PerformedChessMove) super.clone();
			clone.move = move.clone();
			return clone;
		} catch(CloneNotSupportedException e){
			throw new AssertionError();
		}
	}

	@Override
	public int hashCode(){
		return Objects.hash(timePlayed, move);
	}
}
