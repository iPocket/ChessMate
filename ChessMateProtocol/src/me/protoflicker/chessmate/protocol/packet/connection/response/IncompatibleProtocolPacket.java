package me.protoflicker.chessmate.protocol.packet.connection.response;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class IncompatibleProtocolPacket extends Packet implements ServerPacket {

	@Getter
	private final String expectedVersion;

	public IncompatibleProtocolPacket(String expectedVersion){
		this.expectedVersion = expectedVersion;
	}
}
