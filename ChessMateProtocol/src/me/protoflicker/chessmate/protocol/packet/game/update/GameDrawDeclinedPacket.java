package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameDrawDeclinedPacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final GameSide side; //decliner

	public GameDrawDeclinedPacket(byte[] gameId, GameSide side){
		this.gameId = gameId;
		this.side = side;
	}
}
