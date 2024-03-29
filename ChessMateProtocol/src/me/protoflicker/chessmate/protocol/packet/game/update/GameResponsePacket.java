package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameResponsePacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final GameInfo info;

	@Getter
	private final boolean spectating;

	public GameResponsePacket(byte[] gameId, GameInfo info, boolean spectating){
		this.gameId = gameId;
		this.info = info;
		this.spectating = spectating;
	}
}
