package me.protoflicker.chessmate.protocol.packet.game.info.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

import java.util.List;

public class GamesOnlineResponsePacket extends Packet implements ServerPacket {

	@Getter
	private final List<SimpleGameInfo> games;

	public GamesOnlineResponsePacket(List<SimpleGameInfo> games){
		this.games = games;
	}
}
