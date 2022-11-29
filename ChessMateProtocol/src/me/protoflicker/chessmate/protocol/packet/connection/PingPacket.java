package me.protoflicker.chessmate.protocol.packet.connection;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;

public class PingPacket extends Packet {

	@Getter
	private final long time;

	public PingPacket(long time){
		this.time = time;
	}
}
