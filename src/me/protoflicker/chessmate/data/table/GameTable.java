package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class GameTable {


	public static String getGameName(byte[] gameId){
		String statement =
				"""
				SELECT gameName
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Timestamp getStartTime(byte[] gameId){
		String statement =
				"""
				SELECT startTime
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getTimestamp(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Timestamp getEndTime(byte[] gameId){
		String statement =
				"""
				SELECT endTime
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getTimestamp(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getStartingBoardString(byte[] gameId){
		String statement =
				"""
				SELECT startingBoard
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static int getTimeConstraint(byte[] gameId){
		String statement =
				"""
				SELECT timeConstraint
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static int getTimeIncrement(byte[] gameId){
		String statement =
				"""
				SELECT timeConstraint
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Games` (
				gameId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE,
				gameName VARCHAR(32) DEFAULT "Unnamed Game" NOT NULL,
				startTime TIMESTAMP DEFAULT (NOW()) NOT NULL,
				endTime TIMESTAMP DEFAULT (0) NOT NULL,
				startingBoard VARCHAR(191) DEFAULT ("%MedicBag%") NOT NULL,
				timeConstraint INT(16) UNSIGNED NOT NULL,
				timeIncrement INT(16) UNSIGNED NOT NULL,
				PRIMARY KEY (gameId)
				);
				""".replace("%MedicBag%", ChessUtils.getStartingBoardText());

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}