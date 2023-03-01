package me.protoflicker.chessmate.manager;

import lombok.Getter;
import me.protoflicker.chessmate.chess.RunningGame;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.connection.PacketHandler;
import me.protoflicker.chessmate.data.DataManager;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.packet.ClientPacket;
import me.protoflicker.chessmate.protocol.packet.game.info.GamesRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.info.response.GamesResponsePacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameDrawDeclinePacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameMoveRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.request.GameTimingsRequestPacket;
import me.protoflicker.chessmate.protocol.packet.game.update.GameNoLongerRunningPacket;
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

		GameInfo info;
		RunningGame game = runningGames.get(p.getGameId());
		if(game != null){
			info = game.getInfo();
		} else {
			info = getGameInfo(p.getGameId());
			if(info != null && info.getBoard().getGameStatus() == GameStatus.ONGOING){
				game = createRunningGame(info);
			}
		}

		if(game != null){
			game.addClient(c);
		}

		if(info != null){
			c.sendPacket(new GameResponsePacket(p.getGameId(), info, game != null));
		} else {
			c.sendPacket(new GameNotFoundPacket(p.getGameId()));
		}

		if(game != null){
			game.checkTimings();
		}
	}

	public static void handleMoveRequest(ClientThread c, ClientPacket packet){
		GameMoveRequestPacket p = (GameMoveRequestPacket) packet;

		RunningGame game = runningGames.get(p.getGameId());
		if(game != null){
			game.tryMove(c, p.getMove());
		}
	}

	public static void handleDrawDecline(ClientThread c, ClientPacket packet){
		GameDrawDeclinePacket p = (GameDrawDeclinePacket) packet;

		RunningGame game = runningGames.get(p.getGameId());
		if(game != null){
			game.handleDrawDecline(c, p.getSide());
		}
	}

	public static void handleTimingsCheck(ClientThread c, ClientPacket packet){
		GameTimingsRequestPacket p = (GameTimingsRequestPacket) packet;

		RunningGame game = runningGames.get(p.getGameId());
		if(game != null){
			game.checkTimings();
		}
	}


	public static void handleGamesRequest(ClientThread c, ClientPacket packet){
		GamesRequestPacket p = (GamesRequestPacket) packet;

		c.sendPacket(new GamesResponsePacket(p.getUserId(),
				DataManager.getGamesByUser(p.getUserId())));
	}




	public static void onDisconnect(ClientThread c){
		for(RunningGame game : runningGames.values()){
			removeClientFromGame(c, game);
		}
	}




	private static void removeClientFromGame(ClientThread c, RunningGame game){
		game.removeClient(c);
		if(game.getConnected().isEmpty()){
			unloadGame(game);
		}
	}

	private static void unloadGame(RunningGame game){
		//everything should be already saved.
		runningGames.remove(game.getInfo().getGameId());
	}

	public static void unloadGameAndKick(RunningGame game){
		for(ClientThread c : game.getConnected().keySet()){
			c.sendPacket(new GameNoLongerRunningPacket(game.getInfo().getGameId()));
		}
		unloadGame(game);
	}


	private static GameInfo getGameInfo(byte[] gameId){
		return DataManager.getFullGame(gameId);
	}

	private static RunningGame createRunningGame(GameInfo info){
		if(info != null){
			return runningGames.put(info.getGameId(), new RunningGame(info));
		} else {
			return null;
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
		packetHandlers.put(GameRequestPacket.class, GameManager::handleGameRequest);
		packetHandlers.put(GameMoveRequestPacket.class, GameManager::handleMoveRequest);
		packetHandlers.put(GameDrawDeclinePacket.class, GameManager::handleDrawDecline);
		packetHandlers.put(GameTimingsRequestPacket.class, GameManager::handleTimingsCheck);
	}
}
