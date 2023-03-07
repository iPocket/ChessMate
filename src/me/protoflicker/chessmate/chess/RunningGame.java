package me.protoflicker.chessmate.chess;

import lombok.Getter;
import me.protoflicker.chessmate.connection.ClientThread;
import me.protoflicker.chessmate.data.table.GameTable;
import me.protoflicker.chessmate.data.table.MovesTable;
import me.protoflicker.chessmate.manager.GameManager;
import me.protoflicker.chessmate.manager.LoginManager;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.chess.ChessPremove;
import me.protoflicker.chessmate.protocol.chess.PerformedChessMove;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.chess.enums.MoveType;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.update.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RunningGame {

	private final Object locker = new Object(); //synchronisation locker to prevent race condition

	@Getter
	private final GameInfo info;

	//imagine this as a concurrent HashSet<ClientThread>, Collections#newSetFromMap doesn't exist for some reason
	@Getter
	private final Map<ClientThread, Boolean> connected = new ConcurrentHashMap<>();

	@Getter
	private final Map<GameSide, ChessPremove> premoves = new ConcurrentHashMap<>();


	private final Map<GameSide, Integer> drawRequestAtMove = new ConcurrentHashMap<>();

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
		Timestamp time = new Timestamp(System.currentTimeMillis());

		synchronized(locker){
			if(isAuthorised(c, move.getGameSide()) && checkTimings()){
				if(move.getMoveType() != MoveType.DRAW_AGREEMENT){
					if(info.getBoard().isValid(move)){
						PerformedChessMove per = performMove(List.of(c), move, time);
						c.sendPacket(new GameMoveSuccessfulPacket(info.getGameId(), per));
						premoves.remove(move.getGameSide());
						tryPerformPremove(move.getGameSide().getOpposite(), time);
					} else {
						c.sendPacket(new GameMoveInvalidPacket(info.getGameId(), move));
					}
				} else {
					requestDraw(c, move.getGameSide());
				}
			}
		}
	}




	public void tryPremove(ClientThread c, ChessPremove premove){
		//doesn't need sync, only adds into concurrent hash table
		if(isAuthorised(c, premove.getGameSide())){
			if(premove.getPieceFrom() == null || premove.getPieceTo() == null){
				premoves.remove(premove.getGameSide());
			} else {
				if(premoves.get(premove.getGameSide()) != null){
					premoves.replace(premove.getGameSide(), premove);
				} else {
					premoves.put(premove.getGameSide(), premove);
				}
			}
		}
	}

	private void tryPerformPremove(GameSide side, Timestamp time){
		ChessPremove premove = premoves.get(side);
		if(premove != null){
			ChessMove move = info.getBoard().tryGetValidMove(premove.getPieceFrom(), premove.getPieceTo());
			if(move != null){
				move.setPromotionPiece(premove.getPromotionPiece());
				performMove(new ArrayList<>(), move, time);
			} else {
				premoves.remove(side);
			}
		}
	}

	public void requestDraw(ClientThread c, GameSide side){
		synchronized(locker) {
			if(isAuthorised(c, side) && checkTimings()){
				Integer otherLast = drawRequestAtMove.getOrDefault(side.getOpposite(), 0);
				if(info.getBoard().getNumberOfPerformedMoves() == otherLast){
					info.getBoard().performMove(new ChessMove(MoveType.DRAW_AGREEMENT, null, null, null, null),
							new Timestamp(System.currentTimeMillis()));
					updateGameStatus();
				} else {
					Integer a = drawRequestAtMove.get(side);
					if(a == null || a != info.getBoard().getNumberOfPerformedMoves()){
						drawRequestAtMove.put(side, info.getBoard().getNumberOfPerformedMoves());
						broadcastPacket(new GameDrawOfferPacket(info.getGameId(), side), List.of(c));
					}
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

	private PerformedChessMove performMove(List<ClientThread> exempt, ChessMove move, Timestamp time){
		info.getBoard().performMove(move, time);
		PerformedChessMove per = info.getBoard().getLastPerformedMove();

		broadcastPacket(new GameMoveUpdatePacket(info.getGameId(), info.getBoard().getLastPerformedMove()), exempt);

		if(move.getMoveType().isPieceMove()){
			addMoveToTable(info.getBoard().getLastPerformedMove());
		}

		updateGameStatus();
		return per;
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
		if(isAuthorised(c, GameSide.WHITE)){
			premoves.remove(GameSide.WHITE);
		} else if(isAuthorised(c, GameSide.BLACK)){
			premoves.remove(GameSide.BLACK);
		}

		connected.remove(c);
	}

	private boolean isAuthorised(ClientThread c, GameSide side){
		return info.isAuthorised(LoginManager.getUserId(c), side);
	}

	private boolean isParticipant(ClientThread c){
		return info.isParticipant(LoginManager.getUserId(c));
	}
}
