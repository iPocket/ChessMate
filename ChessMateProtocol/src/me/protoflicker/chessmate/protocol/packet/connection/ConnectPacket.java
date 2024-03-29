package me.protoflicker.chessmate.protocol.packet.connection;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class ConnectPacket extends Packet implements ClientPacket {

	public static final String PROTOCOL_VERSION = "0.6";

	@Getter
	private final String expectedVersion;

	public ConnectPacket(String expectedVersion){
		this.expectedVersion = expectedVersion;
	}

    public boolean isCompatible(){
		return expectedVersion.equals(PROTOCOL_VERSION);
	}
}
