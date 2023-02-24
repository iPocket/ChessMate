package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameStatusUpdatePacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final GameStatus newStatus;

	public GameStatusUpdatePacket(byte[] gameId, GameStatus newStatus){
		this.gameId = gameId;
		this.newStatus = newStatus;
	}
}
