package me.protoflicker.chessmate.connection;

import me.protoflicker.chessmate.protocol.packet.ClientPacket;

public interface PacketHandler {
	public void handle(ClientThread client, ClientPacket packet);
}
