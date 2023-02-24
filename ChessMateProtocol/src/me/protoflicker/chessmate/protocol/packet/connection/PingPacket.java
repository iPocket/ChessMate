package me.protoflicker.chessmate.protocol.packet.connection;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class PingPacket extends Packet implements ClientPacket, ServerPacket {

	@Getter
	private final long time;

	public PingPacket(long time){
		this.time = time;
	}
}
