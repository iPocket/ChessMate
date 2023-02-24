package me.protoflicker.chessmate.protocol.packet.game.move;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameMoveRequestPacket extends GamePacket implements ClientPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final ChessMove move;

	public GameMoveRequestPacket(byte[] gameId, ChessMove move){
		this.gameId = gameId;
		this.move = move;
	}
}
