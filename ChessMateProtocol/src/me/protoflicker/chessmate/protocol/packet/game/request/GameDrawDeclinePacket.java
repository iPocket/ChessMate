package me.protoflicker.chessmate.protocol.packet.game.request;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameDrawDeclinePacket extends GamePacket implements ClientPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final GameSide side; //decliner

	public GameDrawDeclinePacket(byte[] gameId, GameSide side){
		this.gameId = gameId;
		this.side = side;
	}
}
