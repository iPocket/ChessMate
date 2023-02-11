package me.protoflicker.chessmate.protocol.chess;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class PerformedChessMove implements Serializable {

	@Getter
	private final int moveNumber;

	@Getter
	private final Timestamp timePlayed;

	@Getter
	private final ChessMove move;

	public PerformedChessMove(int moveNumber, Timestamp timePlayed, ChessMove move){
		this.moveNumber = moveNumber;
		this.timePlayed = timePlayed;
		this.move = move;
	}
}
