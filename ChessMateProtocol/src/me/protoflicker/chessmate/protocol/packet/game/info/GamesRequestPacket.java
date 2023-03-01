package me.protoflicker.chessmate.protocol.packet.game.info;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class GamesRequestPacket extends Packet implements ClientPacket {

	@Getter
	private final byte[] userId;

	public GamesRequestPacket(byte[] userId){
		this.userId = userId;
	}
}
