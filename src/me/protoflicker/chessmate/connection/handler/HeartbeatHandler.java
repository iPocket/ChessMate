package me.protoflicker.chessmate.connection.handler;

import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.connection.PingPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PongPacket;

public abstract class HeartbeatHandler {


	public static void handlePing(ClientThread client, Packet packet){
		client.sendPacket(new PongPacket());
	}

	public static void handlePong(ClientThread client, Packet packet){
		client.sendPacket(new PingPacket(System.currentTimeMillis()));
	}

	public static void handleDisconnect(ClientThread client, Packet packet){
		client.tryClose();
	}
}
