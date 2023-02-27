package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.table.*;
import me.protoflicker.chessmate.protocol.chess.ChessBoard;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;
import me.protoflicker.chessmate.protocol.chess.enums.GameInfo;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.util.DatabaseUtils;

import java.sql.*;

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
		GameInfo info;
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
							LIMIT 1;
							""";
			try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)) {
				s.setBytes(1, gameId);
				ResultSet r = s.executeQuery();
				if(r.next()){
					if(GameSide.getByCode(r.getInt(2)) == GameSide.WHITE){
						white = r.getBytes(1);
					} else {
						black = r.getBytes(1);
					}
				} else {
					return null;
				}
			} catch(SQLException e){
				throw new RuntimeException(e);
			}
		}

		ChessBoard board = new ChessBoard(ChessUtils.stringToBoard(startingBoard), startTime, timeConstraint, timeIncrement);
		board.initMoves(MovesTable.getAllMoves(gameId));
		board.setGameStatus(status);
		return new GameInfo(gameId, gameName, white, black, board);
	}

	public static void createTables(Database database){
		UserTable.createTable(database);
		GameTable.createTable(database);

		TokenTable.createTable(database);
		ParticipationTable.createTable(database);
		MovesTable.createTable(database);


		Logger.getInstance().log(DatabaseUtils.bytesToHex(UserTable.getUserIdByUsername("protoflicker")));
//		MovesTable.addMove(HexFormat.of().parseHex("1BAF3EC9A6D611ED882DA46BB6E06E7E"),
//				new PerformedChessMove(new Timestamp(System.currentTimeMillis()),
//						new ChessMove(MoveType.MOVE, GameSide.WHITE, PieceType.PAWN,
//								new ChessPosition(2, 0),
//								new ChessPosition(4, 0))), 1);
	}
}
