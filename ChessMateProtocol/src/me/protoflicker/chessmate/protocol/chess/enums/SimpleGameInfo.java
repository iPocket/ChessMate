package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

public class SimpleGameInfo implements Serializable {

	@Getter
	@Setter
	private byte[] gameId;

	@Getter
	private final String gameName;

	@Getter
	private final byte[] whiteId;

	@Getter
	private final byte[] blackId;

	@Getter
	private final String startingBoard;

	@Getter
	private final int timeConstraint;

	@Getter
	private final int timeIncrement;

	@Getter
	private final GameStatus status;

	@Getter
	private final Timestamp startTime;

	@Getter
	private final GameSide currentTurn;

	public SimpleGameInfo(byte[] gameId, String gameName, byte[] whiteId, byte[] blackId, String startingBoard,
						  int timeConstraint, int timeIncrement){
		this.gameId = gameId;
		this.gameName = gameName;
		this.whiteId = whiteId;
		this.blackId = blackId;
		this.startingBoard = startingBoard;
		this.timeConstraint = timeConstraint;
		this.timeIncrement = timeIncrement;
		this.status = null;
		this.startTime = null;
		this.currentTurn = null;
	}

	public SimpleGameInfo(byte[] gameId, String gameName, byte[] whiteId, byte[] blackId, String startingBoard,
						  int timeConstraint, int timeIncrement, GameStatus status, Timestamp startTime, GameSide currentTurn){
		this.gameId = gameId;
		this.gameName = gameName;
		this.whiteId = whiteId;
		this.blackId = blackId;
		this.startingBoard = startingBoard;
		this.timeConstraint = timeConstraint;
		this.timeIncrement = timeIncrement;
		this.status = status;
		this.startTime = startTime;
		this.currentTurn = currentTurn;
	}

	public byte[] getId(GameSide side){
		return side == GameSide.WHITE ? whiteId : blackId;
	}

	public GameSide getSide(byte[] id){
		return Arrays.equals(id, whiteId) ? GameSide.WHITE : (Arrays.equals(id, blackId) ? GameSide.BLACK : null);
	}

	public boolean isAuthorised(byte[] userId, GameSide side){
		return Arrays.equals(getId(side), userId);
	}

	public boolean isParticipant(byte[] userId){
		return Arrays.equals(blackId, userId) || Arrays.equals(whiteId, userId);
	}
}
