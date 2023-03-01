package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;

import java.io.Serializable;

public class SimpleGameInfo implements Serializable {

	@Getter
	private final byte[] gameId;

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

	public SimpleGameInfo(byte[] gameId, String gameName, byte[] whiteId, byte[] blackId, String startingBoard,
						  int timeConstraint, int timeIncrement){
		this.gameId = gameId;
		this.gameName = gameName;
		this.whiteId = whiteId;
		this.blackId = blackId;
		this.startingBoard = startingBoard;
		this.timeConstraint = timeConstraint;
		this.timeIncrement = timeIncrement;
	}

	public byte[] getId(GameSide side){
		return side == GameSide.WHITE ? whiteId : blackId;
	}

	public boolean isAuthorised(byte[] userId, GameSide side){
		return getId(side) == userId;
	}

	public boolean isParticipant(byte[] userId){
		return blackId == userId || whiteId == userId;
	}
}
