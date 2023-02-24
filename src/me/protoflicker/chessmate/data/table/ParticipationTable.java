package me.protoflicker.chessmate.data.table;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ParticipationTable {

	public static byte[] getParticipationIdByUserId(byte[] userId){
		String statement =
				"""
				SELECT participationId
				FROM `Participations`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getBytes(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static byte[] getParticipationIdByGameId(byte[] gameId){
		String statement =
				"""
				SELECT participationId
				FROM `Participations`
				WHERE gameId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, gameId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getBytes(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static byte[] getUserId(byte[] participationId){
		String statement =
				"""
				SELECT userId
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, participationId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getBytes(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static byte[] getGameId(byte[] participationId){
		String statement =
				"""
				SELECT gameId
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, participationId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return r.getBytes(1);
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static GameSide getGameSide(byte[] participationId){
		String statement =
				"""
				SELECT gameSide
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, participationId);
			ResultSet r = s.executeQuery();
			if(r.next()){
				return GameSide.getByCode(r.getInt(1));
			} else {
				return null;
			}
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static List<byte[]> getGameIdsByUser(byte[] userId){
		List<byte[]> gameIds = new ArrayList<>();

		String statement =
				"""
				SELECT gameId
				FROM `Participations`
				WHERE userId = ?
				ORDER BY result DESC;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setBytes(1, userId);
			ResultSet r = s.executeQuery();
			while(r.next()){
				gameIds.add(r.getBytes(1));
			}
			r.close();

			return gameIds;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

//	public static List<byte[]> getOngoingGameIdsByUser(byte[] userId){
//		List<byte[]> gameIds = new ArrayList<>();
//
//		String statement =
//				"""
//				SELECT gameId
//				FROM `Participations`
//				WHERE userId = ? AND result = 2;
//				""";
//
//		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
//			s.setBytes(1, userId);
//			ResultSet r = s.executeQuery();
//			while(r.next()){
//				gameIds.add(r.getBytes(1));
//			}
//			r.close();
//
//			return gameIds;
//		} catch (SQLException e){
//			throw new RuntimeException(e);
//		}
//	}

	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Participations` (
				participationId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE,
				userId BINARY(16) NOT NULL,
				gameId BINARY(16) NOT NULL,
				gameSide TINYINT(1) UNSIGNED NOT NULL,
				PRIMARY KEY (participationId),
				FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE,
				FOREIGN KEY (gameId) REFERENCES Games(gameId) ON DELETE CASCADE
				);
				""";

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}
