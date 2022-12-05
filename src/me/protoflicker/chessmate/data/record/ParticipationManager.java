package me.protoflicker.chessmate.data.record;

import me.protoflicker.chessmate.Server;
import me.protoflicker.chessmate.data.Database;
import me.protoflicker.chessmate.data.record.enums.GameSide;
import me.protoflicker.chessmate.data.record.enums.Result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ParticipationManager {

	public static String getParticipationIdByUserId(String participationId){
		String statement =
				"""
				SELECT participationId
				FROM `Users`
				WHERE userId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, participationId);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getUserId(String participationId){
		String statement =
				"""
				SELECT userId
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, participationId);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static String getGameId(String participationId){
		String statement =
				"""
				SELECT gameId
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, participationId);
			ResultSet r = s.executeQuery();
			return r.getString(0);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static GameSide getGameSide(String participationId){
		String statement =
				"""
				SELECT gameSide
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, participationId);
			ResultSet r = s.executeQuery();
			return r.getByte(0) == 1 ? GameSide.WHITE : GameSide.BLACK;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	public static Result getResult(String participationId){
		String statement =
				"""
				SELECT result
				FROM `Participations`
				WHERE participationId = ?
				LIMIT 1;
				""";

		try (PreparedStatement s = Server.getThreadDatabase().getConnection().prepareStatement(statement)){
			s.setString(0, participationId);
			ResultSet r = s.executeQuery();
			return r.getByte(0) == 1 ? Result.WIN : Result.LOSS;
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	//String participationId, String participationId, String gameId, GameSide gameSide, Result result
	public static void createTable(Database database){
		String statement =
				"""
				CREATE TABLE IF NOT EXISTS `Participations` (
				participationId BINARY(16) DEFAULT (UNHEX(REPLACE(UUID(), "-",""))) NOT NULL UNIQUE PRIMARY KEY,
				userId BINARY(16) NOT NULL,
				gameId BINARY(16) NOT NULL,
				gameSide BIT(1) NOT NULL,
				result BIT(1) NOT NULL
				);
				""";

		try (PreparedStatement s = database.getConnection().prepareStatement(statement)){
			s.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}

