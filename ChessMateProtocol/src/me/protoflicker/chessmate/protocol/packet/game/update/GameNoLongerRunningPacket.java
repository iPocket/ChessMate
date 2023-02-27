package me.protoflicker.chessmate.protocol.packet.game.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameNoLongerRunningPacket extends GamePacket implements ServerPacket {

	@Getter
	private final byte[] gameId;

	public GameNoLongerRunningPacket(byte[] gameId){
		this.gameId = gameId;
	}
}
