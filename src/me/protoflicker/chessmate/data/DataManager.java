package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.table.*;
import me.protoflicker.chessmate.protocol.chess.ChessBoard;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;
import me.protoflicker.chessmate.protocol.packet.game.invitation.GameInvitation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

	@Getter
	private final String ip;

	@Getter
	private final String port;

	@Getter
	private final String databaseName;

	private final String username;

	private final String password;

	private Connection connection;

	public DataManager(String ip, String port, String databaseName, String username, String password){
		this.ip = ip;
		this.port = port;
		this.databaseName = databaseName;
		this.username = username;
		this.password = password;
	}

	public Database getNewDatabase(){
		return new SQLDatabase(ip, port, databaseName, username, password);
	}

	public static GameInfo getFullGame(byte[] gameId){
		SimpleGameInfo info = getSimpleGame(gameId);
		if(info != null){
			ChessBoard board = new ChessBoard(ChessUtils.stringToBoard(info.getStartingBoard()), info.getStartTime(),
					info.getTimeConstraint(),
					info.getTimeIncrement());
			board.initMoves(MovesTable.getAllMoves(gameId));
			board.setGameStatus(info.getStatus());
			return new GameInfo(gameId, info.getGameName(), info.getWhiteId(), info.getBlackId(), board);
		} else {
			return null;
		}
	}

	public static SimpleGameInfo getSimpleGame(byte[] gameId){
		String gameName;
		Timestamp startTime;
		String startingBoard;
		int timeConstraint;
		int timeIncrement;
		GameStatus status;

		{
			String statement =
					"""
							SELECT gameName,startTime,startingBoard,timeConstraint,timeIncrement,status
							FROM `Games`
							WHERE gameId = ?
							LIMIT 1;
							""";
			try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)) {
				s.setBytes(1, gameId);
				ResultSet r = s.executeQuery();
				if(r.next()){
					gameName = r.getString(1);
					startTime = r.getTimestamp(2);
					startingBoard = r.getString(3);
					timeConstraint = r.getInt(4);
					timeIncrement = r.getInt(5);
					status = GameStatus.getByCode(r.getInt(6));
				} else {
					return null;
				}
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		byte[] white = null;
		byte[] black = null;
		{
			String statement =
					"""
							SELECT userId,gameSide
							FROM `Participations`
							WHERE gameId = ?
							LIMIT 2;
							""";
			try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)) {
				s.setBytes(1, gameId);
				ResultSet r = s.executeQuery();
				while(r.next()){
					if(GameSide.getByCode(r.getInt(2)) == GameSide.WHITE){
						white = r.getBytes(1);
					} else {
						black = r.getBytes(1);
					}
				}
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		if(white == null || black == null){
			return null;
		}

		return new SimpleGameInfo(gameId, gameName, white, black, startingBoard, timeConstraint, timeIncrement, status, startTime,
				MovesTable.getNumberOfMoves(gameId) % 2 == 0 ? GameSide.WHITE : GameSide.BLACK);
	}

	public static List<SimpleGameInfo> getGamesByUser(byte[] userId){
		List<byte[]> ids = new ArrayList<>();
		List<SimpleGameInfo> games = new ArrayList<>();
		//query game ids by user, getSimpleGame() for each and return

		{
			String statement =
					"""
							SELECT gameId
							FROM `Participations`
							WHERE userId = ?;
							""";
			try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)) {
				s.setBytes(1, userId);
				ResultSet r = s.executeQuery();
				while(r.next()){
					ids.add(r.getBytes(1));
				}
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		for(byte[] id : ids){
			games.add(getSimpleGame(id));
		}

		return games;
	}

	public static byte[] initGameAndGetId(GameInvitation inv){
		byte[] gameId = GameTable.createGameAndGetId(inv.getInfo());
		ParticipationTable.createParticipation(gameId, inv.getInfo().getWhiteId(), inv.getInfo().getBlackId());
		return gameId;
	}

	public static void createTables(Database database){
		UserTable.createTable(database);
		GameTable.createTable(database);

		TokenTable.createTable(database);
		ParticipationTable.createTable(database);
		MovesTable.createTable(database);
		InvitationsTable.createTable(database);
	}
}
