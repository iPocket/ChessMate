package me.protoflicker.chessmate.protocol.packet.connection;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public class ConnectPacket extends Packet implements ClientPacket {

	public static final String PROTOCOL_VERSION = "0.3";

	@Getter
	private final String expectedVersion;

	public ConnectPacket(String expectedVersion){
		this.expectedVersion = expectedVersion;
	}

	public static String getUsernameRegex() {
		return "^[a-zA-Z0-9._-]{3,}$";
	}

    public boolean isCompatible(){
		return expectedVersion.equals(PROTOCOL_VERSION);
	}
}
