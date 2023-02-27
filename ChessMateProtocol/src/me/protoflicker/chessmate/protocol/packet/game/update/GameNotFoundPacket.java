package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameNotFoundPacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	public GameNotFoundPacket(byte[] gameId){
		this.gameId = gameId;
	}
}
