package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameMoveInvalidPacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final ChessMove move;

	public GameMoveInvalidPacket(byte[] gameId, ChessMove move){
		this.gameId = gameId;
		this.move = move;
	}
}
