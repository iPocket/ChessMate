package me.protoflicker.chessmate.connection;

import me.protoflicker.chessmate.protocol.Packet;

public interface PacketHandler {
	public void handle(ClientThread thread, Packet packet);
}
