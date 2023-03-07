package me.protoflicker.chessmate.protocol.packet.game.request;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.ChessPremove;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.GamePacket;

public class GamePremoveSubmitPacket extends GamePacket implements ClientPacket {

	@Getter
	private final byte[] gameId;

	@Getter
	private final ChessPremove premove;

	public GamePremoveSubmitPacket(byte[] gameId, ChessPremove premove){
		this.gameId = gameId;
		this.premove = premove;
	}
}
