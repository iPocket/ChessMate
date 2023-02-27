package me.protoflicker.chessmate.manager;

import lombok.Getter;
import me.protoflicker.chessmate.chess.RunningGame;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameMoveRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.update.GameNotFoundPacket;
import me.protoflicker.chessmate.protocol.packet.game.update.GameResponsePacket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class GameManager {

	@Getter
	private static final Map<byte[], RunningGame> runningGames = Collections.synchronizedMap(new WeakHashMap<>());


	public static void handleGameRequest(ClientThread c, ClientPacket packet){
		GameRequestPacket p = (GameRequestPacket) packet;

		RunningGame game = runningGames.get(p.getGameId());
		if(game == null){
//			game = tryLoadGame(p.getGameId());
		}

		if(game != null){
			c.sendPacket(new GameResponsePacket(p.getGameId(), game.getInfo()));
		} else {
			c.sendPacket(new GameNotFoundPacket(p.getGameId()));
		}
	}

	public static void handleMoveRequest(ClientThread c, ClientPacket packet){
		GameMoveRequestPacket p = (GameMoveRequestPacket) packet;

		RunningGame game = runningGames.get(p.getGameId());
		if(game != null){
			game.tryMove(c, p.getMove());
		}
	}


	private static final Map<Class<?>, PacketHandler> packetHandlers = new HashMap<>();

	static {
		initHandlers();
	}

	public static void registerHandlers(ClientThread clientThread){
		clientThread.getPacketHandlers().putAll(packetHandlers);
	}

	public static void unregisterHandlers(ClientThread clientThread){
		Map<Class<?>, PacketHandler> h = clientThread.getPacketHandlers();
		for(Class<?> aClass : packetHandlers.keySet()){
			h.remove(aClass);
		}
	}

	private static void initHandlers(){

	}
}
