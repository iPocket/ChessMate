package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.table.*;
import me.protoflicker.chessmate.util.DatabaseUtils;

import java.sql.Connection;

public class DataManager {

	@Getter
	private final String ip;

	@Getter
	private final String port;

	private final String database;

	private final String username;

	private final String password;

	private Connection connection;

	public DataManager(String ip, String port, String database, String username, String password){
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public Database getNewDatabase(){
		return new SQLDatabase(ip, port, database, username, password);
	}

	public static void createTables(Database database){
		UserTable.createTable(database);
		GameTable.createTable(database);

		AuthKeyTable.createTable(database);
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
