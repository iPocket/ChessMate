package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.PerformedChessMove;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameMoveUpdatePacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final PerformedChessMove move;

	public GameMoveUpdatePacket(byte[] gameId, PerformedChessMove move){
		this.gameId = gameId;
		this.move = move;
	}
}
