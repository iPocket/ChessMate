package me.protoflicker.chessmate.protocol.packet.game.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

import java.util.List;

public class GamesResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] userId;

	@Getter
	private final List<SimpleGameInfo> games;

	public GamesResponsePacket(byte[] userId, List<SimpleGameInfo> games){
		this.userId = userId;
		this.games = games;
	}
}
