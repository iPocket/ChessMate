package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.ChessUtils;
import me.protoflicker.chessmate.protocol.chess.enums.GameStatus;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;

import java.sql.*;

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

	public static GameStatus getGameStatus(byte[] gameId){
		String statement =
				"""
				SELECT status
				FROM `Games`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return GameStatus.getByCode(r.getInt(1));
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static void setGameStatus(byte[] gameId, GameStatus status){
		String statement =
				"""
				UPDATE `Games`
				SET status = ?
				WHERE gameId = ?;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setInt(1, status.getCode());
			s.setBytes(2, gameId);
			s.executeUpdate();

		} catch (SQLException e){
			return; //no exception needed
		}
	}

	public static byte[] createGameAndGetId(SimpleGameInfo info){
		String statement =
				"""
				INSERT INTO `Games` (gameName, startingBoard, timeConstraint, timeIncrement)
				VALUES (?, ?, ?, ?);
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
			s.setString(1, info.getGameName());
			s.setString(2, info.getStartingBoard());
			s.setInt(3, info.getTimeConstraint());
			s.setInt(4, info.getTimeIncrement());
			s.executeUpdate();

			return s.getGeneratedKeys().getBytes("gameId");
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}


	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Games` (
				gameId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE,
				gameName VARCHAR(64) DEFAULT "Unnamed Game" NOT NULL,
				startTime TIMESTAMP DEFAULT (NOW()) NOT NULL,
				startingBoard CHAR(191) DEFAULT ("%MedicBag%") NOT NULL,
				timeConstraint INT(16) UNSIGNED NOT NULL,
				timeIncrement INT(16) UNSIGNED NOT NULL,
				status TINYINT(1) UNSIGNED DEFAULT 0 NOT NULL,
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
