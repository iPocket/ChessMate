package me.protoflicker.chessmate.data;

import lombok.Getter;
import me.protoflicker.chessmate.console.Logger;
import me.protoflicker.chessmate.data.table.GameTable;
import me.protoflicker.chessmate.data.table.MovesTable;
import me.protoflicker.chessmate.data.table.ParticipationTable;
import me.protoflicker.chessmate.data.table.UserTable;
import me.protoflicker.chessmate.protocol.chess.*;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.MoveType;
import me.protoflicker.chessmate.protocol.enums.PieceType;
import me.protoflicker.chessmate.util.DatabaseUtils;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HexFormat;

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

		ParticipationTable.createTable(database);
		MovesTable.createTable(database);
		Logger.getInstance().log(DatabaseUtils.bytesToHex(UserTable.getUserIdByUsername("protoflicker")));
		ChessBoard startingBoard = ChessUtils.getStartingBoard();
		Logger.getInstance().log("Test: " + ChessPosition.fromChessNotation("e4"));
		LocatableChessPiece piece = startingBoard.getPieceAtLocation(new ChessPosition(0, 6));
		Logger.getInstance().log(piece.getType() + "");
		for(ChessMove move : startingBoard.getMoves(piece)){
			Logger.getInstance().log("Can move to: " + move.getPieceTo() + "");
		}

		Logger.getInstance().log("That's it.");
		MovesTable.addMove(HexFormat.of().parseHex("1BAF3EC9A6D611ED882DA46BB6E06E7E"),
				new PerformedChessMove(1, new Timestamp(System.currentTimeMillis()),
						new ChessMove(MoveType.MOVE, GameSide.WHITE, PieceType.PAWN,
								new ChessPosition(2, 0),
								new ChessPosition(4, 0))));
	}
}
