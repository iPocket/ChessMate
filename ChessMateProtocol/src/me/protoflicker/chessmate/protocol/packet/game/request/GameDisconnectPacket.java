package me.protoflicker.chessmate.protocol.packet.game.request;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GameDisconnectPacket extends GamePacket implements ClientPacket {

	@Getter
	private final byte[] gameId;

	public GameDisconnectPacket(byte[] gameId){
		this.gameId = gameId;
	}
}
