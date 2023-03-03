package me.protoflicker.chessmate.protocol.chess.enums;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.ChessBoard;

import java.io.Serializable;
import java.util.Arrays;

public class GameInfo implements Serializable {

	@Getter
	private final byte[] gameId;

	@Getter
	private final String gameName;

	@Getter
	private final byte[] whiteId;

	@Getter
	private final byte[] blackId;

	@Getter
	private final ChessBoard board;

	public GameInfo(byte[] gameId, String gameName, byte[] whiteId, byte[] blackId, ChessBoard board){
		this.gameId = gameId;
		this.gameName = gameName;
		this.whiteId = whiteId;
		this.blackId = blackId;
		this.board = board;
	}

	public byte[] getId(GameSide side){
		return side == GameSide.WHITE ? whiteId : blackId;
	}

	public boolean isAuthorised(byte[] userId, GameSide side){
		return Arrays.equals(getId(side), userId);
	}

	public boolean isParticipant(byte[] userId){
		return Arrays.equals(blackId, userId) || Arrays.equals(whiteId, userId);
	}
}
