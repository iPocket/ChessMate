package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.ChessMove;
import me.protoflicker.chessmate.protocol.chess.ChessPosition;
import me.protoflicker.chessmate.protocol.chess.PerformedChessMove;
import me.protoflicker.chessmate.protocol.enums.GameSide;
import me.protoflicker.chessmate.protocol.enums.MoveType;
import me.protoflicker.chessmate.protocol.enums.PieceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class MovesTable {


	public static PerformedChessMove getMove(byte[] gameId, int moveNumber){
		String statement =
				"""
				SELECT timePlayed,moveType,pieceFrom,pieceTo,pieceMoved,promotionPiece
				FROM `Moves`
				WHERE gameId = ? AND moveNumber = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			s.setInt(2, moveNumber);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return toPerformedChessMove(
						moveNumber,
						r.getTimestamp("timePlayed"),
						r.getString("pieceMoved"),
						r.getInt("moveType"),
						r.getString("pieceFrom"),
						r.getString("pieceTo"),
						r.getString("promotionPiece")
						);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static List<PerformedChessMove> getAllMoves(byte[] gameId){
		List<PerformedChessMove> moves = new ArrayList<>();

		String statement =
				"""
				SELECT moveNumber,timePlayed,moveType,pieceFrom,pieceTo,pieceMoved,promotionPiece
				FROM `Moves`
				WHERE gameId = ?
				ORDER BY moveNumber ASC;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			while(r.next()){
				moves.add(toPerformedChessMove(
						r.getInt("moveNumber"),
						r.getTimestamp("timePlayed"),
						r.getString("pieceMoved"),
						r.getInt("moveType"),
						r.getString("pieceFrom"),
						r.getString("pieceTo"),
						r.getString("promotionPiece"))
				);
			}
			r.close();

			return moves;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void addMove(byte[] gameId, PerformedChessMove move, int moveNumber){
		String statement =
				"""
				INSERT INTO `Moves` (gameId,moveNumber,timePlayed,moveType,pieceFrom,pieceTo,pieceMoved,promotionPiece)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			s.setInt(2, moveNumber);
			s.setTimestamp(3, move.getTimePlayed());
			s.setInt(4, move.getMove().getMoveType().getCode());
			s.setString(5, move.getMove().getPieceFrom().getChessNotation());
			s.setString(6, move.getMove().getPieceTo().getChessNotation());
			s.setString(7, move.getMove().getPieceMoved().getCode());
			if(move.getMove().getPromotionPiece() != null){
				s.setString(8, move.getMove().getPromotionPiece().getCode());
			} else {
				s.setNull(8, Types.CHAR);
			}
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	private static PerformedChessMove toPerformedChessMove(int moveNumber, Timestamp timePlayed, String pieceMoved, int moveType, String pieceFrom,
														   String pieceTo, String promotionPiece){
		return new PerformedChessMove(
				timePlayed,
				new ChessMove(
					MoveType.getByCode(moveType),
					moveNumber % 2 == 0 ? GameSide.WHITE : GameSide.BLACK,
					PieceType.getByChessCode(pieceMoved),
					ChessPosition.fromChessNotation(pieceFrom),
					ChessPosition.fromChessNotation(pieceTo),
					PieceType.getByChessCode(promotionPiece)
				)
			);
	}

	//todo fix incrementer
	//moveNumber SMALLINT(16) UNSIGNED AUTO_INCREMENT NOT NULL,
	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Moves` (
				gameId BINARY(16) NOT NULL,
				moveNumber SMALLINT(16) UNSIGNED NOT NULL,
				timePlayed TIMESTAMP DEFAULT (NOW()) NOT NULL,
				moveType TINYINT(1) UNSIGNED NOT NULL,
				pieceFrom CHAR(2),
				pieceTo CHAR(2),
				pieceMoved CHAR(1),
				promotionPiece CHAR(1),
				PRIMARY KEY (gameId, moveNumber),
				FOREIGN KEY (gameId) REFERENCES Games(gameId)
				);
				""";

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
