package me.protoflicker.chessmate.connection.handler;

import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PingPacket;
import me.protoflicker.chessmate.protocol.packet.connection.PongPacket;

public abstract class HeartbeatHandler {


	public static void handlePing(ClientThread client, ClientPacket packet){
		client.sendPacket(new PongPacket());
	}

	public static void handlePong(ClientThread client, ClientPacket packet){
		client.sendPacket(new PingPacket(System.currentTimeMillis()));
	}
}
