package me.protoflicker.chessmate.chess;

import lombok.Getter;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.data.table.GameTable;
import me.protoflicker.chessmate.data.table.MovesTable;
import me.protoflicker.chessmate.manager.GameManager;
import me.protoflicker.chessmate.manager.LoginManager;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.chess.PerformedChessMove;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.chess.enums.MoveType;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.update.*;

import java.sql.Timestamp;
import java.util.*;

public class RunningGame {

	private final Object locker = new Object(); //synchronisation locker to prevent race condition

	@Getter
	private final GameInfo info;

	//imagine this as a weak-link HashSet<ClientThread>, Collections#newSetFromMap doesn't exist for some reason
	@Getter
	private final Map<ClientThread, Boolean> connected = Collections.synchronizedMap(new WeakHashMap<>());


	private final Map<GameSide, Integer> drawRequestAtMove = Collections.synchronizedMap(new HashMap<>());

	public RunningGame(GameInfo info){
		this.info = info;
	}

	public boolean checkTimings(){
		if(info.getBoard().getGameStatus() == GameStatus.ONGOING){
			info.getBoard().updateTimingStatus();
			updateGameStatus();
			return info.getBoard().getGameStatus() == GameStatus.ONGOING;
		} else {
			return false;
		}
	}

	public void tryMove(ClientThread c, ChessMove move){
		synchronized(locker){
			if(isAuthorised(c, move.getGameSide()) && checkTimings()){
				if(move.getMoveType() != MoveType.DRAW_AGREEMENT){
					if(info.getBoard().isValid(move)){
						info.getBoard().performMove(move, new Timestamp(System.currentTimeMillis()));

						broadcastPacket(new GameMoveUpdatePacket(info.getGameId(), info.getBoard().getLastPerformedMove()), List.of(c));

						addMoveToTable(info.getBoard().getLastPerformedMove());

						updateGameStatus();
					} else {
						c.sendPacket(new GameMoveInvalidPacket(info.getGameId(), move));
					}
				} else {
					requestDraw(c, move.getGameSide());
				}
			} else {
				c.sendPacket(new GameMoveInvalidPacket(info.getGameId(), move));
			}
		}
	}

	public void requestDraw(ClientThread c, GameSide side){
		//already in sync
		synchronized(locker) {
			if(isAuthorised(c, side) && checkTimings()){
				Integer otherLast = drawRequestAtMove.getOrDefault(side.getOpposite(), 0);
				if(info.getBoard().getNumberOfPerformedMoves() == otherLast){
					info.getBoard().performMove(new ChessMove(MoveType.DRAW_AGREEMENT, null, null, null, null),
							new Timestamp(System.currentTimeMillis()));
					updateGameStatus();
				} else {
					drawRequestAtMove.put(side, info.getBoard().getNumberOfPerformedMoves());
					broadcastPacket(new GameDrawOfferPacket(info.getGameId(), side), List.of(c));
				}
			}
		}
	}

	public void handleDrawDecline(ClientThread c, GameSide side){
		synchronized(locker){
			if(isAuthorised(c, side)){
				Integer otherLast = drawRequestAtMove.getOrDefault(side.getOpposite(), 0);
				if(info.getBoard().getNumberOfPerformedMoves() == otherLast){
					broadcastPacket(new GameDrawDeclinedPacket(info.getGameId(), side), List.of(c));
				}
			}
		}
	}

	public void tryTimingCheck(){
		synchronized(locker){
			checkTimings();
		}
	}

	private void updateGameStatus(){
		if(info.getBoard().getGameStatus() != GameStatus.ONGOING){
			broadcastPacket(new GameStatusUpdatePacket(info.getGameId(), info.getBoard().getGameStatus()));
			GameTable.setGameStatus(info.getGameId(), info.getBoard().getGameStatus());
			GameManager.unloadGameAndKick(this);
		}
	}

	private void addMoveToTable(PerformedChessMove move){
		MovesTable.addMove(info.getGameId(), move, info.getBoard().getPerformedMoves().indexOf(move)+1);
	}

	public void broadcastPacket(ServerPacket packet){
		broadcastPacket(packet, new ArrayList<>());
	}

	public void broadcastPacket(ServerPacket packet, List<ClientThread> exempt){
		for(ClientThread c : connected.keySet()){
			if(!exempt.contains(c)){
				c.sendPacket(packet);
			}
		}
	}

	public void addClient(ClientThread c){
		connected.put(c, isParticipant(c));
	}

	public void removeClient(ClientThread c){
		connected.remove(c);
	}

	private boolean isAuthorised(ClientThread c, GameSide side){
		return info.isAuthorised(LoginManager.getUserId(c), side);
	}

	private boolean isParticipant(ClientThread c){
		return info.isParticipant(LoginManager.getUserId(c));
	}
}
