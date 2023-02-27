package me.protoflicker.chessmate.protocol.packet.game.request;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameTimingsRequestPacket extends GamePacket implements ClientPacket {

	@Getter
	private final byte[] gameId;

	public GameTimingsRequestPacket(byte[] gameId){
		this.gameId = gameId;
	}
}
